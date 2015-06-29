package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bean.PhysicalCountMenuItem;
import com.count.R;
import com.utility.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Avishek on 5/14/2015.
 */
public class PhysicalCountMenuAdapter extends ArrayAdapter<PhysicalCountMenuItem> {

    private LayoutInflater inflater;
    private Context mContext;
    private ImageLoader imageloader;

    public PhysicalCountMenuAdapter(Context context,
                                      ArrayList<PhysicalCountMenuItem> catItems) {
        // TODO Auto-generated constructor stub
        super(context, R.layout.physical_count_menu_row, R.id.tvCatType,
                catItems);
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        imageloader = new ImageLoader(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final PhysicalCountMenuItem surveyList = (PhysicalCountMenuItem) this
                .getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();

        convertView = inflater.inflate(R.layout.physical_count_menu_row, null);

        holder.tvCatType = (TextView) convertView
                .findViewById(R.id.tvCatType);
        holder.imgCat = (ImageView) convertView
                .findViewById(R.id.imgCat);


        convertView.setTag(holder);
        holder = (ViewHolder) convertView.getTag();

        holder.tvCatType.setText(surveyList.getcatType());
        //holder.imgCat.setImageResource(R.drawable.ic_launcher);

        holder.imgCat.setScaleType(ImageView.ScaleType.FIT_XY);
        imageloader.DisplayImage(surveyList.getcatImg(), holder.imgCat);


        return convertView;
    }

    public class ViewHolder {

        TextView tvCatType;
        ImageView imgCat;

    }
}
