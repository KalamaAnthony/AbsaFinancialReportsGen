package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Resources;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Auditing.AuditRepository;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Auditing.AuditTrailsController;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Auditing.Auditing;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.MailService.MailService;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.MailService.MailsService;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.OTP.OTP;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.OTP.OTPRepository;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.OTP.OTPService;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Requests.*;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Responses.JwtResponse;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Responses.MessageResponse;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.ERole;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.Role;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.RoleRepository;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Security.Jwt.JwtUtils;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Security.Services.UserDetailsImpl;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.UserStatusDTO;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.Users;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.UsersRepository;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.UsersService;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.utils.PasswordGeneratorUtil;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.utils.RequestIpContext;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.utils.RequestUsernameContext;
import com.example.EMTECH_ERP.BACKEND.Utils.Shared.EntityResponse;
import com.example.EMTECH_ERP.BACKEND.Utils.Shared.UserRequestContext;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
//import java.time.LocalDate;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@CrossOrigin
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsersRepository userRepository;
    @Autowired
    UsersService usersService;
    @Autowired
    AuditTrailsController auditTrailsController;

    @Autowired
    RoleRepository roleRepository;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String modifiedOn = dtf.format(now);


//    @Autowired
//    EmployeeRepository employeeRepository;

    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    OTPService otpService;

    @Autowired
    MailService mailService;
    @Autowired
    MailsService mailsService;
    @Value("${from_mail}")
    private String fromEmail;
    @Value("${emailOrganizationName}")
    private String emailOrganizationName;
    @Value("${spring.application.useOTP}")
    private String useOTP;
    @Autowired
    OTPRepository otpRepository;
    @Autowired
    AuditRepository auditRepository;

    //    @PostMapping("/signup")
//    public EntityResponse<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {
//        EntityResponse response = new EntityResponse<>();
//
//
//        try {
//            if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())){
//                response.setMessage("Passwords do not match. Please ensure that passwords match.");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//
//            }
//            if (userRepository.existsByUsername(signUpRequest.getUsername())){
//                response.setMessage("User name is already taken.");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//                response.setMessage("Email is already taken.");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            } else {
//                Users user = new Users();
//                user.setUsername(signUpRequest.getUsername());
//                user.setEmail(signUpRequest.getEmail());
//                user.setPassword(encoder.encode(signUpRequest.getPassword()));
//                Set<String> strRoles = signUpRequest.getRole();
//                Set<Role> roles = new HashSet<>();
//
//                if (strRoles == null || strRoles.isEmpty()) {
//                    Role userRole = roleRepository.findByName(ERole.ROLE_APPLICANT.toString())
//                            .orElseThrow(() -> new RuntimeException("Role not found."));
//                    roles.add(userRole);
//                } else {
//                    for (String role : signUpRequest.getRole()) {
//                        try {
//                            Role userRole = roleRepository.findByName(role)
//                                    .orElseThrow(() -> new RuntimeException("Role not found."));
//                            roles.add(userRole);
//                        } catch (RuntimeException e) {
//                            response.setMessage("Role not found: " + role);
//                            return response;
//                        }
//                    }
//                }
//
//                user.setRoles(roles);
//                user.setCreatedOn(new Date());
//                user.setDeleteFlag('N');
//                user.setAcctActive(true);
//                user.setAcctLocked(false);
//                user.setVerifiedFlag('Y');
//                user.setFirstLogin('Y');
//                user.setVerifiedOn(new Date());
//                user.setEmail(signUpRequest.getEmail());
//                user.setFirstName(signUpRequest.getFirstName());
//                user.setLastName(signUpRequest.getLastName());
//                user.setPhoneNo(signUpRequest.getPhoneNo());
//
//                Users users = userRepository.save(user);
//
////                String mailMessage = "Dear " + user.getFirstName() + " your account has been successfully created using username " + user.getUsername()
//                   String mailMessage = "<p>Dear <strong>" + user.getFirstName() +"</strong>,</p>\n" +
//                       " Your account has been successfully created using username " + user.getUsername()
//                        + " and password " + signUpRequest.getPassword();
//                String subject = "Account creation";
//
//                mailService.sendEmail(users.getEmail(),null,mailMessage,subject,false,null,null);
//                response.setMessage("User " + user.getUsername() + " registered successfully!");
//                response.setStatusCode(HttpStatus.CREATED.value());
//                response.setEntity(users);
//
//                return response;
//
//            }
//        } catch (Exception e) {
//            response.setMessage("An error occurred during user registration.");
//            return null;
//        }
//    }
    @PostMapping("/admin/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
        System.out.println("Authentication----------------------------------------------------------------------");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Users user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        log.info("Username is {}", loginRequest.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        Cookie jwtTokenCookie = new Cookie("user-id", "c2FtLnNtaXRoQGV4YW1wbGUuY29t");
        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        jwtTokenCookie.setPath("/user/");
        res.addCookie(jwtTokenCookie);
        Cookie accessTokenCookie = new Cookie("accessToken", jwt);
        accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setHttpOnly(true);
        res.addCookie(accessTokenCookie);
        Cookie userNameCookie = new Cookie("username", loginRequest.getUsername());
        accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
        res.addCookie(userNameCookie);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
//        String otp = "Your otp code is " + otpService.generateOTP(userDetails.getUsername());
//        mailService.sendEmail(userDetails.getEmail(), otp, "OTP Code");

        JwtResponse response = new JwtResponse();
        response.setToken(jwt);
        response.setType("Bearer");
        response.setId(userDetails.getId());
        response.setUsername(userDetails.getUsername());
        response.setEmail(userDetails.getEmail());
        response.setRoles(roles);
//        response.setSolCode(user.getSolCode());
//        response.setEmpNo(user.getEmpNo());
        response.setFirstLogin(user.getFirstLogin());
//        response.setRoleClassification(user.getRoleClassification());
        response.setIsAcctActive(userDetails.getAcctActive());
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/signin")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
//        EntityResponse response = new EntityResponse<>();
//
//        try {
//            System.out.println("Authentication----------------------------------------------------------------------");
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//            Users user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String jwt = jwtUtils.generateJwtToken(authentication);
//            Cookie jwtTokenCookie = new Cookie("user-id", "c2FtLnNtaXRoQGV4YW1wbGUuY29t");
//            jwtTokenCookie.setMaxAge(86400);
//            jwtTokenCookie.setSecure(true);
//            jwtTokenCookie.setHttpOnly(true);
//            jwtTokenCookie.setPath("/user/");
//            res.addCookie(jwtTokenCookie);
//            Cookie accessTokenCookie = new Cookie("accessToken", jwt);
//            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
//            accessTokenCookie.setSecure(true);
//            accessTokenCookie.setHttpOnly(true);
//            res.addCookie(accessTokenCookie);
//            Cookie userNameCookie = new Cookie("username", loginRequest.getUsername());
//            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
//            res.addCookie(userNameCookie);
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            List<String> roles = userDetails.getAuthorities().stream()
//                    .map(item -> item.getAuthority())
//                    .collect(Collectors.toList());
//
//            JwtResponse jwtResponse = new JwtResponse();
//            jwtResponse.setToken(jwt);
//            jwtResponse.setType("Bearer");
//            jwtResponse.setId(userDetails.getId());
//            jwtResponse.setUsername(userDetails.getUsername());
//            jwtResponse.setEmail(userDetails.getEmail());
//            jwtResponse.setRoles(roles);
//            jwtResponse.setFirstLogin(user.getFirstLogin());
//            jwtResponse.setIsAcctActive(userDetails.getAcctActive());
//            jwtResponse.setPhoneNo(user.getPhoneNo());
//            jwtResponse.setFirstName(user.getFirstName());
//            jwtResponse.setLastName(user.getLastName());
//
//            response.setMessage("successfully signed in");
//            response.setStatusCode(HttpStatus.CREATED.value());
//            response.setEntity(jwtResponse);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            response.setMessage("An error occurred during user authentication.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            EntityResponse res = new EntityResponse<>();
            System.out.println("------------------>>Logging out<---------------------");
            // Invalidate user's session
            request.getSession().invalidate();

            // Clear authentication cookies
            Cookie jwtTokenCookie = new Cookie("user-id", null);
            jwtTokenCookie.setMaxAge(0);
            jwtTokenCookie.setSecure(true);
            jwtTokenCookie.setHttpOnly(true);
            jwtTokenCookie.setPath("/user/");
            response.addCookie(jwtTokenCookie);

            Cookie accessTokenCookie = new Cookie("accessToken", null);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setHttpOnly(true);
            response.addCookie(accessTokenCookie);

            Cookie userNameCookie = new Cookie("username", null);
            userNameCookie.setMaxAge(0);
            response.addCookie(userNameCookie);
            //auditTrailsController.addAudit(RequestUsernameContext.getRequestUsername() + " Logout Success");
            Auditing auditing = new Auditing();
            auditing.setLogoutTime(new Date());
            auditing.setStatus("Inactive");
            auditRepository.save(auditing);
            // You can also clear other cookies if needed

            res.setMessage("logged out successfully");
            res.setStatusCode(HttpStatus.OK.value());
            res.setEntity(null);
            return ResponseEntity.ok("Logged out successfully");
        }catch (Exception e){
            log.error("Exception {}",e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error has occurred during logout");
    }

    @GetMapping(path = "/all/per/department")
    public List<UsersRepository.EmployeeAccount> allUsers(@RequestParam Long department_id) {
        return userRepository.findByUserPerDepartment(department_id);
    }


    @GetMapping(path = "/users/{username}")
    public Users getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    @PutMapping(path = "/users/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody Users user) {

        Set<Role> strRoles = new HashSet<>();
        Set<Role> roles = user.getRoles();
        roles.forEach(role -> {
            Role userRole = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            strRoles.add(userRole);
        });
        user.setRoles(strRoles);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User Information has been successfully updated"));
    }


//    @PostMapping(path = "/verifyOTP")
//    public ResponseEntity<?> validateOTP(@RequestBody OTPCode otpCode) {
//        OTP otp = otpRepository.findByOtp(otpCode.getOtp());
//        if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode.otp)) {
//            return ResponseEntity.badRequest().body(new MessageResponse("OTP is not valid!"));
//        } else {
//            return ResponseEntity.ok(new MessageResponse("Welcome, OTP valid!"));
//        }
//    }

//    @PostMapping(path = "/verifyOTP")
//    public ResponseEntity<?> validateOTP(@RequestBody OTPCode otpCode) {
//        OTP otp = otpRepository.validOTP(otpCode.getOtp());
//        if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode.otp)) {
//            return ResponseEntity.badRequest().body(new MessageResponse("OTP is not valid!"));
//        } else {
//            return ResponseEntity.ok(new MessageResponse("Welcome, OTP valid!"));
//
//        }
//    }


    @GetMapping(path = "/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok().body(roleRepository.findAll());
    }

//    @PostMapping(path = "/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
//        if (!userRepository.existsByEmail(passwordResetRequest.getEmailAddress())) {
//            return ResponseEntity.badRequest().body(new MessageResponse("No such user exists"));
//        } else {
//            Users user = userRepository.findByEmail(passwordResetRequest.getEmailAddress()).orElse(null);
//            if (passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword())) {
//                user.setPassword(encoder.encode(passwordResetRequest.getPassword()));
//                user.setFirstLogin('N');
//                userRepository.save(user);
//                // Include the new password in the response message
//                String newPassword = passwordResetRequest.getPassword();
//                log.info("------------------------------------------------ new password:   "+newPassword);
//                return ResponseEntity.ok().body(new MessageResponse("Password updated successfully. New password: " + newPassword));
//            } else {
//                return ResponseEntity.badRequest().body(new MessageResponse("Password mismatch. Try Again"));
//            }
//        }
//    }


    @PostMapping(path = "/reset")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest) throws MessagingException {
        if (!(userRepository.existsByEmail(passwordResetRequest.getEmailAddress()))) {
            return ResponseEntity.badRequest().body(new MessageResponse("User with given email address does not exist."));
        } else {
            PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
            String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
            String pathToReset = "Your Password has been successfully reset. Use the following password to login: " + generatedPassword;
            String subject = "Password Reset Notification";
            Users user = userRepository.findByEmail(passwordResetRequest.getEmailAddress()).orElse(null);
            assert user != null;
            user.setPassword(encoder.encode(generatedPassword));
            user.setFirstLogin('Y');
            userRepository.save(user);
            mailService.sendEmail(user.getEmail(), null, pathToReset, subject, false, "", null);
            return ResponseEntity.ok().body(new MessageResponse("Password Reset Successful."));
        }
    }


    @PostMapping(path = "/resett-password")
    public ResponseEntity<?> Passwordreset(@RequestBody PasswordResetRequest passwordResetRequest) {
        if (!userRepository.existsByEmail(passwordResetRequest.getEmailAddress())) {
            return ResponseEntity.badRequest().body(new MessageResponse("No such user exists"));
        } else {
            Users user = userRepository.findByEmail(passwordResetRequest.getEmailAddress()).orElse(null);
            if (passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword())) {
                user.setPassword(encoder.encode(passwordResetRequest.getPassword()));
                user.setFirstLogin('N');
                userRepository.save(user);
                // Include the new password in the response message
                String newPassword = passwordResetRequest.getPassword();
                log.info("------------------------------------------------ new password:   " + newPassword);
                return ResponseEntity.ok().body(new MessageResponse("Password updated successfully. New password: " + newPassword));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Password mismatch. Try Again"));
            }
        }
    }


    @GetMapping("get/users")
    public List<UserDTO> getAllUsersNoPass() {
        List<Users> users = usersService.getAllUsers();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getSn());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNo(user.getPhoneNo());
        dto.setEmail(user.getEmail());
        return dto;
    }

    @DeleteMapping(path = "/permanent/delete/{sn}")
    public ResponseEntity<EntityResponse> deleteUserPermanently(@PathVariable Long sn) {
        try {
            EntityResponse response = new EntityResponse<>();
            Optional<Users> usersOptional = userRepository.findById(sn);
            if (usersOptional.isPresent()) {
                userRepository.deleteById(sn);
                response.setMessage("User deleted successfully.");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
            } else {
                response.setMessage("User With Sn " + sn + " Does NOT Exist!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
            }
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.info("Error {} " + e);
            return null;
        }

    }

    @PostMapping(path = "/forgot/password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPassword forgotpassword) throws MessagingException, IOException {
        if (!userRepository.existsByEmail(forgotpassword.getEmailAddress())) {
            EntityResponse response = new EntityResponse();
            response.setMessage("No account associated with the email provided " + forgotpassword.getEmailAddress());
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            response.setEntity("");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            PasswordGeneratorUtil passwordGeneratorUtil = new PasswordGeneratorUtil();
            String generatedPassword = passwordGeneratorUtil.generatePassayPassword();
            Optional<Users> user = userRepository.findByEmail(forgotpassword.getEmailAddress());
            if (user.isPresent()) {
                Users existingUser = user.get();
                existingUser.setPassword(encoder.encode(generatedPassword));
                existingUser.setSystemGenPassword(true);
                existingUser.setModifiedBy(user.get().getUsername());
                //newuser.setModifiedBy(newuser.getUsername());
                existingUser.setModifiedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                userRepository.save(existingUser);
                String subject = "PASSWORD RESET:";
                //String userIdentity = "User";
                String mailMessage = //"<p>Dear <strong>" + userIdentity  +"</strong>,</p>\n" +
                        "  <p>Your password has been successfully updated. Find the following credentials that you will use to access the application:</p>\n" +
                                "  <ul>\n" +
                                "    <li>Username: <strong>" + user.get().getUsername() + "</strong></li>\n" +
                                "    <li>Password: <strong>" + generatedPassword + "</strong></li>\n" +
                                "  </ul>\n" +
                                "  <p>Please login to change your password.</p>";
                mailsService.SendEmail(existingUser.getEmail(), null, mailMessage, subject, false, null, null);
                EntityResponse response = new EntityResponse();
                log.info("-------------------------" + generatedPassword);
                response.setMessage("Password Reset Successfully! Password has been sent to the requested email");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                EntityResponse response = new EntityResponse();
                response.setMessage("User with email address not found!");
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity("");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }

    @PostMapping("/signup")
    public EntityResponse<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {
        EntityResponse response = new EntityResponse<>();


        try {
            if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
                response.setMessage("Passwords do not match. Please ensure that passwords match.");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;

            }
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                response.setMessage("User name is already taken.");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                response.setMessage("Email is already taken.");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return response;
            } else {
                Users user = new Users();
                user.setUsername(signUpRequest.getUsername());
                user.setEmail(signUpRequest.getEmail());
                user.setPassword(encoder.encode(signUpRequest.getPassword()));
                Set<String> strRoles = signUpRequest.getRole();
                Set<Role> roles = new HashSet<>();

                if (strRoles == null || strRoles.isEmpty()) {
                    Role userRole = roleRepository.findByName(ERole.APPLICANT.toString())
                            .orElseThrow(() -> new RuntimeException("Role not found."));
                    roles.add(userRole);
                } else {
                    for (String role : signUpRequest.getRole()) {
                        try {
                            Role userRole = roleRepository.findByName(role)
                                    .orElseThrow(() -> new RuntimeException("Role not found."));
                            roles.add(userRole);
                        } catch (RuntimeException e) {
                            response.setMessage("Role not found: " + role);
                            return response;
                        }
                    }
                }

                user.setRoles(roles);
                user.setCreatedOn(new Date());
                user.setDeletedFlag('N');
                user.setAcctActive(false);
                user.setAcctLocked(false);
                user.setStatus("PENDING");
                user.setVerifiedFlag('Y');
                user.setFirstLogin('Y');
                user.setVerifiedOn(new Date());
                user.setEmail(signUpRequest.getEmail());
                user.setFirstName(signUpRequest.getFirstName());
                user.setLastName(signUpRequest.getLastName());
                user.setPhoneNo(signUpRequest.getPhoneNo());

                Users users = userRepository.save(user);

//                String mailMessage = "Dear " + user.getFirstName() + " your account has been successfully created using username " + user.getUsername()
                String mailMessage = "<p>Dear <strong>" + user.getFirstName() + "</strong>,</p>\n" +
                        " Your account has been successfully created using username " + user.getUsername()
                        + " and password " + signUpRequest.getPassword();
                String subject = "Account creation";

                mailService.sendEmail(users.getEmail(), null, mailMessage, subject, false, null, null);
                response.setMessage("User " + user.getUsername() + " registered successfully!");
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(users);

                return response;

            }
        } catch (Exception e) {
            log.error("Exception {}", e);
            response.setMessage("An error occurred during user registration ");
            return null;
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
        EntityResponse response = new EntityResponse<>();
        // Check if the user exists based on the username
        Optional<Users> existingUserOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (existingUserOptional.isEmpty()) {
            response.setMessage("User not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Users existingUser = existingUserOptional.get();
        try {
            if (existingUser.getStatus().equals("PENDING")) {
                response.setMessage("Your status is pending approval, contact the Admin!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
            }
            System.out.println("Authentication----------------------------------------------------------------------");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            Cookie jwtTokenCookie = new Cookie("user-id", "c2FtLnNtaXRoQGV4YW1wbGUuY29t");
            jwtTokenCookie.setMaxAge(86400);
            jwtTokenCookie.setSecure(true);
            jwtTokenCookie.setHttpOnly(true);
            jwtTokenCookie.setPath("/user/");
            res.addCookie(jwtTokenCookie);
            Cookie accessTokenCookie = new Cookie("accessToken", jwt);
            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setHttpOnly(true);
            res.addCookie(accessTokenCookie);
            Cookie userNameCookie = new Cookie("username", loginRequest.getUsername());
            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
            res.addCookie(userNameCookie);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(jwt);
            jwtResponse.setType("Bearer");
            jwtResponse.setId(userDetails.getId());
            jwtResponse.setUsername(userDetails.getUsername());
            jwtResponse.setEmail(userDetails.getEmail());
            jwtResponse.setRoles(roles);
            jwtResponse.setFirstLogin(existingUser.getFirstLogin());
            jwtResponse.setIsAcctActive(userDetails.getAcctActive());
            jwtResponse.setPhoneNo(existingUser.getPhoneNo());
            jwtResponse.setFirstName(existingUser.getFirstName());
            jwtResponse.setLastName(existingUser.getLastName());
            jwtResponse.setStatus("APPROVED");
            jwtResponse.setIsAcctActive(true);

            response.setMessage("Hi, " + loginRequest.getUsername() + ", welcome to ERP solution");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setEntity(jwtResponse);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception {}", e);
            response.setMessage("An error occurred during user authentication.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/approveOrReject")
    public ResponseEntity<?> approveOrReject(@RequestBody List<UserStatusDTO> userStatusDTOList) {
        EntityResponse response = new EntityResponse<>();
        try {
            if (userStatusDTOList.isEmpty()) {
                response.setMessage("you must provide at least one user for approval or rejection");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                List<Users> updatedUsers = new ArrayList<>();
                for (UserStatusDTO userStatusDTO : userStatusDTOList) {
                    Optional<Users> OptionalUser = userRepository.findById(userStatusDTO.getSn());
                    if (OptionalUser.isPresent()) {
                        Users user = OptionalUser.get();
                        user.setRemarks(userStatusDTO.getRemarks());
                        String Status = userStatusDTO.getStatus().toUpperCase();
                        switch (Status) {
                            case "APPROVED":
                                user.setStatus("APPROVED");
                                user.setAdminApprovedBy(UserRequestContext.getCurrentUser());
                                user.setApprovedTime(new Date());
                                user.setApprovedFlag('Y');
                                break;
                            case "REJECTED":
                                user.setStatus("REJECTED");
                                user.setApprovedFlag('N');
                                user.setAdminApprovedBy(null);
                                user.setApprovedTime(null);
                                break;
                            default:
                                response.setMessage("Invalid Status provided: " + Status);
                                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);


                        }
                        updatedUsers.add(userRepository.save(user));

                    } else {
                        response.setMessage("No user found with such an Id: " + userStatusDTO.getSn());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(response);

                    }

                }
                response.setMessage("User Status updated Successfully");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(updatedUsers);

            }

        } catch (Exception e) {
            log.error(e.toString());
            response.setMessage("An unexpected error occurred while updating status");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

        }
        return ResponseEntity.ok(response);

    }

    @GetMapping("/user/status")
    public EntityResponse<List<Users>> getUsersByStatus(@RequestParam String status) {
        return usersService.getUsersByStatus(status);
    }
//    @DeleteMapping("/{userId}")
//    public ResponseEntity<String> softDeleteUser(@PathVariable Long sn) {
//        Users user = userRepository.findById(sn).orElse(null);
//        if (user != null) {
//            user.setDeleted(true); // Set the flag indicating soft delete
//            userRepository.save(user);
//            return ResponseEntity.status(HttpStatus.OK).body("User soft deleted successfully");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }

    //    }
    @DeleteMapping("/delete")
    public EntityResponse delete(@RequestParam Long sn) {
        return usersService.delete(sn);
    }


    @GetMapping("/FetchAll")
    public EntityResponse fetchAll() {
        return usersService.fetchAll();
    }

    @GetMapping("findById")
    public EntityResponse findBySn(@RequestParam Long sn) {
        return usersService.findBysn(sn);

}
//    @PostMapping("/userSignIn")
//    public ResponseEntity<?> userSignIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
//        EntityResponse response = new EntityResponse<>();
//        // Check if the user exists based on the username
//        Optional<Users> existingUserOptional = userRepository.findByUsername(loginRequest.getUsername());
//        if (existingUserOptional.isEmpty()) {
//            response.setMessage("Uaername/password do not match");
//            response.setStatusCode(HttpStatus.NOT_FOUND.value());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//        Users existingUser = existingUserOptional.get();
//        try {
//            if (existingUser.getStatus().equals("PENDING")) {
//                response.setMessage("Your status is pending approval, contact the Admin!");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
//            }
//            System.out.println("Authentication----------------------------------------------------------------------");
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String jwt = jwtUtils.generateJwtToken(authentication);
//            Cookie jwtTokenCookie = new Cookie("user-id", "c2FtLnNtaXRoQGV4YW1wbGUuY29t");
//            jwtTokenCookie.setMaxAge(86400);
//            jwtTokenCookie.setSecure(true);
//            jwtTokenCookie.setHttpOnly(true);
//            jwtTokenCookie.setPath("/user/");
//            res.addCookie(jwtTokenCookie);
//            Cookie accessTokenCookie = new Cookie("accessToken", jwt);
//            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
//            accessTokenCookie.setSecure(true);
//            accessTokenCookie.setHttpOnly(true);
//            res.addCookie(accessTokenCookie);
//            Cookie userNameCookie = new Cookie("username", loginRequest.getUsername());
//            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
//            res.addCookie(userNameCookie);
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            List<String> roles = userDetails.getAuthorities().stream()
//                    .map(item -> item.getAuthority())
//                    .collect(Collectors.toList());
//
//            String otp = otpService.generateOTP(userDetails.getUsername());
//            sendOtpEmail(userDetails.getEmail(), otp);
//
//            JwtResponse jwtResponse = new JwtResponse();
//            jwtResponse.setToken(jwt);
//            jwtResponse.setType("Bearer");
//            jwtResponse.setId(userDetails.getId());
//            jwtResponse.setUsername(userDetails.getUsername());
//            jwtResponse.setEmail(userDetails.getEmail());
//            jwtResponse.setRoles(roles);
//            jwtResponse.setFirstLogin(existingUser.getFirstLogin());
//            jwtResponse.setIsAcctActive(userDetails.getAcctActive());
//            jwtResponse.setPhoneNo(existingUser.getPhoneNo());
//            jwtResponse.setFirstName(existingUser.getFirstName());
//            jwtResponse.setLastName(existingUser.getLastName());
//            jwtResponse.setStatus("APPROVED");
//            jwtResponse.setIsAcctActive(true);
//
//            response.setMessage("Hi, " + loginRequest.getUsername() + ", we have Emailed you an Otp Code,check it on your email to proceed with the log in  ");
//            response.setStatusCode(HttpStatus.OK.value());
//            response.setEntity(jwtResponse);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            log.error("Exception {}",e);
//            response.setMessage("An error occurred during user authentication.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }



//    @PostMapping("/userSignIn")
//    public ResponseEntity<?> userSignIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
//        EntityResponse response = new EntityResponse<>();
//        // Check if the user exists based on the username
//        Optional<Users> existingUserOptional = userRepository.findByUsername(loginRequest.getUsername());
//        if (existingUserOptional.isEmpty()) {
//            response.setMessage("User not found.");
//            response.setStatusCode(HttpStatus.NOT_FOUND.value());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//        Users existingUser = existingUserOptional.get();
//        try {
//            if (existingUser.getStatus().equals("PENDING")) {
//                response.setMessage("Your status is pending approval, contact the Admin!");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
//            }
//            System.out.println("Authentication----------------------------------------------------------------------");
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            String jwt = jwtUtils.generateJwtToken(authentication);
//            Cookie jwtTokenCookie = new Cookie("user-id", "c2FtLnNtaXRoQGV4YW1wbGUuY29t");
//            jwtTokenCookie.setMaxAge(86400);
//            jwtTokenCookie.setSecure(true);
//            jwtTokenCookie.setHttpOnly(true);
//            jwtTokenCookie.setPath("/user/");
//            res.addCookie(jwtTokenCookie);
//            Cookie accessTokenCookie = new Cookie("accessToken", jwt);
//            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
//            accessTokenCookie.setSecure(true);
//            accessTokenCookie.setHttpOnly(true);
//            res.addCookie(accessTokenCookie);
//            Cookie userNameCookie = new Cookie("username", loginRequest.getUsername());
//            accessTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
//            res.addCookie(userNameCookie);
//            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            List<String> roles = userDetails.getAuthorities().stream()
//                    .map(item -> item.getAuthority())
//                    .collect(Collectors.toList());
//
//            String otp = otpService.generateOTP(userDetails.getUsername());
//            sendOtpEmail(userDetails.getEmail(), otp);
//
//            JwtResponse jwtResponse = new JwtResponse();
//            jwtResponse.setToken(jwt);
//            jwtResponse.setType("Bearer");
//            jwtResponse.setId(userDetails.getId());
//            jwtResponse.setUsername(userDetails.getUsername());
//            jwtResponse.setEmail(userDetails.getEmail());
//            jwtResponse.setRoles(roles);
//            jwtResponse.setFirstLogin(existingUser.getFirstLogin());
//            jwtResponse.setIsAcctActive(userDetails.getAcctActive());
//            jwtResponse.setPhoneNo(existingUser.getPhoneNo());
//            jwtResponse.setFirstName(existingUser.getFirstName());
//            jwtResponse.setLastName(existingUser.getLastName());
//            jwtResponse.setStatus("APPROVED");
//            jwtResponse.setIsAcctActive(true);
//
//            response.setMessage("Hi, " + loginRequest.getUsername() + ", we have Emailed you an Otp Code,check it on your email to proceed with the log in  ");
//            response.setStatusCode(HttpStatus.OK.value());
//            response.setEntity(jwtResponse);
//
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (Exception e) {
//            log.error("Exception {}", e);
//            response.setMessage("An error occurred during user authentication.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }


    private void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        String subject = "Verification OTP";
        String message = "Your OTP code is: " + otp + ". The OTP expires in 2 minutes.";
        mailService.sendEmail(toEmail,               // recipient email
                null,                  // cc (if any)
                message,               // email message
                subject,               // email subject
                false,                 // hasAttachment
                null,                  // attachmentName
                null);
    }

    @PostMapping(path = "/verifyOTP")
    public ResponseEntity<?> validateOTP(@RequestBody OTPCode otpCode) {
        OTP otp = otpRepository.validOTP(otpCode.getOtp());
        if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode.otp)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new EntityResponse<>("OTP is not valid!", null, HttpStatus.BAD_REQUEST.value()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new EntityResponse<>("Welcome, OTP valid!", null, HttpStatus.OK.value()));
        }
    }


    @GetMapping(path = "/Users{findUsers}")
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok().body(userRepository.findAll());
    }


    @PostMapping("/Admin/Register")

    public EntityResponse<?> usersRegister(@Valid @RequestBody AdminSignUpRequest adminSignUpRequest) throws MessagingException {
        EntityResponse response = new EntityResponse<>();


        try {
            Optional<Users> checkUser = userRepository.findByEmail(adminSignUpRequest.getEmail());
            if (checkUser.isPresent()) {
                response.setMessage("User already exist");
            } else {
                Users user = new Users();
                user.setUsername(adminSignUpRequest.getUsername());
                user.setEmail(adminSignUpRequest.getEmail());
                String generatedPassword = RandomStringUtils.randomAlphanumeric(10);
                user.setPassword(encoder.encode(generatedPassword)); // Encode the generated password

                Set<String> strRoles = adminSignUpRequest.getRole();
                Set<Role> roles = new HashSet<>();

                if (strRoles == null || strRoles.isEmpty()) {
                    Role userRole = roleRepository.findByName(ERole.APPLICANT.toString())
                            .orElseThrow(() -> new RuntimeException("Role not found."));
                    roles.add(userRole);
                } else {
                    for (String role : adminSignUpRequest.getRole()) {
                        try {
                            Role userRole = roleRepository.findByName(role)
                                    .orElseThrow(() -> new RuntimeException("Role not found."));
                            roles.add(userRole);
                        } catch (RuntimeException e) {
                            response.setMessage("Role not found: " + role);
                            return response;
                        }
                    }
                }

                user.setRoles(roles);

                user.setCreatedOn(new Date());
                user.setDeletedFlag('N');
                user.setAcctActive(false);
                user.setAcctLocked(false);
                user.setStatus("PENDING");
                user.setVerifiedFlag('Y');
                user.setFirstLogin('Y');
                user.setVerifiedOn(new Date());
                user.setEmail(adminSignUpRequest.getEmail());
                user.setFirstName(adminSignUpRequest.getFirstName());
                user.setLastName(adminSignUpRequest.getLastName());
                user.setPhoneNo(adminSignUpRequest.getPhoneNo());

                Users users = userRepository.save(user);

//                String mailMessage = "Dear " + user.getFirstName() + " your account has been successfully created using username " + user.getUsername()
                String mailMessage = "<p>Dear <strong>" + user.getFirstName() + "</strong>,</p>\n" +
                        " Your account has been successfully created using username " + user.getUsername()
                        + " and password " + generatedPassword;
                String subject = "Account creation";

                mailService.sendEmail(users.getEmail(), null, mailMessage, subject, false, null, null);
                response.setMessage("User " + user.getUsername() + " registered successfully!");
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(users);

                return response;

            }
        } catch (Exception e) {
            log.error("Exception during user registration: ", e);
            response.setMessage("An error occurred during user registration: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }
        return response;
    }

    //   @PostMapping("/signup")
//    public EntityResponse<?> applicantSignUp(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {
//        EntityResponse response = new EntityResponse<>();
//
//        try {
//            if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
//                response.setMessage("Passwords do not match. Please ensure that passwords match.");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            }
//            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//                response.setMessage("User name is already taken.");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//                response.setMessage("Email is already taken.");
//                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                return response;
//            } else {
//                Users user = new Users();
//                user.setUsername(signUpRequest.getUsername());
//                user.setEmail(signUpRequest.getEmail());
//                user.setPassword(encoder.encode(signUpRequest.getPassword()));
//
//                // Automatically assign the ROLE_APPLICANT role
//                Role userRole = roleRepository.findByName(ERole.ROLE_APPLICANT.toString())
//                        .orElseThrow(() -> new RuntimeException("Role not found."));
//                Set<Role> roles = new HashSet<>();
//                roles.add(userRole);
//                user.setRoles(roles);
//
//                user.setCreatedOn(new Date());
//                user.setDeletedFlag('N');
//                user.setAcctActive(false);
//                user.setAcctLocked(false);
//                user.setStatus("PENDING");
//                user.setVerifiedFlag('Y');
//                user.setFirstLogin('Y');
//                user.setVerifiedOn(new Date());
//                user.setEmail(signUpRequest.getEmail());
//                user.setFirstName(signUpRequest.getFirstName());
//                user.setLastName(signUpRequest.getLastName());
//                user.setPhoneNo(signUpRequest.getPhoneNo());
//
//                Users savedUser = userRepository.save(user);
//
//                response.setMessage("User " + user.getUsername() + " registered successfully!");
//                response.setStatusCode(HttpStatus.CREATED.value());
//                response.setEntity(savedUser);
//
//                String mailMessage = "<p>Dear <strong>" + user.getFirstName() + "</strong>,</p>\n" +
//                        " Your account has been successfully created using username " + user.getUsername()
//                        + " and password " + signUpRequest.getPassword();
//                String subject = "Account creation";
//
//                mailService.sendEmail(savedUser.getEmail(), null, mailMessage, subject, false, null, null);
//
//
//                return response;
//            }
//        } catch (Exception e) {
//            log.error("Exception {}", e);
//            response.setMessage("An error occurred during user registration.");
//            return null;
//        }
//
//    }
    @PostMapping(path = "AutoPasswordResest{AUTO}")
    public ResponseEntity<?> autoPasswordReset(@RequestBody NewPassworsResetRequest passwordResetRequest) {
        if (!userRepository.existsByEmail(passwordResetRequest.getEmailAddress())) {
            return ResponseEntity.badRequest().body(new MessageResponse("No such user exists"));
        }

        Users user = userRepository.findByEmail(passwordResetRequest.getEmailAddress()).orElseThrow(() ->
                new RuntimeException("User not found for email: " + passwordResetRequest.getEmailAddress()));


        String generatedPassword = RandomStringUtils.randomAlphanumeric(10);


        user.setPassword(encoder.encode(generatedPassword));
        user.setFirstLogin('Y');

        userRepository.save(user);
        log.info("------------------------------------ New Password: " + generatedPassword);

        // Prepare email content with the auto-generated password
        String mailMessage = "<p>Dear <strong>" + user.getFirstName() + "</strong>,</p>\n" +
                " Your password has been reset. Your new password is:<br>" + generatedPassword + "<br>" +
                " Please note that it is recommended to change this password to a strong one of your own choosing as soon as possible.";
        String subject = "Password Reset for Account";

        try {
            mailService.sendEmail(user.getEmail(), null, mailMessage, subject, false, null, null);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(new MessageResponse("Password reset successfully. A new password has been sent to your email."));
    }

    @PostMapping(path = "reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        if (!userRepository.existsByEmail(passwordResetRequest.getEmailAddress())) {
            return ResponseEntity.badRequest().body(new MessageResponse("No such user exists"));
        } else {
            Users user = userRepository.findByEmail(passwordResetRequest.getEmailAddress()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("No such user exists"));
            }


            if (encoder.matches(passwordResetRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body(new MessageResponse("New password cannot be the same as the current password"));
            }

            if (passwordResetRequest.getPassword().equals(passwordResetRequest.getConfirmPassword())) {
                user.setPassword(encoder.encode(passwordResetRequest.getPassword()));
                user.setFirstLogin('N'); // Reset first login flag if needed
                userRepository.save(user);

                return ResponseEntity.ok().body(new MessageResponse("Password updated successfully"));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Password mismatch. Try Again"));
            }
        }

       // return ResponseEntity.ok().body(new MessageResponse("Password reset successfully. A new password has been sent to your email."));
    }
    @PostMapping("/userSignIn")
    public ResponseEntity<?> userSignIn(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
        EntityResponse response = new EntityResponse<>();


        Optional<Users> existingUserOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (existingUserOptional.isEmpty()) {
            response.setMessage("Username/password do not match");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Users existingUser = existingUserOptional.get();


        if (existingUser.isAcctLocked()) {
            response.setMessage("Your account is locked. Please contact support for assistance.");
            response.setStatusCode(HttpStatus.LOCKED.value());
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }

        try {
            if (existingUser.getStatus().equals("PENDING")) {
                response.setMessage("Your status is pending approval, contact the Admin!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
            }


            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // If authentication is successful, reset failed login attempts and generate JWT token
            existingUser.setFailedLoginAttempts(0);
            userRepository.save(existingUser);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Set JWT token in cookie
            Cookie jwtTokenCookie = new Cookie("accessToken", jwt);
            jwtTokenCookie.setMaxAge(1 * 24 * 60 * 60); // expires in 1 day
            jwtTokenCookie.setSecure(true);
            jwtTokenCookie.setHttpOnly(true);
            res.addCookie(jwtTokenCookie);

            // Construct response
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(jwt);
            jwtResponse.setType("Bearer");
            jwtResponse.setId(userDetails.getId());
            jwtResponse.setUsername(userDetails.getUsername());
            jwtResponse.setEmail(userDetails.getEmail());
            jwtResponse.setRoles(roles);
            jwtResponse.setFirstLogin(existingUser.getFirstLogin());
            jwtResponse.setIsAcctActive(userDetails.getAcctActive());
            jwtResponse.setPhoneNo(existingUser.getPhoneNo());
            jwtResponse.setFirstName(existingUser.getFirstName());
            jwtResponse.setLastName(existingUser.getLastName());
            jwtResponse.setStatus("APPROVED");
            jwtResponse.setIsAcctActive(true);


            String otp = otpService.generateOTP(userDetails.getUsername());
            sendOtpEmail(userDetails.getEmail(), otp);

            response.setMessage("Hi, " + loginRequest.getUsername() + ", we have emailed you an OTP code. Please check your email to proceed with the login.");
            response.setStatusCode(HttpStatus.OK.value());
            response.setEntity(jwtResponse);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {

            existingUser.setFailedLoginAttempts(existingUser.getFailedLoginAttempts() + 1);
            if (existingUser.getFailedLoginAttempts() >= 3) {
                // Lock the account if failed login attempts exceed threshold
                existingUser.setAcctLocked(true);
                userRepository.save(existingUser);
                response.setMessage("Your account has been locked due to multiple failed login attempts. Please contact support for assistance.");
                response.setStatusCode(HttpStatus.LOCKED.value());
                return ResponseEntity.status(HttpStatus.LOCKED).body(response);
            }
            userRepository.save(existingUser);

            // Handle failed authentication response
            response.setMessage("Username/password do not match");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            // Handle other exceptions
            log.error("Exception during user authentication: {}", e.getMessage());
            response.setMessage("An error occurred during user authentication.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/unlockAccount")
    public ResponseEntity<String> unlockAccount(@RequestParam String username) {

        Optional<Users> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (user.isAcctLocked()) {
                user.setAcctLocked(false);
                userRepository.save(user);
                return ResponseEntity.ok("Account unlocked successfully.");
            } else {
                return ResponseEntity.badRequest().body("Account is not locked.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/userSignin")
    public ResponseEntity<?> userSigin(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) throws MessagingException {
        Optional<Users> existingUserOptional = userRepository.findByUsername(loginRequest.getUsername());
        EntityResponse response = new EntityResponse<>();
        try {

            if (existingUserOptional.isPresent()) {
                Character deletestatus = userRepository.getByDeletedFlag(loginRequest.getUsername());
                boolean locked = userRepository.getAccountLockedStatus(loginRequest.getUsername());
               // boolean active = userRepository.getAccountInactiveStatus(loginRequest.getUsername());
                String status = userRepository.getByStatus(loginRequest.getUsername());

                if (deletestatus==('Y')) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Account Deleted ! Contact Admin!"));
                } else if (locked) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Account Locked! Contact Admin!"));
//                } else if (!active) {
//                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Account Inactive! Contact Admin!"));
                } else if (status.equalsIgnoreCase("PENDING")) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Error: Account status is pending please contact admin!!"));

                }else {

                    System.out.println("---------------------->>>>>Authentication<<<<<-------------------------------");
                    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    System.out.println("-----------------------------------------");
                    Optional<Users> userOptional = userRepository.findByUsername(loginRequest.getUsername());
                    System.out.println(loginRequest.getUsername());
                    System.out.println(userOptional.get().getSn());
                    JwtResponse jwtResponse = new JwtResponse();
                    jwtResponse.setId(userOptional.get().getSn());
                    if (useOTP.equalsIgnoreCase("false")) {
                        log.info("------------------------Use OTP False");
                        jwtResponse = getAccessToken(loginRequest.getUsername());
                        String jwt = jwtUtils.generateJwtToken(authentication);
                        Duration dt = Duration.ofDays(1);
                        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwt)
                                // .domain(clientside)
                                .httpOnly(true)
                                .maxAge(dt)
                                .path("/")
                                .secure(true)
                                .sameSite("None")  // sameSite
                                .build();
                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                        return new ResponseEntity<>(jwtResponse, headers, HttpStatus.OK);
                    } else {
                        log.info("------------------------Use OTP True");
                       // Users user = userRepository.getById(userDetails.getId());
//                        call otp functionality
                        String otp = otpService.generateOTP(userDetails.getUsername());

                        String email = userDetails.getEmail();


                        String subject = "OTP:";
                        String mailMessage = "<p>Dear <strong>" + userDetails.getUsername() + "</strong>,</p>\n" +
                                "  <p>Your OTP code is:</p>\n" +
                                "  <ul>\n" +
                                //"    <li>Username: <strong>" + userDetails.getUsername() + "</strong></li>\n" +
                                "    <li>OTP: <strong>" + otp + "</strong></li>\n" +
                                "  </ul>\n" +
                                "  <p>The OTP expires in 2 minutes.</p>";

                        System.out.println(mailMessage);
                        mailService.sendEmail(email, null, mailMessage, subject, false, null, null);
                        jwtResponse = getAccessToken(loginRequest.getUsername());
                        response.setMessage("Hi, " + loginRequest.getUsername() + ", we have Emailed you an Otp Code,check it on your email to proceed with the log in  ");
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(jwtResponse);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                        //return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
                    }
                }
            }else{
                return ResponseEntity.badRequest().body(new MessageResponse("Error: User Not Found!"));

            }

        }catch (BadCredentialsException e) {
            Users existingUser = existingUserOptional.get();

            existingUser.setFailedLoginAttempts(existingUser.getFailedLoginAttempts() + 1);
            if (existingUser.getFailedLoginAttempts() >= 3) {
                // Lock the account if failed login attempts exceed threshold
                existingUser.setAcctLocked(true);
                userRepository.save(existingUser);
                response.setMessage("Your account has been locked due to multiple failed login attempts. Please contact support for assistance.");
                response.setStatusCode(HttpStatus.LOCKED.value());
                return ResponseEntity.status(HttpStatus.LOCKED).body(response);
            }
            userRepository.save(existingUser);
            // Handle failed authentication response
            response.setMessage("Username/password do not match");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);

        } catch (Exception e) {
            // Handle other exceptions
            log.error("Exception during user authentication: {}", e.getMessage());
            response.setMessage("An error occurred during user authentication.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String username, @RequestParam Integer otpCode,HttpServletRequest request) {
        EntityResponse response = new EntityResponse<>();
        // Check if the OTP is valid for the given username
        OTP otp = otpRepository.validOtpCode(username);
        log.info("OTP CODE: {},", otpCode);
        if (Objects.isNull(otp) || !Objects.equals(otp.getOtp(), otpCode)) {
            // If OTP is invalid, return a bad request response
            return ResponseEntity.badRequest().body(new MessageResponse("Failed.Invalid OTP Code"));
        } else {
            // If OTP is valid, proceed with generating the access token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            JwtResponse response2 = getAccessToken(username);
            System.out.println("jwt utlits response" + response2);

            String jwt = jwtUtils.generateTokenFromUsername(response2.getUsername());

            System.out.println("jwt utlits " + jwt);
            //String jwt = jwtUtils.generateJwtToken(authentication);
            Duration dt = Duration.ofDays(1);
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwt)
                    .httpOnly(true)
                    .maxAge(dt)
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .build();
            HttpHeaders headers = new HttpHeaders();
            response2.setToken(jwt);
            headers.set(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
            //auditTrailsController.addAudit("Sign in success");
            response.setMessage("Authenticated successfully");
            response.setEntity(response2);
            response.setStatusCode(HttpStatus.OK.value());
             Auditing auditing = new Auditing();
             auditing.setOs(getOperatingSystem(request.getHeader("User-Agent")));
             auditing.setStatus("Active");
             auditing.setUsername(username);
             auditing.setActivity("Sign in Success") ;
             auditing.setIpAddress(request.getRemoteAddr());
             auditing.setLoginTime(new Date());
             String userAgentString = request.getHeader("User-Agent");
             UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
             Browser browser = userAgent.getBrowser();
             auditing.setBrowser(browser.getName());
             auditRepository.save(auditing);

            return new ResponseEntity<>(response, headers, HttpStatus.OK);
        }

    }
    private String getOperatingSystem(String userAgentString) {
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        OperatingSystem os = userAgent.getOperatingSystem();
        return os.getName();
    }

    private JwtResponse getAccessToken(String username) {
        JwtResponse response1 = new JwtResponse();
        Optional<Users> userCheck = userRepository.findByUsername(username);

        Users user = userCheck.get();
        List<String> roles = user.getRoles().stream().map(item -> item.getName()).collect(Collectors.toList());
        userRepository.updateLogInToTrue(
                true, modifiedOn, username, username);
        //Add records to audit table
        //auditTrailsController.addAudit(username + " Log In to System");
        //auditTrailsController.addAudit(username);
        List<String> privileges = null;
        Optional<Role> role = roleRepository.findByName(roles.get(0));
//        if (role.isPresent()) {
//            privileges = role.get().getBasicactions().stream().filter(d -> d.isSelected()).map(item -> item.getName()).collect(Collectors.toList());
//        }
        response1.setId(user.getSn());
        response1.setStatus(user.getStatus());

        response1.setFirstName(user.getFirstName());
        response1.setLastName(user.getLastName());
        response1.setIsAcctActive(user.isAcctActive());
        response1.setFirstLogin(user.getFirstLogin());
        response1.setUsername(user.getUsername());
        response1.setEmail(user.getEmail());
        response1.setRoles(roles);
        //response1.setPrivileges(privileges);
        return response1;
    }

    @PostMapping("/signOut")
    public ResponseEntity<?> signOut(HttpServletResponse response) {
        //Update Logged in status to false
        userRepository.updateLogInToFalse(false, modifiedOn, RequestUsernameContext.getRequestUsername(), RequestUsernameContext.getRequestUsername());
        Duration dt = Duration.ofDays(0);
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", null)
                //.domain(clientside)
                .httpOnly(true)
                .maxAge(dt)
                .path("/")
                .secure(true)
                .sameSite("None")  // sameSite
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        //Add Audit
       // auditTrailsController.addAudit(RequestUsernameContext.getRequestUsername() + " Logout Success");
        EntityResponse response1 = new EntityResponse();
        response1.setMessage("Logged Out Successfully!");
        response1.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<>(response1, headers, HttpStatus.OK);
    }
    @PostMapping("/userSigninTesT")
    public ResponseEntity<?> userSiginTest(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse res) throws MessagingException {
        EntityResponse response = new EntityResponse<>();

        // Check if the user exists based on the username
        Optional<Users> existingUserOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (existingUserOptional.isEmpty()) {
            response.setMessage("User not found.");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        Users existingUser = existingUserOptional.get();

        // Check if the account is locked
        if (existingUser.isAcctLocked()) {
            response.setMessage("Your account is locked. Please contact support for assistance.");
            response.setStatusCode(HttpStatus.LOCKED.value());
            return ResponseEntity.status(HttpStatus.LOCKED).body(response);
        }

        try {
            if (existingUser.getStatus().equals("PENDING")) {
                response.setMessage("Your status is pending approval, contact the Admin!");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
            }

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Reset failed login attempts
            existingUser.setFailedLoginAttempts(0);
            userRepository.save(existingUser);

            // Generate JWT token and set in cookie
            String jwt = jwtUtils.generateJwtToken(authentication);
            Duration dt = Duration.ofDays(1);
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", jwt)
                    .httpOnly(true)
                    .maxAge(dt)
                    .path("/")
                    .secure(true)
                    .sameSite("None") // sameSite
                    .build();
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

            // Send OTP email if required
            if (useOTP.equalsIgnoreCase("true")) {
                String otp = otpService.generateOTP(userDetails.getUsername());
                mailService.sendEmail(userDetails.getEmail(), null, "Your OTP code is: " + otp, "OTP", false, null, null);
                response.setMessage("Hi, " + loginRequest.getUsername() + ", we have emailed you an OTP code. Please check your email to proceed with the login.");
            }

            response.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(response, headers, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            existingUser.setFailedLoginAttempts(existingUser.getFailedLoginAttempts() + 1);

            if (existingUser.getFailedLoginAttempts() >= 3) {
                // Lock the account if failed login attempts exceed threshold
                existingUser.setAcctLocked(true);
                userRepository.save(existingUser);
                response.setMessage("Your account has been locked due to multiple failed login attempts. Please contact support for assistance.");
                response.setStatusCode(HttpStatus.LOCKED.value());
                return ResponseEntity.status(HttpStatus.LOCKED).body(response);
            }
            userRepository.save(existingUser);

            // Handle failed authentication response
            response.setMessage("Username/password do not match");
            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            // Handle other exceptions
            log.error("Exception during user authentication: {}", e.getMessage());
            response.setMessage("An error occurred during user authentication.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }






}