package com.vbs.demo.repos;

import com.vbs.demo.models.Userdata;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository <Userdata,Integer> {



    long countByEmail(String checkemail);

    Userdata findAllByEmail(String checkemail);

    Userdata findByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Userdata u SET u.depositCount = 0, u.withdrawCount = 0, u.transferCount = 0")
    void resetAllTransactionCounts();


    List<Userdata> findByAccountstatusAndAccountType(String upperCase, String upperCase1, Sort sort);

    List<Userdata> findByAccountstatus(String upperCase, Sort sort);

    List<Userdata> findByAccountType(String upperCase, Sort sort);

    Object findByUsernameContainingIgnoreCase(String keyword);
}
