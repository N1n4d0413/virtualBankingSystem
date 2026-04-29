package com.vbs.demo.repos;

import com.vbs.demo.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotiRepo extends JpaRepository <Notification,Integer> {


    List<Notification> findAllByUserIdAndTarget(int userId, String targetR);

    List<Notification> findAllByTarget(String targetR);
}
