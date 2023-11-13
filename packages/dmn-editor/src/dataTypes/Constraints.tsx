import * as React from "react";
import { useMemo, useState, useCallback, useEffect } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import { DMN15__tItemDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintsEnum, isEnum } from "./ConstraintsEnum";
import { ConstraintsRange, isRange } from "./ConstraintsRange";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { EditItemDefinition } from "./DataTypes";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { canHaveConstraints, constrainableBuiltInFeelTypes } from "./DataTypeSpec";
import moment from "moment";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ConstraintDate } from "./ConstraintComponents/ConstraintDate";
import { ConstraintDateTime } from "./ConstraintComponents/ConstraintDateTime";
import {
  ConstraintDateTimeDuration,
  REGEX_DATE_TIME_DURATION,
} from "./ConstraintComponents/ConstraintDateTimeDuration";
import { ConstraintTime } from "./ConstraintComponents/ConstraintTime";
import {
  ConstraintYearsMonthsDuration,
  REGEX_YEARS_MONTH_DURATION,
} from "./ConstraintComponents/ConstraintYearsMonthsDuration";
import { invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";
import { ConstraintProps } from "./ConstraintComponents/Constraint";

export type TypeHelper = {
  check: (value: string) => boolean;
  parse: (value: string) => any;
  transform: (value: string) => string;
  recover: (value: string) => string;
  component: (props: any) => React.ReactNode | undefined;
};

export interface ConstraintComponentProps {
  isReadonly: boolean;
  value?: string;
  savedValue?: string;
  type: DmnBuiltInDataType;
  typeHelper: TypeHelper;
  onSave: (value?: string) => void;
  isDisabled: boolean;
}

enum ConstraintsType {
  ENUMERATION = "Enumeration",
  EXPRESSION = "Expression",
  RANGE = "Range",
  NONE = "None",
}

export const constraintTypeHelper = (typeRef: DmnBuiltInDataType): TypeHelper => {
  return {
    // check if the value has the correct type
    check: (value: string) => {
      const recoveredValue = constraintTypeHelper(typeRef).recover(value);
      switch (typeRef) {
        case DmnBuiltInDataType.Any:
          return true;
        case DmnBuiltInDataType.String:
          if (constraintTypeHelper(DmnBuiltInDataType.Date).check(value)) {
            return false;
          }
          if (constraintTypeHelper(DmnBuiltInDataType.DateTime).check(value)) {
            return false;
          }
          if (constraintTypeHelper(DmnBuiltInDataType.DateTimeDuration).check(value)) {
            return false;
          }
          if (constraintTypeHelper(DmnBuiltInDataType.Time).check(value)) {
            return false;
          }
          if (constraintTypeHelper(DmnBuiltInDataType.YearsMonthsDuration).check(value)) {
            return false;
          }
          return typeof recoveredValue === "string";
        case DmnBuiltInDataType.Date:
          return moment(recoveredValue, "YYYY-MM-DD", true).isValid() || value === "";
        case DmnBuiltInDataType.DateTime:
          return moment(recoveredValue, "YYYY-MM-DDTHH:mm", true).isValid() || value === "";
        case DmnBuiltInDataType.DateTimeDuration:
          return REGEX_DATE_TIME_DURATION.test(recoveredValue) || value === "";
        case DmnBuiltInDataType.Number:
          return !isNaN(parseFloat(recoveredValue)) || value === "";
        case DmnBuiltInDataType.Time:
          return moment(recoveredValue, "HH:mm", true).isValid() || value === "";
        case DmnBuiltInDataType.YearsMonthsDuration:
          return REGEX_YEARS_MONTH_DURATION.test(recoveredValue) || value === "";
        default:
          return false;
      }
    },
    // parse the value to the type
    // useful to make comparisons
    parse: (value: string) => {
      const recoveredValue = constraintTypeHelper(typeRef).recover(value);
      switch (typeRef) {
        case DmnBuiltInDataType.Number:
          return parseFloat(recoveredValue);
        case DmnBuiltInDataType.DateTimeDuration:
        case DmnBuiltInDataType.YearsMonthsDuration:
          return moment.duration(recoveredValue);
        case DmnBuiltInDataType.Date:
        case DmnBuiltInDataType.DateTime:
        case DmnBuiltInDataType.String:
        case DmnBuiltInDataType.Time:
        default:
          return recoveredValue;
      }
    },
    // transform the value before save
    transform: (value: string) => {
      switch (typeRef) {
        case DmnBuiltInDataType.Any:
        case DmnBuiltInDataType.String:
          return JSON.stringify(value);
        case DmnBuiltInDataType.Date:
          return `date("${value}")`;
        case DmnBuiltInDataType.DateTime:
          return `date and time("${value}")`;
        case DmnBuiltInDataType.DateTimeDuration:
        case DmnBuiltInDataType.YearsMonthsDuration:
          return `duration("${value}")`;
        case DmnBuiltInDataType.Number:
          return `${value}`;
        case DmnBuiltInDataType.Time:
          return `time("${value}")`;
        default:
          return value;
      }
    },
    // recover the value before use it
    recover: (value: string) => {
      switch (typeRef) {
        case DmnBuiltInDataType.Any:
        case DmnBuiltInDataType.String:
          try {
            return `${JSON.parse(value)}`;
          } catch (error) {
            return `${value}`;
          }
        case DmnBuiltInDataType.Date:
          return value.replace('date("', "").replace('")', "");
        case DmnBuiltInDataType.DateTime:
          return value.replace('date and time("', "").replace('")', "");
        case DmnBuiltInDataType.DateTimeDuration:
          return value.replace('duration("', "").replace('")', "");
        case DmnBuiltInDataType.Number:
          return `${value}`;
        case DmnBuiltInDataType.Time:
          return value.replace('time("', "").replace('")', "");
        case DmnBuiltInDataType.YearsMonthsDuration:
          return value.replace('duration("', "").replace('")', "");
        default:
          return `${value}`;
      }
    },
    component: (props: ConstraintProps) => {
      switch (typeRef) {
        case DmnBuiltInDataType.Date:
          return <ConstraintDate {...props} />;
        case DmnBuiltInDataType.DateTime:
          return <ConstraintDateTime {...props} />;
        case DmnBuiltInDataType.DateTimeDuration:
          return <ConstraintDateTimeDuration {...props} />;
        case DmnBuiltInDataType.Time:
          return <ConstraintTime {...props} />;
        case DmnBuiltInDataType.YearsMonthsDuration:
          return <ConstraintYearsMonthsDuration {...props} />;
        case DmnBuiltInDataType.Number:
          return (
            <TextInput
              autoFocus={props.autoFocus}
              onBlur={props.onBlur}
              onChange={props.onChange}
              id={props.id}
              isDisabled={props.isDisabled}
              placeholder={props.placeholder}
              style={{ ...props.style, ...(props.isValid ? {} : invalidInlineFeelNameStyle) }}
              type={"number"}
              value={props.value}
            />
          );
        case DmnBuiltInDataType.Any:
        case DmnBuiltInDataType.String:
        default:
          return (
            <TextInput
              autoFocus={props.autoFocus}
              onBlur={props.onBlur}
              onChange={props.onChange}
              id={props.id}
              isDisabled={props.isDisabled}
              placeholder={props.placeholder}
              style={{ ...props.style, ...(props.isValid ? {} : invalidInlineFeelNameStyle) }}
              type={"text"}
              value={props.value}
            />
          );
      }
    },
  };
};

export function Constraints({
  isReadonly,
  itemDefinition,
  editItemDefinition,
}: {
  isReadonly: boolean;
  itemDefinition: DMN15__tItemDefinition;
  editItemDefinition: EditItemDefinition;
}) {
  const typeConstraint = useMemo(
    () => itemDefinition?.typeConstraint ?? itemDefinition?.allowedValues,
    [itemDefinition?.allowedValues, itemDefinition?.typeConstraint]
  );
  const constraintValue = useMemo(
    () => typeConstraint?.text.__$$text ?? typeConstraint?.text.__$$text,
    [typeConstraint?.text.__$$text]
  );
  const kieConstraintType = useMemo(() => typeConstraint?.["@_kie:constraintType"], [typeConstraint]);
  const typeRef: DmnBuiltInDataType = useMemo(
    () => (itemDefinition?.typeRef?.__$$text ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
    [itemDefinition?.typeRef?.__$$text]
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
    const enabledConstraints = constrainableBuiltInFeelTypes.get(typeRef);
    return {
      enumeration: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [enumToKieConstraintType, typeRef]);

  // Correctly set the selection on the first load
  const [selectedConstraint, setSelectConstraint] = useState<ConstraintsType>(() => {
    if (isConstraintEnabled.enumeration && kieConstraintType === "enumeration") {
      return ConstraintsType.ENUMERATION;
    }
    if (isConstraintEnabled.range && kieConstraintType === "range") {
      return ConstraintsType.RANGE;
    }
    if (isConstraintEnabled.expression && kieConstraintType === "expression") {
      return ConstraintsType.EXPRESSION;
    }
    if (typeRef === DmnBuiltInDataType.Any) {
      return ConstraintsType.EXPRESSION;
    }
    if (constraintValue === undefined) {
      return ConstraintsType.NONE;
    }
    if (isConstraintEnabled.enumeration && isEnum(constraintValue, constraintTypeHelper(typeRef).check)) {
      return ConstraintsType.ENUMERATION;
    }
    if (isConstraintEnabled.range && isRange(constraintValue, constraintTypeHelper(typeRef).check)) {
      return ConstraintsType.RANGE;
    }
    return ConstraintsType.EXPRESSION;
  });

  // Update the selection if the typeRef is updated
  useEffect(() => {
    if (kieConstraintType === undefined) {
      return;
    }
    if (isConstraintEnabled.enumeration && kieConstraintType === "enumeration") {
      setSelectConstraint(ConstraintsType.ENUMERATION);
    }
    if (isConstraintEnabled.range && kieConstraintType === "range") {
      setSelectConstraint(ConstraintsType.RANGE);
    }
    if (isConstraintEnabled.expression && kieConstraintType === "expression") {
      setSelectConstraint(ConstraintsType.EXPRESSION);
    }
    if (typeRef === DmnBuiltInDataType.Any) {
      setSelectConstraint(ConstraintsType.EXPRESSION);
    }
  }, [
    isConstraintEnabled.enumeration,
    isConstraintEnabled.expression,
    isConstraintEnabled.range,
    kieConstraintType,
    typeRef,
  ]);

  // Updates the XML value only if the constraint is valid
  const onInternalChange = useCallback(
    (args: { constraintType: ConstraintsType; value?: string }) => {
      const kieConstraintType = enumToKieConstraintType(args.constraintType);

      if (isReadonly) {
        return;
      }

      editItemDefinition(itemDefinition["@_id"]!, (itemDefinition) => {
        if (args.value === undefined || args.value === "") {
          itemDefinition.typeConstraint = undefined;
          return;
        }

        if (
          (args.value === itemDefinition.allowedValues?.text?.__$$text ||
            args.value === itemDefinition.typeConstraint?.text?.__$$text) &&
          (kieConstraintType === itemDefinition.allowedValues?.["@_kie:constraintType"] ||
            kieConstraintType === itemDefinition.typeConstraint?.["@_kie:constraintType"])
        ) {
          return;
        }

        // If DMN has an allowedValues, erase it as it will be replaced by a typeConstraint.
        if (itemDefinition.allowedValues?.text?.__$$text || itemDefinition.allowedValues?.["@_kie:constraintType"]) {
          itemDefinition.allowedValues = undefined;
        }

        itemDefinition.typeConstraint = {
          text: { __$$text: args.value },
          "@_id": itemDefinition?.["@_id"],
          "@_kie:constraintType": kieConstraintType,
        };
      });
    },
    [editItemDefinition, enumToKieConstraintType, isReadonly, itemDefinition]
  );

  const onToggleGroupChange = useCallback(
    (newSelection, event) => {
      if (!newSelection) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      setSelectConstraint(selection);

      if (selection === ConstraintsType.NONE) {
        onInternalChange({
          constraintType: ConstraintsType.NONE,
          value: undefined,
        });
        return;
      }

      if (constraintValue === undefined || constraintValue === "") {
        return;
      }

      if (
        selection === ConstraintsType.ENUMERATION &&
        isEnum(constraintValue ?? "", constraintTypeHelper(typeRef).check)
      ) {
        onInternalChange({
          constraintType: ConstraintsType.ENUMERATION,
          value: constraintValue,
        });
        return;
      }

      if (selection === ConstraintsType.RANGE && isRange(constraintValue ?? "", constraintTypeHelper(typeRef).check)) {
        onInternalChange({
          constraintType: ConstraintsType.RANGE,
          value: constraintValue,
        });
        return;
      }

      if (selection === ConstraintsType.EXPRESSION) {
        onInternalChange({
          constraintType: ConstraintsType.EXPRESSION,
          value: constraintValue,
        });
        return;
      }
    },
    [constraintValue, onInternalChange, typeRef]
  );

  const constraintComponent = useMemo(() => {
    if (selectedConstraint === ConstraintsType.ENUMERATION) {
      return (
        <ConstraintsEnum
          isReadonly={isReadonly}
          type={typeRef}
          typeHelper={constraintTypeHelper(typeRef)}
          value={isEnum(constraintValue, constraintTypeHelper(typeRef).check) ? constraintValue : undefined}
          savedValue={constraintValue}
          onSave={(value?: string) =>
            onInternalChange({
              constraintType: ConstraintsType.ENUMERATION,
              value,
            })
          }
          isDisabled={!isConstraintEnabled.enumeration}
        />
      );
    }
    if (selectedConstraint === ConstraintsType.RANGE) {
      return (
        <ConstraintsRange
          isReadonly={isReadonly}
          savedValue={constraintValue}
          type={typeRef}
          typeHelper={constraintTypeHelper(typeRef)}
          value={isRange(constraintValue, constraintTypeHelper(typeRef).check) ? constraintValue : undefined}
          onSave={(value?: string) =>
            onInternalChange({
              constraintType: ConstraintsType.RANGE,
              value,
            })
          }
          isDisabled={!isConstraintEnabled.range}
        />
      );
    }
    if (selectedConstraint === ConstraintsType.EXPRESSION) {
      return (
        <ConstraintsExpression
          isReadonly={isReadonly}
          type={typeRef}
          value={constraintValue}
          savedValue={constraintValue}
          onSave={(value?: string) =>
            onInternalChange({
              constraintType: ConstraintsType.EXPRESSION,
              value,
            })
          }
          isDisabled={false}
        />
      );
    }

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
  }, [
    constraintValue,
    isConstraintEnabled.enumeration,
    isConstraintEnabled.range,
    isReadonly,
    onInternalChange,
    selectedConstraint,
    typeRef,
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
                isSelected={selectedConstraint === ConstraintsType.NONE}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly}
              />
              <ToggleGroupItem
                text={ConstraintsType.EXPRESSION}
                buttonId={ConstraintsType.EXPRESSION}
                isSelected={selectedConstraint === ConstraintsType.EXPRESSION}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly}
              />
              <ToggleGroupItem
                text={ConstraintsType.ENUMERATION}
                buttonId={ConstraintsType.ENUMERATION}
                isSelected={selectedConstraint === ConstraintsType.ENUMERATION}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly || !isConstraintEnabled.enumeration}
              />
              <ToggleGroupItem
                text={ConstraintsType.RANGE}
                buttonId={ConstraintsType.RANGE}
                isSelected={selectedConstraint === ConstraintsType.RANGE}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly || !isConstraintEnabled.range}
              />
            </ToggleGroup>
          </div>

          <div style={{ paddingTop: "10px" }}>{constraintComponent}</div>
        </div>
      )}
    </>
  );
}
