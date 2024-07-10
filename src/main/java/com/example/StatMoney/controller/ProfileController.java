package com.example.StatMoney.controller;

import com.example.StatMoney.entity.User;
import com.example.StatMoney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping
    public String getProfile(Model model, Principal principal,
                             @RequestParam(value = "successMessage", required = false) String successMessage,
                             @RequestParam(value = "errorMessage", required = false) String errorMessage) {
        String email = principal.getName();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            model.addAttribute("errorMessage", "Пользователь не найден");
            return "profile";
        }
        model.addAttribute("user", user);
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);
        return "profile";
    }

    @PostMapping("/updateEmail")
    public String updateEmail(@RequestParam String newEmail, Principal principal, RedirectAttributes redirectAttributes) {
        String currentEmail = principal.getName();
        try {
            User user = userService.findByEmail(newEmail).orElse(null);
            if(user != null){
                throw new IllegalArgumentException("Пользователь с таким email уже существует");
            }
            if (!userService.isValidEmail(newEmail)) {
                throw new IllegalArgumentException("Заполните поле");
            }

            userService.updateEmail(currentEmail, newEmail);

            UserDetails userDetails = userDetailsService.loadUserByUsername(newEmail);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addAttribute("successMessage", "Email успешно обновлён");
        } catch (IllegalArgumentException | IOException e) {
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Новый пароль и подтверждение не совпадают");
            }
            if (!userService.isStrongPassword(newPassword)) {
                throw new IllegalArgumentException("Пароль должен содержать не менее 8 символов, включая цифры, буквы разного регистра и специальные символы (только латиница)");
            }

            userService.updatePassword(email, oldPassword, newPassword);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addAttribute("successMessage", "Пароль успешно обновлён");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile avatar, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        try {
            userService.updateAvatar(email, avatar);
            redirectAttributes.addAttribute("successMessage", "Аватар успешно обновлён");
        } catch (IOException e) {
            redirectAttributes.addAttribute("errorMessage", "Ошибка при загрузке аватара: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}

