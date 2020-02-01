export class ScientificPublicationDTO {
  public title: string;
  public authors: string[];

  constructor(title: string, authors: string[]) {
    this.title = title;
    this.authors = authors;
  }
}
