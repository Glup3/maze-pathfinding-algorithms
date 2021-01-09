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

    private Stack<Integer> iterativeStack = new Stack<Integer>();

    public maze(int dimy, int dimx) {
        height = dimy;
        width = dimx;
    }

    public static void main(String[] args) {
        System.out.println("dont call");
        maze test = new maze(4, 4);
        test.generateMaze();

    }


    public void generateMaze() {
        boolean[][] visited = new boolean[height][width];
        walls = new boolean[height][width][4];          //would be more efficient to use inverted values, so initialization doesnt have to happen, but this is more intuitive
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int i = 0; i < 4; i++) {
                    walls[y][x][i] = true;
                }
            }
        }
//        System.out.println();
//        System.out.println(Arrays.deepToString(walls));
        generateMaze(0, 0, visited);
//        System.out.println(Arrays.deepToString(walls));
//        System.out.println(Arrays.deepToString(visited));

    }

    public void generateMaze(int y, int x, boolean[][] visited) {
        visited[y][x] = true;
        int[][] possibles = {{y - 1, x, TOP}, {y + 1, x, BOTTOM}, {y, x - 1, LEFT}, {y, x + 1, RIGHT}};
        twoDimArrShuffle(possibles);
        for (int[] newCoords : possibles) {
            int newY = newCoords[0], newX = newCoords[1], wallToBreak = newCoords[2];
            if ((newY >= 0) && (newY < height) && (newX >= 0) && (newX < width) && (!visited[newY][newX])) {
                walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                walls[newY][newX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
                generateMaze(newY, newX, visited);                  //dfs recursive
            }
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
        gc.restore();
    }


    private static void iterativeGeneration(int y, int x){

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
