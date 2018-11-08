package com.nxnd.travelnote.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.squareup.okhttp.Request;
import com.nxnd.travelnote.R;
import com.nxnd.travelnote.Url;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by huchuan 登录
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_phone) EditText userName;
    @BindView(R.id.login_password) EditText userPwd;
    @BindView(R.id.login_login) Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_login)
    public void onCLickLogin() {
        if (userName.getText().toString().length() != 11) {
            Toast.makeText(LoginActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPwd.getText().toString().trim().equals("")) {
            Toast.makeText(LoginActivity.this, "用户密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userName.getText().toString().equals("11111111111")&&userPwd.getText().toString().equals("test")) {
            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences("user",
                    Activity.MODE_PRIVATE);
            //实例化SharedPreferences.Editor对象
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //用putString的方法保存数据
            editor.putString("userphone", userName.getText().toString());
            editor.putString("pwd", userPwd.getText().toString());
            editor.putString("username", "测试");
            //提交当前数据
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
        }
//rxjava + retrofit2
//        String url = Url.url + "login";
//        OkHttpUtils
//                .get()
//                .url(url)
//                .addParams("phone", userName.getText().toString().trim())
//                .addParams("pwd", userPwd.getText().toString().trim())
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//                        //Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                        String code = "100";
//                        String name = "test";
//                        Log.i("lin", "---lin's log--->   name    " + name);
//                        if (code.equals("100")) {
//                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                            SharedPreferences sharedPreferences = getSharedPreferences("user",
//                                    Activity.MODE_PRIVATE);
//                            //实例化SharedPreferences.Editor对象
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            //用putString的方法保存数据
//                            editor.putString("userphone", userName.getText().toString());
//                            editor.putString("pwd", userPwd.getText().toString());
//                            editor.putString("username", name);
//                            //提交当前数据
//                            editor.apply();
//
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
////                        String code = response.split(",")[0];
////                        String name = response.split(",")[1];
//                        String code = "100";
//                        String name = "test";
//                        Log.i("lin", "---lin's log--->   name    " + name);
//                        if (code.equals("100")) {
//                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//                            SharedPreferences sharedPreferences = getSharedPreferences("user",
//                                    Activity.MODE_PRIVATE);
//                            //实例化SharedPreferences.Editor对象
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            //用putString的方法保存数据
//                            editor.putString("userphone", userName.getText().toString());
//                            editor.putString("pwd", userPwd.getText().toString());
//                            editor.putString("username", name);
//                            //提交当前数据
//                            editor.apply();
//
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                });

    }

    @OnClick(R.id.login_reg)
    public void onCLickRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
