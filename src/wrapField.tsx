import React from 'react';
import { FormGroup, FormGroupProps } from '@patternfly/react-core';

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
      fieldId={id}
      label={label}
      isValid={error}
      type={type}
      helperText={help}
      helperTextInvalid={errorMessage}
    >
      {children}
    </FormGroup>
  );
}
