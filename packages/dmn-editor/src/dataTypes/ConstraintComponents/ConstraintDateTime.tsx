import * as React from "react";
import { useCallback, useState } from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import "./Constraint.css";
import "./ConstraintDate.css";
import "./ConstraintTime.css";
import { ConstraintProps } from "./Constraint";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";

export function ConstraintDateTime({ value, onChange, isValid }: ConstraintProps) {
  const [date, setDate] = useState(value.split("T")?.[0] ?? "");
  const [time, setTime] = useState(value.split("T")?.[1] ?? "");

  const onInternalChange = useCallback(
    (args: { date?: string; time?: string }) => {
      const newDate = args.date ?? date;
      const newTime = args.time ?? time;
      if (newDate !== "" && newTime !== "") {
        onChange(`${newDate}T${newTime}`);
      }
    },
    [date, onChange, time]
  );

  const onChangeDate = useCallback(
    (value: string) => {
      setDate(value);
      onInternalChange({ date: value });
    },
    [onInternalChange]
  );

  const onChangeTime = useCallback(
    (value: string) => {
      setTime(value);
      onInternalChange({ time: value });
    },
    [onInternalChange]
  );

  return (
    <>
      <div style={{ display: "flex", flexDirection: "row" }}>
        <DatePicker
          className={"kie-dmn-editor--constraint-date kie-dmn-editor--constraint-input"}
          inputProps={{ className: "kie-dmn-editor--constraint-input" }}
          value={date}
          onChange={(e, value) => onChangeDate(value)}
          style={isValid ? {} : invalidInlineFeelNameStyle}
        />
        <TimePicker
          is24Hour={true}
          className={"kie-dmn-editor--constraint-time kie-dmn-editor--constraint-input"}
          inputProps={{ className: "kie-dmn-editor--constraint-input" }}
          value={time}
          onChange={(e, value) => onChangeTime(value)}
          style={isValid ? {} : invalidInlineFeelNameStyle}
        />
      </div>
    </>
  );
}
