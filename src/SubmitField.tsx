import React, { HTMLProps, Ref } from 'react';
import useForm from './useForm';
import { ButtonProps, Button } from '@patternfly/react-core'

export type SubmitFieldProps = {
  inputRef: undefined;
  name: string;
} & ButtonProps;

function SubmitField({
  isDisabled,
  inputRef,
  value,
  ...props
}: SubmitFieldProps) {
  const { error, state } = useForm();

  return (
    <Button
      isDisabled={isDisabled === undefined ? !!(error || state.disabled) : isDisabled}
      type="submit"
      ref={inputRef}
      variant="primary"
      {...props}
    >
      {value}
    </Button>
  );
}

SubmitField.defaultProps = { value: 'Submit' };

export default SubmitField;
