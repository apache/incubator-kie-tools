import React from 'react';
import { Button, ButtonProps } from '@patternfly/react-core';
import { MinusCircleIcon } from '@patternfly/react-icons';
import { useField, filterDOMProps, joinName } from 'uniforms/es5';

export type ListDelFieldProps = {
  name: string;
  parent?: any;
  value?: unknown;
} & ButtonProps;

function ListDel({ name, disabled, ...props }: ListDelFieldProps) {
  const nameParts = joinName(null, name);
  const nameIndex = +nameParts[nameParts.length - 1];
  const parentName = joinName(nameParts.slice(0, -1));
  const parent = useField<{ minCount?: number }, unknown[]>(
    parentName,
    {},
    { absoluteName: true }
  )[0];

  const limitNotReached =
    !disabled && !(parent.minCount! >= parent.value!.length);

  return (
    <Button
      disabled={!limitNotReached || disabled}
      variant="plain"
      style={{ paddingLeft: '0', paddingRight: '0' }}
      onClick={() => {
        const value = parent.value!.slice();
        value.splice(nameIndex, 1);
        !disabled && limitNotReached && parent.onChange(value);
      }}
      {...filterDOMProps(props)}
    >
      <MinusCircleIcon color="#cc0000" />
    </Button>
  );
}

export default ListDel;
