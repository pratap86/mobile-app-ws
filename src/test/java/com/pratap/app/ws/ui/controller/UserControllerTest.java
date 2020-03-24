package com.pratap.app.ws.ui.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pratap.app.ws.service.impl.UserServiceImpl;
import com.pratap.app.ws.shared.dto.AddressDto;
import com.pratap.app.ws.shared.dto.UserDto;
import com.pratap.app.ws.ui.model.response.UserDetailsResponseModel;

class UserControllerTest {

	@InjectMocks
	private UserController userController;

	@Mock
	private UserServiceImpl userService;

	private UserDto userDto;

	private static final String FIRST_NAME = "Pratap";

	private static final String LAST_NAME = "Narayan";

	private static final String TEST_MAIL = "test@mail.com";

	private static final String ENCRYPTED_PASS = "123EdrtyYUI";

	private static final String USER_ID = "UFX1234";

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userDto = new UserDto();
		userDto.setFirstName(FIRST_NAME);
		userDto.setLastName(LAST_NAME);
		userDto.setEmail(TEST_MAIL);
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEncryptedPassword(ENCRYPTED_PASS);
	}

	private List<AddressDto> getAddressesDto() {
		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");
		addressDto.setCity("Vancouver");
		addressDto.setCountry("Canada");
		addressDto.setPostalCode("ABC123");
		addressDto.setStreetName("123 Street name");

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billling");
		billingAddressDto.setCity("Vancouver");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("ABC123");
		billingAddressDto.setStreetName("123 Street name");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);
		addresses.add(billingAddressDto);

		return addresses;

	}

	@Test
	void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		UserDetailsResponseModel userDetailsResponseModel = userController.getUser(userDto.getUserId());
		assertNotNull(userDetailsResponseModel, "userDetailsResponseModel is null");
		assertEquals(userDto.getUserId(), userDetailsResponseModel.getUserId());
		assertEquals(userDto.getFirstName(), userDetailsResponseModel.getFirstName());
		assertEquals(userDto.getLastName(), userDetailsResponseModel.getLastName());
		assertTrue(userDto.getAddresses().size() == userDetailsResponseModel.getAddresses().size());
	}

}
