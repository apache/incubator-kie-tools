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
import { ConstraintDateTimeDuration } from "./ConstraintComponents/ConstraintDateTimeDuration";
import { ConstraintTime } from "./ConstraintComponents/ConstraintTime";
import { ConstraintYearsMonthsDuration } from "./ConstraintComponents/ConstraintYearsMonthsDuration";
import { invalidInlineFeelNameStyle } from "../feel/InlineFeelNameInput";

const REGEX_DURATION_ISO_8601 = /^P(?!$)(\d+Y)?(\d+M)?(\d+W)?(\d+D)?(T(?=\d)(\d+H)?(\d+M)?(\d+S)?)?$/;

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

  const typeRef: DmnBuiltInDataType = useMemo(
    () => (itemDefinition?.typeRef?.__$$text ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
    [itemDefinition?.typeRef?.__$$text]
  );
  const savedTypeConstraint = useMemo(() => itemDefinition?.typeConstraint, [itemDefinition?.typeConstraint]);

  useEffect(() => {
    setConstraintValue({ value: undefined, isValid: true });
    setSelected(ConstraintsType.NONE);
  }, [typeRef]);

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

  const [selected, setSelected] = useState<ConstraintsType>(
    kieContraintTypeToEnum(savedTypeConstraint?.["@_kie:constraintType"])
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

  // Updates the XML value only if the constraint is valid
  const onInternalChange = useCallback(
    (args: { constraintType: ConstraintsType; isValid: boolean; newValue?: string }) => {
      const kieConstraintType = enumToKieConstraintType(args.constraintType);
      setConstraintValue({ value: args.newValue, isValid: args.isValid });

      if (isReadonly) {
        return;
      }

      editItemDefinition(itemDefinition["@_id"]!, (itemDefinition) => {
        if (args.newValue === undefined || !args.isValid) {
          itemDefinition.typeConstraint = undefined;
          return;
        }

        if (
          args.newValue === itemDefinition.typeConstraint?.text?.__$$text &&
          kieConstraintType === itemDefinition.typeConstraint["@_kie:constraintType"]
        ) {
          return;
        }

        itemDefinition.typeConstraint = {
          text: { __$$text: args.newValue },
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
            return moment(recoveredValue, "YYYY-MM-DD", true).isValid();
          case DmnBuiltInDataType.DateTime:
            return moment(recoveredValue, "YYYY-MM-DDThh:mm", true).isValid();
          case DmnBuiltInDataType.DateTimeDuration:
          case DmnBuiltInDataType.YearsMonthsDuration:
            const test = REGEX_DURATION_ISO_8601.test(recoveredValue);
            return test;
          case DmnBuiltInDataType.Number:
            return !isNaN(parseFloat(recoveredValue));
          case DmnBuiltInDataType.Time:
            return moment(recoveredValue, "hh:mm", true).isValid();
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
      component: (props: any) => {
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

  const onToggleGroupChange = useCallback(
    (newSelection, event) => {
      if (!newSelection) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      setSelected(selection);

      if (selection === ConstraintsType.NONE) {
        onInternalChange({
          constraintType: ConstraintsType.NONE,
          newValue: undefined,
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
          newValue: constraint.value,
          isValid: constraint.isValid,
        });
        return;
      }

      if (selection === ConstraintsType.RANGE && isRange(constraint.value ?? "", typeHelper.check)) {
        onInternalChange({
          constraintType: ConstraintsType.RANGE,
          newValue: constraint.value,
          isValid: constraint.isValid,
        });
        return;
      }

      if (selection === ConstraintsType.EXPRESSION) {
        onInternalChange({
          constraintType: ConstraintsType.EXPRESSION,
          newValue: constraint.value,
          isValid: constraint.isValid,
        });
        return;
      }
    },
    [constraint, onInternalChange, typeHelper.check]
  );

  const constraintComponent = useMemo(() => {
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
          value={constraint.value}
          savedValue={savedTypeConstraint?.text.__$$text}
          type={typeRef}
          typeHelper={typeHelper}
          onChange={(args: { newValue?: string; isValid: boolean }) => {
            onInternalChange({
              constraintType: ConstraintsType.ENUMERATION,
              newValue: args.newValue,
              isValid: args.isValid,
            });
            setConstraintValue({ value: args.newValue, isValid: args.isValid });
          }}
          isDisabled={!isConstraintEnabled.enumeration}
        />
      );
    }
    if (selected === ConstraintsType.RANGE) {
      return (
        <ConstraintsRange
          isReadonly={isReadonly}
          value={constraint.value}
          savedValue={savedTypeConstraint?.text.__$$text}
          type={typeRef}
          typeHelper={typeHelper}
          onChange={(args: { newValue?: string; isValid: boolean }) => {
            onInternalChange({
              constraintType: ConstraintsType.RANGE,
              newValue: args.newValue,
              isValid: args.isValid,
            });
            setConstraintValue({ value: args.newValue, isValid: args.isValid });
          }}
          isDisabled={!isConstraintEnabled.range}
        />
      );
    }
    if (selected === ConstraintsType.EXPRESSION) {
      return (
        <ConstraintsExpression
          isReadonly={isReadonly}
          value={constraint.value}
          type={typeRef}
          onChange={(args: { newValue?: string; isValid: boolean }) => {
            onInternalChange({
              constraintType: ConstraintsType.EXPRESSION,
              newValue: args.newValue,
              isValid: args.isValid,
            });
            setConstraintValue({ value: args.newValue, isValid: args.isValid });
          }}
        />
      );
    }
  }, [
    isConstraintEnabled.enumeration,
    isConstraintEnabled.range,
    isReadonly,
    constraint,
    onInternalChange,
    savedTypeConstraint?.text.__$$text,
    selected,
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

          <div style={{ paddingTop: "10px" }}>{constraintComponent}</div>
        </div>
      )}
    </>
  );
}
