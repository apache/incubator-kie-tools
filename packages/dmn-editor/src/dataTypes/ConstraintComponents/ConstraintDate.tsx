import * as React from "react";
import { useState, useCallback, useEffect } from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import "./Constraint.css";
import "./ConstraintDate.css";
import { ConstraintProps } from "./Constraint";

export function ConstraintDate({ value, onChange }: ConstraintProps) {
  return (
    <>
      <DatePicker
        className={"kie-dmn-editor--constraint-date kie-dmn-editor--constraint-input"}
        inputProps={{ className: "kie-dmn-editor--constraint-input" }}
        value={value}
        onChange={(e, value) => onChange(value)}
      />
    </>
  );
}
