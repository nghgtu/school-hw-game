package game.towerdefense.grid;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Cell extends StackPane {

    String text;

    public Cell( String text) {

        getStyleClass().add("cell");

        this.text = text;

        Label label = new Label( text);

        getChildren().add(label);
    }

    public void showHighlight() {
        if( !getStyleClass().contains("cell-highlight")) {
            getStyleClass().add("cell-highlight");
        }
    }

    public void hideHighlight() {
        getStyleClass().remove("cell-highlight");
    }

    public void showHover() {
        if( !getStyleClass().contains("cell-hover")) {
            getStyleClass().add("cell-hover");
        }
    }

    public void hideHover() {
        getStyleClass().remove("cell-hover");
    }

    public String toString() {
        return text;
    }

}
