import * as React from "react";
import { useEffect, useState } from "react";
import {
  Button,
  Form,
  FormGroup,
  Split,
  SplitItem,
  Stack,
  StackItem,
  TextInput,
  Title,
  TitleSizes
} from "@patternfly/react-core";
import { ArrowAltCircleLeftIcon } from "@patternfly/react-icons";
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

interface MiningSchemaPropertiesEditProps {
  field: MiningField;
  onSave: (field: MiningField) => void;
  onClose: () => void;
}

const MiningSchemaPropertiesEdit = ({ field, onSave, onClose }: MiningSchemaPropertiesEditProps) => {
  const [usageType, setUsageType] = useState(field.usageType ?? "");
  const [opType, setOpType] = useState(field.optype ?? "");
  const [importance, setImportance] = useState(field.importance ?? "");
  const [outliers, setOutliers] = useState(field.outliers ?? "");
  const [lowValue, setLowValue] = useState(field.lowValue ?? "");
  const [highValue, setHighValue] = useState(field.highValue ?? "");
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
    setImportance(field.importance ?? "");
    setOutliers(field.outliers ?? "");
    setLowValue(field.lowValue ?? "");
    setHighValue(field.highValue ?? "");
    setMissingValueReplacement(field.missingValueReplacement ?? "");
    setMissingValueTreatment(field.missingValueTreatment ?? "");
    setInvalidValueTreatment(field.invalidValueTreatment ?? "");
    setInvalidValueReplacement(field.invalidValueReplacement ?? "");
  }, [field]);

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
            <FormGroup className="mining-schema__properties__field" label="Field Usage Type" fieldId="usageType">
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
            <FormGroup
              className="mining-schema__properties__field"
              label="Importance"
              fieldId="importance"
              helperText="Importance is a value between 0.0 and 1.0"
            >
              <TextInput
                type="number"
                min={0}
                max={1}
                id="importance"
                name="importance"
                aria-describedby="Importance"
                value={importance}
                onChange={value => setImportance(Number.parseFloat(value))}
                onBlur={handleSave}
              />
            </FormGroup>
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
            <Split hasGutter={true}>
              <SplitItem style={{ width: 320 }}>
                <FormGroup label="Low Value" fieldId="lowValue">
                  <TextInput
                    type="number"
                    id="lowValue"
                    name="lowValue"
                    aria-describedby="Low Value"
                    value={lowValue}
                    onChange={value => setLowValue(Number.parseFloat(value))}
                    onBlur={handleSave}
                  />
                </FormGroup>
              </SplitItem>
              <SplitItem style={{ width: 320 }}>
                <FormGroup label="High Value" fieldId="highValue">
                  <TextInput
                    type="number"
                    id="highValue"
                    name="highValue"
                    aria-describedby="High Value"
                    value={highValue}
                    onChange={value => setHighValue(Number.parseFloat(value))}
                    onBlur={handleSave}
                  />
                </FormGroup>
              </SplitItem>
            </Split>
            <Split hasGutter={true}>
              <SplitItem style={{ width: 320 }}>
                <FormGroup label="Missing Value Replacement" fieldId="missingValueReplacement">
                  <TextInput
                    type="text"
                    id="missingValueReplacement"
                    name="missingValueReplacement"
                    aria-describedby="Missing Value Replacement"
                    value={missingValueReplacement}
                    onChange={value => setMissingValueReplacement(value)}
                    onBlur={handleSave}
                  />
                </FormGroup>
              </SplitItem>
              <SplitItem style={{ width: 320 }}>
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
            </Split>
            <Split hasGutter={true}>
              <SplitItem style={{ width: 320 }}>
                <FormGroup label="Invalid Value Replacement" fieldId="invalidValueReplacement">
                  <TextInput
                    type="text"
                    id="invalidValueReplacement"
                    name="invalidValueReplacement"
                    aria-describedby="Invalid Value Replacement"
                    value={invalidValueReplacement}
                    onChange={value => setInvalidValueReplacement(value)}
                    onBlur={handleSave}
                  />
                </FormGroup>
              </SplitItem>
              <SplitItem style={{ width: 320 }}>
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
