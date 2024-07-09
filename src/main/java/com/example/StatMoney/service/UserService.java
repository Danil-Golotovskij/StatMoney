package com.example.StatMoney.service;

import com.example.StatMoney.entity.User;
import com.example.StatMoney.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class UserService {

    private static final String AVATAR_FOLDER = "avatars/";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Неверный формат email");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }

        if (!isStrongPassword(user.getPassword())) {
            throw new IllegalArgumentException("Пароль должен содержать не менее 8 символов, включая цифры, буквы разного регистра и специальные символы (только латиница)");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public boolean isStrongPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";
        return password.matches(passwordRegex);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateEmail(String currentEmail, String newEmail) throws IOException {
        User user = userRepository.findByEmail(currentEmail).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Неверный формат email");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Неверный текущий пароль");
        }
        if (!isStrongPassword(newPassword)) {
            throw new IllegalArgumentException("Пароль должен содержать не менее 8 символов, включая цифры, буквы разного регистра и специальные символы (только латиница)");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void updateAvatar(String email, MultipartFile avatar) throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Удаление старой аватарки
        deleteAvatar(user.getAvatarUrl());

        String fileName = StringUtils.cleanPath(avatar.getOriginalFilename());
        String filePath = AVATAR_FOLDER + email + "_" + fileName;
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());  // Создает папку если она не существует
        Files.copy(avatar.getInputStream(), path);
        user.setAvatarUrl("/" + filePath);
        userRepository.save(user);
    }


    public void deleteAvatar(String avatarUrl) throws IOException {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Path path = Paths.get(avatarUrl.replaceFirst("/", ""));
            Files.deleteIfExists(path);
        }
    }


}
