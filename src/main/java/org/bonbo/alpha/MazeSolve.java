package org.bonbo.alpha;

import java.util.Arrays;

public class MazeSolve {
    final private int[][] deltas = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};


    final public static int WAITING = 0;
    final public static int STARTING = 1;
    final public static int SOLVING = 2;
    final public static int SOLVED = 3;
    public int solveStatus = WAITING; //0

    public boolean[][] solutionVisited;
    public int[] currPos;
    public int currDir;


    public void followLeft(MazeClass maze) {
        MazeSolve _solver = maze.solver;
        if (solveStatus == STARTING) {
            _solver.currDir = 1;
            _solver.solutionVisited = new boolean[maze.height][maze.width];
            _solver.currPos = new int[]{0, 0};         //TODO add custom starting point
            solveStatus = SOLVING;
        } else if (solveStatus == SOLVING) {
            int y = _solver.currPos[0], x = _solver.currPos[1];
            for (int rotation : new int[] {3, 0, 1, 2}) { //int rotation = 3; rotation >= 0; rotation--) {                      //preferentially go RELATIVELY left (-1 or +3) else forward (+0) else right (+1) else backward (+2)
                if (!maze.walls[y][x][(_solver.currDir + rotation) % 4]) {    //no wall is blocking move
                    _solver.currDir = (_solver.currDir + rotation) % 4;
                    _solver.currPos = new int[]{y + deltas[_solver.currDir][0], x + deltas[_solver.currDir][1]};
//                    System.out.println("moving in dir " + _solver.currDir + " from {y,x}: {" + y + ", " + x + "}    to {" + currPos[0] + ", " + currPos[1] + "}");
                    _solver.solutionVisited[y][x] = true;
                    break;
                }
            }
            if (Arrays.equals(_solver.currPos, maze.exit)){
                _solver.solveStatus = SOLVED;
                MainApp.informationText = "Solved!";
            }
        }
    }
}
