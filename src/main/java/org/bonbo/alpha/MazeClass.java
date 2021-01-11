package org.bonbo.alpha;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Stack;

public class MazeClass {
    public enum Dir {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }


    public enum GenStatus{
        NOT_STARTED,
        IN_PROCESS,
        DONE
    }


    final public String DFS_REC = "dfs_rec";
    final public String DFS_ITER = "dfs_iter";
    final public String BLANK = "blank";
    final private Color ACTIVE_GEN_SQUARE_COLOR = Color.rgb(0, 200, 0);
    final private Color VISITED_SQUARE_COLOR = Color.hsb(0, 0.7, 1, 1);
    final private Color ACTIVE_SEARCH_SQUARE_COLOR = Color.hsb(0, 1, 0.6, 1);

    public int height;
    public int width;
    public boolean[][][] walls;    //[TOP, LEFT, BOTTOM, RIGHT], [0][0] is top left corner

    public Stack<int[]> iterativeStack = new Stack<>();
    private boolean[][] iterativeVisited = null;
    private int[] currentCell = null;

    private GenStatus genStatus = GenStatus.NOT_STARTED;

    public int[] startingCoords;
    public int[] exitCoords;

    public MazeSolve solver;
    public MainApp mainApp;


    public MazeClass(int yDimension, int xDimension, MazeSolve _solver, MainApp app) {
        height = yDimension;
        width = xDimension;
        solver = _solver;
        startingCoords = new int[]{0, 0};
        exitCoords = new int[]{yDimension - 1, xDimension - 1};
        mainApp = app;
    }


    public void generateMaze(String genType) {
        if (!iterativeStack.isEmpty()) {
            iterativeStack = new Stack<>();
            iterativeVisited = null;
            currentCell = null;

            if (genType.equals(DFS_ITER)) {
                mainApp.setInformationText("New Iteration");
            } else if (genType.equals(DFS_REC)) {
                mainApp.setInformationText("Canceling Iteration");        //useless for now
            }

        }
        solver.solveStatus = MazeSolve.SolveStatus.WAITING;
        boolean[][] visited = new boolean[height][width];
        walls = new boolean[height][width][4];          //would be more efficient to use inverted values, so initialization doesnt have to happen, but this is more intuitive
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int i = 0; i < 4; i++) {
                    walls[y][x][i] = true;
                }
            }
        }
        switch (genType) {
            case DFS_REC:
                dfsRecursiveGeneration(0, 0, visited);
                mainApp.setInformationText("Generated recursively");
                break;
            case DFS_ITER:
                startIterativeGeneration(0, 0);
                break;
            case BLANK:
                break;
            default:
                System.out.println("Invalid generation type");
        }
    }

    public void dfsRecursiveGeneration(int y, int x, boolean[][] visited) {
        visited[y][x] = true;
        int[][] possibles = {{y - 1, x, dir2idx(Dir.TOP)}, {y + 1, x, dir2idx(Dir.BOTTOM)}, {y, x - 1, dir2idx(Dir.LEFT)}, {y, x + 1, dir2idx(Dir.RIGHT)}};
        twoDimArrShuffle(possibles);
        for (int[] newCoords : possibles) {
            int newY = newCoords[0], newX = newCoords[1], wallToBreak = newCoords[2];
            if ((newY >= 0) && (newY < height) && (newX >= 0) && (newX < width) && (!visited[newY][newX])) {
                walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                walls[newY][newX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
                dfsRecursiveGeneration(newY, newX, visited);                  //dfs recursive
            }
        }
    }


    public void startIterativeGeneration(int startY, int startX) {
        mainApp.setInformationText("Iterating...");
        iterativeStack = new Stack<>();
        iterativeStack.push(new int[]{startY, startX});
        iterativeVisited = new boolean[height][width];
        iterativeVisited[startY][startX] = true;
        genStatus = GenStatus.IN_PROCESS;
    }

    public void iterativeGeneration() {
        if (!iterativeStack.isEmpty()) {
            int[] currCell = iterativeStack.pop();
            int y = currCell[0], x = currCell[1];
            iterativeVisited[y][x] = true;
            int[][] possibles = {{y - 1, x, dir2idx(Dir.TOP)}, {y + 1, x, dir2idx(Dir.BOTTOM)}, {y, x - 1, dir2idx(Dir.LEFT)}, {y, x + 1, dir2idx(Dir.RIGHT)}};
            twoDimArrShuffle(possibles);
            for (int[] newCoords : possibles) {
                int newY = newCoords[0], newX = newCoords[1], wallToBreak = newCoords[2];
                if ((newY >= 0) && (newY < height) && (newX >= 0) && (newX < width) && (!iterativeVisited[newY][newX])) {
                    walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                    walls[newY][newX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
                    iterativeStack.push(currCell);
                    iterativeStack.push(newCoords);
                    break;
                }
            }
            currentCell = currCell;
        } else if (genStatus == GenStatus.IN_PROCESS) {
            mainApp.setInformationText("Generated Iteratively");
            genStatus = GenStatus.DONE;
            currentCell = null;
        }
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
        if (!iterativeStack.isEmpty()) {
            gc.setFill(ACTIVE_GEN_SQUARE_COLOR);
            gc.fillRect(currentCell[1] * cellDim, currentCell[0] * cellDim, cellDim, cellDim);
        } else if (solver.solveStatus != MazeSolve.SolveStatus.WAITING) {
            gc.setFill(VISITED_SQUARE_COLOR);
            boolean[][] _pathVisited = solver.solutionVisited;
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

    private void twoDimArrShuffle(int[][] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));    //Random gen
            int[] temp = arr[index]; // Simple swap
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    public int dir2idx(Dir direction){
        return switch (direction) {
            case TOP -> 0;
            case RIGHT -> 1;
            case BOTTOM -> 2;
            case LEFT -> 3;
        };
    }
}
