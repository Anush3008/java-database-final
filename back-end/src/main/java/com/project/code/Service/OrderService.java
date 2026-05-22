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

    public void saveOrder(
            PlaceOrderRequestDTO placeOrderRequest) {

        Customer customer =
                customerRepository.findByEmail(
                        placeOrderRequest.getEmail());

        if (customer == null) {

            customer = new Customer();

            customer.setName(
                    placeOrderRequest.getName());

            customer.setEmail(
                    placeOrderRequest.getEmail());

            customer.setPhone(
                    placeOrderRequest.getPhone());

            customerRepository.save(customer);
        }

        Store store =
                storeRepository.findById(
                        placeOrderRequest.getStoreId());

        OrderDetails orderDetails =
                new OrderDetails();

        orderDetails.setCustomer(customer);

        orderDetails.setStore(store);

        orderDetails.setTotalPrice(
                placeOrderRequest.getTotalPrice());

        orderDetails.setDate(
                LocalDateTime.now());

        // REQUIRED SAVE
        orderDetailsRepository.save(orderDetails);

        for (PurchaseProductDTO purchaseProduct :
                placeOrderRequest.getPurchaseProduct()) {

            Product product =
                    productRepository.findById(
                            purchaseProduct.getProductId());

            Inventory inventory =
                    inventoryRepository
                    .findByProductIdAndStoreId(
                            product.getId(),
                            store.getId());

            // REQUIRED STOCK REDUCTION
            inventory.setStockLevel(
                    inventory.getStockLevel()
                    - purchaseProduct.getQuantity());

            // REQUIRED INVENTORY SAVE
            inventoryRepository.save(inventory);

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrderDetails(
                    orderDetails);

            orderItem.setProduct(product);

            orderItem.setQuantity(
                    purchaseProduct.getQuantity());

            orderItem.setPrice(
                    product.getPrice());

            orderItemRepository.save(orderItem);
        }
    }
}
