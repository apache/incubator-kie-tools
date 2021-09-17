import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
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
  }, [options]);

  return (
    <section data-ouia-component-id="mining-toolbar">
      <Split hasGutter={true}>
        <SplitItem isFilled={true}>
          <Select
            variant={SelectVariant.typeaheadMulti}
            typeAheadAriaLabel="Select fields"
            onToggle={onToggle}
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
          <Button variant="secondary" onClick={addAllFields} isDisabled={isDisabled}>
            Add All Fields
          </Button>
        </SplitItem>
      </Split>
    </section>
  );
};

export default MiningSchemaAddFields;
