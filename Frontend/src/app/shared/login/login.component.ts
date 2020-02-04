import { LoginResponseDTO } from './../model/LoginResponseDTO';
import { LoginRequestDTO } from './../model/LoginRequestDTO';
import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'shr-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  private loginInfo: LoginRequestDTO = { email: '', password: '' };

  constructor(
    private snackBar: MatSnackBar,
    private authService: AuthService,
    private reouter: Router
    ) { }

  ngOnInit() {
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 2000,
    });
  }

  isEmpty(str: string): boolean {
    return (!str || str.trim() === '');
  }

  validate(): boolean {
    if (this.isEmpty(this.loginInfo.email)) {
      this.openSnackBar('Email must be entered');
      return false;
    }
    if (this.isEmpty(this.loginInfo.password)) {
      this.openSnackBar('Password must be entered');
      return false;
    }
    return true;
  }

  login() {
    if (!this.validate()) {
      return;
    }

    this.authService.attemptAuth(this.loginInfo).subscribe(
      (response: LoginResponseDTO) => {
        this.authService.setAuth(response.jsonWebToken);
        this.reouter.navigate(['/publications']);
      },

      (error: any) => this.openSnackBar('Login failed; Invalid user ID or password')
    );


  }

}
