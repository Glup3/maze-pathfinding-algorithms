package org.bonbo;

import javafx.application.Application;
import javafx.stage.Stage;
import org.bonbo.alpha.MainApp;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // start Jake's App, temporary
        new MainApp(primaryStage);
    }
}
