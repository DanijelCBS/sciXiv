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
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.authService.currentUserRole.subscribe(
      (role: UserRole) => {
        this.currentUserRole = role;
      }
    );
  }

  private onLogout() {
    this.authService.purgeAuth();
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
