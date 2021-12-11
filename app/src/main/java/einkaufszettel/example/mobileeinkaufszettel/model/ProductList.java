package einkaufszettel.example.mobileeinkaufszettel.model;

public class ProductList {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductList(String name) {
        this.name = name;
    }
    public ProductList()
    {
    }
    private String name;
}
