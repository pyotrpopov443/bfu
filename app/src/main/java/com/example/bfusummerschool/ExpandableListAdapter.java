package com.example.bfusummerschool;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableListAdapter extends ru.snowmaze.expandablelistview.ExpandableListAdapter {

    private LinkedHashMap<String, List<String>> daysListHashMap;
    private String[] daysListHeaderGroup = new String[0];
    private boolean darkMode;

    void setDays(LinkedHashMap<String, List<String>> daysListHashMap) {
        this.daysListHashMap = daysListHashMap;
        daysListHeaderGroup = daysListHashMap.keySet().toArray(new String[0]);
        notifyDataSetChanged();
    }

    ExpandableListAdapter(Boolean darkMode){
        this.darkMode = darkMode;
    }

    @Override
    public int getSplitterColor(){
        if (darkMode) {
            return R.color.darkGray;
        } else {
            return R.color.lightGray;
        }
    }

    @Override
    public int getGroupCount() {
        return daysListHeaderGroup.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Objects.requireNonNull(daysListHashMap.get(daysListHeaderGroup[groupPosition])).size();
    }

    @Override
    public int getListAnimationType() {
        return NO_ANIMATION;
    }

    @Override
    public long getListAnimationDuration() {
        return 75;
    }

    public String getGroup(int groupPosition) {
        return daysListHeaderGroup[groupPosition];
    }

    public String getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(daysListHashMap.get(daysListHeaderGroup[groupPosition])).get(childPosition);
    }

    @NonNull
    @Override
    public View getGroupView(int groupPosition, ViewGroup parent) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_group, parent, false);
        TextView day = view.findViewById(R.id.day);
        day.setText(getGroup(groupPosition));
        return view;
    }

    @NonNull
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_item, parent, false);
        TextView event = view.findViewById(R.id.event);
        String child = getChild(groupPosition, childPosition);
        event.setText(child);
        return view;
    }

}