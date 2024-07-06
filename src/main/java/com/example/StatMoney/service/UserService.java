package com.example.StatMoney.service;

import com.example.StatMoney.entity.User;
import com.example.StatMoney.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        // Проверка формата email
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Неверный формат email");
        }

        // Проверка уникальности email
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }

        // Проверка сложности пароля
        if (!isStrongPassword(user.getPassword())) {
            throw new IllegalArgumentException("Пароль должен содержать не менее 8 символов, включая цифры, буквы разного регистра и специальные символы (только латиница)");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    private boolean isValidEmail(String email) {
        // Регулярное выражение для проверки формата email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isStrongPassword(String password) {
        // Регулярное выражение для проверки сложности пароля
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,}$";
        return password.matches(passwordRegex);
    }

    public User findById(Long id){
        return userRepository.findById(id).get();
    }

}
