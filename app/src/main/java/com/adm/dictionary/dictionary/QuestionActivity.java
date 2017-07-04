package com.adm.dictionary.dictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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

/**
 * 本地词典Activity
 * Created by Administrator on 2016/10/27.
 */
public class QuestionActivity extends BaseActivity {

    private String userId, token;
    private String groupId, groupName;
    private Boolean hasAdd;

    private ListView lv;
    private TextView titleTv;
    private Button addBtn, removeBtn;
    private List<QuestionBean> list;
    private DictAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initView();
        getData();
    }

    @Override
    public void initView() {
        titleTv = findTextViewById(R.id.act_que_title);
        lv = (ListView) findViewById(R.id.act_que_lv);
        addBtn = findButById(R.id.act_que_add_btn);
        removeBtn = findButById(R.id.act_que_remove_btn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBtn.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.GONE);
                hasAdd = true;
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBtn.setVisibility(View.GONE);
                addBtn.setVisibility(View.VISIBLE);
                hasAdd = false;
            }
        });
    }

    private void getData() {
        SharedPreferences userInfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);

        groupId = (String) getIntent().getSerializableExtra("groupId");
        groupName = (String) getIntent().getSerializableExtra("groupName");

        titleTv.setText(groupName);
        if(!HttpUtil.isNetworkAvailable(this)){
            showToast("当前网络不可用,加载信息失败");
        } else {
            HttpMethods.getInstance().getQuestionListByGroupId(userId, token, groupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(final ResponseBody res) {
                    try {
                        JSONObject obj = new JSONObject(res.string());
                        if(obj.getBoolean("success")){
                            list = new ArrayList<>();
                            hasAdd = obj.getJSONObject("data").getBoolean("hasAdd"); // 当前用户是否已添加当前题库
                            JSONArray items = obj.getJSONObject("data").getJSONArray("list");
                            for(int i = 0; i < items.length(); i++){
                                QuestionBean bean = new QuestionBean();
                                bean.setQuestion(items.getJSONObject(i).getString("question"));
                                bean.setAnswer(items.getJSONObject(i).getString("answer"));
                                list.add(bean);
                            }
                            setUpData();
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


    }


    /**
     * 获取数据绑定到控件上
     */
    private void setUpData() {
        adapter = new DictAdapter();
        lv.setAdapter(adapter);
        if(hasAdd){
            removeBtn.setVisibility(View.VISIBLE);
            addBtn.setVisibility(View.GONE);
        } else {
            removeBtn.setVisibility(View.GONE);
            addBtn.setVisibility(View.VISIBLE);
        }
    }

    class DictAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder vh = null;
            if (v == null) {
                v = View.inflate(QuestionActivity.this, R.layout.item_question, null);
                vh = new ViewHolder();
                vh.question = (TextView) v.findViewById(R.id.item_record_question);
                // vh.answer = (TextView) v.findViewById(R.id.item_record_answer);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
            }
            vh.question.setText(list.get(position).getQuestion());
            // vh.answer.setText(list.get(position).getAnswer());
            return v;
        }

        class ViewHolder {
            TextView question, answer;
        }
    }
}
