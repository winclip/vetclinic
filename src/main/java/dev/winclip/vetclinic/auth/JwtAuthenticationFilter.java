package dev.winclip.vetclinic.auth;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.hasText(header) && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
			String raw = header.substring(7).trim();
			jwtService.parseBearerToken(raw).ifPresent(auth -> {
				if (auth instanceof UsernamePasswordAuthenticationToken) {
					UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
					token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				}
				SecurityContextHolder.getContext().setAuthentication(auth);
			});
		}
		filterChain.doFilter(request, response);
	}
}
