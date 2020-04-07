import React from 'react';
import { Form } from '@patternfly/react-core';

import { BaseForm, context } from './uniforms';

const Patternfly = (parent: any): any => {
  class _ extends parent {
    static Patternfly = Patternfly;

    static displayName = `Patternfly${parent.displayName}`;

    render() {
      return (
        <context.Provider value={this.getContext()}>
          <Form {...this.getNativeFormProps()} />
        </context.Provider>
      );
    }
    
  }
  return _;
};

export default Patternfly(BaseForm);