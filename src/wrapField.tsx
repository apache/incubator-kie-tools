import React from 'react';
import { FormGroup, FormGroupProps } from '@patternfly/react-core';
import { filterDOMProps } from 'uniforms/es5';

filterDOMProps.register('decimal', 'minCount', 'autoValue', 'isDisabled');

type WrapperProps = {
  id: string;
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
    showInlineError,
    help,
    required,
    ...props
  }: WrapperProps,
  children
) {
  return (
    <FormGroup
      fieldId={id}
      label={label}
      isValid={!error}
      type={type}
      helperText={help}
      helperTextInvalid={errorMessage}
      {...filterDOMProps(props)}
    >
      {children}
    </FormGroup>
  );
}
