package com.pratap.app.ws.shared.dto;

import java.util.List;

public class RoleDto {

	private long id;
	
	private String name;
	
	private List<AuthoritiesDto> authorities;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AuthoritiesDto> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<AuthoritiesDto> authorities) {
		this.authorities = authorities;
	}
	
}
