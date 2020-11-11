import * as React from "react";
import { Label } from "@patternfly/react-core";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons";
import { MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import "./MiningSchemaFieldLabels.scss";

interface MiningSchemaFieldLabelsProps {
  field: MiningField;
  onEdit: () => void;
  onDelete: (updatedField: MiningField) => void;
}

const MiningSchemaFieldLabels = (props: MiningSchemaFieldLabelsProps) => {
  const { field, onEdit, onDelete } = props;

  const MiningLabel = (name: string, value: any, updatedField: MiningField) => {
    return (
      <Label color="orange" className="mining-schema-list__item__label" onClose={() => onDelete(updatedField)}>
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </Label>
    );
  };

  return (
    <>
      {field.usageType && MiningLabel("Usage Type", field.usageType, { ...field, usageType: undefined })}
      {field.optype && MiningLabel("Op Type", field.optype, { ...field, optype: undefined })}
      {field.importance && MiningLabel("Importance", field.importance, { ...field, importance: undefined })}
      {field.outliers && MiningLabel("Outliers", field.outliers, { ...field, outliers: undefined })}
      {field.lowValue && MiningLabel("Low Value", field.lowValue, { ...field, lowValue: undefined })}
      {field.highValue && MiningLabel("High Value", field.highValue, { ...field, highValue: undefined })}
      {field.missingValueReplacement &&
        MiningLabel("Missing Value Replacement", field.missingValueReplacement, {
          ...field,
          missingValueReplacement: undefined
        })}
      {field.missingValueTreatment &&
        MiningLabel("Missing Value Treatment", field.missingValueTreatment, {
          ...field,
          missingValueTreatment: undefined
        })}
      {field.invalidValueReplacement &&
        MiningLabel("Missing Value Replacement", field.invalidValueReplacement, {
          ...field,
          invalidValueReplacement: undefined
        })}
      {field.invalidValueTreatment &&
        MiningLabel("Missing Value Treatment", field.invalidValueTreatment, {
          ...field,
          invalidValueTreatment: undefined
        })}
      <Label
        className="mining-schema-list__item__label"
        variant="outline"
        color="orange"
        href="#"
        icon={<ArrowAltCircleRightIcon />}
        onClick={event => {
          event.preventDefault();
          onEdit();
        }}
      >
        Edit Properties
      </Label>
    </>
  );
};

export default MiningSchemaFieldLabels;
