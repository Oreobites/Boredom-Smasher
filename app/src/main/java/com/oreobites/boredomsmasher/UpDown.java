package com.oreobites.boredomsmasher;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpDown extends AppCompatActivity {

    private TextView title, lifeText, life, print;
    private EditText input;
    private int answer, score, lifeNum = 120;
    private Thread lifeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_down);
        initView();
        setViewStyle();
        answer = (int)(Math.random()*100) + 1; //1 이상 100 이하
        lifeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        lifeNum = Integer.parseInt(life.getText().toString());
                        lifeNum--;
                        if (lifeNum <=0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UpDown.this, "~~~ TIME OVER ~~~", Toast.LENGTH_SHORT).show();
                                }
                            });
                            score = 0;
                            finishGame();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                life.setText(String.valueOf(lifeNum));
                            }
                        });
                            Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });

        lifeThread.start();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.UpDown_title);
        input = (EditText) findViewById(R.id.UpDown_input);
        lifeText = (TextView) findViewById(R.id.UpDown_lifeText);
        life = (TextView) findViewById(R.id.UpDown_life);
        print = (TextView) findViewById(R.id.UpDown_print);

        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String tmp = input.getText().toString();
                    System.out.println(tmp);
                    guess(input);
                    return true;
                }
                return false;
            }
        });
    }

    private void setViewStyle() {
        Typeface font_JosefinSlab = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSlab-Regular.ttf");
        title.setTypeface(font_JosefinSlab);
        input.setTypeface(font_JosefinSlab);
        lifeText.setTypeface(font_JosefinSlab);
        life.setTypeface(font_JosefinSlab);
        print.setTypeface(font_JosefinSlab);
    }

    public void guess(View v) {
        try {
            int usr = Integer.parseInt(input.getText().toString());
            if (usr < 1 || usr > 100) {
                Toast.makeText(this, "Enter Number Between 1 ~ 100!!", Toast.LENGTH_SHORT).show();
            } else {
                if (usr==answer) {
                    lifeThread.interrupt();
                    Toast.makeText(this, "~~~ Wow!! The Answer was " + answer + "!! ~~~", Toast.LENGTH_SHORT).show();
                    score = 100 * lifeNum;
                    finishGame();

                } else if (usr < answer) {
                    //print UP
                    print.setText("UP!");
                    lifeNum -= 10;
                    life.setText(String.valueOf(lifeNum));
                } else {
                    //print DOWN
                    print.setText("DOWN!");
                    lifeNum -= 10;
                    life.setText(String.valueOf(lifeNum));
                }
                input.setText("");
                print.setTextColor(Color.rgb((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Only Numbers Allowed!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishGame() {
        lifeThread.interrupt();
        setResult(score);
        finish();
    }
}
