import * as React from "react";
import { useEffect, useState } from "react";
import {
  Button,
  Select,
  SelectOption,
  SelectOptionObject,
  SelectVariant,
  Split,
  SplitItem
} from "@patternfly/react-core";
import { MiningSchemaField } from "../MiningSchemaContainer/MiningSchemaContainer";

interface MiningSchemaAddFieldsProps {
  options: MiningSchemaField[];
  onAdd: (fields: string[]) => void;
}

const MiningSchemaAddFields = ({ options, onAdd }: MiningSchemaAddFieldsProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selectOptions, setSelectOptions] = useState<Array<{ value: string; disabled: boolean }>>([]);
  const [selected, setSelected] = useState<string[]>([]);

  const onToggle = (openStatus: boolean) => {
    setIsOpen(openStatus);
  };

  const onSelect = (event: React.MouseEvent | React.ChangeEvent, selection: SelectOptionObject) => {
    if (selected.includes(selection.toString())) {
      const newSelections = selected.filter(item => item !== selection);
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
    const availableOptions = selectOptions.filter(item => !item.disabled);
    if (availableOptions.length) {
      onAdd(availableOptions.map(item => item.value));
    }
  };

  const clearSelection = () => {
    setSelected([]);
    setIsOpen(false);
  };

  useEffect(() => {
    setSelectOptions(
      options.map(option => ({
        value: option.name,
        disabled: option.isSelected
      }))
    );
  }, [options]);

  return (
    <section>
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
          >
            {selectOptions.map((option, index) => (
              <SelectOption isDisabled={option.disabled} key={index} value={option.value} />
            ))}
          </Select>
        </SplitItem>
        <SplitItem>
          <Button variant="primary" onClick={handleAdd}>
            Add Field(s)
          </Button>
        </SplitItem>
        <SplitItem>
          <Button variant="secondary" onClick={addAllFields}>
            Add All Fields
          </Button>
        </SplitItem>
      </Split>
    </section>
  );
};

export default MiningSchemaAddFields;
