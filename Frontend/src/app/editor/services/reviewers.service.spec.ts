import { TestBed } from '@angular/core/testing';

import { ReviewersService } from './reviewers.service';

describe('ReviewersService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ReviewersService = TestBed.get(ReviewersService);
    expect(service).toBeTruthy();
  });
});
