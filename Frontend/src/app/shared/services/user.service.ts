import { UserRole } from './../model/UserRole';
import { UserRegostrationDTO } from './../model/UserRegistrationDTO';
import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly baseUrl = 'http://localhost:8080/users';

  constructor(private http: HttpClient) { }

  registerAuthor(newAuthor: UserRegostrationDTO) {
    return this.http.post(`${this.baseUrl}/authors`, newAuthor);
  }

  registerReviewer(newReviewer: UserRegostrationDTO) {
    return this.http.post(`${this.baseUrl}/reviewers`, newReviewer);
  }
}
