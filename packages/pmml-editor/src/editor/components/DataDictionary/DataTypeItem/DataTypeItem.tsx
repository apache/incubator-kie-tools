import * as React from "react";
import { BaseSyntheticEvent, useEffect, useMemo, useState } from "react";
import useOnclickOutside from "react-cool-onclickoutside";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import {
  Select,
  SelectOption,
  SelectOptionObject,
  SelectVariant,
} from "@patternfly/react-core/dist/js/components/Select";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-alt-circle-right-icon";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./DataTypeItem.scss";
import ConstraintsLabel from "../ConstraintsLabel/ConstraintsLabel";
import { Validated } from "../../../types";
import PropertiesLabels from "../PropertiesLabels/PropertiesLabels";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { ValidationIndicator } from "../../EditorCore/atoms";

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
    onOutsideClick,
  } = props;
  const [name, setName] = useState(dataType.name);
  const [typeSelection, setTypeSelection] = useState<DDDataField["type"]>(dataType.type);
  const [isTypeSelectOpen, setIsTypeSelectOpen] = useState(false);
  const typeOptions = [
    { value: "string" },
    { value: "integer" },
    { value: "float" },
    { value: "double" },
    { value: "boolean" },
  ];
  const [optypeSelection, setOptypeSelection] = useState(dataType.optype);
  const [isOptypeSelectOpen, setIsOptypeSelectOpen] = useState(false);
  const optypeOptions = [{ value: "categorical" }, { value: "ordinal" }, { value: "continuous" }];
  const [validation, setValidation] = useState<Validated>("default");

  const ref = useOnclickOutside(
    () => {
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

  const typeSelect = (event: React.MouseEvent | React.ChangeEvent, value: string | SelectOptionObject) => {
    if (value !== typeSelection) {
      setTypeSelection(value as DDDataField["type"]);
      setIsTypeSelectOpen(false);
      onSave({ ...dataType, type: value as DDDataField["type"] }, index);
    }
  };

  const optypeToggle = (isOpen: boolean) => {
    setIsOptypeSelectOpen(isOpen);
  };

  const optypeSelect = (event: React.MouseEvent | React.ChangeEvent, value: string | SelectOptionObject) => {
    if (value !== optypeSelection) {
      setOptypeSelection(value as DDDataField["optype"]);
      setIsOptypeSelectOpen(false);
      onSave({ ...dataType, optype: value as DDDataField["optype"] }, index);
    }
  };

  const handleEditStatus = (event: BaseSyntheticEvent) => {
    event.preventDefault();
    event.stopPropagation();
    onEdit?.(index);
  };

  const handleSave = (event?: React.FormEvent<HTMLFormElement>) => {
    event?.preventDefault();
    onSave({ name: name.trim(), type: typeSelection, optype: optypeSelection }, index);
  };

  const handleNameSave = () => {
    if (validation === "error") {
      setName(dataType.name);
      setValidation("default");
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

  const handlePropertiesDelete = (updatedDataType: DDDataField, updateIndex: number) => {
    onSave(updatedDataType, updateIndex);
  };

  useEffect(() => {
    if (editingIndex === index) {
      const input = document.querySelector<HTMLInputElement>(`.data-type-item-n${index} #name`);
      input?.focus();
      if (name.startsWith("New Data Type")) {
        input?.select();
      }
    } else {
      if (validation !== "success") {
        setName(dataType.name);
        setValidation("default");
      }
    }
  }, [editingIndex]);

  useEffect(() => {
    setName(dataType.name);
    setTypeSelection(dataType.type);
    setOptypeSelection(dataType.optype);
  }, [dataType]);

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forDataDictionary().forDataField(index).build()),
    [index, dataType]
  );

  return (
    <article
      className={`editable-item ${editingIndex === index ? "editable-item--editing" : ""} data-type-item-n${index}`}
    >
      {editingIndex === index && (
        <section
          className={"editable-item__inner"}
          ref={ref}
          tabIndex={0}
          onKeyDown={(event) => {
            if (event.key === "Escape") {
              onOutsideClick();
            }
          }}
        >
          <Form
            onSubmit={(e) => {
              e.stopPropagation();
              e.preventDefault();
            }}
          >
            <Split hasGutter={true}>
              <SplitItem>
                <Stack hasGutter={true}>
                  <StackItem>
                    <Split hasGutter={true}>
                      <SplitItem>
                        <FormGroup
                          fieldId="name"
                          label="Name"
                          helperTextInvalid="Name is mandatory and must be unique"
                          helperTextInvalidIcon={<ExclamationCircleIcon />}
                          validated={validation}
                          style={{ width: 280 }}
                          isRequired={true}
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
                        <FormGroup fieldId="type" label="Type" isRequired={true}>
                          <Select
                            id="type"
                            variant={SelectVariant.single}
                            aria-label="Select Input Type"
                            onToggle={typeToggle}
                            onSelect={typeSelect}
                            selections={typeSelection}
                            isOpen={isTypeSelectOpen}
                            placeholder="Type"
                            className="data-type-item__type-select"
                            menuAppendTo={"parent"}
                          >
                            {typeOptions.map((option, optionIndex) => (
                              <SelectOption
                                key={optionIndex}
                                value={option.value}
                                className="ignore-onclickoutside data-type-item__type-select__option"
                              />
                            ))}
                          </Select>
                        </FormGroup>
                      </SplitItem>
                      <SplitItem>
                        <FormGroup fieldId="optype" label="Op Type" isRequired={true}>
                          <Select
                            id="optype"
                            variant={SelectVariant.single}
                            aria-label="Select Op Type"
                            onToggle={optypeToggle}
                            onSelect={optypeSelect}
                            selections={optypeSelection}
                            isOpen={isOptypeSelectOpen}
                            placeholder="Op Type"
                            className="data-type-item__type-select"
                            menuAppendTo={"parent"}
                          >
                            {optypeOptions.map((option, optionIndex) => (
                              <SelectOption
                                key={optionIndex}
                                value={option.value}
                                className="ignore-onclickoutside data-type-item__type-select__option"
                              />
                            ))}
                          </Select>
                        </FormGroup>
                      </SplitItem>
                      <SplitItem isFilled={true}>&nbsp;</SplitItem>
                    </Split>
                  </StackItem>
                  <StackItem>
                    <Split hasGutter={true}>
                      <SplitItem>
                        <PropertiesLabels
                          dataType={dataType}
                          editingIndex={editingIndex}
                          onPropertyDelete={handlePropertiesDelete}
                        />
                        <ConstraintsLabel
                          dataType={dataType}
                          dataTypeIndex={index}
                          editMode={true}
                          onConstraintsDelete={handleConstraintsDelete}
                        />
                        <Label
                          variant="outline"
                          color="cyan"
                          href="#"
                          icon={<ArrowAltCircleRightIcon />}
                          onClick={(event) => {
                            event.preventDefault();
                            handleConstraints();
                          }}
                        >
                          Edit Properties
                        </Label>
                      </SplitItem>
                    </Split>
                  </StackItem>
                </Stack>
              </SplitItem>
            </Split>
          </Form>
        </section>
      )}
      {editingIndex !== index && (
        <section
          className={"editable-item__inner"}
          tabIndex={0}
          onClick={(event) => handleEditStatus(event)}
          onKeyDown={(event) => {
            if (event.key === "Enter") {
              handleEditStatus(event);
            }
          }}
        >
          <Split hasGutter={true}>
            {validations.length > 0 && (
              <SplitItem>
                <Flex
                  alignItems={{ default: "alignItemsCenter" }}
                  justifyContent={{ default: "justifyContentCenter" }}
                  style={{ height: "100%" }}
                >
                  <FlexItem>
                    <ValidationIndicator validations={validations} />
                  </FlexItem>
                </Flex>
              </SplitItem>
            )}
            <SplitItem>
              <span className="data-type-item__name">{name}</span>
            </SplitItem>
            <SplitItem isFilled={true}>
              <Label color="blue" className="data-type-item__type-label">
                {typeSelection}
              </Label>{" "}
              <Label color="blue" className="data-type-item__type-label">
                {optypeSelection}
              </Label>{" "}
              <PropertiesLabels dataType={dataType} />
              <ConstraintsLabel dataType={dataType} dataTypeIndex={index} />
            </SplitItem>
            <SplitItem>
              <Button variant="plain" onClick={handleDelete}>
                <TrashIcon />
              </Button>
            </SplitItem>
          </Split>
        </section>
      )}
    </article>
  );
};

export default DataTypeItem;
