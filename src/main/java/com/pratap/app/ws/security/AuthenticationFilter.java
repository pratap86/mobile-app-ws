package com.pratap.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratap.app.ws.SpringApplicationContext;
import com.pratap.app.ws.service.UserService;
import com.pratap.app.ws.shared.dto.UserDto;
import com.pratap.app.ws.ui.model.request.UserLoginRequestModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}


	/**
	 * Triggered the Authentication.
	 * attemptAuthentication() triggered from http://localhost:8080/mobile-app-ws/users/login.
	 * & locate the user by spring framework 1st made the call loadUserByUsername() 
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {

		try {
			UserLoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					creds.getEmail(), 
					creds.getPassword(),
					new ArrayList<>())
					);
			
		} catch (IOException e) {
			throw new RuntimeException();
		}
		
	}

	/**
	 * called only when authentication is successful
	 */
	@Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        
        String userName = ((UserPrincipal) auth.getPrincipal()).getUsername();  
        
        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret() )
                .compact();
        
        UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto = userService.getUser(userName);
        res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
        res.addHeader("UserID", userDto.getUserId());
    }
	
}
