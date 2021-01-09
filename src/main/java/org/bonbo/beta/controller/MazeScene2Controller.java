package org.bonbo.beta.controller;

import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Stack;

public class MazeScene2Controller implements Initializable {

    public static final int CELL_SIZE = 60;

    @FXML
    private Canvas canvas;

    private GraphicsContext gc;

    private final Random rand = new Random();

    private int height;

    private int width;

    private int rows;

    private int cols;

    private ArrayList<Cell> cells;

    private Stack<Cell> cellStack;

    private Cell current;

    private AnimationTimerExt timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        cells = new ArrayList<>();
        cellStack = new Stack<>();
        height = (int) canvas.getHeight();
        width = (int) canvas.getWidth();

        rows = Math.floorDiv(height, CELL_SIZE);
        cols = Math.floorDiv(width, CELL_SIZE);

        resetGrid();

        timer = new AnimationTimerExt(10) {
            @Override
            public void handle() {
                if (!cellStack.empty()) {
                    iterativeDFS();
                } else {
                    stop();
                }
                updateCanvas();
            }
        };
    }

    @FXML
    private void generateIterative() {
        initCells();
        cells.get(0).setVisited(true);
        cellStack.push(cells.get(0));

        timer.start();
    }

    @FXML
    private void generateRecursive() {
        resetGrid();
        recursiveDFS(cells.get(0));
        updateCanvas();
    }

    @FXML
    private void resetGrid() {
        if (timer != null) {
            timer.stop();
        }
        initCells();
        updateCanvas();
    }

    private void initCells() {
        cells.clear();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                cells.add(Cell.builder().x(x * CELL_SIZE).y(y * CELL_SIZE).build());
            }
        }
    }

    private void recursiveDFS(Cell cell) {
        cell.setVisited(true);

        while (hasUnvisitedNeighbours(cell)) {
            ArrayList<Cell> neighbours = getUnvisitedNeighbours(cell);
            Cell next = neighbours.get(rand.nextInt(neighbours.size()));
            removeWalls(cell, next);
            recursiveDFS(next);
        }
    }

    private void iterativeDFS() {
        current = cellStack.pop();
        if (hasUnvisitedNeighbours(current)) {
            cellStack.push(current);
            ArrayList<Cell> neighbours = getUnvisitedNeighbours(current);
            Cell neighbour = neighbours.get(rand.nextInt(neighbours.size()));
            removeWalls(current, neighbour);
            neighbour.setVisited(true);
            cellStack.push(neighbour);
        }
    }

    private void updateCanvas() {
        gc.clearRect(0, 0, width, height);

        for (Cell cell : cells) {
            cell.draw(gc);
            if (cell == current) {
                cell.draw(gc, Color.GREEN);
            }
        }
    }

    private int index(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            return -1;
        }

        return (x / CELL_SIZE) + (y / CELL_SIZE) * cols;
    }

    private ArrayList<Cell> getUnvisitedNeighbours(Cell cell) {
        ArrayList<Cell> neighbours = new ArrayList<>();
        int topIndex = index(cell.getX(), cell.getY() - CELL_SIZE);
        int rightIndex = index(cell.getX() + CELL_SIZE, cell.getY());
        int bottomIndex = index(cell.getX(), cell.getY() + CELL_SIZE);
        int leftIndex = index(cell.getX() - CELL_SIZE, cell.getY());

        if (topIndex > 0 && !cells.get(topIndex).isVisited()) {
            neighbours.add(cells.get(topIndex));
        }
        if (rightIndex > 0 && !cells.get(rightIndex).isVisited()) {
            neighbours.add(cells.get(rightIndex));
        }
        if (bottomIndex > 0 && !cells.get(bottomIndex).isVisited()) {
            neighbours.add(cells.get(bottomIndex));
        }
        if (leftIndex > 0 && !cells.get(leftIndex).isVisited()) {
            neighbours.add(cells.get(leftIndex));
        }

        return neighbours;
    }

    private boolean hasUnvisitedNeighbours(Cell cell) {
        int topIndex = index(cell.getX(), cell.getY() - CELL_SIZE);
        int rightIndex = index(cell.getX() + CELL_SIZE, cell.getY());
        int bottomIndex = index(cell.getX(), cell.getY() + CELL_SIZE);
        int leftIndex = index(cell.getX() - CELL_SIZE, cell.getY());

        if (topIndex > 0 && !cells.get(topIndex).isVisited()) {
            return true;
        }
        if (rightIndex > 0 && !cells.get(rightIndex).isVisited()) {
            return true;
        }
        if (bottomIndex > 0 && !cells.get(bottomIndex).isVisited()) {
            return true;
        }
        if (leftIndex > 0 && !cells.get(leftIndex).isVisited()) {
            return true;
        }

        return false;
    }

    private void removeWalls(Cell a, Cell b) {
        int x = a.getX() - b.getX();
        int y = a.getY() - b.getY();

        if (x == -CELL_SIZE) {
            a.getWalls()[1] = false;
            b.getWalls()[3] = false;
        } else if (x == CELL_SIZE) {
            a.getWalls()[3] = false;
            b.getWalls()[1] = false;
        }

        if (y == -CELL_SIZE) {
            a.getWalls()[2] = false;
            b.getWalls()[0] = false;
        } else if (y == CELL_SIZE) {
            a.getWalls()[0] = false;
            b.getWalls()[2] = false;
        }
    }
}
