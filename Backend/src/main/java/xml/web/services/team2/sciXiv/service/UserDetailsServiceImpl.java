package xml.web.services.team2.sciXiv.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		try {
			
			TUser user = userRepository.getByEmail(email);
			if (user == null) {
				throw new UsernameNotFoundException("User does not exist.");
			}
			List<GrantedAuthority> userAuthorities = Arrays
					.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()));
			User userDetails = new User(user.getEmail(), user.getPassword(), userAuthorities);
			return userDetails;
			
		} catch (UserRetrievingFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
