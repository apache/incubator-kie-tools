/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { useState } from "react";
import { Form, FormGroup, TextInput } from "@patternfly/react-core";
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";

interface CharacteristicGeneralFormProps {
  characteristic: Characteristic | undefined;
}

export const CharacteristicGeneralForm = (props: CharacteristicGeneralFormProps) => {
  const { characteristic } = props;
  const [name, setName] = useState(characteristic?.name);
  const [reasonCode, setReasonCode] = useState(characteristic?.reasonCode);
  const [baselineScore, setBaselineScore] = useState(characteristic?.baselineScore);

  const toNumber = (value: string): number | undefined => {
    if (value === "") {
      return undefined;
    }
    const n = Number(value);
    if (isNaN(n)) {
      return undefined;
    }
    return n;
  };

  return (
    <Form>
      <FormGroup
        label="Name"
        fieldId="characteristic-form-name"
        helperText="Please provide a name for the Characteristic."
      >
        <TextInput
          type="text"
          id="characteristic-form-name"
          name="characteristic-form-name"
          aria-describedby="characteristic-form-name-helper"
          value={props.characteristic?.name}
          onChange={setName}
        />
      </FormGroup>
      <FormGroup
        label="Reason Code"
        fieldId="characteristic-form-reason-code"
        helperText="A Reason Code is mapped to a Business reason."
      >
        <TextInput
          type="text"
          id="characteristic-form-reason-code"
          name="characteristic-form-reason-code"
          value={props.characteristic?.reasonCode}
          onChange={setReasonCode}
        />
      </FormGroup>
      <FormGroup
        label="Baseline score"
        fieldId="simple-form-number"
        helperText="Helps to determine the ranking of Reason Codes."
      >
        <TextInput
          type="number"
          id="characteristic-form-baseline-score"
          name="characteristic-form-baseline-score"
          value={props.characteristic?.baselineScore}
          onChange={value => setBaselineScore(toNumber(value))}
        />
      </FormGroup>
    </Form>
  );
};
