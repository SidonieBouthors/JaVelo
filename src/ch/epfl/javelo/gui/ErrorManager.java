package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class ErrorManager {

    private final Pane pane;
    private final Text errorText;
    private final VBox errorBox;
    private final SequentialTransition animation;

    public ErrorManager(){
        this.errorText = new Text();
        this.errorBox = new VBox(errorText);
        errorBox.getStylesheets().add("error.css");

        this.pane = new BorderPane(errorBox);
        pane.setMouseTransparent(true);

        FadeTransition appearTransition = new FadeTransition(new Duration(200));
        appearTransition.setToValue(0.8);
        PauseTransition showTransition = new PauseTransition(new Duration(2000));
        FadeTransition disappearTransition = new FadeTransition(new Duration(500));
        disappearTransition.setToValue(0);
        this.animation = new SequentialTransition(appearTransition, showTransition, disappearTransition);
        animation.setNode(errorBox);
    }

    public Pane pane(){
        return pane;
    }

    public void displayError(String errorMessage){
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorText.setText(errorMessage);
        animation.stop();
        animation.play();
    }
}
