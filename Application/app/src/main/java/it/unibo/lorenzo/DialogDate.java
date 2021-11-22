package it.unibo.lorenzo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogDate extends DialogFragment {

    private DialogUpdateProduct listener = null;
    private static DialogDate INSTANCE = null;
    private Product p = null;
    public DialogDate(){

    }

    public static DialogDate newInstance(int year, int month, int day) {
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        if(INSTANCE == null) {
            DialogDate date = new DialogDate();
            date.setArguments(args);
            INSTANCE = date;
            return date;
        }

        INSTANCE.setArguments(args);
        return INSTANCE;
    }

    public void setProduct(Product p){
        this.p = p;
    }

    public Product getProduct(){
        return p;
    }

    public void setListener(DialogUpdateProduct updateP){
        listener = updateP;
    }

    public static DialogDate getInstance(){
        return INSTANCE;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String data = dayOfMonth + "/" + month + "/" + year;
                listener.onUpdate(data);
                INSTANCE = null;
            }
        }, getArguments().getInt("year"), getArguments().getInt("month"), getArguments().getInt("day"));

        datePickerDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.annulla),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCancel();
                        INSTANCE = null;
                        dismiss();
                    }
        });

        datePickerDialog.setCanceledOnTouchOutside(false);
        return datePickerDialog;
    }
}
