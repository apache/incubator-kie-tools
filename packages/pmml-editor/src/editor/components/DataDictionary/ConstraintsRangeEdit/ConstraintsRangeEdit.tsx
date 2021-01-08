/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import * as React from "react";
import {
  Alert,
  Checkbox,
  FormGroup,
  Split,
  SplitItem,
  Stack,
  StackItem,
  Text,
  TextContent,
  TextInput,
  TextInputProps,
  TextVariants
} from "@patternfly/react-core";
import { Validated } from "../../../types";
import { FormValidation } from "../ConstraintsEdit/ConstraintsEdit";
import { DDDataField, RangeConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import { useEffect, useState } from "react";

interface ConstraintsRangeEditProps {
  range: RangeConstraint;
  onChange: (rangeValue: RangeConstraint) => void;
  typeOfData: DDDataField["type"];
  validation: FormValidation;
}

const ConstraintsRangeEdit = (props: ConstraintsRangeEditProps) => {
  const { range, onChange, typeOfData, validation } = props;
  const [rangeValues, setRangeValues] = useState(range);
  const [submitChanges, setSubmitChanges] = useState(false);

  const calcInputType = () => {
    if (["integer", "float", "double"].includes(typeOfData)) {
      return { type: "number" as TextInputProps["type"] };
    }
    return { type: "text" as TextInputProps["type"] };
  };

  const handleRangeChange = (value: string | boolean, event: React.FormEvent<HTMLInputElement>) => {
    switch ((event.target as HTMLInputElement).id) {
      case "start-value":
        setRangeValues({ ...rangeValues, start: { ...rangeValues.start, value: value as string } });
        break;
      case "start-included":
        setRangeValues({ ...rangeValues, start: { ...rangeValues.start, included: value as boolean } });
        break;
      case "end-value":
        setRangeValues({ ...rangeValues, end: { ...rangeValues.end, value: value as string } });
        break;
      case "end-included":
        setRangeValues({ ...rangeValues, end: { ...rangeValues.end, included: value as boolean } });
        break;
      default:
        break;
    }
  };

  const requestCommit = () => {
    setSubmitChanges(true);
  };

  useEffect(() => {
    if (submitChanges) {
      onChange(rangeValues);
      setSubmitChanges(false);
    }
  }, [submitChanges, setSubmitChanges, rangeValues]);

  useEffect(() => {
    setRangeValues(range);
  }, [range, setRangeValues]);

  return (
    <section>
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
                  {...calcInputType()}
                  id="start-value"
                  name="start-value"
                  value={rangeValues.start.value}
                  onChange={handleRangeChange}
                  onBlur={requestCommit}
                  validated={validation.fields.start as Validated}
                  tabIndex={20}
                />
              </FormGroup>
              <FormGroup fieldId="start-included" className="constraints__include-range">
                <Checkbox
                  label="Include Start Value"
                  aria-label="Include Start Value"
                  id="start-included"
                  isChecked={rangeValues.start.included}
                  onChange={handleRangeChange}
                  onClick={requestCommit}
                  tabIndex={22}
                />
              </FormGroup>
            </SplitItem>
            <SplitItem style={{ width: 320 }}>
              <FormGroup label="End Value" fieldId="end-value" isRequired={true}>
                <TextInput
                  {...calcInputType()}
                  id="end-value"
                  name="end-value"
                  value={rangeValues.end.value}
                  onChange={handleRangeChange}
                  onBlur={requestCommit}
                  validated={validation.fields.end as Validated}
                  tabIndex={21}
                />
              </FormGroup>
              <FormGroup fieldId="end-included" className="constraints__include-range">
                <Checkbox
                  label="Include End Value"
                  aria-label="Include End Value"
                  id="end-included"
                  isChecked={rangeValues.end.included}
                  onChange={handleRangeChange}
                  onClick={requestCommit}
                  tabIndex={23}
                />
              </FormGroup>
            </SplitItem>
          </Split>
        </StackItem>
      </Stack>
    </section>
  );
};

export default ConstraintsRangeEdit;
