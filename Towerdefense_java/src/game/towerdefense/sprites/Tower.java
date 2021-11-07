package game.towerdefense.sprites;

import game.towerdefense.Settings;
import game.towerdefense.utils.MathUtils;

import java.util.List;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class Tower extends SpriteBase {

    game.towerdefense.sprites.SpriteBase target; // TODO: use weakreference

    double turnRate = 0.6;

    double speed;

    double targetRange = Settings.TOWER_RANGE; // distance within which a tower can lock on to an enemy

    ColorAdjust colorAdjust;

    double rotationLimitDeg=0;
    double rotationLimitRad =  Math.toDegrees( this.rotationLimitDeg);
    double roatationEasing = 10; // the higher the value, the slower the rotation
    double targetAngle = 0;
    double currentAngle = 0;

    boolean withinFiringRange = false;

    public Tower(Pane layer, Image image, double x, double y, double r, double dx, double dy, double dr, double health, double damage, double speed) {

        super(layer, image, x, y, r, dx, dy, dr, health, damage);

        this.speed = speed;

        this.currentAngle = Math.toRadians(r);

        this.setDamage(Settings.TOWER_DAMAGE);

        init();
    }

    private void init() {

        // red colorization
        colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0.0);
        colorAdjust.setHue(0.8);

    }

    @Override
    public void move() {

        SpriteBase follower = this;

        // reset within firing range
        withinFiringRange = false;

        // rotate towards target
        if( target != null)
        {

            // calculate rotation angle; follower must rotate towards the target
            // we need to consider the angle ranges from -180..180 by transforming the coordinates to a range of 0..360 and checking the values
            // the calculation is done in degrees

            double xDist = target.getCenterX() - follower.getCenterX();
            double yDist = target.getCenterY() - follower.getCenterY();

            double angleToTarget = Math.atan2(yDist, xDist) - Math.PI / 2; // -Math.PI / 2 because our sprite faces downwards

            double targetAngle = Math.toDegrees( angleToTarget);
            double currentAngle = follower.r;

            // check current angle range
            if( Math.abs(currentAngle) > 360) {
                if( currentAngle < 0) {
                    currentAngle = currentAngle % 360 + 360;
                } else {
                    currentAngle = currentAngle % 360;
                }
            }

            // calculate angle difference between follower and target
            double diff = targetAngle - currentAngle;

            // normalize the target angle
            if( Math.abs( diff) < 180) {
                // values within range => ok
            } else {
                if( diff > 0) {
                    targetAngle -= 360;
                } else {
                    targetAngle += 360;
                }
            }

            // get the angle between follower and target
            diff = targetAngle - currentAngle;

            // add the differnce angle to the current angle while considering easing when rotation comes closer to the target point
            currentAngle = currentAngle + diff / roatationEasing;

            // apply rotation
            follower.r = currentAngle;

            // determine if the player ship is within firing range; currently if the player ship is within 10 degrees (-10..+10)
            withinFiringRange = Math.abs( this.targetAngle-this.currentAngle) < 20;

        }

        super.move();

    }

    public void checkTarget() {

        if( target == null) {
            return;
        }


        if( !target.isAlive() || target.isRemovable()) {
            setTarget( null);
            return;
        }

        //get distance between follower and target
        double distanceX = target.getCenterX() - getCenterX();
        double distanceY = target.getCenterY() - getCenterY();

        //get total distance as one number
        double distanceTotal = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if( Double.compare( distanceTotal, targetRange) > 0) {
            setTarget( null);
        }

    }

    public void findTarget(List<Enemy> targetList) {


        // we already have a target
        if( getTarget() != null) {
            return;
        }

        SpriteBase closestTarget = null;
        double closestDistance = 0.0;

        for (SpriteBase target: targetList) {

            if (!target.isAlive())
                continue;

            //get distance between follower and target
            double distanceX = target.getCenterX() - getCenterX();
            double distanceY = target.getCenterY() - getCenterY();

            //get total distance as one number
            double distanceTotal = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            // check if enemy is within range
            if( Double.compare( distanceTotal, targetRange) > 0) {
                continue;
            }

            if (closestTarget == null) {

                closestTarget = target;
                closestDistance = distanceTotal;

            } else if (Double.compare(distanceTotal, closestDistance) < 0) {

                closestTarget = target;
                closestDistance = distanceTotal;

            }
        }

        setTarget(closestTarget);

    }

    public SpriteBase getTarget() {
        return target;
    }

    public void setTarget(SpriteBase target) {
        this.target = target;
    }

    @Override
    public void checkRemovability() {

        if( Double.compare( health, 0) < 0) {
            setTarget(null);
            setRemovable(true);
        }

    }

    public boolean hitsTarget(Enemy enemy) {

        return target == enemy && withinFiringRange;

    }

    public void updateUI() {

        // change effect (color/shadow) depending on whether we're firing or not
        if( withinFiringRange) {
            imageView.setEffect(colorAdjust);
        } else {
            imageView.setEffect(null);
        }

        super.updateUI();

    }
}