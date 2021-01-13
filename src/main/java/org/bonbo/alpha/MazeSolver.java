package org.bonbo.alpha;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MazeSolver {
    public enum SolveStatus {
        WAITING, SOLVING, SOLVED
    }

    final public String SOLVE_METHOD_NOT_YET_SELECTED = "";
    final public String STAY_LEFT = "Stay Left";            //TODO make into enum or make separate classes for algorithms
    final public String BFS = "BFS";

    public ObservableList<String> solveOptions = FXCollections.observableArrayList(STAY_LEFT, BFS);

    public String currentSolveMethod = SOLVE_METHOD_NOT_YET_SELECTED;

    public SolveStatus solveStatus = SolveStatus.WAITING;

    //Values shared by algorithms
    public boolean[][] visited;
    public LinkedList<int[]> shortestPath = new LinkedList<>();
    public int[] currPos;
    public int[][][] prevNode;

    //Stay Left
    public int currSLDir;

    //BFS
    public Queue<int[]> bfsStack = new LinkedList<>();

    final private MazeClass maze;

    public MazeSolver(MazeClass _maze) {
        maze = _maze;
    }

    public void setCurrentSolveMethod() {
        Object solveMethod = maze.mainApp.solveComboBox.getValue();
        currentSolveMethod = solveMethod.toString();
    }

    public void startNewSolve() {
        if (maze.mazeGenerator.genStatus == MazeGenerator.GenStatus.IN_PROCESS) {
            maze.mainApp.setInformationText("Generation in Process!");
            return;
        }
        if (currentSolveMethod.equals(SOLVE_METHOD_NOT_YET_SELECTED)) {
            maze.mainApp.setInformationText("Select solve method!");
            return;
        }
        maze.mainApp.setInformationText("Solving...");
        currPos = maze.startingCoords;
        visited = new boolean[maze.height][maze.width];
        shortestPath.clear();
        solveStatus = SolveStatus.SOLVING;
        switch (currentSolveMethod) {
            case BFS -> initBFS();
            case STAY_LEFT -> currSLDir = 1;
        }


    }

    public void continueSolve() {
        if (solveStatus != SolveStatus.SOLVING || !maze.solvable) return;
        switch (currentSolveMethod) {
            case STAY_LEFT -> followLeft();
            case BFS -> bfs();
            default -> throw new IllegalStateException("Illegal solve state exception");
        }
    }

    public void followLeft() {
        if (solveStatus == SolveStatus.SOLVING) {
            shortestPath.add(currPos);
            int y = currPos[0], x = currPos[1];
            for (int rotation : new int[]{3, 0, 1, 2}) { //int rotation = 3; rotation >= 0; rotation--) {                      //preferentially go RELATIVELY left (-1 or +3) else forward (+0) else right (+1) else backward (+2)
                if (!maze.walls[y][x][(currSLDir + rotation) % 4]) {    //no wall is blocking move
                    currSLDir = (currSLDir + rotation) % 4;
                    currPos = new int[]{y + maze.deltas[currSLDir][0], x + maze.deltas[currSLDir][1]};
//                    System.out.println("moving in dir " + currDir + " from {y,x}: {" + y + ", " + x + "}    to {" + currPos[0] + ", " + currPos[1] + "}");
                    visited[y][x] = true;
                    break;
                }
            }
            if (Arrays.equals(currPos, maze.exitCoords)) {
                shortestPath.add(currPos);
                solveStatus = SolveStatus.SOLVED;
                maze.mainApp.setInformationText("Solved by stay left");
            }
        }
    }

    private void initBFS() {
        bfsStack = new LinkedList<>();
        bfsStack.add(maze.startingCoords);
        prevNode = new int[maze.height][maze.width][2];
    }

    public void bfs() {
        currPos = bfsStack.remove();
        if (Arrays.equals(currPos, maze.exitCoords)) {
            int[] backtrack = currPos;
            while (backtrack[0] != 0 || backtrack[1] != 0) {
                shortestPath.add(backtrack);
                backtrack = prevNode[backtrack[0]][backtrack[1]];
            }
            shortestPath.add(maze.startingCoords);
            maze.mainApp.setInformationText("BFS Solution");
            solveStatus = SolveStatus.SOLVED;
        } else {
            for (int[] newCoords : maze.getAccessibleNeighbors(currPos[0], currPos[1])) {
                if (!visited[newCoords[0]][newCoords[1]]) {
                    visited[newCoords[0]][newCoords[1]] = true;
                    prevNode[newCoords[0]][newCoords[1]] = currPos;
                    bfsStack.add(newCoords);
                }
            }
        }

    }

}

/*  BFS seemed suspiciously similar to dijkstra, it just wasn't made for maze solving I guess
    private int[][] tentDist;       //tentative distance


    public void initDijkstra() {
        maze.mainApp.setInformationText("INIT DIJKSTRA");
        prevNode = new int[maze.height][maze.width][2];
        tentDist = new int[maze.height][maze.width];
        for (int[] row : tentDist) Arrays.fill(row, Integer.MAX_VALUE);
        tentDist[maze.startingCoords[1]][maze.startingCoords[0]] = 0;
    }

    public void DISCONTINUED_dijkstra() {
        int minDist = Integer.MAX_VALUE;                        //find cell with min dist and not visited
        for (int i = 0; i < tentDist.length; i++) {
            for (int j = 0; j < tentDist[i].length; j++) {
                if (!visited[i][j] && tentDist[i][j] < minDist) {
                    minDist = tentDist[i][j];
                    currPos = new int[]{i, j};
                }
            }
        }
        //Update neighbor distance
        int y = currPos[0], x = currPos[1];
        for (int i = 0; i < maze.deltas.length; i++) {
            for (int dir = 0; dir < 4; dir++) {
                if (!maze.walls[y][x][dir]) {            //WILL BE PROBLEM IF START AND EXIT POINT HAVE OPEN WALLS
                    int nY = y + maze.deltas[dir][0], nX = x + maze.deltas[dir][1];
                    if (!visited[nY][nX]) {
                        if (tentDist[y][x] + 1 < tentDist[nY][nX]) {
                            tentDist[nY][nX] = tentDist[y][x] + 1;                        //distances between all nodes are 1
                            prevNode[nY][nX] = new int[]{y, x};
                        }
                    }
                }
            }
        }
        visited[y][x] = true;       //check if done
        if (maze.exitCoords[0] == y && maze.exitCoords[1] == x) {
            int[] backtrack = new int[]{y, x};
            while (backtrack[0] != 0 || backtrack[1] != 0) {
                shortestPath.add(backtrack);
                backtrack = prevNode[backtrack[0]][backtrack[1]];
            }
            shortestPath.add(maze.startingCoords);
            maze.mainApp.setInformationText("Dijkstra Solution");
            solveStatus = SolveStatus.SOLVED;
        }
    }

 */