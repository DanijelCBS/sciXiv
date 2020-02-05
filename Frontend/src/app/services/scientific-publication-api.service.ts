import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SearchPublicationsDTO} from "../shared/model/search-publications-dto-model";

@Injectable({
  providedIn: 'root'
})
export class ScientificPublicationApiService {
  private readonly _baseUrl: string;

  constructor(private _http: HttpClient) {
    this._baseUrl = "http://localhost:8080/scientificPublication"
  }

  basicSearch(parameter: string) {
    return this._http.get(`${this._baseUrl}/basicSearch?parameter=${parameter}`);
  }

  advancedSearch(searchParameters: SearchPublicationsDTO) {
    return this._http.get(`${this._baseUrl}/advancedSearch?title=${searchParameters.title}&dateReceived=${searchParameters.dateReceived}&dateRevised=${searchParameters.dateRevised}&dateAccepted=${searchParameters.dateAccepted}&authorName=${searchParameters.authorName}&authorAffiliation=${searchParameters.authorAffiliation}&keyword=${searchParameters.keyword}&authors&authorsAffiliations&keywords`);
  }

  getReferences(title: string) {
    return this._http.get(`${this._baseUrl}/references?title=${title}`);
  }

  getMetadata(title: string) {
    return this._http.get(`${this._baseUrl}/metadata?title=${title}`);
  }

  submitPublication(scientificPublication: string) {
    return this._http.post(`${this._baseUrl}`, scientificPublication);
  }

  getScientificPublication(title: string, version: number) {
    return this._http.get(`${this._baseUrl}?name=${title}&?version=${version}`)
  }

  getNumberOfVersions(title: string) {
    return this._http.get(`${this._baseUrl}/version?title=${title}`);
  }

  submitRevision(scientificPublication: string) {
    return this._http.put(`${this._baseUrl}/revise`, scientificPublication);
  }
}
