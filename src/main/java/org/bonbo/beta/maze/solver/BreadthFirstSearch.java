package org.bonbo.beta.maze.solver;

import javafx.scene.canvas.GraphicsContext;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;
import java.util.LinkedList;

public class BreadthFirstSearch extends MazeSolver {

    private final LinkedList<Cell> queue;

    public BreadthFirstSearch(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc, Cell source, Cell target) {
        super(cellSize, height, width, grid, gc, source, target);
        queue = new LinkedList<>();
    }

    @Override
    public void solve() {
        setCurrent(getSource());
        getCurrent().setVisitedSolved(true);
        queue.add(getCurrent());

        setTimer(new AnimationTimerExt(10) {
            @Override
            public void handle() {
                if (!queue.isEmpty()) {
                    nextStep();
                } else {
                    stop();
                    setFoundTarget(true);
                    setCurrent(null);
                    updateCanvas();
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
        Cell v = queue.remove();

        if (v == getTarget()) {
            queue.clear();
        } else {
            for (Cell w : getNeighbours(v)) {
                if (!w.isVisitedSolved()) {
                    setCurrent(w);
                    w.setVisitedSolved(true);
                    w.setPrevious(v);
                    queue.add(w);
                }
            }
        }
    }

    private void solve2() {
        setVertex(getTarget().getPrevious());
        setTimer(new AnimationTimerExt(100) {
            @Override
            public void handle() {
                if (getVertex().getPrevious() != null) {
                    path();
                } else {
                    stop();
                    setSolved(true);
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

    private void path() {
        getVertex().setPath(true);
        setVertex(getVertex().getPrevious());
    }

}
