package org.bonbo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bonbo.alpha.MainApp;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // start Jake's App, temporary
         new MainApp(stage);
//        Parent root = FXMLLoader.load(getClass().getResource("/beta/view/MazeScene2.fxml"));
//
//        Scene scene = new Scene(root);
//
//        stage.setTitle("Main Scene");
//        stage.setScene(scene);
//        stage.show();
    }
}
