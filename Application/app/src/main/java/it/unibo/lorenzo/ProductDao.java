package it.unibo.lorenzo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductDao  {

    @Query("DELETE FROM products")
    void deleteAll();

    @Delete
    void delete(Product product);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Product product);

    @Query("SELECT * FROM products WHERE idUser=:idUser")
    LiveData<List<Product>> getListOfProducts(String idUser);

    @Update
    void update(Product product);
}
