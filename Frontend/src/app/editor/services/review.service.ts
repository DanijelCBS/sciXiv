import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AssingReviewersRequestDTO } from '../model/AssingReviewersRequestDTO';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private readonly baseUrl = 'http://localhost:8080/reviews';

  constructor(private http: HttpClient) { }

  public assignReviewers(assignRequest: AssingReviewersRequestDTO) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.put(`${this.baseUrl}/assignments`, assignRequest, {headers});
  }

}
