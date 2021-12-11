package einkaufszettel.example.mobileeinkaufszettel.model;

public class Product {

    private String id ;
    private String name;
    private String quantity;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String unit;

    public Product(String id, String name, String quantity,String unit) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit= unit;
    }
    public Product(String id, String name)
    {
        this.name = name;
        this.id = id;
    }
    public Product()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

   public String toString()
   {
       return name +" : "+quantity+" "+unit;
   }
    public String toStringId()
    {
        return id+" "+name +" "+quantity+" "+unit;
    }

}
