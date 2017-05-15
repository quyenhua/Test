package com.myproject.huutam.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MenuActivity extends Activity {

    private ImageButton imgNewGame, imgContinue, imgSetting, imgExit;
    private MediaPlayer song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initialize();

        if(fileExists(getBaseContext(), "level1.xml") == false){
            imgContinue.setEnabled(false);
        }

        playMusic();
        setEvent();
    }

    private void playMusic() {
        song = MediaPlayer.create(MenuActivity.this, R.raw.alittlelove);
        song.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        song.setAudioStreamType(AudioManager.STREAM_MUSIC);
        song.start();
    }

    private void setEvent() {
        imgNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MenuActivity.this, "New Game", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                alertDialog.setTitle("New Game");
                alertDialog.setMessage("Are you sure you want to play new?");
                alertDialog.setPositiveButton("Yes", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(getBaseContext(), MissionActivity.class);
                        startActivity(intent);
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

        imgContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), MissionActivity.class);
                intent.putExtra("continue", "Continue");
                startActivity(intent);
            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuActivity.this, "Setting", Toast.LENGTH_SHORT).show();
            }
        });

        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);
                alertDialog.setTitle("Exit");
                alertDialog.setMessage("Are you sure you want to exit?");
                alertDialog.setPositiveButton("Yes", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        song.stop();
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
    }

    private void initialize() {
        imgNewGame = (ImageButton) findViewById(R.id.imgNewgame);
        imgContinue = (ImageButton) findViewById(R.id.imgContinue);
        imgSetting = (ImageButton) findViewById(R.id.imgSetting);
        imgExit = (ImageButton) findViewById(R.id.imgExit);
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
}
