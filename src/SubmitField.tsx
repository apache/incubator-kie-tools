import React, { HTMLProps, Ref } from 'react';
import useForm from './helpers/useForm';
import { ButtonProps, Button } from '@patternfly/react-core'

export type SubmitFieldProps = {
  inputRef: undefined;
  name: string;
  disabled: boolean;
} & Omit<ButtonProps, 'isDisabled'>;

function SubmitField({
  disabled,
  inputRef,
  value,
  ...props
}: SubmitFieldProps) {
  const { error, state } = useForm();

  return (
    <Button
      isDisabled={disabled === undefined ? !!(error || state.disabled) : disabled}
      type="submit"
      ref={inputRef}
      variant="primary"
      {...props}
    >
      {value}
    </Button>
  );
}

export default SubmitField;
