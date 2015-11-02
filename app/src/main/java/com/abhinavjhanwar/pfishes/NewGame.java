/*
 * Copyright (C) 2015 Abhinav Jhanwar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abhinavjhanwar.pfishes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import java.io.IOException;
import java.util.Random;

import static android.media.MediaPlayer.*;

/**
 * Created by abhinav on 5/9/15.
 */
public class NewGame extends AppCompatActivity {

    public static GoogleApiClient mGoogleApiClient;
    private MediaPlayer mSound = new MediaPlayer();
    private static long mStart, mEnd;
    private String TAG = "PixelFishes";
    private static int back = 0, hscore = 0, mRound = 1, mLife = 3, fish;
    private static double mTime = 900, mTimediff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mGoogleApiClient = MainActivity.mGoogleApiClient;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgame);
        Typeface font = Typeface.createFromAsset(getAssets(), "8bit.ttf");

        final SharedPreferences mSP;
        mSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor mEditor = mSP.edit();

        final int mScore = mSP.getInt("score", 0);

        final ImageView mFish1 = (ImageView) findViewById(R.id.fish1);
        final ImageView mFish2 = (ImageView) findViewById(R.id.fish2);
        final ImageView mFish3 = (ImageView) findViewById(R.id.fish3);

        mFish1.setClickable(true);
        mFish2.setClickable(true);
        mFish3.setClickable(true);

        final Button mNext = (Button) findViewById(R.id.next);
        final Button mRetry = (Button) findViewById(R.id.retry);
        final Button mExit = (Button) findViewById(R.id.exit);

        mNext.setVisibility(View.GONE);
        mRetry.setVisibility(View.GONE);
        mExit.setVisibility(View.GONE);

        mNext.setTypeface(font);
        mRetry.setTypeface(font);
        mExit.setTypeface(font);

        final TextView mR = (TextView) findViewById(R.id.round);
        final TextView mRoundNo = (TextView) findViewById(R.id.roundno);
        final TextView mGameOver = (TextView) findViewById(R.id.gameover);
        final TextView mError = (TextView) findViewById(R.id.error);
        final TextView mError2 = (TextView) findViewById(R.id.error2);
        final TextView mPoint = (TextView) findViewById(R.id.scoreno);
        final TextView mHighScore = (TextView) findViewById(R.id.highscore);
        final TextView mColon = (TextView) findViewById(R.id.colon);
        final TextView mHighScoreNo = (TextView) findViewById(R.id.highscoreno);

        mR.setTypeface(font);
        mRoundNo.setTypeface(font);
        mPoint.setTypeface(font);
        mGameOver.setTypeface(font);
        mError.setTypeface(font);
        mError2.setTypeface(font);
        mHighScore.setTypeface(font);
        mHighScoreNo.setTypeface(font);
        mColon.setTypeface(font);
        mHighScoreNo.setText(String.valueOf(mScore));

        mGameOver.setVisibility(View.GONE);
        mHighScore.setVisibility(View.GONE);
        mHighScoreNo.setVisibility(View.GONE);
        mColon.setVisibility(View.GONE);

        mPoint.setText(String.valueOf(hscore));
        mRoundNo.setText(String.valueOf(mRound));

        play();

        mFish1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEnd = System.currentTimeMillis();
                if (mSound.isPlaying()) {
                    mSound.reset();
                    mLife--;
                    if (mLife > 0) {
                        vibrate(500);
                        setLife();
                        mFish1.setImageResource(R.drawable.fish_red);
                        mError.setText(R.string.before);
                        mRetry.setVisibility(View.VISIBLE);
                    } else {
                        setLife();
                        over();
                        mError2.setText(R.string.before);
                        back = 1;
                    }
                } else {
                    if (fish == 1) {
                        mTimediff = (mEnd - mStart);
                        if (mTimediff < mTime) {
                            mFish1.setImageResource(R.drawable.fish_green);
                            hscore++;
                            mPoint.setText(String.valueOf(hscore));
                            updateAchievements();
                            updateLeaderboard();
                            if (hscore > mScore) {
                                mEditor.putInt("score", hscore);
                                mEditor.apply();
                            }
                            mNext.setVisibility(View.VISIBLE);
                        } else {
                            mLife--;
                            if (mLife > 0) {
                                vibrate(500);
                                mFish1.setImageResource(R.drawable.fish_red);
                                mError.setText(R.string.after);
                                setLife();
                                mRetry.setVisibility(View.VISIBLE);

                            } else {
                                setLife();
                                over();
                                mError2.setText(R.string.after);
                                back = 1;
                            }
                        }
                    } else {
                        mLife--;
                        if (mLife > 0) {
                            vibrate(500);
                            setLife();
                            mFish1.setImageResource(R.drawable.fish_red);
                            mError.setText(R.string.wrong);
                            mRetry.setVisibility(View.VISIBLE);

                        } else {
                            setLife();
                            over();
                            mError2.setText(R.string.wrong);
                            back = 1;
                        }
                    }

                }
                mFish1.setClickable(false);
                mFish2.setClickable(false);
                mFish3.setClickable(false);
            }
        });

        mFish2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEnd = System.currentTimeMillis();
                if (mSound.isPlaying()) {
                    mSound.reset();
                    mLife--;
                    if (mLife > 0) {
                        vibrate(500);
                        setLife();
                        mFish2.setImageResource(R.drawable.fish_red);
                        mError.setText(R.string.before);
                        mRetry.setVisibility(View.VISIBLE);
                    } else {
                        setLife();
                        over();
                        mError2.setText(R.string.before);
                        back = 1;
                    }
                } else {
                    if (fish == 2) {
                        mTimediff = (mEnd - mStart);
                        if (mTimediff < mTime) {
                            mFish2.setImageResource(R.drawable.fish_green);
                            hscore++;
                            mPoint.setText(String.valueOf(hscore));
                            updateAchievements();
                            updateLeaderboard();
                            if (hscore > mScore) {
                                mEditor.putInt("score", hscore);
                                mEditor.apply();
                            }
                            mNext.setVisibility(View.VISIBLE);
                        } else {
                            mLife--;
                            if (mLife > 0) {
                                vibrate(500);
                                setLife();
                                mFish2.setImageResource(R.drawable.fish_red);
                                mError.setText(R.string.after);
                                mRetry.setVisibility(View.VISIBLE);

                            } else {
                                setLife();
                                over();
                                mError2.setText(R.string.after);
                                back = 1;
                            }
                        }
                    } else {
                        mLife--;
                        if (mLife > 0) {
                            vibrate(500);
                            setLife();
                            mFish2.setImageResource(R.drawable.fish_red);
                            mError.setText(R.string.wrong);
                            mRetry.setVisibility(View.VISIBLE);

                        } else {
                            setLife();
                            over();
                            mError2.setText(R.string.wrong);
                            back = 1;
                        }
                    }

                }
                mFish1.setClickable(false);
                mFish2.setClickable(false);
                mFish3.setClickable(false);
            }
        });

        mFish3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEnd = System.currentTimeMillis();
                if (mSound.isPlaying()) {
                    mSound.reset();
                    mLife--;
                    if (mLife > 0) {
                        vibrate(500);
                        setLife();
                        mFish3.setImageResource(R.drawable.fish_red);
                        mError.setText(R.string.before);
                        mRetry.setVisibility(View.VISIBLE);
                    } else {
                        setLife();
                        over();
                        mError2.setText(R.string.before);
                        back = 1;
                    }
                } else {
                    if (fish == 3) {
                        mTimediff = (mEnd - mStart);
                        if (mTimediff < mTime) {
                            mFish3.setImageResource(R.drawable.fish_green);
                            hscore++;
                            mPoint.setText(String.valueOf(hscore));
                            updateAchievements();
                            updateLeaderboard();
                            if (hscore > mScore) {
                                mEditor.putInt("score", hscore);
                                mEditor.apply();
                            }
                            mNext.setVisibility(View.VISIBLE);
                        } else {
                            mLife--;
                            if (mLife > 0) {
                                vibrate(500);
                                setLife();
                                mFish3.setImageResource(R.drawable.fish_red);
                                mError.setText(R.string.after);
                                mRetry.setVisibility(View.VISIBLE);

                            } else {
                                setLife();
                                over();
                                mError2.setText(R.string.after);
                                back = 1;
                            }
                        }
                    } else {
                        mLife--;
                        if (mLife > 0) {
                            vibrate(500);
                            setLife();
                            mFish3.setImageResource(R.drawable.fish_red);
                            mError.setText(R.string.wrong);
                            mRetry.setVisibility(View.VISIBLE);

                        } else {
                            setLife();
                            over();
                            mError2.setText(R.string.wrong);
                            back = 1;
                        }
                    }

                }
                mFish1.setClickable(false);
                mFish2.setClickable(false);
                mFish3.setClickable(false);
            }
        });

        mRetry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mFish1.setVisibility(View.VISIBLE);
                mFish2.setVisibility(View.VISIBLE);
                mFish3.setVisibility(View.VISIBLE);
                mFish1.setImageResource(R.drawable.fish_purple);
                mFish2.setImageResource(R.drawable.fish_purple);
                mFish3.setImageResource(R.drawable.fish_purple);
                mFish1.setClickable(true);
                mFish2.setClickable(true);
                mFish3.setClickable(true);
                mError.setText(" ");
                play();
                mRetry.setVisibility(View.GONE);
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mRound++;
                mRoundNo.setText(String.valueOf(mRound));
                mTime = (mTime - 90);
                if (mTime < 0) {
                    mTime = 50;
                }
                if (mRound <= 10) {
                    mFish1.setImageResource(R.drawable.fish_purple);
                    mFish2.setImageResource(R.drawable.fish_purple);
                    mFish3.setImageResource(R.drawable.fish_purple);
                    mFish1.setClickable(true);
                    mFish2.setClickable(true);
                    mFish3.setClickable(true);
                    play();
                } else {
                    mNext.setVisibility(View.GONE);
                    mPoint.setVisibility(View.GONE);
                    mRoundNo.setVisibility(View.GONE);
                    mR.setVisibility(View.GONE);
                    mFish1.setVisibility(View.GONE);
                    mFish2.setVisibility(View.GONE);
                    mFish3.setVisibility(View.GONE);
                    mGameOver.setText(R.string.congrats);
                    mError2.setText(R.string.game);
                    mGameOver.setVisibility(View.VISIBLE);
                    mExit.setVisibility(View.VISIBLE);
                    back = 1;
                }
                mNext.setVisibility(View.GONE);
            }
        });

        mExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (back == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit game")
                    .setMessage("Do you want to quit the current game?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            updateLeaderboard();
            updateAchievements();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSound.reset();
        back = 0;
        hscore = 0;
        mTime = 500;
        mRound = 1;
        mLife = 3;
        updateAchievements();
        updateLeaderboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSound.reset();
    }

    public void play() {
        final ImageView mFish1 = (ImageView) findViewById(R.id.fish1);
        final ImageView mFish2 = (ImageView) findViewById(R.id.fish2);
        final ImageView mFish3 = (ImageView) findViewById(R.id.fish3);

        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.sound);

        try {
            mSound.reset();
            mSound.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mSound.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mSound.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }

        CountDownTimer mCountDown = new CountDownTimer(getSeconds(), 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fish = getFish();
                        if (mSound.isPlaying()) {
                            mSound.reset();
                            switch (fish) {
                                case 1:
                                    mFish1.setImageResource(R.drawable.fish_orange);
                                    break;
                                case 2:
                                    mFish2.setImageResource(R.drawable.fish_orange);
                                    break;
                                case 3:
                                    mFish3.setImageResource(R.drawable.fish_orange);
                                    break;
                            }
                        }
                        mStart = System.currentTimeMillis();
                    }
                });
            }
        };
        mCountDown.start();
    }

    public int getSeconds() {
        int[] mSeconds = {1000, 1500, 1700, 2000, 2300, 2500, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000};
        int mPosition = new Random().nextInt(mSeconds.length);
        return mSeconds[mPosition];
    }

    public int getFish() {
        int[] mFishes = {1, 2, 3};
        int mPosition = new Random().nextInt(mFishes.length);
        return mFishes[mPosition];
    }

    public void vibrate(int vib) {
        Vibrator mVibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mVibrate.vibrate(vib);
    }

    public void over() {
        final SharedPreferences mSP;
        mSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final ImageView mFish1 = (ImageView) findViewById(R.id.fish1);
        final ImageView mFish2 = (ImageView) findViewById(R.id.fish2);
        final ImageView mFish3 = (ImageView) findViewById(R.id.fish3);
        final Button mExit = (Button) findViewById(R.id.exit);
        final TextView mGameOver = (TextView) findViewById(R.id.gameover);
        final TextView mHighScore = (TextView) findViewById(R.id.highscore);
        final TextView mColon = (TextView) findViewById(R.id.colon);
        final TextView mHighScoreNo = (TextView) findViewById(R.id.highscoreno);
        mHighScoreNo.setText(String.valueOf(mSP.getInt("score", 0)));
        mFish1.setVisibility(View.GONE);
        mFish2.setVisibility(View.GONE);
        mFish3.setVisibility(View.GONE);
        mGameOver.setVisibility(View.VISIBLE);
        mExit.setVisibility(View.VISIBLE);
        mHighScore.setVisibility(View.VISIBLE);
        mHighScoreNo.setVisibility(View.VISIBLE);
        mColon.setVisibility(View.VISIBLE);
        vibrate(1000);
        updateLeaderboard();
        updateAchievements();
        setLife();
        back = 1;
    }

    public void setLife() {
        final ImageView mLife1 = (ImageView) findViewById(R.id.life1);
        final ImageView mLife2 = (ImageView) findViewById(R.id.life2);
        final ImageView mLife3 = (ImageView) findViewById(R.id.life3);
        switch (mLife) {
            case 0:
                mLife1.setImageResource(R.drawable.heart_grey);
                mLife2.setImageResource(R.drawable.heart_grey);
                mLife3.setImageResource(R.drawable.heart_grey);
                break;
            case 1:
                mLife1.setImageResource(R.drawable.heart_grey);
                mLife2.setImageResource(R.drawable.heart_grey);
                break;
            case 2:
                mLife1.setImageResource(R.drawable.heart_grey);
                break;
        }
    }

    public void updateLeaderboard() {
        final SharedPreferences mSP;
        mSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (mGoogleApiClient.isConnected()) {
            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_high_scores), mSP.getInt("score", 0));
        }
    }

    public void updateAchievements() {
        if (mGoogleApiClient.isConnected()) {
            switch (hscore) {
                case 1:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_newbie_fisher));
                    break;
                case 5:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_intermediate_fisher));
                    break;
                case 9:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_pro_fisher));
                    break;
                case 10:
                    Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_fishing_god));
                    break;
            }
        }
    }
}
