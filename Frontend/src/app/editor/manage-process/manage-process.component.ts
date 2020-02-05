import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { PublicationProcessDTO } from '../model/PublicationProcessDTO';
import { PublicationProcessService } from '../services/publication-process.service';
import { ReviewService } from '../services/review.service';

@Component({
  selector: 'app-manage-process',
  templateUrl: './manage-process.component.html',
  styleUrls: ['./manage-process.component.scss']
})
export class ManageProcessComponent implements OnInit {
  private process: PublicationProcessDTO;
  private publicationTitle = '';
  private loading = true;

  constructor(
    private processService: PublicationProcessService,
    private reviewService: ReviewService,
    private activatedRoute: ActivatedRoute
    ) { }

  ngOnInit() {
    this.loading = true;
    this.activatedRoute.queryParamMap.subscribe(
      (queryParams: ParamMap) => {
        this.readQueryParams(queryParams);
        this.loadProcessInfo();
      }
    );
  }

  private readQueryParams(queryParams: ParamMap) {
    if (queryParams.has('publicationTitle')) {
      this.publicationTitle = queryParams.get('publicationTitle');
    }
  }

  private loadProcessInfo() {
    this.processService.getProcess(this.publicationTitle).subscribe(
      (result: PublicationProcessDTO) => {
        this.process = result;
        this.loading = false;
      }
    );
  }

}
