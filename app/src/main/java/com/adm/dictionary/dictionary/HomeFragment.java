package com.adm.dictionary.dictionary;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.adm.dictionary.base.BaseFragment;
import com.adm.dictionary.bean.Setting;
import com.adm.dictionary.http.HttpMethods;
import com.adm.dictionary.util.HttpUtil;
import com.zhl.cbdialog.CBDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;


/**
 * 推荐Fragment
 * Created by Administrator on 2016/10/18.
 */
public class HomeFragment extends BaseFragment {

    private String userId, token;
    private JSONArray groupsArray;
    private String[] groups;
    private Integer currentGroupId;
    private Integer currentPosition;

    private ViewPager viewpager;
    private Dialog dialog;
    private View v;
    // 继续背题 按键
    private Button reciteBtn;
    private TextView groupNameTv;
    private TextView sumCountTv;
    private TextView modifyGroupTv;

    private Setting setting;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_home, null);
        initView();
        getData();
        return v;
    }

    @Override
    public void initView() {
        reciteBtn = findButById(v, R.id.frag_home_recite);

        groupNameTv = findTextViewbyId(v, R.id.frag_home_group_name);
        sumCountTv = findTextViewbyId(v, R.id.frag_home_sum_count);
        modifyGroupTv = findTextViewbyId(v, R.id.frag_home_modifygroup);

        reciteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HttpUtil.isNetworkAvailable(getActivity())) {
                    showToast("当前网络不可用");
                } else {
                    Intent intent = new Intent(getActivity(), Html5Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", "http://wyx.gege5.cn/pages/test.html"); // wyx.gege5.cn/pages/test.html
                    bundle.putString("groupId", String.valueOf(currentGroupId));
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            }
        });

        modifyGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HttpUtil.isNetworkAvailable(getActivity())) {
                    showToast("当前网络不可用");
                } else {
                    getMineGroups();
                }
            }
        });

    }

    private void getMineGroups() {
        HttpMethods.getInstance().getMineQuestionGroups(userId, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
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
                    System.out.println(res);
                    groupsArray = obj.optJSONArray("data");
                    groups = new String[groupsArray.length()];
                    for(int i = 0; i < groupsArray.length(); i++){
                        groups[i] = groupsArray.optJSONObject(i).optString("name");
                        if(groupsArray.optJSONObject(i).optInt("id") == currentGroupId){
                            currentPosition = i;
                        }
                    }
                    initDialog();
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

    private void getData() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("userinfo", MODE_PRIVATE);
        userId = userInfo.getString("userId", null);
        token = userInfo.getString("token", null);

        if (!HttpUtil.isNetworkAvailable(getActivity())) {
            showToast("当前网络不可用,加载信息失败");
        } else {
            HttpMethods.getInstance().getSetting(userId, token).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
                @Override
                public void call(final ResponseBody res) {
                    try {
                        JSONObject obj = new JSONObject(res.string());
                        if (obj.getBoolean("success")) {
                            setting = new Setting();
                            setting.setGroupId(obj.optJSONObject("data").optInt("groupId"));
                            setting.setGroupName(obj.optJSONObject("data").optString("groupName"));
                            setting.setReciteModel(obj.optJSONObject("data").optInt("reciteModel"));
                            setting.setReciteNum(obj.optJSONObject("data").optInt("reciteNum"));
                            setting.setSumCount(obj.optJSONObject("data").optInt("sumCount"));
                            setting.setHasReciteCount(obj.optJSONObject("data").optInt("hasReciteCount"));
                            refresh();
                        } else {
                            if (obj.getString("returnCode").equals("403")) { // 跳转到登录界面
                                Intent intent = new Intent(getContext(), LoginAndRegistActivity.class);
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

    private void initDialog() {
        dialog = new CBDialogBuilder(getContext(), CBDialogBuilder.DIALOG_STYLE_NORMAL)
                .setTitle("题库更换")
                .setItems(groups, new CBDialogBuilder.onDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(CBDialogBuilder.DialogItemAdapter ItemAdapter, Context context, CBDialogBuilder dialogbuilder, Dialog dialog, int position) {
                        currentPosition = position;
                        groupNameTv.setText(groupsArray.optJSONObject(position).optString("name"));
                        currentGroupId = groupsArray.optJSONObject(position).optInt("id");
                        dialog.dismiss();
                        modifyReciteGroup();
                    }
                }, currentPosition)
                .showIcon(false).create();
        dialog.show();
    }

    private void modifyReciteGroup() {
        HttpMethods.getInstance().modifyReciteGroup(userId, token, currentGroupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
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

    private void refresh() {
        groupNameTv.setText(setting.getGroupName());
        currentGroupId = setting.getGroupId();
        sumCountTv.setText("今日待背：" + setting.getReciteNum() + "道");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setViewpager(ViewPager viewpager) {
        this.viewpager = viewpager;
    }
}
