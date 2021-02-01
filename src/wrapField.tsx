import React from 'react';
import { FormGroup, FormGroupProps } from '@patternfly/react-core';
import { filterDOMProps } from 'uniforms/es5';

declare module 'uniforms' {
  interface FilterDOMProps {
    decimal: never;
    minCount: never;
    autoValue: never;
    isDisabled: never;
    checkboxes: never;
  }
}

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
  children: React.ReactNode
) {
  return (
    <FormGroup
      fieldId={id}
      label={label}
      validated={error ? 'error' : 'default'}
      type={type}
      helperText={help}
      helperTextInvalid={errorMessage}
      {...filterDOMProps(props)}
    >
      {children}
    </FormGroup>
  );
}
