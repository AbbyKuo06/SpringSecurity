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
@Schema(description = "Login Req")
public class LoginReq {

    @Schema(description = "用戶帳號", example = "admin")
    @NotBlank
    private String userAccount;

    @Schema(example = "123456")
    @NotBlank
    private String password;

}
