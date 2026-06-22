package devops_training_portal.controller;

import devops_training_portal.entity.User;
import devops_training_portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        User user = userService.loginUser(email, password);

        if(user != null){

            model.addAttribute("user", user);

            return "dashboard";
        }

        model.addAttribute("error",
                "Invalid Email or Password");

        return "login";
    }
}