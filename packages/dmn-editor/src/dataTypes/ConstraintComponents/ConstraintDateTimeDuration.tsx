import * as React from "react";
import { useState, useCallback, useEffect } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import "./Constraint.css";
import { ConstraintProps } from "./Constraint";
import moment from "moment";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";

export function ConstraintDateTimeDuration({
  value,
  onChange,
  focusOwner,
  setFocusOwner,
  isValid,
  isDisabled,
}: ConstraintProps) {
  const [days, setDays] = useState<string>("");
  const [hours, setHours] = useState<string>("");
  const [minutes, setMinutes] = useState<string>("");
  const [seconds, setSeconds] = useState<string>("");

  // It should run on the first render;
  useEffect(() => {
    const duration = moment.duration(value);
    if (duration.isValid()) {
      setDays(getDaysDuration(value));
      setHours(getHoursDuration(value));
      setMinutes(getMinutesDuration(value));
      setSeconds(getSecondsDuration(value));
    }
    if (focusOwner) {
      document.getElementById(focusOwner)?.focus();
    }
  }, [focusOwner]);

  const onInternalChange = useCallback(
    (args: { days?: string; hours?: string; minutes?: string; seconds?: string }) => {
      onChange(
        `P${args.days ?? days ? (args.days ?? days) + "D" : ""}${
          args.hours ?? (hours || args.minutes) ?? (minutes || args.seconds) ?? seconds ? "T" : ""
        }${args.hours ?? hours ? (args.hours ?? hours) + "H" : ""}${
          args.minutes ?? minutes ? (args.minutes ?? minutes) + "M" : ""
        }${args.seconds ?? seconds ? (args.seconds ?? seconds) + "S" : ""}`
      );
    },
    [days, hours, minutes, onChange, seconds]
  );

  const onDaysChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      setDays(newValue);
      onInternalChange({ days: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onHoursChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      setHours(newValue);
      onInternalChange({ hours: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onMinutesChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      setMinutes(newValue);
      onInternalChange({ minutes: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onSecondsChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      setSeconds(newValue);
      onInternalChange({ seconds: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  return (
    <>
      <div style={{ display: "flex", flexDirection: "row" }}>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>D:</p>
          <TextInput
            id={"days"}
            type={"number"}
            placeholder={"Days"}
            className={"kie-dmn-editor--constraint-input"}
            value={days}
            onChange={onDaysChange}
            style={isValid ? {} : invalidInlineFeelNameStyle}
            isDisabled={isDisabled}
            autoFocus={true}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>H:</p>
          <TextInput
            id={"hours"}
            type={"number"}
            placeholder={"Hours"}
            className={"kie-dmn-editor--constraint-input"}
            value={hours}
            onChange={onHoursChange}
            style={isValid ? {} : invalidInlineFeelNameStyle}
            isDisabled={isDisabled}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>M:</p>
          <TextInput
            id={"minutes"}
            type={"number"}
            placeholder={"Minutes"}
            className={"kie-dmn-editor--constraint-input"}
            value={minutes}
            onChange={onMinutesChange}
            style={isValid ? {} : invalidInlineFeelNameStyle}
            isDisabled={isDisabled}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>S:</p>
          <TextInput
            id={"seconds"}
            type={"number"}
            placeholder={"Seconds"}
            className={"kie-dmn-editor--constraint-input"}
            value={seconds}
            onChange={onSecondsChange}
            style={isValid ? {} : invalidInlineFeelNameStyle}
            isDisabled={isDisabled}
          />
        </div>
      </div>
    </>
  );
}

function getDaysDuration(value: string) {
  const days = value.replace("P", "").split("T")[0];
  if (days.length > 1) {
    const daysValue = days.replace("D", "");
    return !isNaN(parseInt(daysValue)) ? daysValue : "";
  }
  return "";
}

function getHoursDuration(value: string) {
  const time = value.replace("P", "").split("T")[1];
  if (time && time.length > 1) {
    const hoursValue = time.split("H")[0];
    if (hoursValue.length > 1) {
      return !isNaN(parseInt(hoursValue)) ? hoursValue : "";
    }
  }
  return "";
}

function getMinutesDuration(value: string) {
  const time = value.replace("P", "").split("T")[1];
  if (time && time.length > 1) {
    const minutes = time.split("H")[1];
    if (minutes && minutes.length > 1) {
      const minutesValue = minutes.replace("M", "");
      return !isNaN(parseInt(minutesValue)) ? minutesValue : "";
    }
  }
  return "";
}

function getSecondsDuration(value: string) {
  const time = value.replace("P", "").split("T")[1];
  if (time && time.length > 1) {
    const seconds = time.split("M")[1];
    if (seconds && seconds.length > 1) {
      const secondsValue = seconds.replace("S", "");
      return !isNaN(parseInt(secondsValue)) ? secondsValue : "";
    }
  }
  return "";
}
