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
import { DataTypeIndex, EditItemDefinition } from "./DataTypes";
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
import { useDmnEditorStore } from "../store/StoreContext";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { UniqueNameIndex } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";

export type TypeHelper = {
  check: (value: string) => boolean;
  parse: (value: string) => any;
  transform: (value: string) => string;
  recover: (value?: string) => string | undefined;
  component: (props: any) => React.ReactNode | undefined;
};

export interface ConstraintComponentProps {
  id: string;
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

export function recursevelyGetDmnBuiltInDataType(
  itemDefinition: DMN15__tItemDefinition,
  allDataTypesById: DataTypeIndex,
  allTopLevelItemDefinitionUniqueNames: UniqueNameIndex
): DMN15__tItemDefinition {
  const enabledConstraints = constrainableBuiltInFeelTypes.get(
    (itemDefinition.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
  );
  if (enabledConstraints === undefined) {
    const parentId = allTopLevelItemDefinitionUniqueNames.get(
      (itemDefinition.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
    );
    const parentType = allDataTypesById.get(parentId ?? "");
    if (parentType !== undefined) {
      return parentType.itemDefinition.typeRef?.__$$text
        ? recursevelyGetDmnBuiltInDataType(
            parentType.itemDefinition,
            allDataTypesById,
            allTopLevelItemDefinitionUniqueNames
          )
        : itemDefinition;
    }
    return itemDefinition;
  }
  return itemDefinition;
}

export const constraintTypeHelper = (
  itemDefinition: DMN15__tItemDefinition,
  allDataTypesById?: DataTypeIndex,
  allTopLevelItemDefinitionUniqueNames?: UniqueNameIndex
): TypeHelper => {
  const typeRef =
    allDataTypesById !== undefined && allTopLevelItemDefinitionUniqueNames !== undefined
      ? recursevelyGetDmnBuiltInDataType(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames)
      : itemDefinition.typeRef?.__$$text;

  const typeHelper = {
    // check if the value has the correct type
    check: (value: string) => {
      const recoveredValue = typeHelper.recover(value);
      switch (typeRef) {
        case DmnBuiltInDataType.Any:
          return true;
        case DmnBuiltInDataType.String:
          if (recoveredValue === "") {
            return true;
          }
          if (typeHelper.check(value)) {
            return false;
          }
          if (typeHelper.check(value)) {
            return false;
          }
          if (typeHelper.check(value)) {
            return false;
          }
          if (typeHelper.check(value)) {
            return false;
          }
          if (typeHelper.check(value)) {
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
      const recoveredValue = typeHelper.recover(value);
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
    recover: (value: string | undefined) => {
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
  return typeHelper;
};

export function useConstraint({
  constraint,
  itemDefinition,
  isCollectionConstraintEnabled,
  constraintTypeHelper,
  enabledConstraints,
}: {
  constraint: DMN15__tUnaryTests | undefined;
  itemDefinition: DMN15__tItemDefinition;
  isCollectionConstraintEnabled: boolean;
  constraintTypeHelper: TypeHelper;
  enabledConstraints: KIE__tConstraintType[] | undefined;
}) {
  const constraintValue = constraint?.text.__$$text;
  const kieConstraintType = constraint?.["@_kie:constraintType"];
  const isCollection = itemDefinition["@_isCollection"];

  const isConstraintEnum = useMemo(
    () =>
      isCollection === true && isCollectionConstraintEnabled === true // collection doesn't support enumeration constraint
        ? undefined
        : isEnum(constraintValue, constraintTypeHelper.check),
    [constraintTypeHelper.check, constraintValue, isCollection, isCollectionConstraintEnabled]
  );

  const isConstraintRange = useMemo(
    () =>
      isCollection === true && isCollectionConstraintEnabled === true // collection doesn't support range constraint
        ? undefined
        : isRange(constraintValue, constraintTypeHelper.check),
    [constraintTypeHelper, constraintValue, isCollection, isCollectionConstraintEnabled]
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
    return {
      enumeration:
        !(isCollection === true && isCollectionConstraintEnabled === true) &&
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range:
        !(isCollection === true && isCollectionConstraintEnabled === true) &&
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression:
        (isCollection === true && isCollectionConstraintEnabled === true) ||
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [enabledConstraints, enumToKieConstraintType, isCollection, isCollectionConstraintEnabled]);

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

  return {
    constraintValue,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    selectedConstraint,
    enumToKieConstraintType,
  };
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
  const { externalModelsByNamespace } = useExternalModels();
  const allDataTypesById = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allDataTypesById
  );
  const allTopLevelItemDefinitionUniqueNames = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelItemDefinitionUniqueNames
  );

  const allowedValues = useMemo(() => itemDefinition?.allowedValues, [itemDefinition?.allowedValues]);

  const typeRef = (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined;
  const typeRefConstraintTypeHelper = constraintTypeHelper(
    itemDefinition,
    allDataTypesById,
    allTopLevelItemDefinitionUniqueNames
  );

  const rootItemDefinition = recursevelyGetDmnBuiltInDataType(
    itemDefinition,
    allDataTypesById,
    allTopLevelItemDefinitionUniqueNames
  );

  const enabledConstraints = constrainableBuiltInFeelTypes.get(
    (rootItemDefinition.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
  );
  const {
    constraintValue,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    selectedConstraint,
    enumToKieConstraintType,
  } = useConstraint({
    constraint: allowedValues,
    itemDefinition,
    isCollectionConstraintEnabled:
      itemDefinition["@_id"] !== rootItemDefinition["@_id"] ? rootItemDefinition["@_isCollection"] ?? false : false, // allowedValues doesn't support constraint to the collection itself
    constraintTypeHelper: typeRefConstraintTypeHelper,
    enabledConstraints,
  });

  const itemDefinitionId = itemDefinition["@_id"]!;

  const onConstraintChange = useCallback(
    (value?: string) => {
      editItemDefinition(itemDefinitionId, (itemDefinition) => {
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
        editItemDefinition(itemDefinitionId, (itemDefinition) => {
          itemDefinition.allowedValues = undefined;
        });
        return;
      }

      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        itemDefinition.allowedValues ??= { text: { __$$text: "" } };
        const previousKieContraintType = itemDefinition.allowedValues["@_kie:constraintType"];
        itemDefinition.allowedValues["@_kie:constraintType"] = enumToKieConstraintType(selection);

        if (selection === ConstraintsType.EXPRESSION) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.ENUMERATION &&
          isEnum(itemDefinition.allowedValues.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.RANGE &&
          isRange(itemDefinition.allowedValues.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        itemDefinition.allowedValues.text.__$$text = "";
        return;
      });
    },
    [editItemDefinition, enumToKieConstraintType, itemDefinitionId, typeRefConstraintTypeHelper.check]
  );

  return (
    <Constraints
      isReadonly={isReadonly}
      itemDefinitionId={itemDefinitionId}
      constraintValue={constraintValue}
      typeHelper={typeRefConstraintTypeHelper}
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
  const { externalModelsByNamespace } = useExternalModels();
  const allDataTypesById = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allDataTypesById
  );
  const allTopLevelItemDefinitionUniqueNames = useDmnEditorStore(
    (s) => s.computed(s).getDataTypes(externalModelsByNamespace).allTopLevelItemDefinitionUniqueNames
  );

  const typeConstraint = useMemo(
    () =>
      defaultsToAllowedValues
        ? itemDefinition?.typeConstraint ?? itemDefinition?.allowedValues
        : itemDefinition?.typeConstraint,
    [defaultsToAllowedValues, itemDefinition?.allowedValues, itemDefinition?.typeConstraint]
  );

  const typeRef = (itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined;
  const typeRefConstraintTypeHelper = constraintTypeHelper(
    itemDefinition,
    allDataTypesById,
    allTopLevelItemDefinitionUniqueNames
  );

  const enabledConstraints = constrainableBuiltInFeelTypes.get(
    recursevelyGetDmnBuiltInDataType(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames).typeRef
      ?.__$$text as DmnBuiltInDataType as DmnBuiltInDataType.Undefined
  );

  const {
    constraintValue,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    selectedConstraint,
    enumToKieConstraintType,
  } = useConstraint({
    constraint: typeConstraint,
    itemDefinition: itemDefinition,
    isCollectionConstraintEnabled: true, // typeConstraint enables to add a constraint to the collection itself
    constraintTypeHelper: typeRefConstraintTypeHelper,
    enabledConstraints,
  });

  const itemDefinitionId = itemDefinition["@_id"]!;

  const onConstraintChange = useCallback(
    (value: string | undefined) => {
      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        itemDefinition.typeConstraint ??= { text: { __$$text: "" } };
        itemDefinition.typeConstraint.text.__$$text = value ?? "";
        itemDefinition.typeConstraint["@_id"] = itemDefinition.typeConstraint?.["@_id"] ?? generateUuid();
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
        editItemDefinition(itemDefinitionId, (itemDefinition) => {
          itemDefinition.typeConstraint = undefined;
        });
        return;
      }

      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        itemDefinition.typeConstraint ??= { text: { __$$text: "" } };
        const previousKieContraintType = itemDefinition.typeConstraint["@_kie:constraintType"];
        itemDefinition.typeConstraint["@_kie:constraintType"] = enumToKieConstraintType(selection);

        if (selection === ConstraintsType.EXPRESSION) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.ENUMERATION &&
          isEnum(itemDefinition.typeConstraint.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        if (
          previousKieContraintType === "expression" &&
          selection === ConstraintsType.RANGE &&
          isRange(itemDefinition.typeConstraint.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        itemDefinition.typeConstraint.text.__$$text = "";
      });
    },
    [editItemDefinition, enumToKieConstraintType, itemDefinitionId, typeRefConstraintTypeHelper.check]
  );

  return (
    <Constraints
      isReadonly={isReadonly}
      itemDefinitionId={itemDefinitionId}
      constraintValue={constraintValue}
      typeHelper={typeRefConstraintTypeHelper}
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
  itemDefinitionId,
  constraintValue,
  typeHelper,
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
  itemDefinitionId: string;
  constraintValue: string | undefined;
  typeHelper: TypeHelper;
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
                id={itemDefinitionId}
                isReadonly={isReadonly}
                type={typeRef}
                typeHelper={typeHelper}
                value={isConstraintEnum ? constraintValue : undefined}
                expressionValue={constraintValue}
                onSave={onConstraintChange}
                isDisabled={!isConstraintEnabled.enumeration}
                renderOnPropertiesPanel={renderOnPropertiesPanel}
              />
            )}
            {selectedConstraint === ConstraintsType.RANGE && (
              <ConstraintsRange
                id={itemDefinitionId}
                isReadonly={isReadonly}
                expressionValue={constraintValue}
                type={typeRef}
                typeHelper={typeHelper}
                value={isConstraintRange ? constraintValue : undefined}
                onSave={onConstraintChange}
                isDisabled={!isConstraintEnabled.range}
                renderOnPropertiesPanel={renderOnPropertiesPanel}
              />
            )}
            {selectedConstraint === ConstraintsType.EXPRESSION && (
              <ConstraintsExpression
                id={itemDefinitionId}
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
