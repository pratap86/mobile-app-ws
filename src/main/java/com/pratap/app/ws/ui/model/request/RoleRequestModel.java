package com.pratap.app.ws.ui.model.request;

import java.util.List;

public class RoleRequestModel {

	private String name;
	
	private List<AuthoritiesRequestModel> authorities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AuthoritiesRequestModel> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<AuthoritiesRequestModel> authorities) {
		this.authorities = authorities;
	}
	
}
