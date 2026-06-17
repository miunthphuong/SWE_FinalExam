package edu.miu.cs.srmweb.dto;

import java.time.LocalDate;

public class ProductDTO {
    private long productId;
    private String name;
    private double unitPrice;
    private int quantityInStock;
    private LocalDate dateSupplied;
    private SupplierDTO supplier;

    public ProductDTO() {}

    public ProductDTO(long productId, String name, double unitPrice, int quantityInStock, LocalDate dateSupplied, SupplierDTO supplier) {
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantityInStock = quantityInStock;
        this.dateSupplied = dateSupplied;
        this.supplier = supplier;
    }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantityInStock() { return quantityInStock; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
    public LocalDate getDateSupplied() { return dateSupplied; }
    public void setDateSupplied(LocalDate dateSupplied) { this.dateSupplied = dateSupplied; }
    public SupplierDTO getSupplier() { return supplier; }
    public void setSupplier(SupplierDTO supplier) { this.supplier = supplier; }
}
