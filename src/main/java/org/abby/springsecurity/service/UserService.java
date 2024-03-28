package org.abby.springsecurity.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abby.springsecurity.dto.RegisterReq;
import org.abby.springsecurity.dto.UserEditReq;
import org.abby.springsecurity.entity.User;
import org.abby.springsecurity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * <h3>註冊帳號</h3>
     *
     * @return jwtToken
     */
    public boolean register(@Valid RegisterReq req, @NotBlank String currentUserAccount) {
        //check uk
        userRepository.findByUserAccount(req.getUserAccount())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("userAccount already exists : " + req.getUserAccount());
                });
        //save
        var user = User.builder()
                .userAccount(req.getUserAccount())
                .password(passwordEncoder.encode(req.getPassword()))
                .userNickname(req.getUserNickname())
                .lastModifiedBy(currentUserAccount)
                .build();
        return userRepository.addUser(user);
    }

    /**
     * <h3>取得所有帳號</h3>
     *
     * @return jwtToken
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /**
     * <h3>編輯帳號</h3>
     * 按照註冊邏輯
     */
    public boolean editUser(@NotBlank String userAccount, @Valid UserEditReq req, @NotBlank String currentUserAccount) {
        //1.User
        //only user owner can edit change value
        if (!userAccount.equals(currentUserAccount)) {
            throw new IllegalArgumentException("CurrentUser not user owner ");
        }
        //get old data
        var oldUser = userRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new IllegalArgumentException("User not Found : " + userAccount));

        //因為沒有連到DB所以移除後再新增
        var userList = userRepository.findAll();
        userList.remove(oldUser);

        log.debug("New User List");
        userList.forEach(u -> log.debug(u.getUserAccount()));

        var newUser = User.builder()
                .userAccount(userAccount)
                .password(passwordEncoder.encode(req.getPassword()))
                .userNickname(req.getUserNickname())
                .lastModifiedBy(currentUserAccount)
                .build();
        return userRepository.addUser(newUser);

    }


    public boolean removeUser(@NotBlank String userAccount, @NotBlank String currentUserAccount) {
        //1.User
        //only user owner can edit change value
        if (!userAccount.equals(currentUserAccount)) {
            throw new IllegalArgumentException("CurrentUser not user owner ");
        }
        return userRepository.removeUser(userAccount);
    }
}
