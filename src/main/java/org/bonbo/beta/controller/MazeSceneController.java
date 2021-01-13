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

public class MazeSceneController implements Initializable {

    public static final int CELL_SIZE = 40;

    @FXML
    private Canvas canvas;

    @FXML
    private Canvas canvas2;

    private final Random rand = new Random();

    private int height;

    private int width;

    private int rows;

    private int cols;

    private ArrayList<Cell> cells;

    private Stack<Cell> cellStack;

    private Queue<Cell> cellQueue;

    private Cell current;

    private Cell target;

    private Cell source;

    private Cell u;

    private HashSet<Cell> unvisitedCells;

    private AnimationTimerExt timer;

    private AnimationTimerExt solveTimer;

    private boolean generated;

    private boolean foundTarget;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        timer = new AnimationTimerExt(100, 5) {
            @Override
            public void handle() {
                if (!cellStack.empty()) {
                    iterativeDFS();
                } else {
                    generated = true;
                    updateCanvas(canvas.getGraphicsContext2D());
                    stop();
                }
            }

            @Override
            public void renderCanvas() {
                updateCanvas(canvas.getGraphicsContext2D());
            }
        };
        timer.start();
    }

    @FXML
    private void generateRecursive() {
        resetGrid();
        recursiveDFS(cells.get(0));
        updateCanvas(canvas.getGraphicsContext2D());
        generated = true;
    }

    @FXML
    private void resetGrid() {
        if (timer != null) {
            timer.stop();
        }
        updateCanvas(canvas.getGraphicsContext2D());
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
                    updateCanvas(canvas.getGraphicsContext2D());
                    stop();
                }
            }

            @Override
            public void renderCanvas() {
                updateCanvas(canvas.getGraphicsContext2D());
            }


        };

        timer.start();
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

    private void updateCanvas(GraphicsContext gc) {
        gc.clearRect(0, 0, width, height);

        for (Cell cell : cells) {
            if (cell == current) {
                cell.draw(gc, Color.GREEN);
            } else if (cell.isVisitedSolved() && !cell.isPath()) {
                cell.draw(gc, Color.ORANGE);
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
            unvisitedCells = new HashSet<>(cells);
            source = cells.get(0);
            target = cells.get(cells.size() - 1);
            source.setDistance(0);

            timer = new AnimationTimerExt(10) {
                @Override
                public void handle() {
                    if (!foundTarget && !unvisitedCells.isEmpty()) {
                        solveDijkstra();
                    } else {
                        stop();

                        u = target;
                        if (u.getPrevious() != null || target == source) {
                            solveTimer = new AnimationTimerExt(10) {
                                @Override
                                public void handle() {
                                   if (u != null) {
                                       solveDijkstraPath();
                                   } else {
                                       stop();
                                       updateCanvas(canvas2.getGraphicsContext2D());
                                   }
                                }

                                @Override
                                public void renderCanvas() {
                                    updateCanvas(canvas2.getGraphicsContext2D());
                                }
                            };
                            solveTimer.start();
                        }
                    }
                }

                @Override
                public void renderCanvas() {
                    updateCanvas(canvas2.getGraphicsContext2D());
                }
            };

            timer.start();
        } else {
            System.out.println("No maze found...");
        }
    }

    private void solveDijkstra() {
        Cell cell = unvisitedCells
                .stream()
                .min(Comparator.comparing(Cell::getDistance))
                .orElseThrow(NoSuchElementException::new);

        cell.setVisitedSolved(true);
        unvisitedCells.remove(cell);

        if (cell == target) {
            foundTarget = true;
        }

        getNeighbours(cell).forEach(c -> {
            int alt = cell.getDistance() + 1;
            if (alt < c.getDistance()) {
                c.setVisitedSolved(true);
                c.setDistance(alt);
                c.setPrevious(cell);
            }
        });
    }

    private void solveDijkstraPath() {
        u.setPath(true);
        u = u.getPrevious();
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

    @FXML
    private void solveBFS() {
        cellQueue = new LinkedList<>();
        current = cells.get(0);
        target = cells.get(cells.size() - 1);
        current.setVisitedSolved(true);
        cellQueue.add(current);

        if (generated) {
            timer = new AnimationTimerExt(10) {
                @Override
                public void handle() {
                    if (!cellQueue.isEmpty()) {
                        bfs();
                    } else {
                        stop();
                        u = target.getPrevious();
                        solveTimer = new AnimationTimerExt(10) {
                            @Override
                            public void handle() {
                                if (u.getPrevious() != null) {
                                    solveDijkstraPath();
                                } else {
                                    stop();
                                    updateCanvas(canvas2.getGraphicsContext2D());
                                }
                            }

                            @Override
                            public void renderCanvas() {
                                updateCanvas(canvas2.getGraphicsContext2D());
                            }
                        };

                        solveTimer.start();
                    }
                }

                @Override
                public void renderCanvas() {
                    updateCanvas(canvas2.getGraphicsContext2D());
                }
            };

            timer.start();
        } else {
            System.out.println("No Maze found...");
        }
    }

    private void bfs() {
        Cell v = cellQueue.remove();

        if (v == target) {
            cellQueue.clear();
        } else {
            for (Cell w : getNeighbours(v)) {
                if (!w.isVisitedSolved()) {
                    w.setVisitedSolved(true);
                    w.setPrevious(v);
                    cellQueue.add(w);
                }
            }
        }
    }

}
