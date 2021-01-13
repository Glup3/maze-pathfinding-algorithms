package org.bonbo.beta.maze.generator;

import javafx.scene.canvas.GraphicsContext;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;
import java.util.Stack;

public class DepthFirstSearch extends MazeGenerator {

    private final Stack<Cell> stack;

    public DepthFirstSearch(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc) {
        super(cellSize, height, width, grid, gc);

        stack = new Stack<>();
    }

    @Override
    public void generate() {
        reset();
        getGrid().get(0).setVisited(true);
        stack.push(getGrid().get(0));

        setTimer(new AnimationTimerExt(0, 2) {
            @Override
            public void handle() {
                if (!stack.empty()) {
                    nextStep();
                } else {
                    setGenerated(true);
                    setCurrent(null);
                    updateCanvas();
                    stop();
                }
            }

            @Override
            public void renderCanvas() {
                                             updateCanvas();
                                                            }

            @Override
            public boolean isDone() {
                                      return isGenerated();
                                                           }
        });
        getTimer().start();
    }

    @Override
    void nextStep() {
        setCurrent(stack.pop());
        if (hasUnvisitedNeighbours(getCurrent())) {
            stack.push(getCurrent());
            ArrayList<Cell> neighbours = getUnvisitedNeighbours(getCurrent());
            Cell neighbour = neighbours.get(getRand().nextInt(neighbours.size()));
            removeWalls(getCurrent(), neighbour);
            neighbour.setVisited(true);
            stack.push(neighbour);
        }
    }

    @Override
    public void reset() {
        super.reset();
        stack.clear();
    }
}
