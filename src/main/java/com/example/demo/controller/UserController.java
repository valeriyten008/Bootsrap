package com.example.demo.controller;


import com.example.demo.service.RoleService;
import jakarta.validation.Valid;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/admin-panel")
    public String adminPanel(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);

        // Добавляем пустой объект user, чтобы форма не падала
        model.addAttribute("user", new User());

        // Если ты в форме даёшь выбор ролей, это тоже важно
        model.addAttribute("allRoles", roleService.findAll());

        return "user/admin-panel";
    }


    @GetMapping("/{id}")
    public String findUserById(@PathVariable("id") Long id, Model model) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/user-profile";
        } else {
            return "redirect:/user/admin-panel";
        }
    }
    @PostMapping
    public String createUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roleId", required = false) List<Long> roleId,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.findAll());
            return "user/admin-panel";
        }

        userService.save(user, roleId);
        return "redirect:/user/admin-panel";
    }
    @PostMapping("/{id}/edit")
    public String update(@ModelAttribute("user") User user, @PathVariable("id") Long id) {
        userService.updateUser(id, user);
        return "redirect:/user/admin-panel";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/user/admin-panel";

    }
    @GetMapping("/profile")
    public String userProfile(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("roles", user.getRoles());
        return "user/user-profile";
    }

}