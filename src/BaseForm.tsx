import { BaseForm } from 'uniforms';

const PatternFly = (parent: any): any =>
  class extends parent {
    static PatternFly = PatternFly;

    static displayName = `AntD${parent.displayName}`;
  };

export default PatternFly(BaseForm);