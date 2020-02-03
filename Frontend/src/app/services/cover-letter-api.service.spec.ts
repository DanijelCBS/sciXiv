import { TestBed } from '@angular/core/testing';

import { CoverLetterApiService } from './cover-letter-api.service';

describe('CoverLetterApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CoverLetterApiService = TestBed.get(CoverLetterApiService);
    expect(service).toBeTruthy();
  });
});
