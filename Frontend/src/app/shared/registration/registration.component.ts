import { UserRegostrationDTO } from './../model/UserRegistrationDTO';
import { SharedModule } from './../shared.module';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import {Router, ActivatedRoute, ParamMap} from '@angular/router';

import {checkPasswords} from './validators/checkPasswords';
import { UserService } from './../services/user.service';

@Component({
  selector: 'shr-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  userForm: FormGroup;
  private reviewer = false;
  private headerMessage = 'Create an account';
  private buttonMessage = 'Sign up';

  constructor(
    private snackBar: MatSnackBar,
    private userService: UserService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private fb: FormBuilder, ) {
    this.userForm = this.fb.group({
      name: new FormControl('', [Validators.required]),
      surname: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required]),
      retypePassword: new FormControl('', [Validators.required]),
      },
      {
        validators: checkPasswords
      });
    }

  ngOnInit() {
    this.activatedRoute.queryParamMap.subscribe(
      (queryParams: ParamMap) => {
        this.readQueryParams(queryParams);
      }
    );
  }

  readQueryParams(queryParams) {
    if (queryParams.has('reviewer')) {
      this.reviewer = true;
      this.headerMessage = 'Register reviewer';
      this.buttonMessage = 'Add reviewer';
    }
  }

  hasError = (controlName: string, errorName: string) => {
    return this.userForm.controls[controlName].hasError(errorName);
  }

  openSnackBar(message: string) {
    this.snackBar.open(message, 'Close', {
      duration: 2000,
    });
  }

  signUp() {
    if (this.userForm.invalid) {
      return;
    }
    const nweUser: UserRegostrationDTO = {
      firstName: this.userForm.get('name').value,
      lastName: this.userForm.get('surname').value,
      email: this.userForm.get('email').value,
      password: this.userForm.get('password').value,
    };

    if (this.reviewer) {
      this.userService.registerReviewer(nweUser).subscribe(
        () => {
          // registration successfull
          this.openSnackBar('Registration successfull.');
        },
        () => {this.openSnackBar('Failed to create account. Email already taken'); }
      );
    } else {
      this.userService.registerAuthor(nweUser).subscribe(
        () => {
          // registration successfull
          this.openSnackBar('Registration successfull.');
          this.router.navigate(['/login']);
        },
        () => {this.openSnackBar('Failed to create account. Email already taken'); }
      );
    }
  }

}
