package com.oreobites.boredomsmasher;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.oreobites.boredomsmasher.MySound.initSoundPool;
import static com.oreobites.boredomsmasher.MySound.playSound;
import static com.oreobites.boredomsmasher.MySound.soundID_correct_ogg;
import static com.oreobites.boredomsmasher.MySound.soundID_wrong_ogg;

public class OneToFifty extends AppCompatActivity {

    private TextView title;
    private ProgressBar progressBar;
    ArrayList<Button> buttons = new ArrayList<>();
    ArrayList<Integer> numbers = new ArrayList<>();
    private Thread timeThread;

    boolean isRunning = true;
    int nextNumber = 1;

    int randomRange(int Min, int Max) {
        return Min + (int)(Math.random() * ((Max - Min) + 1));
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        numbers.clear();
        buttons.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_fifty);
        initViews();
        setViewStyle();
        initNumbers();
        setNumbers();
        initSoundPool();
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax(120);
                progressBar.setProgress(120);
                while (isRunning) {
                    try {
                        int currentProgress = progressBar.getProgress();
                        if (currentProgress <= 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(OneToFifty.this, "~~~ Time Over ~~~", Toast.LENGTH_SHORT).show();
                                    finishGame();
                                }
                            });
                            break;
                        } else {
                            progressBar.setProgress(currentProgress-1);
                            Thread.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        timeThread.start();
    }

    private void setNumbers() {
        for (int i = 0; i < 25; i++) {
            int index = randomRange(0, numbers.size()-1);
            int num = numbers.remove(index);
            Button button = buttons.get(i);
            button.setText( String.valueOf(num) );
            button.setTextSize(32);

            MyInfo myInfo = (MyInfo)button.getTag();
            myInfo.setMyNumber(num);
            button.setTag(myInfo);
        }

        numbers.clear();
        for (int i = 26; i <= 50; i++) {
            numbers.add(new Integer(i));
        }
    }

    public void onButtonClick(View v) {
        int index = checkId(v.getId());
        Button button = buttons.get(index);
        MyInfo myInfo = (MyInfo)button.getTag();

        if (myInfo.getMyNumber() == nextNumber) {
            playSound(soundID_correct_ogg);
            if (myInfo.getMyNumber() == 50) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OneToFifty.this, "~~~ Congratulations ~~~", Toast.LENGTH_SHORT).show();
                    }
                });
                finishGame();
                return;
            }
            final String tmp = title.getText().toString();
            title.setText("GREAT!");
            if (nextNumber % 2 == 0) title.setTextColor(Color.parseColor("#00cc00"));
            else title.setTextColor(Color.parseColor("#0000cc"));
            changeNumber(v.getId());
            nextNumber++;
        } else if ((!button.getText().toString().equals(""))) {
            playSound(soundID_wrong_ogg);
            final String tmp = title.getText().toString();
            title.setText("WRONG!");
            progressBar.setProgress(progressBar.getProgress()-10);
            if (nextNumber % 2 == 0) title.setTextColor(Color.parseColor("#ff0000"));
            else title.setTextColor(Color.parseColor("#cccc00"));
        } else {
            playSound(soundID_wrong_ogg);
            title.setText("EMPTY!");
            title.setTextColor(Color.rgb(0,0,0));
        }
    }

    private void changeNumber(int id) {
        Button button = (Button) findViewById(id);
        MyInfo myInfo = (MyInfo)button.getTag();
        if (myInfo.getMyNumber() <= 25) {
            int index = randomRange(0,numbers.size()-1);
            int num = numbers.remove(index);
            myInfo.setMyNumber(num);
            button.setTag(myInfo);
            button.setText(String.valueOf(num));
        } else {
            button.setText("");
        }
    }

    private int checkId(int id) {
        for (int i = 0; i < 25; i++) {
            Button button = buttons.get(i);
            if (button.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void setViewStyle() {
        Typeface font_JosefinSlab = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSlab-Bold.ttf");
        title.setTypeface(font_JosefinSlab);
        for (int i = 0; i < 25; i++) {
            buttons.get(i).setTypeface(font_JosefinSlab);
        }
    }

    private void initViews() {

        title = (TextView) findViewById(R.id.OneToFifty_title);
        progressBar = (ProgressBar) findViewById(R.id.OneToFifty_progressBar);
        Button button;
        for (int i = 0; i < 25; i++) {
            int id = getResources().getIdentifier("OneToFifty_b" + String.valueOf(i+1), "id", getPackageName());
            button = (Button) findViewById(id);
            button.setBackgroundResource(R.drawable.button_img);
            MyInfo myInfo = new MyInfo();
            button.setTag(myInfo);

            buttons.add(button);
        }
    }

    private void initNumbers() {
        for (int i = 1; i <= 25; i++) {
            numbers.add(new Integer(i));
        }
    }

    private void finishGame() {
        timeThread.interrupt();
        int score = progressBar.getProgress() * 500;
        System.out.println("Score +" + String.valueOf(score));
        setResult(score);
        finish();
    }

}

class MyInfo {
    private int myNumber = 0;

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

}