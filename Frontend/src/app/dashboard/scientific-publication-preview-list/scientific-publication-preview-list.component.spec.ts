import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScientificPublicationPreviewListComponent } from './scientific-publication-preview-list.component';

describe('ScientificPublicationPreviewListComponent', () => {
  let component: ScientificPublicationPreviewListComponent;
  let fixture: ComponentFixture<ScientificPublicationPreviewListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScientificPublicationPreviewListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScientificPublicationPreviewListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
