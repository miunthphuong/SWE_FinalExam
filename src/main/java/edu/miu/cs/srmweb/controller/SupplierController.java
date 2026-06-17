package edu.miu.cs.srmweb.controller;

import edu.miu.cs.srmweb.dto.ProductDTO;
import edu.miu.cs.srmweb.dto.SupplierDTO;
import edu.miu.cs.srmweb.exception.ResourceNotFoundException;
import edu.miu.cs.srmweb.model.Product;
import edu.miu.cs.srmweb.model.Supplier;
import edu.miu.cs.srmweb.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<SupplierDTO> createSupplier(@org.springframework.web.bind.annotation.RequestBody SupplierDTO dto) {
        Supplier s = new Supplier(dto.getSupplierId(), dto.getName(), dto.getContactPhone());
        Supplier saved = dataService.createSupplier(s);
        return ResponseEntity.ok(new SupplierDTO(saved.getSupplierId(), saved.getName(), saved.getContactPhone()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@org.springframework.web.bind.annotation.PathVariable Integer id, @org.springframework.web.bind.annotation.RequestBody SupplierDTO dto) {
        Supplier incoming = new Supplier(id, dto.getName(), dto.getContactPhone());
        var opt = dataService.updateSupplier(id, incoming);
        if (opt.isPresent()) {
            Supplier s = opt.get();
            return ResponseEntity.ok(new SupplierDTO(s.getSupplierId(), s.getName(), s.getContactPhone()));
        } else {
            throw new ResourceNotFoundException("Supplier with id " + id + " not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@org.springframework.web.bind.annotation.PathVariable Integer id) {
        boolean ok = dataService.deleteSupplier(id);
        if (ok) return ResponseEntity.ok().build();
        throw new ResourceNotFoundException("Supplier with id " + id + " not found");
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
