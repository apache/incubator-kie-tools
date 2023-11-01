import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";

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
  onChange: (newValue: string | undefined, origin: KIE__tConstraintType) => void;
  isDisabled: boolean;
}) {
  const [start, setStart] = useState("");
  const [end, setEnd] = useState("");
  const [includeStart, setIncludeStart] = useState(false);
  const [includeEnd, setIncludeEnd] = useState(false);

  const onInternalChange = useCallback(
    (args?: { start?: string; end?: string; includeStart?: boolean; includeEnd?: boolean }) => {
      if ((args?.start !== undefined && args.start === "") || (args?.end !== undefined && args.end === "")) {
        return;
      }

      if ((args?.start === undefined && start === "") || (args?.end === undefined && end === "")) {
        return;
      }

      onChange(
        `${args?.includeStart ?? includeStart ? "[" : "("}${args?.start ?? start}..${args?.end ?? end}${
          args?.includeEnd ?? includeEnd ? "]" : ")"
        }`,
        "range"
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
      <div style={{ paddingTop: "10px" }}>
        <p>The range constraint creates an expression that will limit the type value to be contained in a range.</p>
        <p>It strict has a starting and ending value, and both can be included in the range.</p>
      </div>
      <br />
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "auto 1fr",
          gridTemplateRows: "1fr 50px 100px 1fr 50px",
          gridTemplateAreas: `
          'includeStartButton startField'
          'arrow startDescription'
          'arrow empty'
          'includeEndButton endField'
          'empty2 endDescription'
          `,
          columnGap: "10px",
          alignItems: "center",
        }}
      >
        <div style={{ gridArea: "includeStartButton" }}>
          <button
            id={"start"}
            disabled={isDisabled}
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
        </div>
        <div style={{ gridArea: "startField" }}>
          <TextInput
            placeholder={"Starts with"}
            type={inputType}
            value={start}
            onChange={onStartChange}
            isDisabled={isDisabled}
            onBlur={() => onInternalChange()}
          />
        </div>
        <div style={{ gridArea: "startDescription" }}>
          {includeStart ? (
            <p style={{ color: "gray" }}>The starting value will be included in the range.</p>
          ) : (
            <p style={{ color: "gray" }}>The starting value will not be included in the range. {nextStartValue}</p>
          )}
        </div>

        <div style={{ gridArea: "arrow", justifySelf: "center", alignSelf: "center", height: "100%" }}>
          <div style={{ borderLeftStyle: "solid", borderLeftColor: "gray", borderLeftWidth: "1px", height: "100%" }} />
        </div>

        <div style={{ gridArea: "includeEndButton" }}>
          <button
            id={"end"}
            disabled={isDisabled}
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
        </div>
        <div style={{ gridArea: "endField" }}>
          <TextInput
            placeholder={"Ends with"}
            type={inputType}
            value={end}
            onChange={onEndChange}
            isDisabled={isDisabled}
            onBlur={() => onInternalChange()}
          />
        </div>
        <div style={{ gridArea: "endDescription" }}>
          {includeEnd ? (
            <p style={{ color: "gray" }}>The ending value will be included in the range.</p>
          ) : (
            <p style={{ color: "gray" }}>The ending value will not be included in the range. {previousEndValue}</p>
          )}
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
