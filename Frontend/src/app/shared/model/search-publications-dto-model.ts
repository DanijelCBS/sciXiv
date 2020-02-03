export class SearchPublicationsDTO {
  public title: string;
  public dateReceived: string;
  public dateRevised: string;
  public dateAccepted: string;
  public authorName: string;
  public authorAffiliation: string;
  public keyword: string;
  public authors: string[];
  public authorsAffiliations: string[];
  public keywords: string[];

  constructor(title: string, dateReceived: string, dateRevised: string, dateAccepted: string, authorName: string, authorAffiliation: string, keyword: string, authors: string[], authorsAffiliations: string[], keywords: string[]) {
    this.title = title;
    this.dateReceived = dateReceived;
    this.dateRevised = dateRevised;
    this.dateAccepted = dateAccepted;
    this.authorName = authorName;
    this.authorAffiliation = authorAffiliation;
    this.keyword = keyword;
    this.authors = authors;
    this.authorsAffiliations = authorsAffiliations;
    this.keywords = keywords;
  }
}
