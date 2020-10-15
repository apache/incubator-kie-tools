import * as React from "react";
import { useContext, useEffect, useState } from "react";
import useOnclickOutside from "react-cool-onclickoutside";
import {
  Button,
  Divider,
  Label,
  Select,
  SelectOption,
  SelectVariant,
  Switch,
  Form,
  Radio,
  Split,
  SplitItem,
  Stack,
  StackItem,
  TextInput
} from "@patternfly/react-core";
import { AngleRightIcon, CheckIcon, EditAltIcon, TrashIcon } from "@patternfly/react-icons";
import { DataType, StatusContext } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./DataTypeItem.scss";

interface DataTypeItemProps {
  dataType: DataType;
  index: number;
  onSave: (dataType: DataType, index: number | null) => void;
  onEdit?: (index: number) => void;
  onDelete?: (index: number) => void;
}

const DataTypeItem = (props: DataTypeItemProps) => {
  const { dataType, index, onSave, onEdit, onDelete } = props;
  const editing = useContext(StatusContext);
  const [name, setName] = useState(dataType.name);
  const [typeSelection, setTypeSelection] = useState<string>(dataType.type);
  const [isTypeSelectOpen, setIsTypeSelectOpen] = useState(false);
  const [isList, setIsList] = useState(dataType.list);
  const [showConstraints, setShowConstraints] = useState(false);
  const typeOptions = [{ value: "String" }, { value: "Number" }, { value: "Boolean" }];

  const ref = useOnclickOutside(() => {
    // this should be split with some kind of validation
    if (editing === index && (index === -1 || name.trim().length > 0)) {
      onSave({ name, type: typeSelection, list: isList }, index);
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
    setTypeSelection("String");
  };

  const typeSelect = (event: any, selection: any, isPlaceholder: boolean) => {
    if (isPlaceholder) {
      clearTypeSelection();
    } else {
      setTypeSelection(selection);
      setIsTypeSelectOpen(false);
    }
  };

  const toggleConstraintsSection = () => {
    setShowConstraints(!showConstraints);
  };

  const handleEditStatus = () => {
    if (onEdit) {
      onEdit(index);
    }
  };

  const handleSave = () => {
    onSave({ name, type: typeSelection, list: isList }, index);
  };

  const handleDelete = () => {
    if (onDelete) {
      onDelete(index);
    }
  };

  useEffect(() => {
    if (editing === index) {
      document.querySelector<HTMLInputElement>(`.data-type-item-n${index} #name`)?.focus();
    }
  }, [editing]);

  return (
    <article className={`data-type-item data-type-item-n${index}`} ref={ref}>
      {editing === index && (
        <Form onSubmit={handleSave}>
          <Stack hasGutter={true}>
            <StackItem>
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
                    menuAppendTo={"parent"}
                  >
                    {typeOptions.map((option, optionIndex) => (
                      <SelectOption key={optionIndex} value={option.value} />
                    ))}
                  </Select>
                </SplitItem>
                <SplitItem isFilled={true}>&nbsp;</SplitItem>
                <SplitItem>
                  <Button variant="plain" onClick={handleSave} isDisabled={name.trim().length === 0}>
                    <CheckIcon />
                  </Button>
                </SplitItem>
              </Split>
            </StackItem>
            <StackItem>
              <Split hasGutter={true}>
                <SplitItem>
                  <Button
                    variant={"link"}
                    isInline={true}
                    onClick={toggleConstraintsSection}
                    className={
                      showConstraints
                        ? "data-type-item__constraints-toggle--on"
                        : "data-type-item__constraints-toggle--off"
                    }
                  >
                    Constraints <AngleRightIcon className="data-type-item__constraints-toggle__icon" />
                  </Button>
                </SplitItem>
                <Divider isVertical={true} />
                <SplitItem>
                  {/*a bit of PF hate here*/}
                  <label className="pf-c-form__label" htmlFor="list-type" style={{ marginRight: "1em" }}>
                    <span className="pf-c-form__label-text">Is a List</span>
                  </label>
                  <Switch id="list-type" aria-label="Yes" isChecked={isList} onChange={() => setIsList(!isList)} />
                </SplitItem>
              </Split>
            </StackItem>
            {showConstraints && (
              <StackItem>
                <Split>
                  <SplitItem>
                    <ConstraintsTypeSelector />
                  </SplitItem>
                </Split>
              </StackItem>
            )}
          </Stack>
        </Form>
      )}
      {editing !== index && (
        // Change Split to Flex
        <Split hasGutter={true}>
          <SplitItem>
            <strong>{name}</strong>
          </SplitItem>
          <SplitItem isFilled={true}>
            <Label color="blue">{typeSelection}</Label>
            {isList && (
              <>
                {" "}
                <Label color="cyan">List</Label>
              </>
            )}
          </SplitItem>
          <SplitItem>
            <Button variant="plain" onClick={handleEditStatus} isDisabled={editing !== false}>
              <EditAltIcon />
            </Button>
            <Button variant="plain" onClick={handleDelete} isDisabled={editing !== false}>
              <TrashIcon />
            </Button>
          </SplitItem>
        </Split>
      )}
    </article>
  );
};

export default DataTypeItem;

const ConstraintsTypeSelector = () => {
  const [typeSelection, setTypeSelection] = useState<string>("");
  const constraintsOptions = [{ value: "Enumeration" }, { value: "Expression" }, { value: "Range" }];

  const handleChange = (checked: boolean, event: React.FormEvent<HTMLInputElement>) => {
    const { value } = event.currentTarget;
    console.log(value);
    setTypeSelection(value);
  };

  return (
    <>
      {constraintsOptions.map(option => (
        <Radio
          isChecked={typeSelection === option.value}
          name={`constraint-type-${option.value.toLowerCase()}`}
          onChange={handleChange}
          label={option.value}
          id={`constraint-type-${option.value.toLowerCase()}`}
          value={option.value}
        />
      ))}
    </>
  );
};
