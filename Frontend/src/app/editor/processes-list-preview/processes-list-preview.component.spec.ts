import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessesListPreviewComponent } from './processes-list-preview.component';

describe('ProcessesListPreviewComponent', () => {
  let component: ProcessesListPreviewComponent;
  let fixture: ComponentFixture<ProcessesListPreviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcessesListPreviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessesListPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
