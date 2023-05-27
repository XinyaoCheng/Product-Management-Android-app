package com.example.a82.model;

public class ProductModel {
    private String name, price, supplier, standard, amount, expiry_year, expiry_month, expiry_day,category;

    public ProductModel() {
    }

    public ProductModel(String name, String price, String supplier, String standard, String amount, String expiry_year, String expiry_month, String expiry_day, String category) {
        this.name = name;
        this.price = price;
        this.supplier = supplier;
        this.standard = standard;
        this.amount = amount;
        this.expiry_year = expiry_year;
        this.expiry_month = expiry_month;
        this.expiry_day = expiry_day;
        this.category = category;
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

    public String getExpiry_year() {
        return expiry_year;
    }

    public void setExpiry_year(String expiry_year) {
        this.expiry_year = expiry_year;
    }

    public String getExpiry_month() {
        return expiry_month;
    }

    public void setExpiry_month(String expiry_month) {
        this.expiry_month = expiry_month;
    }

    public String getExpiry_day() {
        return expiry_day;
    }

    public void setExpiry_day(String expiry_day) {
        this.expiry_day = expiry_day;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", supplier='" + supplier + '\'' +
                ", standard='" + standard + '\'' +
                ", amount='" + amount + '\'' +
                ", expiry_year='" + expiry_year + '\'' +
                ", expiry_month='" + expiry_month + '\'' +
                ", expiry_day='" + expiry_day + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
