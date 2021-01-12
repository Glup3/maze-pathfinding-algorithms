package org.bonbo.beta.maze;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.*;
import org.bonbo.beta.controller.MazeSceneController;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
public abstract class MazeGenerator {

    private int cellSize;

    private int height;

    private int width;

    private ArrayList<Cell> grid;

    private GraphicsContext gc;

    private Cell current;

    private AnimationTimerExt timer;

    private Random rand = new Random();

    private boolean generated;

    public MazeGenerator(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc) {
        this.cellSize = cellSize;
        this.height = height;
        this.width = width;
        this.grid = grid;
        this.gc = gc;

        initCells();
        updateCanvas();
    }

    public abstract void generate();

    abstract void nextStep();

    public void reset() {
        if (timer != null) {
            timer.stop();
        }
        current = null;
        generated = false;
        initCells();
        updateCanvas();
    }

    void initCells() {
        grid.clear();
        int rows = Math.floorDiv(height, cellSize);
        int cols = Math.floorDiv(width, cellSize);

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                grid.add(Cell.builder().x(x * cellSize).y(y * cellSize).build());
            }
        }
    }

    void updateCanvas() {
        gc.clearRect(0, 0, width, height);

        for (Cell cell : grid) {
            if (cell == current) {
                cell.draw(gc, Color.GREEN);
            } else if (cell.isVisitedSolved() && !cell.isPath()) {
                cell.draw(gc, Color.ORANGE);
            } else if (cell.isPath()) {
                cell.draw(gc, Color.PURPLE);
            }
        }

        for (Cell cell : grid) {
            cell.draw(gc);
        }
    }

    int index(int x, int y) {
        int cols = Math.floorDiv(width, cellSize);
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            return -1;
        }

        return (x / cellSize) + (y / cellSize) * cols;
    }

    int index(Cell cell, Positions position) {
        return switch (position) {
            case TOP -> index(cell.getX(), cell.getY() - cellSize);
            case RIGHT -> index(cell.getX() + cellSize, cell.getY());
            case BOTTOM -> index(cell.getX(), cell.getY() + cellSize);
            case LEFT -> index(cell.getX() - cellSize, cell.getY());
        };
    }

    ArrayList<Cell> getUnvisitedNeighbours(Cell cell) {
        ArrayList<Cell> neighbours = new ArrayList<>();
        int topIndex = index(cell, Positions.TOP);
        int rightIndex = index(cell, Positions.RIGHT);
        int bottomIndex = index(cell, Positions.BOTTOM);
        int leftIndex = index(cell, Positions.LEFT);

        if (topIndex > 0 && !grid.get(topIndex).isVisited()) {
            neighbours.add(grid.get(topIndex));
        }
        if (rightIndex > 0 && !grid.get(rightIndex).isVisited()) {
            neighbours.add(grid.get(rightIndex));
        }
        if (bottomIndex > 0 && !grid.get(bottomIndex).isVisited()) {
            neighbours.add(grid.get(bottomIndex));
        }
        if (leftIndex > 0 && !grid.get(leftIndex).isVisited()) {
            neighbours.add(grid.get(leftIndex));
        }

        return neighbours;
    }

    boolean hasUnvisitedNeighbours(Cell cell) {
        int topIndex = index(cell, Positions.TOP);
        int rightIndex = index(cell, Positions.RIGHT);
        int bottomIndex = index(cell, Positions.BOTTOM);
        int leftIndex = index(cell, Positions.LEFT);

        if (topIndex > 0 && !grid.get(topIndex).isVisited()) {
            return true;
        }
        if (rightIndex > 0 && !grid.get(rightIndex).isVisited()) {
            return true;
        }
        if (bottomIndex > 0 && !grid.get(bottomIndex).isVisited()) {
            return true;
        }
        if (leftIndex > 0 && !grid.get(leftIndex).isVisited()) {
            return true;
        }

        return false;
    }

    Cell getRandomNeighbour(Cell cell) {
        int i = -1;

        while (i == -1) {
            int r = rand.nextInt(4);

            i = switch (r) {
                case 0 -> (index(cell, Positions.TOP));
                case 1 -> (index(cell, Positions.RIGHT));
                case 2 -> (index(cell, Positions.BOTTOM));
                case 3 -> (index(cell, Positions.LEFT));
                default -> -1;
            };
        }

        return grid.get(i);
    }

    void removeWalls(Cell a, Cell b) {
        int x = a.getX() - b.getX();
        int y = a.getY() - b.getY();

        if (x == -cellSize) {
            a.getWalls()[1] = false;
            b.getWalls()[3] = false;
        } else if (x == cellSize) {
            a.getWalls()[3] = false;
            b.getWalls()[1] = false;
        }

        if (y == -cellSize) {
            a.getWalls()[2] = false;
            b.getWalls()[0] = false;
        } else if (y == cellSize) {
            a.getWalls()[0] = false;
            b.getWalls()[2] = false;
        }

    }

    enum Positions {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }
}
