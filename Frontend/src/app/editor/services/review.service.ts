import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  private readonly baseUrl = 'http://localhost:8080/reviews';

  constructor(private http: HttpClient) { }

}
