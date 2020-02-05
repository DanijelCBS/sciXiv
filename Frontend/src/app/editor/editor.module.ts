import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatGridListModule,
  MatIconModule, MatInputModule, MatSlideToggleModule,
  MatSnackBarModule, MatToolbarModule,
  MatTooltipModule,
  MatPaginatorModule, MatSelectModule,
  MatDatepickerModule, MatNativeDateModule,
  MatFormFieldModule
} from '@angular/material';

import { EditorRoutingModule } from './editor-routing.module';
import { ProcessesListPreviewComponent } from './processes-list-preview/processes-list-preview.component';
import { ProcessPreviewComponent } from './process-preview/process-preview.component';
import { ManageProcessComponent } from './manage-process/manage-process.component';


@NgModule({
  declarations: [ProcessesListPreviewComponent, ProcessPreviewComponent, ManageProcessComponent],
  imports: [
    CommonModule,
    EditorRoutingModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatGridListModule,
    MatIconModule, MatInputModule, MatSlideToggleModule,
    MatSnackBarModule, MatToolbarModule,
    MatTooltipModule,
    MatPaginatorModule, MatSelectModule,
    MatDatepickerModule, MatNativeDateModule,
    MatFormFieldModule
  ],
  exports: [
    ProcessesListPreviewComponent,
    ManageProcessComponent
  ]
})
export class EditorModule { }
