package it.unibo.lorenzo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogNewProd extends DialogFragment {

    private EditText input;
    public String text = "";
    private static DialogNewProd INSTANCE = null;
    private DialogUpdateProduct listener = null;
    public DialogNewProd(){

    }

    public static DialogNewProd newInstance(){
        if(INSTANCE == null) {
            DialogNewProd newProd = new DialogNewProd();
            INSTANCE = newProd;
            return newProd;
        }
        return INSTANCE;
    }

    public void setListener(DialogUpdateProduct updateP){
        listener = updateP;
    }

    public static DialogNewProd getInstance(){
        return INSTANCE;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            TextView title = StyleDialog.createTitleText(getActivity(), getString(R.string.dialog_title_bar));
            alertDialog.setCustomTitle(title);
            input = new EditText(getActivity());
            input.setWidth(100);
            input.setGravity(Gravity.CENTER);
            input.setText(text);
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    text = s.toString();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            alertDialog.setView(input, 70, 0, 70, 0);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.conferma),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            INSTANCE = null;
                            String barcode = input.getText().toString();
                            input.setText("");
                            listener.onUpdate(barcode);
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.annulla),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            input.setText("");
                            INSTANCE = null;
                            listener.onCancel();
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
    public void onResume() {
        super.onResume();
        Button btnPositive = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        StyleDialog.styleButton(btnPositive, btnNegative);
    }

}
