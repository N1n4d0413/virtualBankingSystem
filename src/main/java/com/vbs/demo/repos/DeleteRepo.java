package com.vbs.demo.repos;

import com.vbs.demo.models.DeletedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteRepo extends JpaRepository <DeletedUsers,Integer> {

}
