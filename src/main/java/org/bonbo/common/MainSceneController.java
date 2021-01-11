package org.bonbo.common;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bonbo.alpha.MainApp;

import java.net.URL;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void showAlpha() {
        new MainApp(new Stage());
    }

    @FXML
    private void showBeta() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/beta/view/MazeScene.fxml"));
        Stage stage = new Stage();

        stage.setTitle("Beta Algorithms");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void showGamma() { }

    @FXML
    private void showDelta() { }

    @FXML
    private void showEpsilon() { }

    @FXML
    private void showZeta() { }

}
