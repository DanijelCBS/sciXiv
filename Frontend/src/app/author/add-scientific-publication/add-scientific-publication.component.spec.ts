import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddScientificPublicationComponent } from './add-scientific-publication.component';

describe('AddScientificPublicationComponent', () => {
  let component: AddScientificPublicationComponent;
  let fixture: ComponentFixture<AddScientificPublicationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddScientificPublicationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddScientificPublicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
