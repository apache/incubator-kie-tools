import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import InfoIcon from "@patternfly/react-icons/dist/js/icons/info-icon";
import { Label } from "@patternfly/react-core/dist/js/components/Label";

export function ConstraintsRange({
  isReadonly,
  inputType,
  value,
  onChange,
  isDisabled,
}: {
  isReadonly: boolean;
  inputType: "text" | "number";
  value?: string;
  onChange: (newValue: string | undefined) => void;
  isDisabled: boolean;
}) {
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [includeStart, setIncludeStart] = useState(true);
  const [includeEnd, setIncludeEnd] = useState(false);

  const onInternalChange = useCallback(
    (args?: { start?: string; end?: string; includeStart?: boolean; includeEnd?: boolean }) => {
      if ((args?.start === undefined || args?.start === "") && (args?.end === undefined || args?.end === "")) {
        onChange("");
      }

      if ((args?.start !== undefined && args.start === "") || (args?.end !== undefined && args.end === "")) {
        return;
      }

      if ((args?.start === undefined && start === "") || (args?.end === undefined && end === "")) {
        return;
      }

      onChange(
        `${args?.includeStart ?? includeStart ? "[" : "("}${args?.start ?? start}..${args?.end ?? end}${
          args?.includeEnd ?? includeEnd ? "]" : ")"
        }`
      );
    },
    [end, includeEnd, includeStart, onChange, start]
  );

  // Keep it in sync with the ConstraintExpression
  useEffect(() => {
    const rangeValues = value?.split("..");
    if (rangeValues?.length === 2) {
      if (hasRangeStartStructure(rangeValues[0])) {
        setStart(rangeValues[0]?.slice(1) ?? "");
      }
      if (hasRangeEndStructure(rangeValues[1])) {
        setEnd(rangeValues[1]?.slice(0, -1) ?? "");
      }
      setIncludeStart(value?.startsWith("[") ?? false);
      setIncludeEnd(value?.endsWith("]") ?? false);
    }
  }, [value]);

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
    if (inputType === "number" && start !== "") {
      return `The next valid number is: (${start} + 2e-52).`;
    }

    return "";
  }, [start, inputType]);

  const previousEndValue = useMemo(() => {
    if (inputType === "number" && end !== "") {
      return `The last valid number is: (${end} - 2e-52).`;
    }

    return "";
  }, [end, inputType]);

  return (
    <div>
      <div>
        <p style={{ paddingTop: "10px" }}>
          The range constraint creates an expression that will limit the value to be in a range.
        </p>
        <HelperText>
          <HelperTextItem style={{ paddingTop: "10px" }} variant="indeterminate" icon={<InfoIcon />}>
            This type of constraint strictly has a starting and ending value, and both can be included in the range.
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
              id={"start"}
              disabled={isReadonly || isDisabled}
              onClick={() => onIncludeStartToogle()}
              style={{
                borderRadius: "100%",
                borderColor: "gray",
                borderStyle: "solid",
                width: "20px",
                height: "20px",
                backgroundColor: includeStart ? "black" : "transparent",
              }}
            />
          </Tooltip>
        </div>
        <div style={{ gridArea: "startField" }}>
          <TextInput
            id={"start-value"}
            placeholder={"Starts with"}
            type={inputType}
            value={start}
            onChange={onStartChange}
            isDisabled={isReadonly || isDisabled}
            onBlur={() => onInternalChange()}
          />
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
          <div style={{ borderLeftStyle: "solid", borderLeftColor: "gray", borderLeftWidth: "1px", height: "100%" }} />
        </div>

        <div style={{ gridArea: "end" }}>
          <Label>End</Label>
        </div>
        <div style={{ gridArea: "includeEndButton" }}>
          <Tooltip
            content={includeEnd ? "Click to remove value from the range" : "Click to include value in the range"}
          >
            <button
              id={"end"}
              disabled={isReadonly || isDisabled}
              onClick={() => onIncludeEndToogle()}
              style={{
                borderRadius: "100%",
                borderColor: "gray",
                borderStyle: "solid",
                width: "20px",
                height: "20px",
                backgroundColor: includeEnd ? "black" : "transparent",
              }}
            />
          </Tooltip>
        </div>
        <div style={{ gridArea: "endField" }}>
          <TextInput
            id={"end-value"}
            placeholder={"Ends with"}
            type={inputType}
            value={end}
            onChange={onEndChange}
            isDisabled={isReadonly || isDisabled}
            onBlur={() => onInternalChange()}
          />
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
      <ConstraintsExpression isReadonly={true} value={value ?? ""} />
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
