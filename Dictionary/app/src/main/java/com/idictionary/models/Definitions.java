package com.idictionary.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafsan on 16-Dec-17.
 */

public class Definitions implements Serializable {
    public String Word;
    public List<String> Definitions;

    public static Definitions fromJSON(JSONObject json) throws JSONException {
        Definitions result = new Definitions();
        result.Word = json.getString("word");
        result.Definitions = new ArrayList<>();
        final JSONArray definitions = json.getJSONArray("definitions");
        for (int i = 0; i < definitions.length(); i++) {
            String def = definitions.getString(i);
            result.Definitions.add(def);
        }
        return result;
    }

    public static JSONObject toJSON(Definitions def) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("word", def.Word);
        JSONArray defs = new JSONArray();
        for (String d : def.Definitions) {
            defs.put(d);
        }
        result.put("definitions", defs);
        return result;
    }
}
