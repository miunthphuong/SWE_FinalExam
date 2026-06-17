package edu.miu.cs.srmweb.dto;

import java.util.ArrayList;
import java.util.List;

public class SupplierDTO {
    private int supplierId;
    private String name;
    private String contactPhone;
    private List<ProductDTO> products = new ArrayList<>();

    public SupplierDTO() {}

    public SupplierDTO(int supplierId, String name, String contactPhone) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPhone = contactPhone;
    }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
    public void addProduct(ProductDTO p) { this.products.add(p); }
}
