import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { FeelInput } from "@kie-tools/feel-input-component/dist";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintsExpression } from "./ConstraintsExpression";
import {
  DMN15__tItemDefinition,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintsEnum, ENUM_SEPARATOR } from "./ConstraintsEnum";
import { ConstraintsRange, isRange } from "./ConstraintsRange";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { EditItemDefinition } from "./DataTypes";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";

enum ConstraintsType {
  ENUMERATION = "Enumeration",
  EXPRESSIONS = "Expression",
  RANGE = "Range",
  NONE = "None",
}

export function Constraints({
  isReadonly,
  itemDefinition,
  editItemDefinition,
}: {
  isReadonly: boolean;
  itemDefinition: DMN15__tItemDefinition;
  editItemDefinition: EditItemDefinition;
}) {
  const [selected, setSelected] = useState<ConstraintsType>(ConstraintsType.NONE);
  const [isDisabled, setIsDisabled] = useState<boolean>(false);
  const [isEnumDisabled, setEnumDisabled] = useState(false);
  const [isRangeDisabled, setRangeDisabled] = useState(false);

  const type = useMemo(() => itemDefinition?.typeRef, [itemDefinition?.typeRef]);
  const value = useMemo(() => itemDefinition?.typeConstraint, [itemDefinition?.typeConstraint]);

  const onInternalChange = useCallback(
    (newConstraint?: string, origin?: KIE__tConstraintType) => {
      if (isReadonly) {
        return;
      }

      editItemDefinition(itemDefinition["@_id"]!, (itemDefinition) => {
        if (newConstraint === undefined) {
          itemDefinition.typeConstraint = undefined;
          return;
        }

        if (
          newConstraint === itemDefinition.typeConstraint?.text &&
          origin === itemDefinition.typeConstraint["@_kie:constraintType"]
        ) {
          return;
        }

        itemDefinition.typeConstraint = {
          text: newConstraint,
          "@_id": itemDefinition?.["@_id"],
          "@_kie:constraintType": origin,
        };
      });
    },
    [itemDefinition, editItemDefinition, isReadonly]
  );

  const equivalentInternalConstraintType: (constraintType: KIE__tConstraintType | undefined) => ConstraintsType =
    useCallback((constraintType) => {
      switch (constraintType) {
        case "enumeration":
          return ConstraintsType.ENUMERATION;
        case "expression":
          return ConstraintsType.EXPRESSIONS;
        case "range":
          return ConstraintsType.RANGE;
        default:
          return ConstraintsType.NONE;
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
    if (type === DmnBuiltInDataType.Boolean || type === DmnBuiltInDataType.Context) {
      setIsDisabled(true);
      return;
    }

    setIsDisabled(false);
    if (type === DmnBuiltInDataType.Any) {
      setRangeDisabled(true);
      setEnumDisabled(true);
      return;
    }

    setRangeDisabled(false);
    setEnumDisabled(false);
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
      return;
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

  const onToggleGroupChange = useCallback(
    (selected, event) => {
      if (!selected) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      setSelected(selection);

      if (selection === ConstraintsType.NONE) {
        onInternalChange(undefined);
        return;
      }

      onInternalChange(value?.text, equivalentKieConstraintType(selection));
    },
    [equivalentKieConstraintType, onInternalChange, value?.text]
  );

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

  const constraintType = useMemo(() => {
    if (selected === ConstraintsType.ENUMERATION) {
      return (
        <ConstraintsEnum
          isReadonly={isReadonly}
          inputType={inputType}
          value={value?.text}
          onChange={onInternalChange}
          isDisabled={isEnumDisabled}
        />
      );
    } else if (selected === ConstraintsType.RANGE) {
      return (
        <ConstraintsRange
          isReadonly={isReadonly}
          inputType={inputType}
          value={value?.text}
          onChange={onInternalChange}
          isDisabled={isRangeDisabled}
        />
      );
    } else if (selected === ConstraintsType.EXPRESSIONS) {
      return <ConstraintsExpression isReadonly={isReadonly} value={value?.text ?? ""} onChange={onExpressionChange} />;
    }
  }, [
    inputType,
    isEnumDisabled,
    isRangeDisabled,
    isReadonly,
    onExpressionChange,
    onInternalChange,
    selected,
    value?.text,
  ]);

  return (
    <>
      <Title size={"md"} headingLevel="h4">
        Constraints
      </Title>
      <br />
      {isDisabled ? (
        <p
          style={{
            padding: "10px",
            background: "#eee",
            borderRadius: "10px",
            textAlign: "center",
          }}
        >
          {`This data type doesn't support constraints`}
        </p>
      ) : (
        <div style={{ display: "flex", flexDirection: "column" }}>
          <div>
            <ToggleGroup aria-label={"Constraint toggle group"}>
              <ToggleGroupItem
                text={ConstraintsType.NONE}
                buttonId={ConstraintsType.NONE}
                isSelected={selected === ConstraintsType.NONE}
                onChange={onToggleGroupChange}
              />
              <ToggleGroupItem
                text={ConstraintsType.ENUMERATION}
                buttonId={ConstraintsType.ENUMERATION}
                isSelected={selected === ConstraintsType.ENUMERATION}
                onChange={onToggleGroupChange}
                isDisabled={isEnumDisabled}
              />
              <ToggleGroupItem
                text={ConstraintsType.EXPRESSIONS}
                buttonId={ConstraintsType.EXPRESSIONS}
                isSelected={selected === ConstraintsType.EXPRESSIONS}
                onChange={onToggleGroupChange}
              />
              <ToggleGroupItem
                text={ConstraintsType.RANGE}
                buttonId={ConstraintsType.RANGE}
                isSelected={selected === ConstraintsType.RANGE}
                onChange={onToggleGroupChange}
                isDisabled={isRangeDisabled}
              />
            </ToggleGroup>
          </div>

          {selected !== ConstraintsType.NONE && (
            <div style={{ paddingTop: "10px", height: "60px" }}>{constraintType}</div>
          )}
        </div>
      )}
    </>
  );
}
