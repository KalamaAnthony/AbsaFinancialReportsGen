package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Auditing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "audittrails")
public class Auditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date loginTime;
    private Date logoutTime;
    private String username;
    private String requestip;
    private String activity;
    private String address;
    private String os;
    private String browser;
    private String status;
    private String action;

    public void setIpAddress(String remoteAddr) {
    }
}
