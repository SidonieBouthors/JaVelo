package ch.epfl.javelo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JavaFXMiniAppTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {


            Button button = new Button("Premiere Application");
            Scene scene = new Scene(button, 400, 200);
            primaryStage.setScene(scene);

            primaryStage.setTitle("Premiere application");

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
