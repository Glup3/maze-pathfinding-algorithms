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
    public void start(Stage stage) throws Exception  {
        startMain(stage);
        // quickStartAlpha(stage);
        // quickStartBeta(stage);
    }

    public void startMain(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/common/view/MainScene.fxml"));

        stage.setTitle("Maze Pathfinding Algorithms");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void quickStartAlpha(Stage stage) {
        new MainApp(stage);
    }

    public void quickStartBeta(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/beta/view/MazeScene2.fxml"));

        stage.setTitle("Beta Algorithms");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
