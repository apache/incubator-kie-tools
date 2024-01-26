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
import { useMemo, useCallback } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { ConstraintComponentProps } from "./Constraints";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";

const RANGE_CONSTRAINT_SEPARATOR = "..";
const CONSTRAINT_START_ID = "start";
const CONSTRAINT_END_ID = "end";

export function ConstraintsRange({
  isReadonly,
  value,
  expressionValue,
  type,
  typeHelper,
  onSave,
  isDisabled,
  renderOnPropertiesPanel,
}: ConstraintComponentProps) {
  const start = useMemo(
    () => typeHelper.recover(isRange(value ?? "", typeHelper.check)?.[0]) ?? "",
    [typeHelper, value]
  );
  const end = useMemo(() => typeHelper.recover(isRange(value ?? "", typeHelper.check)?.[1]) ?? "", [typeHelper, value]);
  const includeStart = useMemo(() => {
    if (value === undefined) {
      return true;
    }
    if (hasRangeStartStructure(value)) {
      if (value.charAt(0) === "[") {
        return true;
      }
      return false;
    }
    return true;
  }, [value]);
  const includeEnd = useMemo(() => {
    if (value === undefined) {
      return false;
    }
    if (hasRangeEndStructure(value)) {
      if (value.charAt(value.length - 1) === "]") {
        return true;
      }
      return false;
    }
    return false;
  }, [value]);

  const isStartValid = useCallback(
    (args: { includeEnd: boolean; start: string; end: string }) => {
      if (type === DmnBuiltInDataType.String) {
        return true;
      }
      const parsedEnd = typeHelper.parse(args.end);
      const parsedStart = typeHelper.parse(args.start);
      return args.end !== "" ? (args.includeEnd ? parsedEnd >= parsedStart : parsedEnd > parsedStart) : true;
    },
    [type, typeHelper]
  );

  const isEndValid = useCallback(
    (args: { includeEnd: boolean; start: string; end: string }) => {
      if (type === DmnBuiltInDataType.String) {
        return true;
      }
      const parsedEnd = typeHelper.parse(args.end);
      const parsedStart = typeHelper.parse(args.start);
      return args.start !== "" ? (args.includeEnd ? parsedEnd >= parsedStart : parsedEnd > parsedStart) : true;
    },
    [type, typeHelper]
  );

  const onInternalChange = useCallback(
    (args?: { start?: string; end?: string; includeStart?: boolean; includeEnd?: boolean }) => {
      if (args === undefined) {
        return;
      }

      const internalStart = args?.start ?? start;
      const internalEnd = args?.end ?? end;

      if (internalStart === "" && internalEnd === "") {
        onSave("");
        return;
      }

      onSave(
        `${args?.includeStart ?? includeStart ? "[" : "("}${typeHelper.transform(
          internalStart
        )}..${typeHelper.transform(internalEnd)}${args?.includeEnd ?? includeEnd ? "]" : ")"}`
      );
    },
    [end, includeEnd, includeStart, onSave, start, typeHelper]
  );

  const onStartChange = useCallback(
    (newStartValue: string) => {
      onInternalChange({ start: newStartValue });
    },
    [onInternalChange]
  );

  const onEndChange = useCallback(
    (newEndValue: string) => {
      onInternalChange({ end: newEndValue });
    },
    [onInternalChange]
  );

  const onIncludeStartToogle = useCallback(() => {
    onInternalChange({ includeStart: !includeStart });
  }, [includeStart, onInternalChange]);

  const onIncludeEndToogle = useCallback(() => {
    onInternalChange({ includeEnd: !includeEnd });
  }, [includeEnd, onInternalChange]);

  const messages = useCallback(
    (value: string, operator: string) => {
      if (type === DmnBuiltInDataType.Date && value !== "") {
        return `The next valid number is: (${value} ${operator} 1 Day).`;
      }
      if (type === DmnBuiltInDataType.DateTime && value !== "") {
        return `The next valid number is: (${value} ${operator} 1 Second).`;
      }
      if (type === DmnBuiltInDataType.DateTimeDuration && value !== "") {
        return `The next valid number is: (${value} ${operator} 1 Second).`;
      }
      if (type === DmnBuiltInDataType.Number && value !== "") {
        return `The next valid number is: (${value} ${operator} 2e-52).`;
      }
      if (type === DmnBuiltInDataType.Time && value !== "") {
        return `The next valid number is: (${value} ${operator} 1 Second).`;
      }
      if (type === DmnBuiltInDataType.YearsMonthsDuration && value !== "") {
        return `The next valid number is: (${value} ${operator} 1 Month).`;
      }
      return "";
    },
    [type]
  );

  const onKeyDown = useCallback((e: React.KeyboardEvent<HTMLInputElement>) => {
    // Check boundary for "." before performing the action
    if (
      e.currentTarget.value[e.currentTarget.selectionStart ?? 0] === "." &&
      e.currentTarget.value[(e.currentTarget.selectionStart ?? 2) - 2] === "." &&
      e.key === "Backspace"
    ) {
      e.preventDefault();
    }
    if (
      (e.currentTarget.value[e.currentTarget.selectionStart ?? 0] === "." ||
        e.currentTarget.value[(e.currentTarget.selectionStart ?? 1) - 1] === ".") &&
      e.key === "."
    ) {
      e.preventDefault();
    }
  }, []);

  return (
    <div>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "auto auto 1fr",
          gridTemplateRows: "1fr 50px 70px 1fr 50px",
          gridTemplateAreas: `
          'start includeStartButton startField'
          'empty1 arrow startDescription'
          'empty2 arrow empty3'
          'end includeEndButton endField'
          'empty4 empty5 endDescription'
          `,
          columnGap: "10px",
          alignItems: "center",
        }}
      >
        <div style={{ gridArea: "start" }}>
          <Label>Start</Label>
        </div>
        <div style={{ gridArea: "includeStartButton" }}>
          <Tooltip
            content={includeStart ? "Click to remove value from the range" : "Click to include value in the range"}
          >
            <button
              id={CONSTRAINT_START_ID}
              disabled={isReadonly || isDisabled}
              onClick={() => onIncludeStartToogle()}
              style={{
                borderRadius: "100%",
                borderColor: "rgb(90 90 90)",
                borderStyle: "solid",
                borderWidth: "2px",
                width: "20px",
                height: "20px",
                backgroundColor: includeStart ? "rgb(90 90 90)" : "transparent",
              }}
            />
          </Tooltip>
        </div>
        <div style={{ gridArea: "startField" }}>
          {typeHelper.component({
            autoFocus: start === "",
            onBlur: () => onInternalChange(),
            onChange: onStartChange,
            id: "start-value",
            isDisabled: isReadonly || isDisabled,
            placeholder: "Starts with",
            style: {
              outline: "none",
            },
            value: start,
            isValid: isStartValid({ includeEnd, start, end }),
            onKeyDown,
          })}
        </div>
        <div style={{ gridArea: "startDescription" }}>
          <HelperText>
            <HelperTextItem variant="indeterminate">
              {includeStart
                ? "The starting value will be included in the range."
                : `The starting value will not be included in the range. ${messages(start, "+")}`}
            </HelperTextItem>
          </HelperText>
        </div>

        <div style={{ gridArea: "arrow", justifySelf: "center", alignSelf: "center", height: "100%" }}>
          <div
            style={{
              borderLeftStyle: "solid",
              borderLeftColor: "rgb(90 90 90)",
              borderLeftWidth: "2px",
              height: "calc(100% + 18px)",
              marginTop: "-10px",
              marginBottom: "-8px",
            }}
          />
        </div>

        <div style={{ gridArea: "end" }}>
          <Label>End</Label>
        </div>
        <div style={{ gridArea: "includeEndButton" }}>
          <Tooltip
            content={includeEnd ? "Click to remove value from the range" : "Click to include value in the range"}
          >
            <button
              id={CONSTRAINT_END_ID}
              disabled={isReadonly || isDisabled}
              onClick={() => onIncludeEndToogle()}
              style={{
                borderRadius: "100%",
                borderColor: "rgb(90 90 90)",
                borderStyle: "solid",
                borderWidth: "2px",
                width: "20px",
                height: "20px",
                backgroundColor: includeEnd ? "rgb(90 90 90)" : "transparent",
              }}
            />
          </Tooltip>
        </div>
        <div style={{ gridArea: "endField" }}>
          {typeHelper.component({
            autoFocus: start !== "",
            onBlur: () => onInternalChange(),
            onChange: onEndChange,
            id: "end-value",
            isDisabled: isReadonly || isDisabled,
            placeholder: "Ends with",
            style: { outline: "none" },
            value: end,
            isValid: isEndValid({ includeEnd, start, end }),
            onKeyDown,
          })}
        </div>
        <div style={{ gridArea: "endDescription" }}>
          <HelperText>
            <HelperTextItem variant="indeterminate">
              {includeEnd
                ? "The ending value will be included in the range."
                : `The ending value will not be included in the range. ${messages(end, "-")}`}
            </HelperTextItem>
          </HelperText>
        </div>
      </div>
      {!renderOnPropertiesPanel && (
        <>
          <br />
          <ConstraintsExpression isReadonly={true} value={expressionValue ?? ""} type={type} />
        </>
      )}
    </div>
  );
}

export function hasRangeStartStructure(value: string): boolean {
  return value.startsWith("(") || value.startsWith("[") || value.startsWith("]");
}

export function hasRangeEndStructure(value: string): boolean {
  return value.endsWith(")") || value.endsWith("]") || value.startsWith("[");
}

export function hasRangeStructure(value: string): boolean {
  return (
    hasRangeStartStructure(value) && hasRangeEndStructure(value) && value.split(RANGE_CONSTRAINT_SEPARATOR).length === 2
  );
}

export function isRange(value?: string, typeCheck?: (value: string) => boolean): [string, string] | undefined {
  if (value === undefined) {
    return undefined;
  }

  if (!hasRangeStructure(value)) {
    return undefined;
  }

  const rangeValues = value.split(RANGE_CONSTRAINT_SEPARATOR);
  if (rangeValues.length === 2 && typeCheck?.(rangeValues[0].slice(1)) && typeCheck?.(rangeValues[1].slice(0, -1))) {
    return [rangeValues[0].slice(1), rangeValues[1].slice(0, -1)];
  }

  return undefined;
}
