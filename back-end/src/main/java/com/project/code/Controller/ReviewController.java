package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping
    public Map<String, String> saveReview(
            @RequestBody Review review) {

        Map<String, String> response = new HashMap<>();

        reviewRepository.save(review);

        response.put(
                "message",
                "Review saved successfully");

        return response;
    }

    // REQUIRED ENDPOINT
    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(
            @PathVariable Long storeId,
            @PathVariable Long productId) {

        Map<String, Object> response = new HashMap<>();

        List<Review> reviews =
                reviewRepository.findByStoreIdAndProductId(
                        storeId,
                        productId);

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Review review : reviews) {

            Map<String, Object> data =
                    new HashMap<>();

            data.put("review", review.getReview());

            data.put("rating", review.getRating());

            // REQUIRED CUSTOMER NAME FETCH
            Customer customer =
                    customerRepository.findById(
                            review.getCustomerId());

            data.put(
                    "customerName",
                    customer.getName());

            result.add(data);
        }

        response.put("reviews", result);

        return response;
    }

    // REQUIRED ENDPOINT
    @GetMapping("/reviews")
    public Map<String, Object> getAllReviews() {

        Map<String, Object> response =
                new HashMap<>();

        // REQUIRED findAll()
        List<Review> reviews =
                reviewRepository.findAll();

        response.put("reviews", reviews);

        return response;
    }
}
