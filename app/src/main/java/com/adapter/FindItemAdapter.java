package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bean.FindItem;
import com.count.R;

import java.util.ArrayList;

/**
 * Created by Rahul on 5/20/2015.
 */
public class FindItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FindItem> findItems;
    private LayoutInflater inflater;

    public FindItemAdapter(Context context, ArrayList<FindItem> findItems){
        this.context = context;
        this.findItems = findItems;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return findItems.size();
    }

    @Override
    public FindItem getItem(int i) {
        return findItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = view;
        if(rootView == null){
            rootView = inflater.inflate(R.layout.find_item_row, null);
            ItemHolder holder = new ItemHolder();
            holder.tvFindArticleCode = (TextView) rootView.findViewById(R.id.tvFindArticleCode);
            holder.tvFindCategory = (TextView) rootView.findViewById(R.id.tvFindCategory);
            holder.tvFindBarcode = (TextView) rootView.findViewById(R.id.tvFindBarcode);
            holder.tvFindDetails = (TextView) rootView.findViewById(R.id.tvFindDetails);
            holder.tvFindCount = (TextView) rootView.findViewById(R.id.tvFindCount);

            rootView.setTag(holder);
        }

        ItemHolder newHolder = (ItemHolder) rootView.getTag();

        newHolder.tvFindArticleCode.setText(getItem(i).getArticleCode());
        newHolder.tvFindCategory.setText(getItem(i).getCategoryName());
        newHolder.tvFindBarcode.setText(getItem(i).getBarcode());
        newHolder.tvFindDetails.setText(getItem(i).getDetails());
        newHolder.tvFindCount.setText(getItem(i).getCount());

        return rootView;
    }

    private class ItemHolder{
        TextView tvFindArticleCode, tvFindBarcode, tvFindCategory, tvFindDetails, tvFindCount;
    }
}
