import { Component, OnInit } from '@angular/core';
import {ScientificPublicationApiService} from "../../services/scientific-publication-api.service";
import {XonomyApiService} from "../../services/xonomy-api.service";
import {MatSnackBar} from "@angular/material";
import {ScientificPublicationDTO} from "../../shared/model/scientific-publication-dto.model";
declare const Xonomy: any;

@Component({
  selector: 'app-add-scientific-publication',
  templateUrl: './add-scientific-publication.component.html',
  styleUrls: ['./add-scientific-publication.component.scss']
})
export class AddScientificPublicationComponent implements OnInit {
  private scientificPublication;

  constructor(private scientificPublicationApiService: ScientificPublicationApiService, private xonomyApiService: XonomyApiService, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.scientificPublication = "<sp:scientificPublication xmlns=\"http://www.w3.org/ns/rdfa#\" xmlns:pred=\"http://schema.org/\" xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:scientificPublication>";
    let xonomyElement = document.getElementById("xonomy");
    Xonomy.render(this.scientificPublication, xonomyElement, this.xonomyApiService.scientificPublicationSpecification);
  }

  submitPublication() {
    this.scientificPublication = Xonomy.harvest();
    this.scientificPublicationApiService.submitPublication(this.scientificPublication).subscribe(
      {
        next: (result: ScientificPublicationDTO[]) => {
          this.snackBar.open("Publication successfully submitted", 'Dismiss', {
            duration: 3000
          });
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  readXMLFile(event) {
    let input = event.target;
    let fileReader = new FileReader();
    fileReader.onload = () => {
        let fileContent = fileReader.result;
        let xonomyElement = document.getElementById("xonomy");
        this.scientificPublication = fileContent;
        Xonomy.render(this.scientificPublication, xonomyElement, this.xonomyApiService.scientificPublicationSpecification);
    };
    fileReader.readAsText(input.files[0]);
  }
}

