import * as React from "react";
import { useMemo } from "react";
import { Label } from "@patternfly/react-core";
import { ArrowAltCircleRightIcon } from "@patternfly/react-icons";
import { MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import "./MiningSchemaFieldLabels.scss";
import { useValidationService, ValidationEntry } from "../../../validation";
import { ValidationIndicatorLabel } from "../../EditorCore/atoms";
import {
  areLowHighValuesRequired,
  isInvalidValueReplacementRequired,
  isMissingValueReplacementRequired
} from "../../../validation/MiningSchema";

interface MiningSchemaFieldLabelsProps {
  index: number;
  modelIndex: number;
  field: MiningField;
  onEdit: () => void;
  onDelete: (updatedField: MiningField) => void;
  editing: boolean;
}

const MiningSchemaFieldLabels = (props: MiningSchemaFieldLabelsProps) => {
  const { index, modelIndex, field, onEdit, onDelete, editing } = props;

  const BasicMiningLabel = (name: string, value: any, onClose: () => void) => {
    return (
      <Label
        color="cyan"
        className="mining-schema-list__item__label"
        closeBtnProps={{ className: "ignore-onclickoutside" }}
        onClose={editing ? onClose : undefined}
      >
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </Label>
    );
  };

  const InvalidMiningLabel = (
    name: string,
    value: any,
    onClose: (() => void) | undefined,
    validations: ValidationEntry[]
  ) => {
    return (
      <ValidationIndicatorLabel
        validations={validations}
        cssClass="mining-schema-list__item__label"
        onClose={editing ? onClose : undefined}
      >
        <strong>{name}:</strong>
        &nbsp;
        <span>{value}</span>
      </ValidationIndicatorLabel>
    );
  };

  const MissingValueAwareMiningLabel = (
    name: string,
    value: any,
    isValueRequired: boolean,
    validations: ValidationEntry[],
    onClose: () => void
  ) => {
    return (
      <>
        {isValueRequired && value !== undefined && BasicMiningLabel(name, value, onClose)}
        {isValueRequired && value === undefined && InvalidMiningLabel(name, <em>Missing</em>, undefined, validations)}
        {!isValueRequired && value !== undefined && InvalidMiningLabel(name, value, onClose, validations)}
      </>
    );
  };

  const { service } = useValidationService();
  const validationsImportance = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${index}].importance`),
    [modelIndex, index, field]
  );

  const validationsLowValue = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${index}].lowValue`),
    [modelIndex, index, field]
  );
  const validationsHighValue = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${index}].highValue`),
    [modelIndex, index, field]
  );
  const _areLowHighValuesRequired = useMemo(() => areLowHighValuesRequired(field.outliers), [modelIndex, index, field]);

  const validationsMissingValueReplacement = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${index}].missingValueReplacement`),
    [modelIndex, index, field]
  );
  const _isMissingValueReplacementRequired = useMemo(
    () => isMissingValueReplacementRequired(field.missingValueTreatment),
    [modelIndex, index, field]
  );

  const validationsInvalidValueReplacement = useMemo(
    () => service.get(`models[${modelIndex}].MiningSchema.MiningField[${index}].invalidValueReplacement`),
    [modelIndex, index, field]
  );
  const _isInvalidValueReplacementRequired = useMemo(
    () => isInvalidValueReplacementRequired(field.invalidValueTreatment),
    [modelIndex, index, field]
  );

  return (
    <>
      {field.usageType !== undefined &&
        BasicMiningLabel("Usage Type", field.usageType, () =>
          onDelete({
            ...field,
            usageType: undefined
          })
        )}

      {field.optype !== undefined &&
        BasicMiningLabel("Op Type", field.optype, () =>
          onDelete({
            ...field,
            optype: undefined
          })
        )}

      {field.importance !== undefined && (
        <>
          {validationsImportance.length === 0 &&
            BasicMiningLabel("Importance", field.importance, () =>
              onDelete({
                ...field,
                importance: undefined
              })
            )}
          {validationsImportance.length > 0 &&
            InvalidMiningLabel(
              "Importance",
              field.importance,
              () => onDelete({ ...field, importance: undefined }),
              validationsImportance
            )}
        </>
      )}

      {field.outliers !== undefined &&
        BasicMiningLabel("Outliers", field.outliers, () =>
          onDelete({
            ...field,
            outliers: undefined
          })
        )}

      {MissingValueAwareMiningLabel("Low Value", field.lowValue, _areLowHighValuesRequired, validationsLowValue, () =>
        onDelete({
          ...field,
          lowValue: undefined
        })
      )}

      {MissingValueAwareMiningLabel(
        "High Value",
        field.highValue,
        _areLowHighValuesRequired,
        validationsHighValue,
        () =>
          onDelete({
            ...field,
            highValue: undefined
          })
      )}

      {field.missingValueTreatment !== undefined &&
        BasicMiningLabel("Missing Value Treatment", field.missingValueTreatment, () =>
          onDelete({
            ...field,
            missingValueTreatment: undefined
          })
        )}

      {MissingValueAwareMiningLabel(
        "Missing Value Replacement",
        field.missingValueReplacement,
        _isMissingValueReplacementRequired,
        validationsMissingValueReplacement,
        () =>
          onDelete({
            ...field,
            missingValueReplacement: undefined
          })
      )}

      {field.invalidValueTreatment !== undefined &&
        BasicMiningLabel("Invalid Value Treatment", field.invalidValueTreatment, () =>
          onDelete({
            ...field,
            invalidValueTreatment: undefined
          })
        )}

      {MissingValueAwareMiningLabel(
        "Invalid Value Replacement",
        field.invalidValueReplacement,
        _isInvalidValueReplacementRequired,
        validationsInvalidValueReplacement,
        () =>
          onDelete({
            ...field,
            invalidValueReplacement: undefined
          })
      )}

      {editing && (
        <Label
          className="mining-schema-list__item__label"
          variant="outline"
          color="cyan"
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
