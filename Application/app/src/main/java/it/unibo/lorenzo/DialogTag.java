package it.unibo.lorenzo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogTag extends DialogFragment {

    private static DialogTag INSTANCE = null;
    private DialogUpdateProduct listener = null;
    private Product p;

    public DialogTag(){
        
    }

    public static DialogTag newInstance( String[] type) {
        if(INSTANCE == null) {
            Bundle args = new Bundle();
            args.putStringArray("type", type);
            DialogTag tag = new DialogTag();
            INSTANCE = tag;
            tag.setArguments(args);
            return tag;
        }
        return INSTANCE;
    }

    public Product getProduct(){
        return p;
    }

    public void setProduct(Product p){
        this.p = p;
    }

    public static DialogTag getInstance(){
        return INSTANCE;
    }

    public void setListener( DialogUpdateProduct updateP){
        listener = updateP;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.which_type));
        builder.setNegativeButton(getString(R.string.annulla), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                listener.onCancel();
                INSTANCE = null;
                dismiss();
            }
        });
        String type[] = getArguments().getStringArray("type");
        builder.setItems(type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = type[which];
                listener.onUpdate(selected);
                INSTANCE = null;
                dismiss();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
