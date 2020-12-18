import * as React from "react";
import { useEffect, useState } from "react";
import { v4 as uuid } from "uuid";
import { omit, isEqual } from "lodash";
import {
  Button,
  Card,
  CardBody,
  CardTitle,
  Form,
  FormGroup,
  Select,
  SelectOption,
  SelectVariant,
  Split,
  SplitItem,
  Stack,
  StackItem,
  Text,
  TextContent,
  TextVariants
} from "@patternfly/react-core";
import { ArrowAltCircleLeftIcon } from "@patternfly/react-icons";
import ConstraintsEnumEdit from "../ConstraintsEnumEdit/ConstraintsEnumEdit";
import { DDDataField, EnumConstraint, RangeConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import { Validated } from "../../../types";
import "./ConstraintsEdit.scss";
import ConstraintsRangeEdit from "../ConstraintsRangeEdit/ConstraintsRangeEdit";

interface ConstraintsEditProps {
  dataType: DDDataField;
  onSave: (payload: DDDataField) => void;
  onClose: () => void;
}

const ConstraintsEdit = (props: ConstraintsEditProps) => {
  const { dataType, onSave, onClose } = props;
  const [dataTypeState, setDataTypeState] = useState(dataType);
  const [constraintType, setConstraintType] = useState<string>(dataType.constraints?.type ?? "");
  const [typeIsOpen, setTypeIsOpen] = useState(false);
  const constraintsTypes = [{ value: "", isPlaceholder: true }, { value: "Range" }, { value: "Enumeration" }];
  const [range, setRange] = useState<RangeConstraint[] | undefined>(
    dataType.constraints?.type === "Range" ? dataType.constraints.value : undefined
  );
  const [enums, setEnums] = useState(
    dataType.constraints?.type === "Enumeration" ? dataType.constraints.value : [{ value: "", id: uuid() }]
  );
  const [validation, setValidation] = useState<FormValidation>({
    form: "default",
    fields: { start: "default", end: "default", enums: "default" }
  });

  useEffect(() => {
    if (!isEqual(omit(dataType, "constraints"), omit(dataTypeState, "constraints"))) {
      onClose();
    } else {
      setDataTypeState(dataType);
    }
  }, [dataType, dataTypeState]);

  useEffect(() => {
    setConstraintType(dataType.constraints?.type ?? "");
    if (dataType.constraints?.type === "Range") {
      setRange(dataType.constraints.value);
    }
    if (dataType.constraints?.type === "Enumeration") {
      setEnums(dataType.constraints.value);
    }
  }, [dataType.constraints, setConstraintType, setRange, setEnums]);

  const handleTypeChange = (event: React.MouseEvent | React.ChangeEvent, value: string) => {
    if (value !== constraintType) {
      setConstraintType(value);
      setValidation({ form: "default", fields: { start: "default", end: "default", enums: "default" } });
      if (value === "Range") {
        onSave({
          ...dataType,
          constraints: {
            type: "Range",
            value: [
              {
                start: {
                  value: "1",
                  included: true
                },
                end: {
                  value: "10",
                  included: true
                }
              }
            ]
          }
        });
      }
    }
    setTypeIsOpen(false);
  };

  const handleTypeOpening = () => {
    setTypeIsOpen(!typeIsOpen);
  };

  const handleRangeChange = (rangeValue: RangeConstraint) => {
    onSave({
      ...dataType,
      constraints: {
        type: "Range",
        value: [rangeValue]
      }
    });
  };

  const handleEnumsChange = (value: string, index: number) => {
    const updatedEnums = [...enums];
    updatedEnums[index].value = value;
    setEnums(updatedEnums);
  };

  const handleEnumsDelete = (index: number) => {
    const updatedEnums = [...enums];
    updatedEnums.splice(index, 1);
    setEnums(updatedEnums);
  };

  const handleAddEnum = () => {
    const updatedEnums = [...enums];
    updatedEnums.push({ value: "", id: uuid() });
    setEnums(updatedEnums);
  };

  const handleEnumSort = (oldIndex: number, newIndex: number) => {
    const newOrder = reorderArray(enums, oldIndex, newIndex);
    setEnums(newOrder);
  };

  const validateConstraints = () => {
    const isValid = { ...validation };
    isValid.form = "success";
    // if (constraintType === "Range") {
    //   isValid.fields.start = range?.start.value.trim().length === 0 ? "error" : "success";
    //   isValid.fields.end = range?.end.value.trim().length === 0 ? "error" : "success";
    // }
    if (constraintType === "Enumeration") {
      const oneEnum = enums.find(item => item.value.trim().length > 0);
      isValid.fields.enums = oneEnum === undefined ? "error" : "success";
    }
    for (const fieldsKey in isValid.fields) {
      if (isValid.fields[fieldsKey] === "error") {
        isValid.form = "error";
      }
    }
    setValidation(isValid);
    return isValid.form;
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (validateConstraints() === "success") {
      onClose();
    }
  };

  const clearConstraints = () => {
    const updatedDataType = { ...dataType };
    delete updatedDataType.constraints;
    onSave(updatedDataType);
    setValidation({ form: "default", fields: {} });
  };

  return (
    <section>
      <Stack hasGutter={true}>
        <StackItem>
          <TextContent>
            <Text component={TextVariants.h3}>
              <Button variant="link" isInline={true} onClick={event => handleSubmit(event)}>
                {dataType.name}
              </Button>
              &nbsp;/&nbsp;Constraints
            </Text>
          </TextContent>
        </StackItem>
        <StackItem>
          <section className="constraints__form">
            <Form onSubmit={handleSubmit} autoComplete="off">
              <FormGroup
                fieldId="constraints-type"
                label="Constraints Type"
                helperText="Select the type of constraint and then fill in the required fields."
              >
                {/*PF 2020.08 has a bug setting width of a Select*/}
                <div style={{ width: 300 }}>
                  <Select
                    id="constraints-type"
                    variant={SelectVariant.single}
                    aria-label="Select Constraint Type"
                    onToggle={handleTypeOpening}
                    onSelect={handleTypeChange}
                    selections={constraintType}
                    isOpen={typeIsOpen}
                    placeholderText={"Select a type"}
                  >
                    {constraintsTypes.map((item, index) => (
                      <SelectOption key={index} value={item.value}>
                        {item.isPlaceholder ? "Select a type" : item.value}
                      </SelectOption>
                    ))}
                  </Select>
                </div>
              </FormGroup>
              {/*range constraints editing to be moved to a dedicated component*/}
              {constraintType === "Range" && range !== undefined && (
                <Card isCompact={true}>
                  <CardTitle>Range Constraint</CardTitle>
                  <CardBody>
                    {/*<ConstraintsRangeEdit*/}
                    {/*  range={range}*/}
                    {/*  onChange={handleRangeChange}*/}
                    {/*  typeOfData={dataType.type}*/}
                    {/*  validation={validation}*/}
                    {/*/>*/}
                  </CardBody>
                </Card>
              )}
              {constraintType === "Enumeration" && (
                <Card isCompact={true}>
                  <CardTitle>Enumerations List</CardTitle>
                  <CardBody>
                    <ConstraintsEnumEdit
                      enumerations={enums}
                      onChange={handleEnumsChange}
                      onDelete={handleEnumsDelete}
                      onAdd={handleAddEnum}
                      onSort={handleEnumSort}
                      validation={validation}
                    />
                  </CardBody>
                </Card>
              )}
            </Form>
          </section>
        </StackItem>
        <StackItem>
          <Split hasGutter={true}>
            <SplitItem>
              <Button
                variant="primary"
                type="button"
                onClick={handleSubmit}
                icon={<ArrowAltCircleLeftIcon />}
                iconPosition="left"
              >
                Done
              </Button>
            </SplitItem>
            <SplitItem>
              <Button variant="secondary" type="button" onClick={clearConstraints}>
                Clear Constraints
              </Button>
            </SplitItem>
          </Split>
        </StackItem>
      </Stack>
    </section>
  );
};

export default ConstraintsEdit;

export interface FormValidation {
  form: Validated;
  fields: { [key: string]: Validated };
}

const reorderArray = (list: EnumConstraint[], startIndex: number, endIndex: number) => {
  const result = [...list];
  const [removed] = result.splice(startIndex, 1);
  result.splice(endIndex, 0, removed);

  return result;
};
