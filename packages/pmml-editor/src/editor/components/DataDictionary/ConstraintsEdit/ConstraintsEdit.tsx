import * as React from "react";
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
  Grid,
  GridItem,
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
import { Constraints, DataType } from "../DataDictionaryContainer/DataDictionaryContainer";
import { useEffect, useState } from "react";
import "./ConstraintsEdit.scss";
import { Validated } from "../../../types";

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
  const [enums, setEnums] = useState("");
  const [validation, setValidation] = useState<{ form: Validated; fields: { [key: string]: Validated } }>({
    form: "default",
    fields: { start: "default", end: "default" }
  });

  const handleTypeChange = (event: React.MouseEvent | React.ChangeEvent, value: string) => {
    if (value !== constraintType) {
      setConstraintType(value);
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

  const handleEnumsChange = (value: string) => {
    setEnums(value);
  };

  const validateConstraints = () => {
    const isValid = { ...validation };
    isValid.form = "success";
    if (constraintType === "Range") {
      isValid.fields.start = range.start.value.trim().length === 0 ? "error" : "success";
      isValid.fields.end = range.end.value.trim().length === 0 ? "error" : "success";
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
          const data = { ...range, type: "Range" };
          onAdd(data as Constraints);
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
    setEnums("");
  };

  return (
    <section>
      <Stack hasGutter={true}>
        <StackItem>
          <TextContent>
            <Text component={TextVariants.h3}>Constraints for {dataType.name}</Text>
            <Text component={TextVariants.p}>Select the type of constraint and then fill in the required fields.</Text>
          </TextContent>
        </StackItem>

        <StackItem>
          <Form onSubmit={handleSubmit} autoComplete="off" className="constraints-form">
            <FormGroup fieldId="constraints-type" label="Constraints Type" style={{ width: 200 }}>
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
            </FormGroup>
            {validation.form === "error" && (
              <StackItem>
                <Alert variant="warning" isInline={true} title="Please check the highlighted fields." />
              </StackItem>
            )}
            {constraintType === "Range" && (
              <Card isCompact={true}>
                <CardTitle>Range Constraint</CardTitle>
                <CardBody>
                  <Stack hasGutter={true}>
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
                          <FormGroup label="Start Value*" fieldId="start-value">
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
                          <FormGroup label="End Value*" fieldId="end-value">
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
              <Card>
                <CardBody>
                  <Grid>
                    <GridItem span={8}>
                      <FormGroup label="Values" fieldId="enum-values">
                        <TextInput
                          type="text"
                          id="enum-values"
                          name="enum-values"
                          value={enums}
                          onChange={handleEnumsChange}
                          isDisabled={constraintType !== "Enumeration"}
                        />
                      </FormGroup>
                    </GridItem>
                  </Grid>
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
