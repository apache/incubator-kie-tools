import React from 'react';
import cloneDeep from 'lodash/cloneDeep';
import { Button, ButtonProps } from '@patternfly/react-core';
import { PlusCircleIcon } from '@patternfly/react-icons';
import {
  connectField,
  FieldProps,
  filterDOMProps,
  joinName,
  useField,
} from 'uniforms/es5';

export type ListAddFieldProps = FieldProps<
  unknown,
  ButtonProps,
  {
    initialCount?: number;
    parent?: any;
    name: string;
    disabled?: boolean;
    value?: unknown;
  }
>;

function ListAdd({
  disabled = false,
  name,
  value,
  ...props
}: ListAddFieldProps) {
  const nameParts = joinName(null, name);
  const parentName = joinName(nameParts.slice(0, -1));
  const parent = useField<{ maxCount?: number }, unknown[]>(
    parentName,
    {},
    { absoluteName: true }
  )[0];

  const limitNotReached =
    !disabled && !(parent.maxCount! <= parent.value!.length);

  return (
    <Button
      variant="plain"
      style={{ paddingLeft: '0', paddingRight: '0' }}
      disabled={!limitNotReached}
      onClick={() => {
        !disabled &&
          limitNotReached &&
          parent.onChange(parent.value!.concat([cloneDeep(value)]));
      }}
      {...filterDOMProps(props)}
    >
      <PlusCircleIcon color="#0088ce" />
    </Button>
  );
}

export default connectField<ListAddFieldProps>(ListAdd, {
  initialValue: false,
  kind: 'leaf',
});
