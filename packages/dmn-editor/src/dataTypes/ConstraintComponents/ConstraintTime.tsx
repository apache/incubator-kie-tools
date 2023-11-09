import * as React from "react";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import "./Constraint.css";
import { ConstraintProps } from "./Constraint";

export function ConstraintTime(props: ConstraintProps) {
  return (
    <>
      <TimePicker is24Hour={true} inputProps={{ className: "kie-dmn-editor--constraint-input" }} />
    </>
  );
}
