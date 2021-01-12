package org.bonbo.beta.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.maze.AldousBroder;
import org.bonbo.beta.maze.DepthFirstSearch;
import org.bonbo.beta.maze.MazeGenerator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MazeSceneController2 implements Initializable {

    @FXML
    private Canvas canvas;

    private MazeGenerator generator;

    private final ArrayList<Cell> grid = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // generator = new AldousBroder(40, (int) canvas.getHeight(), (int) canvas.getWidth(), grid, canvas.getGraphicsContext2D());
        generator = new DepthFirstSearch(40, (int) canvas.getHeight(), (int) canvas.getWidth(), grid, canvas.getGraphicsContext2D());
    }

    @FXML
    private void generateMaze() {
        generator.generate();
    }

    @FXML
    private void resetGrid() {
        generator.reset();
    }
}
