package com.iknow.iflowtracksysproxy.dto.auth;

import com.iknow.iflowtracksysproxy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private User.Role role;
    private DealerInfoDto dealerInfo;

    @Data
    public static class DealerInfoDto {
        private String businessPartnerId;
        private String dealer;
        private String contactId;
        private String contactName;
    }}
