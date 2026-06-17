package edu.miu.cs.srmweb.service;

import edu.miu.cs.srmweb.model.Product;
import edu.miu.cs.srmweb.model.Supplier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataService {
    private final Map<Integer, Supplier> suppliers = new HashMap<>();
    private final Map<Long, Product> products = new HashMap<>();

    public DataService() {
        Supplier s1 = new Supplier(1, "Iowa Farms", "(641) 451-0009");
        Supplier s2 = new Supplier(2, "Hallmark Agro, Inc.", null);

        Product p1 = new Product(1L, "Santa sweet apples", 1.09, 124, LocalDate.parse("2023-05-31"), s1);
        Product p2 = new Product(2L, "Chicken drumsticks", 2.25, 18, LocalDate.parse("2023-04-10"), s1);
        Product p3 = new Product(3L, "Dole Bananas", 0.55, 1097, LocalDate.parse("2023-05-15"), s2);

        s1.addProduct(p1);
        s1.addProduct(p2);
        s2.addProduct(p3);

        suppliers.put(s1.getSupplierId(), s1);
        suppliers.put(s2.getSupplierId(), s2);

        products.put(p1.getProductId(), p1);
        products.put(p2.getProductId(), p2);
        products.put(p3.getProductId(), p3);
    }

    public List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers.values());
    }

    public List<Product> getAllProductsSortedByName() {
        return products.values().stream()
                .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<Supplier> findSupplierById(int id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public List<Product> getProductsBySupplierId(int supplierId) {
        Supplier s = suppliers.get(supplierId);
        if (s == null) return Collections.emptyList();
        return new ArrayList<>(s.getProducts());
    }
}
