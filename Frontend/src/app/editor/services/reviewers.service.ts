import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReviewersService {

  private readonly baseUrl = 'http://localhost:8080/users';

  constructor(private http: HttpClient) { }

  public getPossibleReviewers(publicationTitle: string) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.get(`${this.baseUrl}/possibleReviewers?forPublication=${publicationTitle}`, {headers});
  }
}
