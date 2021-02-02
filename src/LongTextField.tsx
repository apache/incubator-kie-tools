import React from 'react';
import { TextArea } from '@patternfly/react-core';
import { connectField, filterDOMProps, HTMLFieldProps } from 'uniforms/es5';

export type LongTextFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    inputRef: React.RefObject<HTMLInputElement>;
    onChange: (
      value: string,
      event: React.ChangeEvent<HTMLTextAreaElement>
    ) => void;
    value?: string;
    prefix?: string;
  }
>;

const LongText = ({
  disabled,
  id,
  inputRef,
  label,
  name,
  onChange,
  placeholder,
  value,
  ...props
}: LongTextFieldProps) => (
  <div {...filterDOMProps(props)}>
    {label && <label htmlFor={id}>{label}</label>}
    <TextArea
      id={id}
      disabled={disabled}
      name={name}
      aria-label={name}
      onChange={(value, event) => onChange(event.target.value)}
      placeholder={placeholder}
      ref={inputRef}
      value={value ?? ''}
    />
  </div>
);

export default connectField(LongText);
