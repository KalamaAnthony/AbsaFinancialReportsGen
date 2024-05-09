package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.ERole;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.ERole;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.Role;
import com.example.EMTECH_ERP.BACKEND.Utils.Enum.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Table(name = "users")
public class Users implements UserDetails {
    @Id
    @SequenceGenerator(name = "user_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(name = "sn", updatable = false)
    private Long sn;
    @Column(name = "username", length = 40, unique = true, nullable = false)
    private String username;
    public void generateUsername() {
        // Skip username generation for superuser
        if (!"superuser".equals(this.username) && systemGenPassword && firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            // Generate a random integer
            Random random = new Random();
            int randomNumber = random.nextInt(99) + 1000; // Generates a random number between 100 and 999

            // Combine the first letters of both names and the random number
            String generatedUsername = firstName.substring(0, 1) + lastName.substring(0, 1) + randomNumber;

            // Convert the generated username to uppercase
            this.username = generatedUsername.toUpperCase();
        } else if (!"superuser".equals(this.username) && systemGenPassword) {
            // Handle case where first name or last name is not set
            this.username = UUID.randomUUID().toString().toUpperCase();
        }
    }

    // Call generateUsername in the constructor or setters for firstName and lastName
    public Users(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        generateUsername();
    }

    // Override setters for firstName and lastName to regenerate the username
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        generateUsername();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        generateUsername();
    }
    @Column(name = "firstname",  length = 50)
    private String firstName;
    @Column(name = "lastname", length = 50)
    private String lastName;
    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;
    @Column(name = "phone", length = 15)
    private String phoneNo;
    @Column(name = "password", length = 255   , nullable = false)
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "password_history", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "password", length = 255)
    private List<String> passwordHistory = new ArrayList<>();


    @Column(name = "createdOn", length = 50)
    private Date createdOn;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> permissions = new HashSet<>();
    @Column(name = "modifiedBy", length = 50)
    private String modifiedBy;
    @Column(name = "modifiedOn", length = 50)
    private Date modifiedOn;
    @Column(name = "verifiedBy", length = 50)
    private String verifiedBy;
    @Column(name = "verifiedOn", length = 50)
    private Date verifiedOn;
    @Column(name = "verifiedFlag", length = 5)
    private Character verifiedFlag;
    @Column(name = "deleteFlag", length = 5)
    private Character deletedFlag;
    @Column(name = "deleteby", length = 5)
    private String deletedBy;
    @Column(name = "deleteOn", length = 50)
    private Date deleteOn;
    @Column(name = "deletedOn", length = 50)
    private LocalDateTime deletedOn;
    @Column(name = "active", length = 50)
    private boolean isAcctActive;
    @Column(columnDefinition = "boolean default false",name = "loggedin")
    private boolean isLoggedIn;
    @Column(name = "first_login", length = 1)
    private Character firstLogin = 'Y';
    @Column(name = "locked", length = 15)
    private boolean AcctLocked = false;
    private boolean systemGenPassword = true;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    //Operational Audit
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status = StatusEnum.PENDING.toString();
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String adminApprovedBy;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Character approvedFlag;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date approvedTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY )
    private String remarks;
    private ERole role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (role != null) {

            return role.getAuthorities();
        } else {

            return Collections.emptyList();
        }
    }

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts =0;

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }




}

