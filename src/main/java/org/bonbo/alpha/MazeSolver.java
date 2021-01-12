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

    final public String STAY_LEFT = "Stay Left";            //TODO make into enum or make separate classes for algs
    final public String DIJKSTRA = "Dijkstra";

    public ObservableList<String> solveOptions = FXCollections.observableArrayList(STAY_LEFT, DIJKSTRA);

    public String currentSolveMethod;

    public SolveStatus solveStatus = SolveStatus.WAITING;

    //Values shared by algorithms
    public boolean[][] visited;
    public LinkedList<int[]> shortestPath = new LinkedList<>();

    //Stay Left
    public int[] currSLPos;
    public int currSLDir;

    //Dijkstra
    private int[][] tentDist;       //tentative distance
    public int[][][] prevNode;


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
        maze.mainApp.setInformationText("Solving...");
        currSLDir = 1;
        visited = new boolean[maze.height][maze.width];
        currSLPos = maze.startingCoords;
        solveStatus = SolveStatus.SOLVING;
        shortestPath.clear();
        if (Objects.equals(currentSolveMethod, DIJKSTRA)) initDijkstra();


    }

    public void continueSolve() {
        if (solveStatus == SolveStatus.SOLVING && maze.solveable) {
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
            shortestPath.add(currSLPos);
            int y = currSLPos[0], x = currSLPos[1];
            for (int rotation : new int[]{3, 0, 1, 2}) { //int rotation = 3; rotation >= 0; rotation--) {                      //preferentially go RELATIVELY left (-1 or +3) else forward (+0) else right (+1) else backward (+2)
                if (!maze.walls[y][x][(currSLDir + rotation) % 4]) {    //no wall is blocking move
                    currSLDir = (currSLDir + rotation) % 4;
                    currSLPos = new int[]{y + deltas[currSLDir][0], x + deltas[currSLDir][1]};
//                    System.out.println("moving in dir " + currDir + " from {y,x}: {" + y + ", " + x + "}    to {" + currPos[0] + ", " + currPos[1] + "}");
                    visited[y][x] = true;
                    break;
                }
            }
            if (Arrays.equals(currSLPos, maze.exitCoords)) {
                shortestPath.add(currSLPos);
                solveStatus = SolveStatus.SOLVED;
                maze.mainApp.setInformationText("Solved by stay left");
            }
        }
    }


    public void initDijkstra() {
        maze.mainApp.setInformationText("INIT DIJKSTRA");
        tentDist = new int[maze.height][maze.width];
        prevNode = new int[maze.height][maze.width][2];
        for (int[] row : tentDist) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        tentDist[maze.startingCoords[1]][maze.startingCoords[0]] = 0;
    }

    public void dijkstra() {
        int minDist = Integer.MAX_VALUE;                        //find cell with min dist and not visited
        for (int i = 0; i < tentDist.length; i++) {
            for (int j = 0; j < tentDist[i].length; j++) {
                if (!visited[i][j] && tentDist[i][j] < minDist) {
                    minDist = tentDist[i][j];
                    currSLPos = new int[]{i, j};
                }
            }
        }
        //Update neighbor distance
        int y = currSLPos[0], x = currSLPos[1];
        for (int i = 0; i < deltas.length; i++) {
            for (int dir = 0; dir < 4; dir++) {
                if (!maze.walls[y][x][dir]) {            //WILL BE PROBLEM IF START AND EXIT POINT HAVE OPEN WALLS
                    int nY = y + deltas[dir][0], nX = x + deltas[dir][1];
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
//            shortestPath.add(maze.exitCoords);
            int[] backtrack = new int[]{y,x};
            while (backtrack[0] != 0 || backtrack[1] != 0) {
                shortestPath.add(backtrack);
                backtrack = prevNode[backtrack[0]][backtrack[1]];
            }
            shortestPath.add(maze.startingCoords);
            maze.mainApp.setInformationText("Dijkstra Solution");
            solveStatus = SolveStatus.SOLVED;
        }
    }
}
