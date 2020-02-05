import { Component, OnInit } from '@angular/core';
import { ReviewService } from '../services/review.service';
import { ReviewAssignmentDTO } from '../model/ReviewAssignmentDTO';

@Component({
  selector: 'app-review-assignments',
  templateUrl: './review-assignments.component.html',
  styleUrls: ['./review-assignments.component.scss']
})
export class ReviewAssignmentsComponent implements OnInit {

  private reviewAssignments: Array<ReviewAssignmentDTO> = [];
  constructor(private reviewService: ReviewService) { }

  ngOnInit() {
    this.reviewService.getReviewAssignments().subscribe(
      (assignments: Array<ReviewAssignmentDTO>) => {
        this.reviewAssignments = assignments;
      }
    );
  }

}
