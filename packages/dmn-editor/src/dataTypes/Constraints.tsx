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
import { useCallback, useEffect, useMemo, useState } from "react";
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
import { constrainableBuiltInFeelTypes, isCollection, isStruct } from "./DataTypeSpec";
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
import { builtInFeelTypeNames } from "./BuiltInFeelTypes";
import { Normalized } from "../normalization/normalize";

export type TypeHelper = {
  check: (value: string) => boolean;
  parse: (value: string) => any;
  transform: (value: string) => string;
  recover: (value?: string) => string | undefined;
  component: (props: ConstraintProps) => React.ReactNode | undefined;
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

// Recurse the `itemDefinition` until find `typeRef` attribute
// that is part of the built in FEEL types.
// If the found `itemDefinition` is a collection, it will have a early stop.
export function recursivelyGetRootItemDefinition(
  itemDefinition: Normalized<DMN15__tItemDefinition>,
  allDataTypesById: DataTypeIndex,
  allTopLevelItemDefinitionUniqueNames: UniqueNameIndex
): Normalized<DMN15__tItemDefinition> {
  const typeRef: DmnBuiltInDataType = itemDefinition.typeRef?.__$$text as DmnBuiltInDataType;

  if (builtInFeelTypeNames.has(typeRef) === false) {
    const parentDataType = allDataTypesById.get(allTopLevelItemDefinitionUniqueNames.get(typeRef) ?? "");
    if (parentDataType !== undefined && isCollection(parentDataType.itemDefinition)) {
      // Parent `itemDefinition` is a collection. Early stop.
      return parentDataType.itemDefinition;
    } else if (parentDataType !== undefined) {
      return recursivelyGetRootItemDefinition(
        parentDataType.itemDefinition,
        allDataTypesById,
        allTopLevelItemDefinitionUniqueNames
      );
    }
    // Something wrong. Caller `itemDefinition` isn't a built-in FEEL type and doesn't have parent.
    return itemDefinition;
  }
  // Caller `itemDefinition` is a built-in FEEL type
  return itemDefinition;
}

export const constraintTypeHelper = (
  itemDefinition: Normalized<DMN15__tItemDefinition>,
  allDataTypesById?: DataTypeIndex,
  allTopLevelItemDefinitionUniqueNames?: UniqueNameIndex
): TypeHelper => {
  const typeRef =
    (allDataTypesById !== undefined && allTopLevelItemDefinitionUniqueNames !== undefined
      ? recursivelyGetRootItemDefinition(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames).typeRef
          ?.__$$text
      : itemDefinition.typeRef?.__$$text) ?? DmnBuiltInDataType.Undefined;

  const typeHelper = {
    // check if the value has the correct type
    check: (value: string, type?: DmnBuiltInDataType) => {
      const recoveredValue = typeHelper.recover(value);
      switch (type ?? typeRef) {
        case DmnBuiltInDataType.Any:
          return true;
        case DmnBuiltInDataType.String:
          if (recoveredValue === "") {
            return true;
          }
          if (typeHelper.check(value, DmnBuiltInDataType.Date)) {
            return false;
          }
          if (typeHelper.check(value, DmnBuiltInDataType.DateTime)) {
            return false;
          }
          if (typeHelper.check(value, DmnBuiltInDataType.DateTimeDuration)) {
            return false;
          }
          if (typeHelper.check(value, DmnBuiltInDataType.Time)) {
            return false;
          }
          if (typeHelper.check(value, DmnBuiltInDataType.YearsMonthsDuration)) {
            return false;
          }
          return typeof recoveredValue === "string";
        case DmnBuiltInDataType.Date:
          return moment(recoveredValue, "YYYY-MM-DD", true).isValid() || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.DateTime:
          return (
            moment(recoveredValue, "YYYY-MM-DDTHH:mm:ssZZ", true).isValid() ||
            moment(recoveredValue, "YYYY-MM-DDTHH:mmZZ", true).isValid() ||
            moment(recoveredValue, "YYYY-MM-DD", true).isValid() ||
            value === "" ||
            recoveredValue === ""
          );
        case DmnBuiltInDataType.DateTimeDuration:
          return REGEX_DATE_TIME_DURATION.test(recoveredValue ?? "") || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.Number:
          return !isNaN(parseFloat(recoveredValue ?? "")) || value === "" || recoveredValue === "";
        case DmnBuiltInDataType.Time:
          return (
            moment(recoveredValue, "HH:mmZZ", true).isValid() ||
            moment(recoveredValue, "HH:mm:ssZZ", true).isValid() ||
            value === "" ||
            recoveredValue === ""
          );
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
  constraint: Normalized<DMN15__tUnaryTests> | undefined;
  itemDefinition: Normalized<DMN15__tItemDefinition>;
  isCollectionConstraintEnabled: boolean;
  constraintTypeHelper: TypeHelper;
  enabledConstraints: KIE__tConstraintType[] | undefined;
}) {
  const constraintValue = constraint?.text.__$$text;
  const kieConstraintType = constraint?.["@_kie:constraintType"];

  const isConstraintEnum = useMemo(
    () =>
      isCollection(itemDefinition) === true && isCollectionConstraintEnabled === true // collection doesn't support enumeration constraint
        ? undefined
        : isEnum(constraintValue, constraintTypeHelper.check),
    [constraintTypeHelper.check, constraintValue, isCollectionConstraintEnabled, itemDefinition]
  );

  const isConstraintRange = useMemo(
    () =>
      isCollection(itemDefinition) === true && isCollectionConstraintEnabled === true // collection doesn't support range constraint
        ? undefined
        : isRange(constraintValue, constraintTypeHelper.check),
    [constraintTypeHelper.check, constraintValue, isCollectionConstraintEnabled, itemDefinition]
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
        !(isCollection(itemDefinition) === true && isCollectionConstraintEnabled === true) &&
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.ENUMERATION)!),
      range:
        !(isCollection(itemDefinition) === true && isCollectionConstraintEnabled === true) &&
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.RANGE)!),
      expression:
        (isCollection(itemDefinition) === true && isCollectionConstraintEnabled === true) ||
        (enabledConstraints ?? []).includes(enumToKieConstraintType(ConstraintsType.EXPRESSION)!),
    };
  }, [enabledConstraints, enumToKieConstraintType, isCollectionConstraintEnabled, itemDefinition]);

  const selectedKieConstraintType = useMemo(() => {
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
    selectedKieConstraintType,
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
  itemDefinition: Normalized<DMN15__tItemDefinition>;
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
  const itemDefinitionId = itemDefinition["@_id"]!;
  const typeRef = itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType;
  const typeRefConstraintTypeHelper = useMemo(
    () => constraintTypeHelper(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames),
    [allDataTypesById, allTopLevelItemDefinitionUniqueNames, itemDefinition]
  );

  const rootItemDefinition = useMemo(
    () => recursivelyGetRootItemDefinition(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames),
    [allDataTypesById, allTopLevelItemDefinitionUniqueNames, itemDefinition]
  );

  const enabledConstraints = useMemo(
    () =>
      isStruct(rootItemDefinition)
        ? (["expression"] as KIE__tConstraintType[])
        : constrainableBuiltInFeelTypes.get(
            (rootItemDefinition.typeRef?.__$$text as DmnBuiltInDataType) ?? DmnBuiltInDataType.Undefined
          ),
    [rootItemDefinition]
  );

  // Collection constraint on the `allowedValues` must be enabled on cases where `rootItemDefinition` is a collection
  const isCollectionConstraintEnable = useMemo(() => {
    if (itemDefinitionId !== rootItemDefinition["@_id"]) {
      return rootItemDefinition["@_isCollection"] ?? false;
    }
    return false;
  }, [itemDefinitionId, rootItemDefinition]);

  const {
    constraintValue,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    selectedKieConstraintType,
    enumToKieConstraintType,
  } = useConstraint({
    constraint: allowedValues,
    itemDefinition,
    isCollectionConstraintEnabled: isCollectionConstraintEnable,
    constraintTypeHelper: typeRefConstraintTypeHelper,
    enabledConstraints,
  });

  const onConstraintChange = useCallback(
    (value: string | undefined, selectedConstraint: ConstraintsType) => {
      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        if (value === "" || value === undefined) {
          itemDefinition.allowedValues = undefined;
        } else {
          itemDefinition.allowedValues ??= { "@_id": generateUuid(), text: { __$$text: "" } };
          itemDefinition.allowedValues.text.__$$text = value;
          itemDefinition.allowedValues["@_id"] = itemDefinition.allowedValues?.["@_id"] ?? generateUuid();
          itemDefinition.allowedValues["@_kie:constraintType"] = enumToKieConstraintType(selectedConstraint);
        }
      });
    },
    [editItemDefinition, enumToKieConstraintType, itemDefinitionId]
  );

  const onToggleGroupChange = useCallback(
    (newSelection: boolean, selectedConstraint: ConstraintsType) => {
      if (!newSelection) {
        return;
      }

      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        if (selectedConstraint === ConstraintsType.NONE) {
          itemDefinition.allowedValues = undefined;
          return;
        }

        if (itemDefinition.allowedValues) {
          itemDefinition.allowedValues["@_kie:constraintType"] = enumToKieConstraintType(selectedConstraint);
        }

        if (selectedConstraint === ConstraintsType.EXPRESSION) {
          return;
        }

        if (
          selectedConstraint === ConstraintsType.ENUMERATION &&
          isEnum(itemDefinition.allowedValues?.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        if (
          selectedConstraint === ConstraintsType.RANGE &&
          isRange(itemDefinition.allowedValues?.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        itemDefinition.allowedValues = undefined;
      });
    },
    [editItemDefinition, itemDefinitionId, enumToKieConstraintType, typeRefConstraintTypeHelper.check]
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
      selectedKieConstraintType={selectedKieConstraintType}
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
  itemDefinition: Normalized<DMN15__tItemDefinition>;
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
  const itemDefinitionId = itemDefinition["@_id"]!;
  const typeConstraint = useMemo(
    () =>
      defaultsToAllowedValues
        ? itemDefinition?.typeConstraint ?? itemDefinition?.allowedValues
        : itemDefinition?.typeConstraint,
    [defaultsToAllowedValues, itemDefinition?.allowedValues, itemDefinition?.typeConstraint]
  );

  const typeRef = itemDefinition?.typeRef?.__$$text as DmnBuiltInDataType;
  const typeRefConstraintTypeHelper = useMemo(
    () => constraintTypeHelper(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames),
    [allDataTypesById, allTopLevelItemDefinitionUniqueNames, itemDefinition]
  );

  const rootItemDefinition = useMemo(
    () => recursivelyGetRootItemDefinition(itemDefinition, allDataTypesById, allTopLevelItemDefinitionUniqueNames),
    [allDataTypesById, allTopLevelItemDefinitionUniqueNames, itemDefinition]
  );

  const enabledConstraints = useMemo(() => {
    if (isStruct(rootItemDefinition)) {
      return ["expression"] as KIE__tConstraintType[];
    }
    if (isCollection(rootItemDefinition)) {
      return ["expression"] as KIE__tConstraintType[];
    }
    return constrainableBuiltInFeelTypes.get(rootItemDefinition.typeRef?.__$$text as DmnBuiltInDataType);
  }, [rootItemDefinition]);

  const {
    constraintValue,
    isConstraintEnum,
    isConstraintRange,
    isConstraintEnabled,
    selectedKieConstraintType,
    enumToKieConstraintType,
  } = useConstraint({
    constraint: typeConstraint,
    itemDefinition: itemDefinition,
    isCollectionConstraintEnabled: true, // typeConstraint enables to add a constraint to the collection itself
    constraintTypeHelper: typeRefConstraintTypeHelper,
    enabledConstraints,
  });

  const onConstraintChange = useCallback(
    (value: string | undefined, selectedConstraint: ConstraintsType) => {
      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        if (value === "" || value === undefined) {
          itemDefinition.typeConstraint = undefined;
        } else {
          itemDefinition.typeConstraint ??= { "@_id": generateUuid(), text: { __$$text: "" } };
          itemDefinition.typeConstraint.text.__$$text = value;
          itemDefinition.typeConstraint["@_id"] = itemDefinition.typeConstraint?.["@_id"] ?? generateUuid();
          itemDefinition.typeConstraint["@_kie:constraintType"] = enumToKieConstraintType(selectedConstraint);
        }
      });
    },
    [editItemDefinition, enumToKieConstraintType, itemDefinitionId]
  );

  const onToggleGroupChange = useCallback(
    (newSelection: boolean, selectedConstraint: ConstraintsType) => {
      if (!newSelection) {
        return;
      }

      editItemDefinition(itemDefinitionId, (itemDefinition) => {
        if (selectedConstraint === ConstraintsType.NONE) {
          itemDefinition.typeConstraint = undefined;
          return;
        }

        if (!itemDefinition.typeConstraint && itemDefinition.allowedValues) {
          itemDefinition.typeConstraint = itemDefinition.allowedValues;
          itemDefinition.allowedValues = undefined;
        }

        if (itemDefinition.typeConstraint) {
          itemDefinition.typeConstraint["@_kie:constraintType"] = enumToKieConstraintType(selectedConstraint);
        }

        if (selectedConstraint === ConstraintsType.EXPRESSION) {
          return;
        }

        if (
          selectedConstraint === ConstraintsType.ENUMERATION &&
          isEnum(itemDefinition.typeConstraint?.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        if (
          selectedConstraint === ConstraintsType.RANGE &&
          isRange(itemDefinition.typeConstraint?.text.__$$text, typeRefConstraintTypeHelper.check)
        ) {
          return;
        }

        itemDefinition.typeConstraint = undefined;
      });
    },
    [editItemDefinition, itemDefinitionId, enumToKieConstraintType, typeRefConstraintTypeHelper.check]
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
      selectedKieConstraintType={selectedKieConstraintType}
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
  selectedKieConstraintType,
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
  selectedKieConstraintType: ConstraintsType;
  renderOnPropertiesPanel?: boolean;
  onToggleGroupChange: (selected: boolean, selectedConstraint: ConstraintsType) => void;
  onConstraintChange: (value: string | undefined, selectedConstraint: ConstraintsType) => void;
}) {
  const [internalSelectedConstraint, setInternalSelectedConstraint] = useState<{
    selectedConstraint: ConstraintsType;
    itemDefinitionId: string;
  }>({ selectedConstraint: selectedKieConstraintType, itemDefinitionId });

  // Updates the `selectedConstraint` only after changing the active item definition
  // Both `internalSelectedConstraint` and `selectedKieConstraintType` should not be coupled together
  useEffect(() => {
    setInternalSelectedConstraint((prev) => {
      if (selectedKieConstraintType === ConstraintsType.NONE && prev.itemDefinitionId === itemDefinitionId) {
        return prev;
      }
      return { selectedConstraint: selectedKieConstraintType, itemDefinitionId };
    });
  }, [itemDefinitionId, selectedKieConstraintType]);

  const onToggleGroupChangeInternal = useCallback(
    (selected: boolean, event: React.KeyboardEvent<Element> | MouseEvent | React.MouseEvent<any, MouseEvent>) => {
      const selectedConstraint = event.currentTarget.id as ConstraintsType;
      setInternalSelectedConstraint((prev) => ({ selectedConstraint, itemDefinitionId: prev.itemDefinitionId }));
      onToggleGroupChange(selected, selectedConstraint);
    },
    [onToggleGroupChange]
  );

  const onConstraintChangeInternal = useCallback(
    (value: string | undefined) => {
      if (constraintValue !== value) {
        onConstraintChange(value, internalSelectedConstraint.selectedConstraint);
      }
    },
    [onConstraintChange, internalSelectedConstraint, constraintValue]
  );

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
                isSelected={internalSelectedConstraint.selectedConstraint === ConstraintsType.NONE}
                onChange={onToggleGroupChangeInternal}
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
                isSelected={internalSelectedConstraint.selectedConstraint === ConstraintsType.EXPRESSION}
                onChange={onToggleGroupChangeInternal}
                isDisabled={isReadonly || !isConstraintEnabled.expression}
              />
              <ToggleGroupItem
                text={ConstraintsType.ENUMERATION}
                buttonId={ConstraintsType.ENUMERATION}
                isSelected={internalSelectedConstraint.selectedConstraint === ConstraintsType.ENUMERATION}
                onChange={onToggleGroupChangeInternal}
                isDisabled={isReadonly || !isConstraintEnabled.enumeration}
              />
              <ToggleGroupItem
                text={ConstraintsType.RANGE}
                buttonId={ConstraintsType.RANGE}
                isSelected={internalSelectedConstraint.selectedConstraint === ConstraintsType.RANGE}
                onChange={onToggleGroupChangeInternal}
                isDisabled={isReadonly || !isConstraintEnabled.range}
              />
            </ToggleGroup>
          </div>

          <div style={{ paddingTop: "20px" }}>
            {internalSelectedConstraint.selectedConstraint === ConstraintsType.ENUMERATION && (
              <ConstraintsEnum
                id={itemDefinitionId}
                isReadonly={isReadonly}
                type={typeRef}
                typeHelper={typeHelper}
                value={isConstraintEnum ? constraintValue : undefined}
                expressionValue={constraintValue}
                onSave={onConstraintChangeInternal}
                isDisabled={!isConstraintEnabled.enumeration}
                renderOnPropertiesPanel={renderOnPropertiesPanel}
              />
            )}
            {internalSelectedConstraint.selectedConstraint === ConstraintsType.RANGE && (
              <ConstraintsRange
                id={itemDefinitionId}
                isReadonly={isReadonly}
                expressionValue={constraintValue}
                type={typeRef}
                typeHelper={typeHelper}
                value={isConstraintRange ? constraintValue : undefined}
                onSave={onConstraintChangeInternal}
                isDisabled={!isConstraintEnabled.range}
                renderOnPropertiesPanel={renderOnPropertiesPanel}
              />
            )}
            {internalSelectedConstraint.selectedConstraint === ConstraintsType.EXPRESSION && (
              <ConstraintsExpression
                id={itemDefinitionId}
                isReadonly={isReadonly}
                type={typeRef}
                value={constraintValue}
                savedValue={constraintValue}
                onSave={onConstraintChangeInternal}
                isDisabled={!isConstraintEnabled.expression}
              />
            )}
            {internalSelectedConstraint.selectedConstraint === ConstraintsType.NONE && (
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
