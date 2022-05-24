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
    private final SequentialTransition animation;
    private final static String CSS_ERROR = "error.css";
    private final static double MAX_OPACITY = 0.8;
    private final static double MIN_OPACITY = 0;
    private final static Duration APPEAR_DURATION = new Duration(200);
    private final static Duration SHOW_DURATION = new Duration(2000);
    private final static Duration DISAPPEAR_DURATION = new Duration(500);

    public ErrorManager(){
        this.errorText = new Text();
        VBox errorBox = new VBox(errorText);
        errorBox.getStylesheets().add(CSS_ERROR);

        this.pane = new BorderPane(errorBox);
        pane.setMouseTransparent(true);

        FadeTransition appearTransition = new FadeTransition(APPEAR_DURATION);
        appearTransition.setToValue(MAX_OPACITY);
        PauseTransition showTransition = new PauseTransition(SHOW_DURATION);
        FadeTransition disappearTransition = new FadeTransition(DISAPPEAR_DURATION);
        disappearTransition.setToValue(MIN_OPACITY);
        this.animation = new SequentialTransition(appearTransition, showTransition, disappearTransition);
        animation.setNode(errorBox);
    }

    /**
     * Returns the pane containing the error message
     * @return the pane
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Displays the given error message onto the pane
     * @param errorMessage  : error message to display
     */
    public void displayError(String errorMessage){
        java.awt.Toolkit.getDefaultToolkit().beep();
        errorText.setText(errorMessage);
        animation.stop();
        animation.play();
    }
}
