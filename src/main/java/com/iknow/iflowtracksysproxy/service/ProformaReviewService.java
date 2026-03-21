package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.AdditionalDocumentRequestDto;
import com.iknow.iflowtracksysproxy.entity.NotificationStatus;
import com.iknow.iflowtracksysproxy.entity.ProformaReview;
import com.iknow.iflowtracksysproxy.entity.ReviewStatus;
import com.iknow.iflowtracksysproxy.entity.ReviewType;
import com.iknow.iflowtracksysproxy.respository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProformaReviewService {

    private final ReviewRepository proformaReviewRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ProformaReview createReview(@RequestBody ProformaReview proformaReview) {
        if (proformaReview.getDescription() == null || proformaReview.getDescription().trim().length() < 5) {
            throw new RuntimeException("Açıklama zorunlu");
        }
        try {
            ProformaReview entity =
                    ProformaReview.builder()
                            .contractId(proformaReview.getContractId())
                            .description(proformaReview.getDescription().trim())
                            .status(ReviewStatus.OPEN)
                            .notificationStatus(NotificationStatus.NEW)
                            .target(proformaReview.getTarget())
                            .createdAt(LocalDateTime.now())
                            .build();

            return proformaReviewRepository.save(entity);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<ProformaReview> getOpenReviewsForDealer() {
        return proformaReviewRepository.findByTargetAndStatusAndNotificationStatusOrderByCreatedAtDesc(ReviewType.DEALER, ReviewStatus.OPEN, NotificationStatus.NEW);
    }

    public List<ProformaReview> getOpenReviewsForPurchasing() {
        return proformaReviewRepository.findByTargetAndStatusAndNotificationStatusOrderByCreatedAtDesc(ReviewType.PURCHASING, ReviewStatus.OPEN, NotificationStatus.NEW);
    }

    @Transactional
    public void markAsRead(Long id) {

        ProformaReview review = proformaReviewRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bildirim bulunamadı"));

        if (review.getStatus().equals(ReviewStatus.RESOLVED.toString())) {
            return;
        }

        review.setNotificationStatus(NotificationStatus.READ);
        review.setResolvedAt(LocalDateTime.now());

        proformaReviewRepository.save(review);
    }


    public List<ProformaReview> getReadReviewsForPurchasing() {
        return proformaReviewRepository.findByTargetAndNotificationStatusOrderByCreatedAtDesc(ReviewType.PURCHASING, NotificationStatus.READ);
    }

    public List<ProformaReview> getReadReviewsForDealer() {
        return proformaReviewRepository.findByTargetAndNotificationStatusOrderByCreatedAtDesc(ReviewType.DEALER, NotificationStatus.READ);
    }

    public List<ProformaReview> getReadReviews() {
        return proformaReviewRepository.findByNotificationStatusOrderByCreatedAtDesc(NotificationStatus.READ);
    }

    @Transactional
    public ProformaReview createAdditionalDocumentRequest(AdditionalDocumentRequestDto req, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Ek belge zorunludur");
        }

        String path = fileStorageService.store(file);

        ProformaReview entity = ProformaReview.builder()
                .contractId(req.getContractId())
                .description(req.getDescription())
                .target(req.getTarget() != null ? req.getTarget() : ReviewType.DEALER)
                .source(req.getSource() != null ? req.getSource() : ReviewType.PURCHASING)
                .status(ReviewStatus.OPEN)
                .notificationStatus(NotificationStatus.NEW)
                // .additionalDocumentRequested(true)
                .additionalDocumentPath(path)
                .additionalDocumentName(file.getOriginalFilename())
                .createdAt(LocalDateTime.now())
                .build();

        return proformaReviewRepository.save(entity);
    }

    public Optional<ProformaReview> findById(Long id) {
        return proformaReviewRepository.findById(id);

    }

    public List<ProformaReview> getAdditionalDocumentsByContractId(String contractId) {
        return proformaReviewRepository.findByContractIdAndAdditionalDocumentPathIsNotNull(contractId);
    }

}
