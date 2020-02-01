import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {DashboardComponent} from './dashboard.component';
import {
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatGridListModule,
  MatIconModule, MatInputModule, MatSlideToggleModule,
  MatSnackBarModule, MatToolbarModule,
  MatTooltipModule,
  MatPaginatorModule, MatSelectModule,
  MatDatepickerModule, MatNativeDateModule,
  MatFormFieldModule, MAT_DATE_FORMATS, MAT_DATE_LOCALE,
  DateAdapter
} from '@angular/material';
import {FlexModule} from '@angular/flex-layout';
import {RouterModule} from '@angular/router';
import {ToolbarModule} from '../toolbar/toolbar.module';


@NgModule({
  declarations: [DashboardComponent],
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatGridListModule,
    MatTooltipModule,
    MatButtonToggleModule,
    MatSlideToggleModule,
    MatToolbarModule,
    MatInputModule,
    FlexModule,
    MatPaginatorModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatFormFieldModule,
    RouterModule,
    ToolbarModule
  ],
  providers: [
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'DD.MM.YYYY.',
        },
        display: {
          dateInput: 'DD.MM.YYYY.',
          monthYearLabel: 'YYYY',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'YYYY',
        },
      },
    },
  ],
})
export class DashboardModule {
}
