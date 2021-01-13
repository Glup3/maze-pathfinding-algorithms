package org.bonbo.beta.maze.solver;

import javafx.scene.canvas.GraphicsContext;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class Dijkstra extends MazeSolver {

    private HashSet<Cell> unvisitedCells;

    public Dijkstra(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc, Cell source, Cell target) {
        super(cellSize, height, width, grid, gc, source, target);
    }

    @Override
    public void solve() {
        unvisitedCells = new HashSet<>(getGrid());
        setSource(getGrid().get(0));
        setTarget(getGrid().get(getGrid().size() - 1));
        getSource().setDistance(0);

        setTimer(new AnimationTimerExt(100) {
            @Override
            public void handle() {
                if (!isFoundTarget() && !unvisitedCells.isEmpty()) {
                    nextStep();
                } else {
                    stop();
                    setCurrent(null);
                    setFoundTarget(true);
                    solve2();
                }
            }

            @Override
            public void renderCanvas() {
                updateCanvas();
            }
        });

        getTimer().start();
    }

    @Override
    void nextStep() {
        Cell cell = unvisitedCells
                .stream()
                .min(Comparator.comparing(Cell::getDistance))
                .orElseThrow(NoSuchElementException::new);

        cell.setVisitedSolved(true);
        unvisitedCells.remove(cell);

        if (cell == getTarget()) {
            setFoundTarget(true);
        }

        getNeighbours(cell).forEach(c -> {
            int alt = cell.getDistance() + 1;
            if (alt < c.getDistance()) {
                setCurrent(c);
                c.setVisitedSolved(true);
                c.setDistance(alt);
                c.setPrevious(cell);
            }
        });
    }

    private void solve2() {
        setVertex(getTarget());

        if (getVertex().getPrevious() != null || getTarget() == getSource()) {
            setTimer(new AnimationTimerExt(10) {
                @Override
                public void handle() {
                    if (getVertex() != null) {
                        path();
                    } else {
                        stop();
                        setCurrent(null);
                        updateCanvas();
                    }
                }

                @Override
                public void renderCanvas() {
                    updateCanvas();
                }
            });

            getTimer().start();
        }
    }

    void path() {
        getVertex().setPath(true);
        setVertex(getVertex().getPrevious());
    }

}
