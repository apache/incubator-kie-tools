import * as React from "react";
import { useMemo } from "react";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import "./Constraint.css";
import "./ConstraintTime.css";
import { ConstraintProps } from "./Constraint";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";

export function ConstraintTime({ value, onChange, isValid }: ConstraintProps) {
  return (
    <div>
      <TimePicker
        is24Hour={true}
        className={"kie-dmn-editor--constraint-time kie-dmn-editor--constraint-input"}
        inputProps={{ className: "kie-dmn-editor--constraint-input" }}
        time={value}
        onChange={(e, value, hour, minute, seconds, isValid) => {
          if (isValid) {
            onChange(value);
          }
        }}
        style={isValid ? {} : invalidInlineFeelNameStyle}
      />
    </div>
  );
}
