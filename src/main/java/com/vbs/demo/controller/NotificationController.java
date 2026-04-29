package com.vbs.demo.controller;


import com.vbs.demo.models.Admindata;
import com.vbs.demo.models.Notification;
import com.vbs.demo.models.Userdata;
import com.vbs.demo.repos.AdminRepo;
import com.vbs.demo.repos.NotiRepo;
import com.vbs.demo.repos.UserRepo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")

public class NotificationController {

    @Autowired
    UserRepo userRepo;
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    NotiRepo notiRepo;

    @GetMapping("/notifications/{id}")
    public ResponseEntity<?> getNotfied(@PathVariable int id){

        Userdata user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not found"));
        String targetR = "customer";

        List<Notification> notify =
                notiRepo.findAllByUserIdAndTarget(id,targetR);

        return ResponseEntity.ok(notify);
    }
    @GetMapping("/admin/notification/{id}")
    public ResponseEntity<?> getNotfiedA(@PathVariable int id){

        Admindata admin = adminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin Not found"));
        String targetR = "admin";

        List<Notification> notify =
                notiRepo.findAllByTarget(targetR);

        return ResponseEntity.ok(notify);
    }

    @GetMapping("/recentActivity/{userId}")
    public ResponseEntity<?> getAllNotfied(@PathVariable int userId){

        Userdata user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not found"));
        String targetR = "customer";

        List<Notification> notify =
                notiRepo.findAllByUserIdAndTarget(userId,targetR);

        return ResponseEntity.ok(notify);
    }
}
