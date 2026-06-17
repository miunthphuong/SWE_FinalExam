package edu.miu.cs.srmweb.controller;

import edu.miu.cs.srmweb.dto.ProductDTO;
import edu.miu.cs.srmweb.dto.SupplierDTO;
import edu.miu.cs.srmweb.model.Product;
import edu.miu.cs.srmweb.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final DataService dataService;

    @Autowired
    public ProductController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> dtos = dataService.getAllProductsSortedByName().stream().map(this::toDtoWithSupplier).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/product/get/supplier/{supplierId}")
    public ResponseEntity<?> getProductsBySupplier(@PathVariable int supplierId) {
        var opt = dataService.findSupplierById(supplierId);
        if (opt.isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Supplier with id " + supplierId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }
        List<ProductDTO> products = dataService.getProductsBySupplierId(supplierId).stream().map(this::toDtoWithSupplier).collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    private ProductDTO toDtoWithSupplier(Product p) {
        SupplierDTO sd = new SupplierDTO(p.getSupplier().getSupplierId(), p.getSupplier().getName(), p.getSupplier().getContactPhone());
        return new ProductDTO(p.getProductId(), p.getName(), p.getUnitPrice(), p.getQuantityInStock(), p.getDateSupplied(), sd);
    }
}
