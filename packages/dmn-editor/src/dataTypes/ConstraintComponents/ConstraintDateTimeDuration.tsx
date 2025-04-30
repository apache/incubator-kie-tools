/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useMemo, useCallback, useEffect } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import "./Constraint.css";
import { ConstraintProps } from "./Constraint";

export const REGEX_DATE_TIME_DURATION = /^P(?!$)((-)?\d+D)?(T(?=(-)?\d)((-)?\d+H)?((-)?\d+M)?((-)?\d+S)?)?$/;

export function ConstraintDateTimeDuration({
  id,
  value,
  onChange,
  focusOwner,
  setFocusOwner,
  isValid,
  isDisabled,
}: ConstraintProps) {
  const days = useMemo<string>(() => getDaysDuration(value), [value]);
  const hours = useMemo<string>(() => getHoursDuration(value), [value]);
  const minutes = useMemo<string>(() => getMinutesDuration(value), [value]);
  const seconds = useMemo<string>(() => getSecondsDuration(value), [value]);

  // It should run on the first render;
  useEffect(() => {
    if (focusOwner) {
      document.getElementById(focusOwner)?.focus();
    }
  }, [focusOwner]);

  const onInternalChange = useCallback(
    (args: { days?: string; hours?: string; minutes?: string; seconds?: string }) => {
      const d = args.days ?? days ? (args.days ?? days) + "D" : "";
      const h = args.hours ?? hours ? (args.hours ?? hours) + "H" : "";
      const m = args.minutes ?? minutes ? (args.minutes ?? minutes) + "M" : "";
      const s = args.seconds ?? seconds ? (args.seconds ?? seconds) + "S" : "";
      const t = h || m || s ? "T" : "";
      const p = d || h || m || s ? "P" : "";
      onChange(`${p}${d}${t}${h}${m}${s}`);
    },
    [days, hours, minutes, onChange, seconds]
  );

  const onDaysChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      onInternalChange({ days: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onHoursChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      onInternalChange({ hours: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onMinutesChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
      onInternalChange({ minutes: newValue });
      setFocusOwner?.(e.currentTarget.id);
    },
    [onInternalChange, setFocusOwner]
  );

  const onSecondsChange = useCallback(
    (newValue: string, e: React.FormEvent<HTMLInputElement>) => {
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
            id={`${id}-constraint-days`}
            type={"number"}
            placeholder={"Days"}
            className={`kie-dmn-editor--constraint-input ${isValid ? "" : "kie-dmn-editor--constraint-invalid"}`}
            value={days}
            onChange={(event, val) => onDaysChange(val, event)}
            isDisabled={isDisabled}
            autoFocus={true}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>H:</p>
          <TextInput
            id={`${id}-constraint-hours`}
            type={"number"}
            placeholder={"Hours"}
            className={`kie-dmn-editor--constraint-input ${isValid ? "" : "kie-dmn-editor--constraint-invalid"}`}
            value={hours}
            onChange={(event, val) => onHoursChange(val, event)}
            isDisabled={isDisabled}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>M:</p>
          <TextInput
            id={`${id}-constraint-minutes`}
            type={"number"}
            placeholder={"Minutes"}
            className={`kie-dmn-editor--constraint-input ${isValid ? "" : "kie-dmn-editor--constraint-invalid"}`}
            value={minutes}
            onChange={(event, val) => onMinutesChange(val, event)}
            isDisabled={isDisabled}
          />
        </div>
        <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "center" }}>
          <p>S:</p>
          <TextInput
            id={`${id}-constraint-seconds`}
            type={"number"}
            placeholder={"Seconds"}
            className={`kie-dmn-editor--constraint-input ${isValid ? "" : "kie-dmn-editor--constraint-invalid"}`}
            value={seconds}
            onChange={(event, val) => onSecondsChange(val, event)}
            isDisabled={isDisabled}
          />
        </div>
      </div>
    </>
  );
}

function getDaysDuration(value: string) {
  if (!value.includes("D")) {
    return "";
  }
  const days = value.replace("P", "").split("D")[0];
  if (days.length >= 1) {
    return !isNaN(parseInt(days)) ? days : "";
  }
  return "";
}

function getHoursDuration(value: string) {
  if (!value.includes("T") || !value.includes("H")) {
    return "";
  }
  let hours = value.replace("P", "").replace("T", "").split("H")[0];
  if (hours.includes("D")) {
    hours = hours.split("D")[1];
  }
  if (hours.length >= 1) {
    return !isNaN(parseInt(hours)) ? hours : "";
  }
  return "";
}

function getMinutesDuration(value: string) {
  if (!value.includes("T") || !value.includes("M")) {
    return "";
  }
  let hours = value.replace("P", "").replace("T", "").split("M")[0];
  if (hours.includes("D")) {
    hours = hours.split("D")[1];
  }
  if (hours.includes("H")) {
    hours = hours.split("H")[1];
  }
  if (hours.length >= 1) {
    return !isNaN(parseInt(hours)) ? hours : "";
  }
  return "";
}

function getSecondsDuration(value: string) {
  if (!value.includes("T") || !value.includes("S")) {
    return "";
  }
  let hours = value.replace("P", "").replace("T", "").split("S")[0];
  if (hours.includes("D")) {
    hours = hours.split("D")[1];
  }
  if (hours.includes("H")) {
    hours = hours.split("H")[1];
  }
  if (hours.includes("M")) {
    hours = hours.split("M")[1];
  }
  if (hours.length >= 1) {
    return !isNaN(parseInt(hours)) ? hours : "";
  }
  return "";
}
