package com.oreobites.boredomsmasher;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by dal06 on 2017-06-24.
 */

public class MySound {

    static Context context;
    public static SoundPool soundPool;
    static int soundID_correct_ogg;
    static int soundID_wrong_ogg;

    public static void initSoundPool() {
        //SoundPool 초기설정
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(2);
        soundPool = builder.build();
        soundID_correct_ogg = soundPool.load(context, R.raw.correct_ogg, 1);
        soundID_wrong_ogg = soundPool.load(context, R.raw.wrong_ogg, 1);
    }

    public static void setContext(Context c) {
        context = c;
    }

    public static void playSound(int soundID) {
        //시스템 소리 설정 가져오기
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume / maxVolume;

        soundPool.play(soundID, volume, volume, 1, 0, 1f);
    }
}
