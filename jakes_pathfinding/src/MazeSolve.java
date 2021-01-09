public class MazeSolve {
    public static boolean[][] solutionVisited;

    public static void main(String[] args) {
    }

    public static int[][] currFLpos;
    public static int FL_orientation;
    public static void followLeft(MazeClass mazeObj) {
        solutionVisited = new boolean[mazeObj.height][mazeObj.width];
        int[][] deltas = new int[][] {{-1, 0},{0, 1},{1, 0},{0, -1}};
        //int y = currFLpos[0], x = currFLpos[1];    //from 0 to 3 in order UP, RIGHT, DOWN, LEFT

    }
}
