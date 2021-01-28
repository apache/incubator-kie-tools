import * as React from "react";
import { useMemo } from "react";
import { Label, LabelProps } from "@patternfly/react-core";
import { Constraints } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsLabel.scss";
import { ValidationIndicator } from "../../EditorCore/atoms";
import { useValidationService } from "../../../validation";

interface ConstraintsLabelProps {
  editingIndex?: number;
  constraints: Constraints;
  onConstraintsDelete?: () => void;
}

const ConstraintsLabel = ({ editingIndex, constraints, onConstraintsDelete }: ConstraintsLabelProps) => {
  const labelProps: Partial<LabelProps> = {};

  if (editingIndex !== undefined) {
    labelProps.onClose = event => {
      event.nativeEvent.stopImmediatePropagation();
      onConstraintsDelete?.();
    };
  }

  const constraintValue = useMemo(() => {
    switch (constraints.type) {
      case "Range":
        return constraints.value
          .map(range => {
            return (
              `${range.start.included ? "[" : "("}` +
              `${range.start.value || `${String.fromCharCode(8722, 8734)}`}, ` +
              `${range.end.value || `${String.fromCharCode(43, 8734)}`}` +
              `${range.end.included ? "]" : ")"}`
            );
          })
          .join(" ");

      case "Enumeration":
        return constraints.value.map(item => `"${item}"`).join(", ");
      default:
        return "";
    }
  }, [constraints]);

  const { service } = useValidationService();
  const validations = useMemo(() => service.get(`DataDictionary.DataField[${editingIndex}].Interval`), [editingIndex]);

  return (
    <>
      {validations.length > 0 && (
        <span className="constraints-label">
          <ValidationIndicator validations={validations} />
        </span>
      )}
      <Label color="orange" className="constraints-label" {...labelProps}>
        <strong>Constraints:</strong>&nbsp;
        <span>{constraintValue}</span>
      </Label>
    </>
  );
};

export default ConstraintsLabel;
