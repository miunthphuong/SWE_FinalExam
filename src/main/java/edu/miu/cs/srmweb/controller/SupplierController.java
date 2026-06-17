package edu.miu.cs.srmweb.controller;

import edu.miu.cs.srmweb.dto.ProductDTO;
import edu.miu.cs.srmweb.dto.SupplierDTO;
import edu.miu.cs.srmweb.model.Product;
import edu.miu.cs.srmweb.model.Supplier;
import edu.miu.cs.srmweb.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final DataService dataService;

    @Autowired
    public SupplierController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO> dtos = dataService.getAllSuppliers().stream().map(this::toDtoWithProducts).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private SupplierDTO toDtoWithProducts(Supplier s) {
        SupplierDTO sd = new SupplierDTO(s.getSupplierId(), s.getName(), s.getContactPhone());
        for (Product p : s.getProducts()) {
            ProductDTO pd = new ProductDTO(p.getProductId(), p.getName(), p.getUnitPrice(), p.getQuantityInStock(), p.getDateSupplied(), new SupplierDTO(s.getSupplierId(), s.getName(), s.getContactPhone()));
            sd.addProduct(pd);
        }
        return sd;
    }
}
