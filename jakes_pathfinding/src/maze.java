import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Stack;

public class maze {
    private int height;
    private int width;
    private boolean[][][] walls;    //[TOP, LEFT, BOTTOM, RIGHT], [0][0] is top left corner
    final private int TOP = 0;
    final private int RIGHT = 1;
    final private int BOTTOM = 2;
    final private int LEFT = 3;

    final public static String DFS_REC = "dfs_rec";
    final public static String DFS_ITER = "dfs_iter";
    final public static String BLANK = "blank";

    final private static Color ACTIVE_SQUARE_COLOR = Color.rgb(0, 200, 0);

    private Stack<int[]> iterativeStack = new Stack<int[]>();
    private boolean[][] iterativeVisited = null;
    private int[] currentCell = null;

    public maze(int yDimension, int xDimension) {
        height = yDimension;
        width = xDimension;
    }

    public static void main(String[] args) {
    }


    public void generateMaze(String genType) {
        if (!iterativeStack.isEmpty()) {
            iterativeStack = new Stack<int[]>();
            iterativeVisited = null;
            currentCell = null;
            if (genType == DFS_ITER) {
                mainApp.informationText = "New Iteration";
            } else if (genType == DFS_REC) {
                mainApp.informationText = "Canceling Iteration";
            }
        }
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
                mainApp.informationText = "Generated recursively";
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
        int[][] possibles = {{y - 1, x, TOP}, {y + 1, x, BOTTOM}, {y, x - 1, LEFT}, {y, x + 1, RIGHT}};
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
//        if (iterativeStack.isEmpty()) {
        mainApp.informationText = "Iterating...";
        iterativeStack = new Stack<int[]>();
        iterativeStack.push(new int[]{startY, startX});
        iterativeVisited = new boolean[height][width];
        iterativeVisited[0][0] = true;

//        }
    }

    public void iterativeGeneration() {
        if (!iterativeStack.isEmpty()) {
            int[] currCell = iterativeStack.pop();
            int y = currCell[0], x = currCell[1];
            iterativeVisited[y][x] = true;
            int[][] possibles = {{y - 1, x, TOP}, {y + 1, x, BOTTOM}, {y, x - 1, LEFT}, {y, x + 1, RIGHT}};
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
        } else {
            if (currentCell!= null){
                mainApp.informationText = "Generated Iteratively";
            }
            currentCell = null;
        }
    }


    public void drawMaze(GraphicsContext gc) {
        gc.save();
        Canvas _canvas = gc.getCanvas();
        gc.setFill(Color.ROYALBLUE);
        gc.fillRect(0, 0, _canvas.getWidth(), _canvas.getHeight());
        gc.setStroke(Color.LIGHTGREY);
        double cellDim = Math.min(_canvas.getWidth(), _canvas.getHeight()) / Math.max(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double cX = x * cellDim, cY = y * cellDim;      //canvas x and y for drawing
                if (walls[y][x][TOP]) gc.strokeLine(cX, cY, cX + cellDim, cY);
                if (walls[y][x][RIGHT]) gc.strokeLine(cX + cellDim, cY, cX + cellDim, cY + cellDim);
                if (walls[y][x][BOTTOM]) gc.strokeLine(cX, cY + cellDim, cX + cellDim, cY + cellDim);
                if (walls[y][x][LEFT]) gc.strokeLine(cX, cY, cX, cY + cellDim);
            }
        }

        //Draw active square
        gc.setFill(ACTIVE_SQUARE_COLOR);
        if (!iterativeStack.isEmpty())
            gc.fillRect(currentCell[1] * cellDim, currentCell[0] * cellDim, cellDim, cellDim);

        gc.restore();
    }

    private static void twoDimArrShuffle(int[][] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            // Simple swap
            int[] temp = arr[index];
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }
}
