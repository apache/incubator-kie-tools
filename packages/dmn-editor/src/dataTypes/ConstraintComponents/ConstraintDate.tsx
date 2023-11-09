import * as React from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import "./Constraint.css";
import "./ConstraintDate.css";
import { ConstraintProps } from "./Constraint";

export function ConstraintDate(props: ConstraintProps) {
  return (
    <>
      <DatePicker
        className={"kie-dmn-editor--constraint-date"}
        inputProps={{ className: "kie-dmn-editor--constraint-input" }}
      />
    </>
  );
}
