package com.vbs.demo.controller;

import com.vbs.demo.dto.*;
import com.vbs.demo.models.Admindata;
import com.vbs.demo.models.DeletedUsers;
import com.vbs.demo.models.Notification;
import com.vbs.demo.models.Userdata;
import com.vbs.demo.repos.AdminRepo;
import com.vbs.demo.repos.DeleteRepo;
import com.vbs.demo.repos.NotiRepo;
import com.vbs.demo.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin (origins = "*")
public class UserController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    AdminRepo adminRepo;
    @Autowired
    NotiRepo notiRepo;
    @Autowired
    DeleteRepo deleteRepo;

    // sign-up customer
    @PostMapping("/auth/signup/customer")
    public ResponseEntity<?> usersignup(@RequestBody UsignupDto incomingdetails)   /* response entity better then string
    matching in frontend since it doesnt depend upon the case of what expected in frontend it communicates with
    HTTP error/sucess codes */ {
        Userdata newuser = new Userdata();
        String checkemail = incomingdetails.getEmail();
        String checkrole = incomingdetails.getAccountType();
        long numberofemailexist = userRepo.countByEmail(checkemail);

        if (numberofemailexist == 0) {
            newuser.setName(incomingdetails.getName());
            newuser.setUsername(incomingdetails.getUsername());
            newuser.setEmail(incomingdetails.getEmail());
            newuser.setAccountType(incomingdetails.getAccountType());
            newuser.setPassword(incomingdetails.getPassword());
            newuser.setSecQuestion(incomingdetails.getSecQuestion());
            newuser.setSecAnswer(incomingdetails.getSecAnswer());
            newuser.setBalance(0.0);
            if((incomingdetails.getAccountType().equalsIgnoreCase("CURRENT"))){
                newuser.setAccountstatus("PENDING");}
            else{
                newuser.setAccountstatus("ACTIVE");}
            userRepo.save(newuser);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "SignUp Successful"));
        }
        if (numberofemailexist >= 2) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "ALL TYPES OF ACCOUNT EXIST ON THIS EMAIL", "action", "BLOCKED"));
        }
        Userdata existing = userRepo.findAllByEmail(checkemail);

        if ((existing.getAccountType()).equalsIgnoreCase(checkrole)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email exist with the current Account type",
                            "action", "BLOCKED"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("exists", true, "action", "VERIFY_EXISTING_ACCOUNT",
                            "existingAccountType", existing.getAccountType()));
        }
    }

    // customer login
    @PostMapping("/login/user")
    public ResponseEntity<?> userlogin(@RequestBody LoginuDto userlogin) {
        Userdata userdata = userRepo.findByUsername(userlogin.getUsername());
        if(userdata == null)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("exists",false,"message","User Not found"));
        }
        if (!(userdata.getPassword().equals(userlogin.getPassword()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Incorrect Password"));
        }
        if (userdata.getAccountType()==null||!(userlogin.getAccountType()).equalsIgnoreCase(userdata.getAccountType())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Incorrect Account"));
        }
        if((userdata.getAccountstatus()).equalsIgnoreCase("PENDING")&& (userdata.getBalance()>9999)){
            userdata.setAccountstatus("ACTIVE");
            userRepo.save(userdata);
        }
        return ResponseEntity.ok(String.valueOf(userdata.getId()));
    }

    //customer dashboard
    @GetMapping("/get-details/{userId}")
    public ResponseEntity <UserDashboardDto> getdashboard(@PathVariable("userId") int id)
    {
        Userdata user =userRepo.findById(id).orElseThrow(()->new RuntimeException("User not Found"));
        UserDashboardDto show = new UserDashboardDto();
        show.setName(user.getName());
        show.setUsername(user.getUsername());
        show.setEmail(user.getEmail());
        show.setAccountType(user.getAccountType());
        show.setBalance(user.getBalance());
        show.setAccountStatus(user.getAccountstatus());
        show.setDepositCount(user.getDepositCount());
        show.setWithdrawCount(user.getWithdrawCount());
        show.setTransferCount(user.getTransferCount());
        return ResponseEntity.ok(show);

    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updateP(@RequestBody UpdateP_Dto incomingP){
        Userdata userdata = userRepo.findById(incomingP.getId()).orElseThrow(()->new RuntimeException("user not found"));
        if(!(userdata.getPassword()).equals(incomingP.getCurrentPassword()))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Incorrect current password !"));
        }
        if((userdata.getPassword().equals(incomingP.getNewPassword())))
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Cant keep the same password"));
        }
        userdata.setPassword(incomingP.getNewPassword());
        userRepo.save(userdata);
        LocalDateTime timestamp;
        timestamp = LocalDateTime.now();

        Notification NforRcvr = new Notification();
        NforRcvr.setUserId(userdata.getId());
        NforRcvr.setTarget("customer");
        NforRcvr.setDescription("Your password was updated via profile at "+timestamp);
        notiRepo.save(NforRcvr);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Password Updated"));
    }

    // user dashboard get security question
    @GetMapping("/get-secq/{userId}")
    public ResponseEntity<?> seqQ(@PathVariable("userId") int iD){
        Userdata user = userRepo.findById(iD).orElseThrow(()->new RuntimeException(("user not found")));
        return ResponseEntity.ok(Map.of("secQ", user.getSecQuestion()));
    }
    @PostMapping("/request-delete")
    public ResponseEntity<?> requestD(@RequestBody RequestDTO incominginfo){
        Userdata user = userRepo.findByUsername(incominginfo.getUsername());
        LocalDateTime timeS;
        if(user == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","User Not found"));

        }
        if(user.getAccountstatus().equalsIgnoreCase("Delete Requested"))
        {
            return ResponseEntity.status(409)
                    .body(Map.of("exists",true ,"message","Account status already set to Delete requested"));
        }
        if(user.getBalance() != 0 && (!user.getAccountstatus().equalsIgnoreCase("PENDING")))
        {
            return ResponseEntity.status(469)
                    .body(Map.of("message","Balance Should Be Zero"));
        }
        if((incominginfo.getSecurityAnswer()).equalsIgnoreCase(user.getSecAnswer())
                && (incominginfo.getPassword()).equals(user.getPassword()))
        {
            user.setAccountstatus("DELETE REQUESTED");
            userRepo.save(user);
            timeS = LocalDateTime.now();
        }
        else
        {
            return ResponseEntity.status(401)
                    .body(Map.of("message","Password or Security Answer didnt match"));
        }

        Notification NforRcvr = new Notification();
        NforRcvr.setUserId(user.getId());
        NforRcvr.setTarget("customer");
        NforRcvr.setDescription("You requested account deletion at "+timeS);
        notiRepo.save(NforRcvr);

        Notification NforAdmin = new Notification();
        NforAdmin.setUserId(user.getId());
        NforAdmin.setTarget("admin");
        NforAdmin.setDescription(user.getUsername()+" Requested account deletion");
        notiRepo.save(NforAdmin);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","success"));
    }

    // without login procedure forgot password (customer)
    @GetMapping("/forgot-password/question")
    public ResponseEntity<?> ForgotPassword(@RequestParam String username){
        Userdata user = userRepo.findByUsername(username);
        if(user == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","User Not found"));

        }
        if(!(user.getAccountstatus()).equalsIgnoreCase("ACTIVE"))
        {
            return ResponseEntity.status(403)
                    .body(Map.of("message","User account status is Not Active"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("secQuestion",user.getSecQuestion()));
    }
    @PostMapping("forgot-password/verify")
    public ResponseEntity<?> verify(@RequestBody SecQverifyDTO incomingDetails){
        Userdata user = userRepo.findByUsername(incomingDetails.getUsername());
        if(user == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","User Not found"));

        }
        if(!(user.getSecAnswer()).equals(incomingDetails.getAnswer())){
            return ResponseEntity.status(401)
                    .body(Map.of("message","Security Answer didn't match"));
        }
        return ResponseEntity.status(200)
                .body(Map.of("message","answer verified"));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity <?> resetP(@RequestBody OuterResetDTO incomingDetails){
        Userdata user = userRepo.findByUsername(incomingDetails.getUsername());
        if(user == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","User Not found"));

        }
        if((user.getPassword()).equals(incomingDetails.getPassword())){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Password cannot be same as current password"));
        }
        if((incomingDetails.getPassword()).isEmpty()){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Password cannot be empty"));
        }
        user.setPassword(incomingDetails.getPassword());
        userRepo.save(user);
        LocalDateTime timeSS;
        timeSS = LocalDateTime.now();

        Notification NforRcvr = new Notification();
        NforRcvr.setUserId(user.getId());
        NforRcvr.setTarget("customer");
        NforRcvr.setDescription("Your password was reset using Security Question at"+timeSS);
        notiRepo.save(NforRcvr);

        return ResponseEntity.status(200)
                .body(Map.of("message","Password updated Successfully"));
    }

    // admin sign up
    @PostMapping("/auth/signup/admin")
    public ResponseEntity<?> adminsignup(@RequestBody Admindata incomingdetails) {
        Admindata checkemailexist = adminRepo.findByEmail(incomingdetails.getEmail());
        if (checkemailexist != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("exists", true, "message", "Email Already Exists"));
        } else {
            adminRepo.save(incomingdetails);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Sign-Up Successful"));

        }
    }

    // admin login
    @PostMapping("/login/admin")
    public ResponseEntity<?> adminlogin(@RequestBody LoginaDto adminlogin) {
        Admindata admindata = adminRepo.findByUsername(adminlogin.getUsername());
        if(admindata == null)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("exists",false,"message","User Not found"));
        }
        if (!(admindata.getPassword().equals(adminlogin.getPassword()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message","Incorrect Password"));
        }
        return ResponseEntity.ok(String.valueOf(admindata.getId()));
    }

    //admin dash
    @GetMapping("/admin/get-details/{userId}")
    public ResponseEntity <AdmindashboardDTO> getadmin(@PathVariable("userId") int id)
    {
        Admindata admin = adminRepo.findById(id).orElseThrow(()->new RuntimeException("Admin not Found"));
        AdmindashboardDTO showA = new AdmindashboardDTO();
        showA.setUsername(admin.getUsername());
        return ResponseEntity.ok(showA);
    }

    // add user

    @PostMapping("/add/{userId}")
    public ResponseEntity <?> adduser(@RequestBody AdminaddsUserDTO incomingD,@PathVariable int userId){
        Admindata admin = adminRepo.findById(userId).orElseThrow(()->new RuntimeException("Admin not Found"));
        Userdata newuser = new Userdata();
        newuser.setName(incomingD.getName());
        newuser.setEmail(incomingD.getEmail());
        newuser.setUsername(incomingD.getUsername());
        newuser.setPassword(incomingD.getPassword());
        newuser.setSecQuestion(incomingD.getSecQuestion());
        newuser.setSecAnswer(incomingD.getSecAnswer());
        newuser.setBalance(incomingD.getBalance());
        newuser.setAccountType(incomingD.getAccountType());
        if((incomingD.getAccountType().equalsIgnoreCase("CURRENT")) && incomingD.getBalance()>9999){
            newuser.setAccountstatus("ACTIVE");
        }
        else if((incomingD.getAccountType().equalsIgnoreCase("CURRENT"))){
            newuser.setAccountstatus("PENDING");
        }
        else if((incomingD.getAccountType().equalsIgnoreCase("SAVINGS"))) {
            newuser.setAccountstatus("ACTIVE");
        }
        userRepo.save(newuser);
        return ResponseEntity.status(200)
                .body(Map.of("message","Added customer Successfully"));
    }

    //AI
    // search ( improve  refactor later )
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(
                userRepo.findByUsernameContainingIgnoreCase(keyword)
        );
    }

    //all customers ( improve  refactor later )
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "all") String accountType) {


        Sort sort = order.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        List<Userdata> users;


        if(!status.equalsIgnoreCase("all") && !accountType.equalsIgnoreCase("all")) {
            users = userRepo.findByAccountstatusAndAccountType(status.toUpperCase(), accountType.toUpperCase(), sort);
        }
        else if(!status.equalsIgnoreCase("all")) {
            users = userRepo.findByAccountstatus(status.toUpperCase(), sort);
        }
        else if(!accountType.equalsIgnoreCase("all")) {
            users = userRepo.findByAccountType(accountType.toUpperCase(), sort);
        }
        else {
            users = userRepo.findAll(sort);
        }

        return ResponseEntity.ok(users);
    }

    // freeze user ( improve  refactor later )
    @PutMapping("/freeze-user/{customerId}/admin/{adminId}")
    public ResponseEntity<?> freezeUser(@PathVariable int customerId,
                                        @PathVariable int adminId) {

        Userdata user = userRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Admindata admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if(user.getAccountstatus().equalsIgnoreCase("INACTIVE")) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","User already frozen"));
        }

        user.setAccountstatus("INACTIVE");
        userRepo.save(user);

        Notification n = new Notification();
        n.setUserId(user.getId());
        n.setTarget("customer");
        n.setDescription("Your account was frozen by admin");
        notiRepo.save(n);

        return ResponseEntity.ok(Map.of("message","User frozen successfully"));
    }

    // unfreeze ( improve  refactor later )

    @PutMapping("/unfreeze-user/{customerId}/admin/{adminId}")
    public ResponseEntity<?> unfreezeUser(@PathVariable int customerId,
                                          @PathVariable int adminId) {

        Userdata user = userRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Admindata admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if(!user.getAccountstatus().equalsIgnoreCase("INACTIVE")) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","User is not frozen"));
        }

        user.setAccountstatus("ACTIVE");
        userRepo.save(user);

        Notification n = new Notification();
        n.setUserId(user.getId());
        n.setTarget("customer");
        n.setDescription("Your account was reactivated by admin");
        notiRepo.save(n);

        return ResponseEntity.ok(Map.of("message","User unfrozen successfully"));
    }

    // delete user ( improve  refactor later )

    @DeleteMapping("/delete-user/{customerId}/admin/{adminId}")
    public ResponseEntity<?> deleteUser(@PathVariable int customerId,
                                        @PathVariable int adminId) {

        Userdata user = userRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Admindata admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if(user.getBalance() != 0) {
            return ResponseEntity.status(409)
                    .body(Map.of("message","Cannot delete user with non-zero balance"));
        }
        DeletedUsers userDeleted = new DeletedUsers();

        userDeleted.setOgID(user.getId());
        userDeleted.setName(user.getName());
        userDeleted.setUsername(user.getUsername());
        userDeleted.setEmail(user.getEmail());
        userDeleted.setAccountType(user.getAccountType());
        userDeleted.setPassword(user.getPassword());
        userDeleted.setBalance(user.getBalance());
        userDeleted.setAccountstatus("CLOSED");
        userDeleted.setSecQuestion(user.getSecQuestion());
        userDeleted.setSecAnswer(user.getSecAnswer());
        userDeleted.setDepositCount(user.getDepositCount());
        userDeleted.setWithdrawCount(user.getWithdrawCount());
        userDeleted.setTransferCount(user.getTransferCount());

        userDeleted.setDeletedByAdminId(admin.getId());
        userDeleted.setDeletedAt(LocalDateTime.now());

        deleteRepo.save(userDeleted);


        userRepo.delete(user);

        return ResponseEntity.ok(Map.of("message","User deleted successfully"));
    }
    //AI

    // without login procedure forgot password (admin)
    @GetMapping("/admin/forgot-password/question")
    public ResponseEntity<?> ForgotP(@RequestParam String username){
        Admindata admin = adminRepo.findByUsername(username);
        if(admin == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","Admin account Not found"));

        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("secQuestion",admin.getSecQuestion()));
    }
    @PostMapping("/admin/forgot-password/verify")
    public ResponseEntity<?> verifyA(@RequestBody SecQverifyDTO incomingDetails){
        Admindata admin = adminRepo.findByUsername(incomingDetails.getUsername());
        if(admin == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","Admin account Not found"));

        }
        if(!(admin.getSecAnswer()).equals(incomingDetails.getAnswer())){
            return ResponseEntity.status(401)
                    .body(Map.of("message","Security Answer didn't match"));
        }
        return ResponseEntity.status(200)
                .body(Map.of("message","answer verified"));
    }

    @PostMapping("/admin/forgot-password/reset")
    public ResponseEntity <?> resetPass(@RequestBody OuterResetDTO incomingDetails){
        Admindata admin = adminRepo.findByUsername(incomingDetails.getUsername());
        if(admin == null)
        {
            return ResponseEntity.status(404)
                    .body(Map.of("exists",false,"message","User Not found"));

        }
        if((admin.getPassword()).equals(incomingDetails.getPassword())){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Password cannot be same as current password"));
        }
        if((incomingDetails.getPassword()).isEmpty()){
            return ResponseEntity.status(403)
                    .body(Map.of("message","Password cannot be empty"));
        }
        admin.setPassword(incomingDetails.getPassword());
        adminRepo.save(admin);

        LocalDateTime timestamp;
        timestamp = LocalDateTime.now();

        Notification NforRcvr = new Notification();
        NforRcvr.setUserId(admin.getId());
        NforRcvr.setTarget("admin");
        NforRcvr.setDescription("Your password was updated using Security Question ");/*at "+timestamp*/
        notiRepo.save(NforRcvr);

        return ResponseEntity.status(200)
                .body(Map.of("message","Password updated Successfully"));
    }



}