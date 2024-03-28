package org.abby.springsecurity.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.abby.springsecurity.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {
    private List<User> userList = new ArrayList<>();

    /**
     * org.springframework.security.authentication.BadCredentialsException: Bad credentials
     * wrong password : Enable encryption, but the database stores the original password,
     * so it needs to be encrypted before storing it in the database.
     */
    public UserRepository() {
        var passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("123456");
        userList.add(User.builder()
                .userAccount("admin")
                .password(password)
                .userNickname("admin")
                .lastModifiedBy("god")
                .build());
        userList.add(User.builder()
                .userAccount("abby")
                .password(password)
                .userNickname("abby")
                .lastModifiedBy("admin")
                .build());
        userList.add(User.builder()
                .userAccount("john")
                .password(password)
                .userNickname("john")
                .lastModifiedBy("admin")
                .build());
    }

    public List<User> findAll() {
        return userList;
    }

    public Optional<User> findByUserAccount(String userAccount) {
        return userList.stream()
                .filter(u -> u.getUserAccount().equals(userAccount))
                .findFirst();
    }

    public boolean addUser(@NotNull User user) {
        return userList.add(user);
    }

    public boolean removeUser(@NotBlank String userAccount) {
        var user = userList.stream()
                .filter(u -> u.getUserAccount().equals(userAccount))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not Found : " + userAccount));
        return userList.remove(user);
    }
}
