package com.example.bfusummerschool;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;

public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {

    private LinkedHashMap<String, List<String>> daysListHashMap;
    private String[] daysListHeaderGroup = new String[0];

    private OnEventClickCallback callback;

    public void setDays(LinkedHashMap<String, List<String>> daysListHashMap) {
        this.daysListHashMap = daysListHashMap;
        daysListHeaderGroup = daysListHashMap.keySet().toArray(new String[0]);
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return daysListHeaderGroup.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return daysListHashMap.get(daysListHeaderGroup[groupPosition]).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return daysListHeaderGroup[groupPosition];
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return daysListHashMap.get(daysListHeaderGroup[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_group, parent, false);
        }
        TextView day = convertView.findViewById(R.id.day);
        day.setText(getGroup(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_item, parent, false);
        }
        TextView event = convertView.findViewById(R.id.event);
        String child = getChild(groupPosition, childPosition);
        event.setText(child);
        convertView.setOnClickListener(v -> callback.onEventClick(child));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setCallback(OnEventClickCallback callback) {
        this.callback = callback;
    }

    interface OnEventClickCallback {

        void onEventClick(String event);

    }

}