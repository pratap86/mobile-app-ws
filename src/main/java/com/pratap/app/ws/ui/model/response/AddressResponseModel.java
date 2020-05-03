package com.pratap.app.ws.ui.model.response;

import org.springframework.hateoas.RepresentationModel;

/**
 * 
 * @author Pratap Narayan
 * <p>AddressResponseModel extends ResourceSupport changed to -> RepresentationModel to achieve the HATEOAS support </p>
 *
 */
public class AddressResponseModel extends RepresentationModel<AddressResponseModel>{

	private String addressId;
	private String city;
	private String state;
	private String country;
	private String streetName;
	private String postalCode;
	private String type;
	public String getAddressId() {
		return addressId;
	}
	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
