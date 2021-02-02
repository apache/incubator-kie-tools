import React from 'react';
import { Button } from '@patternfly/react-core';
import { filterDOMProps, HTMLFieldProps, useForm } from 'uniforms/es5';

export type SubmitFieldProps = HTMLFieldProps<
  number | string | undefined,
  HTMLDivElement,
  { inputRef: React.RefObject<HTMLButtonElement> }
>;

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
  );
}

SubmitField.defaultProps = { value: 'Submit' };

export default SubmitField;
