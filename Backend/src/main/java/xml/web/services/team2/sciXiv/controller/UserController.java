package xml.web.services.team2.sciXiv.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xml.web.services.team2.sciXiv.dto.UserBaicInfoDTO;
import xml.web.services.team2.sciXiv.dto.UserRegistrationDTO;
import xml.web.services.team2.sciXiv.exception.UserRetrievingFailedException;
import xml.web.services.team2.sciXiv.exception.UserSavingFailedException;
import xml.web.services.team2.sciXiv.model.TRole;
import xml.web.services.team2.sciXiv.model.TUser;
import xml.web.services.team2.sciXiv.service.UserService;

@RestController
@RequestMapping(value = "/users")
@CrossOrigin
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/authors", method = RequestMethod.POST)
	public ResponseEntity<?> registerAuthor(@RequestBody @Valid UserRegistrationDTO registrationRequest) {
		return registerUser(registrationRequest, TRole.AUTHOR);
	}
	
	@RequestMapping(value = "/reviewers", method = RequestMethod.POST)
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<?> registerReviewer(@RequestBody @Valid UserRegistrationDTO registrationRequest) {
		return registerUser(registrationRequest, TRole.REVIEWER);
	}
	
	@RequestMapping(value = "/possibleReviewers", method = RequestMethod.GET)
	public ResponseEntity<List<UserBaicInfoDTO>> getPossobleReviewers(@RequestParam("forPublication") String publicationTitle) throws UserRetrievingFailedException {
		List<TUser> reviewers = userService.getPossibleReviewersForPublication(publicationTitle);
		List<UserBaicInfoDTO> result = new ArrayList<UserBaicInfoDTO>();
		for (TUser reviewer : reviewers) {
			result.add(new UserBaicInfoDTO(reviewer.getEmail(), reviewer.getFirstName(), reviewer.getLastName()));
		}
		
		return new ResponseEntity<List<UserBaicInfoDTO>>(result, HttpStatus.OK);
	}
	
	private ResponseEntity<?> registerUser(UserRegistrationDTO registrationRequest, TRole userRole) {
		TUser newUser = new TUser();
		newUser.setEmail(registrationRequest.getEmail());
		newUser.setPassword(registrationRequest.getPassword());
		newUser.setFirstName(registrationRequest.getFirstName());
		newUser.setLastName(registrationRequest.getLastName());

		try {
			newUser = userService.registerUser(newUser, userRole);
			return new ResponseEntity<>(HttpStatus.CREATED);
		} catch (UserRetrievingFailedException | UserSavingFailedException e) {
			return new ResponseEntity<String>("An error occured during registration. Please try again later.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
