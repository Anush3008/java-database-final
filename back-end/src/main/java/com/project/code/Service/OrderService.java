package com.project.code.Service;

import java.time.LocalDateTime;

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

        // Find customer
        Customer customer =
                customerRepository.findByEmail(
                        placeOrderRequest.getEmail());

        // Create customer if not present
        if (customer == null) {

            customer = new Customer();

            customer.setName(placeOrderRequest.getName());
            customer.setEmail(placeOrderRequest.getEmail());
            customer.setPhone(placeOrderRequest.getPhone());

            customerRepository.save(customer);
        }

        // Find store
        Store store =
                storeRepository.findById(
                        placeOrderRequest.getStoreId());

        if (store == null) {

            throw new RuntimeException("Store not found");
        }

        // Create order details
        OrderDetails orderDetails = new OrderDetails();

        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setTotalPrice(
                placeOrderRequest.getTotalPrice());

        orderDetails.setDate(LocalDateTime.now());

        // REQUIRED: Save order details
        orderDetailsRepository.save(orderDetails);

        // Process ordered products
        for (PurchaseProductDTO purchaseProduct :
                placeOrderRequest.getPurchaseProduct()) {

            // Find product
            Product product =
                    productRepository.findById(
                            purchaseProduct.getProductId());

            if (product == null) {

                throw new RuntimeException("Product not found");
            }

            // Find inventory
            Inventory inventory =
                    inventoryRepository.findByProductIdandStoreId(
                            product.getId(),
                            store.getId());

            if (inventory == null) {

                throw new RuntimeException("Inventory not found");
            }

            // REQUIRED: Reduce stock level
            inventory.setStockLevel(
                    inventory.getStockLevel()
                    - purchaseProduct.getQuantity());

            // REQUIRED: Save updated inventory
            inventoryRepository.save(inventory);

            // Save order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrderDetails(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(
                    purchaseProduct.getQuantity());

            orderItem.setPrice(product.getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}
