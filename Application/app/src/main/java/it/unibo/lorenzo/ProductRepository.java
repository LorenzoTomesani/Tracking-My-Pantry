package it.unibo.lorenzo;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProductRepository {

    private ProductDao productDao;
    private LiveData<List<Product>> listOfProducts;
    private ApiManager apiManager;
    private String email, accessToken, transitionToken;
    private JSONArray toReturn ;
    private Application app;

    ProductRepository (Application app, String idUser, String accessToken) {
        MyDatabase db = MyDatabase.getDatabase(app);
        productDao = db.ProductDao();
        listOfProducts = productDao.getListOfProducts(idUser);
        email = idUser;
        this.accessToken = accessToken;
        this.app = app;
        apiManager = new ApiManager(app.getApplicationContext());
        toReturn = new JSONArray();
    }

    LiveData<List<Product>> getListOfProducts() {
        return listOfProducts;
    }

    public void getProduct(String barcode, final callPantry callView){
        toReturn = new JSONArray();
        apiManager.getProduct(barcode, accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray tmp = result.getJSONArray("products");
                    transitionToken = result.getString("token");
                   if (tmp.length() == 1) {
                        JSONObject object = tmp.getJSONObject(0);
                        insert(object);
                        apiManager.ratingProduct(transitionToken, object.getString("id"), 1);
                    }
                    toReturn = tmp;
                    callView.onSuccess(toReturn);
                } catch (Exception e) {
                    callView.onError();
                }
            }
            @Override
            public void onError() {
                Log.i("errore", "errore");
            }
        });
    }

    void insertDB(String name, String description, String barcode){
        apiManager.postProduct(barcode, name, description, transitionToken, accessToken, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    insert(result);
                } catch (Exception e) {
                    Log.e("error", e.toString());
                }
            }

            @Override
            public void onError() {
                Log.e("error", "errore");
            }
        });
    }

    void insert(JSONObject object) throws JSONException {
            Product prodToInsert = new Product(object.getString("id"),
            object.getString("name"),
            object.getString("description"),
            object.getString("barcode"),
            email,
            1
    );
        MyDatabase.databaseWriteExecutor.execute(() -> {
            productDao.insert(prodToInsert);
        });
    }

    void delete(Product p){
        MyDatabase.databaseWriteExecutor.execute(() -> {
            productDao.delete(p);
        });
    }

    void update(Product p){
        MyDatabase.databaseWriteExecutor.execute(() -> {
            productDao.update(p);
        });
    }
}
