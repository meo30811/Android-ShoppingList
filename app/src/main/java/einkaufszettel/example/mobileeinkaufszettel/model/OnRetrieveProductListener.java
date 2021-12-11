package einkaufszettel.example.mobileeinkaufszettel.model;

import java.util.ArrayList;

public interface OnRetrieveProductListener {
    void onRetrieveProductCache(ArrayList<Product> cache);
    void onRetrieveSuccess(boolean success);
}
