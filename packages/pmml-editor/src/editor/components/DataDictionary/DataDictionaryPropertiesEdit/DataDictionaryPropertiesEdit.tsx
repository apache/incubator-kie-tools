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
  Button,
  Form,
  FormGroup,
  Radio,
  Split,
  SplitItem,
  Stack,
  StackItem,
  TextInput,
  Title,
  TitleSizes
} from "@patternfly/react-core";
import { DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import ConstraintsEdit from "../ConstraintsEdit/ConstraintsEdit";
import "./DataDictionaryPropertiesEdit.scss";

interface DataDictionaryPropertiesEditProps {
  dataType: DDDataField;
  onClose: () => void;
  onSave: (payload: Partial<DDDataField>) => void;
}

const DataDictionaryPropertiesEdit = (props: DataDictionaryPropertiesEditProps) => {
  const { dataType, onClose, onSave } = props;
  const [displayName, setDisplayName] = useState(dataType.displayName ?? "");
  const [isCyclic, setIsCyclic] = useState(dataType.isCyclic);
  const [missingValue, setMissingValue] = useState(dataType.missingValue ?? "");
  const [invalidValue, setInvalidValue] = useState(dataType.invalidValue ?? "");

  useEffect(() => {
    setDisplayName(dataType.displayName ?? "");
    setIsCyclic(dataType.isCyclic);
    setMissingValue(dataType.missingValue ?? "");
    setInvalidValue(dataType.invalidValue ?? "");
  }, [dataType]);

  return (
    <Stack hasGutter={true} className="data-dictionary__properties-edit">
      <StackItem>
        <Title headingLevel="h4" size={TitleSizes.xl}>
          <Button variant="link" isInline={true} onClick={onClose}>
            {dataType.name}
          </Button>
          &nbsp;/&nbsp;Properties
        </Title>
      </StackItem>
      <StackItem className="data-dictionary__properties-edit__form">
        <Form>
          <Split hasGutter={true}>
            <SplitItem>
              <FormGroup
                className="data-dictionary__properties-edit__field"
                label="Display Name"
                fieldId="display-name"
                helperText="Display Name to use instead of the data type name"
              >
                <TextInput
                  type="text"
                  id="display-name"
                  name="display-name"
                  aria-describedby="Display Name"
                  value={displayName}
                  onChange={value => setDisplayName(value)}
                  autoComplete="off"
                  onBlur={() =>
                    onSave({
                      displayName: displayName === "" ? undefined : displayName
                    })
                  }
                />
              </FormGroup>
            </SplitItem>
            <SplitItem>
              <FormGroup
                className="data-dictionary__properties-edit__field"
                label="Cyclic Type"
                fieldId="is-cyclic"
                isInline={true}
              >
                <Radio
                  isChecked={isCyclic === true}
                  name="isCyclic"
                  onChange={() => {
                    setIsCyclic(true);
                    onSave({
                      isCyclic: true
                    });
                  }}
                  label="Yes"
                  id="isCyclic"
                  value="isCyclic"
                />
                <Radio
                  isChecked={isCyclic === false}
                  name="isNotCyclic"
                  onChange={() => {
                    setIsCyclic(false);
                    onSave({
                      isCyclic: false
                    });
                  }}
                  label="No"
                  id="isNotCyclic"
                  value="isNotCyclic"
                />
                <Radio
                  isChecked={isCyclic === undefined}
                  name="cyclicNotSet"
                  onChange={() => {
                    setIsCyclic(undefined);
                    onSave({
                      isCyclic: undefined
                    });
                  }}
                  label="Not Set"
                  id="cyclicNotSet"
                  value="cyclicNotSet"
                />
              </FormGroup>
            </SplitItem>
          </Split>
          <Split hasGutter={true}>
            <SplitItem>
              <FormGroup
                className="data-dictionary__properties-edit__field"
                label="Missing Value"
                fieldId="missing-value"
                helperText="Value for when the input is missing"
              >
                <TextInput
                  type="text"
                  id="missing-value"
                  name="missing-value"
                  aria-describedby="Missing Value"
                  value={missingValue}
                  onChange={value => setMissingValue(value)}
                  autoComplete="off"
                  onBlur={() =>
                    onSave({
                      missingValue: missingValue === "" ? undefined : missingValue
                    })
                  }
                />
              </FormGroup>
            </SplitItem>
            <SplitItem>
              <FormGroup
                className="data-dictionary__properties-edit__field"
                label="Invalid Value"
                fieldId="missing-value"
                helperText="Value for when the input is invalid"
              >
                <TextInput
                  type="text"
                  id="invalid-value"
                  name="invalid-value"
                  aria-describedby="Invalid Value"
                  value={invalidValue}
                  onChange={value => setInvalidValue(value)}
                  autoComplete="off"
                  onBlur={() =>
                    onSave({
                      invalidValue: invalidValue === "" ? undefined : invalidValue
                    })
                  }
                />
              </FormGroup>
            </SplitItem>
          </Split>
          <ConstraintsEdit dataType={dataType} onSave={onSave} />
        </Form>
      </StackItem>
    </Stack>
  );
};

export default DataDictionaryPropertiesEdit;
