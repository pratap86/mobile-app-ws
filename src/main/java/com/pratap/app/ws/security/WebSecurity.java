package com.pratap.app.ws.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pratap.app.ws.io.repository.UserRepository;
import com.pratap.app.ws.service.UserService;

@EnableGlobalMethodSecurity( securedEnabled = true, prePostEnabled = true )
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final UserService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;

	public WebSecurity(UserService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
		this.userDetailsService = userDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
		.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
		.permitAll()
		.antMatchers(HttpMethod.GET, SecurityConstants.VERIFICATION_EMAIL_URL)
		.permitAll()
		.antMatchers(HttpMethod.POST, SecurityConstants.PASSWORD_RESET_REQUEST_URL)
		.permitAll()
//		.antMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
//		.antMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("DELETE_AUTHORITY")
		.anyRequest().authenticated().and()
		.addFilter(getAuthenticationFilter())
		.addFilter(new AuthorizationFilter(authenticationManager(), userRepository))
		.sessionManagement()// -> making your REST API state less
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 * <p>customized spring's default login url(/login)</p>
	 * when user makes the request http://localhost:8080/mobile-app-ws/users/login, 
	 * than its made the called to AuthenticationFilter class's attemptAuthentication() method
	 */
	protected AuthenticationFilter getAuthenticationFilter() throws Exception {
	    final AuthenticationFilter filter = new AuthenticationFilter(authenticationManager());
	    filter.setFilterProcessesUrl("/users/login");
	    return filter;
	}
}
