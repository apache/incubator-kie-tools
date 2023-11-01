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

  return (
    <>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "auto 1fr 1fr",
          gridTemplateRows: "1fr 1fr",
          gap: "10px",
          alignItems: "center",
        }}
      >
        <div>
          <Title size={"md"} headingLevel="h5">
            Starts with:
          </Title>
        </div>
        <div>
          <TextInput
            type={inputType}
            value={start}
            onChange={onStartChange}
            isDisabled={isDisabled}
            onBlur={() => onInternalChange()}
          />
        </div>
        <div>
          <Checkbox
            id={"end"}
            isChecked={includeStart}
            onChange={onIncludeStartToogle}
            isDisabled={isDisabled}
            onBlur={() => onInternalChange()}
          />
        </div>

        <div>
          <Title size={"md"} headingLevel="h5">
            Ends with:
          </Title>
        </div>
        <div>
          <TextInput
            type={inputType}
            value={end}
            onChange={onEndChange}
            isDisabled={isDisabled}
            onBlur={() => onInternalChange()}
          />
        </div>
        <div>
          <Checkbox
            id={"end"}
            isChecked={includeEnd}
            onChange={onIncludeEndToogle}
            isDisabled={isDisabled}
            onBlur={() => onInternalChange()}
          />
        </div>
      </div>
      <br />
      <ConstraintsExpression isReadonly={true} value={value ?? ""} />
    </>
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
