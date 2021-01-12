package org.bonbo.alpha;

import java.util.Arrays;
import java.util.Stack;

public class MazeGenerator {

    public enum GenStatus {
        NOT_STARTED,
        IN_PROCESS,
        DONE
    }

    private static MazeClass maze;

    final public String DFS_REC = "dfs_rec";
    final public String DFS_ITER = "dfs_iter";
    final public String BLANK = "blank";
    public Stack<int[]> iterativeStack = new Stack<>();
    public boolean[][] iterativeVisited = null;
    public int[] currentCell = null;

    public GenStatus genStatus = GenStatus.NOT_STARTED;

    public MazeGenerator(MazeClass _maze) {
        maze = _maze;
    }

    public void generateNewMaze(String genType) {
        genStatus = GenStatus.NOT_STARTED;

        maze.solver.solveStatus = MazeSolver.SolveStatus.WAITING;

        boolean[][] visited = new boolean[maze.height][maze.width];

        maze.walls = new boolean[maze.height][maze.width][4];          //would be more efficient to use inverted values, so initialization doesnt have to happen, but this is more intuitive
        for (int y = 0; y < maze.height; y++) {
            for (boolean[] row : maze.walls[y]) {
                Arrays.fill(row, true);
            }
        }
        switch (genType) {
            case DFS_REC:
                dfsRecursiveGeneration(0, 0, visited);
                maze.mainApp.setInformationText("Generated recursively");
                maze.solveable = true;
                break;
            case DFS_ITER:
                startIterativeGeneration(0, 0);
                break;
            case BLANK:
                maze.solveable = false;
                break;
            default:
                System.out.println("Invalid generation type");
        }
    }

    public void dfsRecursiveGeneration(int y, int x, boolean[][] visited) {
        visited[y][x] = true;
        int[][] possibles = genShuffledPossibles(y, x);
        for (int[] newCoords : possibles) {
            int newY = newCoords[0], newX = newCoords[1], wallToBreak = newCoords[2];
            if ((newY >= 0) && (newY < maze.height) && (newX >= 0) && (newX < maze.width) && (!visited[newY][newX])) {
                maze.walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                maze.walls[newY][newX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
                dfsRecursiveGeneration(newY, newX, visited);                  //dfs recursive
            }
        }
    }


    public void startIterativeGeneration(int startY, int startX) {
        maze.mainApp.setInformationText("Iterating...");
        iterativeStack = new Stack<>();
        iterativeStack.push(new int[]{startY, startX});
        iterativeVisited = new boolean[maze.height][maze.width];
        iterativeVisited[startY][startX] = true;
        genStatus = GenStatus.IN_PROCESS;
        iterativeGeneration();
    }

    public void iterativeGeneration() {
        if (!iterativeStack.isEmpty()) {
            int[] currCell = iterativeStack.pop();
            int y = currCell[0], x = currCell[1];
            iterativeVisited[y][x] = true;

            int[][] possibles = genShuffledPossibles(y, x);
            for (int[] newCoords : possibles) {
                int newY = newCoords[0], newX = newCoords[1], wallToBreak = newCoords[2];
                if ((newY >= 0) && (newY < maze.height) && (newX >= 0) && (newX < maze.width) && (!iterativeVisited[newY][newX])) {
                    maze.walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                    maze.walls[newY][newX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
                    iterativeStack.push(currCell);
                    iterativeStack.push(newCoords);
                    break;
                }
            }
            currentCell = currCell;
        } else if (genStatus == GenStatus.IN_PROCESS) {
            maze.mainApp.setInformationText("Generated Iteratively");
            genStatus = GenStatus.DONE;
            currentCell = null;
            maze.solveable = true;
        }
    }


    private void twoDimArrShuffle(int[][] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));    //Random gen
            int[] temp = arr[index]; // Simple swap
            arr[index] = arr[i];
            arr[i] = temp;
        }
    }

    private int[][] genShuffledPossibles(int y, int x) {
        int[][] possibles = new int[][]{{y - 1, x, dir2idx(MazeClass.Dir.TOP)},
                {y + 1, x, dir2idx(MazeClass.Dir.BOTTOM)},
                {y, x - 1, dir2idx(MazeClass.Dir.LEFT)},
                {y, x + 1, dir2idx(MazeClass.Dir.RIGHT)}};
        twoDimArrShuffle(possibles);
        return possibles;
    }

    private int dir2idx(MazeClass.Dir direction) {
        return maze.dir2idx(direction);
    }

}
