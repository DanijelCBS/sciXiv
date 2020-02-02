import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToolbarModule } from './toolbar/toolbar.module';
import { DashboardModule } from './dashboard/dashboard.module';
import {HttpClientModule} from '@angular/common/http';
import {SharedModule} from "./shared/shared.module";
import {AuthorModule} from "./author/author.module";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ToolbarModule,
    DashboardModule,
    HttpClientModule,
    SharedModule,
    AuthorModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
