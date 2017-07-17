package com.adm.dictionary.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adm.dictionary.R;
import com.adm.dictionary.http.HttpMethods;
import com.adm.dictionary.ui.BaseFragment;
import com.adm.dictionary.ui.activity.LoginAndRegistActivity;
import com.adm.dictionary.ui.fragment.dummy.DummyContent;

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
 * A fragment representing a list of Items.
 * <p/>
 */
public class QuestionItemFragment extends BaseFragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private View view;
    private String userId, token;
    private String[] questions;

    public QuestionItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_mine_item_list, container, false);
        initData();
        return view;
    }

    private void setAdapter() {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            List<DummyContent.DummyItem> items = new ArrayList<DummyContent.DummyItem>();
            for (int i = 1; i <= questions.length; i++) {
                items.add(new DummyContent.DummyItem(String.valueOf(i), questions[i - 1], "hahah"));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(items));
        }
    }

    private void initData() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);
        getReciteQuestions();
    }

    private void getReciteQuestions() {
        HttpMethods.getInstance().getReciteQuestions(userId, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
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
                    JSONArray itemsArray = obj.optJSONArray("data");
                    questions = new String[itemsArray.length()];
                    for(int i = 0; i < itemsArray.length(); i++){
                        questions[i] = itemsArray.optJSONObject(i).optString("question");
                    }
                    setAdapter();
                } else {
                    if (obj.optString("returnCode").equals("403")) { // 跳转到登录界面
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
