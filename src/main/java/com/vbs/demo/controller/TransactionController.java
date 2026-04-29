package com.vbs.demo.controller;


import com.vbs.demo.dto.TransactionDTO;
import com.vbs.demo.dto.TransferrDTO;
import com.vbs.demo.models.Notification;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.Userdata;
import com.vbs.demo.repos.AdminRepo;
import com.vbs.demo.repos.NotiRepo;
import com.vbs.demo.repos.TransactionRepo;
import com.vbs.demo.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin (origins = "*")
public class TransactionController {
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    NotiRepo notiRepo;

    // deposit -edge_cases,transactions_counts.
    @PostMapping("/deposit")
    public ResponseEntity <?> deposit(@RequestBody TransactionDTO incomingT){
        Userdata user = userRepo.findById(incomingT.getId()).orElseThrow(()->new RuntimeException("User Not Found"));
        if(incomingT.getAmount()==0){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Deposit cannot be Zero"));
        }
        if(user.getDepositCount()>=5&&(user.getAccountType().equals("SAVINGS"))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Deposit limit Exceeded Please try in next month"));
        }
        double newB = user.getBalance() + incomingT.getAmount();
        user.setBalance(newB);
        int c = user.getDepositCount();
        user.setDepositCount(c+1);
        userRepo.save(user);

        // transaction Table
        Transaction T = new Transaction();
        T.setUserId(user.getId());
        T.setAmount(incomingT.getAmount());
        T.setCurrBalance(newB);
        T.setAccountType(user.getAccountType());
        T.setDescription("Deposited Rs"+incomingT.getAmount()+" Successfully");
        transactionRepo.save(T);


        return ResponseEntity.status(200)
                .body(Map.of("message","Deposited "+incomingT.getAmount()+" Successfully"));

    }

    // withdraw -edge_cases,transaction_counts.
    @PostMapping("/withdraw")
    public ResponseEntity <?> withdraw(@RequestBody TransactionDTO incomingT){
        Userdata user = userRepo.findById(incomingT.getId()).orElseThrow(()->new RuntimeException("User Not Found"));
        if(user.getWithdrawCount()>=5&&(user.getAccountType().equals("SAVINGS"))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Withdraw limit Exceeded Pls try in next month"));
        }
        if(incomingT.getAmount()==0){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Withdrawal cannot be Zero"));
        }
        double newB = user.getBalance() - incomingT.getAmount();
        if(newB < 0 && (user.getAccountType()).equalsIgnoreCase("SAVINGS")){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Current balance is low you can withdraw "+user.getBalance()));
        } else if ((newB < 10000 && (user.getAccountType()).equalsIgnoreCase("CURRENT"))) {
            double curr = user.getBalance() - 10000;
            return ResponseEntity.status(403)
                    .body(Map.of("message","Minimum balance 10000 required. You can withdraw "+curr));
        }
        user.setBalance(newB);
        int c = user.getWithdrawCount();
        user.setWithdrawCount(c+1);
        userRepo.save(user);

        // transaction Table
        Transaction T = new Transaction();
        T.setUserId(user.getId());
        T.setAmount(incomingT.getAmount());
        T.setCurrBalance(newB);
        T.setAccountType(user.getAccountType());
        T.setDescription("Withdrawn Rs."+incomingT.getAmount()+" Successfully");
        transactionRepo.save(T);

        return ResponseEntity.status(200)
                .body(Map.of("message","Withdrawn "+incomingT.getAmount()+" Successfully"));
    }

    // transfer -edge_cases,transaction_counts.
    @PostMapping("/transfer")
    public ResponseEntity <?> transfer(@RequestBody TransferrDTO incomingD){
        Userdata sender = userRepo.findById(incomingD.getId()).orElseThrow(()->new RuntimeException("Sender not Found"));
        Userdata recvr = userRepo.findByUsername(incomingD.getUsername());
        if(recvr == null){
            return ResponseEntity.status(404)
                    .body(Map.of("message","Recipient not found"));
        }
        if(incomingD.getAmount() <= 0){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Amount must be greater than 0"));
        }
        if(sender.getTransferCount()>=5&&(sender.getAccountType().equals("SAVINGS"))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Transfer limit Exceeded Pls try in next month"));
        }
        if((recvr.getUsername().equals(sender.getUsername()))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Self Transfer not allowed"));
        }
        if((!(recvr.getAccountstatus().equals("ACTIVE"))) && (!(recvr.getAccountstatus().equals("PENDING")))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","The recipient's account status is not ACTIVE"));
        }
        double ifdeducted = sender.getBalance() - incomingD.getAmount();
        if(ifdeducted<0 && (sender.getAccountType().equalsIgnoreCase("SAVINGS"))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Transfer Failed Not Enough Balance"));
        } else if (ifdeducted<10000 && (sender.getAccountType().equalsIgnoreCase("CURRENT"))){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Transfer Failed Not Enough Balance *Minimum Balance 10000"));
        }
        sender.setBalance(ifdeducted);
        int c = sender.getTransferCount();
        sender.setTransferCount(c+1);
        userRepo.save(sender);
        double got_it = recvr.getBalance() + incomingD.getAmount();
        recvr.setBalance(got_it);
        userRepo.save(recvr);

        // transaction Table
        Transaction T1 = new Transaction();
        T1.setUserId(sender.getId());
        T1.setAmount(incomingD.getAmount());
        T1.setCurrBalance(ifdeducted);
        T1.setAccountType(sender.getAccountType());
        T1.setDescription("Transferred Rs."+incomingD.getAmount()+ " to "+recvr.getUsername()+" Successfully");
        transactionRepo.save(T1);

        Transaction T2 = new Transaction();
        T2.setUserId(recvr.getId());
        T2.setAmount(incomingD.getAmount());
        T2.setCurrBalance(recvr.getBalance());
        T2.setAccountType(recvr.getAccountType());
        T2.setDescription("Received Rs."+incomingD.getAmount()+ " from "+sender.getUsername()+" Successfully");
        transactionRepo.save(T2);

        Notification NforRcvr = new Notification();
        NforRcvr.setUserId(recvr.getId());
        NforRcvr.setTarget("customer");
        NforRcvr.setDescription("💰 "+sender.getUsername()+" sent you Rs."+incomingD.getAmount());
        notiRepo.save(NforRcvr);


        return ResponseEntity.status(200)
                .body(Map.of("message","Transferred "+incomingD.getAmount()+" to "+incomingD.getUsername()));

    }

    // passbook
    @GetMapping("/passbook/{id}")
    public List<Transaction> getpassbook(@PathVariable int id) {
        return transactionRepo.findAllByUserId(id);
    }

}
