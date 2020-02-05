import { Component, OnInit, Input } from '@angular/core';
import { PublicationProcessDTO } from '../model/PublicationProcessDTO';
import { Router } from '@angular/router';

@Component({
  selector: 'app-process-preview',
  templateUrl: './process-preview.component.html',
  styleUrls: ['./process-preview.component.scss']
})
export class ProcessPreviewComponent implements OnInit {

  @Input() process: PublicationProcessDTO;

  constructor(private router: Router) { }

  ngOnInit() {
  }

  private isManageable(): boolean {
    return this.process.processState === 'SUBMITTED'
    || this.process.processState === 'REVISED'
    || this.process.processState === 'ON_REVIEW';
  }

  private onManage() {
    const queryParams = {
      publicationTitle: this.process.publicationTitle,
    };
    this.router.navigate(['/manageProcess'], { queryParams });
  }

}
