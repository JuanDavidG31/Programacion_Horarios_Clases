import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Horario} from './horario/horario';

const routes: Routes = [
  { path: '**', component: Horario},];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
