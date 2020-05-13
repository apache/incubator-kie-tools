import { ValidatedForm } from 'uniforms/es5';

import BaseForm from './BaseForm';

function Validated(parent: any): any {
  class _ extends ValidatedForm.Validated(parent) {
    static Validated = Validated;
  }

  return _;
}

export default Validated(BaseForm);
