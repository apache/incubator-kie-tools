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
import { useEffect, useMemo, useRef, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { RangeConstraint } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsRangeEdit.scss";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface ConstraintsRangeEditProps {
  dataFieldIndex: number | undefined;
  ranges: RangeConstraint[];
  onAdd: () => void;
  onChange: (ranges: RangeConstraint[]) => void;
  onDelete: (index: number) => void;
  countLimit?: number;
}

const ConstraintsRangeEdit = (props: ConstraintsRangeEditProps) => {
  const { dataFieldIndex, ranges, onAdd, onChange, onDelete, countLimit } = props;
  const [addedRange, setAddedRange] = useState<number>();

  const updateRange = (index: number, range: RangeConstraint) => {
    const newRanges = [...ranges];
    newRanges[index] = range;
    onChange(newRanges);
  };

  const addRange = () => {
    onAdd();
    setAddedRange(ranges.length);
  };

  const updateAddedRange = (position: number) => {
    setAddedRange(position);
  };

  return (
    <Stack hasGutter={true}>
      <StackItem>
        <TextContent>
          <Text component={TextVariants.small}>
            At least the Start Value or End Value is required for each interval
          </Text>
        </TextContent>
      </StackItem>
      <StackItem>
        {ranges.map((range, index) => (
          <RangeEdit
            dataFieldIndex={dataFieldIndex}
            range={range}
            rangesCount={ranges.length}
            index={index}
            key={index}
            onSave={updateRange}
            onDelete={onDelete}
            addedRange={addedRange}
            updateAddedRange={updateAddedRange}
          />
        ))}
      </StackItem>
      <StackItem>
        <Button
          variant="secondary"
          onClick={addRange}
          isDisabled={countLimit !== undefined && ranges.length >= countLimit}
          size="sm"
          ouiaId="add-another-interval"
        >
          Add another interval
        </Button>
      </StackItem>
    </Stack>
  );
};

export default ConstraintsRangeEdit;

interface RangeEditProps {
  dataFieldIndex: number | undefined;
  range: RangeConstraint;
  rangesCount: number;
  index: number;
  onSave: (index: number, range: RangeConstraint) => void;
  onDelete: (index: number) => void;
  addedRange?: number;
  updateAddedRange: (position: number | undefined) => void;
}

const RangeEdit = (props: RangeEditProps) => {
  const { dataFieldIndex, range, rangesCount, index, onSave, onDelete, addedRange, updateAddedRange } = props;
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

  const handleDelete = () => {
    onDelete(index);
  };

  const saveChange = () => {
    setSubmitChanges(true);
  };

  const rangeRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (submitChanges) {
      onSave(index, rangeValues);
      setSubmitChanges(false);
    }
  }, [submitChanges, rangeValues]);

  useEffect(() => {
    setRangeValues(range);
  }, [range]);

  useEffect(() => {
    if (rangeRef.current && addedRange === index) {
      const container = document.querySelector(".data-dictionary__properties-edit__form .constraints__form");
      container?.scroll({ top: container?.scrollHeight, behavior: "smooth" });
      updateAddedRange(undefined);
    }
  }, [addedRange, index, rangeRef.current]);

  const { validationRegistry } = useValidationRegistry();
  const validations = useMemo(
    () => validationRegistry.get(Builder().forDataDictionary().forDataField(dataFieldIndex).forInterval(index).build()),
    [range]
  );

  return (
    <section ref={rangeRef} data-ouia-component-id={`interval-${index}`} data-ouia-component-type="interval-item">
      <Split hasGutter={true} className="constraints__range-item">
        <SplitItem isFilled={true}>
          <FormGroup label="Start Value" fieldId={`start-value-${index}`}>
            <TextInput
              type="number"
              id={`start-value-${index}`}
              name="start-value"
              value={rangeValues.start.value}
              validated={validations.length === 0 ? "default" : "warning"}
              onChange={(event, value: string | boolean) => handleRangeChange(value, event)}
              onBlur={saveChange}
              tabIndex={(index + 1) * 10 + 1}
              autoComplete="off"
              ouiaId="start-value"
            />
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="error">
                  {validations[0] ? "Please enter start and/or end value" : ""}
                </HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>
          <FormGroup fieldId={`start-included-${index}`} className="constraints__include-range">
            <Checkbox
              label="Include Start Value"
              aria-label="Include Start Value"
              id={`start-included-${index}`}
              name="start-included"
              isChecked={rangeValues.start.included}
              onChange={(event, value: string | boolean) => handleRangeChange(value, event)}
              onClick={saveChange}
              tabIndex={(index + 1) * 10 + 3}
              ouiaId="is-start-included"
            />
          </FormGroup>
        </SplitItem>
        <SplitItem isFilled={true}>
          <FormGroup label="End Value" fieldId={`end-value-${index}`}>
            <TextInput
              type="number"
              id={`end-value-${index}`}
              name="end-value"
              value={rangeValues.end.value}
              validated={validations.length === 0 ? "default" : "warning"}
              onChange={(event, value: string | boolean) => handleRangeChange(value, event)}
              onBlur={saveChange}
              tabIndex={(index + 1) * 10 + 2}
              autoComplete="off"
              ouiaId="end-value"
            />
            <FormHelperText>
              <HelperText>
                <HelperTextItem variant="error">
                  {validations[0] ? "Please enter start and/or end value" : ""}
                </HelperTextItem>
              </HelperText>
            </FormHelperText>
          </FormGroup>
          <FormGroup fieldId={`end-included-${index}`} className="constraints__include-range">
            <Checkbox
              label="Include End Value"
              aria-label="Include End Value"
              id={`end-included-${index}`}
              name="end-included"
              isChecked={rangeValues.end.included}
              onChange={(event, value: string | boolean) => handleRangeChange(value, event)}
              onClick={saveChange}
              tabIndex={(index + 1) * 10 + 4}
              ouiaId="is-end-included"
            />
          </FormGroup>
        </SplitItem>
        <SplitItem>
          <Flex
            alignItems={{ default: "alignItemsCenter" }}
            justifyContent={{ default: "justifyContentCenter" }}
            style={{ height: "100%" }}
          >
            <FlexItem>
              <Button
                variant="plain"
                aria-label="Delete Range"
                onClick={handleDelete}
                isDisabled={rangesCount === 1}
                tabIndex={(index + 1) * 10 + 5}
                ouiaId="delete-item"
              >
                <TrashIcon />
              </Button>
            </FlexItem>
          </Flex>
        </SplitItem>
      </Split>
    </section>
  );
};
