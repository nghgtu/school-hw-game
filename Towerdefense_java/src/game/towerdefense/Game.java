package game.towerdefense;

import game.towerdefense.grid.Grid;
import game.towerdefense.sprites.Enemy;
import game.towerdefense.sprites.SpriteBase;
import game.towerdefense.sprites.Tower;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Game extends Application {

    Random rnd = new Random();

    Pane backgroundLayer;
    Pane playfieldLayer;
    Pane scoreLayer;
    Grid towerGridLayer;

    Image backgroundImage;
    Image playerImage;
    Image enemyImage;

    List<Tower> towers = new ArrayList<>();;
    List<Enemy> enemies = new ArrayList<>();;

    Text scoreText = new Text();
    int score = 0;

    Scene scene;

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        // create layers
        backgroundLayer = new Pane();

        // ensure the playfield size so that we can click on it
        playfieldLayer = new Pane();
        playfieldLayer.setPrefSize( Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

        // score layer
        scoreLayer = new Pane();
        scoreLayer.setPrefWidth(Settings.SCENE_WIDTH); // note: it already spans from x=0 to center because we move the node in the layer to the center, not the layer to the center
        // don't react on mouse events
        scoreLayer.setMouseTransparent(true);

        // tower layer
        towerGridLayer = new Grid( Settings.TOWER_GRID_COLUMNS, Settings.TOWER_GRID_ROWS, Settings.PLAYFIELD_WIDTH, Settings.PLAYFIELD_HEIGHT);

        // add event handler to create towers
        towerGridLayer.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {

            // calculate grid position of new tower
            Bounds bounds = towerGridLayer.getBoundsAt(e.getX(), e.getY());

            // calculate grid cell center
            double centerX = bounds.getMinX() + bounds.getWidth() / 2;
            double centerY = bounds.getMinY() + bounds.getHeight() / 2;

            // create & place new tower
            createTower( centerX, centerY);

        });


        root.getChildren().add( backgroundLayer);
        root.getChildren().add( playfieldLayer);
        root.getChildren().add( towerGridLayer);
        root.getChildren().add( scoreLayer);

        scene = new Scene( root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());

        primaryStage.setScene( scene);

        // fullscreen
        primaryStage.setFullScreen( Settings.STAGE_FULLSCREEN);
        primaryStage.setFullScreenExitHint("");

        // scale by factor of 2 (in settings we have half-hd) to get proper dimensions in fullscreen (full-hd)
        if( primaryStage.isFullScreen()) {
            Scale scale = new Scale( Settings.STAGE_FULLSCREEN_SCALE, Settings.STAGE_FULLSCREEN_SCALE);
            scale.setPivotX(0);
            scale.setPivotY(0);
            scene.getRoot().getTransforms().setAll(scale);
        }

        primaryStage.show();
        loadGame();

        createBackgroundLayer();
        createPlayfieldLayer();
        createScoreLayer();
        createTowers();

        AnimationTimer gameLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {

                // add random enemies
                spawnEnemies( true);

                // check if target is still valid
                towers.forEach( tower -> tower.checkTarget());

                // tower movement: find target
                for( Tower tower: towers) {
                    tower.findTarget( enemies);
                }

                // movement
                towers.forEach(sprite -> sprite.move());
                enemies.forEach(sprite -> sprite.move());

                // check collisions
                checkCollisions();

                // update sprites in scene
                towers.forEach(sprite -> sprite.updateUI());
                enemies.forEach(sprite -> sprite.updateUI());

                // check if sprite can be removed
                enemies.forEach(sprite -> sprite.checkRemovability());

                // remove removables from list, layer, etc
                removeSprites( enemies);

                // update score, health, etc
                updateScore();
            }

        };
        gameLoop.start();

    }

    private void loadGame() {
        backgroundImage = new Image( getClass().getResource("images/background.png").toExternalForm());
        playerImage = new Image( getClass().getResource("images/tower.png").toExternalForm());
        enemyImage = new Image( getClass().getResource("images/ship.png").toExternalForm());
    }

    private void createBackgroundLayer() {
        ImageView background = new ImageView( backgroundImage);
        backgroundLayer.getChildren().add( background);
    }


    private void createPlayfieldLayer() {

        // shadow effect to show depth
        // setting it on the entire group/layer preserves the shadow angle even if the node son the layer are rotated
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(10.0);
        dropShadow.setOffsetY(10.0);

        playfieldLayer.setEffect(dropShadow);

    }

    private void createScoreLayer() {


        scoreText.setFont( Font.font( null, FontWeight.BOLD, 48));
        scoreText.setStroke(Color.BLACK);
        scoreText.setFill(Color.RED);

        scoreLayer.getChildren().add( scoreText);

        scoreText.setText( String.valueOf( score));

        double x = (Settings.SCENE_WIDTH - scoreText.getBoundsInLocal().getWidth()) / 2;
        double y = 0;
        scoreText.relocate(x, y);

        scoreText.setBoundsType(TextBoundsType.VISUAL);


    }
    private void createTowers() {

        // position initial towers
        List<Point2D> towerPositionList = new ArrayList<>();
//		towerPositionList.add(new Point2D( 100, 200));
//		towerPositionList.add(new Point2D( 100, 400));
//		towerPositionList.add(new Point2D( 1160, 200));
//		towerPositionList.add(new Point2D( 1160, 600));

        for( Point2D pos: towerPositionList) {

            createTower( pos.getX(), pos.getY());

        }

    }

    private void createTower( double x, double y) {

        Image image = playerImage;

        // center image at position
        x -= image.getWidth() / 2;
        y -= image.getHeight() / 2;

        // create player
        Tower player = new Tower(playfieldLayer, image, x, y, 180, 0, 0, 0, Settings.PLAYER_SHIP_HEALTH, 0, Settings.PLAYER_SHIP_SPEED);

        // register player
        towers.add( player);

    }

    private void spawnEnemies( boolean random) {

        if( random && rnd.nextInt(Settings.ENEMY_SPAWN_RANDOMNESS) != 0) {
            return;
        }

        // image
        Image image = enemyImage;

        // random speed
        double speed = rnd.nextDouble() * 1.0 + 2.0;

        // x position range: enemy is always fully inside the trench, no part of it is outside
        // y position: right on top of the view, so that it becomes visible with the next game iteration
        double trenchMinX; // left pixel pos of trench
        double trenchMaxX; // right pixel pos of trench

        // 2 waves: 0 = left, 1 = right
        if( rnd.nextInt(2) == 0) {
            trenchMinX = 220; // left pixel pos
            trenchMaxX = 530; // right pixel pos
        } else {
            trenchMinX = 760; // left pixel pos
            trenchMaxX = 1050; // right pixel pos
        }


        double x = trenchMinX + rnd.nextDouble() * (trenchMaxX - trenchMinX - image.getWidth());
        double y = -image.getHeight();

        // create a sprite
        Enemy enemy = new Enemy( playfieldLayer, image, x, y, 0, 0, speed, 0, 1,1);

        // manage sprite
        enemies.add( enemy);

    }

    private void removeSprites(  List<? extends SpriteBase> spriteList) {
        Iterator<? extends SpriteBase> iter = spriteList.iterator();
        while( iter.hasNext()) {
            SpriteBase sprite = iter.next();

            if( sprite.isRemovable()) {

                // remove from layer
                sprite.removeFromLayer();

                // remove from list
                iter.remove();
            }
        }
    }

    private void checkCollisions() {

        for( Tower tower: towers) {
            for( Enemy enemy: enemies) {
                if( tower.hitsTarget( enemy)) {

                    enemy.getDamagedBy( tower);

                    //TODO: explosion
                    if( !enemy.isAlive()) {

                        enemy.setRemovable(true);

                        // increase score
                        score++;

                    }
                }
            }
        }
    }

    private void updateScore() {
        scoreText.setText( String.valueOf( score));
    }

    public static void main(String[] args) {
        launch(args);
    }

}
