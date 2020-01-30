package xml.web.services.team2.sciXiv.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import xml.web.services.team2.sciXiv.exception.InvalidDataException;
import xml.web.services.team2.sciXiv.exception.OperationFailureException;
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
	
	public TUser findByEmail(String email) throws UserRetrievingFailedException {
		return this.userRepository.getByEmail(email);
	}
	
	public List<TUser> getPossibleReviewersForPublication(String publicationTitle) throws UserRetrievingFailedException {
		return this.userRepository.getPossibleReviewersForPublicaton(publicationTitle);
	}
	
	public void addPublicationToReview(String publicationTitle, String reviewerEmail) throws UserRetrievingFailedException, UserSavingFailedException {
		TUser reviewer = this.userRepository.getByEmail(reviewerEmail);
		if(reviewer == null) {
			throw new ResourceNotFoundException(String.format("Unable to find user: %s", reviewerEmail));
		}
		
		if(reviewer.getRole() != TRole.AUTHOR) {
			reviewer.getPublicationsToReview().getPublicationID().add(publicationTitle);
			this.userRepository.save(reviewer);
		} else {
			throw new OperationFailureException("Can not assign review to author user: " + reviewerEmail);
		}
	}
	
	public void removePublicationToReview(String publicationTitle, String reviewerEmail) throws UserRetrievingFailedException, UserSavingFailedException {
		TUser reviewer = this.userRepository.getByEmail(reviewerEmail);
		if(reviewer == null) {
			throw new ResourceNotFoundException(String.format("Unable to find user: %s", reviewerEmail));
		}
		
		Iterator<String> it = reviewer.getPublicationsToReview().getPublicationID().iterator();
		String currentPublication;
		while(it.hasNext()) {
			currentPublication = it.next();
			if(currentPublication.equals(publicationTitle)) {
				it.remove();
			}
		}
		this.userRepository.save(reviewer);
	}
	
	public TUser getEditor() {
		return this.userRepository.getEditor();
	}
	
	private void validateUserData(TUser user) throws UserRetrievingFailedException {
		
		if(!user.getEmail().matches(emailRegex)) {
			throw new InvalidDataException("user email", "Email is invalid.");
		}
		
		if(userRepository.getByEmail(user.getEmail()) != null) {
			throw new InvalidDataException("user email", "Given email already exists.");
		}
		
	}

}
