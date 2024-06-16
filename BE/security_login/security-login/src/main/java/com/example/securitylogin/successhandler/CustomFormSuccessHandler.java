package com.example.securitylogin.successhandler;

import com.example.securitylogin.dto.form.CustomUserDetails;
import com.example.securitylogin.dto.oauth2.CustomOAuth2User;
import com.example.securitylogin.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 폼 로그인 성공 후 JWT 발급
 */
@RequiredArgsConstructor
public class CustomFormSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // create JWT
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // access
        String access = jwtUtil.createJwt("access", username, role, 60 * 10 * 1000L);
        // refresh
        String refresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);

        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        return cookie;
    }
}
