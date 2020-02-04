import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AddScientificPublicationComponent} from "./author/add-scientific-publication/add-scientific-publication.component";
import { RegistrationComponent } from 'src/app/shared/registration/registration.component';
import { LoginComponent } from './shared/login/login.component';


const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'publications',
    component: DashboardComponent
  },
  {
    path: 'addPublication',
    component: AddScientificPublicationComponent
  },
  {
    path: 'register',
    component: RegistrationComponent
  },
  {
    path: 'login',
    component: LoginComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
