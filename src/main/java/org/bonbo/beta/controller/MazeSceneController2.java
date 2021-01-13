package org.bonbo.beta.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.maze.generator.DepthFirstSearch;
import org.bonbo.beta.maze.generator.MazeGenerator;
import org.bonbo.beta.maze.solver.BreadthFirstSearch;
import org.bonbo.beta.maze.solver.Dijkstra;
import org.bonbo.beta.maze.solver.MazeSolver;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MazeSceneController2 implements Initializable {

    private final static int SIZE = 60;

    @FXML
    private Canvas canvasGrid;

    @FXML
    private Canvas canvasSolution;

    private MazeGenerator generator;

    private MazeSolver solver;

    private final ArrayList<Cell> grid = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // generator = new AldousBroder(40, (int) canvas.getHeight(), (int) canvas.getWidth(), grid, canvas.getGraphicsContext2D());
        generator = new DepthFirstSearch(SIZE, (int) canvasGrid.getHeight(), (int) canvasGrid.getWidth(), grid, canvasGrid.getGraphicsContext2D());
    }

    @FXML
    private void generateMaze() {
        generator.generate();
    }

    @FXML
    private void resetGrid() {
        resetSolution();
        generator.reset();
    }

    @FXML
    private void solve() {
        if (generator.isGenerated()) {
            solver = new Dijkstra(SIZE, (int) canvasGrid.getHeight(), (int) canvasGrid.getWidth(), grid, canvasSolution.getGraphicsContext2D(), 0, grid.size() - 1);
            solver.solve();
        }
    }

    @FXML
    private void resetSolution() {
        if (solver != null) {
            solver.reset();
        }
    }
}
