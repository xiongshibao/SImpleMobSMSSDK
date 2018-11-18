package com.edu.sendmessage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_number;
    private EditText et_checkCode;

    private TextView tv_getCheckCode;
    private TextView tv_sendCheckCode;

    private String phoneNumber;
    private String checkCode;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_number = (EditText) findViewById(R.id.et_number);
        et_checkCode = (EditText) findViewById(R.id.et_checkCode);
        tv_getCheckCode = (TextView) findViewById(R.id.tv_getCheckCode);
        tv_sendCheckCode = (TextView) findViewById(R.id.tv_sendCheckCode);

        checkCode = et_checkCode.getText().toString().trim();

        tv_getCheckCode.setOnClickListener(this);
        tv_sendCheckCode.setOnClickListener(this);

        MobSDK.init(this,"28c4d19ea6b04","24006ac31eee052e78214ec8208938e6");

        //注册短信回调
        SMSSDK.registerEventHandler(ev);
    }

    /**
     * 短信验证的回调监听
     */
    private EventHandler ev = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                //提交验证码成功,如果验证成功会在data里返回数据。data数据类型为HashMap<number,code>
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Log.e("TAG", "提交验证码成功" + data.toString());
                    HashMap<String, Object> mData = (HashMap<String, Object>) data;
                    String country = (String) mData.get("country");//返回的国家编号
                    String phone = (String) mData.get("phone");//返回用户注册的手机号

                    Log.e("TAG", country + "====" + phone);

                    if (phone.equals(phoneNumber)) {
                        runOnUiThread(new Runnable() {//更改ui的操作要放在主线程，实际可以发送hander
                            @Override
                            public void run() {
                                showDailog("恭喜你！通过验证");
                                dialog.dismiss();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDailog("验证失败");
                                dialog.dismiss();
                            }
                        });
                    }

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                    Log.e("TAG", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表

                }
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };

    private void showDailog(String text) {
        new AlertDialog.Builder(this)
                .setTitle(text)
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_getCheckCode:
                toast("getCode");
                getCheckCode();
                break;
            case R.id.tv_sendCheckCode:
                toast("sendCode");
                sendCheckCode();
                break;
        }
    }

    /**
     * 获取验证码
     */
    public void getCheckCode() {
        phoneNumber = et_number.getText().toString().trim();
        //发送短信，传入国家号和电话号码
        if (TextUtils.isEmpty(phoneNumber)) {
            toast("号码不能为空！");
        } else {
            SMSSDK.getVerificationCode("+86", phoneNumber);
            toast("发送成功!");
        }
    }

    /**
     * 向服务器提交验证码，在监听回调中监听是否验证
     */
    private void sendCheckCode() {
        checkCode = et_checkCode.getText().toString();
        if (!TextUtils.isEmpty(checkCode)) {
            dialog = ProgressDialog.show(this, null, "正在验证...", false, true);
            //提交短信验证码
            SMSSDK.submitVerificationCode("+86", phoneNumber, checkCode);//国家号，手机号码，验证码
            Toast.makeText(this, "提交了注册信息:" + phoneNumber, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Toast
     * @param info
     */
    public void toast(String info){
        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterEventHandler(ev);
        super.onDestroy();
    }
}
