import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScientificPublicationPreviewComponent } from './scientific-publication-preview.component';

describe('ScientificPublicationPreviewComponent', () => {
  let component: ScientificPublicationPreviewComponent;
  let fixture: ComponentFixture<ScientificPublicationPreviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScientificPublicationPreviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScientificPublicationPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
