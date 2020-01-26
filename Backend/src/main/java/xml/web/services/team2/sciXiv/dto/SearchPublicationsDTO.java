package xml.web.services.team2.sciXiv.dto;

import java.util.ArrayList;

public class SearchPublicationsDTO {

    private String title;
    private String dateReceived;
    private String dateRevised;
    private String dateAccepted;
    private String authorName;
    private String authorAffiliation;
    private String keyword;
    private ArrayList<String> authors;
    private ArrayList<String> authorsAffiliations;
    private ArrayList<String> keywords;

    public SearchPublicationsDTO() {
        authors = new ArrayList<>();
        authorsAffiliations = new ArrayList<>();
        keywords = new ArrayList<>();
    }

    public SearchPublicationsDTO(String title, String dateReceived, String dateRevised,
                                 String dateAccepted, String authorName, String authorAffiliation, String keyword) {
        this.title = title;
        this.dateReceived = dateReceived;
        this.dateRevised = dateRevised;
        this.dateAccepted = dateAccepted;
        this.authorName = authorName;
        this.authorAffiliation = authorAffiliation;
        this.keyword = keyword;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getDateRevised() {
        return dateRevised;
    }

    public void setDateRevised(String dateRevised) {
        this.dateRevised = dateRevised;
    }

    public String getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(String dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAffiliation() {
        return authorAffiliation;
    }

    public void setAuthorAffiliation(String authorAffiliation) {
        this.authorAffiliation = authorAffiliation;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public ArrayList<String> getAuthorsAffiliations() {
        return authorsAffiliations;
    }

    public void setAuthorsAffiliations(ArrayList<String> authorsAffiliations) {
        this.authorsAffiliations = authorsAffiliations;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }
}
