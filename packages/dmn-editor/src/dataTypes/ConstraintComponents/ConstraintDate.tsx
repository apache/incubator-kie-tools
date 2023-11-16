import * as React from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import "./Constraint.css";
import "./ConstraintDate.css";
import { ConstraintProps } from "./Constraint";

export function ConstraintDate({ value, onChange, isValid }: ConstraintProps) {
  return (
    <>
      <DatePicker
        className={`kie-dmn-editor--constraint-date kie-dmn-editor--constraint-input ${
          isValid ? "" : "kie-dmn-editor--constraint-date-invalid"
        }`}
        inputProps={{ className: "kie-dmn-editor--constraint-input" }}
        value={value}
        onChange={(e, value) => onChange(value)}
      />
    </>
  );
}
