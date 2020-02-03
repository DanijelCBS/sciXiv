import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class CoverLetterApiService {
  private readonly _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = "http://localhost:8080/coverLetter"
  }

  submitCoverLetter(coverLetter: string) {
    return this._http.post(`${this._baseUrl}`, coverLetter);
  }
}
