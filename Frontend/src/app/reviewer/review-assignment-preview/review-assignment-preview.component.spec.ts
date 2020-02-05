import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewAssignmentPreviewComponent } from './review-assignment-preview.component';

describe('ReviewAssignmentPreviewComponent', () => {
  let component: ReviewAssignmentPreviewComponent;
  let fixture: ComponentFixture<ReviewAssignmentPreviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewAssignmentPreviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewAssignmentPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
