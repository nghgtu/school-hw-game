package game.towerdefense.grid;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Grid extends Pane {

    int rows;
    int columns;

    double width;
    double height;

    double cellWidth = width / columns;
    double cellHeight = height / rows;

    Cell[][] cellArray;

    public Grid( int columns, int rows, double width, double height) {

        this.columns = columns;
        this.rows = rows;
        this.width = width;
        this.height = height;

        cellWidth = width / columns;
        cellHeight = height / rows;

        // create grid and add cells to it, add cells also to the layer
        cellArray = new Cell[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {

                Cell cell = new Cell( row + "/" + col);

                double x = cellWidth * col;
                double y = cellHeight * row;

                cell.setLayoutX(x);
                cell.setLayoutY(y);
                cell.setPrefWidth( cellWidth);
                cell.setPrefHeight( cellHeight);

                // register cell
                cellArray[row][col] = cell;

                getChildren().add(cell);

            }
        }

        // add hover mouse listener to the grid
        HoverMouseGestures mg = new HoverMouseGestures( this);

        addEventFilter(MouseEvent.MOUSE_ENTERED, mg.getShowHoverEvent());
        addEventFilter(MouseEvent.MOUSE_MOVED, mg.getShowHoverEvent());
        addEventFilter(MouseEvent.MOUSE_EXITED, mg.getHideHoverEvent());
    }


    public Cell getCell(int col, int row) {
        return cellArray[row][col];
    }

    public Cell getCellAt( double sceneX, double sceneY) {

        int col = (int) ( sceneX / cellWidth);
        int row = (int) ( sceneY / cellHeight);

        if( col < 0 || col >= columns)
            return null;

        if( row < 0 || row >= rows)
            return null;

        return cellArray[row][col];
    }

    public Bounds getBoundsAt(  double sceneX, double sceneY) {

        int col = (int) ( sceneX / cellWidth);
        int row = (int) ( sceneY / cellHeight);

        if( col < 0 || col >= columns)
            return null;

        if( row < 0 || row >= rows)
            return null;

//		Cell cell = cellArray[row][col];

        Bounds bounds = new BoundingBox( col * cellWidth, row * cellHeight, cellWidth, cellHeight);

        return bounds;
    }
}
