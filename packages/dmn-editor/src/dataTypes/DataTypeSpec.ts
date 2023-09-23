import { generateUuid, DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import {
  DMN15__tDefinitions,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { SPEC } from "../Spec";
import { DataTypesById } from "./DataTypes";

export function findDataTypeById({
  definitions,
  dataTypeId,
  dataTypesById,
}: {
  dataTypesById: DataTypesById;
  dataTypeId: string;
  definitions: DMN15__tDefinitions;
}) {
  const indexesPath: number[] = [];
  let current = dataTypesById.get(dataTypeId);
  do {
    indexesPath.unshift(current!.index);
    current = current!.parentId ? dataTypesById.get(current!.parentId) : undefined;
  } while (current);

  const last = indexesPath.pop()!; // Since we're using do-while, it's guaranteed we'll have at least one element on the `indexesPath` array.

  definitions.itemDefinition ??= [];
  let items = definitions.itemDefinition;
  for (const i of indexesPath) {
    items = items![i].itemComponent!;
  }
  const itemDefinition = items![last];
  return { items, itemDefinition, index: last };
}

export function getNewItemDefinition(partial?: Partial<DMN15__tItemDefinition>) {
  return {
    "@_id": generateUuid(),
    "@_name": "New data type",
    "@_isCollection": false,
    "@_typeLanguage": SPEC.typeLanguage.default,
    typeRef: DmnBuiltInDataType.Any,
    ...(partial ?? {}),
  };
}

export function isStruct(itemDefinition: DMN15__tItemDefinition) {
  return !itemDefinition.typeRef && !!itemDefinition.itemComponent;
}

const constrainableBuiltInDataTypes = new Map<DmnBuiltInDataType, string[]>([
  [DmnBuiltInDataType.Any, ["expression"]],
  [DmnBuiltInDataType.Boolean, []],
  [DmnBuiltInDataType.Context, []],
  [DmnBuiltInDataType.Number, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.String, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.DateTimeDuration, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.YearsMonthsDuration, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.Date, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.Time, ["expression", "enum", "range"]],
  [DmnBuiltInDataType.DateTime, ["expression", "enum", "range"]],
]);
export function canHaveConstraints(itemDefinition: DMN15__tItemDefinition) {
  return (
    !isStruct(itemDefinition) &&
    (constrainableBuiltInDataTypes.get(itemDefinition.typeRef as DmnBuiltInDataType)?.length ?? 0) > 0
  );
}

export function reassignIds<O extends { "@_id"?: string }, T extends keyof O>(obj: O, prop: T): O {
  obj = { ...obj, "@_id": generateUuid() };

  if (obj[prop]) {
    const newArr = [];
    for (const nested of obj[prop] as O[]) {
      newArr.push(reassignIds(nested, prop));
    }
    (obj[prop] as O[]) = newArr;
  }

  return obj;
}
