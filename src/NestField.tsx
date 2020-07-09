import React, { HTMLProps } from 'react';
import { connectField, joinName, injectName, filterDOMProps } from 'uniforms/es5';

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
    <div
      {...filterDOMProps(props)}
    >
      {label && <label>{label}</label>}
      {children
        ? injectName(name, children)
        : fields?.map(key => (
            <AutoField key={key} disabled={disabled} name={joinName(name, key)} {...itemProps} />
          ))}
    </div>
  );
  
}

export default connectField(Nest, {
  includeInChain: false,
});
