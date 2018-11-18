package com.edu.sendmessage;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mob.MobSDK;


/**
 * Created by Administrator on 2018-11-18.
 */

public class SMSSDKUIShell extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smss);

        MobSDK.init(this);


    }

}
