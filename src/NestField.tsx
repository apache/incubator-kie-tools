import React, { HTMLProps } from 'react';
import { connectField, joinName } from 'uniforms';
import { Label } from '@patternfly/react-core';

import injectName from './uniforms/injectName';
import AutoField from './AutoField';
import wrapField from './wrapField';

export type NestFieldProps = {
  error?: boolean;
  errorMessage?: string;
  fields?: any[];
  itemProps?: object;
  showInlineError?: boolean;
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
  ...props
}: NestFieldProps) =>
  wrapField(
    { ...props },
    label && <Label>{label}</Label>,
    children
      ? injectName(name, children)
      : fields?.map(key => (
          <AutoField key={key} name={joinName(name, key)} {...itemProps} />
        )),
  );

export default connectField(Nest, {
  includeInChain: false,
});
