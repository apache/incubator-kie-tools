import React from 'react';
import { ButtonProps, Button } from '@patternfly/react-core';

import { useForm, filterDOMProps } from './uniforms';
import wrapField from './wrapField';

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
    <div {...filterDOMProps(props)}>
      <Button
        isDisabled={
          disabled === undefined ? !!(error || state.disabled) : disabled
        }
        type="submit"
        ref={inputRef}
        variant="primary"
      >
        {value}
      </Button>
    </div>
  )
} 

export default SubmitField;
