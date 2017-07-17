package com.adm.dictionary.ui.view;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adm.dictionary.R;
import com.adm.dictionary.entity.QuestionBean;
import com.adm.dictionary.http.HttpMethods;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private String userId, token;
    private List<CardView> mViews;
    private List<QuestionBean> mData;
    private float mBaseElevation;
    private Boolean isVisible = true;

    public CardPagerAdapter(String userId, String  token, List<QuestionBean> mData) {
        this.userId = userId;
        this.token = token;
        this.mData = mData;
        mViews = new ArrayList<>();
        for (int i = 0; i < mData.size(); i++) {
            mViews.add(null);
        }
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        initData(view, position);
        mViews.set(position, cardView);
        return view;
    }

    /**
     * 将数据添加到控件上
     *
     * @param view
     * @param position
     */
    private void initData(View view, final int position) {
        final TextView title = (TextView) view.findViewById(R.id.item_prac_name);
        final TextView meaning = (TextView) view.findViewById(R.id.item_prac_meaning);
        final EditText modifyText = (EditText) view.findViewById(R.id.item_prac_modify);
        final TextView modifyTv = (TextView) view.findViewById(R.id.item_prac_modify_tv);
        final Button modifyConfirm = (Button) view.findViewById(R.id.item_prac_modify_confirm);
        title.setText(mData.get(position).getQuestion());
        meaning.setText(mData.get(position).getAnswer());
        modifyText.setText(mData.get(position).getAnswer());
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisible){
                    title.setVisibility(View.VISIBLE);
                    meaning.setVisibility(View.VISIBLE);
                    modifyTv.setVisibility(View.VISIBLE);
                }
            }
        });
        meaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVisible){
                    title.setVisibility(View.VISIBLE);
                    meaning.setVisibility(View.VISIBLE);
                    modifyTv.setVisibility(View.VISIBLE);
                }
            }
        });

        modifyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaning.setVisibility(View.GONE);
                modifyText.setVisibility(View.VISIBLE);
                modifyTv.setVisibility(View.GONE);
                modifyConfirm.setVisibility(View.VISIBLE);
                isVisible = false;
            }
        });

        modifyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaning.setText(modifyText.getText());
                meaning.setVisibility(View.VISIBLE);
                modifyText.setVisibility(View.GONE);
                modifyTv.setVisibility(View.VISIBLE);
                modifyConfirm.setVisibility(View.GONE);
                isVisible = true;
                modifyAnswer(position, modifyText.getText().toString());
            }
        });
    }

    private void modifyAnswer(int position, String answer) {
        HttpMethods.getInstance().modifyAnswer(userId, token, mData.get(position).getQuestionId(), answer).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<ResponseBody>() {
            @Override
            public void call(final ResponseBody res) {

            }
        });
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }


    public void releaseMp() {
    }

}
