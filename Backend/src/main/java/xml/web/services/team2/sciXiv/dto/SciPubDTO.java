package xml.web.services.team2.sciXiv.dto;

import java.util.ArrayList;

public class SciPubDTO {
    private String title;
    private ArrayList<String> authors;
    private boolean ownPublication;

    public SciPubDTO() {
        ownPublication = false;
    }

    public SciPubDTO(String title, ArrayList<String> authors) {
        this.title = title;
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public boolean isOwnPublication() {
        return ownPublication;
    }

    public void setOwnPublication(boolean ownPublication) {
        this.ownPublication = ownPublication;
    }
}
