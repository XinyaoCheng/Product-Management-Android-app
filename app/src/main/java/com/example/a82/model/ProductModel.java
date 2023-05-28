package com.example.a82.model;

public class ProductModel {
    private String name, price, supplier, standard, amount,category,id;
    private long expiry_time;
    public ProductModel() {
    }

    public ProductModel(String name, String price, String supplier, String standard, String amount, String category, String id, long expiry_time) {
        this.name = name;
        this.price = price;
        this.supplier = supplier;
        this.standard = standard;
        this.amount = amount;
        this.category = category;
        this.id = id;
        this.expiry_time = expiry_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getExpiry_time() {
        return expiry_time;
    }

    public void setExpiry_time(long expiry_time) {
        this.expiry_time = expiry_time;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", supplier='" + supplier + '\'' +
                ", standard='" + standard + '\'' +
                ", amount='" + amount + '\'' +
                ", category='" + category + '\'' +
                ", expiry_time=" + expiry_time +
                '}';
    }
}
