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

import {
  CompoundPredicate,
  CompoundPredicateBooleanOperator,
  DataField,
  DataType,
  False,
  Predicate,
  SimplePredicate,
  SimplePredicateOperator,
  SimpleSetPredicate,
  True,
} from "@kie-tools/pmml-editor-marshaller";

const SimplePredicateOperatorMap: Map<SimplePredicateOperator, string> = new Map<SimplePredicateOperator, string>([
  ["equal", "="],
  ["notEqual", "<>"],
  ["lessThan", "<"],
  ["lessOrEqual", "<="],
  ["greaterThan", ">"],
  ["greaterOrEqual", ">="],
  ["isMissing", "isMissing"],
  ["isNotMissing", "isNotMissing"],
]);

export const toText = (predicate: Predicate | undefined, fields: DataField[]): string => {
  const fieldToDataType: Map<string, DataType> = new Map(fields.map((field) => [field.name, field.dataType]));
  return _toText(predicate, fieldToDataType, 0);
};

const _toText = (predicate: Predicate | undefined, fieldToDataType: Map<string, DataType>, nesting: number): string => {
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
    const children: string[] = [];
    // TODO {manstis} If parenthesis are needed: text = text + (nesting > 0 ? "( " : "");
    cp.predicates?.forEach((p) => children.push(_toText(p, fieldToDataType, nesting + 1)));
    text = text + children.join(" " + cp.booleanOperator + " ");
    // TODO {manstis} If parenthesis are needed: text = text + (nesting > 0 ? ")" : "");
    return text;
  }
  return "";
};

const TruePredicate = () => {
  const predicate: True = new True({});
  (predicate as any)._type = "True";
  return predicate;
};

const FalsePredicate = () => {
  const predicate: False = new False({});
  (predicate as any)._type = "False";
  return predicate;
};

const UnarySimplePredicate = (field: string, operator: SimplePredicateOperator) => {
  const predicate: SimplePredicate = new SimplePredicate({ field: field, operator: operator });
  (predicate as any)._type = "SimplePredicate";
  return predicate;
};

const BinarySimplePredicate = (field: string, operator: SimplePredicateOperator, value: any) => {
  const predicate: SimplePredicate = new SimplePredicate({ field: field, operator: operator, value: value });
  (predicate as any)._type = "SimplePredicate";
  return predicate;
};

const SimpleCompoundPredicate = (
  predicate1: Predicate,
  predicate2: Predicate,
  booleanOperator: CompoundPredicateBooleanOperator
) => {
  const predicate: CompoundPredicate = new CompoundPredicate({ predicates: [predicate1, predicate2], booleanOperator });
  (predicate as any)._type = "CompoundPredicate";
  return predicate;
};

const _value = (field: string, value: any, fieldToDataType: Map<string, DataType>): string => {
  if (value === undefined) {
    return "";
  }
  const dataType = fieldToDataType.get(field);
  if (dataType === "string") {
    return `"${value}"`;
  }
  return value.toString();
};

const _operator = (lookup: string): SimplePredicateOperator | undefined => {
  const lookups = SimplePredicateOperatorMap.entries();
  for (const [pmmlOperator, operator] of lookups) {
    if (operator === lookup) {
      return pmmlOperator;
    }
  }
};

//TODO {manstis} The text in the payload needs to have been converted to a Predicate
export const fromText = (text: string | undefined): Predicate | undefined => {
  if (text === undefined) {
    return undefined;
  }

  text = text.trim();
  if (text === "") {
    return undefined;
  }

  //Quick RegEx based match for SimplePredicates.. Need a parser for ALL Predicates
  const regTrue = /^True$/gim;
  const regFalse = /^False$/gim;
  const regUnaryOperator = /^(\S+)\s+(isMissing|isNotMissing)\s*$/gm;
  const regBinaryOperator = /^(\S+)\s*(=|>|<|<=|>=|<>)\s*"?(\S+)"?$/gm;
  const regSimpleCompound = /^(.*)\s*(\band\b|\bxor\b|\bor\b)\s*(.*)$/gm;

  if (regTrue.test(text)) {
    return TruePredicate();
  }
  if (regFalse.test(text)) {
    return FalsePredicate();
  }

  const unaryMatches = regUnaryOperator.exec(text);
  if (unaryMatches !== null) {
    return UnarySimplePredicate(unaryMatches[1], unaryMatches[2] as SimplePredicateOperator);
  }

  const binaryMatches = regBinaryOperator.exec(text);
  if (binaryMatches !== null) {
    return BinarySimplePredicate(
      binaryMatches[1],
      _operator(binaryMatches[2]) as SimplePredicateOperator,
      binaryMatches[3]
    );
  }

  const compoundMatches = regSimpleCompound.exec(text);
  if (compoundMatches !== null) {
    const predicate1 = fromText(compoundMatches[1]);
    const predicate2 = fromText(compoundMatches[3]);
    const booleanOperator = compoundMatches[2] as CompoundPredicateBooleanOperator;
    if (predicate1 !== undefined && predicate2 !== undefined && booleanOperator !== undefined) {
      return SimpleCompoundPredicate(predicate1, predicate2, booleanOperator);
    }
  }

  return TruePredicate();
};
