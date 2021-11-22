package it.unibo.lorenzo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogAddDbProd extends DialogFragment {


    private static DialogAddDbProd INSTANCE;

    private DialogUpdateNewDbProd listener = null;
    String name = "";
    String desc ="";

    EditText nameText;
    EditText descText;

    public DialogAddDbProd(){

    }

    public static DialogAddDbProd newInstance( ){
        if(INSTANCE == null) {
            DialogAddDbProd newDbProd = new DialogAddDbProd();
            INSTANCE = newDbProd;
            return newDbProd;
        }
        return INSTANCE;
    }

    public static DialogAddDbProd getInstance(){
        return INSTANCE;
    }

    public void setListener( DialogUpdateNewDbProd updateP){
        listener = updateP;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        nameText =  ((AlertDialog) getDialog()).findViewById(R.id.editNameText);
        descText = ((AlertDialog) getDialog()).findViewById(R.id.editDescText);

        nameText.setText(name);
        descText.setText(desc);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        descText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                desc = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        TextView title = StyleDialog.createTitleText(getActivity(), getString(R.string.add_new_prod));
        alertDialog.setCustomTitle(title);

        LayoutInflater inflater = getLayoutInflater();
        alertDialog.setView(inflater.inflate(R.layout.insert_new_prod, null), 100, 0, 100, 0);


        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.conferma),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onUpdate(
                                ((EditText) ((AlertDialog) getDialog()).findViewById(R.id.editNameText)).getText().toString(),
                                ((EditText) ((AlertDialog) getDialog()).findViewById(R.id.editDescText)).getText().toString());
                        INSTANCE = null;
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.annulla),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        INSTANCE = null;
                       dismiss();
                    }
                });

        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Button btnPositive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        StyleDialog.styleButton(btnPositive, btnNegative);
    }

}
