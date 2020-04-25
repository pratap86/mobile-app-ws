package com.pratap.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.pratap.app.ws.service.impl.UserServiceImpl;
import com.pratap.app.ws.shared.dto.UserDto;
/**
 * 
 * @author Pratap Narayan
 * @see UserServiceImpl
 *
 */
public interface UserService extends UserDetailsService{

	UserDto createUser(UserDto userDto) throws Exception;

	UserDto getUser(String email);

	UserDto getUserByUserId(String id);

	UserDto updateUser(String id, UserDto userDto);
	
	void deleteUser(String userId);

	List<UserDto> getUsers(int page, int limit);
	/**
	 * 
	 * @param token
	 * @return boolean value based on logic implementation
	 */
	boolean verifyEmailToken(String token);
}
