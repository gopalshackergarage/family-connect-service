package com.gakshintala.mylabspace.familyconnectdb.api.users;

import com.gakshintala.mylabspace.familyconnectdb.api.db.MemberRepo;
import com.gakshintala.mylabspace.familyconnectdb.api.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberRepo userRepo;

    @PostMapping("/create")
    public void createUser(@RequestBody Member user) {
        userRepo.createUser(user);
    }

    @GetMapping()
    public @ResponseBody Member getUser(@RequestParam("emailId") String emailId) {
        return userRepo.getUser(emailId);
    }
}
