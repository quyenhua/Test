package com.myproject.huutam.test;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myproject.huutam.test.dom.XMLDOMParser;
import com.myproject.huutam.test.item.ImageSplit;
import com.myproject.huutam.test.item.MissionItem;
import com.myproject.huutam.test.item.Position;
import com.myproject.huutam.test.item.StateGame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout screen;
    ImageView imgView;
    MediaPlayer song;
    ImageButton imgbtRefresh, imgbtAutoPlay, imgBackOpen, imgBackHome;
    TextView tvMission;
    Bitmap gameImage;
    private String pictureImagePath = "";
    XMLDOMParser parser = new XMLDOMParser();
    Bitmap bitmapList[][] = new  Bitmap[4][4];
    ImageView imgViewList[][] = new  ImageView[4][4];
    ImageSplit imgSplitList[][] = new ImageSplit[4][4];

    String fileName = "level";
    int level;
    MissionItem missionLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = (ConstraintLayout) findViewById(R.id.manhinh) ;
        imgView = (ImageView)findViewById(R.id.imageView);
        screen.setBackgroundResource(R.drawable.screen);
        imgbtRefresh =(ImageButton)findViewById(R.id.imgbtRefresh);
        imgbtAutoPlay=(ImageButton)findViewById(R.id.imgbtAuto);
        imgBackOpen = (ImageButton) findViewById(R.id.imgBackOpen);
        imgBackHome = (ImageButton) findViewById(R.id.imgBackHome);
        tvMission = (TextView) findViewById(R.id.tvMissionName);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            level = bundle.getInt("level");
            String filename = bundle.getString("path");
            try {
                FileInputStream stream = openFileInput(filename);
                gameImage = BitmapFactory.decodeStream(stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imgView.setImageBitmap(gameImage);
        }

        tvMission.setText("Mission " + level);

/*Khởi tạo imageSplitList*/
        for(int i=0;i<4;i++){
            for (int j=0;j<3;j++) {
                imgSplitList[i][j+1]=new ImageSplit();
            }
        }
        imgSplitList[3][0] = new ImageSplit();

        imgSplitList[0][1].imgViewSmall = (ImageView)findViewById(R.id.part1);
        imgSplitList[0][2].imgViewSmall = (ImageView)findViewById(R.id.part2);
        imgSplitList[0][3].imgViewSmall = (ImageView)findViewById(R.id.part3);
        imgSplitList[1][1].imgViewSmall = (ImageView)findViewById(R.id.part4);
        imgSplitList[1][2].imgViewSmall = (ImageView)findViewById(R.id.part5);
        imgSplitList[1][3].imgViewSmall = (ImageView)findViewById(R.id.part6);
        imgSplitList[2][1].imgViewSmall = (ImageView)findViewById(R.id.part7);
        imgSplitList[2][2].imgViewSmall = (ImageView)findViewById(R.id.part8);
        imgSplitList[2][3].imgViewSmall = (ImageView)findViewById(R.id.part9);
        imgSplitList[3][1].imgViewSmall = (ImageView)findViewById(R.id.part10);
        imgSplitList[3][2].imgViewSmall = (ImageView)findViewById(R.id.part11);
        imgSplitList[3][3].imgViewSmall = (ImageView)findViewById(R.id.part12);
        imgSplitList[3][0].imgViewSmall = (ImageView)findViewById(R.id.part0);

        int k=0;
        for(int i=0;i<4;i++){
            for (int j=0;j<3;j++){
               k++;
                imgSplitList[i][j+1].currentValue=k;
                imgSplitList[j][j+1].realValue=k;
            }
        }
        imgSplitList[3][0].currentValue=0;
        imgSplitList[3][0].realValue=0;

        splitImage();

//        song = MediaPlayer.create(MainActivity.this, R.raw.haytinanh);
//        song.setLooping(true);
//        song.start();

        setEvent();

    }





    private void setEvent() {
        imgbtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                embroilGame(20);
            }
        });

        imgbtAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int [][] startState = new int [4][4];
                final int [][] goalState = new int [4][4];
                final ArrayList<StateGame> stateGameList = new ArrayList<>();
                final int idAuto=0;
                startState[3][0]=imgSplitList[3][0].currentValue;
                goalState[3][0]=imgSplitList[3][0].realValue;
                StateGame startGame ;
                Position position = new Position(0,0);
                if(imgSplitList[3][0].currentValue==0){
                    position.X = 3;
                    position.Y =0;
                }
                for(int i=0;i<4;i++){
                    for (int j=0;j<3;j++) {
                        startState[i][j+1]=imgSplitList[i][j+1].currentValue;
                        if(imgSplitList[i][j+1].currentValue==0){
                            position.X=i;
                            position.Y=j+1;
                        }
                        goalState[i][j+1]=imgSplitList[i][j+1].realValue;
                    }
                }

                startGame = new StateGame(startState,idAuto,0,position);
                stateGameList.add(startGame);
                if(checkEqual(stateGameList.get(0).state,goalState)==false){
                    ArrayList<StateGame> rightPath = new ArrayList<>();
//                    rightPath = findPath(stateGameList,goalState);
//                    autoPlay(rightPath);
//                    findRightState(startGame, goalState);
                    rightPath = findListRightState(startGame, goalState);
                    autoPlay(rightPath);
                    complete();
                }

            }
        });

        imgBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Quit Game");
                alertDialog.setMessage("This mission aren't saved. Are you sure you want to quit game?");
                alertDialog.setPositiveButton("Yes", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intentHome = new Intent(MainActivity.this, MenuActivity.class);
                        finish();
                        startActivity(intentHome);
                    }});
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        imgBackOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Quit Game");
                alertDialog.setMessage("This mission aren't saved. Are you sure you want to quit game?");
                alertDialog.setPositiveButton("Yes", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intentReturn = new Intent(MainActivity.this, OpenImage.class);
                        intentReturn.putExtra("level", level);
                        finish();
                        startActivity(intentReturn);
                    }});
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
    }

    private void complete() {
//        completeLevel();
        ImageView imgStar1, imgStar2, imgStar3;
        ImageButton imgNext, imgBackMenu, imgReplay;
        Animation fadein;

        completeLevel();
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Complete Level");
        dialog.setContentView(R.layout.dialog_complete);
        imgStar1 = (ImageView) dialog.findViewById(R.id.imgStarOne);
        imgStar2 = (ImageView) dialog.findViewById(R.id.imgStarTwo);
        imgStar3 = (ImageView) dialog.findViewById(R.id.imgStarThree);
        imgNext = (ImageButton) dialog.findViewById(R.id.imgNextLevel);
        imgBackMenu = (ImageButton) dialog.findViewById(R.id.imgReturnMenu);
        imgReplay = (ImageButton) dialog.findViewById(R.id.imgReplay);

        fadein = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_face_in);
        imgStar1.startAnimation(fadein);
        imgStar2.startAnimation(fadein);
        imgStar3.startAnimation(fadein);
        dialog.show();

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OpenImage.class);
                intent.putExtra("level", level + 1);
                finish();
                startActivity(intent);
            }
        });

        imgBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMenu = new Intent(MainActivity.this, MenuActivity.class);
                finish();
                startActivity(intentMenu);
            }
        });

        imgReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                embroilGame(20);
                dialog.cancel();
            }
        });
    }

    private void completeLevel() {
        MissionItem missionItem = new MissionItem(level, true, R.drawable.threestar, R.drawable.mission);
        String fileCurrentLevel = parser.writeUsingNormalOperation(missionItem);
        writeLevel(fileName + level + ".xml", fileCurrentLevel);
        MissionItem nextMissionItem = new MissionItem(level + 1, true, R.drawable.nostar, R.drawable.mission);
        String fileNextLevel = parser.writeUsingNormalOperation(nextMissionItem);
        writeLevel(fileName + (level + 1) + ".xml", fileNextLevel);
    }

    public void writeLevel(String fileLevel, String level){

        try {
            FileOutputStream output = this.openFileOutput(fileLevel, MODE_PRIVATE);
            output.write(level.getBytes());
            output.close();
            //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error1:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error2:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onStop() {
//        if (song != null){
//            song.stop();
//            song.release();
//        }
//        super.onStop();
//    }
//
//    @Override
//    public void onPause() {
//        if (song != null && song.isPlaying()){
//            song.pause();
//        }
//        super.onPause();
//    }

//    @Override
//    public void onResume(){
//        if(song!=null && !song.isPlaying()) {
//            song.start();
//        }
//        super.onResume();
//    }

    public void onClickImageSplit(View v){
        ImageView image = (ImageView) v;
        int a =0;
        int b =0;

        for(int i=0;i<4;i++){
            for (int j=0;j<3;j++){
                if (imgSplitList[i][j+1].imgViewSmall == image){
                    a=i;
                    b=j+1;
                    i=10;
                    j=10;
                }
            }
        }
        //imgSplitList[0][1].imgViewSmall.setBackgroundDrawable(imgSplitList[1][1].imgViewSmall.getBackground());
        //imgSplitList[0][1].imgViewSmall.setImageBitmap(((BitmapDrawable)imgSplitList[2][1].imgViewSmall.getDrawable()).getBitmap());
        if(v == imgSplitList[3][0].imgViewSmall){
            if(imgSplitList[3][1].currentValue==0){
                changeImage(imgSplitList[3][0],imgSplitList[3][1]);
            }
        }

        else if(imgSplitList[a][b].currentValue!=0){
            if(b>1 || (b==1 && a==3)){
                if(imgSplitList[a][b-1].currentValue==0){
                    changeImage(imgSplitList[a][b-1],imgSplitList[a][b]);
                }
            }
            if(b<3){
                if(imgSplitList[a][b+1].currentValue==0){
                    changeImage(imgSplitList[a][b+1],imgSplitList[a][b]);
                }
            }
            if(a>0){
                if(imgSplitList[a-1][b].currentValue==0){
                    changeImage(imgSplitList[a-1][b],imgSplitList[a][b]);
                }
            }

            if(a<3){
                if(imgSplitList[a+1][b].currentValue==0){
                    changeImage(imgSplitList[a+1][b],imgSplitList[a][b]);
                }
            }

        }

    }

    private void changeImage(ImageSplit a, ImageSplit b){
        Bitmap bm_image = ((BitmapDrawable)a.imgViewSmall.getDrawable()).getBitmap();
        Drawable bm_background = a.imgViewSmall.getBackground();
        int x = a.currentValue;
        a.imgViewSmall.setImageBitmap(((BitmapDrawable)b.imgViewSmall.getDrawable()).getBitmap());
        b.imgViewSmall.setImageBitmap(bm_image);
        a.currentValue = b.currentValue;
        b.currentValue=x;
        a.imgViewSmall.setBackgroundDrawable(b.imgViewSmall.getBackground());
        b.imgViewSmall.setBackgroundDrawable(bm_background);
    }

    private void splitImage(){
        gameImage = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        int widthGameImage = gameImage.getWidth();
        int heightGameImage = gameImage.getHeight();
        for(int i=0;i<4;i++){
            for (int j=0;j<3;j++){
                bitmapList[i][j+1] = Bitmap.createBitmap(gameImage,j*widthGameImage/3,i*heightGameImage/4,widthGameImage/3,heightGameImage/4);
            }
        }

        int value = 0;
        for(int i=0;i<4;i++){
            for (int j=0;j<3;j++) {
                value++;
                imgSplitList[i][j+1].imgViewSmall.setImageBitmap(bitmapList[i][j+1]);
                imgSplitList[i][j+1].currentValue=value;
                imgSplitList[i][j+1].realValue=value;
            }
        }
        imgSplitList[3][0].imgViewSmall.setImageBitmap(null);
        imgSplitList[3][0].imgViewSmall.setBackgroundDrawable(null);
        imgSplitList[3][0].realValue=0;
        imgSplitList[3][0].currentValue=0;
    }

    private void embroilGame(int number){

        int position_X=3;
        int position_y=0;
        for(int i=0;i<4;i++){
            for (int j=0;j<3;j++){
                if (imgSplitList[i][j+1].currentValue == 0){
                   position_X=i;position_y=j+1;
                    i=10;
                    j=10;
                }
            }
        }

        int pre_X=0;
        int pre_Y=0;
        int num=0;
        ArrayList<Position> listNear;

        for(int k=0;k<number;k++){
            if(position_X==3 && position_y==0){
                changeImage(imgSplitList[3][0],imgSplitList[3][1]);
                pre_X=position_X;
                pre_Y=position_y;
                position_y=position_y+1;
            }
            else {
                listNear=new ArrayList<>();
                num=0;
                if(position_y>1 ||(position_y==1 && position_X==3)){
                    if(position_X != pre_X || (position_y-1) != pre_Y) {
                        listNear.add(new Position(position_X, position_y - 1));
                        num++;
                    }
                }
                if(position_X>0){
                    if((position_X-1) != pre_X || position_y != pre_Y) {
                        listNear.add(new Position(position_X - 1, position_y));
                        num++;
                    }
                }
                if(position_y<3){
                    if(position_X != pre_X || (position_y+1) != pre_Y) {
                        listNear.add(new Position(position_X, position_y + 1));
                        num++;
                    }
                }
                if(position_X<3){
                    if((position_X+1) != pre_X || position_y != pre_Y) {
                        listNear.add(new Position(position_X + 1, position_y));
                        num++;
                    }
                }

                Random rand = new Random();
                int local = rand.nextInt(num);
                changeImage(imgSplitList[position_X][position_y],imgSplitList[listNear.get(local).X][listNear.get(local).Y]);
                pre_X=position_X;
                pre_Y=position_y;
                position_X=listNear.get(local).X;
                position_y=listNear.get(local).Y;

            }
        }
    }

    private void autoPlay(ArrayList<StateGame> rightStateList){
        for(int i=0;i<rightStateList.size()-1;i++){
            changeImage(imgSplitList[rightStateList.get(i).position.X][rightStateList.get(i).position.Y],imgSplitList[rightStateList.get(i+1).position.X][rightStateList.get(i+1).position.Y]);
        }
    }

    private ArrayList<StateGame> findPath(ArrayList<StateGame> stateGames,int[][] goalGame){
        ArrayList<StateGame> rightStateList = new ArrayList<>();
        //rightStateList.add(stateGames.get(0));
        boolean checkNextState = true;
        int index=0;
        while (index<stateGames.size()){
            StateGame currentState= new StateGame(stateGames.get(index).state,stateGames.get(index).idState,
                    stateGames.get(index).parentState,stateGames.get(index).position);
            index++;
            if(currentState.position.X==3 && currentState.position.Y==0){
                boolean checkAvailable = true;
                int[][] newStateInt =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = currentState.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=currentState.state[3][0];

                int temp=newStateInt[3][0];
                newStateInt[3][0]= newStateInt[3][1];
                newStateInt[3][1]=temp;

                for(int i=0;i<stateGames.size();i++){
                    if(checkEqual(stateGames.get(i).state,newStateInt)== true){
                        checkAvailable=false;
                        break;
                    }
                }

                if(checkAvailable==true){
                    StateGame nextState = new StateGame(newStateInt,stateGames.get(stateGames.size()-1).idState+1,currentState.idState,new Position(3,1));
                    stateGames.add(nextState);

                    if(checkEqual(nextState.state,goalGame)){
                        rightStateList=getRightPath(stateGames);
                        Collections.reverse(rightStateList);
                        break;
                    }
                }

            }
            else {
                if (currentState.position.Y > 1 || (currentState.position.Y == 1 && currentState.position.X == 3)) {
                    boolean checkAvailable = true;
                    int[][] newStateInt =new int[4][4];
                    for(int i=0;i<4;i++){
                        for(int j=0;j<3;j++) {
                            newStateInt[i][j + 1] = currentState.state[i][j + 1];
                        }
                    }
                    newStateInt[3][0]=currentState.state[3][0];

                    int temp=newStateInt[currentState.position.X][currentState.position.Y];
                    newStateInt[currentState.position.X][currentState.position.Y]= newStateInt[currentState.position.X][currentState.position.Y-1];
                    newStateInt[currentState.position.X][currentState.position.Y-1]=temp;

                    for(int i=0;i<stateGames.size();i++){
                        if(checkEqual(stateGames.get(i).state,newStateInt)== true){
                            checkAvailable=false;
                            break;
                        }
                    }

                    if(checkAvailable==true){
                        StateGame nextState = new StateGame(newStateInt,stateGames.get(stateGames.size()-1).idState+1,currentState.idState,new Position(currentState.position.X,currentState.position.Y-1));
                        stateGames.add(nextState);
                        if(checkEqual(nextState.state,goalGame)){
                            rightStateList=getRightPath(stateGames);
                            Collections.reverse(rightStateList);
                            break;
                        }
                    }
                }
                if (currentState.position.X > 0) {
                    boolean checkAvailable = true;
                    int[][] newStateInt =new int[4][4];
                    for(int i=0;i<4;i++){
                        for(int j=0;j<3;j++) {
                            newStateInt[i][j + 1] = currentState.state[i][j + 1];
                        }
                    }
                    newStateInt[3][0]=currentState.state[3][0];

                    int temp=newStateInt[currentState.position.X][currentState.position.Y];
                    newStateInt[currentState.position.X][currentState.position.Y]= newStateInt[currentState.position.X-1][currentState.position.Y];
                    newStateInt[currentState.position.X-1][currentState.position.Y]=temp;

                    for(int i=0;i<stateGames.size();i++){
                        if(checkEqual(stateGames.get(i).state,newStateInt)== true){
                            checkAvailable=false;
                            break;
                        }
                    }

                    if(checkAvailable==true){
                        StateGame nextState = new StateGame(newStateInt,stateGames.get(stateGames.size()-1).idState+1,currentState.idState,new Position(currentState.position.X-1,currentState.position.Y));
                        stateGames.add(nextState);
                        if(checkEqual(nextState.state,goalGame)){
                            rightStateList=getRightPath(stateGames);
                            Collections.reverse(rightStateList);
                            break;
                        }
                    }

                }
                if (currentState.position.Y < 3) {
                    boolean checkAvailable = true;
                    int[][] newStateInt =new int[4][4];
                    for(int i=0;i<4;i++){
                        for(int j=0;j<3;j++) {
                            newStateInt[i][j + 1] = currentState.state[i][j + 1];
                        }
                    }
                    newStateInt[3][0]=currentState.state[3][0];

                    int temp=newStateInt[currentState.position.X][currentState.position.Y];
                    newStateInt[currentState.position.X][currentState.position.Y]= newStateInt[currentState.position.X][currentState.position.Y+1];
                    newStateInt[currentState.position.X][currentState.position.Y+1]=temp;

                    for(int i=0;i<stateGames.size();i++){
                        if(checkEqual(stateGames.get(i).state,newStateInt)== true){
                            checkAvailable=false;
                            break;
                        }
                    }

                    if(checkAvailable==true){
                        StateGame nextState = new StateGame(newStateInt,stateGames.get(stateGames.size()-1).idState+1,currentState.idState,new Position(currentState.position.X,currentState.position.Y+1));
                        stateGames.add(nextState);
                        if(checkEqual(nextState.state,goalGame)){
                            rightStateList=getRightPath(stateGames);
                            Collections.reverse(rightStateList);
                            break;
                        }
                    }
                }
                if (currentState.position.X < 3) {
                    boolean checkAvailable = true;
                    int[][] newStateInt =new int[4][4];
                    for(int i=0;i<4;i++){
                        for(int j=0;j<3;j++) {
                            newStateInt[i][j + 1] = currentState.state[i][j + 1];
                        }
                    }
                    newStateInt[3][0]=currentState.state[3][0];

                    int temp=newStateInt[currentState.position.X][currentState.position.Y];
                    newStateInt[currentState.position.X][currentState.position.Y]= newStateInt[currentState.position.X+1][currentState.position.Y];
                    newStateInt[currentState.position.X+1][currentState.position.Y]=temp;

                    for(int i=0;i<stateGames.size();i++){
                        if(checkEqual(stateGames.get(i).state,newStateInt)== true){
                            checkAvailable=false;
                            break;
                        }
                    }

                    if(checkAvailable==true){
                        StateGame nextState = new StateGame(newStateInt,stateGames.get(stateGames.size()-1).idState+1,currentState.idState,new Position(currentState.position.X+1,currentState.position.Y));
                        stateGames.add(nextState);
                        if(checkEqual(nextState.state,goalGame)){
                            rightStateList=getRightPath(stateGames);
                            Collections.reverse(rightStateList);
                            break;
                        }
                    }
                }
            }
        }
        return rightStateList;
    }

    private ArrayList<StateGame> findListRightState(StateGame startGame, int[][] goalGame){
        ArrayList<StateGame> listRightState = new ArrayList<>();
        listRightState.add(startGame);
        boolean checkgoal = false;
        while (checkgoal == false){
            StateGame currentState = listRightState.get(listRightState.size() - 1);
            StateGame rightState = findRightState(currentState, goalGame);
            listRightState.add(rightState);
            if(checkEqual(listRightState.get(listRightState.size() - 1).state, goalGame) == true){
                checkgoal = true;
            }
        }
        return listRightState;
    }

    private StateGame findRightState(StateGame stateGames, int [][] goalState){
        StateGame rightState = null;

        if(stateGames.position.X==3 && stateGames.position.Y==0){
            int[][] newStateInt =new int[4][4];
            for(int i=0;i<4;i++){
                for(int j=0;j<3;j++) {
                    newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                }
            }
            newStateInt[3][0]=stateGames.state[3][0];

            int temp=newStateInt[3][0];
            newStateInt[3][0]= newStateInt[3][1];
            newStateInt[3][1]=temp;

            rightState = new StateGame(newStateInt,stateGames.idState+1,stateGames.idState,new Position(3,1));
        }
        else{
            if(stateGames.position.X == 0 && stateGames.position.Y == 1){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];

                int temp=newStateInt[0][1];
                newStateInt[0][1]= newStateInt1[0][2];
                newStateInt[0][2]=temp;

                temp=newStateInt1[0][1];
                newStateInt1[0][1]= newStateInt1[1][1];
                newStateInt1[1][1]=temp;

                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                if(f1 < f2){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(0, 2));
                }
                else{
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(1, 1));
                }
            }
            else if(stateGames.position.X == 0 && stateGames.position.Y == 3){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];

                int temp=newStateInt[0][3];
                newStateInt[0][3]= newStateInt1[0][2];
                newStateInt[0][2]=temp;

                temp=newStateInt1[0][3];
                newStateInt1[0][3]= newStateInt1[1][3];
                newStateInt1[1][3]=temp;

                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                if(f1 < f2){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(0, 2));
                }
                else{
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(1, 3));
                }
            }
            else if(stateGames.position.X == 3 && stateGames.position.Y == 3){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];

                int temp=newStateInt[3][3];
                newStateInt[3][3]= newStateInt1[3][2];
                newStateInt[3][2]=temp;

                temp=newStateInt1[3][3];
                newStateInt1[3][3]= newStateInt1[2][3];
                newStateInt1[2][3]=temp;

                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                if(f1 < f2){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(3, 2));
                }
                else{
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(2, 3));
                }
            }
            else if (stateGames.position.Y == 2 && stateGames.position.X == 0){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                int[][] newStateInt2 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt2[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];
                newStateInt2[3][0]=stateGames.state[3][0];


                int temp=newStateInt[0][2];
                newStateInt[0][2]= newStateInt[0][1];
                newStateInt[0][1]=temp;

                temp=newStateInt1[0][2];
                newStateInt1[0][2]= newStateInt1[0][3];
                newStateInt1[0][3]=temp;

                temp=newStateInt2[0][2];
                newStateInt2[0][2]= newStateInt2[1][2];
                newStateInt2[1][2]=temp;
                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                int f3 = f(newStateInt2, goalState);
                if(min(f1, f2, f3) == f1){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(0, 1));
                }
                else if(min(f1, f2, f3) == f2){
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(0, 3));
                }
                else{
                    rightState = new StateGame(newStateInt2, stateGames.idState+1, stateGames.idState, new Position(1, 2));
                }
            }
            else if(stateGames.position.Y == 1 && stateGames.position.X < 3 && stateGames.position.X > 0){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                int[][] newStateInt2 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt2[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];
                newStateInt2[3][0]=stateGames.state[3][0];


                int temp=newStateInt[stateGames.position.X][1];
                newStateInt[stateGames.position.X][1]= newStateInt[stateGames.position.X - 1][1];
                newStateInt[stateGames.position.X - 1][1]=temp;

                temp=newStateInt1[stateGames.position.X][1];
                newStateInt1[stateGames.position.X][1]= newStateInt1[stateGames.position.X][2];
                newStateInt1[stateGames.position.X][2]=temp;

                temp=newStateInt2[stateGames.position.X][1];
                newStateInt2[stateGames.position.X][1]= newStateInt2[stateGames.position.X + 1][1];
                newStateInt2[stateGames.position.X + 1][1]=temp;
                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                int f3 = f(newStateInt2, goalState);
                if(min(f1, f2, f3) == f1){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X - 1, 1));
                }
                else if(min(f1, f2, f3) == f2){
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X, 2));
                }
                else{
                    rightState = new StateGame(newStateInt2, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X + 1, 1));
                }
            }
            else if(stateGames.position.Y < 3 && stateGames.position.Y > 0 && stateGames.position.X == 3){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                int[][] newStateInt2 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt2[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];
                newStateInt2[3][0]=stateGames.state[3][0];


                int temp=newStateInt[3][stateGames.position.Y];
                newStateInt[3][stateGames.position.Y]= newStateInt[3][stateGames.position.Y - 1];
                newStateInt[3][stateGames.position.Y -1]=temp;

                temp=newStateInt1[3][stateGames.position.Y];
                newStateInt1[3][stateGames.position.Y]= newStateInt1[2][stateGames.position.Y];
                newStateInt1[2][stateGames.position.Y]=temp;

                temp=newStateInt2[3][stateGames.position.Y];
                newStateInt2[3][stateGames.position.Y]= newStateInt2[3][stateGames.position.Y + 1];
                newStateInt2[3][stateGames.position.Y + 1]=temp;
                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                int f3 = f(newStateInt2, goalState);
                if(min(f1, f2, f3) == f1){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(3, stateGames.position.Y - 1));
                }
                else if(min(f1, f2, f3) == f2){
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(2, stateGames.position.Y));
                }
                else{
                    rightState = new StateGame(newStateInt2, stateGames.idState+1, stateGames.idState, new Position(3, stateGames.position.Y + 1));
                }
            }
            else if(stateGames.position.Y == 3 && stateGames.position.X < 3 && stateGames.position.X > 0){
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                int[][] newStateInt2 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt2[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];
                newStateInt2[3][0]=stateGames.state[3][0];


                int temp=newStateInt[stateGames.position.X][3];
                newStateInt[stateGames.position.X][3]= newStateInt[stateGames.position.X][2];
                newStateInt[stateGames.position.X][2]=temp;

                temp=newStateInt1[stateGames.position.X][3];
                newStateInt1[stateGames.position.X][3]= newStateInt1[stateGames.position.X - 1][3];
                newStateInt1[stateGames.position.X - 1][3]=temp;

                temp=newStateInt2[stateGames.position.X][3];
                newStateInt2[stateGames.position.X][3]= newStateInt2[stateGames.position.X + 1][3];
                newStateInt2[stateGames.position.X + 1][3]=temp;
                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                int f3 = f(newStateInt2, goalState);
                if(min(f1, f2, f3) == f1){
                    rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X, 2));
                }
                else if(min(f1, f2, f3) == f2){
                    rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X - 1, 3));
                }
                else{
                    rightState = new StateGame(newStateInt2, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X + 1, 3));
                }
            }
            else{
                int[][] newStateInt =new int[4][4];
                int[][] newStateInt1 =new int[4][4];
                int[][] newStateInt2 =new int[4][4];
                int[][] newStateInt3 =new int[4][4];
                for(int i=0;i<4;i++){
                    for(int j=0;j<3;j++) {
                        newStateInt[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt1[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt2[i][j + 1] = stateGames.state[i][j + 1];
                        newStateInt3[i][j + 1] = stateGames.state[i][j + 1];
                    }
                }
                newStateInt[3][0]=stateGames.state[3][0];
                newStateInt1[3][0]=stateGames.state[3][0];
                newStateInt2[3][0]=stateGames.state[3][0];
                newStateInt3[3][0]=stateGames.state[3][0];

                int temp=newStateInt[stateGames.position.X][stateGames.position.Y];
                newStateInt[stateGames.position.X][stateGames.position.Y]= newStateInt[stateGames.position.X][stateGames.position.Y - 1];
                newStateInt[stateGames.position.X][stateGames.position.Y-1]=temp;

                temp=newStateInt1[stateGames.position.X][stateGames.position.Y];
                newStateInt1[stateGames.position.X][stateGames.position.Y]= newStateInt1[stateGames.position.X - 1][stateGames.position.Y];
                newStateInt1[stateGames.position.X - 1][stateGames.position.Y]=temp;

                temp=newStateInt2[stateGames.position.X][stateGames.position.Y];
                newStateInt2[stateGames.position.X][stateGames.position.Y]= newStateInt2[stateGames.position.X][stateGames.position.Y + 1];
                newStateInt2[stateGames.position.X][stateGames.position.Y+1]=temp;

                temp=newStateInt3[stateGames.position.X][stateGames.position.Y];
                newStateInt3[stateGames.position.X][stateGames.position.Y]= newStateInt3[stateGames.position.X + 1][stateGames.position.Y];
                newStateInt3[stateGames.position.X + 1][stateGames.position.Y]=temp;

                int f1 = f(newStateInt, goalState);
                int f2 = f(newStateInt1, goalState);
                int f3 = f(newStateInt2, goalState);
                int f4 = f(newStateInt3, goalState);
                if(f4 < min(f1, f2, f3)){
                    rightState = new StateGame(newStateInt3, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X + 1, stateGames.position.Y));
                }
                else {
                    if(min(f1, f2, f3) == f1){
                        rightState = new StateGame(newStateInt, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X, stateGames.position.Y - 1));
                    }
                    else if(min(f1, f2, f3) == f2){
                        rightState = new StateGame(newStateInt1, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X - 1, stateGames.position.Y));
                    }
                    else{
                        rightState = new StateGame(newStateInt2, stateGames.idState+1, stateGames.idState, new Position(stateGames.position.X, stateGames.position.Y + 1));
                    }
                }
            }
        }
        return rightState;
    }

    private int min(int f1, int f2, int f3) {
        if(f1 < f2){
            if(f1 < f3) return f1;
            else return f3;
        }
        else{
            if(f2 < f3) return f2;
            else return f3;
        }
    }

    private int f(int[][] newStateInt, int[][] goalState) {
        int f=0;
        for(int x = 0; x < goalState.length; x++){
            for(int y = 1; y < goalState.length; y++){
                if(newStateInt[3][0] == goalState[x][y]){
                    f += abs(3 - x) + abs(0 - y);
                }
            }
        }
        for(int i = 0; i < newStateInt.length; i++){
            for(int j = 1; j < newStateInt.length; j++){
                if(newStateInt[i][j] == goalState[3][0]){
                    f += abs(i - 3) + abs(j - 0);
                }
                else{
                    for(int x = 0; x < goalState.length; x++){
                        for(int y = 1; y < goalState.length; y++){
                            if(newStateInt[i][j] == goalState[x][y]){
                                f += abs(i - x) + abs(j - y);
                            }
                        }
                    }
                }
            }
        }
        return f;
    }

    private boolean checkEqual(int [][] currentState, int [][] goalState){
        boolean check=true;
        if(currentState[3][0] == goalState[3][0]){
            for (int i=0;i<4;i++){
                for (int j=0;j<3;j++){
                    if(currentState[i][j+1] != goalState[i][j+1]) {
                        check = false;
                        break;
                    }
                }
            }
        }
        else {
            check=false;
        }
        return check;
    }

    private ArrayList<Integer> swap(ArrayList<Integer> arrayList){
        int temp=arrayList.get(0);
        arrayList.set(0,arrayList.get(1));
        arrayList.set(1,temp);
        return arrayList;
    }

    private ArrayList<StateGame> getRightPath(ArrayList<StateGame> stateList){
        ArrayList<StateGame> rightPath = new ArrayList<>();
        rightPath.add(stateList.get(stateList.size()-1));
        for(int i=stateList.size()-2;i>=0;i--){
            if(stateList.get(i).idState==rightPath.get(rightPath.size()-1).parentState){
                rightPath.add(stateList.get(i));
            }
        }
        return rightPath;
    }

    private ArrayList<StateGame> findPathShort(ArrayList<StateGame> stateGames,int[][] goalGame){
        ArrayList<StateGame> rightStateList = new ArrayList<>();

        return rightStateList;
    }
}

