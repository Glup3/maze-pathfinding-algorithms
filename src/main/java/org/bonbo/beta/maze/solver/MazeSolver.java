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

    private int sourcePos;

    private Cell target;

    private int targetPos;

    private Cell current;

    private Cell vertex;

    private AnimationTimerExt timer;

    private boolean solved;

    private boolean foundTarget;

    public MazeSolver(int cellSize, int height, int width, ArrayList<Cell> grid, GraphicsContext gc, int sourcePos, int targetPos) {
        super(cellSize, height, width, grid, gc);

        this.sourcePos = sourcePos;
        this.targetPos = targetPos;
    }

    public void reset() {
        for (Cell c : getGrid())  {
            c.setDistance(Integer.MAX_VALUE);
            c.setPrevious(null);
            c.setVisitedSolved(false);
            c.setPath(false);
        }
        this.source = getGrid().get(sourcePos);
        this.target = getGrid().get(targetPos);
        this.current = null;
        this.vertex = null;
        this.solved = false;
        this.foundTarget = false;
        this.timer = null;

        getGc().clearRect(0, 0, getWidth(), getHeight());
    }

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
