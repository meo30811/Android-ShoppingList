package einkaufszettel.example.mobileeinkaufszettel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import einkaufszettel.example.mobileeinkaufszettel.adapter.ListAdapter;
import einkaufszettel.example.mobileeinkaufszettel.model.OnRetrieveProductListener;
import einkaufszettel.example.mobileeinkaufszettel.model.Product;
import einkaufszettel.example.mobileeinkaufszettel.model.ProductList;
import einkaufszettel.example.mobileeinkaufszettel.viewmodel.ProductViewModel;

public class UseRecentProduct extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    public static int p_position;
    public int id = 1;
    private Spinner spinner;
    private static String einheit_waehlen;
    private static String listname;
    private ProductViewModel myProductViewModel;
    private ArrayList<Product> products;
    private static TextView action_bar_title;
    private ListAdapter listAdapter;
    private ListView product_list;
    private AutoCompleteTextView textProduct ;
    private EditText textQuantity;
    private Button einkaufszettel;
    private Button abbrechen;
    //speichert Produkten, die bereits gekauft oder angelegt wurden
    private static ArrayList<String> caching = new ArrayList<>();
    private static String getPositionValue;
    private ArrayList<String> delete_ids;
    private ArrayAdapter adapt_product;
    // for alert dialog
    private AutoCompleteTextView t_product;
    private EditText t_quantity;
    private Spinner t_unit;
    private String product;
    private String quantity;
    private String unit;
    private String einheit_wahl;
    private static boolean checkBoxIschecked;
    private static  CheckBox check;
    private ArrayList<Product> onStopSaveProducts;
    //input filter
    private String blockCharacterSet = "~#^|$%&*!\n";

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
        setContentView(R.layout.activity_use_recent_product);
        // bei onstop werden bereits hinzugef??gte produkte hier gespeichert f??r die sp??tere Nutzung...
        onStopSaveProducts = new ArrayList<>();
        // ckeckbox is not yet checked.
        checkBoxIschecked = false;
        action_bar_title = (TextView)findViewById(R.id.action_bar_title);
        getShoppingListName();
        textProduct = findViewById(R.id.productName);
        textProduct.setFilters(new InputFilter[]{filter});
        textQuantity = findViewById(R.id.productQuantity);
        textQuantity.setFilters(new InputFilter[]{filter});
        product_list = findViewById(R.id.listView);
        spinner = findViewById(R.id.einheit);
        products = new ArrayList<Product>();
        listAdapter = new ListAdapter(this, products);
        einkaufszettel = findViewById(R.id.b_zettel);
        abbrechen = findViewById(R.id.b_zettel_abbrechen);
        // save all product ids to be deleted
        product_list.setAdapter(listAdapter);
        product_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        delete_ids = new ArrayList<>();
        // spinner verwendet fuer die Einheit
        ArrayAdapter<CharSequence> einheitAdapter = ArrayAdapter.createFromResource(this, R.array.einheit, android.R.layout.simple_spinner_item);
        einheitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(einheitAdapter);
        spinner.setOnItemSelectedListener(this);

        myProductViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        myProductViewModel.setProducts(products);
        myProductViewModel.getProducts().observe(this, new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                listAdapter.setProductList(products);
            }
        });

        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                 check = (CheckBox) v;
                if(check.isChecked()) {
                    check.setChecked(true);
                    checkBoxIschecked = true;
                    setProductPosition(position);
                    getPositionValue = product_list.getItemAtPosition(position).toString();
                    // speichert alle ids, die zu loeschen sind.
                    delete_ids.add(getPositionValue);
                    //Toast.makeText(getApplicationContext(), "Item clicked: position= " + position + " id: ", Toast.LENGTH_SHORT).show();
                }else
                {
                    // item zwischenspeichern, wenn es unchecked ist
                    String tempItemPosition = product_list.getItemAtPosition(position).toString();
                    // set checked to false if item unchecked
                    check.setChecked(false);
                    // remove it from the list
                    delete_ids.remove(tempItemPosition);
                    // if there are one or more item selected, by update the last selected Item will be choosen
                    if(!delete_ids.isEmpty()) {
                        getPositionValue = delete_ids.get(delete_ids.size()-1);
                        for(int i=0; i<products.size();i++) {
                            if(products.get(i).toString().equalsIgnoreCase(getPositionValue)) {
                                setProductPosition(i);
                            }
                        }
                    }else
                    {
                        // if no item is checked
                        checkBoxIschecked = false;
                    }
                }

            }
        });

        //zur Speicherung von Einkaufszettel
        einkaufszettel.setOnClickListener(this);
        abbrechen.setOnClickListener(this);
    }


    private void setProductPosition(int position)
    {
        this.p_position = position;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart",":");
        loadProduct();
        cacheProducts();
    }
    
    // add product to list, if add icon is clicked
    public void addProductMenu(View view)
    {
        String product = textProduct.getText().toString();
        String quantity = textQuantity.getText().toString();
        if(quantity.isEmpty()&&product.isEmpty())
        {
            Toast.makeText(this,"Bitte f??llen Sie alle felder!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!product.isEmpty()) {
            Product p = new Product(String.valueOf(id),product,quantity, einheit_waehlen);
            //cache product before adding it to listView
            if(!caching.contains(p.getName())) caching.add(p.getName());
            products.add(p);// add product to listView
            listAdapter.notifyDataSetChanged();// notify changes made
            id = id + 1;
        }else
        {
            Toast.makeText(this,"please input value!", Toast.LENGTH_SHORT).show();
            return;
        }
        cacheProducts();
        textProduct.setText("");
        textQuantity.setText("");
    }
    public void cacheProducts()
    {
        adapt_product = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,caching);
        textProduct.setAdapter(adapt_product);
    }

    public void deleteProductMenu(View view)
    {
        try {
            if(!delete_ids.isEmpty()) {

                for (int i = 0; i < delete_ids.size(); i++) {
                    for (int j = 0; j < products.size(); j++) {
                        if (delete_ids.contains(products.get(j).toString())) {
                            products.remove(j);
                        }
                    }
                }

                // ids von Produkten in Array l??schen, nachdem es von Datenbank gel??scht wurde
                delete_ids = new ArrayList<>();
                listAdapter.notifyDataSetChanged();

            }
            else
            {
                Toast.makeText(this, "Bitte w??hlen Sie das zu l??schende Produkt!!", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e)
        {
            Toast.makeText(this, "Bitte w??hlen Sie das zu l??schende Produkt!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void getShoppingListName()
    {
        Intent result = getIntent();
        String wagen = result.getStringExtra("einkaufszettel");
        action_bar_title.setText(wagen);
    }
    public void updateProductMenu(View view)
    {
        if(!checkBoxIschecked)
        {
            Toast.makeText(this, "Bitte w??hlen Sie ein Produkt!!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.layout_dialog, null);
            t_product = mView.findViewById(R.id.product);
            t_product.setFilters(new InputFilter[]{filter});
            adapt_product = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, caching);
            t_product.setAdapter(adapt_product);
            t_quantity = mView.findViewById(R.id.update_quantity);
            t_quantity.setFilters(new InputFilter[]{filter});
            t_unit = mView.findViewById(R.id.update_einheit);

            Product product = products.get(p_position);
            t_product.setText(product.getName());
            t_quantity.setText(product.getQuantity());
            unit = product.getUnit();

            ArrayAdapter<CharSequence> einheitAdapter = ArrayAdapter.createFromResource(this, R.array.einheit, android.R.layout.simple_spinner_item);
            einheitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            t_unit.setAdapter(einheitAdapter);

            if (!unit.isEmpty()) {
                int spinnerposition = einheitAdapter.getPosition(unit);
                t_unit.setSelection(spinnerposition);
            }
            t_unit.setOnItemSelectedListener(this);
            mBuilder.setTitle("Produkt ??ndern");

            mBuilder.setPositiveButton("??ndern", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            mBuilder.setNegativeButton("abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text_product = t_product.getText().toString();
                    String text_quantity = t_quantity.getText().toString();
                    String text_unit = einheit_waehlen;
                    if (!text_product.isEmpty() && !text_quantity.isEmpty()) {
                        products.get(p_position).setName(text_product);
                        products.get(p_position).setUnit(text_unit);
                        products.get(p_position).setQuantity(text_quantity);
                        Log.d("Log",""+text_product);

                        if (!caching.contains(text_product)) {
                            caching.add(text_product);
                            Log.d("Log: ",""+caching.toString());
                            cacheProducts();
                        }
                        listAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Bitte f??llen Sie alle Felder!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }catch (Exception e)
        {
            Toast.makeText(this, "Bitte w??hlen Sie ein Produkt!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProduct()
    {
        myProductViewModel.loadProducts(this, new OnRetrieveProductListener() {
            @Override
            public void onRetrieveProductCache(ArrayList<Product> cache) {
                Iterator<Product> pcache = cache.iterator();
                ArrayList<String> s_products = new ArrayList<String>();
                while(pcache.hasNext())
                {
                    Product p = pcache.next();
                    if(!caching.contains(p.getName())) {
                        caching.add(p.getName());
                        products.add(p);
                    }
                }
                if(products.isEmpty())
                {
                    Iterator<Product> prods = cache.iterator();
                    while(prods.hasNext()) {
                        Product p = prods.next();
                        if(!s_products.contains(p.toString())) {
                            s_products.add(p.toString());
                              products.add(p);
                        }
                    }
                }

            }

            @Override
            public void onRetrieveSuccess(boolean success) {
                if(!success)
                {
                    Toast.makeText(UseRecentProduct.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        einheit_waehlen = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    // Einkaufszettel wird persistenz gespeichert, wenn auf den Button geclickt wird
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // Einkaufszettel wird persistenz gespeichert, wenn auf den Button geclickt wird
            case R.id.b_zettel:
                if(!products.isEmpty()) {
                    Intent intent = new Intent(UseRecentProduct.this, MainActivity.class);
                    listname = action_bar_title.getText().toString();
                    listname = listname.replace(" ", "");
                    myProductViewModel.addProduct(new ProductList(listname), products, this);
                    startActivity(intent);
                }else
                {
                    Toast.makeText(UseRecentProduct.this, "Wops Einkaufszettel ist leer!", Toast.LENGTH_SHORT).show();
                }
            break;
            // back to previous activity, if this Button ist clicked
            case R.id.b_zettel_abbrechen:
                Intent intent = new Intent(UseRecentProduct.this, MainActivity.class);
                startActivity(intent);
                break;
        }

    }
}