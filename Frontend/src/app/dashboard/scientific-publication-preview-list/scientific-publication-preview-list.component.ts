import { Component, OnInit } from '@angular/core';
import {SearchPublicationsDTO} from "../../shared/model/search-publications-dto-model";
import {ScientificPublicationDTO} from "../../shared/model/scientific-publication-dto.model";
import {ScientificPublicationApiService} from "../../services/scientific-publication-api.service";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-scientific-publication-preview-list',
  templateUrl: './scientific-publication-preview-list.component.html',
  styleUrls: ['./scientific-publication-preview-list.component.scss']
})
export class ScientificPublicationPreviewListComponent implements OnInit {
  private publications: ScientificPublicationDTO[];
  private searchParameters: SearchPublicationsDTO;
  private basicSearchText: string;

  constructor(private scientificPublicationApiService: ScientificPublicationApiService, private snackBar: MatSnackBar) {
    this.searchParameters = new SearchPublicationsDTO("", "", "", "",
      "", "", "", [], [], []);
  }

  ngOnInit() {
    this.advancedSearch();
  }

  advancedSearch() {
    this.scientificPublicationApiService.advancedSearch(this.searchParameters).subscribe(
      {
        next: (result: ScientificPublicationDTO[]) => {
          this.publications = result;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  basicSearch() {
    this.scientificPublicationApiService.basicSearch(this.basicSearchText).subscribe(
      {
        next: (result: ScientificPublicationDTO[]) => {
          this.publications = result;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  resetForm(form1, form2) {
    form1.reset();
    form2.reset();
    this.searchParameters = new SearchPublicationsDTO("", "", "", "",
      "", "", "", [], [], []);
    this.advancedSearch();
  }

}
