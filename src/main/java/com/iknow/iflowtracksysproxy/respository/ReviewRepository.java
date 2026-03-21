package com.iknow.iflowtracksysproxy.respository;


import com.iknow.iflowtracksysproxy.entity.NotificationStatus;
import com.iknow.iflowtracksysproxy.entity.ProformaReview;
import com.iknow.iflowtracksysproxy.entity.ReviewStatus;
import com.iknow.iflowtracksysproxy.entity.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ProformaReview, Long> {

    List<ProformaReview> findByTargetAndStatusAndNotificationStatusOrderByCreatedAtDesc(ReviewType target, ReviewStatus status, NotificationStatus notificationStatus);

    List<ProformaReview> findByTargetAndNotificationStatusOrderByCreatedAtDesc(ReviewType target, NotificationStatus notificationStatus);

    List<ProformaReview> findByNotificationStatusOrderByCreatedAtDesc(NotificationStatus notificationStatus);

    List<ProformaReview> findByContractIdAndAdditionalDocumentPathIsNotNull(String contractId);



}
