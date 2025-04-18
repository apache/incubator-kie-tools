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
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { ArrowAltCircleLeftIcon } from "@patternfly/react-icons/dist/js/icons/arrow-alt-circle-left-icon";
import { HelpIcon } from "@patternfly/react-icons/dist/js/icons/help-icon";
import { GenericSelector } from "../../EditorScorecard/atoms";
import {
  InvalidValueTreatmentMethod,
  MiningField,
  MissingValueTreatmentMethod,
  OpType,
  OutlierTreatmentMethod,
  UsageType,
} from "@kie-tools/pmml-editor-marshaller";
import "./MiningSchemaPropertiesEdit.scss";
import { useValidationRegistry } from "../../../validation";
import { Builder } from "../../../paths";
import {
  areLowHighValuesRequired,
  isInvalidValueReplacementRequired,
  isMissingValueReplacementRequired,
} from "../../../validation/MiningSchema";
import { FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";

interface MiningSchemaPropertiesEditProps {
  modelIndex: number;
  miningFieldIndex: number;
  field: MiningField;
  onSave: (field: MiningField) => void;
  onClose: () => void;
}

const MiningSchemaPropertiesEdit = ({
  modelIndex,
  miningFieldIndex,
  field,
  onSave,
  onClose,
}: MiningSchemaPropertiesEditProps) => {
  const [usageType, setUsageType] = useState(field.usageType ?? "");
  const [opType, setOpType] = useState(field.optype ?? "");
  const [importance, setImportance] = useState(field.importance);
  const [outliers, setOutliers] = useState(field.outliers ?? "");
  const [lowValue, setLowValue] = useState(field.lowValue);
  const [highValue, setHighValue] = useState(field.highValue);
  const [missingValueReplacement, setMissingValueReplacement] = useState(field.missingValueReplacement ?? "");
  const [missingValueTreatment, setMissingValueTreatment] = useState(field.missingValueTreatment ?? "");
  const [invalidValueTreatment, setInvalidValueTreatment] = useState(field.invalidValueTreatment ?? "");
  const [invalidValueReplacement, setInvalidValueReplacement] = useState(field.invalidValueReplacement ?? "");
  const [submitChanges, setSubmitChanges] = useState(false);

  const handleSave = () => {
    const updatedField = { name: field.name } as MiningField;
    if (usageType.length > 0) {
      updatedField.usageType = usageType as UsageType;
    }
    if (opType.length > 0) {
      updatedField.optype = opType as OpType;
    }
    if (typeof importance === "number") {
      updatedField.importance = importance;
    }
    if (outliers) {
      updatedField.outliers = outliers as OutlierTreatmentMethod;
    }
    if (typeof lowValue === "number") {
      updatedField.lowValue = lowValue;
    }
    if (typeof highValue === "number") {
      updatedField.highValue = highValue;
    }
    if (missingValueReplacement) {
      updatedField.missingValueReplacement = missingValueReplacement;
    }
    if (missingValueTreatment.length > 0) {
      updatedField.missingValueTreatment = missingValueTreatment as MissingValueTreatmentMethod;
    }
    if (invalidValueReplacement) {
      updatedField.invalidValueReplacement = invalidValueReplacement;
    }
    if (invalidValueTreatment.length > 0) {
      updatedField.invalidValueTreatment = invalidValueTreatment as InvalidValueTreatmentMethod;
    }
    onSave(updatedField);
    setSubmitChanges(false);
  };

  useEffect(() => {
    if (submitChanges) {
      handleSave();
    }
  }, [submitChanges]);

  useEffect(() => {
    setUsageType(field.usageType ?? "");
    setOpType(field.optype ?? "");
    setImportance(field.importance);
    setOutliers(field.outliers ?? "");
    setLowValue(field.lowValue);
    setHighValue(field.highValue);
    setMissingValueReplacement(field.missingValueReplacement ?? "");
    setMissingValueTreatment(field.missingValueTreatment ?? "");
    setInvalidValueTreatment(field.invalidValueTreatment ?? "");
    setInvalidValueReplacement(field.invalidValueReplacement ?? "");
  }, [field]);

  const { validationRegistry } = useValidationRegistry();
  const validationsImportance = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forImportance().build()
      ),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsLowValue = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forLowValue().build()
      ),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsHighValue = useMemo(
    () =>
      validationRegistry.get(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forHighValue().build()
      ),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsMissingValueReplacement = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .forMiningField(miningFieldIndex)
          .forMissingValueReplacement()
          .build()
      ),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsInvalidValueReplacement = useMemo(
    () =>
      validationRegistry.get(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .forMiningField(miningFieldIndex)
          .forInvalidValueReplacement()
          .build()
      ),
    [modelIndex, miningFieldIndex, field]
  );

  const enableLowValueComponent = useMemo(
    () => areLowHighValuesRequired(field.outliers) || field.lowValue !== undefined,
    [modelIndex, miningFieldIndex, field]
  );
  const enableHighValueComponent = useMemo(
    () => areLowHighValuesRequired(field.outliers) || field.highValue !== undefined,
    [modelIndex, miningFieldIndex, field]
  );
  const enableMissingValueComponent = useMemo(
    () => isMissingValueReplacementRequired(field.missingValueTreatment) || field.missingValueReplacement !== undefined,
    [modelIndex, miningFieldIndex, field]
  );
  const enableInvalidValueComponent = useMemo(
    () => isInvalidValueReplacementRequired(field.invalidValueTreatment) || field.invalidValueReplacement !== undefined,
    [modelIndex, miningFieldIndex, field]
  );

  const toNumberOrUndefined = (value: string): number | undefined => {
    const _value = Number.parseFloat(value);
    return isNaN(_value) ? undefined : _value;
  };

  return (
    <Stack hasGutter={true} className="mining-schema__edit">
      <StackItem>
        <Title headingLevel="h4" size={TitleSizes.xl}>
          <Button variant="link" isInline={true} onClick={onClose}>
            {field.name}
          </Button>
          &nbsp;/&nbsp;Properties
        </Title>
      </StackItem>
      <StackItem>
        <section className="mining-schema__edit__form">
          <Form>
            <Stack hasGutter={true}>
              <StackItem>
                <Split hasGutter={true}>
                  <SplitItem>
                    <FormGroup
                      className="mining-schema__properties__field"
                      label="Field Usage Type"
                      fieldId="usageType"
                    >
                      <GenericSelector
                        id="usageType"
                        items={[
                          "",
                          "active",
                          "predicted",
                          "target",
                          "supplementary",
                          "group",
                          "order",
                          "frequencyWeight",
                          "analysisWeight",
                        ]}
                        onSelect={(selection) => {
                          setUsageType(selection as UsageType);
                          setSubmitChanges(true);
                        }}
                        selection={usageType}
                        data-ouia-component-id="usage-type"
                        data-ouia-component-type="option-box"
                      />
                    </FormGroup>
                  </SplitItem>
                  <SplitItem>
                    <FormGroup className="mining-schema__properties__field" label="Field Op Type" fieldId="opType">
                      <GenericSelector
                        id="opType"
                        items={["", "categorical", "ordinal", "continuous"]}
                        onSelect={(selection) => {
                          setOpType(selection as OpType);
                          setSubmitChanges(true);
                        }}
                        selection={opType}
                      />
                    </FormGroup>
                  </SplitItem>
                </Split>
              </StackItem>
              <StackItem>
                <FormGroup className="mining-schema__properties__field" label="Importance" fieldId="importance">
                  <TextInput
                    type="number"
                    min={0}
                    max={1}
                    id="importance"
                    name="importance"
                    aria-describedby="Importance"
                    value={importance ?? ""}
                    ouiaId="importance"
                    data-ouia-component-type="double-input"
                    validated={validationsImportance.length === 0 ? "default" : "warning"}
                    onChange={(_event, value) => setImportance(toNumberOrUndefined(value))}
                    onBlur={() => {
                      if (importance !== undefined) {
                        let _importance = importance;
                        if (_importance < 0) {
                          _importance = 0;
                          setImportance(_importance);
                        } else if (_importance > 1) {
                          _importance = 1;
                          setImportance(_importance);
                        }
                      }
                      handleSave();
                    }}
                  />
                  {validationsImportance.length === 0 ? (
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="default"></HelperTextItem>
                      </HelperText>
                    </FormHelperText>
                  ) : (
                    <FormHelperText>
                      <HelperText>
                        <HelperTextItem variant="warning">{validationsImportance[0].message}</HelperTextItem>
                      </HelperText>
                    </FormHelperText>
                  )}
                </FormGroup>
              </StackItem>
              <StackItem>
                <Split hasGutter={true}>
                  <SplitItem>
                    <FormGroup
                      className="mining-schema__properties__field"
                      label="Outliers Treatment Method"
                      fieldId="outliers"
                    >
                      <GenericSelector
                        id="outliers"
                        items={["", "asIs", "asMissingValues", "asExtremeValues"]}
                        onSelect={(selection) => {
                          setOutliers(selection as OutlierTreatmentMethod);
                          setSubmitChanges(true);
                        }}
                        selection={outliers}
                        data-ouia-component-id="outliers"
                      />
                    </FormGroup>
                  </SplitItem>
                  <SplitItem>
                    <FormGroup
                      label="Low Value"
                      fieldId="lowValue"
                      className="mining-schema__properties__field"
                      labelIcon={
                        <Tooltip
                          content={`Low Value is required when Outliers is "asExtremeValues" or "asMissingValues"`}
                        >
                          <button
                            aria-label="More information for Low Value field"
                            onClick={(e) => e.preventDefault()}
                            className="pf-v5-c-form__group-label-help"
                          >
                            <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                          </button>
                        </Tooltip>
                      }
                    >
                      <TextInput
                        type="number"
                        id="lowValue"
                        name="lowValue"
                        aria-describedby="Low Value"
                        value={lowValue ?? ""}
                        validated={validationsLowValue.length === 0 ? "default" : "warning"}
                        isDisabled={!enableLowValueComponent}
                        placeholder={!enableLowValueComponent ? "<Not needed>" : ""}
                        className={!enableLowValueComponent ? "mining-schema__edit__form__disabled" : ""}
                        onChange={(_event, value) => setLowValue(toNumberOrUndefined(value))}
                        onBlur={handleSave}
                        ouiaId="low-value"
                      />
                      {validationsLowValue.length === 0 ? (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="default"></HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      ) : (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="warning">{validationsLowValue[0].message}</HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      )}
                    </FormGroup>
                  </SplitItem>
                  <SplitItem>
                    <FormGroup
                      label="High Value"
                      fieldId="highValue"
                      className="mining-schema__properties__field"
                      labelIcon={
                        <Tooltip
                          content={`High Value is required when Outliers is "asExtremeValues" or "asMissingValues"`}
                        >
                          <button
                            aria-label="More information for High Value field"
                            onClick={(e) => e.preventDefault()}
                            className="pf-v5-c-form__group-label-help"
                          >
                            <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                          </button>
                        </Tooltip>
                      }
                    >
                      <TextInput
                        type="number"
                        id="highValue"
                        name="highValue"
                        aria-describedby="High Value"
                        value={highValue ?? ""}
                        validated={validationsHighValue.length === 0 ? "default" : "warning"}
                        isDisabled={!enableHighValueComponent}
                        placeholder={!enableHighValueComponent ? "<Not needed>" : ""}
                        className={!enableHighValueComponent ? "mining-schema__edit__form__disabled" : ""}
                        onChange={(_event, value) => setHighValue(toNumberOrUndefined(value))}
                        onBlur={handleSave}
                        ouiaId="high-value"
                      />
                      {validationsHighValue.length === 0 ? (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="default"></HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      ) : (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="warning">{validationsHighValue[0].message}</HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      )}
                    </FormGroup>
                  </SplitItem>
                </Split>
              </StackItem>
              <StackItem>
                <Split hasGutter={true}>
                  <SplitItem>
                    <FormGroup label="Missing Value Treatment Method" fieldId="missingValueTreatment">
                      <GenericSelector
                        id="missingValueTreatment"
                        items={["", "asIs", "asMean", "asMode", "asMedian", "asValue", "returnInvalid"]}
                        onSelect={(selection) => {
                          setMissingValueTreatment(selection as MissingValueTreatmentMethod);
                          setSubmitChanges(true);
                        }}
                        selection={missingValueTreatment}
                      />
                    </FormGroup>
                  </SplitItem>
                  <SplitItem>
                    <FormGroup
                      label="Missing Value Replacement"
                      fieldId="missingValueReplacement"
                      labelIcon={
                        <Tooltip
                          content={`Missing Value Replacement is required when Missing Value Treatment is "asMean", "asMedian" or "asMode"`}
                        >
                          <button
                            aria-label="More information for Missing Value Replacement field"
                            onClick={(e) => e.preventDefault()}
                            className="pf-v5-c-form__group-label-help"
                          >
                            <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                          </button>
                        </Tooltip>
                      }
                    >
                      <TextInput
                        type="text"
                        id="missingValueReplacement"
                        name="missingValueReplacement"
                        aria-describedby="Missing Value Replacement"
                        value={missingValueReplacement}
                        validated={validationsMissingValueReplacement.length === 0 ? "default" : "warning"}
                        isDisabled={!enableMissingValueComponent}
                        placeholder={!enableMissingValueComponent ? "<Not needed>" : ""}
                        className={!enableMissingValueComponent ? "mining-schema__edit__form__disabled" : ""}
                        onChange={(_event, value) => setMissingValueReplacement(value)}
                        onBlur={handleSave}
                      />
                      {validationsMissingValueReplacement.length === 0 ? (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="default">
                              {validationsMissingValueReplacement[0].message}
                            </HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      ) : (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="warning"></HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      )}
                    </FormGroup>
                  </SplitItem>
                </Split>
              </StackItem>
              <StackItem>
                <Split hasGutter={true}>
                  <SplitItem>
                    <FormGroup label="Invalid Value Treatment Method" fieldId="invalidValueTreatment">
                      <GenericSelector
                        id="invalidValueTreatment"
                        items={["", "returnInvalid", "asIs", "asMissing", "asValue"]}
                        onSelect={(selection) => {
                          setInvalidValueTreatment(selection as InvalidValueTreatmentMethod);
                          setSubmitChanges(true);
                        }}
                        selection={invalidValueTreatment}
                      />
                    </FormGroup>
                  </SplitItem>
                  <SplitItem>
                    <FormGroup
                      label="Invalid Value Replacement"
                      fieldId="invalidValueReplacement"
                      labelIcon={
                        <Tooltip
                          content={`Invalid Value Replacement is required when Invalid Value Treatment is "asValue"`}
                        >
                          <button
                            aria-label="More information for Invalid Value Replacement field"
                            onClick={(e) => e.preventDefault()}
                            className="pf-v5-c-form__group-label-help"
                          >
                            <HelpIcon style={{ color: "var(--pf-v5-global--info-color--100)" }} />
                          </button>
                        </Tooltip>
                      }
                    >
                      <TextInput
                        type="text"
                        id="invalidValueReplacement"
                        name="invalidValueReplacement"
                        aria-describedby="Invalid Value Replacement"
                        value={invalidValueReplacement}
                        validated={validationsInvalidValueReplacement.length === 0 ? "default" : "warning"}
                        isDisabled={!enableInvalidValueComponent}
                        placeholder={!enableInvalidValueComponent ? "<Not needed>" : ""}
                        className={!enableInvalidValueComponent ? "mining-schema__edit__form__disabled" : ""}
                        onChange={(_event, value) => setInvalidValueReplacement(value)}
                        onBlur={handleSave}
                      />
                      {validationsInvalidValueReplacement.length === 0 ? (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="default">
                              {validationsInvalidValueReplacement[0].message}
                            </HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      ) : (
                        <FormHelperText>
                          <HelperText>
                            <HelperTextItem variant="warning"></HelperTextItem>
                          </HelperText>
                        </FormHelperText>
                      )}
                    </FormGroup>
                  </SplitItem>
                </Split>
              </StackItem>
            </Stack>
          </Form>
        </section>
        <section className="mining-schema__edit__actions">
          <Button
            variant="primary"
            onClick={onClose}
            icon={<ArrowAltCircleLeftIcon />}
            iconPosition="left"
            ouiaId="back-to-ms-overview"
          >
            Back
          </Button>
        </section>
      </StackItem>
    </Stack>
  );
};

export default MiningSchemaPropertiesEdit;
