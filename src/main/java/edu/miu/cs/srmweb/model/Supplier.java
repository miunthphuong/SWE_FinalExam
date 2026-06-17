package edu.miu.cs.srmweb.model;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactPhone;
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<Product> products = new ArrayList<>();

    public Supplier() {}

    public Supplier(int supplierId, String name, String contactPhone) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPhone = contactPhone;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product p) {
        this.products.add(p);
    }
}
