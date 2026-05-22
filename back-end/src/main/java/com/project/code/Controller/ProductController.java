package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {

        Map<String, String> response = new HashMap<>();

        try {

            boolean valid = serviceClass.validateProduct(product);

            if (!valid) {
                response.put("message", "Product already exists");
                return response;
            }

            productRepository.save(product);

            response.put("message", "Product added successfully");

        } catch (DataIntegrityViolationException e) {

            response.put("message", "Database error");

        } catch (Exception e) {

            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductbyId(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        Product product = productRepository.findById(id);

        response.put("products", product);

        return response;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {

        Map<String, String> response = new HashMap<>();

        try {

            productRepository.save(product);

            response.put("message", "Product updated successfully");

        } catch (Exception e) {

            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterbyCategoryProduct(@PathVariable String name,
                                                       @PathVariable String category) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products;

        if (name.equals("null")) {

            products = productRepository.findByCategory(category);

        } else if (category.equals("null")) {

            products = productRepository.findProductBySubName(name);

        } else {

            products = productRepository.findProductBySubNameAndCategory(name, category);
        }

        response.put("products", products);

        return response;
    }

    @GetMapping
    public Map<String, Object> listProduct() {

        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findAll();

        response.put("products", products);

        return response;
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductbyCategoryAndStoreId(@PathVariable String category,
                                                              @PathVariable Long storeid) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findProductByCategory(category, storeid);

        response.put("product", products);

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        boolean valid = serviceClass.ValidateProductId(id);

        if (!valid) {

            response.put("message", "Product not present in database");
            return response;
        }

        inventoryRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        response.put("message", "Product deleted successfully");

        return response;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products = productRepository.findProductBySubName(name);

        response.put("products", products);

        return response;
    }
}
