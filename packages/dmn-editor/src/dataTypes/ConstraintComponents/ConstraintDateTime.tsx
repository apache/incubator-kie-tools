import * as React from "react";
import { useCallback, useRef, useState } from "react";
import { DatePicker } from "@patternfly/react-core/dist/js/components/DatePicker";
import { TimePicker } from "@patternfly/react-core/dist/js/components/TimePicker";
import "./Constraint.css";
import "./ConstraintDate.css";
import "./ConstraintTime.css";
import "./ConstraintDateTime.css";
import { ConstraintProps } from "./Constraint";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { useDmnEditor } from "../../DmnEditorContext";
import { useInViewSelect } from "../../responsiveness/useInViewSelect";

const UTC_POSITEVE_TIMEZONES = [
  "+00:00",
  "+01:00",
  "+02:00",
  "+03:00",
  "+03:30",
  "+04:00",
  "+04:30",
  "+05:00",
  "+05:30",
  "+05:45",
  "+06:00",
  "+06:30",
  "+07:00",
  "+08:00",
  "+08:45",
  "+09:00",
  "+09:30",
  "+10:00",
  "+10:30",
  "+11:00",
  "+12:00",
  "+12:45",
  "+13:00",
  "+14:00",
];

const UTC_NEGATIVE_TIMEZONES = [
  "-12:00",
  "-11:00",
  "-10:00",
  "-09:30",
  "-09:00",
  "-08:00",
  "-07:00",
  "-06:00",
  "-05:00",
  "-04:00",
  "-03:30",
  "-03:00",
  "-02:00",
  "-01:00",
];

export function ConstraintDateTime({ value, onChange, isValid }: ConstraintProps) {
  const [date, setDate] = useState(() => value.split("T")?.[0] ?? "");
  const [time, setTime] = useState(() => {
    const timeWithTimezone = value.split("T")?.[1] ?? "";
    return timeWithTimezone.includes("+") ? timeWithTimezone.split("+")[0] : timeWithTimezone.split("-")[0];
  });
  const [timezone, setTimezone] = useState<string>(() =>
    value.includes("+") ? value.split("+")[1] : value.includes("-") ? value.split("-")[1] : UTC_POSITEVE_TIMEZONES[0]
  );
  const [isSelectTimezoneOpen, setSelectTimezoneOpen] = useState(false);

  const onInternalChange = useCallback(
    (args: { date?: string; time?: string; timezone?: string }) => {
      const newDate = args.date ?? date;
      const newTime = args.time ?? time;
      const newTimezone = args.timezone ?? timezone;
      if (newDate !== "" && newTime !== "" && newTimezone !== "") {
        onChange(`${newDate}T${newTime}${newTimezone}`);
      }
    },
    [date, onChange, time, timezone]
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

  const onSelectTimezone = useCallback(
    (e, value) => {
      setTimezone(value.toString());
      onInternalChange({ timezone: value.toString() });
    },
    [onInternalChange]
  );

  const { dmnEditorRootElementRef } = useDmnEditor();
  const toggleRef = useRef<HTMLButtonElement>(null);
  const inViewTimezoneSelect = useInViewSelect(dmnEditorRootElementRef, toggleRef);

  return (
    <>
      <div className={"kie-dmn-editor--constraint-date-time"}>
        <DatePicker
          className={`kie-dmn-editor--constraint-date kie-dmn-editor--constraint-input ${
            isValid ? "" : "kie-dmn-editor--constraint-date-invalid"
          }`}
          inputProps={{ className: "kie-dmn-editor--constraint-input" }}
          value={date}
          onChange={(e, value) => onChangeDate(value)}
        />
        <TimePicker
          is24Hour={true}
          className={`kie-dmn-editor--constraint-time kie-dmn-editor--constraint-input ${
            isValid ? "" : "kie-dmn-editor--constraint-invalid"
          }`}
          inputProps={{ className: "kie-dmn-editor--constraint-input" }}
          time={time}
          onChange={(e, value, hour, minute, seconds, isValid) => {
            if (isValid) {
              onChangeTime(value);
            }
          }}
          includeSeconds={true}
        />
        <Select
          toggleRef={toggleRef}
          className={`kie-dmn-editor--constraint-date-time-timezone ${
            isValid ? "" : "kie-dmn-editor--constraint-date-time-timezone-invalid"
          }`}
          variant={SelectVariant.single}
          placeholderText="Select timezone"
          aria-label="Select timezone"
          onToggle={(isExpanded) => setSelectTimezoneOpen(isExpanded)}
          onSelect={onSelectTimezone}
          selections={timezone}
          isOpen={isSelectTimezoneOpen}
          isDisabled={false}
          isPlain={true}
          maxHeight={inViewTimezoneSelect.maxHeight}
          direction={inViewTimezoneSelect.direction}
        >
          {[...UTC_NEGATIVE_TIMEZONES, ...UTC_POSITEVE_TIMEZONES].map((timezone) => (
            <SelectOption key={timezone} value={timezone} />
          ))}
        </Select>
      </div>
    </>
  );
}
