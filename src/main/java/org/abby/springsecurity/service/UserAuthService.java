package org.abby.springsecurity.service;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abby.springsecurity.bsservice.JwtService;
import org.abby.springsecurity.entity.User;
import org.abby.springsecurity.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    /**
     * <h3>login</h3>
     *
     * @param userAccount 帳號
     * @param password    密碼
     * @return jwtToken
     */
    public String authenticate(@NotEmpty String userAccount, @NotEmpty String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userAccount,
                        password
                )
        );
        var user = userRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new UsernameNotFoundException("UserAccount not found :" + userAccount + "."));
        return jwtService.generateToken(user); //jwtToken
    }

    /**
     * 獲取當前環境中,有效的UserDetails<br>
     * 一定有值,所以不用加Optional<br>
     *
     * @return UserDetails
     */
    public UserDetails getCurrentUserDetails() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails userDetails) {
                    return userDetails;
                } else {
                    throw new UsernameNotFoundException("Current principal is not of type UserDetails.");
                }
            } else {
                throw new UsernameNotFoundException("No authenticated user found.");
            }
        } catch (UsernameNotFoundException e) {
            log.error(e.toString());
            throw e;
        } catch (Exception e) {
            log.error(e.toString());
            throw new UsernameNotFoundException("Failed to retrieve current user details.");
        }
    }

    /**
     * 獲取當前環境中,有效的UserDetails<br>
     * 再用帳號查詢對應User<br>
     * 一定有值,所以不用加Optional<br>
     *
     * @return UserDetails
     */
    public User getCurrentUser() {
        return userRepository.findByUserAccount(this.getCurrentUserDetails().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve current user details."));
    }

}
