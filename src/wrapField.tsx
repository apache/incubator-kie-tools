import React from 'react';
// import omit from 'lodash/omit';
import { FormGroup, FormGroupProps } from '@patternfly/react-core';
import { any } from 'prop-types';
// import { filterDOMProps } from 'uniforms';

// const _filterDOMPropsList = ['fullWidth', 'helperText', 'margin', 'variant'];
// const _filterDOMProps = props => omit(props, _filterDOMPropsList);

type WrapperProps = {
  error?: boolean;
  errorMessage?: string;
  help?: string;
  showInlineError?: boolean;
} & Omit<FormGroupProps, 'onChange' | 'fieldId'>;

export default function wrapField(
  {
    id,
    label,
    type,
    disabled,
    error,
    errorMessage,
    help,
    required,
    ...props
  }: WrapperProps,
  ...children
) {
  return (
    <FormGroup
      fieldId={ id }
      label={ label }
      isValid={ error }
      type={ type }
      helperText={ help }
      helperTextInvalid={ errorMessage }
    >
      { children }
    </FormGroup>
  );
}

// filterDOMProps.register();