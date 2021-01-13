package org.bonbo.alpha;

import java.util.Arrays;

public class Cell {
    //TODO everything
    public boolean[] walls;
    public boolean visited;

    Cell() {
        walls = new boolean[]{true, true, true, true};
    }

    public void breakWall(MazeClass.Dir wallIdx){
        walls[MazeClass.dir2idx(wallIdx)] = false;
    }
}
