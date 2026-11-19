
package com.example.mobile_comp_prj;

public class product {

    private int id;
    private String name;
    private String description;
    private String category;
    private String price;

    // Constructor for existing catalogue products
    public product(String name, String description, String category, String price) {
        this.id = -1;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    // Constructor for products retrieved from SQLite
    public product(int id, String name, String description, String category, String price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }
}
