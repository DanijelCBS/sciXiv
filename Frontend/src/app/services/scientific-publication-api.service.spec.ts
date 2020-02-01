import { TestBed } from '@angular/core/testing';

import { ScientificPublicationApiService } from './scientific-publication-api.service';

describe('ScientificPublicationApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ScientificPublicationApiService = TestBed.get(ScientificPublicationApiService);
    expect(service).toBeTruthy();
  });
});
