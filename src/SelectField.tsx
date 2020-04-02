import React, { useState } from 'react';
import { connectField, filterDOMProps } from 'uniforms';
import {
  Checkbox,
  CheckboxProps,
  Radio,
  RadioProps,
  Select,
  SelectProps,
  SelectOption,
} from '@patternfly/react-core';

type CheckboxesProps = {
  fieldType?: typeof Array | any;
  onChange: (
    value?: string | boolean[] | number | { [key: string]: any },
  ) => void;
  transform?: (value?: string) => string;
  allowedValues: string[];
  id: string;
  disabled?: boolean;
} & (Omit<CheckboxProps, 'isDisabled'> | Omit<RadioProps, 'isDisabled'>);

function renderCheckboxes(props: CheckboxesProps) {
  const Group = props.fieldType === Array ? Checkbox : Radio;
  const checkboxProps = {
    disabled: props.disabled,
    id: props.id,
    name: props.name,
    onChange:
      props.fieldType === Array
        ? value => props.onChange && props.onChange(value)
        : event => props.onChange && props.onChange(event.target.value),
    options: props.allowedValues.map(value => {
      return {
        label: props.transform ? props.transform(value) : value,
        value,
      };
    }),
    value: props.value,
    ...filterDOMProps(props),
  };
  return <Group {...checkboxProps} />;
}

type SelectInputProps = {
  required?: boolean;
  id: string;
  fieldType?: typeof Array | any;
  onChange: (value?: string | string[]) => void;
  placeholder: string;
  allowedValues?: string[];
  disabled?: boolean;
  transform?: (value?: string) => string;
} & Omit<SelectProps, 'isDisabled'>;

function renderSelect(props: SelectInputProps) {
  // const [expanded, setExpanded] = useState<boolean>(false);
  // const [selections, setSelections] = useState([]);

  // const onSelect = (event, selection) => {
  //   event.preventDefault();
  //   const selectionSet = new Set(selections);
  //   selectionSet.add(selection);
  //   setSelections(Array.from(selectionSet));
  // }

  // const clearSelection = () => {
  //   setSelections([]);
  //   setExpanded(false);
  // }

  return (
    <Select
      isDisabled={props.disabled}
      id={props.id}
      variant={props.fieldType === Array ? 'typeaheadmulti' : 'single'}
      name={props.name}
      onChange={value => props.onChange(value)}
      placeholder={props.placeholder}
      // eslint-disable-next-line
      onToggle={() => console.log('toggled') }
      // onSelect={() => console.log('hello')}
      // onClear={() => console.log('hi')}
      selections={[]}
      value={props.value || (props.fieldType === Array ? [] : undefined)}
      {...filterDOMProps(props)}
    >
      {props.allowedValues!.map(value => (
        <SelectOption key={value} value={value}>
          {props.transform ? props.transform(value) : value}
        </SelectOption>
      ))}
    </Select>
  );
}

export type SelectFieldProps = { checkboxes?: boolean } & (
  | CheckboxesProps
  | SelectProps
);

function SelectField({ checkboxes, ...props }: SelectFieldProps) {
  return (
    <React.Fragment>
      {checkboxes
        ? renderCheckboxes(props as CheckboxesProps)
        : renderSelect(props as SelectInputProps)}
    </React.Fragment>
  );
}

export default connectField(SelectField);
