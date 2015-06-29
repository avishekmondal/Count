package com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.ScanScreenActivity;
import com.activities.ScheduleActivity;
import com.count.R;
import com.utility.Constant;
import com.utility.Pref;
import com.utility.ThemeSetter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Rahul on 4/28/2015.
 */
public class HomeFragment extends Fragment implements Animation.AnimationListener {

    private ThemeSetter _themeSetter;
    private Pref _pref;
    private RelativeLayout rlStartCount, rlFindItem, rlHomeMailLayout;
    private View rootView;
    private TextView textStart, textCount;

    private Animation moveDown, moveUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initialize();

        _themeSetter.setBodyColor(rlHomeMailLayout);
        rlStartCount.setAnimation(moveDown);
        rlFindItem.setAnimation(moveUp);

        onClick();

        return rootView;
    }

    private void initialize(){
        _pref = new Pref(getActivity());
        _themeSetter = new ThemeSetter(getActivity());

        rlHomeMailLayout = (RelativeLayout) rootView.findViewById(R.id.rlHomeMailLayout);
        rlStartCount = (RelativeLayout) rootView.findViewById(R.id.rlStartCount);
        rlFindItem = (RelativeLayout) rootView.findViewById(R.id.rlSearchItem);
        textStart = (TextView) rootView.findViewById(R.id.textStart);
        textCount = (TextView) rootView.findViewById(R.id.textCount);

        moveDown = AnimationUtils.loadAnimation(getActivity(), R.anim.move_down);
        moveUp = AnimationUtils.loadAnimation(getActivity(), R.anim.move_up);
    }

    private void onClick(){

        rlStartCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String flJson = getStringFromFile(
                            _pref.getSession(Constant.JSON_SCH_CATEGORY_FILE_NAME));

                    if(!flJson.equals(null))
                    {
                        startActivity(new Intent(getActivity(), ScheduleActivity.class));
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getBaseContext(), "Data not loaded yet...", Toast.LENGTH_SHORT).show();
                }



            }
        });

        rlFindItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent intent = new Intent(getActivity(), ScanScreenActivity.class);
               intent.putExtra("scan_type", "find");
               startActivity(intent);
               getActivity().overridePendingTransition(R.anim.slide_in_right,
                       R.anim.slide_out_left);

            }
        });
    }

    private String getStringFromFile(String filePath) throws Exception {    //Not needed now


        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        // Make sure you close all streams.
        fin.close();

        return ret;
    }

    private String convertStreamToString(InputStream is) throws Exception {


        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();

        return sb.toString();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        rlFindItem.setAnimation(moveUp);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
