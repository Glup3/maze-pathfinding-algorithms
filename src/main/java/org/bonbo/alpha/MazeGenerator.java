package org.bonbo.alpha;

import java.util.ArrayList;
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

    public void genNewMaze(String genType) {
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
            case DFS_REC -> {
                dfsRecursiveGen(0, 0, visited);
                maze.mainApp.setInformationText("Generated recursively");
                maze.solvable = true;
            }
            case DFS_ITER -> initIterDFSgen(0, 0);
            case BLANK -> maze.solvable = false;
            default -> System.out.println("Invalid generation type");
        }
    }

    public void dfsRecursiveGen(int y, int x, boolean[][] visited) {
        visited[y][x] = true;
        ArrayList<int[]> neighbors = maze.getPossibleNeighbors(y, x);
        MazeClass.arrListShuffle2D(neighbors);
        for (int[] newCoords : neighbors) {
            int newY = newCoords[0], newX = newCoords[1], wallToBreak = newCoords[2];
            if (!visited[newY][newX]) {
                maze.walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                maze.walls[newY][newX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
                dfsRecursiveGen(newY, newX, visited);                  //dfs recursive
            }
        }
    }


    public void initIterDFSgen(int startY, int startX) {
        maze.mainApp.setInformationText("Iterating...");
        iterativeStack = new Stack<>();
        iterativeStack.push(new int[]{startY, startX});
        iterativeVisited = new boolean[maze.height][maze.width];
        iterativeVisited[startY][startX] = true;
        genStatus = GenStatus.IN_PROCESS;
        dfsIterativeGen();
    }

    public void dfsIterativeGen() {
        if (!iterativeStack.isEmpty()) {
            int[] currCell = iterativeStack.pop();
            int y = currCell[0], x = currCell[1];
            iterativeVisited[y][x] = true;
            ArrayList<int[]> neighbors = maze.getPossibleNeighbors(y, x);
            MazeClass.arrListShuffle2D(neighbors);
            for (int[] newCoords : neighbors) {
                int nY = newCoords[0], nX = newCoords[1], wallToBreak = newCoords[2];
                if (!iterativeVisited[nY][nX]) {
                    maze.walls[y][x][wallToBreak] = false;                                  //remove wall of current cell
                    maze.walls[nY][nX][(wallToBreak + 2) % 4] = false;                       //remove wall of neighbor
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
            maze.solvable = true;
        }
    }

    //TODO make more obv

}
