package com.project.code.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.code.DTO.PlaceOrderRequestDTO;
import com.project.code.DTO.PurchaseProductDTO;
import com.project.code.Model.Customer;
import com.project.code.Model.Inventory;
import com.project.code.Model.OrderDetails;
import com.project.code.Model.OrderItem;
import com.project.code.Model.Product;
import com.project.code.Model.Store;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderDetailsRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Repo.StoreRepository;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // Find customer by email
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getEmail());

        // Create customer if not exists
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getName());
            customer.setEmail(placeOrderRequest.getEmail());
            customer.setPhone(placeOrderRequest.getPhone());

            customerRepository.save(customer);
        }

        // Find store
        Optional<Store> optionalStore = storeRepository.findById(placeOrderRequest.getStoreId());

        if (!optionalStore.isPresent()) {
            throw new RuntimeException("Store not found");
        }

        Store store = optionalStore.get();

        // Create order details
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails.setDate(LocalDateTime.now());

        // Save order details to database
        orderDetailsRepository.save(orderDetails);

        // Process all ordered products
        for (PurchaseProductDTO purchaseProduct : placeOrderRequest.getPurchaseProduct()) {

            // Find product
            Optional<Product> optionalProduct =
                    productRepository.findById(purchaseProduct.getProductId());

            if (!optionalProduct.isPresent()) {
                throw new RuntimeException("Product not found");
            }

            Product product = optionalProduct.get();

            // Find inventory for product and store
            Inventory inventory = inventoryRepository
                    .findByProductIdandStoreId(
                            product.getId(),
                            store.getId());

            if (inventory == null) {
                throw new RuntimeException("Inventory not found");
            }

            // Reduce inventory stock
            inventory.setStockLevel(
                    inventory.getStockLevel() - purchaseProduct.getQuantity());

            // Save updated inventory
            inventoryRepository.save(inventory);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderDetails(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(purchaseProduct.getQuantity());
            orderItem.setPrice(product.getPrice());

            // Save order item
            orderItemRepository.save(orderItem);
        }
    }
}
