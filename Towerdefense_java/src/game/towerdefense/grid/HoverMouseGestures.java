package game.towerdefense.grid;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class HoverMouseGestures {

    Grid grid;

    Cell prevHighlight = null;

    public HoverMouseGestures( Grid grid) {
        this.grid = grid;
    }

    public EventHandler<MouseEvent> getShowHoverEvent() {
        return highlightEvent;
    }

    public EventHandler<MouseEvent> getHideHoverEvent() {
        return unhighlightEvent;
    }

    private EventHandler<MouseEvent> highlightEvent = e -> {

        // get cell
        Cell cell = grid.getCellAt( e.getSceneX(), e.getSceneY());

        if( cell != prevHighlight) {

            if( cell != null) {
                cell.showHover();
            }

            if( prevHighlight != null) {
                prevHighlight.hideHover();
            }

            // remember for next event (unhighlight etc)
            prevHighlight = cell;
        }

    };

    private EventHandler<MouseEvent> unhighlightEvent = e -> {

        if( prevHighlight != null) {
            prevHighlight.hideHover();
        }

        prevHighlight = null;

    };

}
