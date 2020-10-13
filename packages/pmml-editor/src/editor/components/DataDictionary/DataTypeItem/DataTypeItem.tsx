import * as React from "react";
import { useContext, useState } from "react";
import useOnclickOutside from "react-cool-onclickoutside";
import {
  Button,
  Label,
  Select,
  SelectOption,
  SelectVariant,
  Form,
  Split,
  SplitItem,
  TextInput
} from "@patternfly/react-core";
import { CheckIcon, EditAltIcon, TrashIcon } from "@patternfly/react-icons";
import { DataType, StatusContext } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./DataTypeItem.scss";

interface DataTypeItemProps {
  dataType: DataType;
  index: number;
  onSave: (dataType: DataType, index: number | null) => void;
  onEdit?: (index: number) => void;
}

const DataTypeItem = (props: DataTypeItemProps) => {
  const { dataType, index, onSave, onEdit } = props;
  const editing = useContext(StatusContext);
  const [name, setName] = useState(dataType.name);
  const [isTypeSelectOpen, setIsTypeSelectOpen] = useState(false);
  const [typeSelection, setTypeSelection] = useState<string>(dataType.type);
  const typeOptions = [
    { value: "Select Type", isPlaceholder: true },
    { value: "Number" },
    { value: "String" },
    { value: "Boolean" }
  ];

  const ref = useOnclickOutside(() => {
    if (editing === index) {
      onSave({ name, type: typeSelection }, index);
    }
  });

  const handleNameChange = (value: string) => {
    setName(value);
  };
  const typeToggle = (isOpen: boolean) => {
    setIsTypeSelectOpen(isOpen);
  };

  const clearTypeSelection = () => {
    setIsTypeSelectOpen(false);
    setTypeSelection("Select Type");
  };

  const typeSelect = (event: any, selection: any, isPlaceholder: boolean) => {
    if (isPlaceholder) {
      clearTypeSelection();
    } else {
      setTypeSelection(selection);
      setIsTypeSelectOpen(false);
    }
  };

  const handleEditStatus = () => {
    if (onEdit) {
      onEdit(index);
    }
  };

  const handleSave = () => {
    onSave({ name, type: typeSelection }, index);
  };

  return (
    <article className="data-type-item" ref={ref}>
      {editing === index && (
        <Form>
          <Split hasGutter={true}>
            <SplitItem>
              <TextInput
                type="text"
                id="name"
                name="name"
                value={name}
                onChange={handleNameChange}
                placeholder="Name"
              />
            </SplitItem>
            <SplitItem>
              <Select
                variant={SelectVariant.single}
                aria-label="Select Input"
                onToggle={typeToggle}
                onSelect={typeSelect}
                selections={typeSelection}
                isOpen={isTypeSelectOpen}
                placeholder="Type"
              >
                {typeOptions.map((option, optionIndex) => (
                  <SelectOption key={optionIndex} value={option.value} isPlaceholder={option.isPlaceholder} />
                ))}
              </Select>
            </SplitItem>
            <SplitItem isFilled={true}>&nbsp;</SplitItem>
            <SplitItem>
              <Button variant="plain" onClick={handleSave}>
                <CheckIcon />
              </Button>
            </SplitItem>
          </Split>
        </Form>
      )}
      {editing !== index && (
        <Split hasGutter={true}>
          <SplitItem>
            <strong>{name}</strong>
          </SplitItem>
          <SplitItem isFilled={true}>
            <Label color="blue">{typeSelection}</Label>
          </SplitItem>
          <SplitItem>
            <Button variant="plain" onClick={handleEditStatus} isDisabled={editing !== false}>
              <EditAltIcon />
            </Button>
            <Button variant="plain" isDisabled={editing !== false}>
              <TrashIcon />
            </Button>
          </SplitItem>
        </Split>
      )}
    </article>
  );
};

export default DataTypeItem;
