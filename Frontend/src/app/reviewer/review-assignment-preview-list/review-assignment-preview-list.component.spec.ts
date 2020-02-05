import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewAssignmentPreviewListComponent } from './review-assignment-preview-list.component';

describe('ReviewAssignmentPreviewListComponent', () => {
  let component: ReviewAssignmentPreviewListComponent;
  let fixture: ComponentFixture<ReviewAssignmentPreviewListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReviewAssignmentPreviewListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReviewAssignmentPreviewListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
