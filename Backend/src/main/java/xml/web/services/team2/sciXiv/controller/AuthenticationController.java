package xml.web.services.team2.sciXiv.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import xml.web.services.team2.sciXiv.dto.LoginRequestDTO;
import xml.web.services.team2.sciXiv.dto.LoginResponseDTO;
import xml.web.services.team2.sciXiv.util.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtils;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
		try {
			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginRequest.getEmail(), 
							loginRequest.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// Create JSON web token for user
			User user = (User) authentication.getPrincipal();
			String jwt = jwtUtils.generateToken(user);
			
			// Return token for successful authentication
			LoginResponseDTO response = new LoginResponseDTO(jwt);
			return ResponseEntity.ok(response);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<String>("Invalid email or password", HttpStatus.BAD_REQUEST);
		}
	}

}
