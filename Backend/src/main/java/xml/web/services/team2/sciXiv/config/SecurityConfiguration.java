package xml.web.services.team2.sciXiv.config;

import org.apache.tools.ant.launch.AntMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import xml.web.services.team2.sciXiv.util.JwtRequestFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtRequestFilter jwtAuthFilter;
	
	@Autowired
	public void configureAuthentication(
			AuthenticationManagerBuilder authenticationManagerBuilder)
			throws Exception {
		
		authenticationManagerBuilder
				.userDetailsService(this.userDetailsService).passwordEncoder(
						passwordEncoder());
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.csrf().disable()
		.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/users/authors").permitAll()
		.antMatchers(HttpMethod.POST, "/auth").permitAll()
		.antMatchers(HttpMethod.GET, "/scientificPublication/**").permitAll()
		.antMatchers(HttpMethod.GET, "/reviews/merge**").permitAll()
		.antMatchers(HttpMethod.GET, "/coverLetter/xhtml**").permitAll()
		.antMatchers(HttpMethod.GET, "/coverLetter/export**").permitAll();
		//  .anyRequest().permitAll();
		
		// Custom JWT based authentication
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
	}

}
