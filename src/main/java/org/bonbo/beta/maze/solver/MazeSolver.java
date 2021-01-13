package org.bonbo.beta.maze.solver;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.bonbo.beta.dao.Cell;
import org.bonbo.beta.maze.Maze;
import org.bonbo.beta.util.AnimationTimerExt;

import java.util.ArrayList;

@Getter
@Setter
public abstract class MazeSolver extends Maze {

    private Cell source;

    private Cell target;

    private Cell current;

    private Cell vertex;

    private AnimationTimerExt timer;

    private boolean solved;

    private boolean foundTarget;

    //TODO use grid copy

    public MazeSolver(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc, Cell source, Cell target) {
        super(cellSize, height, width, grid, gc);

        this.source = source;
        this.target = target;
    }

    //TODO reset();

    public abstract void solve();

    abstract void nextStep();

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

        getSource().draw(getGc(), Color.YELLOW);
        getTarget().draw(getGc(), Color.RED);

        for (Cell cell : getGrid()) {
            cell.draw(getGc());
        }
    }
}
