package xml.web.services.team2.sciXiv;

import javax.xml.transform.TransformerException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SciXivApplication {

	public static void main(String[] args) throws TransformerException {
		SpringApplication.run(SciXivApplication.class, args);

	}

}
