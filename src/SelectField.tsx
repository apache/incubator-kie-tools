import React from 'react';
import {
  Checkbox,
  CheckboxProps,
  Radio,
  RadioProps,
  Select,
  SelectProps,
  SelectOption,
} from '@patternfly/react-core';

import { connectField, filterDOMProps } from './uniforms';
import { default as wrapField } from './wrapField';

const xor = (item, array) => {
  const index = array.indexOf(item);
  if (index === -1) {
    return array.concat([item]);
  }

  return array.slice(0, index).concat(array.slice(index + 1));
};

type CheckboxesProps = {
  fieldType?: typeof Array | any;
  onChange: (value?: string | string[]) => void;
  transform?: (value?: string) => string;
  allowedValues: string[];
  id: string;
  disabled?: boolean;
} & (Omit<CheckboxProps, 'isDisabled'> | Omit<RadioProps, 'isDisabled'>);

function renderCheckboxes(props: CheckboxesProps) {
  const Group = props.fieldType === Array ? Checkbox : Radio;
  return (
    <div {...filterDOMProps(props)}>
      {props.label && <label>{props.label}</label>}
      {props.allowedValues.map((item: any, index: number) => {
        return (
          <React.Fragment key={index}>
            <label htmlFor={props.id}>{props.transform ? props.transform(item) : item}</label>
            <Group
              id={`${props.id}-${item}`}
              isDisabled={props.disabled}
              name={props.name}
              aria-label={props.name}
              value={props.value}
              isChecked={
                // @ts-ignore
                // eslint-disable-next-line
                props.fieldType === Array ? props.value!.includes(item) : props.value === item
              }
              onChange={() => {
                props.onChange(props.fieldType === Array ? xor(item, props.value) : item)
              }}
            />
          </React.Fragment>
        );
      })}
    </div>
  );
}

type SelectInputProps = {
  required?: boolean;
  id: string;
  fieldType?: typeof Array | any;
  onSelect: (value?: string | string[]) => void;
  placeholder: string;
  allowedValues?: string[];
  disabled?: boolean;
  transform?: (value?: string) => string;
} & Omit<SelectProps, 'isDisabled'>;

function renderSelect(props: SelectInputProps) {
  return (
    <div {...filterDOMProps(props)}>
      {props.label && <label htmlFor={props.id}>{props.label}</label>}
      <Select
        isDisabled={props.disabled}
        id={props.id}
        variant={props.fieldType === Array ? 'typeaheadmulti' : 'single'}
        name={props.name}
        placeholder={props.placeholder}
        // eslint-disable-next-line
        onToggle={() => console.log('toggled') }
        onSelect={(value) => {
          // @ts-ignore
          props.onChange(value !== '' ? value : '');
        }}
        selections={[]}
        value={props.value || (props.fieldType === Array ? [] : undefined)}
      >
        {props.allowedValues!.map(value => (
          <SelectOption key={value} value={value}>
            {props.transform ? props.transform(value) : value}
          </SelectOption>
        ))}
      </Select>
    </div>
  );
}

export type SelectFieldProps = { checkboxes?: boolean } & (
  | CheckboxesProps
  | SelectProps
);

function SelectField({ checkboxes, ...props }: SelectFieldProps) {
  return checkboxes
    ? renderCheckboxes(props as CheckboxesProps)
    : renderSelect(props as SelectInputProps);
}

// @ts-ignore
export default connectField(SelectField);
