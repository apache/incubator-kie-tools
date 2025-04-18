/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import { useEffect, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { ActionGroup, Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import "./OutputsBatchAdd.scss";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface OutputsBatchAddProps {
  onAdd: (types: string) => void;
  onCancel: () => void;
}

const OutputsBatchAdd = ({ onAdd, onCancel }: OutputsBatchAddProps) => {
  const [input, setInput] = useState("");
  const [inputValidation, setInputValidation] = useState<"success" | "error" | "default">("default");

  useEffect(() => {
    document.querySelector<HTMLInputElement>(`#outputs`)?.focus();
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
            <Text component={TextVariants.h3}>Add Multiple Output Fields</Text>
            <Text component={TextVariants.p}>
              You can add multiple outputs by entering their names below. Add them one per line.
              <br />
              They will be created with the default type of <em>String</em>. You will be able to edit them later.
            </Text>
          </TextContent>
        </StackItem>
        <StackItem>
          <Form onSubmit={handleSubmit} style={{ gridGap: 0 }}>
            <FormGroup label="Outputs" fieldId="outputs" isRequired={true}>
              <TextArea
                className="outputs-container__multiple-outputs"
                value={input}
                onChange={(_event, value: string) => handleInputChange(value)}
                name="outputs"
                isRequired={true}
                id="outputs"
                placeholder={"First Output\nSecond Output\n..."}
              />
              {inputValidation === "error" ? (
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem variant="error">Please enter at least one Output name</HelperTextItem>
                  </HelperText>
                </FormHelperText>
              ) : (
                <FormHelperText>
                  <HelperText>
                    <HelperTextItem variant="default"></HelperTextItem>
                  </HelperText>
                </FormHelperText>
              )}
            </FormGroup>
            <ActionGroup>
              <Button variant="primary" type="submit">
                Add Them
              </Button>
              <Button variant="link" onClick={() => onCancel()}>
                Never mind
              </Button>
            </ActionGroup>
          </Form>
        </StackItem>
      </Stack>
    </section>
  );
};

export default OutputsBatchAdd;
