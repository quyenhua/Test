package com.myproject.huutam.test;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import com.myproject.huutam.test.item.SoundPlayer;
import com.myproject.huutam.test.item.StateGameAuto;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    //RelativeLayout screen;
    ImageView imgView;
    ImageButton imgbtRefresh;
    ImageButton imgbtAutoPlay;
    ImageButton imgBackOpen, imgBackHome;
    TextView tvMission, tvStep;
    ImageView touchedImage;
    MissionItem missionCurrent;
    SoundPlayer soundPlayer;

    GestureDetector gestureDetector;
    String fileName = "level";
    int level;
    XMLDOMParser parser = new XMLDOMParser();
    Bitmap gameImage;
    Bitmap bitmapList[][] = new Bitmap[4][4];
    ImageSplit imgSplitList[][] = new ImageSplit[4][4];
    boolean checkAutoPlaying = false;
    ArrayList<StateGameAuto> stateGameAutos = new ArrayList<>();
    int count = 0;
    int[][] saveStartState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //screen = (RelativeLayout) findViewById(R.id.manhinh);
        gestureDetector = new GestureDetector(this, new GestureListener());
        imgView = (ImageView) findViewById(R.id.imgView);
        soundPlayer = new SoundPlayer(this);
        //screen.setBackgroundResource(R.drawable.screen);
        imgbtRefresh = (ImageButton) findViewById(R.id.imgbtRefresh);
        imgbtAutoPlay = (ImageButton) findViewById(R.id.imgbtAuto);
        imgBackOpen = (ImageButton) findViewById(R.id.imgBackOpen);
        imgBackHome = (ImageButton) findViewById(R.id.imgBackHome);
        tvMission = (TextView) findViewById(R.id.tvMissionName);
        tvStep = (TextView) findViewById(R.id.tvStep);

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
        missionCurrent = readLevels(fileName + level + ".xml");


/*Khởi tạo imageSplitList*/
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                imgSplitList[i][j + 1] = new ImageSplit();
            }
        }
        imgSplitList[3][0] = new ImageSplit();

        imgSplitList[0][1].imgViewSmall = (ImageView) findViewById(R.id.part1);
        imgSplitList[0][2].imgViewSmall = (ImageView) findViewById(R.id.part2);
        imgSplitList[0][3].imgViewSmall = (ImageView) findViewById(R.id.part3);
        imgSplitList[1][1].imgViewSmall = (ImageView) findViewById(R.id.part4);
        imgSplitList[1][2].imgViewSmall = (ImageView) findViewById(R.id.part5);
        imgSplitList[1][3].imgViewSmall = (ImageView) findViewById(R.id.part6);
        imgSplitList[2][1].imgViewSmall = (ImageView) findViewById(R.id.part7);
        imgSplitList[2][2].imgViewSmall = (ImageView) findViewById(R.id.part8);
        imgSplitList[2][3].imgViewSmall = (ImageView) findViewById(R.id.part9);
        imgSplitList[3][1].imgViewSmall = (ImageView) findViewById(R.id.part10);
        imgSplitList[3][2].imgViewSmall = (ImageView) findViewById(R.id.part11);
        imgSplitList[3][3].imgViewSmall = (ImageView) findViewById(R.id.part12);
        imgSplitList[3][0].imgViewSmall = (ImageView) findViewById(R.id.part0);

        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                k++;
                imgSplitList[i][j + 1].currentValue = k;
                imgSplitList[j][j + 1].realValue = k;
                imgSplitList[i][j+1].imgViewSmall.setOnTouchListener(this);
            }
        }
        imgSplitList[3][0].currentValue = 0;
        imgSplitList[3][0].realValue = 0;
        imgSplitList[3][0].imgViewSmall.setOnTouchListener(this);

        splitImage();
        embroilGame(50*level);
        stateGameAutos = findListState();
        setEvent();
        tvStep.setText("Your step: 0/" + (stateGameAutos.size() - 1));
    }

    private ArrayList<StateGameAuto> findListState() {
        final int[][] startState = new int[4][4];
        final int[][] goalState = new int[4][4];
        final ArrayList<StateGameAuto> stateGameList = new ArrayList<>();

        startState[3][0] = imgSplitList[3][0].currentValue;
        goalState[3][0] = imgSplitList[3][0].realValue;
        StateGameAuto startGame;
        Position position = new Position(0, 0);
        if (imgSplitList[3][0].currentValue == 0) {
            position.setX(3);
            position.setY(0);
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                startState[i][j + 1] = imgSplitList[i][j + 1].currentValue;
                if (imgSplitList[i][j + 1].currentValue == 0) {
                    position.setX(i);
                    position.setY(j + 1);
                }
                goalState[i][j + 1] = imgSplitList[i][j + 1].realValue;
            }
        }

        saveStartState = startState;

        startGame = new StateGameAuto(startState, position);
        stateGameList.add(startGame);
        GamePlayAuto autoPlayVar = new GamePlayAuto(imgSplitList, goalState, stateGameList);
        return autoPlayVar.GetRightStateGameList();
    }

    private void setEvent() {
        imgbtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAutoPlaying == false){
                    embroilGame(50*level);
                    stateGameAutos.removeAll(stateGameAutos);
                    stateGameAutos = findListState();
                    count = 0;
                    tvStep.setText("Your step: " + count + "/" + (stateGameAutos.size() - 1));
                }
            }
        });

        imgbtAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAutoPlaying == false) {
                    checkAutoPlaying = true;
                    stateGameAutos.removeAll(stateGameAutos);
                    stateGameAutos = findListState();
                    count = 0;
                    tvStep.setText("Your step: " + count + "/" + (stateGameAutos.size() - 1));
                    autoPlay(stateGameAutos);
                }
            }
        });

        imgBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAutoPlaying == false){
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
            }
        });

        imgBackOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAutoPlaying == false){
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
            }
        });
    }

    private void autoComlete() {

        TextView tvAutoComplete, tvOk;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Autoplay Complete");
        dialog.setContentView(R.layout.dialog_autocomplete);

        tvAutoComplete = (TextView) dialog.findViewById(R.id.tvAutoComplete);
        tvOk = (TextView) dialog.findViewById(R.id.tvOk);

        tvAutoComplete.setText("Auto Play complete!");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                embroilGame(50*level);
                checkAutoPlaying = false;
                stateGameAutos.removeAll(stateGameAutos);
                stateGameAutos = findListState();
                count = 0;
                tvStep.setText("Your step: " + count + "/" + (stateGameAutos.size() - 1));
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void complete() {
//        completeLevel();
        ImageView imgStar1, imgStar2, imgStar3;
        ImageButton imgNext, imgBackMenu, imgReplay;
        Animation fadein;

        MissionItem missionItem = completeLevel();
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setTitle("Complete Level");
        dialog.setContentView(R.layout.dialog_complete);
        imgStar1 = (ImageView) dialog.findViewById(R.id.imgStarOne);
        imgStar2 = (ImageView) dialog.findViewById(R.id.imgStarTwo);
        imgStar3 = (ImageView) dialog.findViewById(R.id.imgStarThree);
        fadein = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_face_in);

        switch (missionItem.getImageStar()){
            case R.drawable.threestar:
                imgStar3.setVisibility(View.VISIBLE);
                imgStar3.startAnimation(fadein);
            case R.drawable.twostar:
                imgStar2.setVisibility(View.VISIBLE);
                imgStar2.startAnimation(fadein);
            case R.drawable.onestar:
                imgStar1.setVisibility(View.VISIBLE);
                imgStar1.startAnimation(fadein);
        }
        imgNext = (ImageButton) dialog.findViewById(R.id.imgNextLevel);
        imgBackMenu = (ImageButton) dialog.findViewById(R.id.imgReturnMenu);
        imgReplay = (ImageButton) dialog.findViewById(R.id.imgReplay);

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
                embroilGame(50*level);
                stateGameAutos.removeAll(stateGameAutos);
                stateGameAutos = findListState();
                dialog.cancel();
            }
        });
    }

    private MissionItem completeLevel() {
        MissionItem missionItem;
        if(count - stateGameAutos.size() <= 5) {
            missionItem = new MissionItem(level, true, R.drawable.threestar, R.drawable.mission, count);
        }
        else if(count - stateGameAutos.size() <= 20){
            missionItem = new MissionItem(level, true, R.drawable.twostar, R.drawable.mission, count);
        }
        else{
            missionItem = new MissionItem(level, true, R.drawable.onestar, R.drawable.mission, count);
        }
        MissionItem itemReturn = missionItem;
        if(missionCurrent.getImageStar() == R.drawable.nostar) {
            String fileCurrentLevel = parser.writeUsingNormalOperation(missionItem);
            writeLevel(fileName + level + ".xml", fileCurrentLevel);
            MissionItem nextMissionItem = new MissionItem(level + 1, true, R.drawable.nostar, R.drawable.mission);
            String fileNextLevel = parser.writeUsingNormalOperation(nextMissionItem);
            writeLevel(fileName + (level + 1) + ".xml", fileNextLevel);
        }
        else{
            if(missionCurrent.getImageStar() == R.drawable.twostar){
                if(missionItem.getImageStar() == R.drawable.onestar){
                    missionItem.setImageStar(missionCurrent.getImageStar());
                    missionItem.setBestStep(stateGameAutos.size());
                }
            }
            else if(missionCurrent.getImageStar() == R.drawable.threestar){
                missionItem.setImageStar(missionCurrent.getImageStar());
                missionItem.setBestStep(stateGameAutos.size());
            }
            String fileCurrentLevel = parser.writeUsingNormalOperation(missionItem);
            writeLevel(fileName + level + ".xml", fileCurrentLevel);
        }
        return itemReturn;
    }

    private MissionItem readLevels(String fileLevel) {
        String currentLevel = readData(fileLevel);
        Document doc = parser.getDocument(currentLevel);
        NodeList node = doc.getElementsByTagName("level");
        Element element = (Element) node.item(0);
        int level = Integer.parseInt(element.getAttribute("id"));
        String lock = parser.getValue(element, "lock");
        Boolean lock_bool;
        if(lock.equals("true"))
            lock_bool = true;
        else lock_bool = false;
        int star = Integer.parseInt(parser.getValue(element, "star"));
        int background = Integer.parseInt(parser.getValue(element, "background"));
        int beststep = Integer.parseInt(parser.getValue(element, "beststep"));

        return new MissionItem(level, lock_bool, star, background, beststep);
    }

    private String readData(String fileLevel) {
        StringBuilder builder = new StringBuilder();
        String content = "";
        try {
            FileInputStream input = this.openFileInput(fileLevel);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
            while ((content = buffer.readLine()) != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    builder.append(content).append('\n');
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error1:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error2:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return builder.toString();
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

    private boolean checkGoal() {
        for(int i = 0; i < 4; i++){
            for (int j = 0; j < 3; j++){
                if(imgSplitList[i][j + 1].currentValue != imgSplitList[i][j + 1].realValue){
                    return false;
                }
            }
        }
        return true;
    }

    private void changeImage(ImageSplit a, ImageSplit b) {
        Bitmap bm_image = ((BitmapDrawable) a.imgViewSmall.getDrawable()).getBitmap();
        Drawable bm_background = a.imgViewSmall.getBackground();
        int x = a.currentValue;
        a.imgViewSmall.setImageBitmap(((BitmapDrawable) b.imgViewSmall.getDrawable()).getBitmap());
        b.imgViewSmall.setImageBitmap(bm_image);
        a.currentValue = b.currentValue;
        b.currentValue = x;
        a.imgViewSmall.setBackgroundDrawable(b.imgViewSmall.getBackground());
        b.imgViewSmall.setBackgroundDrawable(bm_background);
    }

    private void splitImage() {
        gameImage = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
        int widthGameImage = gameImage.getWidth();
        int heightGameImage = gameImage.getHeight();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                bitmapList[i][j + 1] = Bitmap.createBitmap(gameImage, j * widthGameImage / 3, i * heightGameImage / 4, widthGameImage / 3, heightGameImage / 4);
            }
        }

        int value = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                value++;
                imgSplitList[i][j + 1].imgViewSmall.setImageBitmap(bitmapList[i][j + 1]);
                imgSplitList[i][j + 1].currentValue = value;
                imgSplitList[i][j + 1].realValue = value;
            }
        }
        imgSplitList[3][0].imgViewSmall.setImageBitmap(null);
        imgSplitList[3][0].imgViewSmall.setBackgroundDrawable(null);
        imgSplitList[3][0].realValue = 0;
        imgSplitList[3][0].currentValue = 0;
    }

    private void embroilGame(int number) {

        int position_X = 3;
        int position_y = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (imgSplitList[i][j + 1].currentValue == 0) {
                    position_X = i;
                    position_y = j + 1;
                    i = 10;
                    j = 10;
                }
            }
        }

        int pre_X = 0;
        int pre_Y = 0;
        int num = 0;
        ArrayList<Position> listNear;

        for (int k = 0; k < number; k++) {
            if (position_X == 3 && position_y == 0) {
                changeImage(imgSplitList[3][0], imgSplitList[3][1]);
                pre_X = position_X;
                pre_Y = position_y;
                position_y = position_y + 1;
            } else {
                listNear = new ArrayList<>();
                num = 0;
                if (position_y > 1 || (position_y == 1 && position_X == 3)) {
                    if (position_X != pre_X || (position_y - 1) != pre_Y) {
                        listNear.add(new Position(position_X, position_y - 1));
                        num++;
                    }
                }
                if (position_X > 0) {
                    if ((position_X - 1) != pre_X || position_y != pre_Y) {
                        listNear.add(new Position(position_X - 1, position_y));
                        num++;
                    }
                }
                if (position_y < 3) {
                    if (position_X != pre_X || (position_y + 1) != pre_Y) {
                        listNear.add(new Position(position_X, position_y + 1));
                        num++;
                    }
                }
                if (position_X < 3) {
                    if ((position_X + 1) != pre_X || position_y != pre_Y) {
                        listNear.add(new Position(position_X + 1, position_y));
                        num++;
                    }
                }

                Random rand = new Random();
                int local = rand.nextInt(num);
                changeImage(imgSplitList[position_X][position_y], imgSplitList[listNear.get(local).getX()][listNear.get(local).getY()]);
                pre_X = position_X;
                pre_Y = position_y;
                position_X = listNear.get(local).getX();
                position_y = listNear.get(local).getY();

            }
        }
    }

    private void autoPlay(ArrayList<StateGameAuto> rightStateList){
        final ArrayList<StateGameAuto> finalRightPath = rightStateList;
        Thread thread = new Thread(){
            @Override
            public void run() {
                final int[] count = {0};
                if(finalRightPath.size()-2==0){
                    try {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeImage(imgSplitList[finalRightPath.get(count[0]).getPositionEmpty().getX()][finalRightPath.get(count[0]).getPositionEmpty().getY()],
                                        imgSplitList[finalRightPath.get(count[0] +1).getPositionEmpty().getX()][finalRightPath.get(count[0] +1).getPositionEmpty().getY()]);
                                soundPlayer.playMoveSound();
                                count[0]++;
                                tvStep.setText("Your steps: " + count[0] + "/" + (stateGameAutos.size() - 1));
                                if(checkGoal() == true){
                                    autoComlete();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    while (count[0] < (finalRightPath.size() - 2)) {
                        try {
                            Thread.sleep(50);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    changeImage(imgSplitList[finalRightPath.get(count[0]).getPositionEmpty().getX()][finalRightPath.get(count[0]).getPositionEmpty().getY()], imgSplitList[finalRightPath.get(count[0] + 1).getPositionEmpty().getX()][finalRightPath.get(count[0] + 1).getPositionEmpty().getY()]);
                                    soundPlayer.playMoveSound();
                                    count[0]++;
                                    tvStep.setText("Your steps: " + count[0] + "/" + (stateGameAutos.size() - 1));
                                    if(checkGoal() == true){
                                        autoComlete();
                                    }
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                checkAutoPlaying = false;

            }
        };
        thread.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        touchedImage = (ImageView) v;
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 15;
        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

        private int a = 0;
        private int b = 0;

        private boolean checkTouchedImage(){
            if(imgSplitList[3][0].imgViewSmall == touchedImage){
                a = 3; b = 0;
                return true;
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 3; j++) {
                    if (imgSplitList[i][j + 1].imgViewSmall == touchedImage) {
                        a = i;
                        b = j + 1;
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean checkMoveLeft(){
            if(a == 3 && b == 0) return false;
            else if(a != 3 && b == 1) return false;
            return true;
        }

        private boolean checkMoveRight(){
            if(b == 3) return false;
            return true;
        }

        private boolean checkMoveTop(){
            if(a == 3 && b == 0) return false;
            else if(a == 0) return false;
            return true;
        }

        private boolean checkMoveBottom(){
            if(a == 3) return false;
            return true;
        }

        private boolean onClickImageSplit() {
            if (touchedImage == imgSplitList[3][0].imgViewSmall) {
                if (imgSplitList[3][1].currentValue == 0) {
                    changeImage(imgSplitList[3][0], imgSplitList[3][1]);
                    count++;
                    tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                    return true;
                }
            } else if (imgSplitList[a][b].currentValue != 0) {
                if (b > 1 || (b == 1 && a == 3)) {
                    if (imgSplitList[a][b - 1].currentValue == 0) {
                        changeImage(imgSplitList[a][b - 1], imgSplitList[a][b]);
                        count++;
                        tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                        return true;
                    }
                }
                if (b < 3) {
                    if (imgSplitList[a][b + 1].currentValue == 0) {
                        changeImage(imgSplitList[a][b + 1], imgSplitList[a][b]);
                        count++;
                        tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                        return true;
                    }
                }
                if (a > 0) {
                    if (imgSplitList[a - 1][b].currentValue == 0) {
                        changeImage(imgSplitList[a - 1][b], imgSplitList[a][b]);
                        count++;
                        tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                        return true;
                    }
                }

                if (a < 3) {
                    if (imgSplitList[a + 1][b].currentValue == 0) {
                        changeImage(imgSplitList[a + 1][b], imgSplitList[a][b]);
                        count++;
                        tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                        return true;
                    }
                }

            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            soundPlayer.playMoveSound();
            Boolean result =  onClickImageSplit();
            if(checkGoal() == true){
                complete();
            }
            return result;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return checkTouchedImage();        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();



            if(Math.abs(diffX) > Math.abs(diffY)){
                if(Math.abs(diffX)  > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                    if(diffX > 0){
                        onSwipeRight();
                    }
                    else{
                        onSwipeLeft();
                    }
                    result = true;
                }
                if(checkGoal() == true){
                    complete();
                }
            }
            else{
                if(Math.abs(diffY)  > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                    if(diffY > 0){
                        onSwipeBottom();
                    }
                    else{
                        onSwipeTop();
                    }
                    result = true;
                }
                if(checkGoal() == true){
                    complete();
                }
            }
            return result;
        }

        private void onSwipeTop() {
            if(checkMoveTop())
                if(imgSplitList[a-1][b].currentValue == 0){
                    changeImage(imgSplitList[a][b], imgSplitList[a-1][b]);
                    count++;
                    tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                }
        }

        private void onSwipeBottom() {
            if(checkMoveBottom())
                if(imgSplitList[a+1][b].currentValue == 0){
                    changeImage(imgSplitList[a][b], imgSplitList[a+1][b]);
                    count++;
                    tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                }
        }

        private void onSwipeLeft() {
            if(checkMoveLeft())
                if(imgSplitList[a][b-1].currentValue == 0) {
                    changeImage(imgSplitList[a][b], imgSplitList[a][b - 1]);
                    count++;
                    tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                }

        }

        private void onSwipeRight() {
            if(checkMoveRight())
                if(imgSplitList[a][b+1].currentValue == 0) {
                    changeImage(imgSplitList[a][b], imgSplitList[a][b + 1]);
                    count++;
                    tvStep.setText("Your steps: " + count + "/" + (stateGameAutos.size() - 1));
                }
        }
    }
}

