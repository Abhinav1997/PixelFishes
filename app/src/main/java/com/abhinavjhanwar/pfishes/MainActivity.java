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

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PixelFishes";
    private static final int RC_SIGN_IN = 9001, REQUEST_ACHIEVEMENTS = 1001, REQUEST_LEADERBOARD = 1002;
    public static GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private boolean mAutoStartSignInFlow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        final Button mNewGame = (Button) findViewById(R.id.play);
        final Button mAchievements = (Button) findViewById(R.id.achievements);
        final Button mLeaderBoard = (Button) findViewById(R.id.leaderboard);
        final TextView mTitle = (TextView) findViewById(R.id.title1);
        final TextView mTitle2 = (TextView) findViewById(R.id.title2);
        final SharedPreferences mSP;
        mSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Typeface font = Typeface.createFromAsset(getAssets(), "8bit.ttf");
        mNewGame.setTypeface(font);
        mAchievements.setTypeface(font);
        mLeaderBoard.setTypeface(font);
        mTitle.setTypeface(font);
        mTitle2.setTypeface(font);

        mNewGame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int mInstructions = mSP.getInt("instructions", 0);
                if (mInstructions == 0) {
                    Intent instruction = new Intent(getBaseContext(), Instructions.class);
                    startActivity(instruction);
                } else {
                    Intent newgame = new Intent(getBaseContext(), NewGame.class);
                    startActivity(newgame);
                }
            }
        });

        mAchievements.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
                } else {
                    BaseGameUtils.makeSimpleDialog(MainActivity.this, getString(R.string.achievements_not_available)).show();
                }
            }
        });

        mLeaderBoard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                            getString(R.string.leaderboard_high_scores)), REQUEST_LEADERBOARD);
                } else {
                    BaseGameUtils.makeSimpleDialog(MainActivity.this, getString(R.string.leaderboards_not_available)).show();
                }
            }
        });
    }

    protected void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected() called. Sign in successful!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult with requestCode == RC_SIGN_IN, responseCode="
                    + responseCode + ", intent=" + intent);
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (responseCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.signin_other_error);
            }
        }

        if (
                responseCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            mGoogleApiClient.disconnect();
        }
    }
}