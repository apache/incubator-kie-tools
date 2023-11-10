import * as React from "react";
import { useMemo, useState, useCallback } from "react";
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

export interface ConstraintComponentProps {
  isReadonly: boolean;
  value?: string;
  savedValue?: string;
  type: DmnBuiltInDataType;
  typeHelper: TypeHelper;
  onChange: React.Dispatch<
    React.SetStateAction<{
      value: string | undefined;
      isValid: boolean;
    }>
  >;
  onSave: (args: { value?: string; isValid: boolean }) => void;
  isDisabled: boolean;
}

export type TypeHelper = {
  check: (value: string) => boolean;
  parse: (value: string) => any;
  transform: (value: string) => string;
  recover: (value: string) => string;
  component: (props: any) => React.ReactNode | undefined;
};

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
  const [constraint, setConstraintValue] = useState({
    value: itemDefinition?.typeConstraint?.text.__$$text,
    isValid: true,
  });
  const constraintValue = useMemo(
    () => itemDefinition?.typeConstraint?.text.__$$text,
    [itemDefinition?.typeConstraint]
  );
  const kieConstraintType = useMemo(
    () => itemDefinition?.typeConstraint?.["@_kie:constraintType"],
    [itemDefinition?.typeConstraint]
  );
  const [selectedConstraint, setSelectConstraint] = useState<ConstraintsType>(() => {
    switch (kieConstraintType) {
      case "enumeration":
        return ConstraintsType.ENUMERATION;
      case "expression":
        return ConstraintsType.EXPRESSION;
      case "range":
        return ConstraintsType.RANGE;
      default:
        return ConstraintsType.NONE;
    }
  });

  const typeRef: DmnBuiltInDataType = useMemo(
    () => (itemDefinition?.typeRef?.__$$text ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
    [itemDefinition?.typeRef?.__$$text]
  );
  const typeHelper = useMemo(() => {
    return {
      // check if the value has the correct type
      check: (value: string) => {
        const recoveredValue = typeHelper.recover(value);
        switch (typeRef) {
          case DmnBuiltInDataType.Any:
          case DmnBuiltInDataType.String:
            return typeof recoveredValue === "string";
          case DmnBuiltInDataType.Date:
            return moment(recoveredValue, "YYYY-MM-DD", true).isValid() || value === "";
          case DmnBuiltInDataType.DateTime:
            return moment(recoveredValue, "YYYY-MM-DDThh:mm", true).isValid() || value === "";
          case DmnBuiltInDataType.DateTimeDuration:
            return REGEX_DATE_TIME_DURATION.test(recoveredValue) || value === "";
          case DmnBuiltInDataType.Number:
            return !isNaN(parseFloat(recoveredValue)) || value === "";
          case DmnBuiltInDataType.Time:
            return moment(recoveredValue, "hh:mm", true).isValid() || value === "";
          case DmnBuiltInDataType.YearsMonthsDuration:
            return REGEX_YEARS_MONTH_DURATION.test(recoveredValue) || value === "";
          default:
            return false;
        }
      },
      // parse the value to the type
      // useful to make comparisons
      parse: (value: string) => {
        const recoveredValue = typeHelper.recover(value);
        switch (typeRef) {
          case DmnBuiltInDataType.Number:
            return parseFloat(recoveredValue);
          case DmnBuiltInDataType.DateTimeDuration:
          case DmnBuiltInDataType.YearsMonthsDuration:
            return moment.duration(recoveredValue);
          case DmnBuiltInDataType.Any:
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
            return value;
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
              return JSON.parse(value);
            } catch (error) {
              return value;
            }
          case DmnBuiltInDataType.Date:
            return value.replace('date("', "").replace('")', "");
          case DmnBuiltInDataType.DateTime:
            return `date and time("${value}")`;
          case DmnBuiltInDataType.DateTimeDuration:
            return value.replace('duration("', "").replace('")', "");
          case DmnBuiltInDataType.Number:
            return value;
          case DmnBuiltInDataType.Time:
            return value.replace('time("', "").replace('")', "");
          case DmnBuiltInDataType.YearsMonthsDuration:
            return value.replace('duration("', "").replace('")', "");
          default:
            return value;
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
  }, [typeRef]);

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

  // Updates the XML value only if the constraint is valid
  const onInternalChange = useCallback(
    (args: { constraintType: ConstraintsType; isValid: boolean; value?: string }) => {
      const kieConstraintType = enumToKieConstraintType(args.constraintType);
      setConstraintValue({ value: args.value, isValid: args.isValid });

      if (isReadonly) {
        return;
      }

      editItemDefinition(itemDefinition["@_id"]!, (itemDefinition) => {
        if (args.value === undefined || !args.isValid) {
          itemDefinition.typeConstraint = undefined;
          return;
        }

        if (
          args.value === itemDefinition.typeConstraint?.text?.__$$text &&
          kieConstraintType === itemDefinition.typeConstraint["@_kie:constraintType"]
        ) {
          return;
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

  const isConstraintEnabled = useMemo(() => {
    const enabledConstraints = constrainableBuiltInFeelTypes.get(typeRef);
    return {
      enumeration: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [enumToKieConstraintType, typeRef]);

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
          isValid: true,
        });
        return;
      }

      if (constraint.value === undefined || constraint.value === "") {
        return;
      }

      if (selection === ConstraintsType.ENUMERATION && isEnum(constraint.value ?? "", typeHelper.check)) {
        onInternalChange({
          constraintType: ConstraintsType.ENUMERATION,
          value: constraint.value,
          isValid: constraint.isValid,
        });
        return;
      }

      if (selection === ConstraintsType.RANGE && isRange(constraint.value ?? "", typeHelper.check)) {
        onInternalChange({
          constraintType: ConstraintsType.RANGE,
          value: constraint.value,
          isValid: constraint.isValid,
        });
        return;
      }

      if (selection === ConstraintsType.EXPRESSION) {
        onInternalChange({
          constraintType: ConstraintsType.EXPRESSION,
          value: constraint.value,
          isValid: constraint.isValid,
        });
        return;
      }
    },
    [constraint, onInternalChange, typeHelper.check]
  );

  const constraintComponent = useMemo(() => {
    if (selectedConstraint === ConstraintsType.ENUMERATION) {
      return (
        <ConstraintsEnum
          isReadonly={isReadonly}
          type={typeRef}
          typeHelper={typeHelper}
          value={isEnum(constraint.value, typeHelper.check) ? constraint.value : undefined}
          onChange={setConstraintValue}
          savedValue={constraintValue}
          onSave={(args: { value?: string; isValid: boolean }) => {
            onInternalChange({
              constraintType: ConstraintsType.ENUMERATION,
              value: args.value,
              isValid: args.isValid,
            });
          }}
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
          typeHelper={typeHelper}
          value={isRange(constraint.value, typeHelper.check) ? constraint.value : undefined}
          onChange={setConstraintValue}
          onSave={(args: { value?: string; isValid: boolean }) => {
            onInternalChange({
              constraintType: ConstraintsType.RANGE,
              value: args.value,
              isValid: args.isValid,
            });
          }}
          isDisabled={!isConstraintEnabled.range}
        />
      );
    }
    if (selectedConstraint === ConstraintsType.EXPRESSION) {
      return (
        <ConstraintsExpression
          isReadonly={isReadonly}
          type={typeRef}
          value={constraint.value}
          onChange={setConstraintValue}
          savedValue={constraintValue}
          onSave={(args: { value?: string; isValid: boolean }) => {
            onInternalChange({
              constraintType: ConstraintsType.EXPRESSION,
              value: args.value,
              isValid: args.isValid,
            });
          }}
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
    constraint.value,
    constraintValue,
    isConstraintEnabled.enumeration,
    isConstraintEnabled.range,
    isReadonly,
    onInternalChange,
    selectedConstraint,
    typeHelper,
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
