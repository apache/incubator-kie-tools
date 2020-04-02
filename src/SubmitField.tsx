import React from 'react';
import { ButtonProps, Button } from '@patternfly/react-core';

import useForm from './uniforms/useForm';

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
      isDisabled={
        disabled === undefined ? !!(error || state.disabled) : disabled
      }
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
