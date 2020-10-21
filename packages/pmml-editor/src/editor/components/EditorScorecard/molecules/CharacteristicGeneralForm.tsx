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
import { createRef, useEffect } from "react";
import { Form, FormGroup, TextInput } from "@patternfly/react-core";
import { IndexedCharacteristic } from "../organisms";

interface CharacteristicGeneralFormProps {
  characteristic: IndexedCharacteristic | undefined;
}

export const CharacteristicGeneralForm = (props: CharacteristicGeneralFormProps) => {
  const { characteristic } = props;

  const nameRef = createRef<HTMLInputElement>();
  const reasonCodeRef = createRef<HTMLInputElement>();
  const baselineScoreRef = createRef<HTMLInputElement>();

  useEffect(() => {
    if (nameRef.current) {
      nameRef.current.value = characteristic?.characteristic.name ?? "";
    }
    if (reasonCodeRef.current) {
      reasonCodeRef.current.value = characteristic?.characteristic.reasonCode ?? "";
    }
    if (baselineScoreRef.current) {
      baselineScoreRef.current.value = characteristic?.characteristic.baselineScore?.toString() ?? "";
    }
  });

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
        <TextInput
          type="text"
          id="characteristic-form-name"
          ref={nameRef}
          name="characteristic-form-name"
          aria-describedby="characteristic-form-name-helper"
        />
      </FormGroup>
      <FormGroup
        label="Reason Code"
        fieldId="characteristic-form-reason-code-helper"
        helperText="A Reason Code is mapped to a Business reason."
      >
        <TextInput
          type="text"
          id="characteristic-form-reason-code"
          ref={reasonCodeRef}
          name="characteristic-form-reason-code"
          aria-describedby="characteristic-form-reason-code-helper"
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
          ref={baselineScoreRef}
          name="characteristic-form-baseline-score"
          aria-describedby="characteristic-form-baseline-score-helper"
        />
      </FormGroup>
    </Form>
  );
};
