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

import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import {
  Select,
  SelectOption,
  SelectOptionObject,
  SelectVariant,
} from "@patternfly/react-core/dist/js/components/Select";
import wrapField from "./wrapField";
import { SelectInputProps } from "./SelectField";

type SelectFieldValue = string | string[];

function isSelectOptionObject(
  toBeDetermined: string | number | SelectOptionObject
): toBeDetermined is SelectOptionObject {
  return typeof toBeDetermined === "object" && !Array.isArray(toBeDetermined) && toBeDetermined !== null;
}

function SelectInputsField(props: SelectInputProps) {
  const [expanded, setExpanded] = useState<boolean>(false);
  const [selected, setSelected] = useState<SelectFieldValue | undefined>(() => {
    if (props.value === undefined) {
      return [];
    }
    if (props.value === null) {
      return "null";
    }
    if (Array.isArray(props.value)) {
      return [...props.value];
    }
    return props.value;
  });

  // Parses the selection to a string or string[]
  // This prevents a bug where the number 0 can't be selected
  const parseSelection = useCallback(
    (selection: string | number | SelectOptionObject, fieldType: typeof Array): SelectFieldValue => {
      if (selection === null) {
        return `${selection}`;
      }
      const parsedSelection = isSelectOptionObject(selection) ? selection.toString() : selection;

      if (fieldType !== Array) {
        return parsedSelection !== "" ? `${parsedSelection}` : "";
      }

      if (Array.isArray(selected)) {
        if (selected.includes(`${parsedSelection}`)) {
          return selected.filter((s) => s !== `${parsedSelection}`);
        }
        return [`${parsedSelection}`, ...selected];
      }
      return [];
    },
    [selected]
  );

  const handleSelect = useCallback(
    (event: React.MouseEvent | React.ChangeEvent, selection: string | number | SelectOptionObject) => {
      if (selection === props.placeholder) {
        props.onChange(undefined);
        setSelected([]);
      } else {
        if (selection === "null") {
          props.onChange(null);
          setSelected("null");
        } else {
          const selectedItems = parseSelection(selection, props.fieldType);
          // If the selection is a number we should convert the selectedItems back to a number
          const onChanged =
            selection === null
              ? null
              : typeof selection === "number"
                ? Array.isArray(selectedItems)
                  ? selectedItems.map((item) => JSON.parse(item))
                  : JSON.parse(selectedItems)
                : selectedItems;
          props.onChange(onChanged);
          setSelected(selectedItems);
        }
      }
      setExpanded(false);
    },
    [parseSelection, props]
  );

  const selectOptions = useMemo(() => {
    const options: JSX.Element[] = [];
    if (props.placeholder) {
      options.push(
        <SelectOption key={`placeholder ${props.allowedValues!.length}`} isPlaceholder value={props.placeholder} />
      );
    }
    props.allowedValues?.forEach((value) =>
      options.push(
        <SelectOption key={value} value={value}>
          {props.transform ? props.transform(value) : `${value}`}
        </SelectOption>
      )
    );
    return options;
  }, [props]);

  return wrapField(
    props as any,
    <div data-testid={"select-inputs-field"} id={props.id}>
      <Select
        isDisabled={props.disabled}
        id={props.id}
        variant={props.fieldType === Array ? SelectVariant.typeaheadMulti : SelectVariant.single}
        name={props.name}
        placeholderText={props.placeholder}
        isOpen={expanded}
        selections={selected}
        onToggle={(isExpanded) => setExpanded(isExpanded)}
        onSelect={handleSelect}
        menuAppendTo={props.menuAppendTo}
        direction={props.direction}
      >
        {selectOptions}
      </Select>
    </div>
  );
}

export default SelectInputsField;
