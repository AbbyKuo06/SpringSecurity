package org.abby.springsecurity.restful;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abby.springsecurity.dto.LoginReq;
import org.abby.springsecurity.dto.RegisterReq;
import org.abby.springsecurity.dto.UserEditReq;
import org.abby.springsecurity.responsebody.CommonPageResp;
import org.abby.springsecurity.responsebody.CommonResp;
import org.abby.springsecurity.service.UserAuthService;
import org.abby.springsecurity.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@Tag(name = "User", description = "使用者管理")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {


    private final UserAuthService userAuthService;
    private final UserService userService;

    @Operation(summary = "帳號註冊", description = "帳號註冊")
    @PostMapping(value = "/register")
    public ResponseEntity<CommonResp<HashMap<String, Object>>> register(
            @Valid @RequestBody RegisterReq req
    ) {
        log.debug("Current userAccount: {}", userAuthService.getCurrentUser().getUserAccount());
        userService.register(req, userAuthService.getCurrentUser().getUserAccount());
        return ResponseEntity.ok(CommonResp.resp(HttpStatus.OK.value(), "insert user ok", null));

    }

    @Operation(summary = "login", description = "login")
    @PostMapping(value = "/login")
    public ResponseEntity<CommonResp<HashMap<String, Object>>> getToken(
            @Valid @RequestBody LoginReq req
    ) {
        String jwtToken = userAuthService.authenticate(req.getUserAccount(), req.getPassword());
        var map = new HashMap<String, Object>();
        map.put("token", jwtToken);
        return ResponseEntity.ok(CommonResp.resp(HttpStatus.OK.value(), null, map));
    }

    @Operation(summary = "User get all", description = "User get all")
    @PostMapping(value = "/get/all")
    public ResponseEntity<CommonPageResp<Map<String, Object>>> userGetAll(
    ) {
        var userList = userService.getAllUser();

        List<Map<String, Object>> mapList = new ArrayList<>();
        userList.forEach(user -> {
            var map = new HashMap<String, Object>();
            map.put("userAccount", user.getUserAccount());
            map.put("userNickname", user.getUserNickname());
            mapList.add(map);
        });
        return ResponseEntity.ok(CommonPageResp.resp(HttpStatus.OK.value(), "get all user ok", mapList));
    }

    @Operation(summary = "User edit", description = "User edit")
    @PostMapping(value = "/edit/{userAccount}")
    public ResponseEntity<CommonResp<HashMap<String, Object>>> userEdit(
            @Schema(name = "userAccount", description = "用户帳號")
            @PathVariable(name = "userAccount")
            String userAccount,
            @Valid @RequestBody UserEditReq req
    ) {
        log.debug("Current userAccount: {}", userAuthService.getCurrentUser().getUserAccount());
        userService.editUser(userAccount, req, userAuthService.getCurrentUser().getUserAccount());
        return ResponseEntity.ok(CommonResp.resp(HttpStatus.OK.value(), "edit user ok", null));
    }

    @Operation(summary = "User delete", description = "Delete a user")
    @DeleteMapping(value = "/delete/{userAccount}")
    public ResponseEntity<CommonResp<HashMap<String, Object>>> userDelete(
            @Schema(name = "userAccount", description = "用户帳號")
            @PathVariable(name = "userAccount")
            String userAccount
    ) {
        log.debug("Current userAccount: {}", userAuthService.getCurrentUser().getUserAccount());
        userService.removeUser(userAccount, userAuthService.getCurrentUser().getUserAccount());
        return ResponseEntity.ok(CommonResp.resp(HttpStatus.OK.value(), "delete user ok", null));
    }

}
