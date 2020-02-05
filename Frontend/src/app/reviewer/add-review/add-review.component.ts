import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-add-review',
  templateUrl: './add-review.component.html',
  styleUrls: ['./add-review.component.scss']
})
export class AddReviewComponent implements OnInit {

  private publicationTitile = '';
  private publicationVersion = '1';

  constructor(private activatedRoute: ActivatedRoute) { }

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

}
