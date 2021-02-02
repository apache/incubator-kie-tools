import React, { HTMLProps } from 'react';
import { connectField, filterDOMProps } from 'uniforms/es5';
import { Card, CardBody } from '@patternfly/react-core';

import AutoField from './AutoField';

export type NestFieldProps = {
  error?: boolean;
  errorMessage?: string;
  fields?: any[];
  itemProps?: object;
  showInlineError?: boolean;
  disabled?: boolean;
  name: string;
} & HTMLProps<HTMLDivElement>;

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
    <Card {...filterDOMProps(props)} style={{ marginBottom: '1.5rem' }}>
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
