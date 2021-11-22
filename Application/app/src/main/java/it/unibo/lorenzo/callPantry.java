package it.unibo.lorenzo;

import org.json.JSONArray;
import org.json.JSONObject;

public interface callPantry {
        void onSuccess(JSONArray result);
        void onError();
}
