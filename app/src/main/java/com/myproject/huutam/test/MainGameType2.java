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

public class MainGameType2 extends AppCompatActivity {
    //RelativeLayout screen;
    ImageView imgView;
    ImageButton imgbtRefresh;
    ImageButton imgbtAutoPlay;
    ImageButton imgBackOpen, imgBackHome;
    Bitmap gameImage;
    Bitmap bitmapList[][] = new Bitmap[5][5];
    ImageSplit imgSplitList[][] = new ImageSplit[5][5];
    boolean checkAutoPlaying = false;
    TextView tvMission, tvStep;
    MissionItem missionCurrent;
    String fileName = "level";
    int level;
    XMLDOMParser parser = new XMLDOMParser();
    ArrayList<StateGameAuto> stateGameAutos = new ArrayList<>();
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game_type2);
        //screen = (RelativeLayout) findViewById(R.id.manhinh);
        imgView = (ImageView) findViewById(R.id.imgViewPrevious_45);
        //screen.setBackgroundResource(R.drawable.screen);
        imgbtRefresh = (ImageButton) findViewById(R.id.imgbtRefresh_45);
        imgbtAutoPlay = (ImageButton) findViewById(R.id.imgbtAuto_45);

        imgBackOpen = (ImageButton) findViewById(R.id.imgBackOpen_45);
        imgBackHome = (ImageButton) findViewById(R.id.imgBackHome_45);
        tvMission = (TextView) findViewById(R.id.tvMissionName_45);
        tvStep = (TextView) findViewById(R.id.tvStep_45);

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
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                imgSplitList[i][j] = new ImageSplit();
            }
        }
        imgSplitList[4][0] = new ImageSplit();

        imgSplitList[0][1].imgViewSmall = (ImageView) findViewById(R.id.part1_45);
        imgSplitList[0][2].imgViewSmall = (ImageView) findViewById(R.id.part2_45);
        imgSplitList[0][3].imgViewSmall = (ImageView) findViewById(R.id.part3_45);
        imgSplitList[0][4].imgViewSmall = (ImageView) findViewById(R.id.part4_45);
        imgSplitList[1][1].imgViewSmall = (ImageView) findViewById(R.id.part5_45);
        imgSplitList[1][2].imgViewSmall = (ImageView) findViewById(R.id.part6_45);
        imgSplitList[1][3].imgViewSmall = (ImageView) findViewById(R.id.part7_45);
        imgSplitList[1][4].imgViewSmall = (ImageView) findViewById(R.id.part8_45);
        imgSplitList[2][1].imgViewSmall = (ImageView) findViewById(R.id.part9_45);
        imgSplitList[2][2].imgViewSmall = (ImageView) findViewById(R.id.part10_45);
        imgSplitList[2][3].imgViewSmall = (ImageView) findViewById(R.id.part11_45);
        imgSplitList[2][4].imgViewSmall = (ImageView) findViewById(R.id.part12_45);
        imgSplitList[3][1].imgViewSmall = (ImageView) findViewById(R.id.part13_45);
        imgSplitList[3][2].imgViewSmall = (ImageView) findViewById(R.id.part14_45);
        imgSplitList[3][3].imgViewSmall = (ImageView) findViewById(R.id.part15_45);
        imgSplitList[3][4].imgViewSmall = (ImageView) findViewById(R.id.part16_45);
        imgSplitList[4][1].imgViewSmall = (ImageView) findViewById(R.id.part17_45);
        imgSplitList[4][2].imgViewSmall = (ImageView) findViewById(R.id.part18_45);
        imgSplitList[4][3].imgViewSmall = (ImageView) findViewById(R.id.part19_45);
        imgSplitList[4][4].imgViewSmall = (ImageView) findViewById(R.id.part20_45);
        imgSplitList[4][0].imgViewSmall = (ImageView) findViewById(R.id.part0_45);

        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                k++;
                imgSplitList[i][j].currentValue = k;
                imgSplitList[i][j].realValue = k;
            }
        }
        imgSplitList[4][0].currentValue = 0;
        imgSplitList[4][0].realValue = 0;

        splitImage();

        embroilGame(50*level);
        stateGameAutos = findListState();
        tvStep.setText("Your step: 0/" + stateGameAutos.size());

        imgbtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAutoPlaying == false){
                    embroilGame(50 * level);
                    stateGameAutos = new ArrayList<StateGameAuto>();
                    stateGameAutos = findListState();
                    count = 0;
                    tvStep.setText("Your step: 0/" + stateGameAutos.size());
                }
            }
        });

        imgbtAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAutoPlaying == false) {
                    checkAutoPlaying = true;
                    stateGameAutos = new ArrayList<StateGameAuto>();
                    stateGameAutos = findListState();
                    autoPlay(stateGameAutos);
                }

            }
        });

        imgBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainGameType2.this);
                alertDialog.setTitle("Quit Game");
                alertDialog.setMessage("This mission aren't saved. Are you sure you want to quit game?");
                alertDialog.setPositiveButton("Yes", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
//                        Intent intentHome = new Intent(MainActivity.this, MenuActivity.class);
//                        finish();
//                        startActivity(intentHome);
                        finish();
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainGameType2.this);
                alertDialog.setTitle("Quit Game");
                alertDialog.setMessage("This mission aren't saved. Are you sure you want to quit game?");
                alertDialog.setPositiveButton("Yes", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intentReturn = new Intent(MainGameType2.this, OpenImage.class);
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

    private ArrayList<StateGameAuto> findListState() {
        final int[][] startState = new int[5][5];
        final int[][] goalState = new int[5][5];
        final ArrayList<StateGameAuto> stateGameList = new ArrayList<>();

        startState[4][0] = imgSplitList[4][0].currentValue;
        goalState[4][0] = imgSplitList[4][0].realValue;
        StateGameAuto startGame;
        Position position = new Position(0, 0);
        if (imgSplitList[4][0].currentValue == 0) {
            position.setX(4);
            position.setY(0);
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                startState[i][j] = imgSplitList[i][j].currentValue;
                if (imgSplitList[i][j].currentValue == 0) {
                    position.setX(i);
                    position.setY(j);
                }
                goalState[i][j] = imgSplitList[i][j].realValue;
            }
        }


        startGame = new StateGameAuto(startState, position);
        stateGameList.add(startGame);
        GamePlayAuto autoPlayVar = new GamePlayAuto(imgSplitList, goalState, stateGameList);
        return autoPlayVar.GetRightStateGameList();
    }

    private void autoComlete() {

        TextView tvAutoComplete, tvOk;
        final Dialog dialog = new Dialog(MainGameType2.this);
        dialog.setTitle("Autoplay Complete");
        dialog.setContentView(R.layout.dialog_autocomplete);

        tvAutoComplete = (TextView) dialog.findViewById(R.id.tvAutoComplete);
        tvOk = (TextView) dialog.findViewById(R.id.tvOk);

        tvAutoComplete.setText("Auto Play complete!");
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                embroilGame(50*level);
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
        final Dialog dialog = new Dialog(MainGameType2.this);
        dialog.setTitle("Complete Level");
        dialog.setContentView(R.layout.dialog_complete);
        imgStar1 = (ImageView) dialog.findViewById(R.id.imgStarOne);
        imgStar2 = (ImageView) dialog.findViewById(R.id.imgStarTwo);
        imgStar3 = (ImageView) dialog.findViewById(R.id.imgStarThree);
        fadein = AnimationUtils.loadAnimation(MainGameType2.this, R.anim.anim_face_in);

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
                Intent intent = new Intent(MainGameType2.this, OpenImage.class);
                intent.putExtra("level", level + 1);
                finish();
                startActivity(intent);
            }
        });

        imgBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intentMenu = new Intent(MainGameType2.this, MenuActivity.class);
//                finish();
//                startActivity(intentMenu);
                finish();
            }
        });

        imgReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                embroilGame(50*level);
                count = 0;
                dialog.cancel();
            }
        });
    }

    private MissionItem completeLevel() {
        MissionItem missionItem;
        if(count - stateGameAutos.size() <= 5) {
            missionItem = new MissionItem(level, true, R.drawable.threestar, R.drawable.mission, missionCurrent.getImage());
        }
        else if(count - stateGameAutos.size() <= 20){
            missionItem = new MissionItem(level, true, R.drawable.twostar, R.drawable.mission, missionCurrent.getImage());
        }
        else{
            missionItem = new MissionItem(level, true, R.drawable.onestar, R.drawable.mission, missionCurrent.getImage());
        }
        MissionItem itemReturn = missionItem;
        if(missionCurrent.getImageStar() == R.drawable.nostar) {
            String fileCurrentLevel = parser.writeUsingNormalOperation(missionItem);
            writeLevel(fileName + level + ".xml", fileCurrentLevel);
            MissionItem nextOldMission = readLevels(fileName + (level + 1)+ ".xml");
            MissionItem nextMissionItem = new MissionItem(level + 1, true, R.drawable.nostar, R.drawable.mission, nextOldMission.getImage());
            String fileNextLevel = parser.writeUsingNormalOperation(nextMissionItem);
            writeLevel(fileName + (level + 1) + ".xml", fileNextLevel);
        }
        else{
            if(missionCurrent.getImageStar() == R.drawable.twostar){
                if(missionItem.getImageStar() == R.drawable.onestar){
                    missionItem.setImageStar(missionCurrent.getImageStar());
                }
            }
            else if(missionCurrent.getImageStar() == R.drawable.threestar){
                missionItem.setImageStar(missionCurrent.getImageStar());
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
        int image = Integer.parseInt(parser.getValue(element, "image"));

        return new MissionItem(level, lock_bool, star, background, image);
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

    public void onClickImageSplit(View v) {
        ImageView image = (ImageView) v;
        int a = 0;
        int b = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                if (imgSplitList[i][j].imgViewSmall == image) {
                    a = i;
                    b = j;
                    i = 5;
                    j = 5;
                }
            }
        }

        if (v == imgSplitList[4][0].imgViewSmall) {
            if (imgSplitList[4][1].currentValue == 0) {
                changeImage(imgSplitList[4][0], imgSplitList[4][1]);
            }
        } else if (imgSplitList[a][b].currentValue != 0) {
            if (b > 1 || (b == 1 && a == 4)) {
                if (imgSplitList[a][b - 1].currentValue == 0) {
                    changeImage(imgSplitList[a][b - 1], imgSplitList[a][b]);
                }
            }
            if (b < 4) {
                if (imgSplitList[a][b + 1].currentValue == 0) {
                    changeImage(imgSplitList[a][b + 1], imgSplitList[a][b]);
                }
            }
            if (a > 0) {
                if (imgSplitList[a - 1][b].currentValue == 0) {
                    changeImage(imgSplitList[a - 1][b], imgSplitList[a][b]);
                }
            }

            if (a < 4) {
                if (imgSplitList[a + 1][b].currentValue == 0) {
                    changeImage(imgSplitList[a + 1][b], imgSplitList[a][b]);
                }
            }

        }

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
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                bitmapList[i][j] = Bitmap.createBitmap(gameImage, (j-1) * widthGameImage / 4, i * heightGameImage / 5, widthGameImage / 4, heightGameImage / 5);
            }
        }

        int value = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                value++;
                imgSplitList[i][j].imgViewSmall.setImageBitmap(bitmapList[i][j]);
                imgSplitList[i][j].currentValue = value;
                imgSplitList[i][j].realValue = value;
            }
        }
        imgSplitList[4][0].imgViewSmall.setImageBitmap(null);
        imgSplitList[4][0].imgViewSmall.setBackgroundDrawable(null);
        imgSplitList[4][0].realValue = 0;
        imgSplitList[4][0].currentValue = 0;
    }

    private void embroilGame(int number) {

        int position_X = 4;
        int position_y = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 5; j++) {
                if (imgSplitList[i][j].currentValue == 0) {
                    position_X = i;
                    position_y = j;
                    i = 5;
                    j = 5;
                }
            }
        }

        int pre_X = 0;
        int pre_Y = 0;
        int num = 0;
        ArrayList<Position> listNear;

        for (int k = 0; k < number; k++) {
            if (position_X == 4 && position_y == 0) {
                changeImage(imgSplitList[4][0], imgSplitList[4][1]);
                pre_X = position_X;
                pre_Y = position_y;
                position_y = position_y + 1;
            } else {
                listNear = new ArrayList<>();
                num = 0;
                if (position_y > 1 || (position_y == 1 && position_X == 4)) {
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
                if (position_y < 4) {
                    if (position_X != pre_X || (position_y + 1) != pre_Y) {
                        listNear.add(new Position(position_X, position_y + 1));
                        num++;
                    }
                }
                if (position_X < 4) {
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
                        Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                changeImage(imgSplitList[finalRightPath.get(count[0]).getPositionEmpty().getX()][finalRightPath.get(count[0]).getPositionEmpty().getY()],
                                        imgSplitList[finalRightPath.get(count[0] +1).getPositionEmpty().getX()][finalRightPath.get(count[0] +1).getPositionEmpty().getY()]);
                                count[0]++;
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    while (count[0] < (finalRightPath.size() - 2)) {
                        try {
                            Thread.sleep(500);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    changeImage(imgSplitList[finalRightPath.get(count[0]).getPositionEmpty().getX()][finalRightPath.get(count[0]).getPositionEmpty().getY()], imgSplitList[finalRightPath.get(count[0] + 1).getPositionEmpty().getX()][finalRightPath.get(count[0] + 1).getPositionEmpty().getY()]);
                                    count[0]++;
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                checkAutoPlaying =false;
            }
        };
        thread.start();
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
}


