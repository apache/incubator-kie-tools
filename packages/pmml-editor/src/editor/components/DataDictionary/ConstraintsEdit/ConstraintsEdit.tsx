import * as React from "react";
import {
  ActionGroup,
  Alert,
  Button,
  Card,
  CardBody,
  Checkbox,
  Form,
  FormGroup,
  Grid,
  GridItem,
  Radio,
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
import { useState } from "react";
import "./ConstraintsEdit.scss";

interface ConstraintsEditProps {
  dataType: DataType;
  onAdd: (payload: Constraints) => void;
  onCancel: () => void;
}

const ConstraintsEdit = (props: ConstraintsEditProps) => {
  const { dataType, onAdd, onCancel } = props;
  const [constraintType, setConstraintType] = useState<string>(dataType.constraints?.type ?? "");
  const rangeInitialValue =
    dataType.constraints?.type === "Range"
      ? dataType.constraints
      : {
          start: {
            value: "",
            included: false
          },
          end: {
            value: "",
            included: false
          }
        };
  const [range, setRange] = useState(rangeInitialValue);
  const [enums, setEnums] = useState("");
  const [validation, setValidation] = useState<"success" | "error" | "default">("default");

  const handleTypeChange = (checked: boolean, event: React.FormEvent<HTMLInputElement>) => {
    const { value } = event.currentTarget;
    setConstraintType(value);
  };

  const handleStartChange = (value: string) => {
    setRange({ ...range, start: { value, included: range.start.included } });
  };

  const handleEndChange = (value: string) => {
    setRange({ ...range, end: { value, included: range.end.included } });
  };
  const handleEnumsChange = (value: string) => {
    setEnums(value);
  };

  const validateConstraints = () => {
    // const isValid = constraintType.length > 0 ? "success" : "error";
    const isValid = "success";
    setValidation(isValid);
    return isValid;
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (validateConstraints() === "success") {
      switch (constraintType) {
        case "":
          onCancel();
          break;
        case "Range":
          const data = { ...range, type: "Range" };
          onAdd(data as Constraints);
          break;
      }
    }
  };
  return (
    <section>
      <Stack hasGutter={true}>
        <StackItem>
          <TextContent>
            <Text component={TextVariants.h3}>Constraints</Text>
            <Text component={TextVariants.p}>Select the type of constraint and then fill in the required fields.</Text>
          </TextContent>
        </StackItem>
        {validation === "error" && (
          <StackItem>
            <Alert variant="warning" isInline={true} title="Please select a constraint first" />
          </StackItem>
        )}
        <StackItem>
          <Form onSubmit={handleSubmit}>
            <Grid hasGutter={true}>
              <GridItem span={3}>Constraint Type</GridItem>
              <GridItem span={8}>Details</GridItem>
            </Grid>
            <Card>
              <CardBody>
                <Grid>
                  <GridItem span={3}>
                    <Radio
                      isChecked={constraintType === "Range"}
                      name={`constraint-type-range`}
                      onChange={handleTypeChange}
                      label="Range"
                      id={`constraint-type-range`}
                      value={"Range"}
                    />
                  </GridItem>
                  <GridItem span={8}>
                    <Split hasGutter={true}>
                      <SplitItem isFilled={true}>
                        <FormGroup label="Start Value" fieldId="start-value">
                          <TextInput
                            type="text"
                            id="start-value"
                            name="start-value"
                            value={range.start.value}
                            isDisabled={constraintType !== "Range"}
                            onChange={handleStartChange}
                          />
                        </FormGroup>
                        <FormGroup fieldId="start-included" className="constraints__include-range">
                          <Checkbox
                            label="Include Start Value"
                            aria-label="Include Start Value"
                            id="start-included"
                            isDisabled={constraintType !== "Range"}
                          />
                        </FormGroup>
                      </SplitItem>
                      <SplitItem isFilled={true}>
                        <FormGroup label="End Value" fieldId="end-value">
                          <TextInput
                            type="text"
                            id="end-value"
                            name="end-value"
                            value={range.end.value}
                            onChange={handleEndChange}
                            isDisabled={constraintType !== "Range"}
                          />
                        </FormGroup>
                        <FormGroup fieldId="end-included" className="constraints__include-range">
                          <Checkbox
                            label="Include End Value"
                            aria-label="Include End Value"
                            id="end-included"
                            isDisabled={constraintType !== "Range"}
                          />
                        </FormGroup>
                      </SplitItem>
                    </Split>
                  </GridItem>
                </Grid>
              </CardBody>
            </Card>
            <Card>
              <CardBody>
                <Grid>
                  <GridItem span={3}>
                    <Radio
                      isChecked={constraintType === "Enumeration"}
                      name={`constraint-type-enum`}
                      onChange={handleTypeChange}
                      label="Enumeration"
                      id={`constraint-type-enum`}
                      value={"Enumeration"}
                    />
                  </GridItem>
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
            <ActionGroup>
              <Button variant="primary" type="submit">
                Done
              </Button>
            </ActionGroup>
          </Form>
          {/*<Form onSubmit={handleSubmit} style={{ gridGap: 0 }}>*/}
          {/*  <FormGroup*/}
          {/*    label="Data Types"*/}
          {/*    fieldId="data-types"*/}
          {/*    isRequired={true}*/}
          {/*    validated={inputValidation}*/}
          {/*    helperTextInvalid={"Please enter at least one Data Type Name"}*/}
          {/*  >*/}
          {/*    <TextArea*/}
          {/*      className="data-dictionary__multiple-data-types"*/}
          {/*      value={input}*/}
          {/*      onChange={handleInputChange}*/}
          {/*      name="data-types"*/}
          {/*      isRequired={true}*/}
          {/*      id="data-types"*/}
          {/*      placeholder={"First Data Type\nSecond Data Type\n..."}*/}
          {/*    />*/}
          {/*  </FormGroup>*/}
          {/*  <ActionGroup>*/}
          {/*    <Button variant="primary" type="submit">*/}
          {/*      Add Them*/}
          {/*    </Button>*/}
          {/*    <Button variant="link" onClick={() => onCancel()}>*/}
          {/*      Cancel*/}
          {/*    </Button>*/}
          {/*  </ActionGroup>*/}
          {/*</Form>*/}
        </StackItem>
      </Stack>
      {/*<Button variant="link" iconPosition="right" onClick={onAdd}>*/}
      {/*  Done*/}
      {/*</Button>*/}
    </section>
  );
};

export default ConstraintsEdit;
