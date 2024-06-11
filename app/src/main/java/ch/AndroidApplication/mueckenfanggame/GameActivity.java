package ch.AndroidApplication.mueckenfanggame;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    private Boolean gameRunning;
    private int round;
    private int points;
    private int mosquitoes;
    private int catchedmosquitoes;
    private int playTime;
    private float scale;
    private Handler handler = new Handler();
    private Random randomNumberGenerator = new Random();
    private ViewGroup playGround;

    private static final long maxAge = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scale = getResources().getDisplayMetrics().density;
        playGround = playGround.findViewById(R.id.play_area);

    }

    private void runGame() {
        gameRunning = true;
        round = 0;
        points = 0;
        startroundGame();
    }

    private void startroundGame() {
        round = +1;
        mosquitoes = round * 10;
        catchedmosquitoes = 0;
        playTime = 60;

        updateScreen();
        handler.postDelayed(this, 1000);
    }

    private void updateScreen() {
        /*Update Points*/
        TextView tvpoints = findViewById(R.id.points);
        tvpoints.setText(Integer.toString(points));

        /*Update Round*/
        TextView tvround = findViewById(R.id.round);
        tvround.setText(Integer.toString(round));

        /*Update Hits*/
        TextView tvhits = findViewById(R.id.hits);
        tvhits.setText(Integer.toString(catchedmosquitoes));

        /*Update Time*/
        TextView tvtime = findViewById(R.id.time);
        tvtime.setText(Integer.toString(playTime));

        /*Bars*/
        FrameLayout flhits = findViewById(R.id.bar_hits);
        FrameLayout fltime = findViewById(R.id.bar_time);


        /*Update Bar*/
        FrameLayout.LayoutParams lphits = (FrameLayout.LayoutParams) flhits.getLayoutParams();
        // Berechne die Breite:
        // scale: Bildschirmmaßstab (dichte-unabhängige Pixel zu Pixel)
        // 300: Maximalbreite des Balkens in dp
        // Math.min(catchedmosquitoes, mosquitoes): Anzahl gefangener Mücken, aber nicht mehr als die Anzahl der zu fangenden Mücken
        // mosquitoes: Anzahl der zu fangenden Mücken (Verhältnis)
        lphits.width = Math.round(scale * 300 * Math.min(catchedmosquitoes, mosquitoes) / mosquitoes);

        /*Update Bar*/
        FrameLayout.LayoutParams lptime = (FrameLayout.LayoutParams) fltime.getLayoutParams();
        // Berechne die Breite:
        // scale: Bildschirmmaßstab (dichte-unabhängige Pixel zu Pixel)
        // playTime: Verbleibende Zeit
        // 300: Maximalbreite des Balkens in dp
        // 60: Maximale Spielzeit in Sekunden (Verhältnis)
        lphits.width = Math.round(scale * playTime * 300 / 60);
    }

    public void timeCounter() {
        /*Chance*/
        double chance = mosquitoes * 1.5f / 60;
        playTime = playTime - 1;

        /*Abrunden Aufrunden*/
        float randomNumber = randomNumberGenerator.nextFloat();

        /*Chance*/
        if (chance > -1) {
            showmosquitoe();
            if (randomNumber < chance - 1) {
                showmosquitoe();
            }
        } else {
            if (randomNumber < chance - 1) {
                showmosquitoe();
            }
        }

        hidemosquitoe();
        updateScreen();
        if (!checkGameEnd()) {
            checkRoundEnd();
            handler.postDelayed(this,1000);
        }
        handler.postDelayed(this,1000);
    }

    private boolean checkGameEnd() {
        if (playTime == 0 && catchedmosquitoes < mosquitoes) {
            gameOver();
            return true;
        }
        return false;
    }

    private void gameOver() {
        Dialog dialog = new Dialog(this, android.R.style
                .Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.gameover);
        dialog.show();
        gameRunning = false;
    }

    private boolean checkRoundEnd() {
        if (catchedmosquitoes >= mosquitoes) {
            startroundGame();
        }
        return false;
    }

    private void hidemosquitoe() {
        int number = 0;
        while (number < playGround.getChildCount()) {
            ImageView mosquito = (ImageView) playGround.getChildAt(number);
            number = number + 1;
            Date birthdate = (Date) mosquito.getTag(R.id.birthdate);
            long age = (new Date()).getTime() - birthdate.getTime();
            if (age > maxAge) {
                playGround.removeView(mosquito);
            } else {
                number++;
            }
        }

    }

    private void showmosquitoe() {
        int width = playGround.getWidth();
        int height = playGround.getHeight();

        int mosquitoewidth = Math.round(scale * 50);
        int mosquitoeheight = Math.round(scale * 42);

        int left = randomNumberGenerator.nextInt(width - mosquitoewidth);
        int top = randomNumberGenerator.nextInt(height - mosquitoeheight);

        ImageView mosquito = new ImageView(this);
        mosquito.setImageResource(R.drawable.muecke);
        mosquito.setOnClickListener(this);
        mosquito.setTag(R.id.birthdate, new Date());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mosquitoewidth, mosquitoeheight);
        params.leftMargin = left;
        params.topMargin = top;
        params.gravity = Gravity.TOP + Gravity.LEFT;

        playGround.addView(mosquito, params);

    }


    @Override
    public void onClick(View v) {
        catchedmosquitoes++;
        points += 100;
        updateScreen();

    }

    @Override
    public void run() {
        handler.postDelayed(this, 1000);
    }
}