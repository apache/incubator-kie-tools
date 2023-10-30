import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { DMN15__tUnaryTests } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintsEnum } from "./ConstraintsEnum";
import { ConstraintsRange, isRange } from "./ConstraintsRange";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";

enum ConstraintsType {
  ENUMERATION = "Enumeration",
  EXPRESSIONS = "Expression",
  RANGE = "Range",
}

export function Constraints({
  type,
  value,
  onChange,
}: {
  type: DmnBuiltInDataType;
  value?: DMN15__tUnaryTests;
  onChange: (newValue?: DMN15__tUnaryTests) => void;
}) {
  const [isOpen, setOpen] = useState<boolean>(false);
  const [selected, setSelected] = useState<ConstraintsType | undefined>(undefined);
  const [isDisabled, setIsDisabled] = useState<boolean>(false);
  const [isEnumDisabled, setEnumDisabled] = useState(false);
  const [isRangeDisabled, setRangeDisabled] = useState(false);

  const equivalentInternalConstraintType: (
    constraintType: KIE__tConstraintType | undefined
  ) => ConstraintsType | undefined = useCallback((constraintType) => {
    switch (constraintType) {
      case "enumeration":
        return ConstraintsType.ENUMERATION;
      case "expression":
        return ConstraintsType.EXPRESSIONS;
      case "range":
        return ConstraintsType.RANGE;
      default:
        return undefined;
    }
  }, []);

  const equivalentKieConstraintType: (selection: ConstraintsType) => KIE__tConstraintType | undefined = useCallback(
    (selection) => {
      switch (selection) {
        case ConstraintsType.ENUMERATION:
          return "enumeration";
        case ConstraintsType.EXPRESSIONS:
          return "expression";
        case ConstraintsType.RANGE:
          return "range";
        default:
          return undefined;
      }
    },
    []
  );

  // Start in the previous constraint type
  useEffect(() => {
    setSelected(equivalentInternalConstraintType(value?.["@_kie:constraintType"]));
  }, [equivalentInternalConstraintType, value]);

  const onInternalChange = useCallback(
    (newConstraint: string | undefined, origin?: KIE__tConstraintType) => {
      if (newConstraint === undefined) {
        onChange(undefined);
      }
      if (newConstraint !== undefined) {
        const newValue: DMN15__tUnaryTests = {
          text: newConstraint,
          "@_id": generateUuid(),
          "@_kie:constraintType": origin,
        };
        onChange(newValue);
      }
    },
    [onChange]
  );

  const inputType: "text" | "number" = useMemo(() => {
    switch (type) {
      case DmnBuiltInDataType.Number:
        return "number";
      case DmnBuiltInDataType.Any:
      case DmnBuiltInDataType.Boolean:
      case DmnBuiltInDataType.Context:
        return "text";
      case DmnBuiltInDataType.Date:
      case DmnBuiltInDataType.DateTime:
      case DmnBuiltInDataType.DateTimeDuration:
      case DmnBuiltInDataType.String:
      case DmnBuiltInDataType.Time:
      case DmnBuiltInDataType.YearsMonthsDuration:
      default:
        return "text";
    }
  }, [type]);

  /**
   * parse the value
   *
   * undefined -> undefined
   * <word>,<word>,<word> -> enum
   * [/( ... )/] -> range
   */
  useEffect(() => {
    if (type === DmnBuiltInDataType.Any || type === DmnBuiltInDataType.Boolean || type === DmnBuiltInDataType.Context) {
      setIsDisabled(true);
      setSelected(undefined);
      return;
    }

    setIsDisabled(false);
    if (type === DmnBuiltInDataType.Date) {
      return;
    }
    if (type === DmnBuiltInDataType.DateTime) {
      return;
    }
    if (type === DmnBuiltInDataType.DateTimeDuration) {
      return;
    }
    if (type === DmnBuiltInDataType.Number) {
      if (value?.text === undefined) {
        setRangeDisabled(false);
        setEnumDisabled(false);
        return;
      }

      const isNumber = (n: any) => !isNaN(parseFloat(n));
      const rangeValues = isRange(value?.text ?? "", (e: string) => isNumber(e));
      if (rangeValues) {
        setRangeDisabled(false);
      } else {
        setRangeDisabled(true);
      }

      const enumValues = value?.text.split(", ");
      if (enumValues?.reduce((isEnum, e) => isEnum && isNumber(e), true)) {
        setEnumDisabled(false);
      } else {
        setEnumDisabled(true);
      }
    }
    if (type === DmnBuiltInDataType.String) {
      return;
    }
    if (type === DmnBuiltInDataType.Time) {
      return;
    }
    if (type === DmnBuiltInDataType.YearsMonthsDuration) {
      return;
    }
  }, [type, value]);

  const onSelect = useCallback(
    (e, selection, isPlaceholder) => {
      if (isPlaceholder) {
        setSelected(undefined);
        setOpen(false);
        onInternalChange(undefined);
        return;
      }

      setSelected(selection);
      setOpen(false);
      onInternalChange(value?.text, equivalentKieConstraintType(selection));
    },
    [equivalentKieConstraintType, onInternalChange, value?.text]
  );

  const constraintOptions = useMemo(() => {
    return [
      <SelectOption key={0} value={"Select a constraint type..."} isPlaceholder={true} />,
      <SelectOption key={1} value={ConstraintsType.ENUMERATION} />,
      <SelectOption key={2} value={ConstraintsType.EXPRESSIONS} />,
      <SelectOption key={3} value={ConstraintsType.RANGE} />,
    ];
  }, []);

  const constraintType = useMemo(() => {
    if (selected === ConstraintsType.ENUMERATION) {
      return (
        <ConstraintsEnum
          inputType={inputType}
          value={value?.text ?? ""}
          onChange={onInternalChange}
          isDisabled={isEnumDisabled}
        />
      );
    } else if (selected === ConstraintsType.RANGE) {
      return (
        <ConstraintsRange
          inputType={inputType}
          value={value?.text ?? ""}
          onChange={onInternalChange}
          isDisabled={isRangeDisabled}
        />
      );
    }
  }, [inputType, isEnumDisabled, isRangeDisabled, onInternalChange, selected, value?.text]);

  const onExpressionChange = useCallback(
    (newValue: string, origin: KIE__tConstraintType) => {
      if (selected === ConstraintsType.ENUMERATION) {
        onInternalChange(newValue, "enumeration");
        return;
      }
      if (selected === ConstraintsType.RANGE) {
        onInternalChange(newValue, "range");
        return;
      }
      onInternalChange(newValue, origin);
    },
    [onInternalChange, selected]
  );

  return (
    <>
      <Title size={"md"} headingLevel="h4">
        Constraints
      </Title>
      <br />
      <div style={{ display: "flex", flexDirection: "column" }}>
        <div style={{ padding: "10px" }}>
          <Select
            onToggle={() => setOpen((prev) => !prev)}
            onSelect={onSelect}
            selections={selected}
            variant={SelectVariant.single}
            isDisabled={isDisabled}
            isOpen={isOpen}
            aria-label={"Constraint selector"}
          >
            {constraintOptions}
          </Select>
        </div>

        {selected && (
          <div style={{ padding: "10px", height: "60px" }}>
            <ConstraintsExpression value={value?.text ?? ""} onChange={onExpressionChange} />
            <br />
            {constraintType}
          </div>
        )}
      </div>
    </>
  );
}
