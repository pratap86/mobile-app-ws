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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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


	@InjectMocks
	private UserServiceImpl userServiceImpl;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private UserEntity userEntity;
	
	private UserDto userDto;
	
	private AddressDto shippingAddressDto;
	
	private AddressDto billingAddressDto;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userEntity = new UserEntity();
		userEntity.setId(ENTITY_ID);
		userEntity.setFirstName(FIRST_NAME);
		userEntity.setLastName(LAST_NAME);
		userEntity.setUserId(USER_ID);
		userEntity.setEncryptedPassword(ENCRYPTED_PASS);
		
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
	}

	@Test
	final void testGetUser() {
		
		when(userRepository.findByEmail( anyString() )).thenReturn(userEntity);
		
		UserDto userDto = userServiceImpl.getUser(TEST_MAIL);
		assertNotNull(userDto, "userDto object is null");
		assertEquals("Pratap", userDto.getFirstName());
		
	}
	
	@Test// JUnit 4 way - (expected = UsernameNotFoundException.class)
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

}
