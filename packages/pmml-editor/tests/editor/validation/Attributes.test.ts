/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { ValidationEntry, ValidationRegistry } from "@kogito-tooling/pmml-editor/dist/editor/validation";
import { validateAttribute, validateAttributes } from "@kogito-tooling/pmml-editor/dist/editor/validation/Attributes";
import { CompoundPredicate, FieldName, SimplePredicate } from "@kogito-tooling/pmml-editor-marshaller";

let registry: ValidationRegistry;
beforeEach(() => {
  registry = new ValidationRegistry();
});

const asPath = (segment: string) => {
  return { path: segment };
};

const assertValidationEntry = (path: string, snippet: string) => {
  const validations: ValidationEntry[] = registry.get(asPath(path));
  expect(validations.length).toBe(1);
  expect(validations[0].message).toContain(snippet);
};

describe("ValidateAttribute", () => {
  test("ValidateAttribute::reasonCode::requiredButNotDefined", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: true },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      { predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }) },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(1);
    assertValidationEntry(
      "models[0].Characteristics.Characteristic[1].Attribute[2].reasonCode",
      "Reason code is required"
    );
  });

  test("ValidateAttribute::reasonCode::requiredAndDefinedOnCharacteristic", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: true },
      1,
      {
        reasonCode: "reasonCode",
        Attribute: [],
      },
      false,
      2,
      { predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }) },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });

  test("ValidateAttribute::reasonCode::requiredAndDefinedOnAttribute", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: true },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      {
        reasonCode: "reasonCode",
        predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });

  test("ValidateAttribute::partialScore::requiredButNotDefined", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      true,
      2,
      {
        predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(1);
    assertValidationEntry(
      "models[0].Characteristics.Characteristic[1].Attribute[2].partialScore",
      "Partial score is required"
    );
  });

  test("ValidateAttribute::partialScore::requiredAndPresent", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      true,
      2,
      {
        partialScore: 2.0,
        predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });

  test("ValidateAttribute::predicates::notDefined", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      {},
      [],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(1);
    assertValidationEntry("models[0].Characteristics.Characteristic[1].Attribute[2].predicate", "No predicate defined");
  });

  test("ValidateAttribute::predicates::definedButDoesNotExist::SimplePredicate", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      { predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }) },
      [],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(1);
    assertValidationEntry(
      "models[0].Characteristics.Characteristic[1].Attribute[2].predicate.fieldName",
      "cannot be found"
    );
  });

  test("ValidateAttribute::predicates::definedButDoesNotExist::CompoundPredicate", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      {
        predicate: new CompoundPredicate({
          predicates: [
            new SimplePredicate({
              field: "field1" as FieldName,
              operator: "greaterThan",
              value: 100,
            }),
            new SimplePredicate({
              field: "field1" as FieldName,
              operator: "lessOrEqual",
              value: 200,
            }),
          ],
          booleanOperator: "and",
        }),
      },
      [],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(2);
    assertValidationEntry(
      "models[0].Characteristics.Characteristic[1].Attribute[2].predicate.predicates[0].fieldName",
      "cannot be found"
    );
    assertValidationEntry(
      "models[0].Characteristics.Characteristic[1].Attribute[2].predicate.predicates[1].fieldName",
      "cannot be found"
    );
  });

  test("ValidateAttribute::predicates::definedAndDoesExist::SimplePredicate", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      { predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }) },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });

  test("ValidateAttribute::predicates::definedAndDoesExist::CompoundPredicate", () => {
    validateAttribute(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [],
      },
      false,
      2,
      {
        predicate: new CompoundPredicate({
          predicates: [
            new SimplePredicate({
              field: "field1" as FieldName,
              operator: "greaterThan",
              value: 100,
            }),
            new SimplePredicate({
              field: "field1" as FieldName,
              operator: "lessOrEqual",
              value: 200,
            }),
          ],
          booleanOperator: "and",
        }),
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });
});

describe("ValidateAttributes", () => {
  test("ValidateAttributes::partialScore::notRequired", () => {
    validateAttributes(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [
          {
            predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
          },
          { predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }) },
        ],
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });

  test("ValidateAttributes::partialScore::requiredButNotDefined", () => {
    validateAttributes(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [
          {
            partialScore: 1.0,
            predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
          },
          { predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }) },
        ],
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(1);
    assertValidationEntry(
      "models[0].Characteristics.Characteristic[1].Attribute[1].partialScore",
      "Partial score is required"
    );
  });

  test("ValidateAttributes::partialScore::requiredAndPresent", () => {
    validateAttributes(
      0,
      { baselineScore: 0.0, useReasonCodes: false },
      1,
      {
        Attribute: [
          {
            partialScore: 1.0,
            predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
          },
          {
            partialScore: 2.0,
            predicate: new SimplePredicate({ field: "field1" as FieldName, operator: "equal", value: 100 }),
          },
        ],
      },
      [{ name: "field1" as FieldName }],
      registry
    );

    expect(registry.get(asPath("models[0]")).length).toBe(0);
  });
});
