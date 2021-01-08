import * as React from "react";
import { useEffect, useState } from "react";
import useOnclickOutside from "react-cool-onclickoutside";
import {
  Button,
  Flex,
  FlexItem,
  FormGroup,
  Label,
  Select,
  SelectOption,
  SelectVariant,
  Form,
  Split,
  SplitItem,
  Stack,
  StackItem,
  TextInput
} from "@patternfly/react-core";
import { ArrowAltCircleRightIcon, TrashIcon } from "@patternfly/react-icons";
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./DataTypeItem.scss";
import ConstraintsLabel from "../ConstraintsLabel/ConstraintsLabel";
import { Validated } from "../../../types";

interface DataTypeItemProps {
  dataType: DDDataField;
  index: number;
  editingIndex: number | undefined;
  onSave: (dataType: DDDataField, index: number | null) => void;
  onEdit?: (index: number) => void;
  onDelete?: (index: number) => void;
  onConstraintsEdit: (dataType: DDDataField) => void;
  onConstraintsSave: (dataType: DDDataField) => void;
  onValidate: (dataTypeName: string) => boolean;
  onOutsideClick: () => void;
}

const DataTypeItem = (props: DataTypeItemProps) => {
  const {
    dataType,
    index,
    editingIndex,
    onSave,
    onEdit,
    onDelete,
    onConstraintsEdit,
    onConstraintsSave,
    onValidate,
    onOutsideClick
  } = props;
  const [name, setName] = useState(dataType.name);
  const [typeSelection, setTypeSelection] = useState<DDDataField["type"]>(dataType.type);
  const [isTypeSelectOpen, setIsTypeSelectOpen] = useState(false);
  const typeOptions = [
    { value: "string" },
    { value: "integer" },
    { value: "float" },
    { value: "double" },
    { value: "boolean" }
  ];
  const [validation, setValidation] = useState<Validated>("default");

  const ref = useOnclickOutside(
    () => {
      console.log("click outside");
      onOutsideClick();
    },
    { eventTypes: ["click"], disabled: editingIndex !== index }
  );

  const handleNameChange = (value: string) => {
    setName(value);
    setValidation(onValidate(value) ? "default" : "error");
  };

  const typeToggle = (isOpen: boolean) => {
    setIsTypeSelectOpen(isOpen);
  };

  const clearTypeSelection = () => {
    setIsTypeSelectOpen(false);
    setTypeSelection("string");
  };

  const typeSelect = (event: any, selection: any, isPlaceholder: boolean) => {
    if (isPlaceholder) {
      clearTypeSelection();
    } else {
      setTypeSelection(selection);
      setIsTypeSelectOpen(false);
      onSave({ name: name.trim(), type: selection }, index);
    }
  };

  const handleEditStatus = () => {
    console.log("set edit status");
    if (onEdit) {
      onEdit(index);
    }
  };

  const handleSave = (event?: React.FormEvent<HTMLFormElement>) => {
    event?.preventDefault();
    onSave({ name: name.trim(), type: typeSelection }, index);
  };

  const handleNameSave = () => {
    if (name.trim().length === 0) {
      setName(dataType.name);
    } else if (name !== dataType.name) {
      handleSave();
    }
  };

  const handleDelete = (event: React.MouseEvent | React.KeyboardEvent) => {
    event.stopPropagation();
    event.preventDefault();
    if (onDelete) {
      onDelete(index);
    }
  };

  const handleConstraints = () => {
    onConstraintsEdit({ ...dataType, name, type: typeSelection });
  };

  const handleConstraintsDelete = () => {
    const updatedDataType = { ...dataType };
    delete updatedDataType.constraints;
    onConstraintsSave(updatedDataType);
  };

  useEffect(() => {
    if (editingIndex === index) {
      const input = document.querySelector<HTMLInputElement>(`.data-type-item-n${index} #name`);
      input?.focus();
      if (name.startsWith("New Data Type")) {
        input?.select();
      }
    }
  }, [editingIndex]);

  useEffect(() => {
    setName(dataType.name);
    setTypeSelection(dataType.type);
  }, [dataType]);

  return (
    <article
      className={`editable-item ${editingIndex === index ? "editable-item--editing" : ""} data-type-item-n${index}`}
    >
      {editingIndex === index && (
        <section
          className={"editable-item__inner"}
          ref={ref}
          tabIndex={0}
          onKeyDown={event => {
            if (event.key === "Escape") {
              onOutsideClick();
            }
          }}
        >
          <Form onSubmit={handleSave}>
            <Stack hasGutter={true}>
              <StackItem>
                <Split hasGutter={true}>
                  <SplitItem>
                    <FormGroup
                      fieldId="name"
                      helperTextInvalid="Name already used by another Data Type"
                      validated={validation}
                      style={{ width: 280 }}
                    >
                      <TextInput
                        type="text"
                        id="name"
                        name="name"
                        value={name}
                        onChange={handleNameChange}
                        placeholder="Name"
                        validated={validation}
                        onBlur={handleNameSave}
                        autoComplete="off"
                      />
                    </FormGroup>
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
                      className="data-type-item__type-select"
                    >
                      {typeOptions.map((option, optionIndex) => (
                        <SelectOption key={optionIndex} value={option.value} className="ignore-onclickoutside" />
                      ))}
                    </Select>
                  </SplitItem>
                  <SplitItem isFilled={true}>&nbsp;</SplitItem>
                </Split>
              </StackItem>
              <StackItem>
                <Split hasGutter={true}>
                  <SplitItem>
                    {dataType.constraints !== undefined && (
                      <ConstraintsLabel
                        editingIndex={editingIndex}
                        constraints={dataType.constraints}
                        onConstraintsDelete={handleConstraintsDelete}
                      />
                    )}
                    {(name.trim().length === 0 || typeSelection === "boolean") && (
                      <Label icon={<ArrowAltCircleRightIcon />}>
                        {dataType.constraints === undefined ? "Add" : "Edit"} Constraints
                      </Label>
                    )}
                    {!(name.trim().length === 0 || typeSelection === "boolean") && (
                      <Label
                        variant="outline"
                        color="orange"
                        href="#"
                        icon={<ArrowAltCircleRightIcon />}
                        onClick={event => {
                          event.preventDefault();
                          handleConstraints();
                        }}
                      >
                        {dataType.constraints === undefined ? "Add" : "Edit"} Constraints
                      </Label>
                    )}
                  </SplitItem>
                </Split>
              </StackItem>
            </Stack>
          </Form>
        </section>
      )}
      {editingIndex !== index && (
        <section
          className={"editable-item__inner"}
          tabIndex={0}
          onClick={handleEditStatus}
          onKeyDown={event => {
            if (event.key === "Enter") {
              event.preventDefault();
              event.stopPropagation();
              handleEditStatus();
            }
          }}
        >
          <Flex alignItems={{ default: "alignItemsCenter" }} style={{ height: "100%" }}>
            <FlexItem>
              <strong>{name}</strong>
            </FlexItem>
            <FlexItem>
              <Label color="blue" className="data-type-item__type-label">
                {typeSelection}
              </Label>
              {dataType.constraints !== undefined && (
                <>
                  {" "}
                  <ConstraintsLabel constraints={dataType.constraints} />
                </>
              )}
            </FlexItem>
            <FlexItem align={{ default: "alignRight" }}>
              <Button variant="plain" onClick={handleDelete}>
                <TrashIcon />
              </Button>
            </FlexItem>
          </Flex>
        </section>
      )}
    </article>
  );
};

export default DataTypeItem;
