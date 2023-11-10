import * as React from "react";
import { useCallback, useState, useEffect } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import "./Constraint.css";
import { ConstraintProps } from "./Constraint";
import { invalidInlineFeelNameStyle } from "../../feel/InlineFeelNameInput";

export const REGEX_YEARS_MONTH_DURATION = /^P(?!$)(\d+Y)?(\d+M)?$/;

export function ConstraintYearsMonthsDuration({
  id,
  value,
  onChange,
  focusOwner,
  setFocusOwner,
  isValid,
  isDisabled,
}: ConstraintProps) {
  const [years, setYears] = useState<string>(getYearsDuration(value));
  const [months, setMonths] = useState<string>(getMonthsDuration(value));

  // It should run on the first render;
  useEffect(() => {
    if (focusOwner) {
      document.getElementById(focusOwner)?.focus();
    }
  }, [focusOwner]);

  const onInternalChange = useCallback(
    (args: { years?: string; months?: string }) => {
      onChange(
        `P${args.years ?? years ? (args.years ?? years) + "Y" : ""}${
          args.months ?? months ? (args.months ?? months) + "M" : ""
        }`
      );
    },
    [months, onChange, years]
  );

  const onYearsChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      setYears(newValue);
      onInternalChange({ years: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onMonthsChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      setMonths(newValue);
      onInternalChange({ months: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  return (
    <>
      <div style={{ display: "flex", flexDirection: "row" }}>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>Y:</p>
          <TextInput
            id={`${id}-constraint-years`}
            type={"number"}
            placeholder={"Years"}
            style={{ flex: "1 1 0px", ...(isValid ? {} : invalidInlineFeelNameStyle) }}
            className={"kie-dmn-editor--constraint-input"}
            value={years}
            onChange={onYearsChange}
            autoFocus={true}
            isDisabled={isDisabled}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>M:</p>
          <TextInput
            id={`${id}-constraint-months`}
            type={"number"}
            placeholder={"Months"}
            style={{ flex: "1 1 0px", ...(isValid ? {} : invalidInlineFeelNameStyle) }}
            className={"kie-dmn-editor--constraint-input"}
            value={months}
            onChange={onMonthsChange}
            isDisabled={isDisabled}
          />
        </div>
      </div>
    </>
  );
}

function getYearsDuration(value: string) {
  const years = value.replace("P", "").split("Y")[0];
  if (years.length >= 1) {
    return !isNaN(parseInt(years)) ? years : "";
  }
  return "";
}

function getMonthsDuration(value: string) {
  const months = value.replace("P", "").split("Y")[1];
  if (months && months.length >= 1) {
    const monthsValue = months.replace("M", "");
    return !isNaN(parseInt(monthsValue)) ? monthsValue : "";
  }
  return "";
}
