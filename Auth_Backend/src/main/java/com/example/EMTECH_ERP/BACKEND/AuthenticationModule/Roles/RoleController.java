package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles;

import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Requests.RoleApprovalRequest;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.Users;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.UsersRepository;
import com.example.EMTECH_ERP.BACKEND.Utils.Enum.StatusEnum;
import com.example.EMTECH_ERP.BACKEND.Utils.Shared.EntityResponse;
import com.example.EMTECH_ERP.BACKEND.Utils.Shared.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/role")
@CrossOrigin
@Slf4j
public class RoleController {
    @Autowired
    RoleService roleService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UsersRepository usersRepository;

    @PostMapping("/add")
    public EntityResponse add(@RequestBody Role role) {
        return roleService.add(role);
    }

    @PostMapping("/addBulk")
    public EntityResponse addBulk(@RequestBody List<Role> roles) {
        return roleService.addBulk(roles);
    }

    @DeleteMapping("/delete")
    public EntityResponse deleteRole(@RequestParam Long id) {
        return roleService.deleteRole(id);
    }

    @GetMapping("/find/all/roles")
    public EntityResponse findAllRoles() {
        return roleService.findAllRoles();
    }
//    @PostMapping("/manageStatus/{roleId}")
//    public ResponseEntity<EntityResponse> manageRoleStatus(@PathVariable Long id, @RequestParam StatusEnum action) {
//        EntityResponse response = roleService.manageRoleStatus(id, action);
//        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignPermissionsToRoles() {
        roleService.assignPermissionsToRoles();
        return ResponseEntity.ok("Permissions assigned to roles successfully");
    }



    @PutMapping("/approveOrRejectRoles")
//        public ResponseEntity<?> approveOrRejectRoles(@RequestBody List<RoleApprovalRequest> roleApprovalRequests) {
//            EntityResponse response = new EntityResponse<>();
//            try {
//                if (roleApprovalRequests.isEmpty()) {
//                    response.setMessage("You must provide at least one role approval request");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
//                }
//
//                List<Role> updatedRoles = new ArrayList<>();
//                for (RoleApprovalRequest request : roleApprovalRequests) {
//                    Optional<Role> optionalRole = roleRepository.findById(request.getId());
//                    if (optionalRole.isPresent()) {
//                        Role role = optionalRole.get();
//                        String upperCaseStatus = request.getStatus().toUpperCase();
//                        switch (upperCaseStatus) {
//                            case "APPROVED":
//                                role.setApprovalStatus(StatusEnum.APPROVED);
//                                break;
//                            case "REJECTED":
//                                role.setApprovalStatus(StatusEnum.REJECTED);
//                                break;
//                            default:
//                                response.setMessage("Invalid status provided: " + request.getStatus());
//                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
//                        }
//                        // Assuming you want to save remarks or any other additional information
//                        // role.setRemarks(request.getRemarks());
//                        updatedRoles.add(roleRepository.save(role));
//                    } else {
//                        response.setMessage("No role found with ID: " + request.getId());
//                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
//                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//                    }
//                }
//                response.setMessage("Role status updated successfully");
//                response.setStatusCode(HttpStatus.OK.value());
//                response.setEntity(updatedRoles);
//            } catch (Exception e) {
//                log.error("An unexpected error occurred while updating role status: {}", e.toString());
//                response.setMessage("An unexpected error occurred while updating role status");
//                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//            }
//            return ResponseEntity.ok(response);
//        }
    public ResponseEntity<?> approveOrRejectRoles(@RequestBody List<RoleApprovalRequest> roleApprovalRequests) {
        EntityResponse response = new EntityResponse<>();
        try {
            if (roleApprovalRequests.isEmpty()) {
                response.setMessage("You must provide at least one role approval request");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
            }

            List<Role> updatedRoles = new ArrayList<>();
            for (RoleApprovalRequest request : roleApprovalRequests) {
                Optional<Role> optionalRole = roleRepository.findById(request.getId());
                if (optionalRole.isPresent()) {
                    Role role = optionalRole.get();
                    // Directly set the status from the request
                   // role.setApprovalStatus(request.getStatus());
                    updatedRoles.add(roleRepository.save(role));
                } else {
                    response.setMessage("No role found with ID: " + request.getId());
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            }
            response.setMessage("Role status updated successfully");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(updatedRoles);
        } catch (Exception e) {
            log.error("An unexpected error occurred while updating role status: {}", e.toString());
            response.setMessage("An unexpected error occurred while updating role status");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @PostMapping("/api/v1/user/roles/change")
    public ResponseEntity<EntityResponse> changeUserRoles(@RequestParam String username, @RequestParam String roleName) {
        EntityResponse response = new EntityResponse<>();
        HttpStatus status;
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

                    response.setMessage("User roles updated successfully");
                    status = HttpStatus.OK;
                } else {
                    // Role not found
                    response.setMessage("Role not found");
                    status = HttpStatus.NOT_FOUND;
                }
            } else {
                // User not found
                response.setMessage("User not found");
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            // Log error
            log.error("An error occurred while trying to update user roles! {}", e);
            // Set error response
            response.setMessage("An error occurred while updating user roles");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, status);
    }

}