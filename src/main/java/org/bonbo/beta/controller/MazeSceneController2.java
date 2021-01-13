package org.bonbo.beta.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.maze.generator.AldousBroder;
import org.bonbo.beta.maze.generator.DepthFirstSearch;
import org.bonbo.beta.maze.generator.Generators;
import org.bonbo.beta.maze.generator.MazeGenerator;
import org.bonbo.beta.maze.solver.BreadthFirstSearch;
import org.bonbo.beta.maze.solver.Dijkstra;
import org.bonbo.beta.maze.solver.MazeSolver;
import org.bonbo.beta.maze.solver.Solvers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MazeSceneController2 implements Initializable {

    private final static int SIZE = 60;

    @FXML
    private Canvas canvasGrid;

    @FXML
    private Canvas canvasSolution;

    @FXML
    private ComboBox<Generators> generatorComboBox;

    @FXML
    private ComboBox<Solvers> solverComboBox;

    private MazeGenerator generator;

    private MazeSolver solver;

    private final ArrayList<Cell> grid = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboBoxes();
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
            solver.solve();
        }
    }

    @FXML
    private void resetSolution() {
        solver.reset();
    }

    private void initComboBoxes() {
        generatorComboBox.getItems().setAll(Generators.values());
        generatorComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (generator != null && generator.getTimer() != null) {
                generator.getTimer().stop();
            }
            generator = switch (newValue) {
                case ALDOUS_BRODER -> new AldousBroder(SIZE, (int) canvasGrid.getHeight(), (int) canvasGrid.getWidth(), grid, canvasGrid.getGraphicsContext2D());
                case DEPTH_FIRST_SEARCH -> new DepthFirstSearch(SIZE, (int) canvasGrid.getHeight(), (int) canvasGrid.getWidth(), grid, canvasGrid.getGraphicsContext2D());
            };
        });
        generatorComboBox.getSelectionModel().selectFirst();

        solverComboBox.getItems().setAll(Solvers.values());
        solverComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (solver != null && solver.getTimer() != null) {
                solver.getTimer().stop();
            }
            solver = switch (newValue) {
                case BREADTH_FIRST_SEARCH -> new BreadthFirstSearch(SIZE, (int) canvasGrid.getHeight(), (int) canvasGrid.getWidth(), grid, canvasSolution.getGraphicsContext2D(), 0, grid.size() - 1);
                case DIJKSTRA -> new Dijkstra(SIZE, (int) canvasGrid.getHeight(), (int) canvasGrid.getWidth(), grid, canvasSolution.getGraphicsContext2D(), 0, grid.size() - 1);
            };
        });
        solverComboBox.getSelectionModel().selectFirst();
    }
}
