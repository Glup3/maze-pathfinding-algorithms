package org.bonbo.beta.maze.generator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.*;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.maze.Maze;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;
import java.util.Random;

@Getter
@Setter
public abstract class MazeGenerator extends Maze {

    private Cell current;

    private AnimationTimerExt timer;

    private Random rand = new Random();

    private boolean generated;

    public MazeGenerator(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc) {
        super(cellSize, height, width, grid, gc);

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

    void updateCanvas() {
        getGc().clearRect(0, 0, getWidth(), getHeight());

        for (Cell cell : getGrid()) {
            if (cell == current) {
                cell.draw(getGc(), Color.GREEN);
            } else if (cell.isVisitedSolved() && !cell.isPath()) {
                cell.draw(getGc(), Color.ORANGE);
            } else if (cell.isPath()) {
                cell.draw(getGc(), Color.PURPLE);
            }
        }

        for (Cell cell : getGrid()) {
            cell.draw(getGc());
        }
    }
}
