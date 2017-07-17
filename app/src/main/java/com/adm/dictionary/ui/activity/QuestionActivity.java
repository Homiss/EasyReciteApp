package com.adm.dictionary.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adm.dictionary.R;
import com.adm.dictionary.core.Html5WebView;
import com.adm.dictionary.entity.QuestionBean;
import com.adm.dictionary.http.HttpMethods;
import com.adm.dictionary.ui.BaseActivity;
import com.adm.dictionary.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 本地词典Activity
 * Created by Administrator on 2016/10/27.
 */
public class QuestionActivity extends BaseActivity {

    private String userId, token;
    private String groupId, groupName;
    private Boolean hasAdd;

    private TextView titleTv;
    private Button addBtn, removeBtn;
    private List<QuestionBean> list;
    private WebView mWebView;
    private LinearLayout webLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        findId();
        initListener();
        initData();
    }

    public void findId() {
        titleTv = findTextViewById(R.id.act_que_title);
        addBtn = findButById(R.id.act_que_add_btn);
        removeBtn = findButById(R.id.act_que_remove_btn);
        webLayout = findLinById(R.id.act_question_web);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mWebView = new Html5WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        webLayout.addView(mWebView);
        mWebView.loadUrl("http://wyx.gege5.cn/pages/show_all.html");
    }

    private void initListener() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroupToMine();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGroupFromMine();
            }
        });
    }

    private void initData() {
        SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);

        groupId = (String) getIntent().getSerializableExtra("groupId");
        groupName = (String) getIntent().getSerializableExtra("groupName");

        titleTv.setText(groupName);
        if (!HttpUtil.isNetworkAvailable(this)) {
            showToast("当前网络不可用,加载信息失败");
        } else {
            HttpMethods.getInstance().getQuestionListByGroupId(userId, token, groupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(final ResponseBody res) {
                    try {
                        JSONObject obj = new JSONObject(res.string());
                        if (obj.getBoolean("success")) {
                            list = new ArrayList<>();
                            hasAdd = obj.getJSONObject("data").getBoolean("hasAdd"); // 当前用户是否已添加当前题库
                            JSONArray items = obj.getJSONObject("data").getJSONArray("list");
                            for (int i = 0; i < items.length(); i++) {
                                QuestionBean bean = new QuestionBean();
                                bean.setQuestion(items.getJSONObject(i).getString("question"));
                                bean.setAnswer(items.getJSONObject(i).getString("answer"));
                                list.add(bean);
                            }
                            setUpData();
                        } else {
                            if (obj.getString("returnCode").equals("403")) { // 跳转到登录界面
                                Intent intent = new Intent(getApplication(), LoginAndRegistActivity.class);
                                startActivity(intent);
                                return;
                            }
                            showToast("出错了～");
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
    }

    /**
     * 获取数据绑定到控件上
     */
    private void setUpData() {
        initWebViewClient();
        if (hasAdd) {
            removeBtn.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.GONE);
        } else {
            removeBtn.setVisibility(View.GONE);
            addBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {

            //页面开始加载时
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //页面完成加载时
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(list != null && list.size() != 0){
                    for(QuestionBean q : list){
                        String h5 = "<h3 id=\"" + q.getQuestion() + "\">" + q.getQuestion() + "</h3>" + q.getAnswer();
                        mWebView.loadUrl("javascript:actionFromNativeWithParam(" + "'" + h5 + "'" + ")");
                    }
                }
            }

            //网络错误时回调的方法
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                /**
                 * 在这里写网络错误时的逻辑,比如显示一个错误页面
                 *
                 * 这里我偷个懒不写了
                 * */
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }

    private void addGroupToMine() {
        HttpMethods.getInstance().addGroupToMine(userId, token, groupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(final ResponseBody res) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(res.string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (obj.optBoolean("success")) {
                    showToast(obj.optString("data"));
                    removeBtn.setVisibility(View.VISIBLE);
                    addBtn.setVisibility(View.GONE);
                    hasAdd = true;
                } else {
                    if (obj.optString("returnCode").equals("403")) { // 跳转到登录界面
                        Intent intent = new Intent(getApplication(), LoginAndRegistActivity.class);
                        startActivity(intent);
                        return;
                    }
                    showToast("出错了～");
                    return;
                }

            }
        });
    }

    private void removeGroupFromMine() {
        HttpMethods.getInstance().removeGroupFromMine(userId, token, groupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(final ResponseBody res) {
                JSONObject obj = null;
                try {
                    obj = new JSONObject(res.string());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (obj.optBoolean("success")) {
                    showToast(obj.optString("data"));
                    removeBtn.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                    hasAdd = false;
                } else {
                    if (obj.optString("returnCode").equals("403")) { // 跳转到登录界面
                        Intent intent = new Intent(getApplication(), LoginAndRegistActivity.class);
                        startActivity(intent);
                        return;
                    }
                    showToast("出错了～");
                    return;
                }
            }
        });
    }



}
