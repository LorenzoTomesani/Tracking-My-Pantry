package it.unibo.lorenzo;
import org.json.JSONObject;

public interface VolleyCallback {
    void onSuccess(JSONObject result);
    void onError();
}
