/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useEffect, useState } from "react";
import { v4 as uuid } from "uuid";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import {
  Select,
  SelectOption,
  SelectOptionObject,
  SelectVariant,
} from "@patternfly/react-core/dist/js/components/Select";
import { MiningSchemaOption } from "../MiningSchemaContainer/MiningSchemaContainer";

interface MiningSchemaAddFieldsProps {
  options: MiningSchemaOption[];
  onAdd: (fields: string[]) => void;
  isDisabled: boolean;
}

const MiningSchemaAddFields = ({ options, onAdd, isDisabled }: MiningSchemaAddFieldsProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectOptions, setSelectOptions] = useState<Array<{ value: string; disabled: boolean }>>([]);
  const [selected, setSelected] = useState<string[]>([]);
  /* setting up a dynamic key for the Select component to fix PF issue with refreshing SelectOptions
  upon changes. See https://issues.redhat.com/browse/FAI-682 for more details.
   */
  const [selectKey, setSelectKey] = useState(uuid());

  const onToggle = (openStatus: boolean) => {
    setIsOpen(openStatus);
  };

  const onSelect = (event: React.MouseEvent | React.ChangeEvent, selection: SelectOptionObject) => {
    if (selected.includes(selection.toString())) {
      const newSelections = selected.filter((item) => item !== selection);
      setSelected(newSelections);
    } else {
      setSelected([...selected, selection.toString()]);
    }
  };

  const handleAdd = () => {
    onAdd(selected);
    clearSelection();
  };

  const addAllFields = () => {
    const availableOptions = selectOptions.filter((item) => !item.disabled);
    if (availableOptions.length) {
      onAdd(availableOptions.map((item) => item.value));
    }
  };

  const clearSelection = () => {
    setSelected([]);
    setIsOpen(false);
  };

  useEffect(() => {
    setSelectOptions(
      options.map((option) => ({
        value: option.name,
        disabled: option.isSelected,
      }))
    );
    setSelectKey(uuid());
  }, [options]);

  return (
    <section data-ouia-component-id="mining-toolbar">
      <Split hasGutter={true}>
        <SplitItem isFilled={true}>
          <Select
            key={selectKey}
            variant={SelectVariant.typeaheadMulti}
            typeAheadAriaLabel="Select fields"
            onToggle={onToggle}
            toggleId="select-mining-field"
            onSelect={onSelect}
            onClear={clearSelection}
            selections={selected}
            isOpen={isOpen}
            aria-labelledby={"Select fields to add"}
            placeholderText="Select fields"
            isDisabled={isDisabled}
            ouiaId="select-mining-field"
          >
            {selectOptions.map((option, index) => (
              <SelectOption
                isDisabled={option.disabled}
                key={index}
                value={option.value}
                data-ouia-component-id={option.value}
                data-ouia-component-type="select-option"
              />
            ))}
          </Select>
        </SplitItem>
        <SplitItem>
          <Button variant="primary" onClick={handleAdd} isDisabled={isDisabled} ouiaId="add-mining-field">
            Add Field(s)
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={addAllFields} isDisabled={isDisabled} ouiaId="add-all-fields">
            Add All Fields
          </Button>
        </SplitItem>
      </Split>
    </section>
  );
};

export default MiningSchemaAddFields;
