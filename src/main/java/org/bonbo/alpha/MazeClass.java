package org.bonbo.alpha;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class MazeClass {
    public enum Dir {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }

    final private Color ACTIVE_GEN_SQUARE_COLOR = Color.rgb(0, 200, 0);
    final private Color VISITED_SQUARE_COLOR = Color.hsb(0, 0.7, 1, 1);
    final private Color ACTIVE_SEARCH_SQUARE_COLOR = Color.hsb(0, 1, 0.6, 1);
    final private Color SHORTEST_PATH_COLOR = Color.hsb(300, 1, 0.5, 1);
//    final private Color START_SQUARE_COLOR = Color.hsb(200, 1, 1, 1);
//    final private Color EXIT_SQUARE_COLOR = Color.hsb(200, 1, 0.5, 1);

    public int height;
    public int width;
    public boolean[][][] walls;    //[TOP, LEFT, BOTTOM, RIGHT], [0][0] is top left corner

    public boolean solvable = false;

    final public int[][] deltas = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public int[] startingCoords;
    public int[] exitCoords;

    public MainApp mainApp;
    public MazeSolver solver;
    public MazeGenerator mazeGenerator;


    public MazeClass(int yDimension, int xDimension, MainApp _app) {
        height = yDimension;
        width = xDimension;
        startingCoords = new int[]{0, 0};
        exitCoords = new int[]{yDimension - 1, xDimension - 1};
        mainApp = _app;
    }


    public void drawMaze(GraphicsContext gc) {
        gc.save();
//        gc.setLineWidth(1);
        Canvas _canvas = gc.getCanvas();
        double cellDim = _canvas.getWidth() / width;
        //Background
        gc.setFill(Color.ROYALBLUE);
        gc.fillRect(0, 0, _canvas.getWidth(), _canvas.getHeight());

        //Draw filled squares for generating or solving
        if (mazeGenerator.genStatus == MazeGenerator.GenStatus.IN_PROCESS) {
            gc.setFill(ACTIVE_GEN_SQUARE_COLOR);
            gc.fillRect(mazeGenerator.currentCell[1] * cellDim, mazeGenerator.currentCell[0] * cellDim, cellDim, cellDim);
        } else if (solver.solveStatus != MazeSolver.SolveStatus.WAITING) {
            gc.setFill(VISITED_SQUARE_COLOR);
            boolean[][] _pathVisited = solver.visited;
            for (int y = 0; y < _pathVisited.length; y++) {
                for (int x = 0; x < _pathVisited[y].length; x++) {
                    if (_pathVisited[y][x]) gc.fillRect(x * cellDim - 1, y * cellDim - 1, cellDim + 1, cellDim + 1);
//                    if (_pathVisited[y][x]) gc.fillRect(x * cellDim-1, y * cellDim-1 , cellDim , cellDim );                   //drawing is inaccurate, choose which one looks the best
//                    if (_pathVisited[y][x]) gc.fillRect(x * cellDim, y * cellDim , cellDim , cellDim );
//                    if (_pathVisited[y][x]) gc.fillRect(x * cellDim - 1, y * cellDim - 1, cellDim + 2, cellDim + 2);

                }
            }
            gc.setFill(ACTIVE_SEARCH_SQUARE_COLOR);
            gc.fillRect(solver.currPos[1] * cellDim, solver.currPos[0] * cellDim, cellDim, cellDim);
        }

        //Draw shortest path
        if (solver.solveStatus == MazeSolver.SolveStatus.SOLVED && !solver.shortestPath.isEmpty()) {
            gc.setStroke(SHORTEST_PATH_COLOR);
            gc.setLineWidth(cellDim / 3);
            int[] prev = solver.shortestPath.getFirst();
            for (int[] coords : solver.shortestPath) {
                gc.strokeLine(coords[1] * cellDim + 0.5 * cellDim, coords[0] * cellDim + 0.5 * cellDim, prev[1] * cellDim + 0.5 * cellDim, prev[0] * cellDim + 0.5 * cellDim);
                prev = coords;
            }
        }
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        //Draw Start and end
        gc.setFill(SHORTEST_PATH_COLOR);
        gc.fillRect(startingCoords[1] * cellDim, startingCoords[0] * cellDim, cellDim, cellDim);
        gc.setFill(SHORTEST_PATH_COLOR);
        gc.fillRect(exitCoords[1] * cellDim, exitCoords[0] * cellDim, cellDim, cellDim);


        //Grid
        gc.setStroke(Color.LIGHTGREY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double cX = x * cellDim, cY = y * cellDim;      //canvas x and y for drawing
                if (walls[y][x][dir2idx(Dir.TOP)]) gc.strokeLine(cX, cY, cX + cellDim, cY);
                if (walls[y][x][dir2idx(Dir.RIGHT)]) gc.strokeLine(cX + cellDim, cY, cX + cellDim, cY + cellDim);
                if (walls[y][x][dir2idx(Dir.BOTTOM)]) gc.strokeLine(cX, cY + cellDim, cX + cellDim, cY + cellDim);
                if (walls[y][x][dir2idx(Dir.LEFT)]) gc.strokeLine(cX, cY, cX, cY + cellDim);
            }
        }

        gc.restore();
    }

    public ArrayList<int[]> getPossibleNeighbors(int y, int x) {
        ArrayList<int[]> neighbors = new ArrayList<>(Arrays.asList(new int[][]{{y - 1, x, dir2idx(MazeClass.Dir.TOP)},
                {y + 1, x, dir2idx(MazeClass.Dir.BOTTOM)},
                {y, x - 1, dir2idx(MazeClass.Dir.LEFT)},
                {y, x + 1, dir2idx(MazeClass.Dir.RIGHT)}}));

        for (int i = 0; i < neighbors.size(); i++) {
            int[] coords = neighbors.get(i);
            if (!((coords[0] >= 0) && (coords[0] < height) && (coords[1] >= 0) && (coords[1] < width)))
                neighbors.remove(i--);
        }
        return neighbors;
    }

    public ArrayList<int[]> getAccessibleNeighbors(int y, int x) {
        ArrayList<int[]> neighbors = new ArrayList<>();
        for(int dir = 0; dir < deltas.length;dir++) {
            if (!walls[y][x][dir]) {            //WILL BE PROBLEM IF START AND EXIT POINT HAVE OPEN WALLS
                int nY = y + deltas[dir][0], nX = x + deltas[dir][1];
                neighbors.add(new int[]{nY, nX});
            }
        }
        return neighbors;
    }


    public static void arrListShuffle2D(ArrayList<int[]> arr) {
        for (int i = arr.size() - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));    //Random gen
            int[] temp = arr.get(j); // Simple swap
            arr.set(j, arr.get(i));
            arr.set(i, temp);
        }
    }

    public static int dir2idx(Dir direction) {
        return switch (direction) {
            case TOP -> 0;
            case RIGHT -> 1;
            case BOTTOM -> 2;
            case LEFT -> 3;
        };
    }
}
