import * as React from "react";
import { useEffect, useState } from "react";
import {
  ActionGroup,
  Button,
  Form,
  FormGroup,
  Stack,
  StackItem,
  Text,
  TextArea,
  TextContent,
  TextVariants
} from "@patternfly/react-core";
import { AngleLeftIcon } from "@patternfly/react-icons";
import "./MultipleDataTypesAdd.scss";

interface MultipleDataTypeAddProps {
  onAdd: (types: string) => void;
  onCancel: () => void;
}

const MultipleDataTypeAdd = ({ onAdd, onCancel }: MultipleDataTypeAddProps) => {
  const [input, setInput] = useState("");
  const [inputValidation, setInputValidation] = useState<"success" | "error" | "default">("default");

  useEffect(() => {
    document.querySelector<HTMLInputElement>(`#data-types`)?.focus();
  }, []);

  const handleInputChange = (value: string) => {
    setInput(value);
  };

  const validateInput = () => {
    const validation = input.trim().length > 0 ? "success" : "error";
    setInputValidation(validation);
    return validation;
  };

  const handleSubmit = (event: React.FormEvent) => {
    if (validateInput() === "success") {
      onAdd(input);
    }
    event.preventDefault();
  };

  return (
    <section>
      <Stack hasGutter={true}>
        <StackItem>
          <TextContent>
            <Text component={TextVariants.h3}>Add Multiple Data Types</Text>
            <Text component={TextVariants.p}>
              You can add multiple data types by entering their names below. Add them one per line.
              <br />
              They will be created with the default type of <em>String</em>. You will be able to edit them later.
            </Text>
          </TextContent>
        </StackItem>
        <StackItem>
          <Form onSubmit={handleSubmit} style={{ gridGap: 0 }}>
            <FormGroup
              label="Data Types"
              fieldId="data-types"
              isRequired={true}
              validated={inputValidation}
              helperTextInvalid={"Please enter at least one Data Type Name"}
            >
              <TextArea
                className="data-dictionary__multiple-data-types"
                value={input}
                onChange={handleInputChange}
                name="data-types"
                isRequired={true}
                id="data-types"
                placeholder={"First Data Type\nSecond Data Type\n..."}
              />
            </FormGroup>
            <ActionGroup>
              <Button variant="primary" type="submit">
                Add Them
              </Button>
              <Button variant="link" onClick={() => onCancel()} icon={<AngleLeftIcon />} iconPosition="left">
                Back
              </Button>
            </ActionGroup>
          </Form>
        </StackItem>
      </Stack>
    </section>
  );
};

export default MultipleDataTypeAdd;
