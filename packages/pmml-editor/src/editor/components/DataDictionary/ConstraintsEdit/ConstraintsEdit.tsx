import * as React from "react";
import { useEffect, useState } from "react";
import { v4 as uuid } from "uuid";
import {
  ActionGroup,
  Alert,
  Button,
  Card,
  CardBody,
  CardTitle,
  Checkbox,
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
  TextInput,
  TextVariants
} from "@patternfly/react-core";
import ConstraintsEnumEdit from "../ConstraintsEnumEdit/ConstraintsEnumEdit";
import { Constraints, DataType, EnumConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import { Validated } from "../../../types";
import "./ConstraintsEdit.scss";

interface ConstraintsEditProps {
  dataType: DataType;
  onAdd: (payload: Constraints) => void;
  onDelete: () => void;
}

const ConstraintsEdit = (props: ConstraintsEditProps) => {
  const { dataType, onAdd, onDelete } = props;
  const [constraintType, setConstraintType] = useState<string>(dataType.constraints?.type ?? "");
  const [typeIsOpen, setTypeIsOpen] = useState(false);
  const constraintsTypes = [{ value: "", isPlaceholder: true }, { value: "Range" }, { value: "Enumeration" }];
  const rangeEmptyValue = {
    start: {
      value: "",
      included: true
    },
    end: {
      value: "",
      included: true
    }
  };
  const rangeInitialValue = dataType.constraints?.type === "Range" ? dataType.constraints : rangeEmptyValue;
  const [range, setRange] = useState(rangeInitialValue);
  const [enums, setEnums] = useState(
    dataType.constraints?.type === "Enumeration" ? dataType.constraints.value : [{ value: "", id: uuid() }]
  );
  const [validation, setValidation] = useState<FormValidation>({
    form: "default",
    fields: { start: "default", end: "default", enums: "default" }
  });

  const handleTypeChange = (event: React.MouseEvent | React.ChangeEvent, value: string) => {
    if (value !== constraintType) {
      setConstraintType(value);
      setValidation({ form: "default", fields: { start: "default", end: "default", enums: "default" } });
    }
    setTypeIsOpen(false);
  };

  const handleTypeOpening = () => {
    setTypeIsOpen(!typeIsOpen);
  };

  const handleRangeChange = (value: string | boolean, event: React.FormEvent<HTMLInputElement>) => {
    switch ((event.target as HTMLInputElement).id) {
      case "start-value":
        setRange({ ...range, start: { ...range.start, value: value as string } });
        break;
      case "start-included":
        setRange({ ...range, start: { ...range.start, included: value as boolean } });
        break;
      case "end-value":
        setRange({ ...range, end: { ...range.end, value: value as string } });
        break;
      case "end-included":
        setRange({ ...range, end: { ...range.end, included: value as boolean } });
        break;
      default:
        break;
    }
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
    if (constraintType === "Range") {
      isValid.fields.start = range.start.value.trim().length === 0 ? "error" : "success";
      isValid.fields.end = range.end.value.trim().length === 0 ? "error" : "success";
    }
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
      switch (constraintType) {
        case "":
          onDelete();
          break;
        case "Range":
          const rangeData = { ...range, type: "Range" };
          onAdd(rangeData as Constraints);
          break;
        case "Enumeration":
          onAdd({ type: "Enumeration", value: enums.filter(item => item.value.trim().length > 0) } as Constraints);
          break;
      }
    }
  };

  useEffect(() => {
    if (constraintType === "Range") {
      document.querySelector<HTMLInputElement>(`#start-value`)?.focus();
    }
  }, [constraintType]);

  const clearConstraints = () => {
    setRange(rangeEmptyValue);
    setConstraintType("");
    setEnums([]);
    setValidation({ form: "default", fields: {} });
  };

  return (
    <section>
      <Stack hasGutter={true}>
        <StackItem>
          <TextContent>
            <Text component={TextVariants.h3}>Constraints for {dataType.name}</Text>
          </TextContent>
        </StackItem>

        <StackItem>
          <Form onSubmit={handleSubmit} autoComplete="off" className="constraints-form">
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
            {constraintType === "Range" && (
              <Card isCompact={true}>
                <CardTitle>Range Constraint</CardTitle>
                <CardBody>
                  <Stack hasGutter={true}>
                    {validation.form === "error" && (
                      <StackItem>
                        <Alert variant="danger" isInline={true} title="Please enter both start and end value." />
                      </StackItem>
                    )}
                    <StackItem>
                      <TextContent>
                        <Text component={TextVariants.p}>
                          A range has a start and an end value, both field values are required (*). <br />
                          The value at each end of the range may be included or excluded from the range definition.
                          <br />
                          If the check box is cleared, the start or end value is excluded.
                        </Text>
                      </TextContent>
                    </StackItem>
                    <StackItem>
                      <Split hasGutter={true}>
                        <SplitItem style={{ width: 320 }}>
                          <FormGroup label="Start Value" fieldId="start-value" isRequired={true}>
                            <TextInput
                              type="text"
                              id="start-value"
                              name="start-value"
                              value={range.start.value}
                              onChange={handleRangeChange}
                              validated={validation.fields.start as Validated}
                              tabIndex={20}
                            />
                          </FormGroup>
                          <FormGroup fieldId="start-included" className="constraints__include-range">
                            <Checkbox
                              label="Include Start Value"
                              aria-label="Include Start Value"
                              id="start-included"
                              isChecked={range.start.included}
                              onChange={handleRangeChange}
                              tabIndex={22}
                            />
                          </FormGroup>
                        </SplitItem>
                        <SplitItem style={{ width: 320 }}>
                          <FormGroup label="End Value" fieldId="end-value" isRequired={true}>
                            <TextInput
                              type="text"
                              id="end-value"
                              name="end-value"
                              value={range.end.value}
                              onChange={handleRangeChange}
                              validated={validation.fields.end as Validated}
                              tabIndex={21}
                            />
                          </FormGroup>
                          <FormGroup fieldId="end-included" className="constraints__include-range">
                            <Checkbox
                              label="Include End Value"
                              aria-label="Include End Value"
                              id="end-included"
                              isChecked={range.end.included}
                              onChange={handleRangeChange}
                              tabIndex={23}
                            />
                          </FormGroup>
                        </SplitItem>
                      </Split>
                    </StackItem>
                  </Stack>
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
            <ActionGroup>
              <Button variant="primary" type="submit">
                Done
              </Button>
              <Button variant="secondary" type="button" onClick={clearConstraints}>
                Clear Constraints
              </Button>
            </ActionGroup>
          </Form>
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
