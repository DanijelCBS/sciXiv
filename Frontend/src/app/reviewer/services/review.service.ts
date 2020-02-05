import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private readonly baseUrl = 'http://localhost:8080/reviews';

  constructor(private http: HttpClient) { }

  public getReviewAssignments() {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.get(`${this.baseUrl}/assignments`, {headers});
  }

  public acceptReviewAssignment(publicationTitle: string) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.put(`${this.baseUrl}/acceptAssignment?forPublication=${publicationTitle}`, {}, {headers});
  }

  public rejectReviewAssignment(publicationTitle: string) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.put(`${this.baseUrl}/rejectAssignment?forPublication=${publicationTitle}`, {}, {headers});
  }

}
