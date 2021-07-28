/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import {
  CompoundPredicate,
  CompoundPredicateBooleanOperator,
  False,
  Predicate,
  SimplePredicate,
  SimplePredicateOperator,
  True,
} from "@kogito-tooling/pmml-editor-marshaller";
import { fromText } from "@kogito-tooling/pmml-editor/dist/editor/components/EditorScorecard/organisms";

describe("PredicateConverter", () => {
  test("fromText::undefined", () => {
    const result = fromText(undefined);

    expect(result).toBeUndefined();
  });

  test("fromText::True", () => {
    assertTrue(fromText("True"));
  });

  test("fromText::true", () => {
    assertTrue(fromText("true"));
  });

  const assertTrue = (predicate: Predicate | undefined) => {
    expect(predicate).toBeInstanceOf(True);
  };

  test("fromText::False", () => {
    assertFalse(fromText("False"));
  });

  test("fromText::false", () => {
    assertFalse(fromText("false"));
  });

  const assertFalse = (predicate: Predicate | undefined) => {
    expect(predicate).toBeInstanceOf(False);
  };

  test("fromText::UnaryOperator::isMissing", () => {
    assertSimplePredicate(fromText("field1 isMissing"), "isMissing");
  });

  test("fromText::UnaryOperator::isNotMissing", () => {
    assertSimplePredicate(fromText("field1 isNotMissing"), "isNotMissing");
  });

  test("fromText::BinaryOperator::equal", () => {
    assertSimplePredicate(fromText("field1 = 100"), "equal", "100");
  });

  test("fromText::BinaryOperator::notEqual", () => {
    assertSimplePredicate(fromText("field1 <> 100"), "notEqual", "100");
  });

  test("fromText::BinaryOperator::greaterThan", () => {
    assertSimplePredicate(fromText("field1 > 100"), "greaterThan", "100");
  });

  test("fromText::BinaryOperator::greaterOrEqual", () => {
    assertSimplePredicate(fromText("field1 >= 100"), "greaterOrEqual", "100");
  });

  test("fromText::BinaryOperator::lessThan", () => {
    assertSimplePredicate(fromText("field1 < 100"), "lessThan", "100");
  });

  test("fromText::BinaryOperator::lessOrEqual", () => {
    assertSimplePredicate(fromText("field1 <= 100"), "lessOrEqual", "100");
  });

  const assertSimplePredicate = (
    predicate: Predicate | undefined,
    operator: SimplePredicateOperator,
    value?: string
  ) => {
    expect(predicate).toBeInstanceOf(SimplePredicate);

    const sp = predicate as SimplePredicate;
    expect(sp.field).toBe("field1");
    expect(sp.operator).toBe(operator);
    value ? expect(sp.value).toBe(value) : expect(sp.value).toBeUndefined();
  };

  test("fromText::CompoundPredicate::or", () => {
    assertCompoundPredicate(fromText("field1 = 10 or field2 = 20"), "or");
  });

  test("fromText::CompoundPredicate::and", () => {
    assertCompoundPredicate(fromText("field1 = 10 and field2 = 20"), "and");
  });

  test("fromText::CompoundPredicate::xor", () => {
    assertCompoundPredicate(fromText("field1 = 10 xor field2 = 20"), "xor");
  });

  const assertCompoundPredicate = (
    predicate: Predicate | undefined,
    operator: CompoundPredicateBooleanOperator,
    value?: string
  ) => {
    expect(predicate).toBeInstanceOf(CompoundPredicate);

    const cp = predicate as CompoundPredicate;
    expect(cp.booleanOperator).toBe(operator);
    expect(cp.predicates?.length).toBe(2);

    const cpp1 = cp.predicates ? cp.predicates[0] : undefined;
    const cpp2 = cp.predicates ? cp.predicates[1] : undefined;
    expect(cpp1).toBeInstanceOf(SimplePredicate);
    const sp1 = cpp1 as SimplePredicate;
    expect(sp1.field).toBe("field1");
    expect(sp1.operator).toBe("equal");
    expect(sp1.value).toBe("10");

    expect(cpp2).toBeInstanceOf(SimplePredicate);
    const sp2 = cpp2 as SimplePredicate;
    expect(sp2.field).toBe("field2");
    expect(sp2.operator).toBe("equal");
    expect(sp2.value).toBe("20");
  };

  test("fromText::fieldName::underscore", () => {
    assertFieldName(fromText("field_1 isMissing"), "field_1");
    assertFieldName(fromText("fi__eld_1 isMissing"), "fi__eld_1");
    assertFieldName(fromText("field_1 = 100"), "field_1");
    assertFieldName(fromText("fi__eld_1 = 100"), "fi__eld_1");
  });

  test("fromText::fieldName::hyphen", () => {
    assertFieldName(fromText("field-1 isMissing"), "field-1");
    assertFieldName(fromText("fi--eld-1 isMissing"), "fi--eld-1");
    assertFieldName(fromText("field-1 = 100"), "field-1");
    assertFieldName(fromText("fi--eld-1 = 100"), "fi--eld-1");
  });

  const assertFieldName = (predicate: Predicate | undefined, fieldName: string) => {
    expect(predicate).toBeInstanceOf(SimplePredicate);

    const sp = predicate as SimplePredicate;
    expect(sp.field).toBe(fieldName);
  };
});
