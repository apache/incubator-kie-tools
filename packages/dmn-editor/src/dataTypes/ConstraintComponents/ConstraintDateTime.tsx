import * as React from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import "./Constraint.css";
import { ConstraintProps } from "./Constraint";

export function ConstraintDateTime(props: ConstraintProps) {
  return (
    <>
      <div style={{ display: "flex", flexDirection: "row" }}>
        <DatePicker
          className={"kie-dmn-editor--constraint-date"}
          inputProps={{ className: "kie-dmn-editor--constraint-input" }}
        />
        <TimePicker is24Hour={true} inputProps={{ className: "kie-dmn-editor--constraint-input" }} />
      </div>
    </>
  );
}
