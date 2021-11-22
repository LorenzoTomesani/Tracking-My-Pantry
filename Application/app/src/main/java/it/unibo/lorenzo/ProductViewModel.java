package it.unibo.lorenzo;

import android.app.Application;
import android.util.Log;


import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProductViewModel extends AndroidViewModel {

    private ProductRepository repository;
    private LiveData<List<Product>> listOfProducts;
    private MutableLiveData<List<Product>> filtered;
    private String lastBarcode;
    private boolean filterTag = false;
    private boolean filterName = false;

    private String filterN = "";
    final String[] type = {"Pasta", "Carne", "Verdura", "Frutta", "Dolce", "Legume", "Pesce", "Bevanda", "Latticino", "Condimento"};
    final String blank_space = "\\s*";
    boolean[] isTypeActive = new boolean[type.length];


    public ProductViewModel(Application application, String idUser, String accessToken) {
        super(application);
        repository = new ProductRepository(application, idUser, accessToken);
        listOfProducts = repository.getListOfProducts();
        filtered = new MutableLiveData<>();
    }

    public boolean isListFiltered() {
        return filterName || filterTag;
    }

    public LiveData<List<Product>> getListOfProducts() {
        return listOfProducts;
    }

    public MutableLiveData<List<Product>> getFiltered() {
        return filtered;
    }

    public void getProductsByName(String filterRaw) {
        List<Product> lista = new ArrayList<>();
        List<Product> listaFiltered = new ArrayList<>();
        if (!filterTag) {
            lista.addAll(listOfProducts.getValue());
        } else {
            lista.addAll(filtered.getValue());
        }

        if (filterRaw.isEmpty()) {
            filterN = "";
            filterName = false;
            if (lista.size() == 0 && !listOfProducts.getValue().isEmpty()) {
                filterTag(type, isTypeActive);
            } else {
                filtered.postValue(lista);
            }
        } else {
            String filter = filterRaw.replaceAll(blank_space, "");
            if (!filter.isEmpty()) {
                filterN = filter;
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getName().contains(filter)) {
                        listaFiltered.add(lista.get(i));
                    }
                }
                filtered.postValue(listaFiltered);
                filterName = true;
            }
        }
    }

    public void filterTag(String[] filter, boolean[] checked) {
        List<Product> lista = new ArrayList<>();
        List<Product> listaFiltered = new ArrayList<>();
        isTypeActive = checked;
        if (!filterName) {
            lista.addAll(listOfProducts.getValue());
        } else {
            lista.addAll(filtered.getValue());
        }
        if (!Arrays.toString(checked).contains("true")) {
            Arrays.fill(isTypeActive, false);
            filterTag = false;
            if (lista.size() == 0 && !listOfProducts.getValue().isEmpty()) {
                getProductsByName(filterN);
            } else {
                filtered.postValue(lista);
            }
        } else {
            for (int i = 0; i < lista.size(); i++) {
                for (int j = 0; j < filter.length; j++) {
                    if (lista.get(i).getTag() != null) {
                        if (lista.get(i).getTag().equals(filter[j]) && checked[j]) {
                            listaFiltered.add(lista.get(i));
                        }
                    }
                }
            }

            filtered.postValue(listaFiltered);
            filterTag = true;
        }
    }

    public void getProduct(String barcode, final callPantry callView) {
        lastBarcode = barcode;
        boolean flag = false;
        for (int i = 0; i < listOfProducts.getValue().size(); i++) {
            if (listOfProducts.getValue().get(i).getBarcode().equals(barcode)) {
                flag = true;
            }
        }
        if (!flag) {
            this.repository.getProduct(barcode, new callPantry() {
                @Override
                public void onSuccess(JSONArray result) {
                    callView.onSuccess(result);
                }

                @Override
                public void onError() {
                    callView.onError();
                }
            });
        } else {
            callView.onSuccess(null);
        }
    }

    private int getProductsByBarcode(Product p, List<Product> products) {
        int pos = -1;
        boolean found = false;
        int i = 0;
        while (i < products.size() && !found) {
            if (p.getId() == products.get(i).getId()) {
                pos = i;
                found = true;
            }
            i++;
        }
        return pos;
    }

    /* aggiorna i prodotti filtrati */
    public void updateFilteredProduct(List<Product> products) {
        List<Product> toReturn = new ArrayList<>();
        toReturn.addAll(getFiltered().getValue());
        for (int i = 0; i < toReturn.size(); i++) {
            Product p = toReturn.get(i);
            int j = getProductsByBarcode(p, products);
            if (j > -1) {
                Product p2 = products.get(j);
                //controllo che o uno sia null e non l'altro oppure nessuno dei due è null e i due valori sono diversi
                // lazy evalutation, così evito che di usare l'equals sul null
                if (p.getTag() == null && p2.getTag() != null || !(p.getTag() == null && p2.getTag() == null) && !p.getTag().equals(p2.getTag())) {
                    toReturn.set(i, p2);
                } else if (p.getDate() == null && p2.getDate() != null || !(p.getDate() == null && p2.getDate() == null) && !p.getDate().equals(p2.getDate())) {
                    toReturn.set(i, p2);
                } else if (p.getQuantity() != p2.getQuantity()) {
                    toReturn.set(i, p2);
                }
            } else {
                toReturn.remove(i);
            }

            filtered.postValue(toReturn);
        }
    }


    public void insertProduct(JSONObject p) throws JSONException {
        this.repository.insert(p);
    }

    public void insertProductDB(String name, String description) {
        this.repository.insertDB(name, description, lastBarcode);
    }

    public void updateProduct(Product p) {
        this.repository.update(p);
    }

    public void deleteProduct(Product p) {
        this.repository.delete(p);
    }
}

