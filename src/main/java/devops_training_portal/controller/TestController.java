package devops_training_portal.controller;

import devops_training_portal.entity.User;
import devops_training_portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerTestUser() {

        User user = new User();

        user.setFullname("Pawan");
        user.setEmail("pawan@test.com");
        user.setPassword("password123");
        user.setMobile("9876543210");
        user.setExperience("4 Years");
        user.setCompany("TCS");
        user.setCity("Nagpur");

        userService.registerUser(user);

        return "User Saved Successfully";
    }
}