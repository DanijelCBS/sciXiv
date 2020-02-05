import { PublicationProcessService } from './../services/publication-process.service';
import { PublicationProcessDTO } from './../model/PublicationProcessDTO';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-processes-list-preview',
  templateUrl: './processes-list-preview.component.html',
  styleUrls: ['./processes-list-preview.component.scss']
})
export class ProcessesListPreviewComponent implements OnInit {
  private processes: Array<PublicationProcessDTO> = [];

  constructor(private processService: PublicationProcessService) { }

  ngOnInit() {
    this.processService.getProcesses().subscribe(
      (result: Array<PublicationProcessDTO>) => {
        this.processes = result;
      }
    );
  }

}
