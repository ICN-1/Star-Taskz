package com.theteam.taskz.utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class JsonConverter {

    public static HashMap<String,Object> convertToHashMap(JSONObject jsonObject) throws JSONException {
        final HashMap<String,Object> hash = new HashMap<>();

        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            hash.put(key, jsonObject.get(key));
        }

        return hash;
    }
}
