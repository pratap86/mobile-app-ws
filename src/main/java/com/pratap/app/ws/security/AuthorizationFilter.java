package com.pratap.app.ws.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.pratap.app.ws.io.entity.UserEntity;
import com.pratap.app.ws.io.repository.UserRepository;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter{

	private final UserRepository userRepository;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		String header = req.getHeader(SecurityConstants.HEADER_STRING);

		if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		String token = request.getHeader(SecurityConstants.HEADER_STRING);

		if (token != null) {

			token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

			String user = Jwts.parser().setSigningKey(SecurityConstants.getTokenSecret()).parseClaimsJws(token).getBody()
					.getSubject();

			if (user != null) {
				// modified Authorities
				UserEntity userEntity = userRepository.findByEmail(user);
				UserPrincipal userPrincipal = new UserPrincipal(userEntity);
				return new UsernamePasswordAuthenticationToken(user, null, userPrincipal.getAuthorities());
			}

			return null;
		}

		return null;
	}
	
}
