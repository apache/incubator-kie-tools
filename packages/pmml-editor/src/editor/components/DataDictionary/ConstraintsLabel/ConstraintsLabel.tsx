import * as React from "react";
import { Label } from "@patternfly/react-core";
import { Constraints } from "../DataDictionaryContainer/DataDictionaryContainer";

interface ConstraintsLabelProps {
  constraints: Constraints;
}
const ConstraintsLabel = ({ constraints }: ConstraintsLabelProps) => {
  let constraintValue;
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
  return <Label color="orange">{`${constraints.type} ${constraintValue}`}</Label>;
};

export default ConstraintsLabel;
