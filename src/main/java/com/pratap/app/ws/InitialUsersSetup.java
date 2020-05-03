package com.pratap.app.ws;

import java.util.Arrays;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.pratap.app.ws.io.entity.AuthorityEntity;
import com.pratap.app.ws.io.entity.RoleEntity;
import com.pratap.app.ws.io.entity.UserEntity;
import com.pratap.app.ws.io.repository.AuthorityRepository;
import com.pratap.app.ws.io.repository.RoleRepository;
import com.pratap.app.ws.io.repository.UserRepository;
import com.pratap.app.ws.shared.Utils;

//@Component
public class InitialUsersSetup {

	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@Autowired
	UserRepository userRepository;
	
	@EventListener
	@Transactional
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("From Application ready event..");
		
		AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
		AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
		AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
		
		createRole("ROLE_USER", Arrays.asList(readAuthority, writeAuthority));
		createRole("ROLE_ADMIN", Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
		
	}
	
	@Transactional
	private AuthorityEntity createAuthority(String name) {
		
		AuthorityEntity authority = authorityRepository.findByName(name);
		
		if( authority == null ) {
			authority = new AuthorityEntity(name);
			authorityRepository.save(authority);
		}
		return authority;
	}
	
	// A single role can has multiple authorities
	@Transactional
	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		
		RoleEntity role = roleRepository.findByName(name);
		
		if(role == null) {
			role = new RoleEntity(name);
			role.setAuthorities(authorities);
			roleRepository.save(role);
		}
		return role;
	}
}
