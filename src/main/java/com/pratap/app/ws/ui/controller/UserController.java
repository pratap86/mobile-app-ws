package com.pratap.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pratap.app.ws.exceptions.UserServiceException;
import com.pratap.app.ws.service.AddressService;
import com.pratap.app.ws.service.UserService;
import com.pratap.app.ws.shared.dto.AddressDto;
import com.pratap.app.ws.shared.dto.UserDto;
import com.pratap.app.ws.ui.model.request.UserDetailsRequestModel;
import com.pratap.app.ws.ui.model.response.AddressResponseModel;
import com.pratap.app.ws.ui.model.response.ErrorMessages;
import com.pratap.app.ws.ui.model.response.OperationStatusModel;
import com.pratap.app.ws.ui.model.response.RequestOperationStatus;
import com.pratap.app.ws.ui.model.response.UserDetailsResponseModel;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AddressService addressService;
	
	@GetMapping(path = "/{id}", 
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserDetailsResponseModel getUser(@PathVariable String id) {

		UserDetailsResponseModel returnValue = new UserDetailsResponseModel();
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		return returnValue;
	}
	
	@PostMapping(
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserDetailsResponseModel createUser(@RequestBody UserDetailsRequestModel userDetailReq) throws Exception {
		
		if(userDetailReq.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		// use ModelMapper instead of BeanUtils to map the objects bcz object contains another object
		UserDto userDto = modelMapper.map(userDetailReq, UserDto.class);
		UserDto createdUser = userService.createUser(userDto);
		return modelMapper.map(createdUser, UserDetailsResponseModel.class);
	}
	
	@PutMapping(path = "/{id}",
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public UserDetailsResponseModel updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetailReq) {

		UserDetailsResponseModel returnValue = new UserDetailsResponseModel();
		UserDto userDto = new UserDto();
		
		if(userDetailReq.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		BeanUtils.copyProperties(userDetailReq, userDto);
		UserDto updatedUser = userService.updateUser(id, userDto);
		
		BeanUtils.copyProperties(updatedUser, returnValue);
		return returnValue;
	}
	
	@DeleteMapping(path = "/{id}",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}
	// http:8080/users?page=2&limit=50 (query String)
	@GetMapping(
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
			)
	public List<UserDetailsResponseModel> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, 
			@RequestParam(value = "limit", defaultValue = "25") int limit){
		List<UserDetailsResponseModel> returnValues = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		users.forEach(userDto -> {
			UserDetailsResponseModel userModel = new UserDetailsResponseModel();
			BeanUtils.copyProperties(userDto, userModel);
			returnValues.add(userModel);
		});
		
		return returnValues;
	}
	//End-point http://localhost:8080/mobile-app-ws/users/<user-id>/address
	@GetMapping(path = "/{id}/address", 
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<AddressResponseModel> getUserAddresses(@PathVariable String id) {

		List<AddressResponseModel> returnValue = new ArrayList<>();
		List<AddressDto> addressesDto = addressService.getAddresses(id);
		// go to ModelMapper generics - http://modelmapper.org/user-manual/generics/
		if(addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressResponseModel>>() {}.getType();
			returnValue = modelMapper.map(addressesDto, listType);
		}
		return returnValue;
	}
	
	//End-point http://localhost:8080/mobile-app-ws/users/<user-id>/address/<address-id>
		@GetMapping(path = "/{userId}/address/{addressId}", 
				produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
		public AddressResponseModel getUserAddress(@PathVariable String addressId) {

			AddressDto addressDto = addressService.getAddress(addressId);
			return modelMapper.map(addressDto, AddressResponseModel.class);
		}
}
