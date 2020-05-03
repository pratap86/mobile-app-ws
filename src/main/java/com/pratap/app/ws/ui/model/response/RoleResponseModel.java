package com.pratap.app.ws.ui.model.response;

import java.util.List;

public class RoleResponseModel {

	private String name;
	
	private List<AuthoritiesResponseModel> authorities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AuthoritiesResponseModel> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<AuthoritiesResponseModel> authorities) {
		this.authorities = authorities;
	}
	
}
