import * as React from "react";
import { useContext, useEffect, useState } from "react";
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
import { AngleRightIcon, CheckIcon, EditAltIcon, TrashIcon } from "@patternfly/react-icons";
import { DataField, StatusContext } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./DataTypeItem.scss";
import ConstraintsLabel from "../ConstraintsLabel/ConstraintsLabel";
import { Validated } from "../../../types";

interface DataTypeItemProps {
  dataType: DataField;
  index: number;
  onSave: (dataType: DataField, index: number | null) => void;
  onEdit?: (index: number) => void;
  onDelete?: (index: number) => void;
  onConstraintsEdit: (dataType: DataField) => void;
  onValidate: (dataTypeName: string) => boolean;
}

const DataTypeItem = (props: DataTypeItemProps) => {
  const { dataType, index, onSave, onEdit, onDelete, onConstraintsEdit, onValidate } = props;
  const editing = useContext(StatusContext);
  const [name, setName] = useState(dataType.name);
  const [typeSelection, setTypeSelection] = useState<DataField["type"]>(dataType.type);
  const [isTypeSelectOpen, setIsTypeSelectOpen] = useState(false);
  const typeOptions = [
    { value: "string" },
    { value: "integer" },
    { value: "float" },
    { value: "double" },
    { value: "boolean" }
  ];
  const [validation, setValidation] = useState<Validated>("default");

  const ref = useOnclickOutside(() => {
    // this should be split with some kind of validation
    if (editing === index && (index === -1 || name.trim().length > 0)) {
      handleSave();
    }
  });

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
    }
  };

  const handleEditStatus = () => {
    if (onEdit) {
      onEdit(index);
    }
  };

  const handleSave = () => {
    if (validation !== "error") {
      onSave({ name: name.trim(), type: typeSelection }, index);
    }
  };

  const handleDelete = () => {
    if (onDelete) {
      onDelete(index);
    }
  };

  const handleConstraints = () => {
    onConstraintsEdit({ ...dataType, name, type: typeSelection });
  };

  useEffect(() => {
    if (editing === index) {
      document.querySelector<HTMLInputElement>(`.data-type-item-n${index} #name`)?.focus();
    }
  }, [editing]);

  return (
    <>
      {editing === index && (
        <article className={`data-type-item data-type-item-n${index}`} ref={ref}>
          <Form onSubmit={handleSave}>
            <Stack hasGutter={true}>
              <StackItem>
                {/* Change Split to Flexbox */}
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
                      menuAppendTo={"parent"}
                      className="data-type-item__type-select"
                    >
                      {typeOptions.map((option, optionIndex) => (
                        <SelectOption
                          key={optionIndex}
                          value={option.value}
                          className="data-type-item__type-select__option"
                        />
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
                    {dataType.constraints === undefined && (
                      <Button
                        variant="link"
                        icon={<AngleRightIcon />}
                        isInline={true}
                        iconPosition="right"
                        onClick={handleConstraints}
                        isDisabled={name.trim().length === 0 || typeSelection === "boolean"}
                      >
                        <span>Add Constraints</span>
                      </Button>
                    )}
                    {dataType.constraints !== undefined && (
                      <>
                        <span>Constraints</span>
                        <Button
                          variant="link"
                          onClick={handleConstraints}
                          isDisabled={name.trim().length === 0 || typeSelection === "boolean"}
                        >
                          <ConstraintsLabel constraints={dataType.constraints} />
                        </Button>
                      </>
                    )}
                  </SplitItem>
                </Split>
              </StackItem>
            </Stack>
          </Form>
        </article>
      )}
      {editing !== index && (
        <article className={`data-type-item data-type-item-n${index}`} onDoubleClick={handleEditStatus}>
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
              <Button variant="plain" onClick={handleEditStatus} isDisabled={editing !== false}>
                <EditAltIcon />
              </Button>
              <Button variant="plain" onClick={handleDelete} isDisabled={editing !== false}>
                <TrashIcon />
              </Button>
            </FlexItem>
          </Flex>
        </article>
      )}
    </>
  );
};

export default DataTypeItem;
