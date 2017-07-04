package com.adm.dictionary.dictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adm.dictionary.base.BaseActivity;
import com.adm.dictionary.bean.QuestionBean;
import com.adm.dictionary.http.HttpMethods;
import com.adm.dictionary.view.CardPagerAdapter;
import com.adm.dictionary.view.ShadowTransformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 复习单词的界面
 * Created by Homiss on 2017/6/13.
 */
public class PlanPracticeActivity extends BaseActivity {

    private String userId, token;

    private ViewPager vp;
    private CardPagerAdapter adapter;
    private ShadowTransformer transformer;
    private List<QuestionBean> list;
    private String groupId;
    private int position, reciteCount = 1, sumCount;
    private TextView labelTv;
    private Button remember, unremember, unshow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_practice);
        groupId = getIntent().getStringExtra("groupId");
        initView();
        initData();
    }

    private void initData() {
        SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);
        HttpMethods.getInstance().todayReciteTask(userId, token, groupId, 20).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("error", e.getLocalizedMessage());
            }

            @Override
            public void onNext(ResponseBody responseBody) {
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
                    labelTv.setText("正在背题(" + reciteCount + "/" + sumCount + ")");
                    adapter = new CardPagerAdapter(userId, token, list);
                    transformer = new ShadowTransformer(vp, adapter);
                    transformer.enableScaling(true);
                    vp.setCurrentItem(0, false);
                    vp.setAdapter(adapter);
                    vp.setPageTransformer(false, transformer);
                } else {
                    showToast("出错了～");
                    finish();
                }

            }
        });
    }

    @Override
    public void initView() {

        vp = (ViewPager) findViewById(R.id.act_prac_vp);

        labelTv = findTextViewById(R.id.act_prac_label);

        remember = findButById(R.id.act_prac_remember);
        unremember = findButById(R.id.act_prac_unremember);
        unshow = findButById(R.id.act_prac_unshow);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SharedPreferences userInfo = getSharedPreferences("reciteRecord", MODE_PRIVATE);
                SharedPreferences.Editor editor = userInfo.edit();
                editor.putString("lastPosition", String.valueOf(position));
                editor.commit();
                PlanPracticeActivity.this.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        unshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != list.size() - 1) {
                    vp.setCurrentItem(position);
                    updateAdapter();
                    reciteCount++;
                    QuestionBean bean = list.get(position);
                    modifyReciteRecord(userId, token, bean.getId(), "-1");
                } else {
                    // 最后一个界面的时候 关闭
                    showToast("本次复习结束，继续努力");
                    finish();
                }

            }
        });

        remember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != list.size() - 1) {
                    vp.setCurrentItem(position);
                    updateAdapter();

                    reciteCount++;
                    QuestionBean bean = list.get(position);
                    modifyReciteRecord(userId, token, bean.getId(), "1");
                } else {
                    // 最后一个界面的时候 关闭
                    showToast("本次复习结束，继续努力");
                    finish();
                }
            }
        });

        unremember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != list.size() - 1) {
                    vp.setCurrentItem(position);
                    updateAdapter();

                    reciteCount++;
                    QuestionBean bean = list.get(position);
                    modifyReciteRecord(userId, token, bean.getId(), "0");
                } else {
                    showToast("本次复习结束，继续努力");
                    finish();
                }
            }
        });
    }

    private void modifyReciteRecord(String userId, String token, Long id, String status) {
        labelTv.setText("正在背题(" + reciteCount + "/" + sumCount + ")");
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

    public void updateAdapter(){
        list.remove(position);
        adapter = new CardPagerAdapter(userId, token, list);
        transformer = new ShadowTransformer(vp, adapter);
        transformer.enableScaling(true);
        vp.setAdapter(adapter);
        vp.setPageTransformer(false, transformer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(adapter != null) adapter.releaseMp();
    }
}
