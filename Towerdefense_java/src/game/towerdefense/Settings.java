package game.towerdefense;

public class Settings {

    // fullscreen or windowed mode
    public static boolean STAGE_FULLSCREEN = false;

    // scale by factor of 2 (in settings we have half-hd) to get proper dimensions in fullscreen (full-hd)
    public static double STAGE_FULLSCREEN_SCALE = 2;

    public static double SCENE_WIDTH = 1280;
    public static double SCENE_HEIGHT = 720;


    public static double PLAYFIELD_WIDTH = SCENE_WIDTH;
    public static double PLAYFIELD_HEIGHT = SCENE_HEIGHT;

    public static int TOWER_GRID_COLUMNS = 10;
    public static int TOWER_GRID_ROWS = 6;


    public static double TOWER_DAMAGE = 1;

    // distance within which a tower can lock on to an enemy
    public static double TOWER_RANGE = 400;

    public static double PLAYER_SHIP_SPEED = 4.0;
    public static double PLAYER_SHIP_HEALTH = 100.0;

    public static int ENEMY_HEALTH = 500;
    public static int ENEMY_SPAWN_RANDOMNESS = 30;

}



