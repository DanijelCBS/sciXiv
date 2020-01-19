package xml.web.services.team2.sciXiv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
import xml.web.services.team2.sciXiv.dto.SearchPublicationsDTO;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.service.ScientificPublicationService;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/scientificPublication")
public class ScientificPublicationController {

    @Autowired
    private ScientificPublicationService scientificPublicationService;

    @PostMapping
    public ResponseEntity<Object> addScientificPublication(String sciPub) {
        try {
            return new ResponseEntity<>(scientificPublicationService.save(sciPub), HttpStatus.CREATED);
        } catch (DocumentParsingFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while saving document", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "revise")
    public ResponseEntity<Object> reviseScientificPublication(String sciPub) {
        try {
            return new ResponseEntity<>(scientificPublicationService.revise(sciPub), HttpStatus.OK);
        } catch (DocumentParsingFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while revising document", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "withdraw")
    public ResponseEntity<Object> withdrawScientificPublication(String title) {
        try {
            return new ResponseEntity<>(scientificPublicationService.withdraw(title), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while withdrawing document", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "basicSearch")
    public ResponseEntity<Object> basicSearch(String parameter) {
        try {
            return new ResponseEntity<>(scientificPublicationService.basicSearch(parameter), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching documents", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "advancedSearch")
    public ResponseEntity<Object> advancedSearch(SearchPublicationsDTO searchParameters) {
        try {
            return new ResponseEntity<>(scientificPublicationService.advancedSearch(searchParameters), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while searching documents", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "references")
    public ResponseEntity<ArrayList<SciPubDTO>> getReferences(String title) {
        return new ResponseEntity<>(scientificPublicationService.getReferences(title), HttpStatus.OK);
    }
}
