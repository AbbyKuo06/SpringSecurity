package org.abby.springsecurity.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.abby.springsecurity.bsservice.JwtService;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.abby.springsecurity.config.security.SecurityConfig.API_POST_WHITELIST;
import static org.abby.springsecurity.config.security.SecurityConfig.SWAGGER_WHITELIST;
/**
 * @ClassName: ControllerLogFilter
 * OncePerRequestFilter 是Spring提供的，加強確保後端接收到一個請求，該 Filter 只會執行*一次*。<br>
 * 該Filter的功能有<br>
 * <p>
 * 1.JwtAuthentication 驗證
 * <p>
 * 2.解決Controller的參數有@RequestBody，spring會透過Filter解析request的io流後關閉，<br>
 * 造成java.io.IOException: Stream closed 讀不到資料的狀況<br>
 * 因此為了保留請求主體，將請求與回應分別包裝成ContentCachingRequestWrapper 與 ContentCachingResponseWrapper<br>
 * 再如同往常傳入FilterChain<br>
 * 此Wrapper 的特色是會在內部另外備份一個 ByteArrayOutputStream<br>
 * 呼叫 Wrapper 的 getContentAsByteArray 方法，便能不限次數地獲得主體內容<br>
 * <p>
 * 3.紀錄Controller的請求時間
 * <p>
 * 實作完 Filter 的程式後，需要向 Spring 註冊，才會建立它的Bean，建立在FilterConfig<br>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationAndOncePerRequestFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_HEADER_TYPE = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * 解析器實例: 因為是線程安全的，可以定義成final
     */
    private final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    /**
     *  application 啟動時載入不需要token的白名單，並且快取
     */
    private final List<PathPattern> CACHED_PATTERNS = Stream.concat(
                    Arrays.stream(API_POST_WHITELIST), Arrays.stream(SWAGGER_WHITELIST))
            .map(PATH_PATTERN_PARSER::parse)
            .toList();

    /**
     * Filter的核心方法，每次請求都會執行一次<br>
     * Filter是責任練的設計模式,所以參數會有FilterChain,把參數繼續傳下去<br>
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException, RuntimeException {
        log.info("req.URI: {} ,/{}", request.getRequestURI(), request.getMethod());
        // 1.開始時間
        var start = new Date();
        try {
            if (this.checkWhiteList(request)) {
                log.debug("pattern whiteList");
                filterChain.doFilter(request, response);
                return;
            }

            String userAccount = this.checkToken(request);
            log.info("userAccount: {}", userAccount);

        } catch (ExpiredJwtException e) {
            log.info("The token has expired. Please sign in again or renew your session to continue.");
            throw e;
        }

        /**
         * finally release
         */
        filterChain.doFilter(request, response);

        var end = new Date();
        // 3.計算執行時長(ms)
        long time = end.getTime() - start.getTime();
        // 6.正常日誌
        log.info("req.URI: {} ,/{} spendTime:{} ms", request.getRequestURI(), request.getMethod(), time);
    }

    /**
     * 1.skip WhiteList<p>
     */
    @NonNull
    private boolean checkWhiteList(@NonNull HttpServletRequest request) {
        CACHED_PATTERNS.forEach(pattern -> log.debug("WhiteList pattern: {}", pattern));
        var pathContainer = PathContainer.parsePath(request.getRequestURI());
        return CACHED_PATTERNS.stream().anyMatch(pattern -> pattern.matches(pathContainer));
    }

    /**
     * 2.token vaild<p>
     */
    @NonNull
    private String checkToken(@NonNull HttpServletRequest request) throws AuthenticationException {
        // Extract JWT from the request header
        final var authHeader = request.getHeader(AUTH_HEADER);

        // Throw exception if token is not present or doesn't start with the expected format
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_TYPE)) {
            throw new AuthenticationException("token is null or not start with Bearer") {
            };
        }

        // Retrieve token from the header information
        final var jwt = authHeader.substring(7);
        final var userAccount = jwtService.extractUsername(jwt);

        // If the user hasn't been authenticated
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Fetch user details from the DB
            var userDetails = this.userDetailsService.loadUserByUsername(userAccount);

            // Check if the token is valid
            if (jwtService.isTokenVaild(jwt, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                throw new AuthenticationException("Invalid token") {
                };
            }
        }
        return userAccount;
    }

}
