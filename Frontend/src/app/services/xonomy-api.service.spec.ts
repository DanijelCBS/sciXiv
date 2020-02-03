import { TestBed } from '@angular/core/testing';

import { XonomyApiService } from './xonomy-api.service';

describe('XonomyApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: XonomyApiService = TestBed.get(XonomyApiService);
    expect(service).toBeTruthy();
  });
});
