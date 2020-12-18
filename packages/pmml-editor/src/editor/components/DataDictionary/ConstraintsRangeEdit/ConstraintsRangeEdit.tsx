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
import { useEffect, useState } from "react";
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
import { FormValidation } from "../ConstraintsEdit/ConstraintsEdit";
import { DDDataField, RangeConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsRangeEdit.scss";

interface ConstraintsRangeEditProps {
  ranges: RangeConstraint[];
  onChange: (ranges: RangeConstraint[]) => void;
  typeOfData: DDDataField["type"];
  validation: FormValidation;
}

const ConstraintsRangeEdit = (props: ConstraintsRangeEditProps) => {
  const { ranges, onChange, typeOfData, validation } = props;

  const updateRange = (index: number, range: RangeConstraint) => {
    const newRanges = [...ranges];
    newRanges[index] = range;
    onChange(newRanges);
  };

  return (
    <Stack hasGutter={true}>
      {validation.form === "error" && (
        <StackItem>
          <Alert variant="danger" isInline={true} title="Please enter both start and end value." />
        </StackItem>
      )}
      <StackItem>
        <TextContent>
          <Text component={TextVariants.p}>At least one between Start Value and End Value is required.</Text>
        </TextContent>
      </StackItem>
      <StackItem>
        {ranges.map((range, index) => (
          <RangeEdit range={range} index={index} key={index} onSave={updateRange} />
        ))}
      </StackItem>
    </Stack>
  );
};

export default ConstraintsRangeEdit;

interface RangeEditProps {
  range: RangeConstraint;
  index: number;
  onSave: (index: number, range: RangeConstraint) => void;
}

const RangeEdit = (props: RangeEditProps) => {
  const { range, index, onSave } = props;
  const [rangeValues, setRangeValues] = useState(range);
  const [submitChanges, setSubmitChanges] = useState(false);

  const handleRangeChange = (value: string | boolean, event: React.FormEvent<HTMLInputElement>) => {
    switch ((event.target as HTMLInputElement).name) {
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
  const saveChange = () => {
    setSubmitChanges(true);
  };

  useEffect(() => {
    if (submitChanges) {
      onSave(index, rangeValues);
      setSubmitChanges(false);
    }
  }, [submitChanges, rangeValues]);

  useEffect(() => {
    setRangeValues(range);
  }, [range]);

  return (
    <Split hasGutter={true}>
      <SplitItem style={{ width: 320 }}>
        <FormGroup label="Start Value" fieldId={`start-value-${index}`}>
          <TextInput
            type="number"
            id={`start-value-${index}`}
            name="start-value"
            value={rangeValues.start.value}
            onChange={handleRangeChange}
            onBlur={saveChange}
            tabIndex={20}
          />
        </FormGroup>
        <FormGroup fieldId={`start-included-${index}`} className="constraints__include-range">
          <Checkbox
            label="Include Start Value"
            aria-label="Include Start Value"
            id={`start-included-${index}`}
            name="start-included"
            isChecked={rangeValues.start.included}
            onChange={handleRangeChange}
            onClick={saveChange}
            tabIndex={22}
          />
        </FormGroup>
      </SplitItem>
      <SplitItem style={{ width: 320 }}>
        <FormGroup label="End Value" fieldId={`end-value-${index}`}>
          <TextInput
            type="number"
            id={`end-value-${index}`}
            name="end-value"
            value={rangeValues.end.value}
            onChange={handleRangeChange}
            onBlur={saveChange}
            tabIndex={21}
          />
        </FormGroup>
        <FormGroup fieldId={`end-included-${index}`} className="constraints__include-range">
          <Checkbox
            label="Include End Value"
            aria-label="Include End Value"
            id={`end-included-${index}`}
            name="end-included"
            isChecked={rangeValues.end.included}
            onChange={handleRangeChange}
            onClick={saveChange}
            tabIndex={23}
          />
        </FormGroup>
      </SplitItem>
    </Split>
  );
};
