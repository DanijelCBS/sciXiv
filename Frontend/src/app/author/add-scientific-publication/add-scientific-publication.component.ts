import {AfterViewInit, Component, OnInit} from '@angular/core';
import {ScientificPublicationApiService} from "../../services/scientific-publication-api.service";
import {XonomyApiService} from "../../services/xonomy-api.service";
import {MatSnackBar} from "@angular/material";
import {ScientificPublicationDTO} from "../../shared/model/scientific-publication-dto.model";
import {CoverLetterApiService} from "../../services/cover-letter-api.service";
import {Router} from "@angular/router";
declare const Xonomy: any;

@Component({
  selector: 'app-add-scientific-publication',
  templateUrl: './add-scientific-publication.component.html',
  styleUrls: ['./add-scientific-publication.component.scss']
})
export class AddScientificPublicationComponent implements AfterViewInit {
  private scientificPublication;
  private coverLetter;
  private tabIndex = 0;
  private cvDisabled = true;
  private spDisabled = false;

  constructor(private scientificPublicationApiService: ScientificPublicationApiService,
              private coverLetterApiService: CoverLetterApiService, private xonomyApiService: XonomyApiService,
              private snackBar: MatSnackBar, private router: Router) {}

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    this.scientificPublication = "<sp:scientificPublication xmlns=\"http://www.w3.org/ns/rdfa#\" xmlns:pred=\"http://schema.org/\" xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:scientificPublication>";
    let xonomyElement = document.getElementById("xonomy");
    Xonomy.render(this.scientificPublication, xonomyElement, this.xonomyApiService.scientificPublicationSpecification);
  }

  changeToCoverLetter() {
    let xonomy = document.getElementById("xonomy");
    this.scientificPublication = Xonomy.harvest();
    this.cvDisabled = false;
    this.spDisabled = true;
    this.tabIndex = 1;
    xonomy.innerHTML = "";
  }

  tabChanged() {
    this.coverLetter = "<cl:coverLetter xmlns:cl=\"http://ftn.uns.ac.rs/coverLetter\"></cl:coverLetter>";
    let xonomyElement = document.getElementById("xonomyCV");
    Xonomy.render(this.coverLetter, xonomyElement, this.xonomyApiService.coverLetterSpecification);
  }

  submit() {
    this.submitPublication();
    this.submitCoverLetter();
  }

  submitPublication() {
    this.scientificPublicationApiService.submitPublication(this.scientificPublication).subscribe(
      {
        next: (result) => {
          this.snackBar.open("Publication successfully submitted", 'Dismiss', {
            duration: 3000
          });
          console.log("submitovano pub");
          this.submitCoverLetter();
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  submitCoverLetter() {
    this.coverLetter = Xonomy.harvest();
    this.coverLetterApiService.submitCoverLetter(this.coverLetter).subscribe(
      {
        next: (result) => {
          this.snackBar.open("Cover letter successfully submitted", 'Dismiss', {
            duration: 3000
          });
          this.router.navigate(['/dashboard']).then(r => {});
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  readXMLFile(event, publication) {
    let input = event.target;
    let fileReader = new FileReader();
    if (publication) {
      fileReader.onload = () => {
        let fileContent = fileReader.result;
        let xonomyElement = document.getElementById("xonomy");
        this.scientificPublication = fileContent;
        Xonomy.render(this.scientificPublication, xonomyElement, this.xonomyApiService.scientificPublicationSpecification);
      };
      fileReader.readAsText(input.files[0]);
    }
    else {
      fileReader.onload = () => {
        let fileContent = fileReader.result;
        let xonomyElement = document.getElementById("xonomyCV");
        this.coverLetter = fileContent;
        Xonomy.render(this.coverLetter, xonomyElement, this.xonomyApiService.coverLetterSpecification);
      };
      fileReader.readAsText(input.files[0]);
    }
  }
}

