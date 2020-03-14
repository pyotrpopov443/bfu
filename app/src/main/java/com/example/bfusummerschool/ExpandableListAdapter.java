package com.example.bfusummerschool;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableListAdapter extends ru.snowmaze.expandablelistview.ExpandableListAdapter {

    private StorageReference storageReference;
    private LinkedHashMap<String, List<String>> daysListHashMap;
    private List<String> photos;
    private String[] daysListHeaderGroup = new String[0];
    private boolean darkMode;
    private int showImage;

    void setData(LinkedHashMap<String, List<String>> daysListHashMap) {
        this.daysListHashMap = daysListHashMap;
        daysListHeaderGroup = daysListHashMap.keySet().toArray(new String[0]);
        notifyDataSetChanged();
    }

    void setData(LinkedHashMap<String, List<String>> daysListHashMap, List<String> photos) {
        this.daysListHashMap = daysListHashMap;
        this.photos = photos;
        daysListHeaderGroup = daysListHashMap.keySet().toArray(new String[0]);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://bfu-summer-school.appspot.com/photos");
        notifyDataSetChanged();
    }

    ExpandableListAdapter(Boolean darkMode, int showImage){
        this.darkMode = darkMode;
        this.showImage = showImage;
    }

    @Override
    public int getSplitterColor(@NonNull Context context) {
        if (darkMode) {
            return ContextCompat.getColor(context, R.color.darkGray);
        } else {
            return ContextCompat.getColor(context, R.color.lightGray);
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
        ImageView photo = view.findViewById(R.id.photo);
        String child = getChild(groupPosition, childPosition);
        event.setText(child);
        photo.setVisibility(showImage);
        if (photo.getVisibility() == View.VISIBLE){
            GlideApp.with(view.getContext()).load( storageReference + "/" + photos.get(groupPosition) + ".jpg").into(photo);
        }
        return view;
    }

}