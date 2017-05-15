package com.myproject.huutam.test.item;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.myproject.huutam.test.R;

/**
 * Created by Asus on 5/15/2017.
 */

public class SoundPlayer {
    private static SoundPool soundPool;
    private static int moveSound;

    public SoundPlayer(Context context){
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        moveSound = soundPool.load(context, R.raw.move, 1);
    }

    public void playMoveSound(){
        soundPool.play(moveSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}
