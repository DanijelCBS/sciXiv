import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedRoutingModule } from './shared-routing.module';
import { JoinArrayElementsPipe } from './pipes/join-array-elements.pipe';


@NgModule({
  declarations: [JoinArrayElementsPipe],
  exports: [
    JoinArrayElementsPipe
  ],
  imports: [
    CommonModule,
    SharedRoutingModule
  ]
})
export class SharedModule { }
