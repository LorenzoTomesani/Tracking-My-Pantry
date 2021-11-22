package it.unibo.lorenzo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private String idProd;
    private String name;
    private String description;
    private String barcode;
    private String idUser;
    private int quantity;
    private String date;
    private String tag;

    public Product(String idProd, String name, String description, String barcode, String idUser, int quantity) {
        this.idProd = idProd;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.idUser = idUser;
        this.quantity = quantity;
        this.id = 0;
    }

    @Ignore
    public Product(int id, String idProd, String name, String description, String barcode, String idUser, int quantity, String date, String tag) {
        this.idProd = idProd;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.idUser = idUser;
        this.quantity = quantity;
        this.id = id;
        this.date = date;
        this.tag = tag;
    }


    public String getTag() {
        return tag;
    }

    public String getDate(){return date;}

    public int getId() {
        return id;
    }

    public String getIdProd() {
        return idProd;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getIdUser() {
        return idUser;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setDate(String date){this.date = date;}

    public void setTag(String tag){this.tag = tag;}

    public boolean equals(Product p){
        if(this == p) return true;
        if(this.tag == null && p.getTag() != null) return false;
        if(this.date == null && p.getDate() != null) return false;
        if(this.date == null && p.getDate() == null ) return this.tag == p.getTag() && this.quantity == p.getQuantity();
        if(this.tag == null && p.getTag() == null ) return this.date == p.getDate() && this.quantity == p.getQuantity();
        if(this.tag == null && p.getTag() == null && this.date == null && p.getDate() == null) return this.quantity == p.getQuantity();
        return this.tag.equals(p.getTag())
                && this.date.equals(p.getDate())
                && this.quantity == p.getQuantity()
                ;
    }

    public Product copy(){
        return new Product(getId(), getIdProd(), getName(), getDescription(), getBarcode(), getIdUser(), getQuantity(), getDate(), getTag());
    }
}
