package org.abby.springsecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@Schema(description = "用戶 Entity")
public class User implements UserDetails {

    @Schema(description = "用戶帳號")
    private String userAccount;

    @Schema(description = "用戶暱稱")
    private String userNickname;

    private String password;

    @Schema(description = "創建用戶的用戶名稱")
    private String lastModifiedBy;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Username = userAccount / 任何你想設定的主鍵,這邊改變數名稱以覆蓋全部!
     * @return String
     */
    @Override
    public String getUsername() {
        return this.userAccount;
    }

    /**
     * 使用者帳戶是否沒有過期,true -> 沒有
     *
     * @return boolean
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 使用者帳戶是否被鎖定,true -> 沒有
     *
     * @return boolean
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 這個方法表示使用者帳戶的憑證（例如密碼）是否沒有過期。true -> 沒有
     *
     * @return boolean
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 使用者帳戶是否啟用。true -> 啟用
     *
     * @return boolean
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
