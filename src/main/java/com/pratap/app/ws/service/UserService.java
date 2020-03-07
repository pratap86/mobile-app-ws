package com.pratap.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.pratap.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService{

	public UserDto createUser(UserDto userDto);
}
