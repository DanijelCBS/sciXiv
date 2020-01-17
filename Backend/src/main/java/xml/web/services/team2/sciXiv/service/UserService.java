package xml.web.services.team2.sciXiv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import xml.web.services.team2.sciXiv.exception.InvalidDataException;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.exception.UserSavingFailedException;
import xml.web.services.team2.sciXiv.model.TPublications;
import xml.web.services.team2.sciXiv.model.TRole;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private static String emailRegex = "[^@]+@[^\\.]+\\..+";
	
	public TUser registerUser(TUser newUser, TRole userRole) throws UserRetrievingFailedException, UserSavingFailedException {
		validateUserData(newUser);
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		newUser.setRole(userRole);
		newUser.setOwnPublications(new TPublications());
		newUser.setPublicationsToReview(new TPublications());
		return userRepository.save(newUser);
	}
	
	private void validateUserData(TUser user) throws UserRetrievingFailedException {
		
		if(user.getEmail().equals("")) {
			throw new InvalidDataException("user email", "Email is required.");
		}
		
		if(!user.getEmail().matches(emailRegex)) {
			throw new InvalidDataException("user email", "Email is invalid.");
		}
		
		if(user.getPassword().equals("")) {
			throw new InvalidDataException("user password", "Password is required.");
		}
		
		if(user.getFirstName().equals("")) {
			throw new InvalidDataException("user first name", "First name is required.");
		}
		
		if(user.getLastName().equals("")) {
			throw new InvalidDataException("user last name", "Last name is required.");
		}
		
		if(userRepository.getByEmail(user.getEmail()) != null) {
			throw new InvalidDataException("user email", "Given email already exists.");
		}
		
	}

}
