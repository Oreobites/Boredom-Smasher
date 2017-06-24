package com.oreobites.boredomsmasher;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.oreobites.boredomsmasher.MySound.initSoundPool;
import static com.oreobites.boredomsmasher.MySound.playSound;
import static com.oreobites.boredomsmasher.MySound.setContext;
import static com.oreobites.boredomsmasher.MySound.soundID_correct_ogg;

public class MainActivity extends AppCompatActivity {

    TextView title1, title2, title3;
    Button please;
    MediaPlayer bgmPlayer = null;

    int score = 0;
    ArrayList<Integer> gameIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setViewStyle();
        resetGameIDs();
        initBGM();
        setContext(this);
        initSoundPool();
    }

    private void initBGM() {
        if (bgmPlayer != null) return;
        bgmPlayer = MediaPlayer.create(MainActivity.this, R.raw.energetic);
        bgmPlayer.setLooping(true);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = (actualVolume / maxVolume) ;
        bgmPlayer.setVolume(volume, volume);
        bgmPlayer.start();
    }

    private void resetGameIDs() {
        for (int i=1; i<=3; i++) {
            gameIDs.add(new Integer(i));
        }
    }

    private void initView() {
        title1 = (TextView)findViewById(R.id.title1);
        title2 = (TextView)findViewById(R.id.title2);
        title3 = (TextView)findViewById(R.id.title3);
        please = (Button)findViewById(R.id.please);
    }

    private void setViewStyle() {
        Typeface font_JosefinSlab = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSlab-Bold.ttf");
        title1.setTypeface(font_JosefinSlab);
        title2.setTypeface(font_JosefinSlab);
        title3.setTypeface(font_JosefinSlab);
        please.setTypeface(font_JosefinSlab);
    }

    int randomRange(int Min, int Max) {
        return Min + (int)(Math.random() * ((Max - Min) + 1));
    }

    public void launchGame(View v) {
        //버튼 onClick 메소드
        playSound(soundID_correct_ogg);

        if (gameIDs.size() == 0) resetGameIDs();
        final int index = randomRange(0, gameIDs.size()-1);

        Toast.makeText(this, "Current Score is " + String.valueOf(score) + "!!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                switch (gameIDs.remove(index)) {
                    case 1:
                        //Up & Down Game
                        intent = new Intent(MainActivity.this, UpDown.class);
                        startActivityForResult(intent, 100);
                        break;
                    case 2:
                        //끝말잇기
                        intent = new Intent(MainActivity.this, WordChain.class);
                        startActivityForResult(intent, 200);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, OneToFifty.class);
                        startActivityForResult(intent, 300);
                        break;
                }
            }
        }, 500);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //Up&Down
            case 100:
                score += resultCode;
                break;

            case 200:
                score += resultCode;
                break;

            case 300:
                score += resultCode;
                break;
        }
    }
}
