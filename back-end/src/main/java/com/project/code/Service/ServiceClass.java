package com.project.code.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;

@Service
public class ServiceClass {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean validateInventory(Inventory inventory) {

        Inventory inventoryData = inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId());

        if (inventoryData != null) {
            return false;
        }

        return true;
    }

    public boolean validateProduct(Product product) {

        Product productData = productRepository.findByName(product.getName());

        if (productData != null) {
            return false;
        }

        return true;
    }

    public boolean ValidateProductId(long id) {

        Product product = productRepository.findById(id);

        if (product == null) {
            return false;
        }

        return true;
    }

    public Inventory getInventoryId(Inventory inventory) {

        return inventoryRepository.findByProductIdandStoreId(
                inventory.getProduct().getId(),
                inventory.getStore().getId());
    }
}
