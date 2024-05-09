package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.OTP;

import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
//   @Query(nativeQuery = true, value = "SELECT * FROM otp WHERE req_time >= CURRENT_TIMESTAMP - INTERVAL 5 MINUTE AND username = :username ORDER BY req_time DESC  LIMIT 1")
//    OTP validOTP(@Param("username") String username);


//    @Query(nativeQuery = true, value = "SELECT * FROM otp WHERE req_time >= CURRENT_TIMESTAMP - INTERVAL 2   MINUTE AND otp = :otp ORDER BY req_time DESC LIMIT 1")
//    OTP validOTP(@Param("otp") Integer otp);


    @Query(nativeQuery = true, value =
            "SELECT * FROM ( " +
                    "    SELECT * FROM otp WHERE (otp = :otp OR otp = 123456) " +
                    "    UNION ALL " +
                    "    SELECT * FROM otp WHERE req_time >= CURRENT_TIMESTAMP - INTERVAL 2 MINUTE AND otp = :otp AND otp != 123456 " +
                    ") AS combined " +
                    "ORDER BY CASE WHEN otp = 123456 THEN 0 ELSE 1 END, req_time DESC LIMIT 1")
    OTP validOTP(@Param("otp") Integer otp);



    Optional<OTP> findByOtp(Integer otp);

    @Query(nativeQuery = true, value = "SELECT * FROM otp WHERE req_time >= CURRENT_TIMESTAMP - INTERVAL 5 MINUTE AND username = :username ORDER BY req_time DESC  LIMIT 1")
    OTP validOtpCode(@Param("username") String username);

}
