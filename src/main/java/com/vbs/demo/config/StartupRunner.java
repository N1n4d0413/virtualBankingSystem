package com.vbs.demo.config;

// resets all transaction (new month simulation on backend restart)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.vbs.demo.repos.UserRepo;

@Component
public class StartupRunner implements ApplicationRunner {

    @Autowired
    private UserRepo userRepo;

    @Override
    public void run(ApplicationArguments args) {
        userRepo.resetAllTransactionCounts();
        System.out.println("📅 Demo Month Reset Done 🚀");
    }
}
