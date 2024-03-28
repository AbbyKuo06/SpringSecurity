package org.abby.springsecurity.bsservice;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * JWT Service
 */
@Service
public class JwtService {


    /**
     * secrtKey: minimum level required for JWT is 256 bits.<p>
     */
    @Value("${token.key}")
    private String secrtKey;

    @Value("${token.expirationMinute}")
    private int expirationMinute;

    /**
     * 1、解析token字符串中的加密信息【加密算法&加密密鑰】, 提取所有聲明的方法
     *
     * @param token token
     * @return Claims 聲明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 2、獲取簽名密鑰的方法
     *
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secrtKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 3、解析token字符串中的權限信息
     *
     * @param token token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        val claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 4、從token中解析出username
     *
     * @param token token
     * @return username
     */
    @NonNull
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 5、判断token是否過期
     */
    public boolean isTokenVaild(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 6、驗證token是否過期
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 6.1、從授權信息中獲取token過期时间
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    /**
     * 產生公共token
     */
    public String generateToken(
            UserDetails userDetails
    ) {
        return generateToken(new HashMap<>(), userDetails);
    }


    /**
     * 產生token,用userDetails.getUsername()
     */
    public String generateToken(
            Map<String, Object> exrtaClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(exrtaClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) //設定token的發行時間
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * expirationMinute)) //設定token的有效時間
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)//設定加密方式
                .compact();
    }

}
