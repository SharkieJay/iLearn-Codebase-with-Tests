package com.example.mycorrectapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Util {
    public static JSONArray sortJsonArray(JSONArray jsonArr) throws JSONException {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArr.length(); i++) {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "Timestamp";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                Long valA = Long.valueOf(0);
                Long valB = Long.valueOf(0);

                try {
                    valA =  a.getLong(KEY_NAME);
                    valB =  b.getLong(KEY_NAME);
                }
                catch (JSONException e) {
                    System.err.println(e);
                }

                return valA.compareTo(valB);
            }
        });

        for (int i = 0; i < jsonArr.length(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }
}
