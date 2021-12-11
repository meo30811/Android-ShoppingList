package einkaufszettel.example.mobileeinkaufszettel.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import einkaufszettel.example.mobileeinkaufszettel.model.OnRetrieveProductListener;
import einkaufszettel.example.mobileeinkaufszettel.model.Product;
import einkaufszettel.example.mobileeinkaufszettel.model.ProductList;

public class FirebaseRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); ;
    private CollectionReference colref ;
    private ArrayList<Product> cache;
    private static FirebaseRepository repository;
    private CollectionReference cacheProduct = db.collection("Products");

    private FirebaseRepository() {
    }
    // Singleton design pattern, damit diese klasse nur einmal instanziiert wird...
    public static FirebaseRepository getInstance()
    {
        if(repository == null)
        {
            repository = new FirebaseRepository();
        }
        return repository;
    }

    // Einkaufszettel in Firestore persistenz speichern...
    public void addProductInFirebase (ProductList listname, Product product, Product cache, Context context)
    {
        Map<String, String> map =new HashMap<>();
        map.put("ID",product.getId());
        map.put("name",product.getName());
        map.put("quantity", product.getQuantity());
        map.put("unit",product.getUnit());

        colref = db.collection(listname.getName());
        colref.add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context,"document erfolgreich hinzugefügt!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Einfügen nicht erfolgreich!",Toast.LENGTH_SHORT).show();
            }
        });
       // document wird für später zwischengespeichert nachdem es hinzugefügt wurde.
       saveProductInCache(cache, context);

    }

    // bereits gekaufte Produkte in firestore speichern...
    private void saveProductInCache(Product cache, Context context)
    {
        Map<String, String> mapCache =new HashMap<>();
        mapCache.put("ID",cache.getId());
        mapCache.put("name",cache.getName());
        mapCache.put("quantity", cache.getQuantity());
        mapCache.put("unit",cache.getUnit());

            cacheProduct
                    .add(mapCache)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                           // Toast.makeText(context, "Dokument erfolgreich in cache hinzugefügt!",Toast.LENGTH_SHORT).show();
                        }
                    });

    }
    // alle bereits gekaufte produkte vom Datenbank abfragen...
    public void getAllProductFromFirebase(Context context, OnRetrieveProductListener listener)
    {

        cacheProduct
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        cache = new ArrayList<>();
                        if(!task.isSuccessful())
                        {
                            listener.onRetrieveSuccess(false);
                            Toast.makeText(context,"data not successfully retrieved!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(DocumentSnapshot doc : task.getResult())
                        {
                            Product p = new Product();
                            p.setName(doc.getString("name"));
                            p.setUnit(doc.getString("unit"));
                            p.setQuantity(doc.getString("quantity"));
                            cache.add(p);
                        }
                        listener.onRetrieveProductCache(cache);
                        listener.onRetrieveSuccess(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed to retrieve data from database",Toast.LENGTH_SHORT).show();
            }
        });
    }


}