package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleApprovalRequest {
    private Long id;
    private String status;
    private String remarks;
}
