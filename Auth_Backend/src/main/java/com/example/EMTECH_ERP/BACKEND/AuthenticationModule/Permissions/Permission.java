package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Permissions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    findByUsername,
    findByUserPerDepartment,
    updateUser,
    deleteUserPermanently,
    forgotPassword,
    registerUser,
    authenticateUser,
    approveOrReject,
    getUsersByStatus,
    delete,
    fetchAll,
    findBySn;

    @Getter
    private final String permissions = name();

}
