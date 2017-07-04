package com.adm.dictionary.dictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adm.dictionary.base.BaseActivity;
import com.adm.dictionary.http.HttpMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by homiss on 2017/6/14.
 */

public class LoginAndRegistActivity extends BaseActivity {

    private LinearLayout loginLin, registLin, forgetLin;
    private EditText loginPhoneEt, loginPasswordEt,
            registPhoneEt, registPasswordEt, registCodeEt,
            forgetPhoneEt, forgetPassword, forgetCodeEt;
    private TextView loginBtn1, registBtn1, forgetBtn1, loginBtn2, registBtn2, forgetBtn2;
    private Button loginSubmitBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_regist);
        initView();

        initData();
    }

    @Override
    public void initView() {
        loginLin = findLinById(R.id.act_login);
        registLin = findLinById(R.id.act_regist);
        forgetLin = findLinById(R.id.act_forget);

        loginBtn1 = findTextViewById(R.id.act_login_btn1);
        registBtn1 = findTextViewById(R.id.act_regist_btn1);
        forgetBtn1 = findTextViewById(R.id.act_forget_btn1);
        loginBtn2 = findTextViewById(R.id.act_login_btn2);
        registBtn2 = findTextViewById(R.id.act_regist_btn2);
        forgetBtn2 = findTextViewById(R.id.act_forget_btn2);

        loginSubmitBtn = findButById(R.id.act_login_submit);

        loginPhoneEt = findEtnById(R.id.act_login_phone);
        loginPasswordEt = findEtnById(R.id.act_login_password);
        registPhoneEt = findEtnById(R.id.act_regist_phone);
        registPasswordEt = findEtnById(R.id.act_regist_password);
        registCodeEt = findEtnById(R.id.act_regist_code);
        forgetPhoneEt = findEtnById(R.id.act_forget_phone);
        forgetPassword = findEtnById(R.id.act_forget_password);
        forgetCodeEt = findEtnById(R.id.act_forget_code);

        addClickEvent();
    }

    private void initData() {
        SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        String phone = userInfo.getString("phone", null);
        if(phone != null) loginPhoneEt.setText(phone);
    }

    private void addClickEvent() {

        loginSubmitBtn.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                String phone = loginPhoneEt.getText().toString();
                String password = loginPasswordEt.getText().toString();
                HttpMethods.getInstance().login(phone, password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(final ResponseBody res) {
                        try {
                            JSONObject obj = new JSONObject(res.string());
                            if(obj.getBoolean("success")){
                                JSONObject data = obj.getJSONObject("data");
                                String userId = data.getString("userId");
                                String name = data.getString("name");
                                String headpic = data.getString("headpic");
                                String phone = data.getString("phone");
                                String token = data.getString("token");
                                SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
                                SharedPreferences.Editor editor = userInfo.edit();
                                editor.putString("userId", userId);
                                editor.putString("phone", phone);
                                editor.putString("name", name);
                                editor.putString("headpic", headpic);
                                editor.putString("token", token);
                                editor.commit();
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast(obj.optString("error"));
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        loginBtn1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                loginLin.setVisibility(View.VISIBLE);
                registLin.setVisibility(View.GONE);
                forgetLin.setVisibility(View.GONE);
            }
        });

        loginBtn2.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                loginLin.setVisibility(View.VISIBLE);
                registLin.setVisibility(View.GONE);
                forgetLin.setVisibility(View.GONE);
            }
        });

        registBtn1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                loginLin.setVisibility(View.GONE);
                registLin.setVisibility(View.VISIBLE);
                forgetLin.setVisibility(View.GONE);
            }
        });

        registBtn2.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                loginLin.setVisibility(View.GONE);
                registLin.setVisibility(View.VISIBLE);
                forgetLin.setVisibility(View.GONE);
            }
        });

        forgetBtn1.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                loginLin.setVisibility(View.GONE);
                registLin.setVisibility(View.GONE);
                forgetLin.setVisibility(View.VISIBLE);
            }
        });

        forgetBtn2.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                loginLin.setVisibility(View.GONE);
                registLin.setVisibility(View.GONE);
                forgetLin.setVisibility(View.VISIBLE);
            }
        });
    }
}
