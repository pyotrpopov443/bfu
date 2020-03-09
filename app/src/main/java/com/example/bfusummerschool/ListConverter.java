package com.example.bfusummerschool;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

class ListConverter {

    @TypeConverter
    public static List<String> fromString(String event) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(event, listType);
    }

    @TypeConverter
    public static String fromList(List<String> events) {
        return new Gson().toJson(events);
    }

}
