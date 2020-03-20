package com.pratap.app.ws.service;

import java.util.List;

import com.pratap.app.ws.shared.dto.AddressDto;

public interface AddressService {

	List<AddressDto> getAddresses(String userId);
	
	AddressDto getAddress(String addressId);
}
