import * as React from "react";
import { useCallback, useMemo, useEffect } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import "./Constraint.css";
import { ConstraintProps } from "./Constraint";

export const REGEX_YEARS_MONTH_DURATION = /^P(?!$)((-)?\d+Y)?((-)?\d+M)?$/;

export function ConstraintYearsMonthsDuration({
  id,
  value,
  onChange,
  focusOwner,
  setFocusOwner,
  isValid,
  isDisabled,
}: ConstraintProps) {
  const years = useMemo<string>(() => getYearsDuration(value), [value]);
  const months = useMemo<string>(() => getMonthsDuration(value), [value]);

  // It should run on the first render;
  useEffect(() => {
    if (focusOwner) {
      document.getElementById(focusOwner)?.focus();
    }
  }, [focusOwner]);

  const onInternalChange = useCallback(
    (args: { years?: string; months?: string }) => {
      const y = args.years ?? years ? (args.years ?? years) + "Y" : "";
      const m = args.months ?? months ? (args.months ?? months) + "M" : "";
      const p = y || m ? "P" : "";
      onChange(`${p}${y}${m}`);
    },
    [months, onChange, years]
  );

  const onYearsChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      onInternalChange({ years: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onMonthsChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
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
            style={{ flex: "1 1 0px" }}
            className={`kie-dmn-editor--constraint-input ${isValid ? "" : "kie-dmn-editor--constraint-invalid"}`}
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
            style={{ flex: "1 1 0px" }}
            className={`kie-dmn-editor--constraint-input ${isValid ? "" : "kie-dmn-editor--constraint-invalid"}`}
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
  if (!value.includes("Y")) {
    return "";
  }
  const years = value.replace("P", "").split("Y")[0];
  if (years.length >= 1) {
    return !isNaN(parseInt(years)) ? years : "";
  }
  return "";
}

function getMonthsDuration(value: string) {
  if (!value.includes("M")) {
    return "";
  }
  let months = value.replace("P", "").split("M")[0];
  // if has year, remove it
  if (value.includes("Y")) {
    months = months.split("Y")[1];
  }
  // checks if has a value, and if its a number
  if (months.length >= 1) {
    return !isNaN(parseInt(months)) ? months : "";
  }
  return "";
}
