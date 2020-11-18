/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import {
  CompoundPredicate,
  DataField,
  DataType,
  False,
  FieldName,
  Predicate,
  SimplePredicate,
  SimplePredicateOperator,
  SimpleSetPredicate,
  True
} from "@kogito-tooling/pmml-editor-marshaller";

const SimplePredicateOperatorMap: Map<SimplePredicateOperator, string> = new Map<SimplePredicateOperator, string>([
  ["equal", "="],
  ["notEqual", "<>"],
  ["lessThan", "<"],
  ["lessOrEqual", "<="],
  ["greaterThan", ">"],
  ["greaterOrEqual", ">="],
  ["isMissing", "isMissing"],
  ["isNotMissing", "isNotMissing"]
]);

export const toText = (predicate: Predicate | undefined, fields: DataField[]): string => {
  const fieldToDataType: Map<FieldName, DataType> = new Map(fields.map(field => [field.name, field.dataType]));
  return predicate ? _toText(predicate, fieldToDataType, 0) : "";
};

const _toText = (predicate: Predicate, fieldToDataType: Map<FieldName, DataType>, nesting: number): string => {
  if (predicate instanceof True) {
    return "True";
  } else if (predicate instanceof False) {
    return "False";
  } else if (predicate instanceof SimpleSetPredicate) {
    const ssp: SimpleSetPredicate = predicate as SimpleSetPredicate;
    return `${ssp.field.toString()} ${ssp.booleanOperator} ${ssp.Array.toString()} `;
  } else if (predicate instanceof SimplePredicate) {
    const sp: SimplePredicate = predicate as SimplePredicate;
    return `${sp.field.toString()} ${SimplePredicateOperatorMap.get(sp.operator)} ${_value(
      sp.field,
      sp.value,
      fieldToDataType
    )}`;
  } else if (predicate instanceof CompoundPredicate) {
    const cp: CompoundPredicate = predicate as CompoundPredicate;
    let text: string = "";
    let children: string[] = [];
    text = text + (nesting > 0 ? "( " : "");
    cp.predicates?.forEach(p => children.push(_toText(p, fieldToDataType, nesting + 1)));
    text = text + children.join(" " + cp.booleanOperator + " ");
    text = text + (nesting > 0 ? ")" : "");
    return text;
  }
  return "";
};

const _value = (field: FieldName, value: any, fieldToDataType: Map<FieldName, DataType>): string => {
  const dataType = fieldToDataType.get(field);
  if (dataType === "string") {
    return `"${value}"`;
  }
  return value.toString();
};

export const fromText = (text: string): Predicate => {
  //TODO {manstis} The text in the payload needs to have been converted to a Predicate
  const predicate = new SimplePredicate({
    field: "mocked" as FieldName,
    operator: "equal",
    value: 48
  });
  //TODO {manstis} This is vitally important to ensure marshalling to XML works OK!
  (predicate as any)._type = "SimplePredicate";
  return predicate;
};
