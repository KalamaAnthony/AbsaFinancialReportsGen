package com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Users;

//import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.ERole;
import com.example.EMTECH_ERP.BACKEND.AuthenticationModule.Roles.Role;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

//    Optional<Users> findByEmpNo(String empNo);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhoneNo(String phone);

    Optional<Users> findByEmail(String email);

    @Query(value = "SELECT users.sn as UserId, employee.id as Employeeid, employee.department_id as Departmentid, employee.first_name as Firstname, employee.middle_name as Middlename, employee.last_name as Lastname FROM employee JOIN users ON employee.id = users.employee_id WHERE employee.department_id=:department_id",nativeQuery = true)
    List<EmployeeAccount> findByUserPerDepartment(Long department_id);

    Optional<Users> findByFirstName(String firstName);

    List<Users> findByStatusAndDeletedFlag(String status, char deletedFlag);

    List<Users> findAllByDeletedFlag(char deletedFlag);

    Optional<Users> findBysn(Long sn);

    boolean existsByRoles_Name(String string);

    @Transactional
    @Query(nativeQuery = true,value = "SELECT locked FROM users WHERE username = :username")
    boolean getAccountLockedStatus(
            @Param(value = "username") String username
    );
//    @Transactional
//    @Query(nativeQuery = true,value = "SELECT active FROM users WHERE username = :username")
//    boolean getAccountInactiveStatus(
//            @Param(value = "username") String username
//    );
    @Transactional
    @Query(nativeQuery = true,value = "SELECT status FROM users WHERE username = :username")
    String getByStatus(@Param(value ="username") String username);

    @Transactional
    @Query(nativeQuery = true,value = "SELECT delete_flag FROM users WHERE username = :username")
    Character getByDeletedFlag(
            @Param(value = "username") String username
    );

    interface EmployeeAccount{
        Long getUserId();
        Long getEmployeeid();
        Long getDepartmentid();
        String getFirstname();
        String getMiddlename();
        String getLastname();
    }
    @Override
    List<Users> findAll();

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update users set loggedin = :loggedin,modified_on= :modified_on,modified_by = :modified_by where username = :username")
    void updateLogInToTrue(
            @Param(value = "loggedin") boolean loggedin,
            @Param(value = "modified_on") String modifiedon,
            @Param(value = "modified_by") String modifiedby,
            @Param(value = "username") String username
    );

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update users set loggedin = :loggedin,modified_on= :modified_on,modified_by = :modified_by where username = :username")
    void updateLogInToFalse(
            @Param(value = "loggedin") boolean loggedin,
            @Param(value = "modified_on") String modifiedon,
            @Param(value = "modified_by") String modifiedby,
            @Param(value = "username") String username
    );
}



