package com.pratap.app.ws.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pratap.app.ws.exceptions.UserServiceException;
import com.pratap.app.ws.io.entity.UserEntity;
import com.pratap.app.ws.io.repository.UserRepository;
import com.pratap.app.ws.shared.Utils;
import com.pratap.app.ws.shared.dto.AddressDto;
import com.pratap.app.ws.shared.dto.UserDto;

class UserServiceImplTest {

	private static final String FIRST_NAME = "Pratap";

	private static final String LAST_NAME = "Narayan";

	private static final String ADDRESS_ID = "qwQW12345";

	private static final String TEST_MAIL = "test@mail.com";

	private static final long ENTITY_ID = 1L;

	private static final String ENCRYPTED_PASS = "123EdrtyYUI";

	private static final String USER_ID = "UFX1234";

	private static final int PAGE = 1;

	private static final int LIMIT = 25;

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@Mock
	private UserRepository userRepository;

	@Mock
	private Utils utils;

	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
//	@Mock
	private Page<UserEntity> page;

	private UserEntity userEntity;

	private UserDto userDto;

	private AddressDto shippingAddressDto;

	private AddressDto billingAddressDto;
	
	private List<UserDto> userDtos;
	
	private List<UserEntity> userEntities = new ArrayList<>();

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userEntity = new UserEntity();
		userEntity.setId(ENTITY_ID);
		userEntity.setFirstName(FIRST_NAME);
		userEntity.setLastName(LAST_NAME);
		userEntity.setEmail(TEST_MAIL);
		userEntity.setUserId(USER_ID);
		userEntity.setEncryptedPassword(ENCRYPTED_PASS);
		
		userEntities.add(userEntity);

		shippingAddressDto = new AddressDto();
		shippingAddressDto.setType("shipping");
		shippingAddressDto.setState("Karnataka");
		shippingAddressDto.setCity("Bangalore");
		shippingAddressDto.setCountry("India");
		shippingAddressDto.setPostalCode("560090");
		shippingAddressDto.setStreetName("New Avenue Road");

		billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setState("Karnataka");
		billingAddressDto.setCity("Bangalore");
		billingAddressDto.setCountry("India");
		billingAddressDto.setPostalCode("560090");
		billingAddressDto.setStreetName("New Avenue Road");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(shippingAddressDto);
		addresses.add(billingAddressDto);

		userDto = new UserDto();
		userDto.setAddresses(addresses);
		userDto.setEmail(TEST_MAIL);
		userDto.setFirstName(FIRST_NAME);
		userDto.setLastName(LAST_NAME);
		userDto.setPassword(ENCRYPTED_PASS);
		
		UserDto secUserDto = new UserDto();
		secUserDto.setAddresses(addresses);
		secUserDto.setEmail(TEST_MAIL);
		secUserDto.setFirstName(FIRST_NAME);
		secUserDto.setLastName(LAST_NAME);
		secUserDto.setPassword(ENCRYPTED_PASS);
		
		userDtos = new ArrayList<>();
		userDtos.add(userDto);
//		userDtos.add(secUserDto);
		
		page = new PageImpl<>(userEntities);
	}

	@Test
	final void testGetUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto getUserDto = userServiceImpl.getUser(TEST_MAIL);
		assertNotNull(getUserDto, "getUserDto object is null");
		assertEquals("Pratap", userDto.getFirstName());

	}

	@Test // JUnit 4 way - (expected = UsernameNotFoundException.class)
	final void testGetUser_UsernameNotFoundException() {

		when(userRepository.findByEmail(TEST_MAIL)).thenReturn(null);
		// JUnit 5 way, to handle Exception
		assertThrows(UsernameNotFoundException.class, () -> {
			userServiceImpl.getUser(TEST_MAIL);
		});
	}

	@Test
	final void testCreateUser() {

		when(utils.generateAddressId(anyInt())).thenReturn(ADDRESS_ID);

		when(utils.generateUserId(anyInt())).thenReturn(USER_ID);

		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASS);

		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

		UserDto storedUserDetails = userServiceImpl.createUser(userDto);

		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
	}

	@Test
	final void testCreateUser_CreateUserServiceException() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.createUser(userDto);
		});

	}

	@Test
	final void testLoadUserByUsername() {

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDetails userDetails = userServiceImpl.loadUserByUsername(TEST_MAIL);
		assertNotNull(userDetails, "userDetails is null");
	}

	@Test
	final void testLoadUserByUsername_UsernameNotFoundException() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		assertThrows(UsernameNotFoundException.class, () -> {
			userServiceImpl.loadUserByUsername(TEST_MAIL);
		});
	}

	@Test
	final void testGetUserByUserId() {

		when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
		UserDto userDto = userServiceImpl.getUserByUserId(USER_ID);
		assertNotNull(userDto, "userDto is null");
		assertEquals(userDto.getFirstName(), userEntity.getFirstName());
	}

	@Test
	final void testGetUserByUserId_UsernameNotFoundException() {

		when(userRepository.findByUserId(anyString())).thenReturn(null);
		assertThrows(UsernameNotFoundException.class, () -> {
			userServiceImpl.getUserByUserId(USER_ID);
		});
	}

	@Test
	final void testUpdateUser() {

		when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
		when(userRepository.save(any())).thenReturn(userEntity);
		UserDto updatedDto = userServiceImpl.updateUser(USER_ID, userDto);
		assertNotNull(userEntity, "userEntity is null");
		assertEquals(userDto.getFirstName(), updatedDto.getFirstName());
	}

	@Test
	final void testUpdateUser_UserServiceException() {

		when(userRepository.findByUserId(anyString())).thenReturn(null);
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.updateUser(USER_ID, userDto);
		});
	}

	@Test
	final void testDeleteUser() {

		when(userRepository.findByUserId(anyString())).thenReturn(userEntity);
		userServiceImpl.deleteUser(USER_ID);
		assertNotNull(userEntity, "userEntity is null");
	}

	@Test
	final void testDeleteUser_UserServiceException() {

		when(userRepository.findByUserId(anyString())).thenReturn(null);
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.deleteUser(USER_ID);
		});
	}
	
	@Test
	@DisplayName("Object not matched")
	final void testGetUsers() {
		
		Pageable pageableRequest = PageRequest.of(PAGE, LIMIT);
		when(userRepository.findAll(pageableRequest)).thenReturn(page);
		List<UserDto> users = userServiceImpl.getUsers(PAGE, LIMIT);
		assertNotNull(users, "users details is null");
		Assertions.assertSame(userEntities.get(0).getFirstName(), users.get(0).getFirstName());
	}
	
//	@Test
	final void testGetusers_UserServiceException() {
		
		Pageable pageableRequest = PageRequest.of(PAGE, LIMIT);
		when(userRepository.findAll(pageableRequest)).thenReturn(page);
		when(page.getContent()).thenReturn(null);
		assertThrows(UserServiceException.class, () -> {
			userServiceImpl.getUsers(PAGE, LIMIT);
		});
	}
}
