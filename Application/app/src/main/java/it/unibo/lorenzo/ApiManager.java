package it.unibo.lorenzo;


import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ApiManager {

    private String base_url;
    private RequestQueue queue;

    public ApiManager(Context context){
            base_url ="https://lam21.iot-prism-lab.cs.unibo.it";
            queue = Volley.newRequestQueue(context);
    }

    public void login(String email, String psw, final VolleyCallback callback){
        JSONObject jsonBody = new JSONObject();
        if(email.isEmpty() || psw.isEmpty()){
            callback.onError();
        } else {
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", psw);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, base_url + "/auth/login", jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("login err:" , error.toString());
                    callback.onError();
                }
            });
            Thread thread = new Thread() {
                @Override
                public void run() {
                    queue.add(jsonRequest);
                }
            };
            thread.start();
        }
    }

    public void register(String email, String psw, String nickname,final VolleyCallback callback){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", psw);
            jsonBody.put("username", nickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, base_url + "/users", jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError();
            }
        });
        Thread thread = new Thread() {
            @Override
            public void run() {
                queue.add(jsonRequest);
            }
        };
        thread.start();
    }

    public void getProduct(String barcode, String authorization, VolleyCallback callback){
        Map<String, String> Headers = new ArrayMap<String, String>();
        String auth = "Bearer " + authorization;
        Headers.put("Authorization", auth);

        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, base_url + "/products?barcode=" + barcode, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    callback.onSuccess(response);
                }
            },
            new Response.ErrorListener() {
                @Override
            public void onErrorResponse(VolleyError error) {
                    Log.i("ciao", error.toString());
                    callback.onError();
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders()  {
                return Headers;
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                queue.add(jsonRequest);
            }
        };
        thread.start();
    }

    public void postProduct(String barcode, String name, String description, String token, String accessToken, final VolleyCallback callback) {

        Map<String, String> Headers = new ArrayMap<String, String>();
        String auth = "Bearer " + accessToken;
        Headers.put("Authorization", auth);

        JSONObject jsonBody = new JSONObject();
        if (barcode.isEmpty() || name.isEmpty() || description.isEmpty()) {
            callback.onError();
        } else {
            try {
                jsonBody.put("barcode", barcode);
                jsonBody.put("description", description);
                jsonBody.put("test", true);
                jsonBody.put("token", token);
                jsonBody.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, base_url + "/products", jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    return Headers;
                }
            };

            Thread thread = new Thread() {
                @Override
                public void run() {
                    queue.add(jsonRequest);
                }
            };
            thread.start();
        }
    }

    public void ratingProduct(String token, String productId, int rating){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token", token);
            jsonBody.put("productId", productId);
            jsonBody.put("rating", rating);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, base_url + "/votes", jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        Thread thread = new Thread() {
            @Override
            public void run() {
                queue.add(jsonRequest);
            }
        };
        thread.start();
    }

}
