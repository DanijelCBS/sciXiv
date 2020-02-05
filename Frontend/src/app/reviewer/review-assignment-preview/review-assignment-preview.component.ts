import { Component, OnInit, Input } from '@angular/core';
import { ReviewAssignmentDTO } from '../model/ReviewAssignmentDTO';
import { ReviewService } from '../services/review.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-review-assignment-preview',
  templateUrl: './review-assignment-preview.component.html',
  styleUrls: ['./review-assignment-preview.component.scss']
})
export class ReviewAssignmentPreviewComponent implements OnInit {

  @Input() assignment: ReviewAssignmentDTO;
  private processing = false;

  constructor(
    private reviewService: ReviewService,
    private router: Router
    ) { }

  ngOnInit() {
  }

  private accept() {
    this.processing = true;
    this.reviewService.acceptReviewAssignment(this.assignment.publicationTitle).subscribe(
      (success: any) => {
        this.assignment.reviewStatus = 'ACCEPTED';
        this.processing = false;
      }
    );
  }

  private reject() {
    this.processing = true;
    this.reviewService.rejectReviewAssignment(this.assignment.publicationTitle).subscribe(
      (success: any) => {
        this.assignment.reviewStatus = 'REJECTED';
        this.processing = false;
        //  refresh assignments page
        window.location.reload();
      }
    );
  }

  private submit() {
    const queryParams = {
      publicationTitle: this.assignment.publicationTitle,
      publicationVersion: this.assignment.publicationVersion
    };
    this.router.navigate(['/addReview'], { queryParams });
  }

}
