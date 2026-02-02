package com.iknow.iflowtracksysproxy.dto;

import com.iknow.iflowtracksysproxy.entity.ReviewType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalDocumentRequestDto {
    private String contractId;
    private String description;
    private ReviewType target;
    private ReviewType source;
}
