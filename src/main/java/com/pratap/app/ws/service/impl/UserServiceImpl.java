package com.pratap.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratap.app.ws.exceptions.UserServiceException;
import com.pratap.app.ws.io.entity.PasswordResetTokenEntity;
import com.pratap.app.ws.io.entity.UserEntity;
import com.pratap.app.ws.io.repository.PasswordResetTokenRepository;
import com.pratap.app.ws.io.repository.UserRepository;
import com.pratap.app.ws.security.UserPrincipal;
import com.pratap.app.ws.service.UserService;
import com.pratap.app.ws.shared.AmazonSES;
import com.pratap.app.ws.shared.Utils;
import com.pratap.app.ws.shared.dto.UserDto;
import com.pratap.app.ws.ui.model.response.ErrorMessages;

/**
 * Implement {@link UserService}
 * 
 * @author Pratap Narayan
 *
 */

@Service
public class UserServiceImpl implements UserService {

	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	ObjectMapper jsonMapper;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Override
	public UserDto createUser(UserDto userDto) throws JsonProcessingException {

		log.info("createUser() method called with {} ", jsonMapper.writeValueAsString(userDto));
		if (userRepository.findByEmail(userDto.getEmail()) != null)
			throw new RuntimeException("Record already exist");

		//
		AtomicInteger counter = new AtomicInteger(0);
		userDto.getAddresses().forEach(address -> {
			address.setUserDetails(userDto);
			address.setAddressId(Utils.generateAddressId(30));
			userDto.getAddresses().set(counter.getAndIncrement(), address);
		});
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

		String publicUserId = Utils.generateUserId(30);

		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setEmailVerificationToken(Utils.generatedEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);

		UserEntity storedUserDetail = userRepository.save(userEntity);

		UserDto returnValue = modelMapper.map(storedUserDetail, UserDto.class);

		// send an email message to user to verify their email address
		//new AmazonSES().verifyEmail(returnValue);

		return returnValue;
	}

	// triggered by spring web security : UserDetailsService -> loadUserByUsername()
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new UserPrincipal(userEntity);
		
//		  return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
//		  userEntity.getEmailVerificationStatus(), true, true, true, new
//		  ArrayList<>());
		 
		// return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new
		// ArrayList<>());
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UsernameNotFoundException(userId);
		BeanUtils.copyProperties(userEntity, returnValue);
		return returnValue;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		// Null Check
		if (userEntity.getFirstName() != null && userEntity.getLastName() != null) {
			userEntity.setFirstName(userDto.getFirstName());
			userEntity.setLastName(userDto.getLastName());
		}
		UserEntity updatedEntity = userRepository.save(userEntity);
		BeanUtils.copyProperties(updatedEntity, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {

		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userRepository.delete(userEntity);

	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValues = new ArrayList<>();
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage = userRepository.findAll(pageableRequest);
		List<UserEntity> users = userPage.getContent();
		if (users.isEmpty() || users == null)
			throw new UserServiceException(ErrorMessages.NO_RECORDS_FOUND.getErrorMessage());
		users.forEach(userEntity -> {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValues.add(userDto);
		});
		return returnValues;
	}

	@Override
	public boolean verifyEmailToken(String token) {

		boolean returnValue = false;

		// Find user by token
		UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

		if (userEntity != null) {
			boolean hastokenExpired = Utils.hasTokenExpired(token);
			if (!hastokenExpired) {
				// erase token to prevent successive attempt
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}

		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {

		boolean returnValue = false;

		UserEntity userEntity = userRepository.findByEmail(email);

		if (userEntity == null) {
			return returnValue;
		}

		// generate password reset token
		String token = Utils.generatePasswordResetToken(userEntity.getUserId());

		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		// update PasswordResetTokenEntity with token & user details
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		
		// and save this PRTEntity in to DB
		passwordResetTokenRepository.save(passwordResetTokenEntity);

		// once PRTEntity saved in to DB, send the mail to Client by calling AWS SES 
		returnValue = new AmazonSES()
						.sendPasswordResetRequest(userEntity.getFirstName(), userEntity.getEmail(), token);

		return returnValue;
	}

}
