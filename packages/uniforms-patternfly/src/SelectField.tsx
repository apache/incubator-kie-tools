import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Checkbox,
  CheckboxProps,
  Radio,
  RadioProps,
  Select,
  SelectDirection,
  SelectOption,
  SelectOptionObject,
  SelectProps,
  SelectVariant
} from '@patternfly/react-core';
import { connectField, FieldProps, filterDOMProps } from 'uniforms';

import wrapField from './wrapField';

function xor<T>(item: T, array: T[]) {
  const index = array.indexOf(item);
  if (index === -1) {
    return array.concat([item]);
  }

  return array.slice(0, index).concat(array.slice(index + 1));
}

type CheckboxesProps = FieldProps<
  string | string[],
  CheckboxProps | RadioProps,
  {
    checkboxes: true;
    fieldType?: typeof Array | any;
    onChange: (value?: string | string[]) => void;
    transform?: (value?: string) => string;
    allowedValues: string[];
    id: string;
    disabled?: boolean;
  }
>;

filterDOMProps.register('autoValue');

type SelectInputProps = FieldProps<
  string | string[],
  SelectProps,
  {
    checkboxes: false;
    required?: boolean;
    id: string;
    fieldType?: typeof Array | any;
    onChange: (value?: string | string[] | number | number[]) => void;
    placeholder: string;
    allowedValues?: (string | number)[];
    disabled?: boolean;
    error?: boolean;
    transform?: (value?: string | number) => string | number;
    direction: SelectDirection;
    menuAppendTo: HTMLElement;
  }
>;

function isSelectOptionObject(
  toBeDetermined: string | number | SelectOptionObject
): toBeDetermined is SelectOptionObject {
  return typeof toBeDetermined === 'object' &&
    !Array.isArray(toBeDetermined) &&
    toBeDetermined !== null
}

function isSelectOptionString(
  toBeDetermined: string[] | number[]
): toBeDetermined is string[] {
  return (toBeDetermined.length > 0 && typeof toBeDetermined[0] === 'string') || toBeDetermined.length === 0;
}

export type SelectFieldProps = CheckboxesProps | SelectInputProps;

function SelectField(props: SelectFieldProps) {
  if (props.checkboxes) {
    const Group = useMemo(
      () => (props.fieldType === Array ? Checkbox : Radio),
      [props]
    );

    return (
      <div {...filterDOMProps(props)}>
        {props.label && <label>{props.label}</label>}
        {props.allowedValues!.map((item: string, index: number) => {
          return (
            <React.Fragment key={index}>
              <label htmlFor={props.id}>
                {props.transform ? props.transform(item) : item}
              </label>
              <Group
                id={`${props.id}-${item}`}
                isDisabled={props.disabled}
                name={props.name}
                aria-label={props.name}
                value={props.value}
                isChecked={
                  props.fieldType === Array && Array.isArray(props.value)
                    ? props.value!.includes(item)
                    : props.value === item
                }
                onChange={() => {
                  props.onChange(
                    props.fieldType === Array && Array.isArray(props.value)
                      ? xor(item, props.value)
                      : item
                  );
                }}
              />
            </React.Fragment>
          );
        })}
      </div>
    );
  }

  const [expanded, setExpanded] = useState<boolean>(false);
  const [selected, setSelected] = useState<string | string[] | number | number[] | undefined>([]);

  const parseInput = useCallback(
    (
      selection: string | number | SelectOptionObject,
      fieldType: typeof Array | any
    ): string | string[] | number | number[] => {
      const parsedSelection = isSelectOptionObject(selection)
        ? selection.toString()
        : selection;

      if (fieldType !== Array) {
        return parsedSelection !== '' ? parsedSelection : '';
      }

      if (Array.isArray(selected)) {
        if (isSelectOptionString(selected) && typeof parsedSelection === "string") {
          if (selected.includes(parsedSelection)) {
            return selected.filter((s) => s !== parsedSelection);
          }
          return [parsedSelection, ...selected];
        } else if (!isSelectOptionString(selected) && typeof parsedSelection === "number") {
          if (selected.includes(parsedSelection)) {
            return selected.filter((s) => s !== parsedSelection);
          }
          return [parsedSelection, ...selected];
        }
      }
      return [];
    },
    [selected]
  );

  const handleSelect = useCallback(
    (
      event: React.MouseEvent | React.ChangeEvent,
      selection: string | SelectOptionObject
    ) => {
      if (selection === props.placeholder) {
        props.onChange(undefined);
        setSelected(undefined);
      } else {
        const items = parseInput(selection, props.fieldType);
        props.onChange(items);
        setSelected(items);
      }
      setExpanded(false);

    },
    [parseInput, props]
  );

  const selectOptions = useMemo(
    () =>
      props.allowedValues?.map((value) => (
        <SelectOption key={value} value={value}>
          {props.transform ? props.transform(value) : value}
        </SelectOption>
      )),
    [props]
  );

  useEffect(() => {
    if (props.placeholder)
      selectOptions?.unshift(
        <SelectOption
          key={props.allowedValues!.length}
          isPlaceholder
          value={props.placeholder}
        />
      );
  }, [props.placeholder, selectOptions])

  return wrapField(
    props,
    <Select
      isDisabled={props.disabled}
      id={props.id}
      variant={
        props.fieldType === Array
          ? SelectVariant.typeaheadMulti
          : SelectVariant.single
      }
      name={props.name}
      placeholderText={props.placeholder}
      isOpen={expanded}
      selections={selected}
      onToggle={(isExpanded) => setExpanded(isExpanded)}
      onSelect={handleSelect}
      value={props.value || (props.fieldType === Array ? [] : undefined)}
      menuAppendTo={props.menuAppendTo}
      direction={props.direction}
    >
      {selectOptions}
    </Select>
  );
}

export default connectField<SelectFieldProps>(SelectField);
