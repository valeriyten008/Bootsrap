package com.example.demo.service;

import jakarta.transaction.Transactional;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    @Override
    public void save(User user, List<Long> roleIds) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Получаем список ролей по их id
        List<Role> roles = roleIds.stream()
                .map(roleService::findById)
                .collect(Collectors.toList());

        // Устанавливаем роли пользователю
        user.setRoles(roles);

        // Сохраняем пользователя в базу
        userRepository.save(user);
    }



    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUser(Long id, User updatedUser) {
        Optional<User> optionalUser = findById(id);

        if (optionalUser.isPresent()) {
            User userToBeUpdated = optionalUser.get();

            userToBeUpdated.setFirstName(updatedUser.getFirstName());
            userToBeUpdated.setLastName(updatedUser.getLastName());
            userToBeUpdated.setAge(updatedUser.getAge());
            userToBeUpdated.setEmail(updatedUser.getEmail());
            userToBeUpdated.setPassword(updatedUser.getPassword());
            userToBeUpdated.setRoles(updatedUser.getRoles());

            userRepository.save(userToBeUpdated);
        }
    }


    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}