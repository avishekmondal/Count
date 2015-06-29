package com.utility;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.count.R;
import com.utility.Pref;

/**
 * Created by Rahul on 5/5/2015.
 */
public class ThemeSetter {
    private Context context;
    private SharedPreferences _pref;
    private LayoutInflater inflater;
    private View actionbarCustView;
    private ActionBar.LayoutParams layoutParams;
    private TextView tvHeaderTitle;
    private ImageView ivMenu;
    private LayerDrawable layerDrawableButton;

    public ThemeSetter(Context context){
        this.context = context;
        this._pref = new Pref(context).getSharedPreferencesInstance();
    }

    public ImageView setHeaderTheme(ActionBar actionBar, String heading, int ivMenuDrawable){

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionbarCustView = inflater.inflate(R.layout.custom_actionbar_layout, null);
        tvHeaderTitle = (TextView) actionbarCustView.findViewById(R.id.tvHeaderTitle);
        ivMenu = (ImageView) actionbarCustView.findViewById(R.id.ivMenu);
        ivMenu.setImageResource(ivMenuDrawable);
        layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0FAA83")));
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionbarCustView, layoutParams);

        tvHeaderTitle.setText(heading);

        return ivMenu;
    }

    public void setBodyColor(LinearLayout mainLayout){
        mainLayout.setBackground(new ColorDrawable(Color.parseColor("#1DE9D6")));
    }

    public void setBodyColor(RelativeLayout mainLayout){
        mainLayout.setBackground(new ColorDrawable(Color.parseColor("#1DE9D6")));
    }

    public void setButtonColor(LinearLayout buttonLayout){
        layerDrawableButton = (LayerDrawable) context.getResources().getDrawable(R.drawable.button_shadow_border);
        GradientDrawable itemId = (GradientDrawable)layerDrawableButton.findDrawableByLayerId(R.id.itemButtonColor);
        itemId.setColor(Color.parseColor("#0FA983"));
        buttonLayout.setBackground(layerDrawableButton);
    }

    public void setHeadingColor(){

    }

    public void setTextColor(){

    }
}
