package it.unibo.lorenzo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DialogChooseProdList extends DialogFragment {


    private static DialogChooseProdList INSTANCE;
    private static JSONArray result;
    private DialogChooseJson listener;

    public DialogChooseProdList(){

    }


    public static DialogChooseProdList getInstance(){
        return INSTANCE;
    }

    public static JSONArray getProd(){
        return result;
    }

    public static DialogChooseProdList newInstance(JSONArray products) {
        Bundle args = new Bundle();
        args.putString("list", products.toString());
        result = products;
        if(INSTANCE == null) {
            DialogChooseProdList listProd = new DialogChooseProdList();
            INSTANCE = listProd;
            listProd.setArguments(args);
            return listProd;
        }
        INSTANCE.setArguments(args);
        return INSTANCE;
    }

    public void setListener(DialogChooseJson updateP){
        listener = updateP;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        TextView title = StyleDialog.createTitleText(getActivity(), getString(R.string.chose_prod_dialog));
        alertDialog.setCustomTitle(title);
        try {
            final JSONArray products = new JSONArray(getArguments().getString("list"));
            String[] arrayAdapter = new String[products.length()];
            for (int i = 0; i < products.length(); i++) {
                try {
                    arrayAdapter[i] = getResources().getString(R.string.nome) + ": " + products.getJSONObject(i).getString("name");
                    arrayAdapter[i] += "\n" + getResources().getString(R.string.descrizione) + ": " + products.getJSONObject(i).getString("description");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            alertDialog.setNegativeButton(getString(R.string.annulla), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    INSTANCE = null;
                    dialog.dismiss();
                }
            });

            alertDialog.setItems(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        JSONObject tmp = products.getJSONObject(which);
                        listener.onUpdate(tmp);
                        INSTANCE = null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Dialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        ListView list = ((AlertDialog) getDialog()).getListView();
        list.setPadding(20, 10, 20, 10);
        list.setDivider(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.green))); // set color
        list.setDividerHeight(2);
    }
}
