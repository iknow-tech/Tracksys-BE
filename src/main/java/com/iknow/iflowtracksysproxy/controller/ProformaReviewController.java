package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.dto.AdditionalDocumentRequestDto;
import com.iknow.iflowtracksysproxy.entity.ProformaReview;
import com.iknow.iflowtracksysproxy.service.ProformaReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/proforma/review")
@RequiredArgsConstructor
public class ProformaReviewController {

    private final ProformaReviewService reviewService;

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

    @PostMapping( "/additional-document")
    public ResponseEntity<ProformaReview> createAdditionalDocumentRequest(@ModelAttribute  AdditionalDocumentRequestDto req, @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(reviewService.createAdditionalDocumentRequest(req, file));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadAdditionalDocument(@PathVariable String id) {

        ProformaReview review = reviewService.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Bildirim bulunamadı"));

        if (review.getAdditionalDocumentPath() == null) {
            throw new RuntimeException("Bu bildirim için ek belge bulunmuyor");
        }

        Path filePath = Paths.get(review.getAdditionalDocumentPath());
        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Dosya okunamadı");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + review.getAdditionalDocumentName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
