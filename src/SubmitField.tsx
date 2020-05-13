import React from 'react';
import { Button, ButtonProps } from '@patternfly/react-core';
import { useForm, filterDOMProps } from 'uniforms/es5';

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
    // @ts-ignore
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

SubmitField.defaultProps = { value: 'Submit' };

export default SubmitField;
