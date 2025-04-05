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
import { useEffect, useMemo, useState } from "react";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Radio } from "@patternfly/react-core/dist/js/components/Radio";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { ArrowAltCircleLeftIcon } from "@patternfly/react-icons/dist/js/icons/arrow-alt-circle-left-icon";
import { ConstraintType, DDDataField } from "../DataDictionaryContainer/DataDictionaryContainer";
import ConstraintsEdit from "../ConstraintsEdit/ConstraintsEdit";
import "./DataDictionaryPropertiesEdit.scss";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface DataDictionaryPropertiesEditProps {
  dataType: DDDataField;
  dataFieldIndex: number | undefined;
  onClose: () => void;
  onSave: (payload: Partial<DDDataField>) => void;
}

const DataDictionaryPropertiesEdit = (props: DataDictionaryPropertiesEditProps) => {
  const { dataType, dataFieldIndex, onClose, onSave } = props;
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

  const saveCyclicProperty = (value: DDDataField["isCyclic"]) => {
    setIsCyclic(value);
    onSave({
      isCyclic: value,
    });
  };

  const isOptypeDisabled = useMemo(() => dataType.optype === "categorical", [dataType.optype]);

  const constraintAlert = useMemo(() => {
    if (dataType.optype === "continuous" && dataType.isCyclic && dataType.constraints === undefined) {
      return "Interval or Value constraints are required for cyclic continuous data types";
    }
    if (
      dataType.isCyclic &&
      dataType.optype === "continuous" &&
      dataType.constraints?.type === ConstraintType.RANGE &&
      dataType.constraints.value?.length > 1
    ) {
      return "Cyclic continuous data types can only have a single interval constraint";
    }
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
      <StackItem
        className="data-dictionary__properties-edit__form-container"
        data-ouia-component-id="df-props"
        data-ouia-component-type="editor-body"
      >
        <Form className="data-dictionary__properties-edit__form">
          <Split hasGutter={true}>
            <SplitItem className="data-dictionary__properties-edit__form__left-column">
              <Stack hasGutter={true}>
                <StackItem>
                  <FormGroup
                    className="data-dictionary__properties-edit__field"
                    label="Display Name"
                    fieldId="display-name"
                  >
                    <TextInput
                      type="text"
                      id="display-name"
                      name="display-name"
                      aria-describedby="Display Name"
                      value={displayName}
                      onChange={(_event, value) => setDisplayName(value)}
                      autoComplete="off"
                      onBlur={() =>
                        onSave({
                          displayName: displayName === "" ? undefined : displayName,
                        })
                      }
                      ouiaId="display-name"
                    />
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="error">
                          Display Name to use instead of the data type name
                        </HelperTextItem>
                      </HelperText>
                    </FormHelperText>
                  </FormGroup>
                </StackItem>
                <StackItem>
                  <FormGroup
                    className="data-dictionary__properties-edit__field"
                    label="Cyclic Type"
                    fieldId="is-cyclic"
                    isInline={true}
                    labelIcon={
                      dataType.optype === "categorical" ? (
                        <Tooltip content={"Categorical fields cannot be cyclic"}>
                          <button
                            aria-label="More info for Cyclic Type"
                            onClick={(e) => e.preventDefault()}
                            className="pf-v5-c-form__group-label-help"
                          >
                            <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                          </button>
                        </Tooltip>
                      ) : (
                        <></>
                      )
                    }
                  >
                    <Radio
                      isChecked={isCyclic === true}
                      name="isCyclic"
                      onChange={() => {
                        saveCyclicProperty(true);
                      }}
                      label="Yes"
                      id="isCyclic"
                      value="isCyclic"
                      isDisabled={isOptypeDisabled}
                    />
                    <Radio
                      isChecked={isCyclic === false}
                      name="isNotCyclic"
                      onChange={() => {
                        saveCyclicProperty(false);
                      }}
                      label="No"
                      id="isNotCyclic"
                      value="isNotCyclic"
                      isDisabled={isOptypeDisabled}
                    />
                    <Radio
                      isChecked={isCyclic === undefined}
                      name="cyclicNotSet"
                      onChange={() => {
                        saveCyclicProperty(undefined);
                      }}
                      label="Not Set"
                      id="cyclicNotSet"
                      value="cyclicNotSet"
                      isDisabled={isOptypeDisabled}
                    />
                  </FormGroup>
                </StackItem>
                <StackItem>
                  <FormGroup
                    className="data-dictionary__properties-edit__field"
                    label="Missing Value"
                    fieldId="missing-value"
                  >
                    <TextInput
                      type="text"
                      id="missing-value"
                      name="missing-value"
                      aria-describedby="Missing Value"
                      value={missingValue}
                      onChange={(_event, value) => setMissingValue(value)}
                      autoComplete="off"
                      onBlur={() =>
                        onSave({
                          missingValue: missingValue === "" ? undefined : missingValue,
                        })
                      }
                      ouiaId="missing-value"
                    />
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="error">Value for when the input is missing</HelperTextItem>
                      </HelperText>
                    </FormHelperText>
                  </FormGroup>
                </StackItem>
                <StackItem>
                  <FormGroup
                    className="data-dictionary__properties-edit__field"
                    label="Invalid Value"
                    fieldId="missing-value"
                  >
                    <TextInput
                      type="text"
                      id="invalid-value"
                      name="invalid-value"
                      aria-describedby="Invalid Value"
                      value={invalidValue}
                      onChange={(_event, value) => setInvalidValue(value)}
                      autoComplete="off"
                      onBlur={() =>
                        onSave({
                          invalidValue: invalidValue === "" ? undefined : invalidValue,
                        })
                      }
                      ouiaId="invalid-value"
                    />
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="error">Value for when the input is invalid</HelperTextItem>
                      </HelperText>
                    </FormHelperText>
                  </FormGroup>
                </StackItem>
              </Stack>
            </SplitItem>
            <SplitItem isFilled={true}>
              <section className="data-dictionary__constraints-section">
                {constraintAlert && (
                  <Alert
                    variant="warning"
                    isInline={true}
                    className="data-dictionary__validation-alert"
                    title={constraintAlert}
                  />
                )}
                <ConstraintsEdit dataType={dataType} dataFieldIndex={dataFieldIndex} onSave={onSave} />
              </section>
            </SplitItem>
          </Split>
        </Form>
      </StackItem>
      <StackItem>
        <Button
          variant="primary"
          onClick={onClose}
          icon={<ArrowAltCircleLeftIcon />}
          iconPosition="left"
          ouiaId="back-to-DFs"
        >
          Back
        </Button>
      </StackItem>
    </Stack>
  );
};

export default DataDictionaryPropertiesEdit;
