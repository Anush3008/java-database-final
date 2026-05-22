package com.project.code.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Customer;
import com.project.code.Model.Review;
import com.project.code.Repo.CustomerRepository;
import com.project.code.Repo.ReviewRepository;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId,
                                          @PathVariable Long productId) {

        Map<String, Object> response = new HashMap<>();

        List<Review> reviews = reviewRepository
                .findByStoreIdAndProductId(storeId, productId);

        List<Map<String, Object>> reviewList = new ArrayList<>();

        for (Review review : reviews) {

            Map<String, Object> reviewData = new HashMap<>();

            reviewData.put("comment", review.getComment());
            reviewData.put("rating", review.getRating());

            Customer customer = customerRepository.findById(review.getCustomerId());

            if (customer != null) {
                reviewData.put("customerName", customer.getName());
            } else {
                reviewData.put("customerName", "Unknown");
            }

            reviewList.add(reviewData);
        }

        response.put("reviews", reviewList);

        return response;
    }
}
