import * as React from "react";
import { useContext } from "react";
import { Label, LabelProps } from "@patternfly/react-core";
import { Constraints, StatusContext } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsLabel.scss";

interface ConstraintsLabelProps {
  constraints: Constraints;
  onConstraintsDelete?: () => void;
}
const ConstraintsLabel = ({ constraints, onConstraintsDelete }: ConstraintsLabelProps) => {
  let constraintValue;
  const editing = useContext(StatusContext);

  const labelProps: Partial<LabelProps> = {};

  if (editing !== false) {
    labelProps.onClose = event => {
      event.nativeEvent.stopImmediatePropagation();
      onConstraintsDelete?.();
    };
  }

  switch (constraints.type) {
    case "Range":
      constraintValue = `${constraints.start.included ? "[" : "("}${constraints.start.value}, ${constraints.end.value}${
        constraints.end.included ? "]" : ")"
      }`;
      break;
    case "Enumeration":
      const enums = constraints.value.map(item => `"${item.value}"`);
      constraintValue = `${enums.join(", ")}`;
      break;
    default:
      constraintValue = "";
      break;
  }
  return (
    <Label color="orange" className="constraints-label" {...labelProps}>
      <strong>Constraints:</strong>&nbsp;
      <span>{`${constraints.type} ${constraintValue}`}</span>
    </Label>
  );
};

export default ConstraintsLabel;
