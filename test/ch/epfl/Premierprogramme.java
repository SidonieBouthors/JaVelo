package ch.epfl;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Premierprogramme extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("Premiere Application");
        Scene scene = new Scene(button, 400, 200);
        primaryStage.setScene(scene);

        primaryStage.setTitle("Premiere application");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
