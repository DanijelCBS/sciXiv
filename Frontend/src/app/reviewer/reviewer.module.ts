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
  MatFormFieldModule, MAT_DATE_FORMATS, MAT_DATE_LOCALE,
  DateAdapter
} from '@angular/material';

import { ReviewerRoutingModule } from './reviewer-routing.module';
import { ReviewAssignmentsComponent } from './review-assignments/review-assignments.component';
import { ReviewAssignmentPreviewComponent } from './review-assignment-preview/review-assignment-preview.component';
import { ReviewAssignmentPreviewListComponent } from './review-assignment-preview-list/review-assignment-preview-list.component';
import { AddReviewComponent } from './add-review/add-review.component';


@NgModule({
  declarations: [ReviewAssignmentsComponent, ReviewAssignmentPreviewComponent, ReviewAssignmentPreviewListComponent, AddReviewComponent],
  imports: [
    CommonModule,
    ReviewerRoutingModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatGridListModule,
    MatTooltipModule,
    MatPaginatorModule,
    MatSelectModule,
    MatFormFieldModule
  ],
  exports: [
    ReviewAssignmentsComponent,
    AddReviewComponent
  ]
})
export class ReviewerModule { }
