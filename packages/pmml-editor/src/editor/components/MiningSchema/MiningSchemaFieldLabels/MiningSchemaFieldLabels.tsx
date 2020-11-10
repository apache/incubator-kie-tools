import * as React from "react";
import { Label } from "@patternfly/react-core";
import { MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import "./MiningSchemaFieldLabels.scss";

interface MiningSchemaFieldLabelsProps {
  field: MiningField;
}

const MiningSchemaFieldLabels = (props: MiningSchemaFieldLabelsProps) => {
  const { field } = props;
  return (
    <>
      {field.usageType && MiningLabel("Usage Type", field.usageType)}
      {field.optype && MiningLabel("Op Type", field.optype)}
      {field.importance && MiningLabel("Importance", field.importance)}
      {field.outliers && MiningLabel("Outliers", field.outliers)}
      {field.lowValue && MiningLabel("Low Value", field.lowValue)}
      {field.highValue && MiningLabel("High Value", field.highValue)}
      {field.missingValueReplacement && MiningLabel("Missing Value Replacement", field.missingValueReplacement)}
      {field.missingValueTreatment && MiningLabel("Missing Value Treatment", field.missingValueTreatment)}
      {field.invalidValueReplacement && MiningLabel("Missing Value Replacement", field.invalidValueReplacement)}
      {field.invalidValueTreatment && MiningLabel("Missing Value Treatment", field.invalidValueTreatment)}
    </>
  );
};

export default MiningSchemaFieldLabels;

const MiningLabel = (name: string, value: any) => {
  return (
    <Label color="blue" className="mining-schema-list__item__label">
      <strong>{name}:</strong>
      &nbsp;
      <span>{value}</span>
    </Label>
  );
};
