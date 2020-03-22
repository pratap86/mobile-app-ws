package com.pratap.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.pratap.app.ws.exceptions.UserServiceException;
import com.pratap.app.ws.io.entity.UserEntity;
import com.pratap.app.ws.io.repository.UserRepository;
import com.pratap.app.ws.service.UserService;
import com.pratap.app.ws.shared.Utils;
import com.pratap.app.ws.shared.dto.UserDto;
import com.pratap.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto userDto) {

		if (userRepository.findByEmail(userDto.getEmail()) != null)
			throw new UserServiceException("Record already exist");
		
		//
		AtomicInteger counter = new AtomicInteger(0);
		userDto.getAddresses().forEach(address -> {
			address.setUserDetails(userDto);
			address.setAddressId(utils.generateAddressId(30));
			userDto.getAddresses().set(counter.getAndIncrement(), address);
		});
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

		String generateUserId = utils.generateUserId(30);
		
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		userEntity.setUserId(generateUserId);

		UserEntity storedUserDetail = userRepository.save(userEntity);
		return modelMapper.map(storedUserDetail, UserDto.class);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
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
		if(userEntity.getFirstName() != null && userEntity.getLastName() != null) {
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

}
