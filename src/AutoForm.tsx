import { AutoForm } from 'uniforms/es5';

import ValidatedQuickForm from './ValidatedQuickForm';

function Auto(parent: any): any {
  class _ extends AutoForm.Auto(parent) {
    static Auto = Auto;
  }

  return _;
}

export default Auto(ValidatedQuickForm);
