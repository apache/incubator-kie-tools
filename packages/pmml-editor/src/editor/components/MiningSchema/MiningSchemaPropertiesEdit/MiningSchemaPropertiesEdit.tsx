import * as React from "react";
import { useEffect, useMemo, useState } from "react";
import {
  Button,
  Card,
  CardBody,
  Form,
  FormGroup,
  Split,
  SplitItem,
  Stack,
  StackItem,
  TextInput,
  Title,
  TitleSizes,
  Tooltip
} from "@patternfly/react-core";
import { ArrowAltCircleLeftIcon, HelpIcon } from "@patternfly/react-icons";
import { GenericSelector } from "../../EditorScorecard/atoms";
import {
  InvalidValueTreatmentMethod,
  MiningField,
  MissingValueTreatmentMethod,
  OpType,
  OutlierTreatmentMethod,
  UsageType
} from "@kogito-tooling/pmml-editor-marshaller";
import "./MiningSchemaPropertiesEdit.scss";
import { useValidationService } from "../../../validation";
import {
  areLowHighValuesRequired,
  isInvalidValueReplacementRequired,
  isMissingValueReplacementRequired
} from "../../../validation/MiningSchema";

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
  onClose
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

  const service = useValidationService().service;
  const validationsImportance = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].importance`),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsLowValue = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].lowValue`),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsHighValue = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].highValue`),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsMissingValueReplacement = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].missingValueReplacement`),
    [modelIndex, miningFieldIndex, field]
  );
  const validationsInvalidValueReplacement = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].invalidValueReplacement`),
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
            <Split hasGutter={true}>
              <SplitItem style={{ width: "50%" }}>
                <Stack hasGutter={true}>
                  <StackItem>
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
                          "analysisWeight"
                        ]}
                        onSelect={selection => {
                          setUsageType(selection as UsageType);
                          setSubmitChanges(true);
                        }}
                        selection={usageType}
                      />
                    </FormGroup>
                  </StackItem>
                  <StackItem>
                    <FormGroup className="mining-schema__properties__field" label="Field Op Type" fieldId="opType">
                      <GenericSelector
                        id="opType"
                        items={["", "categorical", "ordinal", "continuous"]}
                        onSelect={selection => {
                          setOpType(selection as OpType);
                          setSubmitChanges(true);
                        }}
                        selection={opType}
                      />
                    </FormGroup>
                  </StackItem>
                  <StackItem>
                    <FormGroup
                      className="mining-schema__properties__field"
                      label="Importance"
                      fieldId="importance"
                      helperText="Importance must be between 0 and 1."
                      validated={validationsImportance.length === 0 ? "default" : "warning"}
                    >
                      <TextInput
                        type="number"
                        min={0}
                        max={1}
                        id="importance"
                        name="importance"
                        aria-describedby="Importance"
                        value={importance ?? ""}
                        validated={validationsImportance.length === 0 ? "default" : "warning"}
                        onChange={value => setImportance(toNumberOrUndefined(value))}
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
                    </FormGroup>
                  </StackItem>
                </Stack>
              </SplitItem>
              <SplitItem style={{ width: "50%" }}>
                <Card isCompact={true} style={{ margin: "1em 0" }}>
                  <CardBody>
                    <Stack hasGutter={true}>
                      <StackItem>
                        <FormGroup
                          className="mining-schema__properties__field"
                          label="Outliers Treatment Method"
                          fieldId="outliers"
                        >
                          <GenericSelector
                            id="outliers"
                            items={["", "asIs", "asMissingValues", "asExtremeValues"]}
                            onSelect={selection => {
                              setOutliers(selection as OutlierTreatmentMethod);
                              setSubmitChanges(true);
                            }}
                            selection={outliers}
                          />
                        </FormGroup>
                      </StackItem>
                      <StackItem>
                        <FormGroup
                          label="Low Value"
                          fieldId="lowValue"
                          className="mining-schema__properties__field"
                          helperText={validationsLowValue.length === 0 ? "" : validationsLowValue[0].message}
                          validated={validationsLowValue.length === 0 ? "default" : "warning"}
                          labelIcon={
                            <Tooltip
                              content={`Low Value is required when Outliers is "asExtremeValues" or "asMissingValues"`}
                            >
                              <button
                                aria-label="More information for Low Value field"
                                onClick={e => e.preventDefault()}
                                aria-describedby="simple-form-name"
                                className="pf-c-form__group-label-help"
                              >
                                <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
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
                            onChange={value => setLowValue(toNumberOrUndefined(value))}
                            onBlur={handleSave}
                          />
                        </FormGroup>
                      </StackItem>
                      <StackItem>
                        <FormGroup
                          label="High Value"
                          fieldId="highValue"
                          className="mining-schema__properties__field"
                          helperText={validationsHighValue.length === 0 ? "" : validationsHighValue[0].message}
                          validated={validationsHighValue.length === 0 ? "default" : "warning"}
                          labelIcon={
                            <Tooltip
                              content={`High Value is required when Outliers is "asExtremeValues" or "asMissingValues"`}
                            >
                              <button
                                aria-label="More information for High Value field"
                                onClick={e => e.preventDefault()}
                                aria-describedby="simple-form-name"
                                className="pf-c-form__group-label-help"
                              >
                                <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
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
                            onChange={value => setHighValue(toNumberOrUndefined(value))}
                            onBlur={handleSave}
                          />
                        </FormGroup>
                      </StackItem>
                    </Stack>
                  </CardBody>
                </Card>
              </SplitItem>
            </Split>
            <Split hasGutter={true}>
              <SplitItem>
                <FormGroup label="Missing Value Treatment Method" fieldId="missingValueTreatment">
                  <GenericSelector
                    id="missingValueTreatment"
                    items={["", "asIs", "asMean", "asMode", "asMedian", "asValue", "returnInvalid"]}
                    onSelect={selection => {
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
                  validated={validationsMissingValueReplacement.length === 0 ? "default" : "warning"}
                  helperText={
                    validationsMissingValueReplacement[0] ? validationsMissingValueReplacement[0].message : ""
                  }
                  labelIcon={
                    <Tooltip
                      content={`Missing Value Replacement is required when Missing Value Treatment is "asMean", "asMedian" or "asMode"`}
                    >
                      <button
                        aria-label="More information for Missing Value Replacement field"
                        onClick={e => e.preventDefault()}
                        aria-describedby="simple-form-name"
                        className="pf-c-form__group-label-help"
                      >
                        <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
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
                    style={
                      !enableMissingValueComponent ? { backgroundColor: "var(--pf-global--BorderColor--100)" } : {}
                    }
                    onChange={value => setMissingValueReplacement(value)}
                    onBlur={handleSave}
                  />
                </FormGroup>
              </SplitItem>
            </Split>
            <Split hasGutter={true}>
              <SplitItem>
                <FormGroup label="Invalid Value Treatment Method" fieldId="invalidValueTreatment">
                  <GenericSelector
                    id="invalidValueTreatment"
                    items={["", "returnInvalid", "asIs", "asMissing", "asValue"]}
                    onSelect={selection => {
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
                  validated={validationsInvalidValueReplacement.length === 0 ? "default" : "warning"}
                  helperText={
                    validationsInvalidValueReplacement[0] ? validationsInvalidValueReplacement[0].message : ""
                  }
                  labelIcon={
                    <Tooltip
                      content={`Invalid Value Replacement is required when Invalid Value Treatment is "asValue"`}
                    >
                      <button
                        aria-label="More information for Invalid Value Replacement field"
                        onClick={e => e.preventDefault()}
                        aria-describedby="simple-form-name"
                        className="pf-c-form__group-label-help"
                      >
                        <HelpIcon style={{ color: "var(--pf-global--info-color--100)" }} />
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
                    style={
                      !enableInvalidValueComponent ? { backgroundColor: "var(--pf-global--BorderColor--100)" } : {}
                    }
                    onChange={value => setInvalidValueReplacement(value)}
                    onBlur={handleSave}
                  />
                </FormGroup>
              </SplitItem>
            </Split>
          </Form>
        </section>
        <section className="mining-schema__edit__actions">
          <Button variant="primary" onClick={onClose} icon={<ArrowAltCircleLeftIcon />} iconPosition="left">
            Done
          </Button>
        </section>
      </StackItem>
    </Stack>
  );
};

export default MiningSchemaPropertiesEdit;
