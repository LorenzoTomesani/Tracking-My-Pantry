package it.unibo.lorenzo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogChooseTag extends DialogFragment {

    private static DialogChooseTag INSTANCE = null;
    private DialogUpdateFilter listener;

    public DialogChooseTag(){

    }


    public static DialogChooseTag getInstance(){
        return INSTANCE;
    }

    public static DialogChooseTag newInstance( String[] type, boolean[] checked) {
        Bundle args = new Bundle();
        args.putStringArray("type", type);
        args.putBooleanArray("checked", checked);
        if(INSTANCE == null) {
            DialogChooseTag tag = new DialogChooseTag();
            INSTANCE = tag;
            tag.setArguments(args);
            return tag;
        }
        INSTANCE.setArguments(args);
        return INSTANCE;
    }

    public void setListener(DialogUpdateFilter updateP){
        listener = updateP;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.type));

        String type[] = getArguments().getStringArray("type");
        boolean checked[] =  getArguments().getBooleanArray("checked");
        builder.setMultiChoiceItems(type,checked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checked[which] = isChecked;
            }
        });

        builder.setPositiveButton(getString(R.string.conferma), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                INSTANCE = null;
                listener.onUpdate(checked);
                dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBooleanArray("checkedItems", getArguments().getBooleanArray("checked"));
    }

}
