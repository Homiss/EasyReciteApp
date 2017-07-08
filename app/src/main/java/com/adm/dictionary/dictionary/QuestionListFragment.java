package com.adm.dictionary.dictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.adm.dictionary.base.BaseFragment;
import com.adm.dictionary.bean.PlanCount;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * 发现Fragment
 * Created by Administrator on 2016/10/18.
 */
public class QuestionListFragment extends BaseFragment {
    private String userId, token;

    private ListView lv;
    private View v;
    private JSONArray groups;
    private List<String> plans;
    private List<PlanCount> counts;
    private QuestionListFragment.PlanAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_question_list, null);
        initView();
        getData();
        return v;
    }

    @Override
    public void initView() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);
        lv = (ListView) v.findViewById(R.id.act_plan_lv);
        TextView emptyView = new TextView(getContext());
        ((ViewGroup) lv.getParent()).addView(emptyView);
        lv.setEmptyView(emptyView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = groups.optJSONObject(position);
                Intent intent=new Intent(getContext(), QuestionActivity.class);
                intent.putExtra("groupId",obj.optString("id"));
                intent.putExtra("groupName",obj.optString("name"));
                startActivity(intent);
            }
        });
    }

    private void getData() {
        if(!HttpUtil.isNetworkAvailable(getActivity())){
            showToast("当前网络不可用");
        } else {
            HttpMethods.getInstance().getQuestionGroups(userId, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
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
                    if(obj.optBoolean("success")){
                        System.out.println(res);
                        groups = obj.optJSONArray("data");
                        setUpData();
                    } else {
                        if(obj.optString("returnCode").equals("403")){ // 跳转到登录界面
                            Intent intent = new Intent(getContext(), LoginAndRegistActivity.class);
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

    private void setUpData() {
        plans = new ArrayList<>();
        counts = new ArrayList<>();
        if (groups != null) {
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = null;
                group = groups.optJSONObject(i);
                plans.add(group.optString("name"));
                long a = group.optInt("sumCount");
                long b = group.optInt("hasReciteCount");
                counts.add(new PlanCount(a, b));
            }
        }
        adapter = new PlanAdapter();
        lv.setAdapter(adapter);
    }

    class PlanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return plans.size();
        }

        @Override
        public Object getItem(int position) {
            return plans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            QuestionListFragment.PlanAdapter.ViewHolder vh = null;

            if (v == null) {
                v = View.inflate(getActivity(), R.layout.item_plan, null);
                vh = new QuestionListFragment.PlanAdapter.ViewHolder();
                vh.cv = (CardView) v.findViewById(R.id.item_plan_cv);
                vh.name = (TextView) v.findViewById(R.id.item_question_name);
                vh.des = (TextView) v.findViewById(R.id.item_question_num);
                v.setTag(vh);
            } else {
                vh = (QuestionListFragment.PlanAdapter.ViewHolder) v.getTag();
            }

            vh.name.setText(plans.get(position));
            vh.des.setText("共" + counts.get(position).getTotal() + "个，已完成" + counts.get(position).getDone() + "个");
            return v;
        }

        class ViewHolder {
            TextView name, des;
            CardView cv;
        }
    }


}
