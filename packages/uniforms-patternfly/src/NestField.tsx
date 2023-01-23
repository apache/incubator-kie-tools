import React from 'react';
import { connectField, filterDOMProps, HTMLFieldProps } from 'uniforms';
import { Card, CardBody } from '@patternfly/react-core';

import AutoField from './AutoField';

export type NestFieldProps = HTMLFieldProps<
  object,
  HTMLDivElement,
  { helperText?: string; itemProps?: object }
>;

const Nest = ({
  children,
  error,
  errorMessage,
  fields,
  itemProps,
  label,
  name,
  showInlineError,
  disabled,
  ...props
}: NestFieldProps) => {
  return (
    <Card {...filterDOMProps(props)}>
      <CardBody className="pf-c-form">
        {label && (
          <label>
            <b>{label}</b>
          </label>
        )}
        {children ||
          fields?.map((field) => (
            <AutoField
              key={field}
              disabled={disabled}
              name={field}
              {...itemProps}
            />
          ))}
      </CardBody>
    </Card>
  );
};

export default connectField(Nest);
