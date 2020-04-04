import { BaseForm } from 'uniforms';

const Patternfly = (parent: any): any => {
  class _ extends parent {
    static Patternfly = Patternfly;

    static displayName = `Patternfly${parent.displayName}`;
  }

  return _;
};

export default Patternfly(BaseForm);