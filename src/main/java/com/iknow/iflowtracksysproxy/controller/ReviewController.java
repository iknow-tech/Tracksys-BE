package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.entity.ProformaReview;
import com.iknow.iflowtracksysproxy.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proforma/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ProformaReview> createReview(@RequestBody ProformaReview req) {

        if (req.getDescription() == null || req.getDescription().length() == 0) {
            throw new RuntimeException("Açıklama zorunlu");
        }
       return new ResponseEntity<>(reviewService.createReview(req), HttpStatus.OK) ;
}

    @GetMapping
    public ResponseEntity<List<ProformaReview>> getOpenReviews() {
        return new ResponseEntity<>(reviewService.getOpenReviewsForDealer(), HttpStatus.OK);
    }

    @GetMapping("/purchasing")
    public ResponseEntity<List<ProformaReview>> getOpenReviewsForPurchasing() {
        return new ResponseEntity<>(reviewService.getOpenReviewsForPurchasing(), HttpStatus.OK);
    }

    // Okunan Bildirimler

    @GetMapping("/advisor")
    public ResponseEntity<List<ProformaReview>> getOpenReviewsForAdvisor() {
        return new ResponseEntity<>(reviewService.getOpenReviewsForPurchasing(), HttpStatus.OK);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        reviewService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/purchasing/read")
    public List<ProformaReview> getReadNotificationsForPurchasing() {
        return reviewService.getReadReviewsForPurchasing();
    }

    @GetMapping("/dealer/read")
    public List<ProformaReview> getReadNotificationsForDealer() {
        return reviewService.getReadReviewsForDealer();
    }

    @GetMapping("/read")
    public List<ProformaReview> getReadNotifications() {
        return reviewService.getReadReviews();
    }

    // Ek Belge Talebi

    @PostMapping("/{id}/additional-document")
    public ResponseEntity<Void> requestAdditionalDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file, @RequestParam("description") String description
    ) {
        reviewService.requestAdditionalDocument(id, file, description);
        return ResponseEntity.ok().build();
    }


}
