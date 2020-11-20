package com.example.notework.CustomAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.notework.Models.Remind;
import com.example.notework.R;

import java.util.ArrayList;

public class NhacNhoAdapter extends BaseAdapter {
    Context context;
    ArrayList<Remind> dsRemind;

    public NhacNhoAdapter(Context context, ArrayList<Remind> dsRemind) {
        this.context = context;
        this.dsRemind = dsRemind;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Remind> getDsRemind() {
        return dsRemind;
    }

    public void setDsRemind(ArrayList<Remind> dsRemind) {
        this.dsRemind = dsRemind;
    }

    @Override
    public int getCount() {
        return dsRemind.size();
    }

    @Override
    public Object getItem(int position) {
        return dsRemind.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dsRemind.indexOf(dsRemind.get(position));
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

        viewHolder.tvTitleRemind.setText(dsRemind.get(position).getTitle());

        if(dsRemind.get(position).getContentRemind().length() < 20){
            viewHolder.tvSummaryRemind.setText(dsRemind.get(position).getContentRemind() + ". . .");
        }else {
            String SummaryRemind = dsRemind.get(position).getContentRemind();
            viewHolder.tvSummaryRemind.setText(SummaryRemind.substring(0, 21) + ". . .");
        }

        return convertView;
    }
}
