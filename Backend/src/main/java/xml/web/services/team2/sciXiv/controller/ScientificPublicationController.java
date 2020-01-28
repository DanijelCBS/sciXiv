package xml.web.services.team2.sciXiv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import xml.web.services.team2.sciXiv.dto.SciPubDTO;
import xml.web.services.team2.sciXiv.dto.SearchPublicationsDTO;
import xml.web.services.team2.sciXiv.exception.DocumentLoadingFailedException;
import xml.web.services.team2.sciXiv.exception.DocumentParsingFailedException;
import xml.web.services.team2.sciXiv.service.ScientificPublicationService;

import java.util.ArrayList;

@RestController
@RequestMapping(value = "/scientificPublication")
public class ScientificPublicationController {

    @Autowired
    private ScientificPublicationService scientificPublicationService;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> findByNameAndVersion(@RequestParam String name, @RequestParam int version) {
        try {
            return new ResponseEntity<>(scientificPublicationService.findByNameAndVersion(name, version), HttpStatus.OK);
        } catch (DocumentLoadingFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while saving document", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "xhtml", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Object> getScientificPublicationAsXHTML(@RequestParam String title) {
        try {
            return new ResponseEntity<>(scientificPublicationService.getScientificPublicationAsXHTML(title), HttpStatus.OK);
        } catch (DocumentLoadingFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while retrieving document", HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping(value = "anonymus/xhtml", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<Object> getAnonymusScientificPublicationAsXHTML(@RequestParam String title, @RequestParam int version) {
        try {
            return new ResponseEntity<>(scientificPublicationService.getAnonymusScientificPublicationAsXHTML(title, version), HttpStatus.OK);
        } catch (DocumentLoadingFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while retrieving document", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "metadata")
    public ResponseEntity<Object> getPublicationsMetadata(@RequestParam String title) {
        return new ResponseEntity<>(scientificPublicationService.getPublicationsMetadata(title), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> addScientificPublication(@RequestBody String sciPub) {
        try {
            return new ResponseEntity<>(scientificPublicationService.save(sciPub), HttpStatus.CREATED);
        } catch (DocumentParsingFailedException e) {
        	e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
        	e.printStackTrace();
            return new ResponseEntity<>("An error occurred while saving document", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "revise", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Object> reviseScientificPublication(@RequestBody String sciPub) {
        try {
            return new ResponseEntity<>(scientificPublicationService.revise(sciPub), HttpStatus.OK);
        } catch (DocumentParsingFailedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while revising document", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "withdraw")
    public ResponseEntity<Object> withdrawScientificPublication(@RequestParam String title) {
        try {
            return new ResponseEntity<>(scientificPublicationService.withdraw(title), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while withdrawing document", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "basicSearch")
    public ResponseEntity<Object> basicSearch(@RequestParam String parameter) {
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
    public ResponseEntity<ArrayList<SciPubDTO>> getReferences(@RequestParam String title) {
        return new ResponseEntity<>(scientificPublicationService.getReferences(title), HttpStatus.OK);
    }
}
