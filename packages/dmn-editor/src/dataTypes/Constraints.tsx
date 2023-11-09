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
  const [selected, setSelected] = useState<ConstraintsType>(ConstraintsType.NONE);
  const [constraintValue, setConstraintValue] = useState(itemDefinition?.typeConstraint?.text.__$$text);
  const [constraintOrigin, setConstraintOrigin] = useState<ConstraintsType>(ConstraintsType.NONE);
  const [isConstraintValid, setConstraintValidity] = useState(true);

  const type: DmnBuiltInDataType = useMemo(
    () => (itemDefinition?.typeRef?.__$$text ?? DmnBuiltInDataType.Undefined) as DmnBuiltInDataType,
    [itemDefinition?.typeRef?.__$$text]
  );
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

  useEffect(() => {
    if (isConstraintValid) {
      onInternalChange(constraintValue, enumToKieConstraintType(constraintOrigin));
    }
  }, [constraintOrigin, constraintValue, enumToKieConstraintType, isConstraintValid, onInternalChange]);

  const isConstraintEnabled = useMemo(() => {
    const enabledConstraints = constrainableBuiltInFeelTypes.get(type as DmnBuiltInDataType);
    return {
      enumeration: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression: (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [enumToKieConstraintType, type]);

  const typeHelper = useMemo(() => {
    return {
      check: (value: string) => {
        switch (type) {
          case DmnBuiltInDataType.Any:
          case DmnBuiltInDataType.String:
            return typeof value === "string";
          case DmnBuiltInDataType.Date:
            return moment(value, "YYYY-MM-DD", true).isValid();
          case DmnBuiltInDataType.DateTime:
            return moment(value, "YYYY-MM-DDThh:mm", true).isValid();
          case DmnBuiltInDataType.DateTimeDuration:
          case DmnBuiltInDataType.YearsMonthsDuration:
            return moment.duration(value).isValid();
          case DmnBuiltInDataType.Number:
            return !isNaN(parseFloat(value));
          case DmnBuiltInDataType.Time:
            return moment(value, "hh:mm", true).isValid();
          default:
            return false;
        }
      },
      parse: (value: string) => {
        switch (type) {
          case DmnBuiltInDataType.Number:
            return parseFloat(value);
          case DmnBuiltInDataType.DateTimeDuration:
          case DmnBuiltInDataType.YearsMonthsDuration:
            return moment.duration(value);
          case DmnBuiltInDataType.Any:
          case DmnBuiltInDataType.Date:
          case DmnBuiltInDataType.DateTime:
          case DmnBuiltInDataType.String:
          case DmnBuiltInDataType.Time:
          default:
            return value;
        }
      },
      transform: (value: string) => {
        switch (type) {
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
      recover: (value: string) => {
        switch (type) {
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
        switch (type) {
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
  }, [type]);

  // Start in the previous constraint type
  useEffect(() => {
    setSelected(kieContraintTypeToEnum(value?.["@_kie:constraintType"]));
  }, [kieContraintTypeToEnum, value]);

  const onToggleGroupChange = useCallback(
    (newSelection, event) => {
      if (!newSelection) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      setSelected(selection);

      if (selection === ConstraintsType.NONE) {
        setConstraintValue(undefined);
        setConstraintOrigin(ConstraintsType.NONE);
        return;
      }

      if (constraintValue === undefined || constraintValue === "") {
        return;
      }

      if (selection === ConstraintsType.ENUMERATION && isEnum(constraintValue ?? "", typeHelper.check)) {
        setConstraintOrigin(ConstraintsType.ENUMERATION);
        return;
      }

      if (selection === ConstraintsType.RANGE && isRange(constraintValue ?? "", typeHelper.check)) {
        setConstraintOrigin(ConstraintsType.RANGE);
        return;
      }

      if (selection === ConstraintsType.EXPRESSION) {
        setConstraintOrigin(ConstraintsType.EXPRESSION);
        return;
      }

      setConstraintOrigin(selected);
    },
    [constraintValue, selected, typeHelper.check]
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
          value={constraintValue}
          savedValue={value?.text.__$$text}
          type={type as DmnBuiltInDataType}
          typeHelper={typeHelper}
          onChange={(newValue: string) => {
            setConstraintValue(newValue);
            setConstraintOrigin(ConstraintsType.ENUMERATION);
          }}
          isDisabled={!isConstraintEnabled.enumeration}
          setConstraintValidity={(isValid: boolean) => setConstraintValidity(isValid)}
        />
      );
    }
    if (selected === ConstraintsType.RANGE) {
      return (
        <ConstraintsRange
          isReadonly={isReadonly}
          value={constraintValue}
          savedValue={value?.text.__$$text}
          type={type as DmnBuiltInDataType}
          typeHelper={typeHelper}
          onChange={(newValue: string) => {
            setConstraintValue(newValue);
            setConstraintOrigin(ConstraintsType.RANGE);
          }}
          isDisabled={!isConstraintEnabled.range}
          setConstraintValidity={(isValid: boolean) => setConstraintValidity(isValid)}
        />
      );
    }
    if (selected === ConstraintsType.EXPRESSION) {
      return (
        <ConstraintsExpression
          isReadonly={isReadonly}
          value={constraintValue}
          type={type as DmnBuiltInDataType}
          onChange={(newValue: string) => {
            setConstraintValue(newValue);
            setConstraintOrigin(ConstraintsType.EXPRESSION);
          }}
        />
      );
    }
  }, [
    constraintValue,
    value,
    isConstraintEnabled.enumeration,
    isConstraintEnabled.range,
    isReadonly,
    selected,
    type,
    typeHelper,
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
