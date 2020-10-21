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
      constraintValue = `(${constraints.start.value},${constraints.end.value})`;
  }
  return <Label color="orange">{`${constraints.type} ${constraintValue}`}</Label>;
};

export default ConstraintsLabel;
