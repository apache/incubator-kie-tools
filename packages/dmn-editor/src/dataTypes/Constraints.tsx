import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintsEnum, isEnum } from "./ConstraintsEnum";
import { ConstraintsRange, isRange } from "./ConstraintsRange";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { EditItemDefinition } from "./DataTypes";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { canHaveConstraints, constrainableBuiltInFeelTypes } from "./DataTypeSpec";

enum ConstraintsType {
  ENUMERATION = "Enumeration",
  EXPRESSION = "Expression",
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

  const type = useMemo(() => itemDefinition?.typeRef?.__$$text, [itemDefinition?.typeRef?.__$$text]);
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
          newConstraint === itemDefinition.typeConstraint?.text?.__$$text &&
          origin === itemDefinition.typeConstraint["@_kie:constraintType"]
        ) {
          return;
        }

        itemDefinition.typeConstraint = {
          text: { __$$text: newConstraint },
          "@_id": itemDefinition?.["@_id"],
          "@_kie:constraintType": origin,
        };
      });
    },
    [itemDefinition, editItemDefinition, isReadonly]
  );

  const kieContraintTypeToEnum: (constraintType: KIE__tConstraintType | undefined) => ConstraintsType = useCallback(
    (constraintType) => {
      switch (constraintType) {
        case "enumeration":
          return ConstraintsType.ENUMERATION;
        case "expression":
          return ConstraintsType.EXPRESSION;
        case "range":
          return ConstraintsType.RANGE;
        default:
          return ConstraintsType.NONE;
      }
    },
    []
  );

  const enumToKieConstraintType: (selection: ConstraintsType) => KIE__tConstraintType | undefined = useCallback(
    (selection) => {
      switch (selection) {
        case ConstraintsType.ENUMERATION:
          return "enumeration";
        case ConstraintsType.EXPRESSION:
          return "expression";
        case ConstraintsType.RANGE:
          return "range";
        case ConstraintsType.NONE:
        default:
          return undefined;
      }
    },
    []
  );

  const isConstraintEnabled = useMemo(() => {
    const enabledConstraints = constrainableBuiltInFeelTypes.get(type as DmnBuiltInDataType);
    return {
      enumeration: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [enumToKieConstraintType, type]);

  const typeHelper = useMemo(() => {
    if (type === DmnBuiltInDataType.Any) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    if (type === DmnBuiltInDataType.Date) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    if (type === DmnBuiltInDataType.DateTime) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    if (type === DmnBuiltInDataType.DateTimeDuration) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    if (type === DmnBuiltInDataType.Number) {
      return {
        check: (value: string) => !isNaN(parseFloat(value)),
        parser: (value: string) => parseFloat(value),
      };
    }
    if (type === DmnBuiltInDataType.String) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    if (type === DmnBuiltInDataType.Time) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    if (type === DmnBuiltInDataType.YearsMonthsDuration) {
      return {
        check: (value: string) => false,
        parser: (value: string) => {},
      };
    }
    return {
      check: (value: string) => false,
      parser: (value: string) => {},
    };
  }, [type]);

  // Start in the previous constraint type
  useEffect(() => {
    setSelected(kieContraintTypeToEnum(value?.["@_kie:constraintType"]));
  }, [kieContraintTypeToEnum, value]);

  const inputType: "text" | "number" = useMemo(() => {
    switch (type) {
      case DmnBuiltInDataType.Number:
        return "number";
      case DmnBuiltInDataType.Any:
      case DmnBuiltInDataType.Boolean:
      case DmnBuiltInDataType.Context:
      case DmnBuiltInDataType.String:
        return "text";
      case DmnBuiltInDataType.Date:
      case DmnBuiltInDataType.DateTime:
      case DmnBuiltInDataType.DateTimeDuration:
      case DmnBuiltInDataType.Time:
      case DmnBuiltInDataType.YearsMonthsDuration:
      default:
        return "text";
    }
  }, [type]);

  const onToggleGroupChange = useCallback(
    (newSelection, event) => {
      if (!newSelection) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      setSelected(selection);

      if (selection === ConstraintsType.NONE) {
        onInternalChange(undefined);
        return;
      }

      if (value === undefined || value.text?.__$$text === "") {
        return;
      }

      if (selection === ConstraintsType.ENUMERATION && isEnum(value.text?.__$$text, typeHelper.check)) {
        onInternalChange(value.text?.__$$text, enumToKieConstraintType(ConstraintsType.ENUMERATION));
        return;
      }

      if (selection === ConstraintsType.RANGE && isRange(value.text?.__$$text, typeHelper.check)) {
        onInternalChange(value.text?.__$$text, enumToKieConstraintType(ConstraintsType.RANGE));
        return;
      }

      if (selection === ConstraintsType.EXPRESSION) {
        onInternalChange(value.text?.__$$text, enumToKieConstraintType(ConstraintsType.EXPRESSION));
        return;
      }

      onInternalChange(value.text?.__$$text, enumToKieConstraintType(selected));
    },
    [enumToKieConstraintType, onInternalChange, typeHelper.check, value, selected]
  );

  const constraintType = useMemo(() => {
    if (selected === ConstraintsType.NONE) {
      return (
        <p
          style={{
            padding: "10px",
            background: "#eee",
            borderRadius: "10px",
          }}
        >
          {`Type without constraints.`}
        </p>
      );
    }
    if (selected === ConstraintsType.ENUMERATION) {
      return (
        <ConstraintsEnum
          isReadonly={isReadonly}
          inputType={inputType}
          value={value?.text?.__$$text}
          type={type as DmnBuiltInDataType}
          typeParser={typeHelper.parser}
          onChange={(newValue: string) =>
            onInternalChange(newValue, enumToKieConstraintType(ConstraintsType.ENUMERATION))
          }
          isDisabled={!isConstraintEnabled.enumeration}
        />
      );
    }
    if (selected === ConstraintsType.RANGE) {
      return (
        <ConstraintsRange
          isReadonly={isReadonly}
          inputType={inputType}
          value={value?.text?.__$$text}
          type={type as DmnBuiltInDataType}
          typeParser={typeHelper.parser}
          onChange={(newValue: string) => onInternalChange(newValue, enumToKieConstraintType(ConstraintsType.RANGE))}
          isDisabled={!isConstraintEnabled.range}
        />
      );
    }
    if (selected === ConstraintsType.EXPRESSION) {
      return (
        <ConstraintsExpression
          isReadonly={isReadonly}
          value={value?.text?.__$$text}
          type={type as DmnBuiltInDataType}
          onChange={(newValue: string) =>
            onInternalChange(newValue, enumToKieConstraintType(ConstraintsType.EXPRESSION))
          }
        />
      );
    }
  }, [
    enumToKieConstraintType,
    inputType,
    isConstraintEnabled.enumeration,
    isConstraintEnabled.range,
    isReadonly,
    onInternalChange,
    selected,
    type,
    typeHelper.parser,
    value?.text,
  ]);

  return (
    <>
      {!canHaveConstraints(itemDefinition) ? (
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
                isDisabled={isReadonly}
              />
              <ToggleGroupItem
                text={ConstraintsType.EXPRESSION}
                buttonId={ConstraintsType.EXPRESSION}
                isSelected={selected === ConstraintsType.EXPRESSION}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly}
              />
              <ToggleGroupItem
                text={ConstraintsType.ENUMERATION}
                buttonId={ConstraintsType.ENUMERATION}
                isSelected={selected === ConstraintsType.ENUMERATION}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly || !isConstraintEnabled.enumeration}
              />
              <ToggleGroupItem
                text={ConstraintsType.RANGE}
                buttonId={ConstraintsType.RANGE}
                isSelected={selected === ConstraintsType.RANGE}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly || !isConstraintEnabled.range}
              />
            </ToggleGroup>
          </div>

          <div style={{ paddingTop: "10px" }}>{constraintType}</div>
        </div>
      )}
    </>
  );
}
