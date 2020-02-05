import {AfterViewInit, Component, OnInit} from '@angular/core';
import {ScientificPublicationApiService} from "../../services/scientific-publication-api.service";
import {XonomyApiService} from "../../services/xonomy-api.service";
import {MatSnackBar} from "@angular/material";
import {CoverLetterApiService} from "../../services/cover-letter-api.service";
import {ActivatedRoute, Router} from "@angular/router";
declare const Xonomy: any;

@Component({
  selector: 'app-add-scientific-publication',
  templateUrl: './add-scientific-publication.component.html',
  styleUrls: ['./add-scientific-publication.component.scss']
})
export class AddScientificPublicationComponent implements AfterViewInit {
  private scientificPublication = null;
  private coverLetter = null;
  private title;
  private version;
  private tabIndex = 0;
  private cvDisabled = true;
  private spDisabled = false;
  private addMode = true;
  private versions = [];

  constructor(private scientificPublicationApiService: ScientificPublicationApiService,
              private coverLetterApiService: CoverLetterApiService, private xonomyApiService: XonomyApiService,
              private snackBar: MatSnackBar, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params.title) {
        this.addMode = false;
        this.title = params.title;
        this.version = -1;
        this.cvDisabled = false;
        this.getNumberOfVersions();
        this.getScientificPublication();
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.addMode) {
      this.scientificPublication = "<sp:scientificPublication xmlns=\"http://www.w3.org/ns/rdfa#\" xmlns:pred=\"http://schema.org/\" xmlns:sp=\"http://ftn.uns.ac.rs/scientificPublication\"></sp:scientificPublication>";
      let xonomyElement = document.getElementById("xonomy");
      Xonomy.render(this.scientificPublication, xonomyElement, this.xonomyApiService.scientificPublicationSpecification);
    }
  }

  getScientificPublication() {
    this.scientificPublicationApiService.getScientificPublication(this.title, this.version).subscribe(
      {
        next: (result) => {
          this.scientificPublication = result;
          let xonomyElement = document.getElementById("xonomy");
          Xonomy.render(this.scientificPublication, xonomyElement, this.xonomyApiService.scientificPublicationSpecification);
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  getCoverLetter() {
    this.coverLetterApiService.getCoverLetter(this.title, this.version).subscribe(
      {
        next: (result) => {
          this.coverLetter = result;
          let xonomyElement = document.getElementById("xonomyCV");
          Xonomy.render(this.coverLetter, xonomyElement, this.xonomyApiService.coverLetterSpecification);
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  getNumberOfVersions() {
    this.scientificPublicationApiService.getNumberOfVersions(this.title).subscribe(
      {
        next: (result) => {
          this.versions = Array(result).fill(1).map((x, i) => i + 1);
          this.version = result;
        },
        error: (message: string) => {
          this.snackBar.open(message, 'Dismiss', {
            duration: 3000
          });
        }
      }
    );
  }

  changeToCoverLetter() {
    let xonomy = document.getElementById("xonomy");
    this.scientificPublication = Xonomy.harvest();
    if (this.addMode) {
      this.cvDisabled = false;
      this.spDisabled = true;
    }
    this.tabIndex = 1;
    xonomy.innerHTML = "";
  }

  tabChanged() {
    if (this.addMode) {
      this.coverLetter = "<cl:coverLetter xmlns:cl=\"http://ftn.uns.ac.rs/coverLetter\"></cl:coverLetter>";
      let xonomyElement = document.getElementById("xonomyCV");
      Xonomy.render(this.coverLetter, xonomyElement, this.xonomyApiService.coverLetterSpecification);
    }
    else {
      if (this.tabIndex == 0) {
        this.getScientificPublication();
      }
      else {
        if (this.coverLetter === null) {
          this.getCoverLetter();
        }
      }
    }
  }

  changeVersion() {
    if (this.tabIndex == 0) {
      this.getScientificPublication();
    }
    else {
      this.getCoverLetter();
    }
  }

  submitPublication() {
    this.scientificPublicationApiService.submitPublication(this.scientificPublication).subscribe(
      {
        next: (result) => {
          this.snackBar.open("Publication successfully submitted", 'Dismiss', {
            duration: 3000
          });
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

  submitRevision() {
    this.scientificPublicationApiService.submitRevision(this.scientificPublication).subscribe(
      {
        next: (result) => {
          this.snackBar.open("Publication successfully revised", 'Dismiss', {
            duration: 3000
          });
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

