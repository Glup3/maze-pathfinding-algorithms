package org.bonbo.alpha;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class MazeSolver {
    public enum SolveStatus {
        WAITING, SOLVING, SOLVED
    }

    final private int[][] deltas = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    final public String STAY_LEFT = "Stay Left";
    final public String DIJKSTRA = "Dijkstra";

    public ObservableList<String> solveOptions = FXCollections.observableArrayList(STAY_LEFT, DIJKSTRA);

    public String currentSolveMethod;

    public SolveStatus solveStatus = SolveStatus.WAITING;

    public boolean[][] visited;
    public int[] currPos;
    public int currDir;

    //Dijkstra
    private int[][] tentativeDistance;
    public int[][][] prevNode;
    public LinkedList<int[]> shortestPath = new LinkedList<>();

    final private MazeClass maze;

    public MazeSolver(MazeClass _maze) {
        maze = _maze;
    }

    public void setCurrentSolveMethod() {
        Object solveMethod = maze.mainApp.solveComboBox.getValue();
        currentSolveMethod = solveMethod.toString();
    }

    public void startNewSolve() {
        maze.mainApp.setInformationText("Solving...");
        currDir = 1;
        visited = new boolean[maze.height][maze.width];
        currPos = maze.startingCoords;
        solveStatus = SolveStatus.SOLVING;
        shortestPath.clear();
        if (Objects.equals(currentSolveMethod, DIJKSTRA)) initDijkstra();


    }

    public void continueSolve() {
        if (solveStatus == SolveStatus.SOLVING) {
            if (Objects.equals(currentSolveMethod, STAY_LEFT)) {
                followLeft();//switch doesnt work, because apparently not constant (at compile time or something)
            } else if (Objects.equals(currentSolveMethod, DIJKSTRA)) {
                dijkstra();
            } else {
                solveStatus = SolveStatus.WAITING;     //currentSolveMethod is null (not chosen) or invalid
                maze.mainApp.setInformationText("Choose solve Method!");
            }
        }
    }

    public void followLeft() {
        if (solveStatus == SolveStatus.SOLVING) {
            shortestPath.add(currPos);
            int y = currPos[0], x = currPos[1];
            for (int rotation : new int[]{3, 0, 1, 2}) { //int rotation = 3; rotation >= 0; rotation--) {                      //preferentially go RELATIVELY left (-1 or +3) else forward (+0) else right (+1) else backward (+2)
                if (!maze.walls[y][x][(currDir + rotation) % 4]) {    //no wall is blocking move
                    currDir = (currDir + rotation) % 4;
                    currPos = new int[]{y + deltas[currDir][0], x + deltas[currDir][1]};
//                    System.out.println("moving in dir " + currDir + " from {y,x}: {" + y + ", " + x + "}    to {" + currPos[0] + ", " + currPos[1] + "}");
                    visited[y][x] = true;
                    break;
                }
            }
            if (Arrays.equals(currPos, maze.exitCoords)) {
                shortestPath.add(currPos);
                solveStatus = SolveStatus.SOLVED;
                maze.mainApp.setInformationText("Solved!");
            }
        }
    }


    public void initDijkstra() {
        maze.mainApp.setInformationText("INIT DIJKSTRA");
        tentativeDistance = new int[maze.height][maze.width];
        prevNode = new int[maze.height][maze.width][2];
        for (int[] row : tentativeDistance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        tentativeDistance[maze.startingCoords[1]][maze.startingCoords[0]] = 0;
    }

    public void dijkstra() {
        int minDist = Integer.MAX_VALUE;                        //find cell with min dist and not visited
        for (int i = 0; i < tentativeDistance.length; i++) {
            for (int j = 0; j < tentativeDistance[i].length; j++) {
                if (!visited[i][j] && tentativeDistance[i][j] < minDist) {
                    minDist = tentativeDistance[i][j];
                    currPos = new int[]{i, j};
                }
            }
        }
        //Update neighbor distance
        int y = currPos[0], x = currPos[1];
        for (int i = 0; i < deltas.length; i++) {
            for (int dir = 0; dir < 4; dir++) {
                if (!maze.walls[y][x][dir]) {            //WILL BE PROBLEM IF START AND EXIT POINT HAVE OPEN WALLS
                    int nY = y + deltas[dir][0], nX = x + deltas[dir][1];
                    if (!visited[nY][nX]) {
                        if (tentativeDistance[y][x] + 1 < tentativeDistance[nY][nX]) {
                            tentativeDistance[nY][nX] = tentativeDistance[y][x] + 1;                        //distances between all nodes are 1
                            prevNode[nY][nX] = new int[]{y, x};
                        }
                    }
                }
            }
        }
        visited[y][x] = true;       //check if done
        if (maze.exitCoords[0] == y && maze.exitCoords[1] == x) {
            shortestPath.add(maze.exitCoords);
            int[] backtrack = prevNode[y][x];
            while (backtrack[0] != 0 || backtrack[1] != 0) {
                shortestPath.add(backtrack);
                backtrack = prevNode[backtrack[0]][backtrack[1]];
            }
            shortestPath.add(maze.startingCoords);
            maze.mainApp.setInformationText("Dijkstra Solve");
            solveStatus = SolveStatus.SOLVED;
        }
    }
}
