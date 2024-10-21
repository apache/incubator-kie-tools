/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { FormSelect, FormSelectOption } from "@patternfly/react-core";
import { useCallback, useMemo, useState } from "react";
import { SelectInputProps, isSelectOptionObject } from "./SelectField.types";
import wrapField from "./wrapField";

type SelectFieldValue = string | string[] | number | number[];

function isSelectOptionString(toBeDetermined: string[] | number[]): toBeDetermined is string[] {
  return (toBeDetermined.length > 0 && typeof toBeDetermined[0] === "string") || toBeDetermined.length === 0;
}

function SelectInputsField(props: SelectInputProps) {
  const [selected, setSelected] = useState<SelectFieldValue | undefined>(() => {
    if (!props.value) {
      return [];
    }
    if (Array.isArray(props.value)) {
      return [...props.value];
    }
    return props.value;
  });

  const parseInput = useCallback(
    (selection: string | number, fieldType?: typeof Array): SelectFieldValue => {
      const parsedSelection = isSelectOptionObject(selection) ? selection.toString() : selection;

      if (fieldType !== Array) {
        return parsedSelection !== "" ? parsedSelection : "";
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
    (_event: unknown, selection: string) => {
      if (selection === props.placeholder) {
        props.onChange(undefined);
        setSelected([]);
      } else {
        const items = parseInput(selection, props.fieldType);
        props.onChange(items);
        setSelected(items);
      }
    },
    [parseInput, props]
  );

  const selectOptions = useMemo(() => {
    const options: JSX.Element[] = [];
    if (props.placeholder) {
      options.push(
        <FormSelectOption
          isPlaceholder
          key={`placeholder ${props.options!.length}`}
          value={props.placeholder}
          label={props.placeholder}
        >
          {props.placeholder}
        </FormSelectOption>
      );
    }

    props.options?.forEach((option) => {
      const value = isSelectOptionObject(option) ? option.value : option;
      const label = props.transform?.(value).label ?? value;

      options.push(
        <FormSelectOption key={value} value={value} label={label.toString()}>
          {label}
        </FormSelectOption>
      );
    });

    return options;
  }, [props]);

  return wrapField(
    props as any,
    <div data-testid={"select-inputs-field"} id={props.id}>
      <FormSelect
        role="listbox"
        placeholder={props.placeholder}
        value={props.value || (props.fieldType === Array ? [] : undefined)}
        id={props.id}
        isDisabled={props.disabled}
        name={props.name}
        onChange={handleSelect}
      >
        {selectOptions}
      </FormSelect>
    </div>
  );
}

export default SelectInputsField;
