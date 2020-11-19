package com.example.notework.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.notework.R;

public class NhacNhoAdapter extends BaseAdapter {
    Context context;


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView tvTitleRemind, tvSummaryRemind;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.remind_item, null);

            viewHolder.tvTitleRemind = (TextView) convertView.findViewById(R.id.tvTitleRemind);
            viewHolder.tvSummaryRemind = (TextView) convertView.findViewById(R.id.tvSummaryRemind);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }



        return convertView;
    }
}
