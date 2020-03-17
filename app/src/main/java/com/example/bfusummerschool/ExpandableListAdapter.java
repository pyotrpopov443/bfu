package com.example.bfusummerschool;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableListAdapter extends ru.snowmaze.expandablelistview.ExpandableListAdapter {

    private StorageReference storageReference;
    private LinkedHashMap<String, List<String>> daysListHashMap;
    private List<String> photos;
    private String[] daysListHeaderGroup = new String[0];
    private boolean darkMode;
    private boolean connected;
    private int showImage;

    void setData(LinkedHashMap<String, List<String>> daysListHashMap) {
        this.daysListHashMap = daysListHashMap;
        daysListHeaderGroup = daysListHashMap.keySet().toArray(new String[0]);
        notifyDataSetChanged();
    }

    void setData(LinkedHashMap<String, List<String>> daysListHashMap, List<String> photos, boolean connected) {
        this.daysListHashMap = daysListHashMap;
        this.photos = photos;
        daysListHeaderGroup = daysListHashMap.keySet().toArray(new String[0]);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("photos");
        this.connected = connected;
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
        String child = getChild(groupPosition, childPosition);
        TextView event = view.findViewById(R.id.event);
        event.setText(child);
        ImageView photo = view.findViewById(R.id.photo);
        photo.setVisibility(showImage);
        if (showImage == View.VISIBLE){
            String fileName = photos.get(groupPosition) + ".png";
            ContextWrapper wrapper = new ContextWrapper(parent.getContext());
            File directory = wrapper.getDir("photos", Context.MODE_PRIVATE);
            File file = new File(directory, fileName);
            if(connected) {
                Glide.with(parent.getContext())
                        .asBitmap()
                        .load(storageReference.child(fileName))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                photo.setImageBitmap(resource);
                                try {
                                    FileOutputStream fos = new FileOutputStream(file);
                                    resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    photo.setImageBitmap(bitmap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return view;
    }

}