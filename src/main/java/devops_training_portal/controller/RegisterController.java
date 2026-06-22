package devops_training_portal.controller;

import devops_training_portal.entity.User;
import devops_training_portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model){

        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user){

        userService.registerUser(user);

        return "redirect:/success";
    }

    @GetMapping("/success")
    public String successPage(){

        return "success";
    }
}