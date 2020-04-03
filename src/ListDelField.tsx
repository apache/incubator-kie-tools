import React from 'react';
import { Button, ButtonProps } from '@patternfly/react-core';
import { MinusCircleIcon } from '@patternfly/react-icons';

import { useField, filterDOMProps, joinName } from './uniforms';

export type ListDelFieldProps<T> = {
  name: string;
  parent?: any;
  value?: T;
} & Omit<ButtonProps, 'isDisabled'>;

function ListDel<T>(rawProps: ListDelFieldProps<T>) {
  const props = useField<ListDelFieldProps<T>, T>(rawProps.name, rawProps, {
    initialValue: false,
  })[0];

  const nameParts = joinName(null, props.name);
  const parentName = joinName(nameParts.slice(0, -1));
  const parent = useField<{ minCount?: number }, T[]>(parentName, {})[0];
  if (rawProps.parent) Object.assign(parent, rawProps.parent);

  const fieldIndex = +nameParts[nameParts.length - 1];
  const limitNotReached =
    !props.disabled && !(parent.minCount! >= parent.value!.length);

  return (
    <Button
      disabled={!limitNotReached || rawProps.disabled}
      onClick={() => {
        if (limitNotReached) {
          const value = parent.value!.slice();
          value.splice(fieldIndex, 1);
          parent.onChange(value);
        }
      }}
      {...filterDOMProps(props)}
    >
      <MinusCircleIcon />
    </Button>
  );
}

export default ListDel;
