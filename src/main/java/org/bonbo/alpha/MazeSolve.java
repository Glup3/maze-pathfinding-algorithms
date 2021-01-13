package org.bonbo.alpha;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.Objects;

public class MazeSolve {
    final private int[][] deltas = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    final public String STAY_LEFT = "Stay Left";

    public ObservableList<String> solveOptions = FXCollections.observableArrayList(STAY_LEFT);

    public String currentSolveMethod;

    public enum SolveStatus {
        WAITING, STARTING, SOLVING, SOLVED;
    }

    public SolveStatus solveStatus = SolveStatus.WAITING;

    public boolean[][] solutionVisited;
    public int[] currPos;
    public int currDir;

    final private MainApp mainApp;

    public MazeSolve(MainApp app) {
        mainApp = app;
    }

    public void setCurrentSolveMethod(MazeSolve solver) {
        Object solveMethod = mainApp.solveComboBox.getValue();
        solver.currentSolveMethod = solveMethod.toString();
    }

    public void continueSolve(MazeClass maze) {
//        if (solveStatus
        if (solveStatus == SolveStatus.STARTING) {
            mainApp.setInformationText("Solving...");
            maze.solver.currDir = 1;
            maze.solver.solutionVisited = new boolean[maze.height][maze.width];
            maze.solver.currPos = maze.startingCoords;
            solveStatus = SolveStatus.SOLVING;
            return;
        }
        if (solveStatus == SolveStatus.SOLVING) {
            if (Objects.equals(maze.solver.currentSolveMethod, maze.solver.STAY_LEFT)) {
                maze.solver.followLeft(maze);//switch doesnt work, because apparently not constant (at compile time or something)
            } else {
                solveStatus = SolveStatus.WAITING;     //maze.solver.currentSolveMethod is null (not chosen) or invalid
                mainApp.setInformationText("Choose solve Method!");
            }
        }
    }

    public void followLeft(MazeClass maze) {        //TODO make dependant on selected input
        MazeSolve _solver = maze.solver;
        if (solveStatus == SolveStatus.SOLVING) {
            int y = _solver.currPos[0], x = _solver.currPos[1];
            for (int rotation : new int[]{3, 0, 1, 2}) { //int rotation = 3; rotation >= 0; rotation--) {                      //preferentially go RELATIVELY left (-1 or +3) else forward (+0) else right (+1) else backward (+2)
                if (!maze.walls[y][x][(_solver.currDir + rotation) % 4]) {    //no wall is blocking move
                    _solver.currDir = (_solver.currDir + rotation) % 4;
                    _solver.currPos = new int[]{y + deltas[_solver.currDir][0], x + deltas[_solver.currDir][1]};
//                    System.out.println("moving in dir " + _solver.currDir + " from {y,x}: {" + y + ", " + x + "}    to {" + currPos[0] + ", " + currPos[1] + "}");
                    _solver.solutionVisited[y][x] = true;
                    break;
                }
            }
            if (Arrays.equals(_solver.currPos, maze.exitCoords)) {
                _solver.solveStatus = SolveStatus.SOLVED;
                mainApp.setInformationText("Solved!");
            }
        }
    }
}