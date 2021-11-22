package it.unibo.lorenzo;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Pantry extends AppCompatActivity {

    final String UPDATEPLUS = "UPDATEPLUS";
    final String UPDATELESS = "UPDATELESS";
    final String DELETE = "DELETE";
    final String UPDATE_DATE = "UPDATE_DATE";
    final String UPDATE_TAG = "UPDATE_TAG";
    final String[] type = {"Pasta", "Carne", "Verdura", "Frutta", "Dolce", "Legume", "Pesce", "Bevanda", "Latticino", "Condimento"};
    boolean[] checkedItems = new boolean[type.length];

    private static final int REQUEST_CAMERA_PERMISSION = 201;

    String accessToken;
    String email;

    private ImageView Exit;
    private ImageView Settings;
    private ImageView Filter;


    private ProductViewModel ProductViewModel;
    private RecyclerView recyclerView;
    private EditText SearchEditText;
    private ActivityResultLauncher<Intent> mGetContent;

    private boolean alreadyOpen = false;
    private boolean menuOpen = false;

    final int delay = 1;
    final int wait = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // ---- SALVO ACCESS TOKEN -----
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accessToken = extras.getString("accessToken");
            email = extras.getString("email");
        }

        // ---- impostazione della view ----


        // ---- SETTO LA TOPBAR ----
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.menu_pantry);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.green)));


        Settings = findViewById(R.id.iconSettings);
        Exit = findViewById(R.id.iconExit);
        Filter = findViewById(R.id.iconFilter);
        recyclerView = findViewById(R.id.recycler_prod);
        SearchEditText = findViewById(R.id.SearchEditText);

        // ---- nessun filtro selezionato -----
        for (int i = 0; i < type.length; i++) {
            checkedItems[i] = false;
        }

        // ---- risultati della scansione del barcode passata in output tramite contratto ----
        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String str = data.getDataString();
                            Result(str);
                        } else {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error), duration);
                            toast.show();
                        }
                    }
                });

        //thread per inviare notifiche


        NotifyThread myThread = new NotifyThread(getApplication());
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(!Thread.currentThread().isInterrupted()){
                    myThread.run(ProductViewModel.getListOfProducts().getValue());
                }
            }
        }, delay, wait, TimeUnit.MINUTES);

        // ---- imposto i listener dei click ----
        // ---- PER USCIRE ----
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accessToken = "";
				
                ((NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
                scheduler.shutdown();
                finishAndRemoveTask();
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(Pantry.this, v);
                popup.inflate(R.menu.popmenu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.add_prod) {
                            menuOpen = false;
                            createDialogNewprod(false);
                        } else if (item.getItemId() == R.id.scan_prod) {
                            menuOpen = false;
                            createDialogNewprod(true);
                        }
                        return true;
                    }
                });
                popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
                    @Override
                    public void onDismiss(PopupMenu menu) {
                        menu.dismiss();
                        menuOpen = false;
                    }
                });
                if(!menuOpen) {
                    popup.show();
                    menuOpen = true;
                }
            }
        });

        Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilterTag();
            }
        });

        ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ProductViewModel(getApplication(), email, accessToken);
            }
        };
        ProductViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        final ProductListAdapter adapter = new ProductListAdapter(new ProductListAdapter.productDiff(), new OnItemClickListener() {
            @Override
            public void onItemClick(Product p, String op) {
                Product tmp = p.copy();
                if (op.equals(UPDATEPLUS)) {
                    tmp.setQuantity(tmp.getQuantity() + 1);
                    ProductViewModel.updateProduct(tmp);
                } else if (op.equals(UPDATELESS)) {
                    tmp.setQuantity(tmp.getQuantity() - 1);
                    ProductViewModel.updateProduct(tmp);
                } else if (op.equals(DELETE)) {
                    ProductViewModel.deleteProduct(tmp);
                } else if (op.equals(UPDATE_DATE)) {
                    createDialogChooseDate(tmp);
                } else if (op.equals(UPDATE_TAG)) {
                    createDialogChooseType(tmp);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        ProductViewModel.getListOfProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                if (ProductViewModel.isListFiltered()) {
                    ProductViewModel.updateFilteredProduct(products);
                } else {
                    if (products.isEmpty()) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        findViewById(R.id.noProducts).setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.noProducts).setVisibility(View.INVISIBLE);
                        adapter.submitList(products);
                    }
                }
            }
        });

        ProductViewModel.getFiltered().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                adapter.submitList(products);
            }
        });

        SearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ProductViewModel.getProductsByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setFilterTag() {
        DialogChooseTag.newInstance(type, checkedItems).show(getSupportFragmentManager(), "checkTag");
        DialogChooseTag.getInstance().setListener(new DialogUpdateFilter() {
            @Override
            public void onUpdate(boolean[] newUpdate) {
                ProductViewModel.filterTag(type, checkedItems);
            }
        });
    }

    private void createDialogChooseType(Product p) {
        if (!alreadyOpen) {
            alreadyOpen = true;
            DialogTag.newInstance(type).show(getSupportFragmentManager(), "chooseType");
            DialogTag.getInstance().setProduct(p);
            DialogTag.getInstance().setListener( new DialogUpdateProduct() {
                @Override
                public void onUpdate(String newUpdate) {
                    p.setTag(newUpdate);
                    ProductViewModel.updateProduct(p);
                    alreadyOpen = false;
                }

                @Override
                public void onCancel() {
                    alreadyOpen = false;
                }
            });
        }
    }

    private void createDialogChooseDate(Product p) {
        if (!alreadyOpen) {
            alreadyOpen = true;
            Calendar tmpCalendar = Calendar.getInstance();
            int year = tmpCalendar.get(Calendar.YEAR);
            int month = tmpCalendar.get(Calendar.MONTH);
            int day = tmpCalendar.get(Calendar.DAY_OF_MONTH);
            if (p.getDate() != null) {
                String tmp[] = p.getDate().split("/", p.getDate().length());
                year = Integer.parseInt(tmp[2]);
                month = Integer.parseInt(tmp[1]);
                day = Integer.parseInt(tmp[0]);
            }
            DialogDate.newInstance(year, month, day).show(getSupportFragmentManager(), "chooseDate");
            DialogDate.getInstance().setProduct(p);
            DialogDate.getInstance().setListener( new DialogUpdateProduct() {
                @Override
                public void onUpdate(String newUpdate) {
                    p.setDate(newUpdate);
                    ProductViewModel.updateProduct(p);
                    alreadyOpen = false;
                }

                @Override
                public void onCancel() {
                    alreadyOpen = false;
                }
            });
        }
    }

    private void Result(String barcode) {
        ProductViewModel.getProduct(barcode, new callPantry() {
            @Override
            public void onSuccess(JSONArray result) {
                if (result != null) {
                    if (result.length() == 0) {
                        createDialogAddProd();
                    } else if (result.length() == 1) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplication().getApplicationContext(), getString(R.string.product_insert), duration);
                        toast.show();
                    } else if (result.length() > 1) {
                        createDialogChooseProd(result);
                    }
                } else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.already_insert), duration);
                    toast.show();
                }
            }

            @Override
            public void onError() {
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.error), duration);
                toast.show();
            }
        });
    }

    private void createDialogNewprod(boolean scan) {
        if (!scan) {
            DialogNewProd.newInstance().show(getSupportFragmentManager(), "newProd");
            DialogNewProd.getInstance().setListener(new DialogUpdateProduct() {
                @Override
                public void onUpdate(String newUpdate) {
                    Result(newUpdate);
                }

                @Override
                public void onCancel() {
                    alreadyOpen = false;
                }
            });

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                LaunchActivity();
            }
        }
    }

    private void LaunchActivity() {
        Intent intent = new Intent(this, ScanCode.class);
        mGetContent.launch(intent);
    }

    private void createDialogChooseProd(JSONArray products) {
        DialogChooseProdList.newInstance(products).show(getSupportFragmentManager(), "prodList");
        DialogChooseProdList.getInstance().setListener(new DialogChooseJson() {
            @Override
            public void onUpdate(JSONObject object) {
                try {
                    ProductViewModel.insertProduct(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createDialogAddProd() {
        DialogAddDbProd.newInstance().show(getSupportFragmentManager(), "proDb");
        DialogAddDbProd.getInstance().setListener(new DialogUpdateNewDbProd() {
            @Override
            public void onUpdate(String name, String desc) {
                ProductViewModel.insertProductDB(name, desc);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBooleanArray("checkedItems", checkedItems);
    }

    @Override
    public void onStop() {
        super.onStop();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment possiblePrev = getSupportFragmentManager().findFragmentByTag("newProd");
        Fragment possiblePrev2 = getSupportFragmentManager().findFragmentByTag("proDb");
        Fragment possiblePrev3 = getSupportFragmentManager().findFragmentByTag("prodList");
        Fragment possiblePrev4 = getSupportFragmentManager().findFragmentByTag("checkTag");
        Fragment possiblePrev5 = getSupportFragmentManager().findFragmentByTag("chooseDate");
        Fragment possiblePrev6 = getSupportFragmentManager().findFragmentByTag("chooseType");
        if (possiblePrev != null) {
            ft.remove(possiblePrev);
        }
        if (possiblePrev2 != null) {
            ft.remove(possiblePrev2);
        }
        if (possiblePrev3 != null) {
            ft.remove(possiblePrev3);
        }
        if (possiblePrev4 != null) {
            ft.remove(possiblePrev4);
        }
        if (possiblePrev5 != null) {
            ft.remove(possiblePrev5);
        }
        if (possiblePrev6 != null) {
            ft.remove(possiblePrev6);
        }
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        checkedItems = savedInstanceState.getBooleanArray("checkedItems");
        ProductViewModel.filterTag(type, checkedItems);
        try {
            if (DialogNewProd.getInstance() != null) {
                DialogNewProd.getInstance().show(getSupportFragmentManager(), "newProd");
                DialogNewProd.getInstance().setListener(new DialogUpdateProduct() {
                    @Override
                    public void onUpdate(String newUpdate) {
                        Result(newUpdate);
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            } else if(DialogAddDbProd.getInstance() != null){
                DialogAddDbProd.getInstance().show(getSupportFragmentManager(), "proDb");
                DialogAddDbProd.getInstance().setListener(new DialogUpdateNewDbProd() {
                    @Override
                    public void onUpdate(String name, String desc) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.product_insert, duration);
                        toast.show();
                        ProductViewModel.insertProductDB(name, desc);
                    }
                });
            } else if(DialogChooseProdList.getInstance() != null){
                DialogChooseProdList.getInstance().show(getSupportFragmentManager(), "prodList");
                DialogChooseProdList.getInstance().setListener(new DialogChooseJson() {
                    @Override
                    public void onUpdate(JSONObject object) {
                        try {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.product_insert, duration);
                            toast.show();
                            ProductViewModel.insertProduct(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if(DialogChooseTag.getInstance() != null){
                DialogChooseTag.getInstance().show(getSupportFragmentManager(), "chooseDate");
                DialogChooseTag.getInstance().setListener(new DialogUpdateFilter() {
                    @Override
                    public void onUpdate(boolean[] newUpdate) {
                        ProductViewModel.filterTag(type, checkedItems);
                    }
                });
            } else if(DialogDate.getInstance() != null){
                DialogDate.getInstance().show(getSupportFragmentManager(), "checkTag");
                DialogDate.getInstance().setListener( new DialogUpdateProduct() {
                    @Override
                    public void onUpdate(String newUpdate) {
                        Product tmp = DialogDate.getInstance().getProduct();
                        tmp.setDate(newUpdate);
                        ProductViewModel.updateProduct(tmp);
                        alreadyOpen = false;
                    }

                    @Override
                    public void onCancel() {
                        alreadyOpen = false;
                    }
                });
            } else if(DialogTag.getInstance() != null){
                DialogTag.getInstance().show(getSupportFragmentManager(), "chooseType");
                DialogTag.getInstance().setListener( new DialogUpdateProduct() {
                    @Override
                    public void onUpdate(String newUpdate) {
                        Product tmp = DialogTag.getInstance().getProduct();
                        tmp.setTag(newUpdate);
                        ProductViewModel.updateProduct(tmp);
                        alreadyOpen = false;
                    }

                    @Override
                    public void onCancel() {
                        alreadyOpen = false;
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LaunchActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}