import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PublicationProcessService {

  private readonly baseUrl = 'http://localhost:8080/publicationProcesses';

  constructor(private http: HttpClient) { }

  public getProcesses() {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.get(this.baseUrl, {headers});
  }

  public getProcess(publicationTitle: string) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    return this.http.get(`${this.baseUrl}/${publicationTitle}`, {headers});
  }

  public publishPaper(publicationTitle: string) {
    return this.http.put(`${this.baseUrl}/publish?title=${publicationTitle}`, {});
  }

  public rejectPaper(publicationTitle: string) {
    return this.http.put(`${this.baseUrl}/reject?title=${publicationTitle}`, {});
  }

  public requestRevision(publicationTitle: string) {
    return this.http.put(`${this.baseUrl}/requestRevision?title=${publicationTitle}`, {});
  }
}