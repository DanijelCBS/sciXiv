import { TestBed } from '@angular/core/testing';

import { PublicationProcessService } from './publication-process.service';

describe('PublicationProcessService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PublicationProcessService = TestBed.get(PublicationProcessService);
    expect(service).toBeTruthy();
  });
});
