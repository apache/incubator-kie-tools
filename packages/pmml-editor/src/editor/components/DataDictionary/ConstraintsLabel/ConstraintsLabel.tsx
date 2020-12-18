import * as React from "react";
import { useMemo } from "react";
import { Label, LabelProps } from "@patternfly/react-core";
import { Constraints } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsLabel.scss";

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
        return (
          `${constraints.value[0].start.included ? "[" : "("}` +
          `${constraints.value[0].start.value},` +
          `${constraints.value[0].end.value}` +
          `${constraints.value[0].end.included ? "]" : ")"}`
        );
      case "Enumeration":
        return constraints.value.map(item => `"${item.value}"`).join(", ");
      default:
        return "";
    }
  }, [constraints]);

  return (
    <Label color="orange" className="constraints-label" {...labelProps}>
      <strong>Constraints:</strong>&nbsp;
      <span>{`${constraints.type} ${constraintValue}`}</span>
    </Label>
  );
};

export default ConstraintsLabel;
