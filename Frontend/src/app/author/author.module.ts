import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthorRoutingModule } from './author-routing.module';
import { AddScientificPublicationComponent } from './add-scientific-publication/add-scientific-publication.component';
import {ToolbarModule} from "../toolbar/toolbar.module";
import {MatButtonModule, MatTooltipModule} from "@angular/material";
import {FlexModule} from "@angular/flex-layout";


@NgModule({
  declarations: [AddScientificPublicationComponent],
  imports: [
    CommonModule,
    AuthorRoutingModule,
    ToolbarModule,
    MatButtonModule,
    MatTooltipModule,
    FlexModule
  ]
})
export class AuthorModule { }
