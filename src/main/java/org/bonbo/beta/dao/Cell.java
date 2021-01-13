package org.bonbo.beta.dao;

import org.bonbo.beta.controller.MazeSceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cell {

    @EqualsAndHashCode.Include
    private int x;

    @EqualsAndHashCode.Include
    private int y;

    private int size;

    private final boolean[] walls = { true, true, true, true };

    private boolean visited;

    private boolean visitedSolved;

    @Builder.Default
    private int distance = Integer.MAX_VALUE;

    private Cell previous;

    private boolean isPath;
    
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(2.0);
        gc.setStroke(Color.WHITE);

        if (walls[0]) { gc.strokeLine(x, y, x + size, y); }
        if (walls[1]) { gc.strokeLine(x + size, y, x + size, y + size); }
        if (walls[2]) { gc.strokeLine(x + size, y + size, x, y + size); }
        if (walls[3]) { gc.strokeLine(x, y + size, x, y); }

    }

    public void draw(GraphicsContext gc, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, size, size);
    }
}
