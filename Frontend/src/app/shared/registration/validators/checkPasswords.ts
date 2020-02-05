import { AbstractControl } from '@angular/forms';

export function checkPasswords(control: AbstractControl) {
    control.get('password').setErrors(null);
    control.get('retypePassword').setErrors(null);

    const pass = control.get('password').value;
    const confirmedPass = control.get('retypePassword').value;
    if (pass !== confirmedPass) {
      control.get('retypePassword').setErrors({passwordNotMatch: true});
    }
    return null;
  }
