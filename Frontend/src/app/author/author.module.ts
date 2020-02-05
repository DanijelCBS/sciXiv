import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthorRoutingModule } from './author-routing.module';
import { AddScientificPublicationComponent } from './add-scientific-publication/add-scientific-publication.component';
import {ToolbarModule} from "../toolbar/toolbar.module";
import {FlexModule} from "@angular/flex-layout";
import {MatTabsModule} from '@angular/material/tabs';
import {
  MatButtonModule, MatInputModule,
  MatSnackBarModule,
  MatTooltipModule,
  MatFormFieldModule, MatSelectModule
} from '@angular/material';
import {FormsModule} from "@angular/forms";


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
    MatFormFieldModule,
    MatTabsModule,
    MatSelectModule,
    FormsModule
  ]
})
export class AuthorModule { }
