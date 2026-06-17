package edu.miu.cs.srmweb.service;

import edu.miu.cs.srmweb.model.Product;
import edu.miu.cs.srmweb.model.Supplier;
import edu.miu.cs.srmweb.repository.ProductRepository;
import edu.miu.cs.srmweb.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataService {
    private final Map<Integer, Supplier> suppliers = new HashMap<>();
    private final Map<Long, Product> products = new HashMap<>();

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DataService(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;

        // initialize in-memory data as fallback; may be replaced by DB data on startup
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

    @EventListener(ApplicationReadyEvent.class)
    @org.springframework.transaction.annotation.Transactional
    public void initDbIfEmpty() {
        try {
            if (supplierRepository.count() == 0 && productRepository.count() == 0) {
                // save suppliers and products to DB
                for (Supplier s : suppliers.values()) {
                    // ensure products refer to supplier properly
                    for (Product p : s.getProducts()) {
                        p.setSupplier(s);
                    }
                    supplierRepository.save(s);
                }
                // refresh maps from DB and initialize collections
                supplierRepository.findAll().forEach(s -> {
                    s.getProducts().size(); // force initialize inside transaction
                    suppliers.put(s.getSupplierId(), s);
                });
                productRepository.findAll().forEach(p -> products.put(p.getProductId(), p));
            } else {
                // load from DB into memory maps and initialize collections
                supplierRepository.findAll().forEach(s -> {
                    s.getProducts().size();
                    suppliers.put(s.getSupplierId(), s);
                });
                productRepository.findAll().forEach(p -> products.put(p.getProductId(), p));
            }
        } catch (Exception ex) {
            // If DB unavailable, fail fast now (we require MySQL)
            throw new IllegalStateException("DB init failed: " + ex.getMessage(), ex);
        }
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

    // --- CRUD operations backed by repository ---
    @org.springframework.transaction.annotation.Transactional
    public Supplier createSupplier(Supplier s) {
        Supplier saved = supplierRepository.save(s);
        saved.getProducts().forEach(p -> p.setSupplier(saved));
        suppliers.put(saved.getSupplierId(), saved);
        return saved;
    }

    @org.springframework.transaction.annotation.Transactional
    public Optional<Supplier> updateSupplier(Integer id, Supplier incoming) {
        return supplierRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            existing.setContactPhone(incoming.getContactPhone());
            // do not overwrite products here
            Supplier saved = supplierRepository.save(existing);
            saved.getProducts().size();
            suppliers.put(saved.getSupplierId(), saved);
            return saved;
        });
    }

    @org.springframework.transaction.annotation.Transactional
    public boolean deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) return false;
        supplierRepository.deleteById(id);
        suppliers.remove(id);
        // remove products from map that belong to supplier
        products.values().removeIf(p -> p.getSupplier() != null && Objects.equals(p.getSupplier().getSupplierId(), id));
        return true;
    }

    @org.springframework.transaction.annotation.Transactional
    public Product createProduct(Product p) {
        // associate supplier if provided
        if (p.getSupplier() != null && p.getSupplier().getSupplierId() != null) {
            Supplier s = supplierRepository.findById(p.getSupplier().getSupplierId()).orElse(null);
            p.setSupplier(s);
        }
        Product saved = productRepository.save(p);
        products.put(saved.getProductId(), saved);
        if (saved.getSupplier() != null) {
            Supplier sup = suppliers.get(saved.getSupplier().getSupplierId());
            if (sup != null) sup.getProducts().add(saved);
        }
        return saved;
    }

    @org.springframework.transaction.annotation.Transactional
    public Optional<Product> updateProduct(Long id, Product incoming) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            existing.setUnitPrice(incoming.getUnitPrice());
            existing.setQuantityInStock(incoming.getQuantityInStock());
            existing.setDateSupplied(incoming.getDateSupplied());
            if (incoming.getSupplier() != null && incoming.getSupplier().getSupplierId() != null) {
                Supplier s = supplierRepository.findById(incoming.getSupplier().getSupplierId()).orElse(null);
                existing.setSupplier(s);
            }
            Product saved = productRepository.save(existing);
            products.put(saved.getProductId(), saved);
            return saved;
        });
    }

    @org.springframework.transaction.annotation.Transactional
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        products.remove(id);
        return true;
    }
}
