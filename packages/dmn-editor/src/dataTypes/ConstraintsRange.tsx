import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import InfoIcon from "@patternfly/react-icons/dist/js/icons/info-icon";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { TypeHelper } from "./Constraints";

const CONSTRAINT_START_ID = "start";
const CONSTRAINT_END_ID = "end";

export function ConstraintsRange({
  isReadonly,
  value,
  savedValue,
  type,
  typeHelper,
  onChange,
  isDisabled,
  setConstraintValidity,
}: {
  isReadonly: boolean;
  value?: string;
  savedValue?: string;
  type: DmnBuiltInDataType;
  typeHelper: TypeHelper;
  onChange: (newValue: string | undefined) => void;
  isDisabled: boolean;
  setConstraintValidity: (isValid: boolean) => void;
}) {
  const [start, setStart] = useState(isRange(value ?? "", typeHelper.check)?.[0] ?? "");
  const [end, setEnd] = useState(isRange(value ?? "", typeHelper.check)?.[1] ?? "");
  const [includeStart, setIncludeStart] = useState(true);
  const [includeEnd, setIncludeEnd] = useState(false);

  const isStartValid = useCallback(
    (args: { includeEnd: boolean; start: string; end: string }) => {
      return args.end !== ""
        ? args.includeEnd
          ? typeHelper.parse(args.end) >= typeHelper.parse(args.start)
          : typeHelper.parse(args.end) > typeHelper.parse(args.start)
        : true;
    },
    [typeHelper]
  );

  const isEndValid = useCallback(
    (args: { includeEnd: boolean; start: string; end: string }) => {
      return args.start !== ""
        ? args.includeEnd
          ? typeHelper.parse(args.end) >= typeHelper.parse(args.start)
          : typeHelper.parse(args.end) > typeHelper.parse(args.start)
        : true;
    },
    [typeHelper]
  );

  const onInternalChange = useCallback(
    (args?: { start?: string; end?: string; includeStart?: boolean; includeEnd?: boolean }) => {
      if (
        args !== undefined &&
        (args?.start === undefined || args.start === "") &&
        (args?.end === undefined || args.end === "")
      ) {
        onChange("");
      }

      if ((args?.start !== undefined && args.start === "") || (args?.end !== undefined && args.end === "")) {
        return;
      }

      if ((args?.start === undefined && start === "") || (args?.end === undefined && end === "")) {
        return;
      }

      setConstraintValidity(
        isStartValid({
          includeEnd: args?.includeEnd ?? includeEnd,
          start: args?.start ?? start,
          end: args?.end ?? end,
        }) &&
          isEndValid({
            includeEnd: args?.includeEnd ?? includeEnd,
            start: args?.start ?? start,
            end: args?.end ?? end,
          })
      );
      onChange(
        `${args?.includeStart ?? includeStart ? "[" : "("}${typeHelper.transform(
          args?.start ?? start
        )}..${typeHelper.transform(args?.end ?? end)}${args?.includeEnd ?? includeEnd ? "]" : ")"}`
      );
    },
    [end, includeEnd, includeStart, isEndValid, isStartValid, onChange, setConstraintValidity, start, typeHelper]
  );

  const onStartChange = useCallback(
    (newStartValue: string) => {
      setStart(newStartValue);
      onInternalChange({ start: newStartValue });
    },
    [onInternalChange]
  );

  const onEndChange = useCallback(
    (newEndValue: string) => {
      setEnd(newEndValue);
      onInternalChange({ end: newEndValue });
    },
    [onInternalChange]
  );

  const onIncludeStartToogle = useCallback(() => {
    setIncludeStart((prev) => {
      onInternalChange({ includeStart: !prev });
      return !prev;
    });
  }, [onInternalChange]);

  const onIncludeEndToogle = useCallback(() => {
    setIncludeEnd((prev) => {
      onInternalChange({ includeEnd: !prev });
      return !prev;
    });
  }, [onInternalChange]);

  const nextStartValue = useMemo(() => {
    if (typeof typeHelper.parse(value ?? "") === "number" && start !== "") {
      return `The next valid number is: (${start} + 2e-52).`;
    }

    return "";
  }, [start, typeHelper, value]);

  const previousEndValue = useMemo(() => {
    if (typeof typeHelper.parse(value ?? "") === "number" && end !== "") {
      return `The last valid number is: (${end} - 2e-52).`;
    }

    return "";
  }, [end, typeHelper, value]);

  return (
    <div>
      <div>
        <p style={{ paddingTop: "10px" }}>
          The range constraint creates an expression that will limit the value to be in a range.
        </p>
        <HelperText>
          <HelperTextItem style={{ paddingTop: "10px" }} variant="indeterminate" icon={<InfoIcon />}>
            This type of constraint strictly has a starting and ending value, which can be opened or closed.
          </HelperTextItem>
        </HelperText>
      </div>
      <br />
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
          })}
        </div>
        <div style={{ gridArea: "startDescription" }}>
          <HelperText>
            <HelperTextItem variant="indeterminate">
              {includeStart
                ? "The starting value will be included in the range."
                : `The starting value will not be included in the range. ${nextStartValue}`}
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
          })}
        </div>
        <div style={{ gridArea: "endDescription" }}>
          <HelperText>
            <HelperTextItem variant="indeterminate">
              {includeEnd
                ? "The ending value will be included in the range."
                : `The ending value will not be included in the range. ${previousEndValue}`}
            </HelperTextItem>
          </HelperText>
        </div>
      </div>
      <br />
      <ConstraintsExpression isReadonly={true} value={savedValue ?? ""} type={type} />
    </div>
  );
}

export function hasRangeStartStructure(value: string): boolean {
  return value.startsWith("(") || value.startsWith("[");
}

export function hasRangeEndStructure(value: string): boolean {
  return value.endsWith(")") || value.endsWith("]");
}

export function hasRangeStructure(value: string): boolean {
  return hasRangeStartStructure(value) && hasRangeEndStructure(value) && value.split(".").length - 1 === 2;
}

export function isRange(value: string, typeCheck: (e: string) => boolean): [string, string] | undefined {
  if (!hasRangeStructure(value)) {
    return undefined;
  }

  const rangeValues = value.split("..");
  if (rangeValues.length === 2 && typeCheck(rangeValues[0].slice(1)) && typeCheck(rangeValues[1].slice(0, -1))) {
    return [rangeValues[0].slice(1), rangeValues[1].slice(0, -1)];
  }

  return undefined;
}
