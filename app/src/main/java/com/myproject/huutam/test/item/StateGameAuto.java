package com.myproject.huutam.test.item;

/**
 * Created by huutam on 12/05/2017.
 */

public class StateGameAuto {
    private int [][] state;
    private Position positionEmpty;

    public StateGameAuto(int[][] state, Position positionEmpty) {
        this.state = state;
        this.positionEmpty = positionEmpty;
    }


    public int[][] getState() {

        return state;
    }

    public Position getPositionEmpty() {
        return positionEmpty;
    }
}
