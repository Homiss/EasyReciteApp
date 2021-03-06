package com.adm.dictionary.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.adm.dictionary.R;
import com.adm.dictionary.core.WaveHelper;
import com.adm.dictionary.entity.Setting;
import com.adm.dictionary.http.HttpMethods;
import com.adm.dictionary.ui.BaseFragment;
import com.adm.dictionary.ui.activity.Html5Activity;
import com.adm.dictionary.ui.activity.LoginAndRegistActivity;
import com.adm.dictionary.util.HttpUtil;
import com.gelitenight.waveview.library.WaveView;
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

    private WaveHelper mWaveHelper;

    private String userId, token;
    private JSONArray groupsArray;
    private String[] groups;
    private Integer currentGroupId;
    private Integer currentGroupPosition;
    private Integer currentReciteModelPosition;

    private ViewPager viewpager;
    private Dialog dialog;
    private View v;
    private Button reciteBtn;
    private TextView groupNameTv;
    private TextView sumCountTv;
    private TextView modifyGroupTv;
    private TextView modifyReciteModelTv;

    private Setting setting;

    private WaveView waveView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_home, null);
        findId();
        initListener();
        initData();
        return v;
    }

    private void findId() {
        waveView = (WaveView) v.findViewById(R.id.wave);
        reciteBtn = findButById(v, R.id.frag_home_recite);
        groupNameTv = findTextViewbyId(v, R.id.frag_home_group_name);
        sumCountTv = findTextViewbyId(v, R.id.frag_home_sum_count);
        modifyGroupTv = findTextViewbyId(v, R.id.frag_home_modifygroup);
        modifyReciteModelTv = findTextViewbyId(v, R.id.frag_home_recitemodel);
    }

    public void initListener() {
        reciteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HttpUtil.isNetworkAvailable(getActivity())) {
                    showToast("当前网络不可用");
                } else {
                    Intent intent = new Intent(getActivity(), Html5Activity.class);
                    Bundle bundle = new Bundle();
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
        modifyReciteModelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HttpUtil.isNetworkAvailable(getActivity())) {
                    showToast("当前网络不可用");
                } else {
                    recitewayDialog();
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
                    groupsArray = obj.optJSONArray("data");
                    groups = new String[groupsArray.length()];
                    for(int i = 0; i < groupsArray.length(); i++){
                        groups[i] = groupsArray.optJSONObject(i).optString("name");
                        if(groupsArray.optJSONObject(i).optInt("id") == currentGroupId){
                            currentGroupPosition = i;
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

    private void initData() {
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
                            currentReciteModelPosition = setting.getReciteModel();
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
                        currentGroupPosition = position;
                        groupNameTv.setText(groupsArray.optJSONObject(position).optString("name"));
                        currentGroupId = groupsArray.optJSONObject(position).optInt("id");
                        dialog.dismiss();
                        modifyReciteGroup();
                    }
                }, currentGroupPosition)
                .showIcon(false).create();
        dialog.show();
    }

    private void recitewayDialog() {
        dialog = new CBDialogBuilder(getContext(), CBDialogBuilder.DIALOG_STYLE_NORMAL)
                .setTitle("背题方式更换")
                .setItems(new String[]{"顺序背题", "随机背题"}, new CBDialogBuilder.onDialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(CBDialogBuilder.DialogItemAdapter ItemAdapter, Context context, CBDialogBuilder dialogbuilder, Dialog dialog, int position) {
                        currentReciteModelPosition = position;
                        dialog.dismiss();
                        modifyReciteModel();
                    }
                }, currentReciteModelPosition)
                .showIcon(false).create();
        dialog.show();
    }

    private void modifyReciteModel() {
        HttpMethods.getInstance().modifyReciteModel(userId, token, currentReciteModelPosition).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
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
                    initData();
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
                    initData();
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
        refreshWave();
        groupNameTv.setText(setting.getGroupName());
        currentGroupId = setting.getGroupId();
        sumCountTv.setText("今日待背：" + setting.getReciteNum() + "道");
    }

    private void refreshWave(){
        waveView.setBorder(2, Color.parseColor("#FFFFFF"));
        waveView.setWaveColor(
                Color.parseColor("#b8f1ed"),
                Color.parseColor("#FFFFFF"));
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);
        float waveLength = setting.getHasReciteCount() * 1.0f / setting.getSumCount();
        mWaveHelper = new WaveHelper(waveView, waveLength);
        mWaveHelper.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    public void setViewpager(ViewPager viewpager) {
        this.viewpager = viewpager;
    }
}
