package portfolio_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. If no token, skip to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract token (remove "Bearer " prefix)
        String jwt = authHeader.substring(7);

        try {
            // 4. Extract email from token
            String email = jwtUtil.extractUsername(jwt);

            // 5. If email found and not already authenticated
            if (email != null && SecurityContextHolder
                    .getContext().getAuthentication() == null) {

                // 6. Load user from database
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                // 7. Validate token
                if (jwtUtil.isTokenValid(jwt, userDetails)) {

                    // 8. Create authentication object
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null,
                                    userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    // 9. Tell Spring Security this user is authenticated
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    log.debug("Authenticated user: {}", email);
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
        }

        // 10. Continue to next filter or controller
        filterChain.doFilter(request, response);
    }
}