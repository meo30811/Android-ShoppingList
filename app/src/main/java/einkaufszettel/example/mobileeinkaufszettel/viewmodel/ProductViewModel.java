package einkaufszettel.example.mobileeinkaufszettel.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Iterator;

import einkaufszettel.example.mobileeinkaufszettel.model.OnRetrieveProductListener;
import einkaufszettel.example.mobileeinkaufszettel.model.Product;
import einkaufszettel.example.mobileeinkaufszettel.model.ProductList;
import einkaufszettel.example.mobileeinkaufszettel.repository.FirebaseRepository;

public class ProductViewModel extends AndroidViewModel {
    // mutableLivedata to save product in list.
    private MutableLiveData<ArrayList<Product>> products;
    private FirebaseRepository repository;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        repository = FirebaseRepository.getInstance();
    }
    //
    public LiveData<ArrayList<Product>> getProducts()
    {
        return products;
    }
    public void setProducts(ArrayList<Product> list)
    {
        products = new MutableLiveData<>();
        products.setValue(list);
    }
    // einkaufszettel in firestore speichern
    public void addProduct(ProductList name,ArrayList<Product> products, Context context)
    {
        Iterator<Product> ps = products.iterator();
        int id = 1;
        while(ps.hasNext())
        {
            Product p = ps.next();
            p.setId(String.valueOf(id));
            repository.addProductInFirebase(name, p,p, context);
            id = id +1;
        }
    }
    // bereits gekaufte Produkten laden
    public void loadProducts(Context context, OnRetrieveProductListener listener)
    {
       repository.getAllProductFromFirebase(context,listener);
    }


}
