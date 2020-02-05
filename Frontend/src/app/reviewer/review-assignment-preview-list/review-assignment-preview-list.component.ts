import { Component, OnInit, Input } from '@angular/core';
import { ReviewAssignmentDTO } from '../model/ReviewAssignmentDTO';

@Component({
  selector: 'app-review-assignment-preview-list',
  templateUrl: './review-assignment-preview-list.component.html',
  styleUrls: ['./review-assignment-preview-list.component.scss']
})
export class ReviewAssignmentPreviewListComponent implements OnInit {

  @Input() assignments: Array<ReviewAssignmentDTO>;

  constructor() { }

  ngOnInit() {
  }

}
