import * as React from "react";
import { useCallback, useState } from "react";
import "./ConstraintDateTime.css";
import { ConstraintProps } from "./Constraint";
import { ConstraintTime } from "./ConstraintTime";
import { ConstraintDate } from "./ConstraintDate";

export function ConstraintDateTime({ value, onChange, isValid, ...props }: ConstraintProps) {
  const [date, setDate] = useState(() => value.split("T")?.[0] ?? "");
  const [time, setTime] = useState(() => value.split("T")?.[1] ?? "");

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
      <div className={"kie-dmn-editor--constraint-date-time"}>
        <ConstraintDate {...props} value={date} onChange={onChangeDate} isValid={isValid} />
        <ConstraintTime {...props} value={time} onChange={onChangeTime} isValid={isValid} />
      </div>
    </>
  );
}
