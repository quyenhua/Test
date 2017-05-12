package com.myproject.huutam.test.item;

/**
 * Created by huutam on 04/05/2017.
 */

public class StateGame {
    public int [][] state;
    public int idState;
    public int parentState;
    public Position position;

    public StateGame(int [][] state, int idState, int parentState, Position position) {
        this.state = state;
        this.idState = idState;
        this.parentState = parentState;
        this.position = position;
    }

    public int [][] getState() {
        return state;
    }

    public void setState(int [][] state) {
        this.state = state;
    }

    public int getIdState() {
        return idState;
    }

    public void setIdState(int idState) {
        this.idState = idState;
    }

    public int getParentState() {
        return parentState;
    }

    public void setParentState(int parentState) {
        this.parentState = parentState;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
