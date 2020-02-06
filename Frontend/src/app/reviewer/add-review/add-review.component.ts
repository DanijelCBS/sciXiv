import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { ReviewService } from '../services/review.service';

@Component({
  selector: 'app-add-review',
  templateUrl: './add-review.component.html',
  styleUrls: ['./add-review.component.scss']
})
export class AddReviewComponent implements OnInit {

  private publicationTitile = '';
  private publicationVersion = '1';
  private reviewXml = '';
  private processing = false;
  @ViewChild('content', { static: true }) previewDiv: ElementRef;

  constructor(
    private activatedRoute: ActivatedRoute,
    private reouter: Router,
    private reviewService: ReviewService
    ) { }

  ngOnInit() {
    this.activatedRoute.queryParamMap.subscribe(
      (queryParams: ParamMap) => {
        this.readQueryParams(queryParams);
      }
    );
  }

  private readQueryParams(queryParams: ParamMap) {
    if (queryParams.has('publicationTitle')) {
      this.publicationTitile = queryParams.get('publicationTitle');
    }
    if (queryParams.has('publicationVersion')) {
      this.publicationVersion = queryParams.get('publicationVersion');
    }
  }

  private readXMLFile(event, publication) {
    let input = event.target;
    let fileReader = new FileReader();
    fileReader.onload = () => {
      let fileContent = fileReader.result;
      this.reviewXml = fileContent as string;
      console.log(this.reviewXml);
      this.previewDiv.nativeElement.innerHTML = this.reviewXml;
    };
    fileReader.readAsText(input.files[0]);
  }

  private onSubmit() {
    if (!this.reviewXml) {
      alert('Please choose XML file containing the review.');
      return;
    }
    this.processing = true;
    this.reviewService.submitReview(this.reviewXml).subscribe(
      (success) => {
        this.processing = false;
        this.reouter.navigate(['/reviewAssignments']);
      }
    );

  }

}
