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
@Schema(description = "編輯用戶訊息Req")
public class UserEditReq {

    @NotBlank
    private String password;

    @Schema(description = "用戶暱稱")
    @NotBlank
    private String userNickname;

}
