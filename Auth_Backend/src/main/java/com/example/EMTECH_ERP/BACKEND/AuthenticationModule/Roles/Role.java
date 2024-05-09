package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Permissions.Permission;
import com.example.EMTECH_ERP.BACKEND.Utils.Enum.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false, unique = true)
    private String name;
    @JsonIgnore
    @Column(name = "deleteFlag", length = 5)
    private Character deletedFlag =('N');
    @Column(name = "deleteby", length = 5)
    @JsonIgnore
    private String deletedBy;
    @Column(name = "deleteOn", length = 50)
    @JsonIgnore
    private LocalDateTime deletedOn;
    @JsonIgnore
    @Column(name = "Added", length = 5)
    private Character addedflag =('Y');
    @Column(name = "addedBy", length = 5)
    @JsonIgnore
    private String addedBy =("USER");
    @JsonIgnore
    @Column(name = "addedOn", length = 50)
    private LocalDateTime addedOn;
//    @ManyToMany
//    @JoinTable(
//            name = "role_permission",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id")
//    )
    @JsonIgnore
    @ElementCollection
    private Set<String> permissions = new HashSet<>();

    public void addPermission(String permission) {
        permissions.add(permission);
    }
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private StatusEnum approvalStatus = StatusEnum.PENDING;
//
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//    }

//    public StatusEnum getApprovalStatus() {
//        return approvalStatus;
//    }
//
//    public void setApprovalStatus(StatusEnum approvalStatus) {
//        this.approvalStatus = approvalStatus;
//    }
//    public Set<Permission> getPermissions() {
//        return permissions;
//    }
//
//    public void setPermissions(Set<Permission> permissions) {
//        this.permissions = permissions;
//    }
//
//    public void addPermission(java.security.Permission permission) {
//    }
//public List<SimpleGrantedAuthority> getAuthorities( ){
//    var authorities = getPermissions()
//            .stream()
//            .map(permission -> new SimpleGrantedAuthority(permission.wait()))
//            .collect(Collectors.toList());
//    authorities.add(new SimpleGrantedAuthority(""+ this.wait()));
//    return authorities;
//}
}
