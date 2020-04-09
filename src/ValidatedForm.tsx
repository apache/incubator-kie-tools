import { ValidatedForm } from 'uniforms';

import BaseForm from './BaseForm';

function Validated(parent: any): any {
  class _ extends ValidatedForm.Validated(parent) {
    static Validated = Validated;
  }

  return new _();
}

export default Validated(BaseForm);
