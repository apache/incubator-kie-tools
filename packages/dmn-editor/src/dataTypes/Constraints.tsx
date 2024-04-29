/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useMemo, useCallback } from "react";
import { ConstraintsExpression } from "./ConstraintsExpression";
import {
  DMN15__tItemDefinition,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { DmnBuiltInDataType, generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ConstraintsEnum, isEnum } from "./ConstraintsEnum";
import { ConstraintsRange, isRange } from "./ConstraintsRange";
import { KIE__tConstraintType } from "@kie-tools/dmn-marshaller/dist/schemas/kie-1_0/ts-gen/types";
import { EditItemDefinition } from "./DataTypes";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { constrainableBuiltInFeelTypes } from "./DataTypeSpec";
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
  recover: (value?: string) => string | undefined;
  component: (props: any) => React.ReactNode | undefined;
};

export interface ConstraintComponentProps {
  isReadonly: boolean;
  value?: string;
  expressionValue?: string;
  type: DmnBuiltInDataType;
  typeHelper: TypeHelper;
  onSave: (value?: string) => void;
  isDisabled: boolean;
  renderOnPropertiesPanel?: boolean;
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
          if (recoveredValue === "") {
            return true;
          }
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
          return moment(recoveredValue, "YYYY-MM-DD", true).isValid() || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.DateTime:
          return (
            moment(recoveredValue, "YYYY-MM-DDTHH:mm:ssZZ", true).isValid() ||
            moment(recoveredValue, "YYYY-MM-DD", true).isValid() ||
            value === "" ||
            recoveredValue === ""
          );
        case DmnBuiltInDataType.DateTimeDuration:
          return REGEX_DATE_TIME_DURATION.test(recoveredValue ?? "") || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.Number:
          return !isNaN(parseFloat(recoveredValue ?? "")) || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.Time:
          return moment(recoveredValue, "HH:mm:ssZZ", true).isValid() || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.YearsMonthsDuration:
          return REGEX_YEARS_MONTH_DURATION.test(recoveredValue ?? "") || value === "" || recoveredValue === "";
        default:
          return false;
      }
    },
    // parse the value to the type
    // useful for comparisons
    parse: (value: string) => {
      const recoveredValue = constraintTypeHelper(typeRef).recover(value);
      switch (typeRef) {
        case DmnBuiltInDataType.Number:
          return parseFloat(recoveredValue ?? "");
        case DmnBuiltInDataType.DateTimeDuration:
        case DmnBuiltInDataType.YearsMonthsDuration:
          return moment.duration(recoveredValue);
        case DmnBuiltInDataType.DateTime:
          return moment(recoveredValue).toDate();
        case DmnBuiltInDataType.Date:
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
    recover: (value?: string) => {
      if (value === undefined) {
        return undefined;
      }
      switch (typeRef) {
        case DmnBuiltInDataType.Any:
          if (value === "") {
            return "";
          }
          try {
            return `${JSON.parse(value)}`;
          } catch (error) {
            return undefined;
          }
        case DmnBuiltInDataType.String:
          if (value === "") {
            return "";
          }
          try {
            if (typeof JSON.parse(value) !== "string") {
              return undefined;
            }
            return `${JSON.parse(value)}`;
          } catch (error) {
            return undefined;
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
              onChange={props.onChange}
              id={props.id}
              isDisabled={props.isDisabled}
              value={props.value}
              onKeyDown={props.onKeyDown}
              style={{ ...props.style, ...(props.isValid ? {} : invalidInlineFeelNameStyle) }}
              type={"number"}
            />
          );
        case DmnBuiltInDataType.Any:
        case DmnBuiltInDataType.String:
        default:
          return (
            <TextInput
              autoFocus={props.autoFocus}
              onChange={props.onChange}
              id={props.id}
              isDisabled={props.isDisabled}
              value={props.value}
              onKeyDown={props.onKeyDown}
              style={{ ...props.style, ...(props.isValid ? {} : invalidInlineFeelNameStyle) }}
              type={"text"}
            />
          );
      }
    },
  };
};

export function useConstraint({
  constraint,
  itemDefinition,
  isCollectionConstraintEnable,
}: {
  constraint: DMN15__tUnaryTests | undefined;
  itemDefinition: DMN15__tItemDefinition;
  isCollectionConstraintEnable: boolean;
}) {
  const constraintValue = useMemo(() => constraint?.text.__$$text, [constraint?.text.__$$text]);
  const kieConstraintType = useMemo(() => constraint?.["@_kie:constraintType"], [constraint]);
  const isCollection = useMemo(() => itemDefinition["@_isCollection"] ?? false, [itemDefinition]);
  const itemDefinitionId = useMemo(() => itemDefinition["@_id"], [itemDefinition]);

  const typeRef: DmnBuiltInDataType = useMemo(
    () => (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined,
    [itemDefinition?.typeRef?.__$$text]
  );

  const isConstraintEnum = useMemo(
    () =>
      isCollection === true && isCollectionConstraintEnable === true // collection doesn't support enumeration constraint
        ? undefined
        : isEnum(constraintValue, constraintTypeHelper(typeRef).check),
    [constraintValue, isCollectionConstraintEnable, isCollection, typeRef]
  );

  const isConstraintRange = useMemo(
    () =>
      isCollection === true && isCollectionConstraintEnable === true // collection doesn't support range constraint
        ? undefined
        : isRange(constraintValue, constraintTypeHelper(typeRef).check),
    [constraintValue, isCollectionConstraintEnable, isCollection, typeRef]
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
      enumeration:
        !(isCollection === true && isCollectionConstraintEnable === true) &&
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range:
        !(isCollection === true && isCollectionConstraintEnable === true) &&
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression:
        (isCollection === true && isCollectionConstraintEnable === true) ||
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [typeRef, isCollection, isCollectionConstraintEnable, enumToKieConstraintType]);

  const selectedConstraint = useMemo<ConstraintsType>(() => {
    if (isConstraintEnabled.enumeration && kieConstraintType === "enumeration") {
      return ConstraintsType.ENUMERATION;
    }
    if (isConstraintEnabled.range && kieConstraintType === "range") {
      return ConstraintsType.RANGE;
    }
    if (isConstraintEnabled.expression && kieConstraintType === "expression") {
      return ConstraintsType.EXPRESSION;
    }
    if (kieConstraintType === undefined && constraintValue && isConstraintEnabled.enumeration && isConstraintEnum) {
      return ConstraintsType.ENUMERATION;
    }
    if (kieConstraintType === undefined && constraintValue && isConstraintEnabled.range && isConstraintRange) {
      return ConstraintsType.RANGE;
    }
    if (kieConstraintType === undefined && constraintValue) {
      return ConstraintsType.EXPRESSION;
    }
    return ConstraintsType.NONE;
  }, [
    constraintValue,
    isConstraintEnabled.enumeration,
    isConstraintEnabled.expression,
    isConstraintEnabled.range,
    isConstraintEnum,
    isConstraintRange,
    kieConstraintType,
  ]);

  return useMemo(() => {
    return {
      constraintValue,
      typeRef,
      isConstraintEnum,
      isConstraintRange,
      isConstraintEnabled,
      itemDefinitionId,
      selectedConstraint,
      enumToKieConstraintType,
    };
  }, [
    constraintValue,
    isConstraintEnabled,
    isConstraintEnum,
    isConstraintRange,
    itemDefinitionId,
    selectedConstraint,
    typeRef,
    enumToKieConstraintType,
  ]);
}

export function ConstraintsFromAllowedValuesAttribute({
  isReadonly,
  itemDefinition,
  editItemDefinition,
  renderOnPropertiesPanel,
}: {
  isReadonly: boolean;
  itemDefinition: DMN15__tItemDefinition;
  editItemDefinition: EditItemDefinition;
  renderOnPropertiesPanel?: boolean;
  isEnumDisabled?: boolean;
  isRangeDisabled?: boolean;
}) {
  const allowedValues = useMemo(() => itemDefinition?.allowedValues, [itemDefinition?.allowedValues]);

  const {
    constraintValue,
    typeRef,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    itemDefinitionId,
    selectedConstraint,
    enumToKieConstraintType,
  } = useConstraint({
    constraint: allowedValues,
    itemDefinition,
    isCollectionConstraintEnable: false, // allowedValues doesn't support constraint to the collection itself
  });

  const onConstraintChange = useCallback(
    (value?: string) => {
      editItemDefinition(itemDefinitionId!, (itemDefinition) => {
        itemDefinition.allowedValues ??= { text: { __$$text: "" } };
        itemDefinition.allowedValues.text.__$$text = value ?? "";
        itemDefinition.allowedValues["@_id"] = itemDefinition.allowedValues?.["@_id"] ?? generateUuid();
        return;
      });
    },
    [editItemDefinition, itemDefinitionId]
  );

  const onToggleGroupChange = useCallback(
    (newSelection: boolean, event: React.KeyboardEvent<Element> | MouseEvent | React.MouseEvent<any, MouseEvent>) => {
      if (!newSelection) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      if (selection === ConstraintsType.NONE) {
        editItemDefinition(itemDefinitionId!, (itemDefinition) => {
          itemDefinition.allowedValues = undefined;
        });
        return;
      }

      editItemDefinition(itemDefinitionId!, (itemDefinition) => {
        itemDefinition.allowedValues ??= { text: { __$$text: "" } };
        const previousKieContraintType = itemDefinition.allowedValues["@_kie:constraintType"];
        itemDefinition.allowedValues["@_kie:constraintType"] = enumToKieConstraintType(selection);

        if (selection === ConstraintsType.EXPRESSION) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.ENUMERATION &&
          isEnum(
            itemDefinition.allowedValues.text.__$$text,
            constraintTypeHelper(
              (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
            ).check
          )
        ) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.RANGE &&
          isRange(
            itemDefinition.allowedValues.text.__$$text,
            constraintTypeHelper(
              (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
            ).check
          )
        ) {
          return;
        }

        itemDefinition.allowedValues.text.__$$text = "";
        return;
      });
    },
    [editItemDefinition, enumToKieConstraintType, itemDefinitionId]
  );

  return (
    <Constraints
      isReadonly={isReadonly}
      itemDefinition={itemDefinition}
      constraintValue={constraintValue}
      typeRef={typeRef}
      isConstraintEnum={isConstraintEnum}
      isConstraintRange={isConstraintRange}
      isConstraintEnabled={isConstraintEnabled}
      selectedConstraint={selectedConstraint}
      onToggleGroupChange={onToggleGroupChange}
      onConstraintChange={onConstraintChange}
      renderOnPropertiesPanel={renderOnPropertiesPanel}
    />
  );
}

export function ConstraintsFromTypeConstraintAttribute({
  isReadonly,
  itemDefinition,
  editItemDefinition,
  renderOnPropertiesPanel,
  defaultsToAllowedValues,
}: {
  isReadonly: boolean;
  itemDefinition: DMN15__tItemDefinition;
  editItemDefinition: EditItemDefinition;
  renderOnPropertiesPanel?: boolean;
  defaultsToAllowedValues: boolean;
}) {
  const typeConstraint = useMemo(
    () =>
      defaultsToAllowedValues
        ? itemDefinition?.typeConstraint ?? itemDefinition?.allowedValues
        : itemDefinition?.typeConstraint,
    [defaultsToAllowedValues, itemDefinition?.allowedValues, itemDefinition?.typeConstraint]
  );

  const {
    constraintValue,
    typeRef,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    itemDefinitionId,
    selectedConstraint,
    enumToKieConstraintType,
  } = useConstraint({
    constraint: typeConstraint,
    itemDefinition,
    isCollectionConstraintEnable: true, // typeConstraint enables to add a constraint to the collection itself
  });

  const onConstraintChange = useCallback(
    (value?: string) => {
      editItemDefinition(itemDefinitionId!, (itemDefinition) => {
        itemDefinition.typeConstraint ??= { text: { __$$text: "" } };
        itemDefinition.typeConstraint.text.__$$text = value ?? "";
        itemDefinition.typeConstraint["@_id"] = itemDefinition.typeConstraint?.["@_id"] ?? generateUuid();
      });
    },
    [editItemDefinition, itemDefinitionId]
  );

  const onToggleGroupChange = useCallback(
    (newSelection: boolean, event: React.KeyboardEvent<Element> | MouseEvent | React.MouseEvent<any, MouseEvent>) => {
      if (!newSelection) {
        return;
      }
      const selection = event.currentTarget.id as ConstraintsType;
      if (selection === ConstraintsType.NONE) {
        editItemDefinition(itemDefinitionId!, (itemDefinition) => {
          itemDefinition.typeConstraint = undefined;
        });
        return;
      }

      editItemDefinition(itemDefinitionId!, (itemDefinition) => {
        itemDefinition.typeConstraint ??= { text: { __$$text: "" } };
        const previousKieContraintType = itemDefinition.typeConstraint["@_kie:constraintType"];
        itemDefinition.typeConstraint["@_kie:constraintType"] = enumToKieConstraintType(selection);

        if (selection === ConstraintsType.EXPRESSION) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.ENUMERATION &&
          isEnum(
            itemDefinition.typeConstraint.text.__$$text,
            constraintTypeHelper(
              (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
            ).check
          )
        ) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.RANGE &&
          isRange(
            itemDefinition.typeConstraint.text.__$$text,
            constraintTypeHelper(
              (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
            ).check
          )
        ) {
          return;
        }

        itemDefinition.typeConstraint.text.__$$text = "";
      });
    },
    [editItemDefinition, enumToKieConstraintType, itemDefinitionId]
  );

  return (
    <Constraints
      isReadonly={isReadonly}
      itemDefinition={itemDefinition}
      constraintValue={constraintValue}
      typeRef={typeRef}
      isConstraintEnum={isConstraintEnum}
      isConstraintRange={isConstraintRange}
      isConstraintEnabled={isConstraintEnabled}
      selectedConstraint={selectedConstraint}
      onToggleGroupChange={onToggleGroupChange}
      onConstraintChange={onConstraintChange}
      renderOnPropertiesPanel={renderOnPropertiesPanel}
    />
  );
}

export function Constraints({
  isReadonly,
  itemDefinition,
  constraintValue,
  typeRef,
  isConstraintEnum,
  isConstraintRange,
  isConstraintEnabled,
  selectedConstraint,
  onToggleGroupChange,
  onConstraintChange,
  renderOnPropertiesPanel,
}: {
  isReadonly: boolean;
  itemDefinition: DMN15__tItemDefinition;
  constraintValue: string | undefined;
  typeRef: DmnBuiltInDataType;
  isConstraintEnum: string[] | undefined;
  isConstraintRange: [string, string] | undefined;
  isConstraintEnabled: {
    enumeration: boolean;
    range: boolean;
    expression: boolean;
  };
  selectedConstraint: ConstraintsType;
  renderOnPropertiesPanel?: boolean;
  onToggleGroupChange: (
    selected: boolean,
    event: React.KeyboardEvent<Element> | MouseEvent | React.MouseEvent<any, MouseEvent>
  ) => void;
  onConstraintChange: (value?: string) => void;
}) {
  return (
    <>
      {isConstraintEnabled.expression === false &&
      isConstraintEnabled.enumeration === false &&
      isConstraintEnabled.range === false ? (
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
                // The default ToggleGroupItem zIndex is bigger than the
                // the Monaco suggestion zIndex. This causes the button
                // to be on top of the Monaco suggestion. The 10
                // is an arbirtrary value, which solves the issue.
                style={{ zIndex: 10 }}
                text={ConstraintsType.EXPRESSION}
                buttonId={ConstraintsType.EXPRESSION}
                isSelected={selectedConstraint === ConstraintsType.EXPRESSION}
                onChange={onToggleGroupChange}
                isDisabled={isReadonly || !isConstraintEnabled.expression}
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

          <div style={{ paddingTop: "20px" }}>
            {selectedConstraint === ConstraintsType.ENUMERATION && (
              <ConstraintsEnum
                isReadonly={isReadonly}
                type={typeRef}
                typeHelper={constraintTypeHelper(typeRef)}
                value={isConstraintEnum ? constraintValue : undefined}
                expressionValue={constraintValue}
                onSave={onConstraintChange}
                isDisabled={!isConstraintEnabled.enumeration}
                renderOnPropertiesPanel={renderOnPropertiesPanel}
              />
            )}
            {selectedConstraint === ConstraintsType.RANGE && (
              <ConstraintsRange
                isReadonly={isReadonly}
                expressionValue={constraintValue}
                type={typeRef}
                typeHelper={constraintTypeHelper(typeRef)}
                value={isConstraintRange ? constraintValue : undefined}
                onSave={onConstraintChange}
                isDisabled={!isConstraintEnabled.range}
                renderOnPropertiesPanel={renderOnPropertiesPanel}
              />
            )}
            {selectedConstraint === ConstraintsType.EXPRESSION && (
              <ConstraintsExpression
                isReadonly={isReadonly}
                type={typeRef}
                value={constraintValue}
                savedValue={constraintValue}
                onSave={onConstraintChange}
                isDisabled={!isConstraintEnabled.expression}
              />
            )}
            {selectedConstraint === ConstraintsType.NONE && (
              <p
                style={{
                  padding: "24px",
                  background: "#eee",
                  borderRadius: "10px",
                  textAlign: "center",
                }}
              >
                {`All values are allowed`}
              </p>
            )}
          </div>
        </div>
      )}
    </>
  );
}
