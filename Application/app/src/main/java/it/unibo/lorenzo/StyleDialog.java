package it.unibo.lorenzo;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class StyleDialog {

    static TextView createTitleText(Context context, String text){
        TextView title = new TextView(context);
        title.setText(text);
        title.setPadding(0, 70, 0, 40);
        title.setGravity(Gravity.CENTER);
        title.setAllCaps(true);
        title.setTextColor(context.getColor(R.color.green));
        title.setTypeface(null, Typeface.BOLD);
        title.setTextSize(20);
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        return title;
    }

    static void styleButton(Button B1, Button B2){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) B1.getLayoutParams();
        layoutParams.weight = 10;
        B1.setLayoutParams(layoutParams);
        B2.setLayoutParams(layoutParams);
    }
}
