package com.oreobites.boredomsmasher;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WordChain extends AppCompatActivity {

    private TextView title, wordPrint, chainText, chain;
    private EditText input;
    private Button send;
    private ProgressBar progressBar;
    private Thread timeThread, okPrintThread;
    int score = 0;
    int cnt = randomRange(10, 15);
    ArrayList<String> wordList = new ArrayList<>();
    ArrayList<String> usedWordList = new ArrayList<>();
    String pcWord, usrWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        wordList.clear();
        usedWordList.clear();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordchain);
        initView();
        setViewStyle();
        timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax(40);
                progressBar.setProgress(40);
                while (true) {
                    try {
                        int currentProgress = progressBar.getProgress();
                        if (currentProgress <= 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WordChain.this, "~~~ Time Over ~~~", Toast.LENGTH_SHORT).show();
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

        okPrintThread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String tmp1, tmp2;

                        tmp1 = chainText.getText().toString();
                        tmp2 = chain.getText().toString();
                        score += usrWord.length();
                        chainText.setText("OK! Score +");
                        chain.setText(String.valueOf(usrWord.length() * 100));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                chainText.setText(tmp1);
                                chain.setText(tmp2);
                                chain.setText(String.valueOf(Integer.parseInt(chain.getText().toString()) + 1));
                            }
                        }, 1000);
                    }
                });
            }
        });
        timeThread.start();
        initWordList();

        pcWord = getRandomWord();
        wordPrint.setText(pcWord);
    }

    int randomRange(int Min, int Max) {
        return Min + (int)(Math.random() * ((Max - Min) + 1));
    }

    private String getRandomWord() {
        int index = randomRange(0, wordList.size()-1);
        return wordList.get(index);
    }

    private void getNextWord(char c) {
        progressBar.setProgress(40);
        cnt--;
        if (cnt <= 0) {
            Toast.makeText(this, "~~~ I Give Up! You win ;) ~~~", Toast.LENGTH_SHORT).show();
            finishGame();
        } else {
            wordPrint.setText("Hmm...");
            while (true) {
                pcWord = getRandomWord();
                if (!(usedWordList.contains(pcWord)) && (pcWord.charAt(0) == c) && (pcWord.charAt(pcWord.length()-1) >= 'a') && (pcWord.charAt(pcWord.length()-1) <= 'z')) {
                    break;
                }
            }
            wordPrint.setText(pcWord);
            usedWordList.add(pcWord);
            progressBar.setProgress(60);
        }
    }

    public void sendWord(View v) {
        if (input.getText().toString().equals("")) {
            Toast.makeText(this, "You didn't write anything :(", Toast.LENGTH_SHORT).show();
            return;
        }
        usrWord = input.getText().toString();
        input.setText("");
        if (!(usedWordList.contains(usrWord)) && (Character.toLowerCase(usrWord.charAt(0)) == Character.toLowerCase(pcWord.charAt(pcWord.length()-1))) && wordList.contains(usrWord) && (usrWord.length()>=4)) {
            usedWordList.add(usrWord);
            okPrintThread.start();
            getNextWord(usrWord.charAt(usrWord.length()-1));
        } else {
            Toast.makeText(this, "No, you can't do that :D", Toast.LENGTH_SHORT).show();
        }
    }

    private void initWordList() {
        BufferedReader reader;
        try {
            final InputStream file = getAssets().open("words.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            wordList.add(line);
            while (true){
                line = reader.readLine();
                if (line != null) {
                    wordList.add(line);
                } else {
                    break;
                }
            }
            file.close();
            reader.close();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initView() {
        title = (TextView)findViewById(R.id.WordChain_title);
        wordPrint = (TextView)findViewById(R.id.WordChain_wordPrint);
        chainText = (TextView)findViewById(R.id.WordChain_currentChainText);
        chain = (TextView)findViewById(R.id.WordChain_currentChain);
        input = (EditText) findViewById(R.id.WordChain_input);
        input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String tmp = input.getText().toString();
                    System.out.println(tmp);
                    sendWord(input);
                    return true;
                }
                return false;
            }
        });
        send = (Button) findViewById(R.id.WordChain_send);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void setViewStyle() {
        Typeface font_JosefinSlab = Typeface.createFromAsset(getAssets(),
                "fonts/JosefinSlab-Regular.ttf");
        title.setTypeface(font_JosefinSlab);
        wordPrint.setTypeface(font_JosefinSlab);
        chainText.setTypeface(font_JosefinSlab);
        chain.setTypeface(font_JosefinSlab);
        input.setTypeface(font_JosefinSlab);
        send.setTypeface(font_JosefinSlab);
    }

    private void finishGame() {
        timeThread.interrupt();
        score *= 100;
        setResult(score);
        finish();
    }
}
