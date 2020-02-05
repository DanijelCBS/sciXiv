export class ScientificPublicationDTO {
  public title: string;
  public authors: string[];
  public ownPublication: boolean;

  constructor(title: string, authors: string[], ownPublication: boolean) {
    this.title = title;
    this.authors = authors;
    this.ownPublication = ownPublication;
  }
}
