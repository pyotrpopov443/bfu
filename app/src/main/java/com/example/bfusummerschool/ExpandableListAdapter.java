package com.example.bfusummerschool;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableListAdapter extends ru.snowmaze.expandablerecyclerviewexample.ExpandableListAdapter {

    private LinkedHashMap<String, List<String>> daysListHashMap;
    private String[] daysListHeaderGroup = new String[0];

    void setDays(LinkedHashMap<String, List<String>> daysListHashMap) {
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
        return Objects.requireNonNull(daysListHashMap.get(daysListHeaderGroup[groupPosition])).size();
    }

    public String getGroup(int groupPosition) {
        return daysListHeaderGroup[groupPosition];
    }

    public String getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(daysListHashMap.get(daysListHeaderGroup[groupPosition])).get(childPosition);
    }

    @Nullable
    @Override
    public View getGroupView(int groupPosition, @NotNull ViewGroup parent) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_group, parent, false);
        TextView day = view.findViewById(R.id.day);
        day.setText(getGroup(groupPosition));
        return view;
    }

    @Nullable
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, @NotNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_list_item, parent, false);
        TextView event = view.findViewById(R.id.event);
        String child = getChild(groupPosition, childPosition);
        event.setText(child);
        return view;
    }
}