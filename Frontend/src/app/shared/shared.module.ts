import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatButtonModule, MatToolbarModule} from '@angular/material';
import {FlexModule} from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material';
import {MatGridListModule} from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import {RouterModule} from '@angular/router';


import { SharedRoutingModule } from './shared-routing.module';
import { JoinArrayElementsPipe } from './pipes/join-array-elements.pipe';
import { HeaderComponent } from './header/header.component';
import { RegistrationComponent } from './registration/registration.component';
import { LoginComponent } from './login/login.component';


@NgModule({
  declarations: [JoinArrayElementsPipe, HeaderComponent, RegistrationComponent, LoginComponent],
  exports: [
    JoinArrayElementsPipe,
    HeaderComponent,
    RegistrationComponent
  ],
  imports: [
    CommonModule,
    SharedRoutingModule,
    MatToolbarModule,
    MatButtonModule,
    FlexModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatGridListModule,
    MatCardModule
  ]
})
export class SharedModule { }
