import { ReviewersService } from './../services/reviewers.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { PublicationProcessDTO } from '../model/PublicationProcessDTO';
import { PublicationProcessService } from '../services/publication-process.service';
import { ReviewService } from '../services/review.service';
import { UserBaicInfoDTO } from '../model/UserBaicInfoDTO';
import { AssingReviewersRequestDTO } from '../model/AssingReviewersRequestDTO';

@Component({
  selector: 'app-manage-process',
  templateUrl: './manage-process.component.html',
  styleUrls: ['./manage-process.component.scss']
})
export class ManageProcessComponent implements OnInit {
  private process: PublicationProcessDTO;
  private publicationTitle = '';
  private loading = true;
  private processingAssignment = false;
  private processingAction = false;
  private reviewers: Array<UserBaicInfoDTO> = [];
  private selectedReviewers: Array<string> = [];

  constructor(
    private processService: PublicationProcessService,
    private reviewService: ReviewService,
    private reviewerService: ReviewersService,
    private activatedRoute: ActivatedRoute,
    private router: Router
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
        // this.loading = false;
        this.loadReviewers();
      }
    );
  }

  private loadReviewers() {
    if (!['SUBMITTED', 'REVISED', 'ON_REVIEW'].includes(this.process.processState)) {
      this.loading = false;
      return;
    }
    this.reviewerService.getPossibleReviewers(this.process.publicationTitle).subscribe(
      (possibleReviewers: Array<UserBaicInfoDTO>) => {
        this.reviewers = possibleReviewers;
        this.loading = false;
      }
    );
  }

  private onReviewerSelection(list) {
    this.selectedReviewers = list.selectedOptions.selected.map(item => item.value);
    console.log(this.selectedReviewers);
  }

  private onReviewersAssigned() {
    const assignmentRequest: AssingReviewersRequestDTO = {
      publicationTitle: this.process.publicationTitle,
      assignedReviewerEmails: this.selectedReviewers
    };
    this.processingAssignment = true;
    this.reviewService.assignReviewers(assignmentRequest).subscribe(
      (success) => {
        this.processingAssignment = false;
        window.location.reload();
      }
    );
  }

  private onPublish() {
    this.processingAction = true;
    this.processService.publishPaper(this.process.publicationTitle).subscribe(
      (success) => {
        this.processingAction = false;
        this.router.navigate(['processes']);
      }
    );
  }

  private onReject() {
    this.processingAction = true;
    this.processService.rejectPaper(this.process.publicationTitle).subscribe(
      (success) => {
        this.processingAction = false;
        this.router.navigate(['processes']);
      }
    );
  }

  private onRevisionRequested() {
    this.processingAction = true;
    this.processService.requestRevision(this.process.publicationTitle).subscribe(
      (success) => {
        this.processingAction = false;
        this.router.navigate(['processes']);
      }
    );
  }

}