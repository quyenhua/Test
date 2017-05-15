package com.myproject.huutam.test;

import com.myproject.huutam.test.item.ImageSplit;
import com.myproject.huutam.test.item.Position;
import com.myproject.huutam.test.item.StateGameAuto;

import java.util.ArrayList;

/**
 * Created by huutam on 11/05/2017.
 */

public class GamePlayAuto {
    private ImageSplit[][] imageSplitsGame; //Save list ImageSplit of game board
    private int [][] goalState; //Save array int type with goal state
    private ArrayList<StateGameAuto> stateGameList; //Save list of game state from
    private int numOfRow; //Save the row of game board
    private int numOfCol; //Save the column of game board

    /* *
    * Name: GamePlayAuto
    * Type: Constructor function
    * Action: Initialize some important variable of class : imageSplitsGame,goalState,stateGameList
    * Input: imageSplitsGame,goalState,stateGameList
    * */
    public GamePlayAuto(ImageSplit[][] imageSplitsGame, int[][] goalState, ArrayList<StateGameAuto> stateGameList) {
        this.imageSplitsGame = imageSplitsGame;
        this.goalState = goalState;
        this.stateGameList = stateGameList;
        this.numOfRow = this.goalState.length;
        this.numOfCol = this.goalState[0].length;
    }

    /* *
     * Name: CheckEqualStateGame
     * Type: boolean
     * Action: Check 2 array with type int is equal
     * input: stateGame1,stateGame2
     * output: checkEqual
     * */
    private boolean CheckEqualStateGame(int [][]stateGame1, int [][]stateGame2){
        boolean checkEqual = true;
        if (stateGame1[this.numOfRow - 1][0] == stateGame2[this.numOfRow - 1][0]) {
            for (int i = 0; i < this.numOfRow; i++) {
                for (int j = 1; j < this.numOfCol; j++) {
                    if (stateGame1[i][j] != stateGame2[i][j]) {
                        checkEqual = false;
                        break;
                    }
                }
            }
        } else {
            checkEqual = false;
        }
        return checkEqual;
    }
    /*
    * Name: MoveImageSplit
    * Type: ArrayList<StateGameAuto>
    * Action: Move the imageSplit with value 0 to targetPosition by one step
    * input: stateGameAutoArrayList,targetPosition
    * output: stateGameAutoArrayList
    * */

    private ArrayList<StateGameAuto> MoveImageSplitOneStep(ArrayList<StateGameAuto> stateGameAutoArrayList,Position targetPosition){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        if((currentState.getPositionEmpty().getX() == targetPosition.getX() && Math.abs(currentState.getPositionEmpty().getY() - targetPosition.getY()) == 1) ||
                (currentState.getPositionEmpty().getY() == targetPosition.getY() && Math.abs(currentState.getPositionEmpty().getX() - targetPosition.getX()) == 1)){
            int [][]newStateInt = new int[this.numOfRow][this.numOfCol];
            int temp = 0;
            StateGameAuto nextState = null;
            boolean checkAvailable =true;
            for (int i = 0; i < this.numOfRow; i++) {
                for (int j = 1; j < this.numOfCol; j++) {
                    newStateInt[i][j] = currentState.getState()[i][j];
                }
            }
            newStateInt[this.numOfRow - 1][0] = currentState.getState()[this.numOfRow - 1][0];
            temp = newStateInt[currentState.getPositionEmpty().getX()][currentState.getPositionEmpty().getY()];
            newStateInt[currentState.getPositionEmpty().getX()][currentState.getPositionEmpty().getY()] =
                    newStateInt[targetPosition.getX()][targetPosition.getY()];
            newStateInt[targetPosition.getX()][targetPosition.getY()] = temp;
            for (int i = 0; i < stateGameAutoArrayList.size(); i++) {
                if (CheckEqualStateGame(stateGameAutoArrayList.get(i).getState(), newStateInt) == true) {
                    checkAvailable = false;
                    break;
                }
            }
            if (checkAvailable == true) {
                nextState = new StateGameAuto(newStateInt,
                        new Position(targetPosition.getX(),
                                targetPosition.getY()));
                stateGameAutoArrayList.add(nextState);
            }
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: SortEndRow
    * Type: ArrayList<StateGameAuto>
    * Action: Finish the last row of game board
    * Input: stateGameAutoArrayList
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> SortEndRow(ArrayList<StateGameAuto> stateGameAutoArrayList){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        while (currentState.getPositionEmpty().getY()>0){
            stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(currentState.getPositionEmpty().getX(),currentState.getPositionEmpty().getY() - 1));
            currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                    stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: CheckFinishSortRow
    * Type boolean
    * Action: check a row is right sort
    * Input: stateGameAutoArrayList,rowPositionSet
    * Output: checkFinishRow;
    * */
    private boolean CheckFinishSortRow(ArrayList<StateGameAuto> stateGameAutoArrayList, int rowPositionSet){
        boolean checkFinishRow = true;
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        for (int i=1;i<this.numOfCol;i++){
            if (currentState.getState()[rowPositionSet][i] != this.imageSplitsGame[rowPositionSet][i].realValue){
                checkFinishRow = false;
                break;
            }
        }
        return checkFinishRow;
    }

    /*
    * Name: FindNextPositionListSpecial
    * Type: ArrayList<Position>
    * Action: find the next position list , the main position is position of split with 0 value
    * Input: stateGameAutoArrayList, targetPosition, positionSet,positionDisableList
    * Output: direction
    * */
    private ArrayList<Position> FindNextPositionListSpecial(ArrayList<StateGameAuto> stateGameAutoArrayList,
                                                ArrayList<Position> positionDisableList,
                                                Position targetPosition,
                                                Position positionSet){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        ArrayList<Position> direction = new ArrayList<>();
        int distance = 100;
        // Move to left
        if((currentState.getPositionEmpty().getX() != this.numOfRow -1|| currentState.getPositionEmpty().getY() != 1) && (currentState.getPositionEmpty().getY() > 1)){
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX() && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()-1){
                    checkMove=false;
                    break;
                }
            }
            if(checkMove==true){
                distance = Math.abs(currentState.getPositionEmpty().getX() - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() - 1 - targetPosition.getY());
                direction.add(new Position(0, distance));
            }
        }
        // Move to top
        if (currentState.getPositionEmpty().getX() > positionSet.getX()) {
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX()-1 && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()){
                    checkMove=false;
                    break;
                }
            }
            if(checkMove==true) {
                distance = Math.abs(currentState.getPositionEmpty().getX() - 1 - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() - targetPosition.getY());
                direction.add(new Position(1, distance));
            }
        }
        // Move to right
        if (currentState.getPositionEmpty().getY() < numOfCol-1) {
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX() && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()+1){
                    checkMove=false;
                    break;
                }
            }
            if(checkMove==true) {
                distance = Math.abs(currentState.getPositionEmpty().getX() - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() + 1 - targetPosition.getY());
                direction.add(new Position(2, distance));
            }
        }
        if (currentState.getPositionEmpty().getX() < numOfRow-1) {
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX()+1 && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()){
                    checkMove=false;
                    break;
                }
            }
            if(checkMove==true){
                distance = Math.abs(currentState.getPositionEmpty().getX() + 1 - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() - targetPosition.getY());
                direction.add(new Position(3, distance));
            }
        }

        return direction;
    }

    /*
    * Name: FindNextPositionListNormal
    * Type: ArrayList<Position>
    * Action: find the next position list , the main position different position of split with 0 value
    * Input: stateGameAutoArrayList, targetPosition, mainPosition, positionSet,positionDisableList
    * Output: direction
    * */
    private ArrayList<Position> FindNextPositionListNormal(ArrayList<StateGameAuto> stateGameAutoArrayList,Position targetPosition,Position mainPosition,
                                           Position positionSet,ArrayList<Position> positionDisableList){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        ArrayList<Position> direction = new ArrayList<>();
        int distance = 100;
        // Move to left
        if((currentState.getPositionEmpty().getX() != this.numOfRow -1|| currentState.getPositionEmpty().getY() != 1) &&
                (currentState.getPositionEmpty().getY() > 1 || (currentState.getPositionEmpty().getY() == 1 && currentState.getPositionEmpty().getX() == this.numOfRow-1))
                && (mainPosition.getX() != currentState.getPositionEmpty().getX() || mainPosition.getY() != (currentState.getPositionEmpty().getY() - 1))){
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX() && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()-1){
                    checkMove=false;
                    break;
                }
                else if(positionDisableList.get(i).getY() == mainPosition.getY() - 1 && positionDisableList.get(i).getX() == mainPosition.getX()-1){
                    if(currentState.getPositionEmpty().getX() == mainPosition.getX()+1 &&
                            currentState.getPositionEmpty().getY() == mainPosition.getY()){
                        checkMove=false;
                        break;
                    }
                }
            }
            if(checkMove==true){
                distance = Math.abs(currentState.getPositionEmpty().getX() - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() - 1 - targetPosition.getY());
                if(mainPosition.getX() == currentState.getPositionEmpty().getX() - 1 && mainPosition.getY() == currentState.getPositionEmpty().getY() - 1){
                    distance +=1;
                }
                if(mainPosition.getY() == targetPosition.getY()-1 && mainPosition.getX() == targetPosition.getX()){
                    distance +=1;
                }
                direction.add(new Position(0, distance));
            }
        }
        // Move to top
        int rowNow = positionSet.getX();
        for (int i = 0; i<positionDisableList.size();i++){
            if(positionDisableList.get(i).getX() < rowNow){
                rowNow = positionDisableList.get(i).getX();
            }
        }
        if (currentState.getPositionEmpty().getX() > rowNow && ((currentState.getPositionEmpty().getX() - 1) != mainPosition.getX() || currentState.getPositionEmpty().getY() != mainPosition.getY())) {
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX()-1 && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()){
                    checkMove=false;
                    break;
                }
                else if(positionDisableList.get(i).getY() == mainPosition.getY() - 1 && positionDisableList.get(i).getX() == mainPosition.getX()-1){
                    if(currentState.getPositionEmpty().getX() == mainPosition.getX()+1 &&
                            currentState.getPositionEmpty().getY() == mainPosition.getY() - 1){
                        checkMove=false;
                        break;
                    }
                }
            }
            if(checkMove==true) {
                distance = Math.abs(currentState.getPositionEmpty().getX() - 1 - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() - targetPosition.getY());
                if(mainPosition.getX() == currentState.getPositionEmpty().getX() - 1 && mainPosition.getY() == currentState.getPositionEmpty().getY() + 1){
                    distance +=1;
                }
                if(mainPosition.getY() == targetPosition.getY()-1 && mainPosition.getX() == targetPosition.getX()){
                    distance +=2;
                }
                direction.add(new Position(1, distance));
            }
        }
        // Move to right
        if (currentState.getPositionEmpty().getY() < numOfCol-1 && (mainPosition.getX() != currentState.getPositionEmpty().getX() || mainPosition.getY() != (currentState.getPositionEmpty().getY() + 1))) {
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX() && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()+1){
                    checkMove=false;
                    break;
                }
                else if(positionDisableList.get(i).getY() == mainPosition.getY() + 1 && positionDisableList.get(i).getX() == mainPosition.getX()-1){
                    if(currentState.getPositionEmpty().getX() == mainPosition.getX()- 1 &&
                            currentState.getPositionEmpty().getY() == mainPosition.getY() - 1){
                        checkMove=false;
                        break;
                    }
                }
            }
            if(checkMove==true) {
                distance = Math.abs(currentState.getPositionEmpty().getX() - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() + 1 - targetPosition.getY());
                direction.add(new Position(2, distance));
            }
        }
        if (currentState.getPositionEmpty().getX() < numOfRow-1 && (currentState.getPositionEmpty().getX() + 1 != mainPosition.getX() || currentState.getPositionEmpty().getY() != mainPosition.getY())) {
            boolean checkMove = true;
            for (int i=0;i<positionDisableList.size();i++){
                if(positionDisableList.get(i).getX() == currentState.getPositionEmpty().getX()+1 && positionDisableList.get(i).getY() == currentState.getPositionEmpty().getY()){
                    checkMove=false;
                    break;
                }
            }
            if(checkMove==true){
                distance = Math.abs(currentState.getPositionEmpty().getX() + 1 - targetPosition.getX()) + Math.abs(currentState.getPositionEmpty().getY() - targetPosition.getY());
                direction.add(new Position(3, distance));
            }
        }

        return direction;
    }

    /**
     * Name: MoveToNextStep
     * Type: ArrayList<StateGameAuto>
     * Action: Move the empty split to next position
     * Input: stateGameAutoArrayList,direction
     * Output: stateGameAutoArrayList
     * */
    private ArrayList<StateGameAuto> MoveToNextStep(ArrayList<StateGameAuto> stateGameAutoArrayList, ArrayList<Position> direction){
        for (int i = 0; i < direction.size(); i++) {
            for (int j = i + 1; j < direction.size(); j++) {
                if (direction.get(i).getY() > direction.get(j).getY()) {
                    Position temp = new Position(direction.get(i).getX(),direction.get(i).getY());
                    direction.set(i,direction.get(j));
                    direction.set(j,temp);
                }
            }
        }
        int index = 0;
        while (index < direction.size()) {
            boolean checkAvailable = true;
            int[][] newStateInt = new int[this.numOfRow][this.numOfCol];
            int previousLengthList = 0;
            int temp = 0;
            switch (direction.get(index).getX()) {
                case 0:
                    previousLengthList = stateGameAutoArrayList.size();
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty().getX(),
                            stateGameAutoArrayList.get(stateGameAutoArrayList.size() -1).getPositionEmpty().getY() - 1));
                    if (stateGameAutoArrayList.size() != previousLengthList){
                        index = direction.size();
                    }
                    break;
                case 1:
                    previousLengthList = stateGameAutoArrayList.size();
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty().getX() - 1,
                            stateGameAutoArrayList.get(stateGameAutoArrayList.size() -1).getPositionEmpty().getY()));
                    if (stateGameAutoArrayList.size() != previousLengthList){
                        index = direction.size();
                    }
                    break;
                case 2:
                    previousLengthList = stateGameAutoArrayList.size();
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty().getX(),
                            stateGameAutoArrayList.get(stateGameAutoArrayList.size() -1).getPositionEmpty().getY() + 1));
                    if (stateGameAutoArrayList.size() != previousLengthList){
                        index = direction.size();
                    }
                    break;
                case 3:
                    previousLengthList = stateGameAutoArrayList.size();
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty().getX() + 1,
                            stateGameAutoArrayList.get(stateGameAutoArrayList.size() -1).getPositionEmpty().getY()));
                    if (stateGameAutoArrayList.size() != previousLengthList){
                        index = direction.size();
                    }
                    break;
                default:
                    break;
            }
            index = index+1;
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: MoveEmptyPointToTargetNormal
    * Type: ArrayList<StateGameAuto>
    * Action: move the empty split to target state
    * Input: stateGameAutoArrayList,targetPosition,nowPositionMainPoint,positionRealSplit, positionDisableList
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> MoveEmptyPointToTargetNormal(ArrayList<StateGameAuto> stateGameAutoArrayList, Position targetPosition,
                                                            Position nowPositionMainPoint, Position positionRealSplit,ArrayList<Position> positionDisableList){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        while (currentState.getPositionEmpty().getX() != targetPosition.getX() || currentState.getPositionEmpty().getY() != targetPosition.getY()){
            stateGameAutoArrayList = MoveToNextStep(
                    stateGameAutoArrayList, FindNextPositionListNormal(
                            stateGameAutoArrayList,targetPosition,nowPositionMainPoint,positionRealSplit,positionDisableList));
            currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                    stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: MoveEmptyPointToTargetSpecial
    * Type: ArrayList<StateGameAuto>
    * Action: move the empty split to target state
    * Input: stateGameAutoArrayList,targetPosition,nowPositionMainPoint,positionRealSplit, positionDisableList
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> MoveEmptyPointToTargetSpecial(ArrayList<StateGameAuto> stateGameAutoArrayList, Position targetPosition
                                                                  ,ArrayList<Position> positionDisableList){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        while (currentState.getPositionEmpty().getX() != targetPosition.getX() || currentState.getPositionEmpty().getY() != targetPosition.getY()){
            stateGameAutoArrayList = MoveToNextStep(
                    stateGameAutoArrayList, FindNextPositionListSpecial(
                            stateGameAutoArrayList,positionDisableList,targetPosition,new Position(positionDisableList.get(positionDisableList.size()-2).getX(),
                                    positionDisableList.get(positionDisableList.size()-2).getY())));
            currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                    stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: MoveToFinishTop
    * Type: ArrayList<StateGameAuto>
    * Action: move the split which is being set to the target position
    * Input: stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> MoveToFinishTop(ArrayList<StateGameAuto> stateGameAutoArrayList,
                                                     ArrayList<Position> positionDisableList,
                                                     Position positionRealSplit,
                                                     Position targetPosition){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        Position nowPositionMainPoint = new Position(0,0);
        int rowNow = positionRealSplit.getX();
        for (int i = 0; i<positionDisableList.size();i++){
            if(positionDisableList.get(i).getX() < rowNow){
                rowNow = positionDisableList.get(i).getX();
            }
        }
        for (int i = rowNow; i < this.numOfRow; i++) {
            for (int j = 1; j < this.numOfCol; j++) {
                if (currentState.getState()[i][j] == imageSplitsGame[positionRealSplit.getX()][positionRealSplit.getY()].realValue) {
                    nowPositionMainPoint.setX(i);
                    nowPositionMainPoint.setY(j);
                }
            }
        }
        if(nowPositionMainPoint.getX() < targetPosition.getX()){
            stateGameAutoArrayList = MoveToFinishBottom(stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition);
        }
        while (nowPositionMainPoint.getX() > targetPosition.getX()) {
            int moveTopPosition = nowPositionMainPoint.getX() - 1;
            Position positionMoveNext = new Position(moveTopPosition,nowPositionMainPoint.getY());
            for(int i=0;i<positionDisableList.size();i++){
                if(positionMoveNext.getX() == positionDisableList.get(i).getX() && positionMoveNext.getY() == positionDisableList.get(i).getY()){
                    stateGameAutoArrayList=MoveToFinishRight(stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition);
                    currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                            stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
                    for (int k = rowNow; k < this.numOfRow; k++) {
                        for (int t = 1; t < this.numOfCol; t++) {
                            if (currentState.getState()[k][t] == imageSplitsGame[positionRealSplit.getX()][positionRealSplit.getY()].realValue) {
                                nowPositionMainPoint.setX(k);
                                nowPositionMainPoint.setY(t);
                            }
                        }
                    }
                    break;
                }
            }
            moveTopPosition = nowPositionMainPoint.getX() - 1;
            positionMoveNext = new Position(moveTopPosition,nowPositionMainPoint.getY());
            stateGameAutoArrayList = MoveEmptyPointToTargetNormal(stateGameAutoArrayList,positionMoveNext,nowPositionMainPoint,positionRealSplit,positionDisableList);
            stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,nowPositionMainPoint);
            nowPositionMainPoint.setX(moveTopPosition);
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: MoveToFinishLeft
    * Type: ArrayList<StateGameAuto>
    * Action: move the split which is being set to the left target position
    * Input: stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> MoveToFinishLeft(ArrayList<StateGameAuto> stateGameAutoArrayList,
                                                     ArrayList<Position> positionDisableList,
                                                     Position positionRealSplit,
                                                     Position targetPosition){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        Position nowPositionMainPoint = new Position(0,0);
        int rowNow = positionRealSplit.getX();
        for (int i = 0; i<positionDisableList.size();i++){
            if(positionDisableList.get(i).getX() < rowNow){
                rowNow = positionDisableList.get(i).getX();
            }
        }
        for (int i = rowNow; i < this.numOfRow; i++) {
            for (int j = 1; j < this.numOfCol; j++) {
                if (currentState.getState()[i][j] == imageSplitsGame[positionRealSplit.getX()][positionRealSplit.getY()].realValue) {
                    nowPositionMainPoint.setX(i);
                    nowPositionMainPoint.setY(j);
                }
            }
        }
        if(nowPositionMainPoint.getY() < targetPosition.getY()){
            stateGameAutoArrayList = MoveToFinishRight(stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition);
        }
        while (nowPositionMainPoint.getY() > targetPosition.getY()) {
            int moveLeftPosition = nowPositionMainPoint.getY() - 1;
            Position positionMoveNext = new Position(nowPositionMainPoint.getX(),moveLeftPosition);
            stateGameAutoArrayList = MoveEmptyPointToTargetNormal(stateGameAutoArrayList,positionMoveNext,nowPositionMainPoint,positionRealSplit,positionDisableList);
            stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,nowPositionMainPoint);
            nowPositionMainPoint.setY(moveLeftPosition);
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: MoveToFinishBottom
    * Type: ArrayList<StateGameAuto>
    * Action: move the split which is being set to the bottom target position
    * Input: stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> MoveToFinishBottom(ArrayList<StateGameAuto> stateGameAutoArrayList,
                                                      ArrayList<Position> positionDisableList,
                                                      Position positionRealSplit,
                                                      Position targetPosition){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        Position nowPositionMainPoint = new Position(0,0);
        int rowNow = positionRealSplit.getX();
        for (int i = 0; i<positionDisableList.size();i++){
            if(positionDisableList.get(i).getX() < rowNow){
                rowNow = positionDisableList.get(i).getX();
            }
        }
        for (int i = rowNow; i < this.numOfRow; i++) {
            for (int j = 1; j < this.numOfCol; j++) {
                if (currentState.getState()[i][j] == imageSplitsGame[positionRealSplit.getX()][positionRealSplit.getY()].realValue) {
                    nowPositionMainPoint.setX(i);
                    nowPositionMainPoint.setY(j);
                }
            }
        }
        while (nowPositionMainPoint.getX() < targetPosition.getX()) {
            int moveBottomPosition = nowPositionMainPoint.getX() + 1;
            Position positionMoveNext = new Position(moveBottomPosition,nowPositionMainPoint.getY());
            stateGameAutoArrayList = MoveEmptyPointToTargetNormal(stateGameAutoArrayList,positionMoveNext,nowPositionMainPoint,positionRealSplit,positionDisableList);
            stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,nowPositionMainPoint);
            nowPositionMainPoint.setX(moveBottomPosition);
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: MoveToFinishRight
    * Type: ArrayList<StateGameAuto>
    * Action: move the split which is being set to the right target position
    * Input: stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> MoveToFinishRight(ArrayList<StateGameAuto> stateGameAutoArrayList,
                                                      ArrayList<Position> positionDisableList,
                                                      Position positionRealSplit,
                                                      Position targetPosition){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        Position nowPositionMainPoint = new Position(0,0);
        int rowNow = positionRealSplit.getX();
        for (int i = 0; i<positionDisableList.size();i++){
            if(positionDisableList.get(i).getX() < rowNow){
                rowNow = positionDisableList.get(i).getX();
            }
        }
        for (int i = rowNow; i < this.numOfRow; i++) {
            for (int j = 1; j < this.numOfCol; j++) {
                if (currentState.getState()[i][j] == imageSplitsGame[positionRealSplit.getX()][positionRealSplit.getY()].realValue) {
                    nowPositionMainPoint.setX(i);
                    nowPositionMainPoint.setY(j);
                }
            }
        }
        while (nowPositionMainPoint.getY() < targetPosition.getY()) {
            int moveRightPosition = nowPositionMainPoint.getY() + 1;
            Position positionMoveNext = new Position(nowPositionMainPoint.getX(),moveRightPosition);
            for(int i=0;i<positionDisableList.size();i++){
                if(positionMoveNext.getX() == positionDisableList.get(i).getX() && positionMoveNext.getY() == positionDisableList.get(i).getY()){
                    stateGameAutoArrayList=MoveToFinishBottom(stateGameAutoArrayList,positionDisableList,positionRealSplit,targetPosition);
                    currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                            stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
                    for (int k = rowNow; k < this.numOfRow; k++) {
                        for (int t = 1; t < this.numOfCol; t++) {
                            if (currentState.getState()[k][t] == imageSplitsGame[positionRealSplit.getX()][positionRealSplit.getY()].realValue) {
                                nowPositionMainPoint.setX(k);
                                nowPositionMainPoint.setY(t);
                            }
                        }
                    }
                    break;
                }
            }
            moveRightPosition = nowPositionMainPoint.getY() + 1;
            positionMoveNext = new Position(nowPositionMainPoint.getX(),moveRightPosition);
            stateGameAutoArrayList = MoveEmptyPointToTargetNormal(stateGameAutoArrayList,positionMoveNext,nowPositionMainPoint,positionRealSplit,positionDisableList);
            stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,nowPositionMainPoint);
            nowPositionMainPoint.setY(moveRightPosition);
        }
        return stateGameAutoArrayList;
    }

    /*
    * Name: EmbroidRow
    * Type: ArrayList<StateGameAuto>
    * Action: move the empty split to under positionDisableList(positionDisableList.size()-2);
    * Input: stateGameAutoArrayList,positionDisableList
    * Output: stateGameAutoArrayList
    * */
    private ArrayList<StateGameAuto> EmbroidRow(ArrayList<StateGameAuto> stateGameAutoArrayList, ArrayList<Position> positionDisableList){
        StateGameAuto currentState = new StateGameAuto(stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getState(),
                stateGameAutoArrayList.get(stateGameAutoArrayList.size() - 1).getPositionEmpty());
        Position targetPosition = new Position(positionDisableList.get(positionDisableList.size()-2).getX() + 1,
                positionDisableList.get(positionDisableList.size() - 2).getY());
        stateGameAutoArrayList = MoveEmptyPointToTargetSpecial(stateGameAutoArrayList,targetPosition,positionDisableList);
        return stateGameAutoArrayList;
    }

    /*
    * Name: GetRightStateGameList
    * Type: ArrayList<StateGameAuto>
    * Action: get right a list of state game
    * Output: stateGameList
    * */
    public ArrayList<StateGameAuto> GetRightStateGameList(){
        Position positionSet = new Position(0, 1);
        Position nowSplitSetPosition = new Position(0, 0);
        boolean checkEmbroid = false;
        ArrayList<Position> positionDisableLists = new ArrayList<>();
        int n = 0;
        while (this.numOfCol > 4) {
            this.stateGameList = stateGameListRepair(this.stateGameList);
            this.numOfCol -= 1;
        }

        for(int m=0;m < this.numOfRow -1;m++){
            boolean checkSort = false;
            positionDisableLists = new ArrayList<>();
            n=0;
            while (n<2){
                n++;
                if(this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX()][this.numOfCol-3] == imageSplitsGame[positionSet.getX()][this.numOfCol-2].realValue
                        && this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][this.numOfCol -3] == imageSplitsGame[positionSet.getX()][this.numOfCol-3].realValue){
                    positionDisableLists = new ArrayList<>();
                    positionDisableLists.add(new Position(positionSet.getX()+1,this.numOfCol-3));
                    positionDisableLists.add(new Position(positionSet.getX(),this.numOfCol-3));
                    positionSet.setY(positionSet.getY()+2);
                    n=2;
                    checkSort=true;
                }
                else if(this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX()][this.numOfCol-2] == imageSplitsGame[positionSet.getX()][this.numOfCol-2].realValue
                        && this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][this.numOfCol-3] == imageSplitsGame[positionSet.getX()][this.numOfCol-3].realValue
                        && this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX()][this.numOfCol-3] == imageSplitsGame[this.numOfRow - 1][0].realValue
                        && this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX()][this.numOfCol - 1] != imageSplitsGame[positionSet.getX()][this.numOfCol - 1].realValue){
                    positionDisableLists = new ArrayList<>();
                    positionDisableLists.add(new Position(positionSet.getX()+1,this.numOfCol-3));
                    this.stateGameList = MoveImageSplitOneStep(this.stateGameList,new Position(positionSet.getX(),positionDisableLists.get(positionDisableLists.size()-1).getY()));
                    positionDisableLists.add(new Position(positionSet.getX(),this.numOfCol-3));
                    positionSet.setY(positionSet.getY()+2);
                    checkSort = true;
                    n=2;
                }
                else {
                    if(positionSet.getY() == 2 && m==numOfRow-2){
                        if((this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][positionSet.getY() - 1] ==
                                imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue) ||
                                (this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][positionSet.getY()] ==
                                        imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue &&
                                        (this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][positionSet.getY() - 1] ==0 ||
                                                this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][positionSet.getY() - 2] ==0))) {
                            positionDisableLists = new ArrayList<>();
                            this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists, positionSet, new Position(positionSet.getX(), positionSet.getY() + 1)),
                                    positionDisableLists,positionSet, new Position(positionSet.getX(), positionSet.getY() + 1));
                            n = n -2;
                            positionSet.setY(positionSet.getY() - 1);
                        }
                        else {
                            this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists, positionSet, positionSet),positionDisableLists, positionSet, positionSet);
                            positionDisableLists.add(new Position(positionSet.getX(), positionSet.getY()));
                            positionSet.setY(positionSet.getY()+1);
                        }
                    }
                    else {
                        this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists, positionSet, positionSet),positionDisableLists, positionSet, positionSet);
                        positionDisableLists.add(new Position(positionSet.getX(), positionSet.getY()));
                        positionSet.setY(positionSet.getY()+1);
                    }
                }

                if (positionDisableLists.size() == 2) {
                    if(CheckFinishSortRow(this.stateGameList,positionSet.getX()) == false){
                        if(checkSort==false &&
                                (this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][positionSet.getY()]!= imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue ||
                                        this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX()][positionSet.getY()]!= 0)) {
                            this.stateGameList = EmbroidRow(this.stateGameList, positionDisableLists);
                            this.stateGameList = MoveImageSplitOneStep(this.stateGameList,new Position(positionSet.getX(),positionDisableLists.get(positionDisableLists.size()-2).getY()));
                            positionDisableLists = new ArrayList<>();
                            positionDisableLists.add(new Position(positionSet.getX()+1,this.numOfCol-3));
                            this.stateGameList = MoveImageSplitOneStep(this.stateGameList,new Position(positionSet.getX()
                                    ,positionDisableLists.get(positionDisableLists.size()-1).getY()));
                            positionDisableLists.add(new Position(positionSet.getX(),this.numOfCol-3));
                            if(positionSet.getX() == numOfRow -3){
                                if(imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue == stateGameList.get(stateGameList.size()-1).getState()[positionSet.getX()+2][positionSet.getY() - 2]
                                        ||(imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue == stateGameList.get(stateGameList.size()-1).getState()[positionSet.getX()+2][positionSet.getY() - 1]
                                        && (this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 2][positionSet.getY() - 2] ==0 ||
                                        this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 2][positionSet.getY() - 3] ==0))){
                                    Position savePositionDisable0 = new Position(positionDisableLists.get(positionDisableLists.size()-2).getX(),
                                            positionDisableLists.get(positionDisableLists.size()-2).getY());
                                    positionDisableLists.set(positionDisableLists.size()-2, new Position(0,0));
                                    this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,positionSet,positionSet),
                                            positionDisableLists,positionSet,positionSet);
                                    this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,new Position(savePositionDisable0.getX() -1,savePositionDisable0.getY()),new Position(savePositionDisable0.getX(),savePositionDisable0.getY())),
                                            positionDisableLists,new Position(savePositionDisable0.getX() -1,savePositionDisable0.getY()),new Position(savePositionDisable0.getX(),savePositionDisable0.getY()));
                                    positionDisableLists.set(positionDisableLists.size()-2, new Position(savePositionDisable0.getX(),savePositionDisable0.getY()));
                                }
                            }
                            this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,positionSet,positionSet),
                                    positionDisableLists,positionSet,positionSet);
                            positionDisableLists = new ArrayList<>();
                            this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,new Position(positionSet.getX(),positionSet.getY() -2),new Position(positionSet.getX(),positionSet.getY() -2)),
                                    positionDisableLists,new Position(positionSet.getX(),positionSet.getY() -2),new Position(positionSet.getX(),positionSet.getY() -2));
                        }
                        else if(checkSort == false && this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 1][positionSet.getY()]==
                                imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue && this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX()][positionSet.getY()]!= 0){
                            this.stateGameList = MoveImageSplitOneStep(this.stateGameList,new Position(positionSet.getX(),positionSet.getY()));
                        }
                        else {
                            if(positionSet.getX() == numOfRow -3){
                                if(imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue == stateGameList.get(stateGameList.size()-1).getState()[positionSet.getX()+2][positionSet.getY() - 2]
                                        ||(imageSplitsGame[positionSet.getX()][positionSet.getY()].realValue == stateGameList.get(stateGameList.size()-1).getState()[positionSet.getX()+2][positionSet.getY() - 1]
                                    && (this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 2][positionSet.getY() - 2] ==0 ||
                                        this.stateGameList.get(this.stateGameList.size()-1).getState()[positionSet.getX() + 2][positionSet.getY() - 3] ==0))){
                                    Position savePositionDisable0 = new Position(positionDisableLists.get(positionDisableLists.size()-2).getX(),
                                            positionDisableLists.get(positionDisableLists.size()-2).getY());
                                    positionDisableLists.set(positionDisableLists.size()-2, new Position(0,0));
                                    this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,positionSet,positionSet),
                                            positionDisableLists,positionSet,positionSet);
                                    this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,new Position(savePositionDisable0.getX() -1,savePositionDisable0.getY()),new Position(savePositionDisable0.getX(),savePositionDisable0.getY())),
                                            positionDisableLists,new Position(savePositionDisable0.getX() -1,savePositionDisable0.getY()),new Position(savePositionDisable0.getX(),savePositionDisable0.getY()));
                                    positionDisableLists.set(positionDisableLists.size()-2, new Position(savePositionDisable0.getX(),savePositionDisable0.getY()));
                                }
                            }
                            this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,positionSet,positionSet),
                                    positionDisableLists,positionSet,positionSet);
                            positionDisableLists = new ArrayList<>();
                            this.stateGameList = MoveToFinishLeft(MoveToFinishTop(this.stateGameList,positionDisableLists,new Position(positionSet.getX(),positionSet.getY() -2),new Position(positionSet.getX(),positionSet.getY() -2)),
                                    positionDisableLists,new Position(positionSet.getX(),positionSet.getY() -2),new Position(positionSet.getX(),positionSet.getY() -2));
                        }
                    }
                }
            }
            positionSet = new Position(positionSet.getX()+1,1);
        }
        this.stateGameList = SortEndRow(this.stateGameList);
        return this.stateGameList;
    }

    private ArrayList<StateGameAuto> stateGameListRepair(ArrayList<StateGameAuto> stateGameAutoArrayList){
        Position positionSet = new Position(0, numOfCol - 1);
        Position nowSplitSetPosition = new Position(0, 0);
        boolean checkEmbroid = false;
        ArrayList<Position> positionDisableLists = new ArrayList<>();
        int n = 0;
        boolean checkColSort = false;
        for (int i=0;i<1;i++){
            while (n<numOfRow - 1){
                if(positionDisableLists.size()==2){
                    if(stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[this.numOfRow -3][positionSet.getY()-1] == imageSplitsGame[this.numOfRow -3][positionSet.getY()].realValue
                            && this.stateGameList.get(this.stateGameList.size()-1).getState()[this.numOfRow -3][positionSet.getY()] == imageSplitsGame[this.numOfRow -2][positionSet.getY()].realValue) {
                        positionDisableLists.add(new Position(this.numOfRow - 3, positionSet.getY() - 1));
                        positionDisableLists.add(new Position(this.numOfRow - 3, positionSet.getY()));
                        positionSet.setX(positionSet.getX() + 2);
                        n = numOfRow - 1;
                        checkColSort = true;
                    }

                    else if(stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[this.numOfRow -3][positionSet.getY()-1] == imageSplitsGame[this.numOfRow -3][positionSet.getY()].realValue
                            && stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[this.numOfRow -2][positionSet.getY()] == imageSplitsGame[this.numOfRow -2][positionSet.getY()].realValue
                            && stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[this.numOfRow -3][positionSet.getY()] == 0) {
                        positionDisableLists.add(new Position(this.numOfRow - 3, positionSet.getY() - 1));
                        stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(numOfRow-2,positionSet.getY()));
                        positionDisableLists.add(new Position(this.numOfRow - 3, positionSet.getY()));
                        positionSet.setX(positionSet.getX() + 2);
                        n = numOfRow - 1;
                        checkColSort = true;
                    }
                    else{
                        stateGameAutoArrayList = MoveToFinishTop(MoveToFinishRight(stateGameAutoArrayList,positionDisableLists,positionSet,positionSet),
                                positionDisableLists,positionSet,positionSet);
                        positionDisableLists.add(new Position(positionSet.getX(),positionSet.getY()));
                    }
                }
                else {
                    stateGameAutoArrayList = MoveToFinishTop(MoveToFinishRight(stateGameAutoArrayList,positionDisableLists,positionSet,positionSet),
                        positionDisableLists,positionSet,positionSet);
                    positionDisableLists.add(new Position(positionSet.getX(),positionSet.getY()));
                }

                positionSet.setX(positionSet.getX() + 1);
                n++;
            }
            positionSet = new Position(numOfRow-1,numOfCol-1);

            if(CheckColGoal(stateGameAutoArrayList) == false){
                if(checkColSort == false && (stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[numOfRow-1][numOfCol-2] != imageSplitsGame[numOfRow-1][numOfCol-1].realValue
                        || stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[numOfRow-1][numOfCol-1] != 0)){
                    stateGameAutoArrayList = MoveToFinishTop(MoveToFinishRight(stateGameAutoArrayList,positionDisableLists,positionSet,new Position(positionSet.getX(),positionSet.getY()-1)),
                            positionDisableLists,positionSet,new Position(positionSet.getX(),positionSet.getY()-1));
                    stateGameAutoArrayList = MoveEmptyPointToTargetSpecial(stateGameAutoArrayList,new Position(positionSet.getX()-2,positionSet.getY()-1),positionDisableLists);
                    positionDisableLists.set(positionDisableLists.size()-2,new Position(0,0));
                    positionDisableLists.set(positionDisableLists.size()-1,new Position(0,0));
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(numOfRow-3,numOfCol-1));
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(numOfRow-2,numOfCol-1));
                    positionDisableLists.set(positionDisableLists.size()-2,new Position(numOfRow-3,numOfCol-2));
                    positionDisableLists.set(positionDisableLists.size()-1,new Position(numOfRow-3,numOfCol-1));
                    stateGameAutoArrayList = MoveToFinishTop(MoveToFinishRight(stateGameAutoArrayList,positionDisableLists,positionSet,positionSet),
                            positionDisableLists,positionSet,positionSet);
                    positionSet.setX(positionSet.getX()-2);
                    positionDisableLists.set(positionDisableLists.size()-2,new Position(0,0));
                    positionDisableLists.set(positionDisableLists.size()-1,new Position(0,0));
                    stateGameAutoArrayList = MoveToFinishTop(MoveToFinishRight(stateGameAutoArrayList,positionDisableLists,positionSet,positionSet),
                            positionDisableLists,positionSet,positionSet);
                }
                else if(checkColSort == false && stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[numOfRow-1][numOfCol-2] == imageSplitsGame[numOfRow-1][numOfCol-1].realValue
                        && stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[numOfRow-1][numOfCol-1] == 0){
                    stateGameAutoArrayList = MoveImageSplitOneStep(stateGameAutoArrayList,new Position(numOfRow-1,numOfCol-2));
                }
                else if(checkColSort == true){
                    Position now = new Position(0,0);
                    for (int a = 0;a<numOfRow;a++){
                        for (int b=1;b<numOfCol;b++){
                            if (imageSplitsGame[numOfRow-1][numOfCol-1].realValue == stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[a][b]){
                                now = new Position(a,b);
                                a= 5;
                                b=5;
                            }

                        }
                    }
                    if(now.getY()>numOfCol-3){
                        stateGameAutoArrayList = MoveToFinishLeft(stateGameAutoArrayList,positionDisableLists,positionSet,new Position(now.getX(),numOfCol-3));
                    }
                    stateGameAutoArrayList = MoveToFinishRight(MoveToFinishBottom(stateGameAutoArrayList,positionDisableLists,positionSet,positionSet),
                            positionDisableLists,positionSet,positionSet);
                    positionSet.setX(positionSet.getX()-2);
                    positionDisableLists.set(positionDisableLists.size()-2,new Position(0,0));
                    positionDisableLists.set(positionDisableLists.size()-1,new Position(0,0));
                    stateGameAutoArrayList = MoveToFinishTop(MoveToFinishRight(stateGameAutoArrayList,positionDisableLists,positionSet,positionSet),
                            positionDisableLists,positionSet,positionSet);
                }
            }

        }
        return stateGameAutoArrayList;
    }

    private boolean CheckColGoal(ArrayList<StateGameAuto> stateGameAutoArrayList){
        boolean check = true;
        for(int i=0;i<this.numOfRow;i++){
            if(stateGameAutoArrayList.get(stateGameAutoArrayList.size()-1).getState()[i][this.numOfCol-1] != imageSplitsGame[i][this.numOfCol-1].realValue){
                check=false;
                break;
            }
        }
        return check;
    }
}
