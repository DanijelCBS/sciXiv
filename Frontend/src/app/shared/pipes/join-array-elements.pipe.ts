import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'joinArrayElements'
})
export class JoinArrayElementsPipe implements PipeTransform {

  transform(value: string[], ...args: any[]): any {
    return value.join(', ');
  }

}
