package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    // REQUIRED FILTER ENDPOINT
    @GetMapping("/filter/{category}/{name}/{storeId}")
    public Map<String, Object> filterProducts(
            @PathVariable String category,
            @PathVariable String name,
            @PathVariable Long storeId) {

        Map<String, Object> response = new HashMap<>();

        List<Product> products;

        // REQUIRED CONDITIONAL FILTERING LOGIC
        if (category.equals("null")) {

            products =
                    productRepository.findByNameLike(
                            storeId,
                            name);

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

    // REQUIRED VALIDATION ENDPOINT
    @GetMapping("/validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(
            @PathVariable Integer quantity,
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        Inventory inventory =
                inventoryRepository.findByProductIdAndStoreId(
                        productId,
                        storeId);

        if (inventory == null) {

            return false;
        }

        if (inventory.getStockLevel() >= quantity) {

            return true;
        }

        return false;
    }
}
