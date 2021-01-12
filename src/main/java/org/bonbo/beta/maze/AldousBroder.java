package org.bonbo.beta.maze;

import javafx.scene.canvas.GraphicsContext;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;

public class AldousBroder extends MazeGenerator {
    public AldousBroder(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc) {
        super(cellSize, height, width, grid, gc);
    }

    @Override
    public void generate() {
        reset();

        setCurrent(getGrid().get(getRand().nextInt(getGrid().size())));

        setTimer(new AnimationTimerExt(0, 2) {
            @Override
            public void handle() {
                if (!getGrid().parallelStream().allMatch(Cell::isVisited)) {
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
        Cell cell = getRandomNeighbour(getCurrent());
        if (!cell.isVisited()) {
            removeWalls(getCurrent(), cell);
            cell.setVisited(true);
        }

        setCurrent(cell);
    }
}
