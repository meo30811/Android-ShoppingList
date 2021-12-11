package einkaufszettel.example.mobileeinkaufszettel;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import einkaufszettel.example.mobileeinkaufszettel.model.OnRetrieveProductListener;
import einkaufszettel.example.mobileeinkaufszettel.model.Product;
import einkaufszettel.example.mobileeinkaufszettel.viewmodel.ProductViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Initialiserung des widgets
    private EditText wagen ;
    private Button anlegen;
    private Button bereitsGekauft;
    private ProductViewModel myProductViewModel;

    // folgende zeichen können bei der eingabe nicht ausgewählt werden
    private String blockCharacterSet = "~#^|$%&*+'*´7894561230/!()=§^?ß\n";
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // editText für die Eingabe von Einkaufszettel initialisieren
        wagen = findViewById(R.id.einkaufWagen);
        // filtern, damit nur charakter eingegeben werden
        wagen.setFilters(new InputFilter[]{filter});
        // button für die das anlegen eines Einkaufszettels
        anlegen = findViewById(R.id.b_anlegen);
        //button für die bereits gekauften Produkte
        bereitsGekauft = findViewById(R.id.b_recentProduct);
        anlegen.setOnClickListener(this);
        bereitsGekauft.setOnClickListener(this);
        myProductViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        enableBereitsGekauftButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableBereitsGekauftButton();
    }

    // nach eingabe und nach click auf den Button, wird ein neues einkaufszettel angelegt
    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.b_anlegen:
                onWagenButtonClicked();
                break;
            case R.id.b_recentProduct:
                onBereitsGekauftButtonClicked();
                break;
        }
    }

    public void enableBereitsGekauftButton()
    {
       myProductViewModel.loadProducts(this, new OnRetrieveProductListener() {
           @Override
           public void onRetrieveProductCache(ArrayList<Product> cache) {
               if(!cache.isEmpty())
               {
                   bereitsGekauft.setEnabled(true);
               }
           }
           @Override
           public void onRetrieveSuccess(boolean success) {

           }
       });
    }
    public void onBereitsGekauftButtonClicked()
    {
        Intent intent = new Intent(MainActivity.this,UseRecentProduct.class);
        String textWagen = wagen.getText().toString();
        if(!textWagen.isEmpty())
        {
            intent.putExtra("einkaufszettel",textWagen);
            startActivity(intent);
        }else
        {
            Toast.makeText(this,"Bitte legen Sie ein Einkaufswagen an!",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void onWagenButtonClicked()
    {
        Intent intent = new Intent(MainActivity.this,ShoppingList.class);
        String textWagen = wagen.getText().toString();
        if(!textWagen.isEmpty())
        {
            intent.putExtra("einkaufszettel",textWagen);
            startActivity(intent);
        }else
        {
            Toast.makeText(this,"Bitte legen Sie ein Einkaufswagen an!",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}

