package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Auditing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<Auditing,Long> {
    @Transactional
    @Query(nativeQuery = true,value = "SELECT * FROM audittrails WHERE username = :username AND starttime LIKE %:starttime%")
    List<Auditing> todayTrails(@Param("username") String username, @Param("starttime") String starttime);

    //Select all trails for one user
    @Transactional
    @Query(nativeQuery = true,value = "SELECT * FROM audittrails WHERE username = :username")
    List<Auditing> allTrails(@Param("username") String username);

    @Transactional
    @Query(nativeQuery = true,value = "select * from audittrails where starttime between :fromDate and :toDate ORDER BY ID DESC;")
    List<Auditing> allTrailsByDate(String fromDate, String toDate);
}

