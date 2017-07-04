package com.adm.dictionary.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adm.dictionary.base.BaseActivity;
import com.adm.dictionary.bean.QuestionBean;
import com.adm.dictionary.http.HttpMethods;
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

public class Html5Activity extends BaseActivity {

    private String userId, token;
    private String mUrl;
    private String groupId;
    private long mOldTime;
    private int position, sumCount;
    private List<QuestionBean> list;

    private LinearLayout mLayout, modifyLayout;
    private TextView questionTv, labelTv;
    TextView modifyTv;
    EditText modifyText;
    Button modifyConfirm;
    private WebView mWebView;
    private Button remember, unremember, unshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            mUrl = bundle.getString("url");
        } else {
            mUrl = "https://homiss.github.io/";
        }

        modifyLayout = findLinById(R.id.act_h5_modyfy);
        mLayout = (LinearLayout) findViewById(R.id.act_h5_web);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .MATCH_PARENT);
        mWebView = new Html5WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);
        mWebView.loadUrl(mUrl);

        initView();
        getData();
        initWebViewClient();
    }

    public void initView() {
        groupId = getIntent().getStringExtra("groupId");

        modifyText = (EditText) findViewById(R.id.item_prac_modify);
        modifyTv = (TextView) findViewById(R.id.item_prac_modify_tv);
        modifyConfirm = (Button) findViewById(R.id.item_prac_modify_confirm);
        labelTv = (TextView) findViewById(R.id.act_h5_label);
        questionTv = (TextView) findViewById(R.id.act_h5_question);
        remember = (Button) findViewById(R.id.act_prac_remember);
        unremember = (Button) findViewById(R.id.act_prac_unremember);
        unshow = (Button) findViewById(R.id.act_prac_unshow);

        unshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position < list.size()) {
                    QuestionBean bean = list.get(position);
                    modifyReciteRecord(userId, token, bean.getId(), "-1");
                } else {
                    showToast("本次复习结束，继续努力");
                    finish();
                }
            }
        });

        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position++;
                if (position < list.size()) {
                    QuestionBean bean = list.get(position);
                    modifyReciteRecord(userId, token, bean.getId(), "1");
                } else {
                    showToast("本次复习结束，继续努力");
                    finish();
                }
            }
        });

        unremember.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                position++;
                if(position < list.size()){
                    QuestionBean bean = list.get(position);
                    modifyReciteRecord(userId, token, bean.getId(), "0");
                } else {
                    showToast("本次复习结束，继续努力");
                    finish();
                }
            }
        });

        CardView cardView = (CardView) findViewById(R.id.act_h5_cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionTv.setGravity(Gravity.CENTER_HORIZONTAL);
                questionTv.setHeight(100);
                mLayout.setVisibility(View.VISIBLE);
                modifyTv.setVisibility(View.VISIBLE);
            }
        });
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionTv.setGravity(Gravity.CENTER_HORIZONTAL);
                questionTv.setHeight(100);
                mLayout.setVisibility(View.VISIBLE);
                modifyTv.setVisibility(View.VISIBLE);
            }
        });


        modifyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setVisibility(View.GONE);
                questionTv.setVisibility(View.GONE);
                // questionTvTmp.setVisibility(View.GONE);
                modifyText.setText(list.get(position).getAnswer());
                modifyLayout.setVisibility(View.VISIBLE);
            }
        });

        modifyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setVisibility(View.VISIBLE);
                questionTv.setGravity(Gravity.CENTER_HORIZONTAL);
                modifyLayout.setVisibility(View.GONE);
                modifyAnswer(position, modifyText.getText().toString());
            }
        });
    }

    private void getData(){
        SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);

        if(!HttpUtil.isNetworkAvailable(this)){
            showToast("当前网络不可用,加载信息失败");
        } else {
            HttpMethods.getInstance().todayReciteTask(userId, token, groupId, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(final ResponseBody responseBody) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(responseBody.string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(jsonObj.optBoolean("success")){
                        JSONArray items = jsonObj.optJSONObject("data").optJSONArray("list");
                        List<QuestionBean> questionBeenList = convertToQuestionBean(items);
                        list = questionBeenList;
                        sumCount = list.size();
                    } else {
                        showToast("出错了～");
                        finish();
                    }
                }
            });
        }
    }

    private void modifyReciteRecord(String userId, String token, Long id, String status) {
        questionTv.setVisibility(View.VISIBLE);
        questionTv.setGravity(Gravity.CENTER);
        mLayout.setVisibility(View.GONE);
        questionTv.setText(list.get(position).getQuestion());
        mWebView.loadUrl("javascript:actionFromNativeWithParam(" + "'" + list.get(position).getAnswer() + "'" + ")");
        labelTv.setText("正在背题(" + (position + 1) + "/" + sumCount + ")");
        HttpMethods.getInstance().modifyReciteRecord(userId, token, id, status).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(final ResponseBody res) {
                try {
                    JSONObject obj = new JSONObject(res.string());
                    if(obj.getBoolean("success")){

                    } else {
                        if(obj.getString("returnCode").equals("403")){ // 跳转到登录界面
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

    private List<QuestionBean> convertToQuestionBean(JSONArray items) {
        List<QuestionBean> questionBeens = new ArrayList<>();
        for(int i = 0; i < items.length(); i++){
            QuestionBean questionBean = new QuestionBean();
            questionBean.setId(items.optJSONObject(i).optLong("id"));
            questionBean.setQuestionId(items.optJSONObject(i).optInt("questionId"));
            questionBean.setQuestion(items.optJSONObject(i).optString("question"));
            questionBean.setAnswer(items.optJSONObject(i).optString("answer"));
            questionBean.setLevel(items.optJSONObject(i).optInt("level"));
            questionBean.setStrange(items.optJSONObject(i).optInt("strange"));
            questionBean.setGroupName("默认分组");
            questionBeens.add(questionBean);
        }
        return questionBeens;
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
                labelTv.setText("正在背题(" + (position + 1) + "/" + sumCount + ")");
                questionTv.setText(list.get(position).getQuestion());
                mWebView.loadUrl("javascript:actionFromNativeWithParam(" + "'" + list.get(position).getAnswer() + "'" + ")");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mOldTime < 1500) {
                mWebView.clearHistory();
                mWebView.loadUrl(mUrl);
            } else if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                Html5Activity.this.finish();
            }
            mOldTime = System.currentTimeMillis();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private void modifyAnswer(int position, String answer) {
        HttpMethods.getInstance().modifyAnswer(userId, token, list.get(position).getQuestionId(), answer).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(final ResponseBody res) {

            }
        });
    }

}