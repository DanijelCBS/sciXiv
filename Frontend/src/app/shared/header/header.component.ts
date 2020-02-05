import { Router } from '@angular/router';
import { UserRole } from './../model/UserRole';
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'shr-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  private currentUserRole = UserRole.UNREGISTERED;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
    this.authService.currentUserRole.subscribe(
      (role: UserRole) => {
        this.currentUserRole = role;
      }
    );
    this.authService.populate();
  }

  private onLogout() {
    this.authService.purgeAuth();
    this.router.navigate(['/publications']);
  }

  private hasAuthorPermissions(): boolean {
    return this.currentUserRole === UserRole.AUTHOR
    || this.currentUserRole === UserRole.REVIEWER
    || this.currentUserRole === UserRole.EDITOR;
  }

  private hasReviewerPermissions(): boolean {
    return this.currentUserRole === UserRole.REVIEWER
    || this.currentUserRole === UserRole.EDITOR;
  }

  private hasEditorPermissions(): boolean {
    return this.currentUserRole === UserRole.EDITOR;
  }

}
