package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Auditing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Auditing,Long> {

    List<Auditing> findByUsernameAndLoginTime(String username, String loginTime);

    List<Auditing> findByLoginTime(Date loginTime);
}
