package com.vbs.demo.repos;

import com.vbs.demo.models.Admindata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admindata,Integer> {
    Admindata findByEmail(String email);

    Admindata findByUsername(String username);
}
