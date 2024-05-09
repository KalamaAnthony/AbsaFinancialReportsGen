package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles;

import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Permissions.RolePermissionsConfig;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.Users;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.UsersRepository;
import com.example.EMTECH_ERP.BACKEND.Utils.Enum.StatusEnum;
import com.example.EMTECH_ERP.BACKEND.Utils.Shared.EntityResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;
import java.security.Permission;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j

public class RoleService {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    private final RolePermissionsConfig rolePermissionsConfig;

    public RoleService(RolePermissionsConfig rolePermissionsConfig) {
        this.rolePermissionsConfig = rolePermissionsConfig;
        this.roleRepository = roleRepository;}

    public EntityResponse add(Role role) {
        EntityResponse entityResponse = new EntityResponse<>();
        try {
            // Set the initial approval status to PENDING and the addedOn timestamp before saving
            role.setApprovalStatus(StatusEnum.PENDING);
            role.setAddedOn(LocalDateTime.now());

            // Now save the role with the updated properties
            Role savedRole = roleRepository.save(role);
            entityResponse.setMessage("Role created successfully and is pending approval");
            entityResponse.setStatusCode(HttpStatus.CREATED.value());
            entityResponse.setEntity(savedRole);
        } catch (Exception e) {
            log.error("An error has occurred while trying to create a ROLE! {} ", e);
            entityResponse.setMessage("Failed to create role");
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return entityResponse;
    }



    public EntityResponse addBulk(Collection<Role> roles) {
        EntityResponse entityResponse = new EntityResponse<>();
        List<Role> savedRoles = new ArrayList<>();

        try {
            for (Role role : roles) {

                role.setApprovalStatus(StatusEnum.PENDING);

                Optional<Role> existingRoleOptional = roleRepository.findByName(role.getName());
                if (existingRoleOptional.isPresent()) {

                    log.warn("Role with name '{}' already exists. Skipping creation.", role.getName());
                    role.setAddedBy("User");
                    role.setAddedflag('Y');
                    role.setAddedOn(LocalDateTime.now());
                    role.setApprovalStatus(StatusEnum.PENDING);
                } else {

                    Role savedRole = roleRepository.save(role);
                    savedRoles.add(savedRole);
                }
            }
            entityResponse.setMessage("Roles created successfully awaiting Approval");
            entityResponse.setStatusCode(HttpStatus.CREATED.value());
            entityResponse.setEntity(savedRoles);



        } catch (Exception e) {
            // Log error
            log.error("An error has occurred while trying to create roles! {} ", e);
            // Set error response
            entityResponse.setMessage("An error occurred while creating roles");
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return entityResponse;
    }
    public EntityResponse deleteRole(Long id) {
        EntityResponse entityResponse = new EntityResponse<>();
        try {

            Optional<Role> optionalRole = roleRepository.findById(id);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                role.setDeletedBy("true");
                role.setDeletedOn(LocalDateTime.now());
                role.setDeletedFlag('Y');
                role.setApprovalStatus(StatusEnum.PENDING);
                roleRepository.save(role);

                roleRepository.delete(role);
                entityResponse.setMessage("Role deleted successfully");
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setEntity(role);
            } else {
                // Role not found
                entityResponse.setMessage("Role not found");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            log.error("An error occurred while deleting role with id: {}", id, e);
            entityResponse.setMessage("Failed to delete role");
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return entityResponse;
    }
    public EntityResponse findAllRoles() {
        EntityResponse response = new EntityResponse<>();
        try {
            List<Role> roles = roleRepository.findAll();
            if (!roles.isEmpty()) {
                response.setMessage("Roles retrieved successfully");
                response.setStatusCode(HttpStatus.FOUND.value());
                // Include permissions along with roles in the response
                List<Map<String, Object>> rolesWithPermissions = roles.stream().map(role -> {
                    Map<String, Object> roleWithPermissions = new HashMap<>();
                    roleWithPermissions.put("id", role.getId());
                    roleWithPermissions.put("name", role.getName());
                    roleWithPermissions.put("permissions", role.getPermissions());
                    return roleWithPermissions;
                }).collect(Collectors.toList());
                response.setEntity(rolesWithPermissions);
            } else {
                response.setMessage("No roles found");
                response.setStatusCode(HttpStatus.NO_CONTENT.value());
                response.setEntity("");
            }
        } catch (Exception e) {
            log.error("Exception: {}", e);
            // Optionally, you can set an error message and status code if an exception occurs
            response.setMessage("Error occurred while fetching roles");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

//    @Value("${permissions}")
//    private String permissionsString;
//
//    public void updateRolePermissions(String roleName, Set<String> newPermissions) {
//        // Find the role by name
//        Optional<Role> optionalRole = roleRepository.findByName(roleName);
//        if (!optionalRole.isPresent()) {
//            throw new IllegalArgumentException("Role not found: " + roleName);
//        }
//        Role role = optionalRole.get();
//
//        // Update the role's permissions
////        role.setPermissions(Permissions);
//        roleRepository.save(role);
//    }

    public void assignPermissionsToRoles() {
        Map<String, String> permissions = rolePermissionsConfig.getPermissions();
        permissions.forEach(this::assignPermissionToRole);
    }
    private void assignPermissionToRole(String endpoint, String roleName) {
        Optional<Role> optionalRole = roleRepository.findByName(roleName);
        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            Set<String> rolePermissions = role.getPermissions();
            if (!rolePermissions.contains(endpoint)) {
                rolePermissions.add(endpoint);
                roleRepository.save(role);
                System.out.println("Assigned permission '" + endpoint + "' to role '" + roleName + "'");
            } else {
                System.out.println("Permission '" + endpoint + "' already assigned to role '" + roleName + "'");
            }
        } else {
            System.out.println("Role not found: " + roleName);
        }
    }

    public EntityResponse changeUserRoles(String username, String roleName) {
        EntityResponse entityResponse = new EntityResponse<>();
        try {
            // Find the user by username
            Optional<Users> optionalUser = usersRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();

                // Find the role by name
                Optional<Role> optionalRole = roleRepository.findByName(roleName);
                if (optionalRole.isPresent()) {
                    // Set the new roles for the user
                    Set<Role> newRoles = new HashSet<>();
                    newRoles.add(optionalRole.get());
                    user.setRoles(newRoles);

                    // Save the updated user
                    usersRepository.save(user);

                    entityResponse.setMessage("User roles updated successfully");
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setEntity(user);
                } else {
                    // Role not found
                    entityResponse.setMessage("Role not found");
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                }
            } else {
                // User not found
                entityResponse.setMessage("User not found");
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
        } catch (Exception e) {
            // Log error
            log.error("An error occurred while trying to update user roles! {}", e);
            // Set error response
            entityResponse.setMessage("An error occurred while updating user roles");
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return entityResponse;
    }
//    public EntityResponse manageRoleStatus(Long id, StatusEnum action) {
//        EntityResponse entityResponse = new EntityResponse<>();
//        try {
//            Optional<Role> optionalRole = roleRepository.findById(id);
//            if (optionalRole.isPresent()) {
//                Role role = optionalRole.get();
//                role.setApprovalStatus(action);
//                roleRepository.save(role);
//
//                String message = "Role status updated successfully";
//                switch (action) {
//                    case APPROVED:
//                        message = "Role approved successfully";
//                        break;
//                    case REJECTED:
//                        message = "Role rejected successfully";
//                        break;
//                    case RETURNED:
//                        message = "Role returned successfully";
//                        break;
//                    case PROCESSING:
//                        message = "Role processing initiated";
//                        break;
//                    default:
//                        message = "Role status updated";
//                }
//
//                entityResponse.setMessage(message);
//                entityResponse.setStatusCode(HttpStatus.OK.value());
//                entityResponse.setEntity(role);
//            } else {
//                entityResponse.setMessage("Role not found");
//                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
//            }
//        } catch (Exception e) {
//            log.error("An error occurred while trying to manage role status! {}", e);
//            entityResponse.setMessage("Failed to manage role status");
//            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//        return entityResponse;
//    }


}


