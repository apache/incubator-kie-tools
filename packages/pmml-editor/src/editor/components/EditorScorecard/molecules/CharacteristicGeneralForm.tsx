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
import { useMemo, useState } from "react";
import { Form, FormGroup, TextInput } from "@patternfly/react-core";
import { GenericTextInput } from "../atoms";

interface CharacteristicGeneralFormProps {
  index: number | undefined;
  name: string | undefined;
  reasonCode: string | undefined;
  baselineScore: number | undefined;
  validateCharacteristicName: (index: number | undefined, name: string | undefined) => boolean;
}

export const CharacteristicGeneralForm = (props: CharacteristicGeneralFormProps) => {
  const { index } = props;

  const _name = props.name;
  const [name, setName] = useState({
    valid: props.validateCharacteristicName(index, _name),
    value: props.name
  });
  const [reasonCode, setReasonCode] = useState(props.reasonCode);
  const [baselineScore, setBaselineScore] = useState(props.baselineScore);

  const nameEditor = useMemo(
    () => (
      <GenericTextInput
        id="characteristic-form-name"
        aria-describedby="characteristic-form-name-helper"
        value={name.value ?? ""}
        valid={name.valid}
        onChange={_value =>
          setName({
            value: _value,
            valid: props.validateCharacteristicName(index, _value)
          })
        }
      />
    ),
    [name]
  );

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
        fieldId="characteristic-form-name-helper"
        helperText="Please provide a name for the Characteristic."
      >
        {nameEditor}
      </FormGroup>
      <FormGroup
        label="Reason Code"
        fieldId="characteristic-form-reason-code-helper"
        helperText="A Reason Code is mapped to a Business reason."
      >
        <TextInput
          type="text"
          id="characteristic-form-reason-code"
          name="characteristic-form-reason-code"
          aria-describedby="characteristic-form-reason-code-helper"
          value={reasonCode}
          onChange={e => setReasonCode(e)}
        />
      </FormGroup>
      <FormGroup
        label="Baseline score"
        fieldId="characteristic-form-baseline-score-helper"
        helperText="Helps to determine the ranking of Reason Codes."
      >
        <TextInput
          type="number"
          id="characteristic-form-baseline-score"
          name="characteristic-form-baseline-score"
          aria-describedby="characteristic-form-baseline-score-helper"
          value={baselineScore}
          onChange={e => setBaselineScore(toNumber(e))}
        />
      </FormGroup>
    </Form>
  );
};
