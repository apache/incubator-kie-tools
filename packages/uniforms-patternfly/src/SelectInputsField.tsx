/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Select,
  SelectOption,
  SelectOptionObject,
  SelectVariant,
} from "@patternfly/react-core/dist/js/components/Select";
import wrapField from "./wrapField";
import { filterDOMProps } from "uniforms";
import { SelectInputProps } from "./SelectField";

function isSelectOptionObject(
  toBeDetermined: string | number | SelectOptionObject
): toBeDetermined is SelectOptionObject {
  return typeof toBeDetermined === "object" && !Array.isArray(toBeDetermined) && toBeDetermined !== null;
}

function isSelectOptionString(toBeDetermined: string[] | number[]): toBeDetermined is string[] {
  return (toBeDetermined.length > 0 && typeof toBeDetermined[0] === "string") || toBeDetermined.length === 0;
}

function SelectInputsField(props: SelectInputProps) {
  const [expanded, setExpanded] = useState<boolean>(false);
  const [selected, setSelected] = useState<string | string[] | number | number[] | undefined>([]);

  const parseInput = useCallback(
    (
      selection: string | number | SelectOptionObject,
      fieldType: typeof Array | any
    ): string | string[] | number | number[] => {
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
    (event: React.MouseEvent | React.ChangeEvent, selection: string | SelectOptionObject) => {
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
        <SelectOption key={props.allowedValues!.length} isPlaceholder value={props.placeholder} />
      );
  }, [props.placeholder, selectOptions]);

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
        value={props.value || (props.fieldType === Array ? [] : undefined)}
        menuAppendTo={props.menuAppendTo}
        direction={props.direction}
      >
        {selectOptions}
      </Select>
    </div>
  );
}

export default SelectInputsField;
