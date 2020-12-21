import * as React from "react";
import { Label } from "@patternfly/react-core";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons";
import { MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import "./MiningSchemaFieldLabels.scss";

interface MiningSchemaFieldLabelsProps {
  field: MiningField;
  onEdit: () => void;
  onDelete: (updatedField: MiningField) => void;
  editing: boolean;
}

const MiningSchemaFieldLabels = (props: MiningSchemaFieldLabelsProps) => {
  const { field, onEdit, onDelete, editing } = props;

  const MiningLabel = (name: string, value: any, updatedField: MiningField) => {
    return (
      <Label
        color="orange"
        className="mining-schema-list__item__label"
        closeBtnProps={{ className: "ignore-onclickoutside" }}
        onClose={editing ? () => onDelete(updatedField) : undefined}
      >
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </Label>
    );
  };

  return (
    <>
      {field.usageType !== undefined && MiningLabel("Usage Type", field.usageType, { ...field, usageType: undefined })}
      {field.optype !== undefined && MiningLabel("Op Type", field.optype, { ...field, optype: undefined })}
      {field.importance !== undefined &&
        MiningLabel("Importance", field.importance, { ...field, importance: undefined })}
      {field.outliers !== undefined && MiningLabel("Outliers", field.outliers, { ...field, outliers: undefined })}
      {field.lowValue !== undefined && MiningLabel("Low Value", field.lowValue, { ...field, lowValue: undefined })}
      {field.highValue !== undefined && MiningLabel("High Value", field.highValue, { ...field, highValue: undefined })}
      {field.missingValueReplacement !== undefined &&
        MiningLabel("Missing Value Replacement", field.missingValueReplacement, {
          ...field,
          missingValueReplacement: undefined
        })}
      {field.missingValueTreatment !== undefined &&
        MiningLabel("Missing Value Treatment", field.missingValueTreatment, {
          ...field,
          missingValueTreatment: undefined
        })}
      {field.invalidValueReplacement !== undefined &&
        MiningLabel("Missing Value Replacement", field.invalidValueReplacement, {
          ...field,
          invalidValueReplacement: undefined
        })}
      {field.invalidValueTreatment !== undefined &&
        MiningLabel("Missing Value Treatment", field.invalidValueTreatment, {
          ...field,
          invalidValueTreatment: undefined
        })}
      {editing && (
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
      )}
    </>
  );
};

export default MiningSchemaFieldLabels;
