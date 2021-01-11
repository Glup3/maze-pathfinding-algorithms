package org.bonbo.beta.controller;

import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class MazeScene2Controller implements Initializable {

    public static final int CELL_SIZE = 40;

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

    private boolean generated;

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
    }

    @FXML
    private void generateIterative() {
        resetGrid();
        cells.get(0).setVisited(true);
        cellStack.push(cells.get(0));

        timer = new AnimationTimerExt(10, 10) {
            @Override
            public void handle() {
                if (!cellStack.empty()) {
                    iterativeDFS();
                } else {
                    generated = true;
                    updateCanvas();
                    stop();
                }
            }

            @Override
            public void renderCanvas() {
                updateCanvas();
            }
        };
        timer.start();
    }

    @FXML
    private void generateRecursive() {
        resetGrid();
        recursiveDFS(cells.get(0));
        updateCanvas();
        generated = true;
    }

    @FXML
    private void resetGrid() {
        if (timer != null) {
            timer.stop();
        }
        initCells();
        updateCanvas();
        generated = false;
    }

    @FXML
    private void generateAldousBroder() {
        resetGrid();
        current = cells.get(rand.nextInt(cells.size()));

        timer = new AnimationTimerExt(0, 20) {
            @Override
            public void handle() {
                if (!cells.parallelStream().allMatch(Cell::isVisited)) {
                   aldousBroder();
                } else {
                    generated = true;
                    updateCanvas();
                    stop();
                }
            }

            @Override
            public void renderCanvas() {
                updateCanvas();
            }
        };

        timer.start();
    }

    private void initCells() {
        cells.clear();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                cells.add(Cell.builder().x(x * CELL_SIZE).y(y * CELL_SIZE).build());
            }
        }
    }

    private void aldousBroder() {
        Cell cell = getRandomNeighbour(current);
        if (!cell.isVisited()) {
            removeWalls(current, cell);
            cell.setVisited(true);
        }

        current = cell;
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
            if (cell == current) {
                cell.draw(gc, Color.GREEN);
            } else if (cell.isPath()) {
                cell.draw(gc, Color.PURPLE);
            }
        }

        cells.get(0).draw(gc, Color.YELLOW);
        cells.get(cells.size() - 1).draw(gc, Color.RED);

        for (Cell cell : cells) {
            cell.draw(gc);
        }
    }

    private int index(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            return -1;
        }

        return (x / CELL_SIZE) + (y / CELL_SIZE) * cols;
    }

    private int index(Cell cell, Positions position) {
        return switch (position) {
            case TOP -> index(cell.getX(), cell.getY() - CELL_SIZE);
            case RIGHT -> index(cell.getX() + CELL_SIZE, cell.getY());
            case BOTTOM -> index(cell.getX(), cell.getY() + CELL_SIZE);
            case LEFT -> index(cell.getX() - CELL_SIZE, cell.getY());
        };
    }

    private ArrayList<Cell> getUnvisitedNeighbours(Cell cell) {
        ArrayList<Cell> neighbours = new ArrayList<>();
        int topIndex = index(cell, Positions.TOP);
        int rightIndex = index(cell, Positions.RIGHT);
        int bottomIndex = index(cell, Positions.BOTTOM);
        int leftIndex = index(cell, Positions.LEFT);

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
        int topIndex = index(cell, Positions.TOP);
        int rightIndex = index(cell, Positions.RIGHT);
        int bottomIndex = index(cell, Positions.BOTTOM);
        int leftIndex = index(cell, Positions.LEFT);

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

    @FXML
    private void solve() {
        if (generated) {
            solveDijkstra(cells.get(0), cells.get(cells.size() - 1));
        } else {
            System.out.println("No maze found...");
        }
    }

    private void solveDijkstra(Cell source, Cell target) {
        Set<Cell> unvisited = new HashSet<>(cells);
        source.setDistance(0);

        while (!unvisited.isEmpty()) {
            Cell cell = unvisited
                    .stream()
                    .min(Comparator.comparing(Cell::getDistance))
                    .orElseThrow(NoSuchElementException::new);

            unvisited.remove(cell);

            if (cell == target) {
                break;
            }

            getNeighbours(cell).forEach(c -> {
                int alt = cell.getDistance() + 1;
                if (alt < c.getDistance()) {
                    c.setDistance(alt);
                    c.setPrevious(cell);
                }
            });
        }

        Cell u = target;

        if (u.getPrevious() != null || target == source) {
            while (u != null) {
                u.setPath(true);
                u = u.getPrevious();
            }
        }

        updateCanvas();
    }

    private ArrayList<Cell> getNeighbours(Cell cell) {
        ArrayList<Cell> neighbours = new ArrayList<>();

        if (!cell.getWalls()[0]) {
            neighbours.add(cells.get(index(cell, Positions.TOP)));
        }
        if (!cell.getWalls()[1]) {
            neighbours.add(cells.get(index(cell, Positions.RIGHT)));
        }
        if (!cell.getWalls()[2]) {
            neighbours.add(cells.get(index(cell, Positions.BOTTOM)));
        }
        if (!cell.getWalls()[3]) {
           neighbours.add(cells.get(index(cell, Positions.LEFT)));
        }

        return neighbours;
    }

    private Cell getRandomNeighbour(Cell cell) {
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

        return cells.get(i);
    }

    private enum Positions {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }
}
