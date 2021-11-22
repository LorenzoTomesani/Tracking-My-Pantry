package it.unibo.lorenzo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private TextView productName, productDescription, productQuantity, productExpiry, productTag, productBarcode;

    private ImageButton minusQ, plusQ;


    final int DRAWABLE_RIGHT = 2;

    private Context context;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productName = itemView.findViewById(R.id.productName);
        productDescription = itemView.findViewById(R.id.productDescription);
        productQuantity = itemView.findViewById(R.id.productQuantity);
        productExpiry = itemView.findViewById(R.id.productExpireDate);
        productBarcode = itemView.findViewById(R.id.productBarcode);
        productTag = itemView.findViewById(R.id.productTag);
        minusQ = itemView.findViewById(R.id.minusQ);
        plusQ = itemView.findViewById(R.id.plusQ);
        context= itemView.getContext();
    }


    @SuppressLint("ClickableViewAccessibility")
    public void bind(Product prod, final OnItemClickListener listener) {
        SpannableString nameString =  new SpannableString(prod.getName());
        nameString.setSpan(new StyleSpan(Typeface.BOLD), 0, nameString.length(), 0);
        productName.setText(nameString);
        productBarcode.setText(prod.getBarcode());
        productDescription.setText(prod.getDescription());
        productQuantity.setText(String.valueOf(prod.getQuantity()));

        if(prod.getDate() == null){
            productExpiry.setText(context.getString(R.string.expire_not_set));
        } else {
            String tmp[] = prod.getDate().split("/", prod.getDate().length());
            int year = Integer.parseInt(tmp[2]);
            int month = Integer.parseInt(tmp[1])+1;
            int day = Integer.parseInt(tmp[0]);
            String data = day + "/" + month + "/" + year;
            productExpiry.setText(context.getString(R.string.expire) + " " + data);
        }

        if(prod.getTag() == null){
            productTag.setText(context.getString(R.string.type_not_set));
        } else {
            productTag.setText(prod.getTag());
        }

        minusQ.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(prod.getQuantity() == 1){
                    listener.onItemClick(prod, "DELETE");
                } else {
                    listener.onItemClick(prod, "UPDATELESS");
                }
            }
        });

        plusQ.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(prod, "UPDATEPLUS");
            }
        });

        productExpiry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (productExpiry.getRight() - productExpiry.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        listener.onItemClick(prod, "UPDATE_DATE");
                        return true;
                    }
                }
                return false;
            }
        });

        productTag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(event.getRawX() >= (productTag.getRight() - productTag.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        listener.onItemClick(prod, "UPDATE_TAG");
                        return true;
                    }
                }
                return false;
            }
        });

    }

    static ProductViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

}

