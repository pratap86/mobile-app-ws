package com.pratap.app.ws.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pratap.app.ws.io.entity.AddressEntity;
import com.pratap.app.ws.io.entity.UserEntity;
import com.pratap.app.ws.io.repository.AddressRepository;
import com.pratap.app.ws.io.repository.UserRepository;
import com.pratap.app.ws.service.AddressService;
import com.pratap.app.ws.shared.dto.AddressDto;
@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {

		List<AddressDto> returnValue = new ArrayList<>();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null) return returnValue;
		List<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		if(addresses != null && !addresses.isEmpty()) {
			addresses.forEach(addressEntity -> {
				returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
			});
		}
		return returnValue;
	}

	@Override
	public AddressDto getAddress(String addressId) {

		AddressDto addressDto = null;
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if (addressEntity != null) {
			addressDto = modelMapper.map(addressEntity, AddressDto.class);
		}

		return addressDto;
	}

}
