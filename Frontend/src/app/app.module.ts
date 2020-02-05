import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToolbarModule } from './toolbar/toolbar.module';
import { DashboardModule } from './dashboard/dashboard.module';
import {HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import {SharedModule} from "./shared/shared.module";
import {AuthorModule} from "./author/author.module";
import { HttpErrorInterceptor } from './interceptors/HttpErrorInterceptor';
import { ReviewerModule } from './reviewer/reviewer.module';
import { EditorModule } from './editor/editor.module';
import { TokenInterceptor } from './interceptors/TokenInterceptor';

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
    AuthorModule,
    ReviewerModule,
    EditorModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
