import {Component, Input, OnInit} from '@angular/core';
import {ScientificPublicationDTO} from "../../../shared/model/scientific-publication-dto.model";
import {SearchPublicationsDTO} from "../../../shared/model/search-publications-dto-model";
import {ScientificPublicationApiService} from "../../../services/scientific-publication-api.service";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-scientific-publication-preview',
  templateUrl: './scientific-publication-preview.component.html',
  styleUrls: ['./scientific-publication-preview.component.scss']
})
export class ScientificPublicationPreviewComponent implements OnInit {
  @Input() publication: ScientificPublicationDTO;
  private metadata: SearchPublicationsDTO;
  private references: string[];
  private detailsShown = false;

  constructor(private scientificPublicationApiService: ScientificPublicationApiService, private snackBar: MatSnackBar) { }

  ngOnInit() {
  }

  getMetadata() {
    this.scientificPublicationApiService.getMetadata(this.publication.title).subscribe(
      {
        next: (result: SearchPublicationsDTO) => {
          this.metadata = result;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  getReferences() {
    this.scientificPublicationApiService.getReferences(this.publication.title).subscribe(
      {
        next: (result: ScientificPublicationDTO[]) => {
          this.references = result.map(sp => sp.title);
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  getDetails() {
    this.detailsShown = true;
    this.getMetadata();
    this.getReferences();
  }

}
