import React, { useState } from 'react';
import {
  Checkbox,
  CheckboxProps,
  Radio,
  RadioProps,
  Select,
  SelectProps,
  SelectOption,
  SelectVariant,
} from '@patternfly/react-core';
import { connectField, filterDOMProps } from 'uniforms/es5';

import wrapField from './wrapField';

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

filterDOMProps.register('autoValue');

function RenderCheckboxes(props: CheckboxesProps) {
  const Group = props.fieldType === Array ? Checkbox : Radio;
  
  return (
    // @ts-ignore
    <div {...filterDOMProps(props)}>
      {props.label && <label>{props.label}</label>}
      {props.allowedValues!.map((item: any, index: number) => {
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
  onChange: (value?: string | string[]) => void;
  placeholder: string;
  allowedValues?: string[];
  disabled?: boolean;
  error?: boolean;
  transform?: (value?: string) => string;
} & Omit<SelectProps, 'isDisabled'>;

function RenderSelect(props: SelectInputProps) {

  const selectDefault = props.fieldType === Array ? [] : props.placeholder;

  const [expanded, setExpanded] = useState<boolean>(false);
  const [selected, setSelected] = useState<string | string[]>(selectDefault);

  const handleSelect = (event, selection) => {
    const items = parseInput(selection, props.fieldType);
    props.onChange(items);
    setSelected(items);
    setExpanded(false);
  }

  const parseInput = (selection, fieldType) => {
    if (fieldType !== Array) return (selection !== '') ? selection : '';
    return (selected.includes(selection))
      // @ts-ignore
      ? selected.filter(s => s !== selection)
      // @ts-ignore
      : [selection, ...selected];
  }

  const selectedOptions = props.allowedValues!.map(value => (
    <SelectOption key={value} value={value}>
      {props.transform ? props.transform(value) : value}
    </SelectOption>
  ));

  if (props.placeholder) selectedOptions.unshift(
    <SelectOption
      key={props.allowedValues!.length}
      isDisabled
      isPlaceholder
      value={props.placeholder}
    />
  );
  return (
    wrapField(
      props,
      <Select
        isDisabled={props.disabled}
        id={props.id}
        variant={props.fieldType === Array ? SelectVariant.typeaheadMulti : SelectVariant.single}
        name={props.name}
        placeholderText={props.placeholder}
        isOpen={expanded}
        selections={selected}
        onToggle={() => setExpanded(!expanded) }
        onSelect={handleSelect}
        value={props.value || (props.fieldType === Array ? [] : undefined)}
      >
        { selectedOptions }
      </Select>
    )
  );
}

export type SelectFieldProps = { checkboxes?: boolean } & (
  | CheckboxesProps
  | SelectProps
);

function SelectField({ checkboxes, ...props }: SelectFieldProps) {
  return checkboxes 
    ? RenderCheckboxes(props as CheckboxesProps)
    : RenderSelect(props as SelectInputProps);
}

// @ts-ignore
export default connectField(SelectField);
