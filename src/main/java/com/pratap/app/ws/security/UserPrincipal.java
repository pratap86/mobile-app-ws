package com.pratap.app.ws.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pratap.app.ws.io.entity.AuthorityEntity;
import com.pratap.app.ws.io.entity.RoleEntity;
import com.pratap.app.ws.io.entity.UserEntity;

public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 8979213358573497067L;

	UserEntity userEntity;
	
	public UserPrincipal(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		List<AuthorityEntity> authorityEntities = new ArrayList<>();
		
		// Get user roles
		Collection<RoleEntity> roles = userEntity.getRoles();
		
		if( roles == null ) return authorities;
		
		// continue with roles
		roles.forEach( (role) -> {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
			// for each role, contains multiple authorities
			authorityEntities.addAll(role.getAuthorities());
		});
		
		authorityEntities.forEach( (authorityEntity) -> {
			authorities.add( new SimpleGrantedAuthority(authorityEntity.getName()) );
		});
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.userEntity.getEncryptedPassword();
	}

	@Override
	public String getUsername() {
		return this.userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.userEntity.getEmailVerificationStatus();
	}

}
