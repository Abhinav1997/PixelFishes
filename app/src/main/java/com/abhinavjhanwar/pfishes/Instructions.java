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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.games.Games;

/**
 * Created by abhinav on 13/9/15.
 */
public class Instructions extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        final SharedPreferences mSP;
        mSP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor mEditor = mSP.edit();

        final Button mRead = (Button) findViewById(R.id.read);
        final TextView mInstruct = (TextView) findViewById(R.id.instructions);
        final TextView mInstruct1 = (TextView) findViewById(R.id.instruct1);
        final TextView mInstruct2 = (TextView) findViewById(R.id.instruct2);
        final TextView mInstruct3 = (TextView) findViewById(R.id.instruct3);
        final TextView mGluck = (TextView) findViewById(R.id.gluck);

        Typeface font = Typeface.createFromAsset(getAssets(), "8bit.ttf");

        mRead.setTypeface(font);
        mInstruct.setTypeface(font);
        mInstruct1.setTypeface(font);
        mInstruct2.setTypeface(font);
        mInstruct3.setTypeface(font);
        mGluck.setTypeface(font);

        mRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mEditor.putInt("instructions", 1);
                mEditor.apply();
                if (MainActivity.mGoogleApiClient.isConnected()) {
                    Games.Achievements.unlock(MainActivity.mGoogleApiClient, getString(R.string.achievement_nerd));
                }
                Intent newgame = new Intent(getBaseContext(), NewGame.class);
                startActivity(newgame);
                finish();
            }
        });
    }
}