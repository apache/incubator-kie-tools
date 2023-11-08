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

  const typeCheck = useMemo(() => {
    if (type === DmnBuiltInDataType.Any) {
      return (value: string) => false;
    }
    if (type === DmnBuiltInDataType.Date) {
      return (value: string) => false;
    }
    if (type === DmnBuiltInDataType.DateTime) {
      return (value: string) => false;
    }
    if (type === DmnBuiltInDataType.DateTimeDuration) {
      return (value: string) => false;
    }
    if (type === DmnBuiltInDataType.Number) {
      return (value: string) => !isNaN(parseFloat(value));
    }
    if (type === DmnBuiltInDataType.String) {
      return (value: string) => false;
    }
    if (type === DmnBuiltInDataType.Time) {
      return (value: string) => false;
    }
    if (type === DmnBuiltInDataType.YearsMonthsDuration) {
      return (value: string) => false;
    }
    return (value: string) => false;
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

      if (value === undefined || value.text === "") {
        return;
      }

      if (selection === ConstraintsType.ENUMERATION && isEnum(value.text, typeCheck)) {
        onInternalChange(value.text, enumToKieConstraintType(ConstraintsType.ENUMERATION));
        return;
      }

      if (selection === ConstraintsType.RANGE && isRange(value.text, typeCheck)) {
        onInternalChange(value.text, enumToKieConstraintType(ConstraintsType.RANGE));
        return;
      }

      if (selection === ConstraintsType.EXPRESSION) {
        onInternalChange(value.text, enumToKieConstraintType(ConstraintsType.EXPRESSION));
        return;
      }

      onInternalChange(value.text, enumToKieConstraintType(selected));
    },
    [enumToKieConstraintType, onInternalChange, typeCheck, value, selected]
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
          value={value?.text}
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
          value={value?.text}
          onChange={(newValue: string) => onInternalChange(newValue, enumToKieConstraintType(ConstraintsType.RANGE))}
          isDisabled={!isConstraintEnabled.range}
        />
      );
    }
    if (selected === ConstraintsType.EXPRESSION) {
      return (
        <ConstraintsExpression
          isReadonly={isReadonly}
          value={value?.text}
          onChange={(newValue: string) =>
            onInternalChange(newValue, enumToKieConstraintType(ConstraintsType.EXPRESSION))
          }
        />
      );
    }
  }, [enumToKieConstraintType, inputType, isConstraintEnabled, isReadonly, onInternalChange, selected, value?.text]);

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
