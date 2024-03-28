package org.abby.springsecurity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "註冊Req")
public class RegisterReq {

    @Schema(description = "用戶帳號")
    @NotBlank
    private String userAccount;

    @NotBlank
    private String password;

    @Schema(description = "用戶暱稱")
    @NotBlank
    private String userNickname;

}
