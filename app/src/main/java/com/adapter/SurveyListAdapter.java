package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activities.CountTypeActivity;
import com.activities.ScanScreenActivity;
import com.bean.SurveyItem;
import com.count.R;
import com.utility.Pref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rahul on 5/14/2015.
 */
public class SurveyListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SurveyItem> surveyItemsList;
    private LayoutInflater inflater;
    private Pref _pref;
    private SimpleDateFormat format;

    public SurveyListAdapter(Context context, ArrayList<SurveyItem> surveyItemsList){
        this.context = context;
        this.surveyItemsList = surveyItemsList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _pref = new Pref(context);
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
    }

    @Override
    public int getCount() {
        return surveyItemsList.size();
    }

    @Override
    public SurveyItem getItem(int i) {
        return surveyItemsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        try {

                rowView = inflater.inflate(R.layout.survey_list_layout, null);
                ItemHolder holder = new ItemHolder();
                holder.llBtnSurvey = (LinearLayout) rowView.findViewById(R.id.llBtnSurvey);
                holder.tvSurveyTime = (TextView) rowView.findViewById(R.id.tvSurveyTime);
                holder.tvSurveyTitle = (TextView) rowView.findViewById(R.id.tvSurveyTitle);
                holder.ivSurveyStatusIcon = (ImageView) rowView.findViewById(R.id.ivSurveyStatusIcon);
                rowView.setTag(holder);


            ItemHolder newHolder = (ItemHolder) rowView.getTag();
            /*if (getItem(i).getSurveyStatus().equals("done")) {
                newHolder.llBtnSurvey.setBackground(context.getResources().
                        getDrawable(R.drawable.shadow_background_complete));
                newHolder.llBtnSurvey.setEnabled(false);
                newHolder.ivSurveyStatusIcon.setImageResource(R.drawable.tick_icon);
            }*/
            newHolder.tvSurveyTitle.setText(getItem(i).getSurveyName());

            //Date surveyTime = format.parse(getItem(i).getSurveyTime());
            //SimpleDateFormat printDate = new SimpleDateFormat("hh:MM");
            //String surveyTimeStr = printDate.format(surveyTime);
            newHolder.tvSurveyTime.setText(getItem(i).getSurveyTime());

            newHolder.llBtnSurvey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (getItem(i).getSurveyType().equals("daily")) {

                    Intent intent = new Intent(context, ScanScreenActivity.class);
                    intent.putExtra("scan_type", "count");
                    _pref.setSession("current_surveyid", getItem(i).getSurveyId());
                    context.startActivity(intent);

                } else {

                    Intent intent = new Intent(context, CountTypeActivity.class);
                    _pref.setSession("current_surveyid", getItem(i).getSurveyId());
                    intent.putExtra("instruction", getItem(i).getSurveyInstruction());
                    context.startActivity(intent);
                }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
        return rowView;
    }

    private class ItemHolder{
        LinearLayout llBtnSurvey;
        TextView tvSurveyTitle, tvSurveyTime;
        ImageView ivSurveyStatusIcon;
    }
}
