import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthorRoutingModule } from './author-routing.module';
import { AddScientificPublicationComponent } from './add-scientific-publication/add-scientific-publication.component';
import {ToolbarModule} from "../toolbar/toolbar.module";
import {FlexModule} from "@angular/flex-layout";
import {
  MatButtonModule, MatInputModule,
  MatSnackBarModule,
  MatTooltipModule,
  MatFormFieldModule
} from '@angular/material';


@NgModule({
  declarations: [AddScientificPublicationComponent],
  imports: [
    CommonModule,
    AuthorRoutingModule,
    ToolbarModule,
    MatButtonModule,
    MatTooltipModule,
    FlexModule,
    MatInputModule,
    MatSnackBarModule,
    MatFormFieldModule
  ]
})
export class AuthorModule { }
