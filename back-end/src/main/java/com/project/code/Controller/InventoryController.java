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

import com.project.code.DTO.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public Map<String, String> updateInventory(
            @RequestBody CombinedRequest combinedRequest) {

        Map<String, String> response = new HashMap<>();

        try {

            Product product = combinedRequest.getProduct();
            Inventory inventory = combinedRequest.getInventory();

            boolean valid =
                    serviceClass.ValidateProductId(product.getId());

            if (!valid) {

                response.put("message", "Product not available");
                return response;
            }

            Inventory existingInventory =
                    serviceClass.getInventoryId(inventory);

            if (existingInventory != null) {

                productRepository.save(product);

                existingInventory.setStockLevel(
                        inventory.getStockLevel());

                inventoryRepository.save(existingInventory);

                response.put(
                        "message",
                        "Successfully updated product");

            } else {

                response.put("message", "No data available");
            }

        } catch (DataIntegrityViolationException e) {

            response.put("message", "Database error");

        } catch (Exception e) {

            response.put("message", e.getMessage());
        }

        return response;
    }

    @PostMapping
    public Map<String, String> saveInventory(
            @RequestBody Inventory inventory) {

        Map<String, String> response = new HashMap<>();

        try {

            boolean valid =
                    serviceClass.validateInventory(inventory);

            if (!valid) {

                response.put("message", "Data already present");
                return response;
            }

            inventoryRepository.save(inventory);

            response.put("message", "Data saved successfully");

        } catch (DataIntegrityViolationException e) {

            response.put("message", "Database error");

        } catch (Exception e) {

            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/{storeId}")
    public Map<String, Object> getAllProducts(
            @PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products =
                productRepository.findProductsByStoreId(storeId);

        response.put("products", products);

        return response;
    }

    // Required endpoint
    @GetMapping("/filter/{category}/{name}/{storeId}")
    public Map<String, Object> getProductName(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products;

        // Conditional filtering logic
        if (category.equals("null")) {

            products =
                    productRepository.findByNameLike(storeId, name);

        } else if (name.equals("null")) {

            products =
                    productRepository.findByCategoryAndStoreId(
                            storeId,
                            category);

        } else {

            products =
                    productRepository.findByNameAndCategory(
                            storeId,
                            name,
                            category);
        }

        response.put("products", products);

        return response;
    }

    @GetMapping("/search/{name}/{storeId}")
    public Map<String, Object> searchProduct(
            @PathVariable String name,
            @PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products =
                productRepository.findByNameLike(storeId, name);

        response.put("products", products);

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(
            @PathVariable Long id) {

        Map<String, String> response = new HashMap<>();

        boolean valid = serviceClass.ValidateProductId(id);

        if (!valid) {

            response.put(
                    "message",
                    "Product not present in database");

            return response;
        }

        inventoryRepository.deleteByProductId(id);

        productRepository.deleteById(id);

        response.put(
                "message",
                "Product deleted successfully");

        return response;
    }

    // Required validation endpoint
    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(
            @PathVariable Integer quantity,
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        Inventory inventory =
                inventoryRepository.findByProductIdandStoreId(
                        productId,
                        storeId);

        if (inventory == null) {

            return false;
        }

        return inventory.getStockLevel() >= quantity;
    }
}
