package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles;

import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Permissions.Permission;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.Users;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Permissions.Permission.*;
@Getter

public enum ERole {
    DIRECTOR(
            Set.of(
                    findByUserPerDepartment,
                    findByUsername,
                    forgotPassword,
                    getUsersByStatus,
                    fetchAll,
                    findBySn

            )
    ),
   HR(
            Set.of(
                    findByUserPerDepartment,
                    findByUsername,
                    forgotPassword,
                    getUsersByStatus,
                    fetchAll,
                    findBySn
            )
    ),
    SUPERVISOR(
            Set.of(
                    findByUserPerDepartment,
                    findByUsername,
                    forgotPassword,
                    getUsersByStatus,
                    fetchAll,
                    findBySn


            )),
 EMPLOYEE(
            Set.of(
                    forgotPassword,
                    fetchAll,
                    findBySn
            )
    ),
    SUPERUSER(
            Set.of(
                    deleteUserPermanently,
                    findByUserPerDepartment,
                    findByUsername,
                    updateUser,
                    forgotPassword,
                    registerUser,
                    authenticateUser,
                    approveOrReject,
                    getUsersByStatus,
                    delete,
                    fetchAll,
                    findBySn
            )),

   DEVELOPER(
            Set.of(
                    deleteUserPermanently,
                    findByUserPerDepartment,
                    findByUsername,
                    updateUser,
                    forgotPassword,
                    registerUser,
                    authenticateUser,
                    approveOrReject,
                    getUsersByStatus,
                    delete,
                    fetchAll,
                    findBySn
            )),
    APPLICANT(
            Set.of()
    );



    @Getter
    private final Set<Permission> permissions;

    ERole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities( ){
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority(""+ this.name()));
        return authorities;
    }
    @OneToMany(mappedBy = "erole", cascade = CascadeType.ALL)
    private List<Users> users = new ArrayList<>();
}


