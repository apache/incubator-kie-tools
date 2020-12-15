import * as React from "react";
import { useContext, useMemo } from "react";
import { Label, LabelProps } from "@patternfly/react-core";
import { Constraints, StatusContext } from "../DataDictionaryContainer/DataDictionaryContainer";
import "./ConstraintsLabel.scss";

interface ConstraintsLabelProps {
  constraints: Constraints;
  onConstraintsDelete?: () => void;
}
const ConstraintsLabel = ({ constraints, onConstraintsDelete }: ConstraintsLabelProps) => {
  const editing = useContext(StatusContext);

  const labelProps: Partial<LabelProps> = {};

  if (editing !== false) {
    labelProps.onClose = event => {
      event.nativeEvent.stopImmediatePropagation();
      onConstraintsDelete?.();
    };
  }

  const constraintValue = useMemo(() => {
    let value;
    switch (constraints.type) {
      case "Range":
        value = `${constraints.value.start.included ? "[" : "("}${constraints.value.start.value}, ${
          constraints.value.end.value
        }${constraints.value.end.included ? "]" : ")"}`;
        break;
      case "Enumeration":
        const enums = constraints.value.map(item => `"${item.value}"`);
        value = `${enums.join(", ")}`;
        break;
      default:
        value = "";
        break;
    }
    return value;
  }, [constraints]);

  return (
    <Label color="orange" className="constraints-label" {...labelProps}>
      <strong>Constraints:</strong>&nbsp;
      <span>{`${constraints.type} ${constraintValue}`}</span>
    </Label>
  );
};

export default ConstraintsLabel;
