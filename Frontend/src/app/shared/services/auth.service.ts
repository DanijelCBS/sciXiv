import { JwtService } from './jwt.service';
import { LoginRequestDTO } from './../model/LoginRequestDTO';
import { Injectable } from '@angular/core';
import { UserRole } from '../model/UserRole';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl = 'http://localhost:8080/auth';
  private currentUserRoleSubject = new BehaviorSubject<UserRole>({} as UserRole);
  public currentUserRole = this.currentUserRoleSubject.asObservable().pipe(distinctUntilChanged());

  constructor(private http: HttpClient, private jwtService: JwtService) { }

  populate() {
    // If JWT detected, attempt to get & store user's info
    const jwt = this.jwtService.getToken();
    if (jwt) {
      this.setAuth(jwt);
    } else {
      // Remove any potential remnants of previous auth states
      this.purgeAuth();
    }
  }

  setAuth(token: string) {
    // Save JWT sent from server in local storage
    this.jwtService.saveToken(token);
    // Set current user role into observable
    const decodedToken = this.jwtService.decodeToken(token);
    switch (decodedToken.role) {
      case 'ROLE_AUTHOR': this.currentUserRoleSubject.next(UserRole.AUTHOR); break;
      case 'ROLE_REVIEWER': this.currentUserRoleSubject.next(UserRole.REVIEWER); break;
      case 'ROLE_EDITOR': this.currentUserRoleSubject.next(UserRole.EDITOR); break;
      default: throw new Error('Unknown role');
    }
    // Set isAuthenticated to true
    // this.isAuthenticatedSubject.next(true);
  }

  purgeAuth() {
    // Remove JWT from local storage
    this.jwtService.destroyToken();
    // Set current user to an empty object
    this.currentUserRoleSubject.next(UserRole.UNREGISTERED as UserRole);
  }

  getAuthToken() {
    return this.jwtService.getToken();
  }

  attemptAuth(loginRequest: LoginRequestDTO): Observable<any> {
    return this.http.post(this.baseUrl , loginRequest);
  }

}
