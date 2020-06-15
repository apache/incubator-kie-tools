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
  Attribute,
  Characteristic,
  CompoundPredicate,
  DataDictionary,
  False,
  MiningField,
  Model,
  OutputField,
  PMML,
  Predicate,
  Scorecard,
  SimplePredicate,
  True
} from "@kogito-tooling/pmml-editor-codegen";
import * as XMLJS from "xml-js";
import { PMML2XML, XML2PMML } from "../../marshaller";
import {
  SCORE_CARD_BASIC_COMPLEX_PARTIAL_SCORE,
  SCORE_CARD_COMPOUND_PREDICATE,
  SCORE_CARD_NESTED_COMPLEX_PARTIAL_SCORE,
  SCORE_CARD_NESTED_COMPOUND_PREDICATE,
  SCORE_CARD_SIMPLE_PREDICATE
} from "./TestData_ScoreCards";

describe("Scorecard tests", () => {
  test("Scorecard::DataDictionary", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary.DataField.length).toBe(3);
    expect(dataDictionary.DataField[0].name).toBe("input1");
    expect(dataDictionary.DataField[0].dataType).toBe("double");
    expect(dataDictionary.DataField[0].optype).toBe("continuous");

    expect(dataDictionary.DataField[1].name).toBe("input2");
    expect(dataDictionary.DataField[1].dataType).toBe("double");
    expect(dataDictionary.DataField[1].optype).toBe("continuous");

    expect(dataDictionary.DataField[2].name).toBe("score");
    expect(dataDictionary.DataField[2].dataType).toBe("double");
    expect(dataDictionary.DataField[2].optype).toBe("continuous");
  });

  test("Scorecard::Models", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);

    expect(pmml).not.toBeNull();

    expect(pmml.models).not.toBeUndefined();
    const models: Model[] = pmml.models ?? [];
    expect(models.length).toBe(1);

    const model: Model = models[0];
    expect(model).toBeInstanceOf(Scorecard);
  });

  test("Scorecard::MiningSchema", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(scorecard.MiningSchema.MiningField.length).toBe(3);
    const miningFields: MiningField[] = scorecard.MiningSchema.MiningField;
    expect(miningFields[0].name).toBe("input1");
    expect(miningFields[0].usageType).toBe("active");
    expect(miningFields[0].invalidValueTreatment).toBe("asMissing");

    expect(miningFields[1].name).toBe("input2");
    expect(miningFields[1].usageType).toBe("active");
    expect(miningFields[1].invalidValueTreatment).toBe("asMissing");

    expect(miningFields[2].name).toBe("score");
    expect(miningFields[2].usageType).toBe("target");
    expect(miningFields[2].invalidValueTreatment).toBeUndefined();
  });

  test("Scorecard::Output", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(scorecard.Output?.OutputField.length).toBe(3);
    const outputFields: OutputField[] = scorecard.Output?.OutputField as OutputField[];
    expect(outputFields[0].name).toBe("Score");
    expect(outputFields[0].feature).toBe("predictedValue");
    expect(outputFields[0].dataType).toBe("double");
    expect(outputFields[0].optype).toBe("continuous");

    expect(outputFields[1].name).toBe("Reason Code 1");
    expect(outputFields[1].rank).toBe(1);
    expect(outputFields[1].feature).toBe("reasonCode");
    expect(outputFields[1].dataType).toBe("string");
    expect(outputFields[1].optype).toBe("categorical");

    expect(outputFields[2].name).toBe("Reason Code 2");
    expect(outputFields[2].rank).toBe(2);
    expect(outputFields[2].feature).toBe("reasonCode");
    expect(outputFields[2].dataType).toBe("string");
    expect(outputFields[2].optype).toBe("categorical");
  });

  test("Scorecard::Characteristics", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(scorecard.Characteristics.Characteristic.length).toBe(2);
    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];

    expect(characteristics[0].name).toBe("input1Score");
    expect(characteristics[0].baselineScore).toBe(4);
    expect(characteristics[0].reasonCode).toBe("Input1ReasonCode");

    expect(characteristics[1].name).toBe("input2Score");
    expect(characteristics[1].baselineScore).toBe(8);
    expect(characteristics[1].reasonCode).toBe("Input2ReasonCode");
  });

  test("Scorecard::Characteristics:Attributes", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;
    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];

    expect(characteristics[0].Attribute?.length).toBe(2);
    expect(characteristics[0].Attribute[0].partialScore).toBe(-12);
    expect(characteristics[0].Attribute[1].partialScore).toBe(50);

    expect(characteristics[1].Attribute?.length).toBe(2);
    expect(characteristics[1].Attribute[0].partialScore).toBe(-8);
    expect(characteristics[1].Attribute[1].partialScore).toBe(32);
  });

  test("Scorecard::Characteristics:Attributes::SimplePredicate", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;
    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];
    const characteristic0Attributes: Attribute[] = characteristics[0].Attribute as Attribute[];
    const characteristic1Attributes: Attribute[] = characteristics[1].Attribute as Attribute[];

    expect(characteristic0Attributes[0].predicate).toBeInstanceOf(SimplePredicate);
    expect(characteristic0Attributes[1].predicate).toBeInstanceOf(SimplePredicate);

    const predicateC0A0: SimplePredicate = characteristic0Attributes[0].predicate as SimplePredicate;
    expect(predicateC0A0.field).toBe("input1");
    expect(predicateC0A0.operator).toBe("lessOrEqual");
    expect(predicateC0A0.value).toBe("10");

    const predicateC0A1: SimplePredicate = characteristic0Attributes[1].predicate as SimplePredicate;
    expect(predicateC0A1.field).toBe("input1");
    expect(predicateC0A1.operator).toBe("greaterThan");
    expect(predicateC0A1.value).toBe("10");

    expect(characteristic1Attributes[0].predicate).toBeInstanceOf(SimplePredicate);
    expect(characteristic1Attributes[1].predicate).toBeInstanceOf(SimplePredicate);

    const predicateC1A0: SimplePredicate = characteristic1Attributes[0].predicate as SimplePredicate;
    expect(predicateC1A0.field).toBe("input2");
    expect(predicateC1A0.operator).toBe("lessOrEqual");
    expect(predicateC1A0.value).toBe("-5");

    const predicateC1A1: SimplePredicate = characteristic1Attributes[1].predicate as SimplePredicate;
    expect(predicateC1A1.field).toBe("input2");
    expect(predicateC1A1.operator).toBe("greaterThan");
    expect(predicateC1A1.value).toBe("-5");
  });

  test("Scorecard::Characteristics:Attributes::CompoundPredicate", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_COMPOUND_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(pmml.DataDictionary.DataField.length).toBe(5);
    expect(scorecard.MiningSchema.MiningField.length).toBe(5);
    expect(scorecard.Output?.OutputField.length).toBe(4);
    expect(scorecard.Characteristics.Characteristic.length).toBe(3);

    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];
    const characteristic0: Characteristic = characteristics[0];
    const characteristic1: Characteristic = characteristics[1];
    const characteristic2: Characteristic = characteristics[2];

    //Characteristics
    expect(characteristic0.name).toBe("characteristic1Score");
    expect(characteristic0.baselineScore).toBe(-5.5);
    expect(characteristic0.reasonCode).toBe("characteristic1ReasonCode");

    expect(characteristic1.name).toBe("characteristic2Score");
    expect(characteristic1.baselineScore).toBe(11);
    expect(characteristic1.reasonCode).toBe("characteristic2ReasonCode");

    expect(characteristic2.name).toBe("characteristic3Score");
    expect(characteristic2.baselineScore).toBe(25);
    expect(characteristic2.reasonCode).toBe("characteristic3ReasonCode");

    const characteristic0Attributes: Attribute[] = characteristics[0].Attribute as Attribute[];
    const characteristic1Attributes: Attribute[] = characteristics[1].Attribute as Attribute[];
    const characteristic2Attributes: Attribute[] = characteristics[2].Attribute as Attribute[];

    //Characteristic 0, Attributes
    expect(characteristic0Attributes.length).toBe(3);
    expect(characteristic0Attributes[0].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic0Attributes[1].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic0Attributes[2].predicate).toBeInstanceOf(True);

    //Characteristic 0, Attribute 0
    const predicateC0A0: CompoundPredicate = characteristic0Attributes[0].predicate as CompoundPredicate;
    expect(predicateC0A0.booleanOperator).toBe("and");
    expect(predicateC0A0.predicates?.length).toBe(2);
    const predicateC0A0P0: Predicate[] = predicateC0A0.predicates as Predicate[];
    expect(predicateC0A0P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC0A0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC0A0P0P0: SimplePredicate = predicateC0A0P0[0] as SimplePredicate;
    expect(predicateC0A0P0P0.field).toBe("input1");
    expect(predicateC0A0P0P0.operator).toBe("lessOrEqual");
    expect(predicateC0A0P0P0.value).toBe("-5");
    const predicateC0A0P0P1: SimplePredicate = predicateC0A0P0[1] as SimplePredicate;
    expect(predicateC0A0P0P1.field).toBe("input2");
    expect(predicateC0A0P0P1.operator).toBe("lessOrEqual");
    expect(predicateC0A0P0P1.value).toBe("-5");

    //Characteristic 0, Attribute 1
    const predicateC0A1: CompoundPredicate = characteristic0Attributes[1].predicate as CompoundPredicate;
    expect(predicateC0A1.booleanOperator).toBe("and");
    expect(predicateC0A1.predicates?.length).toBe(2);
    const predicateC0A1P0: Predicate[] = predicateC0A1.predicates as Predicate[];
    expect(predicateC0A1P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC0A1P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC0A1P0P0: SimplePredicate = predicateC0A1P0[0] as SimplePredicate;
    expect(predicateC0A1P0P0.field).toBe("input1");
    expect(predicateC0A1P0P0.operator).toBe("greaterThan");
    expect(predicateC0A1P0P0.value).toBe("-5");
    const predicateC0A1P0P1: SimplePredicate = predicateC0A1P0[1] as SimplePredicate;
    expect(predicateC0A1P0P1.field).toBe("input2");
    expect(predicateC0A1P0P1.operator).toBe("greaterThan");
    expect(predicateC0A1P0P1.value).toBe("-5");

    //Characteristic 1, Attributes
    expect(characteristic1Attributes.length).toBe(4);
    expect(characteristic1Attributes[0].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic1Attributes[1].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic1Attributes[2].predicate).toBeInstanceOf(False);
    expect(characteristic1Attributes[3].predicate).toBeInstanceOf(True);

    //Characteristic 1, Attribute 0
    const predicateC1A0: CompoundPredicate = characteristic1Attributes[0].predicate as CompoundPredicate;
    expect(predicateC1A0.booleanOperator).toBe("or");
    expect(predicateC1A0.predicates?.length).toBe(2);
    const predicateC1A0P0: Predicate[] = predicateC1A0.predicates as Predicate[];
    expect(predicateC1A0P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC1A0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC1A0P0P0: SimplePredicate = predicateC1A0P0[0] as SimplePredicate;
    expect(predicateC1A0P0P0.field).toBe("input3");
    expect(predicateC1A0P0P0.operator).toBe("equal");
    expect(predicateC1A0P0P0.value).toBe("classA");
    const predicateC1A0P0P1: SimplePredicate = predicateC1A0P0[1] as SimplePredicate;
    expect(predicateC1A0P0P1.field).toBe("input4");
    expect(predicateC1A0P0P1.operator).toBe("equal");
    expect(predicateC1A0P0P1.value).toBe("classA");

    //Characteristic 1, Attribute 1
    const predicateC1A1: CompoundPredicate = characteristic1Attributes[1].predicate as CompoundPredicate;
    expect(predicateC1A1.booleanOperator).toBe("or");
    expect(predicateC1A1.predicates?.length).toBe(2);
    const predicateC1A1P0: Predicate[] = predicateC1A1.predicates as Predicate[];
    expect(predicateC1A1P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC1A1P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC1A1P0P0: SimplePredicate = predicateC1A1P0[0] as SimplePredicate;
    expect(predicateC1A1P0P0.field).toBe("input3");
    expect(predicateC1A1P0P0.operator).toBe("equal");
    expect(predicateC1A1P0P0.value).toBe("classB");
    const predicateC1A1P0P1: SimplePredicate = predicateC1A1P0[1] as SimplePredicate;
    expect(predicateC1A1P0P1.field).toBe("input4");
    expect(predicateC1A1P0P1.operator).toBe("equal");
    expect(predicateC1A1P0P1.value).toBe("classB");

    //Characteristic 2, Attributes
    expect(characteristic2Attributes.length).toBe(2);
    expect(characteristic2Attributes[0].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic2Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 2, Attribute 0
    const predicateC2A0: CompoundPredicate = characteristic2Attributes[0].predicate as CompoundPredicate;
    expect(predicateC2A0.booleanOperator).toBe("xor");
    expect(predicateC2A0.predicates?.length).toBe(2);
    const predicateC2A0P0: Predicate[] = predicateC2A0.predicates as Predicate[];
    expect(predicateC2A0P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC2A0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC2A0P0P0: SimplePredicate = predicateC2A0P0[0] as SimplePredicate;
    expect(predicateC2A0P0P0.field).toBe("input3");
    expect(predicateC2A0P0P0.operator).toBe("equal");
    expect(predicateC2A0P0P0.value).toBe("classA");
    const predicateC2A0P0P1: SimplePredicate = predicateC2A0P0[1] as SimplePredicate;
    expect(predicateC2A0P0P1.field).toBe("input4");
    expect(predicateC2A0P0P1.operator).toBe("equal");
    expect(predicateC2A0P0P1.value).toBe("classA");
  });

  test("Scorecard::Characteristics:Attributes::NestedCompoundPredicate", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_NESTED_COMPOUND_PREDICATE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(pmml.DataDictionary.DataField.length).toBe(3);
    expect(scorecard.MiningSchema.MiningField.length).toBe(3);
    expect(scorecard.Output?.OutputField.length).toBe(3);
    expect(scorecard.Characteristics.Characteristic.length).toBe(2);

    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];
    const characteristic0: Characteristic = characteristics[0];
    const characteristic1: Characteristic = characteristics[1];

    //Characteristics
    expect(characteristic0.name).toBe("characteristic1Score");
    expect(characteristic0.baselineScore).toBe(21.8);
    expect(characteristic0.reasonCode).toBe("characteristic1ReasonCode");

    expect(characteristic1.name).toBe("characteristic2Score");
    expect(characteristic1.baselineScore).toBe(11);
    expect(characteristic1.reasonCode).toBe("characteristic2ReasonCode");

    const characteristic0Attributes: Attribute[] = characteristics[0].Attribute as Attribute[];
    const characteristic1Attributes: Attribute[] = characteristics[1].Attribute as Attribute[];

    //Characteristic 0, Attributes
    expect(characteristic0Attributes.length).toBe(2);
    expect(characteristic0Attributes[0].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic0Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 0, Attribute 0
    const predicateC0A0: CompoundPredicate = characteristic0Attributes[0].predicate as CompoundPredicate;
    expect(predicateC0A0.booleanOperator).toBe("and");
    expect(predicateC0A0.predicates?.length).toBe(2);
    const predicateC0A0P0: Predicate[] = predicateC0A0.predicates as Predicate[];

    expect(predicateC0A0P0[0]).toBeInstanceOf(CompoundPredicate);
    const predicateC0A0P0P0: CompoundPredicate = predicateC0A0P0[0] as CompoundPredicate;
    expect(predicateC0A0P0P0.booleanOperator).toBe("and");
    expect(predicateC0A0P0P0.predicates?.length).toBe(3);
    const predicateC0A0P0P0P0: Predicate[] = predicateC0A0P0P0.predicates as Predicate[];

    expect(predicateC0A0P0P0P0[0]).toBeInstanceOf(True);
    expect(predicateC0A0P0P0P0[1]).toBeInstanceOf(SimplePredicate);
    expect(predicateC0A0P0P0P0[2]).toBeInstanceOf(SimplePredicate);
    const predicateC0A0P0P0P0P0: SimplePredicate = predicateC0A0P0P0P0[1] as SimplePredicate;
    expect(predicateC0A0P0P0P0P0.field).toBe("input1");
    expect(predicateC0A0P0P0P0P0.operator).toBe("greaterThan");
    expect(predicateC0A0P0P0P0P0.value).toBe("-15");
    const predicateC0A0P0P0P0P1: SimplePredicate = predicateC0A0P0P0P0[2] as SimplePredicate;
    expect(predicateC0A0P0P0P0P1.field).toBe("input1");
    expect(predicateC0A0P0P0P0P1.operator).toBe("lessOrEqual");
    expect(predicateC0A0P0P0P0P1.value).toBe("25.4");

    expect(predicateC0A0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC0A0P0P1: SimplePredicate = predicateC0A0P0[1] as SimplePredicate;
    expect(predicateC0A0P0P1.field).toBe("input2");
    expect(predicateC0A0P0P1.operator).toBe("notEqual");
    expect(predicateC0A0P0P1.value).toBe("classA");

    //Characteristic 1, Attributes
    expect(characteristic1Attributes.length).toBe(3);
    expect(characteristic1Attributes[0].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic1Attributes[1].predicate).toBeInstanceOf(CompoundPredicate);
    expect(characteristic1Attributes[2].predicate).toBeInstanceOf(True);

    //Characteristic 1, Attribute 0
    const predicateC1A0: CompoundPredicate = characteristic1Attributes[0].predicate as CompoundPredicate;
    expect(predicateC1A0.booleanOperator).toBe("or");
    expect(predicateC1A0.predicates?.length).toBe(2);
    const predicateC1A0P0: Predicate[] = predicateC1A0.predicates as Predicate[];
    expect(predicateC1A0P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC1A0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC1A0P0P0: SimplePredicate = predicateC1A0P0[0] as SimplePredicate;
    expect(predicateC1A0P0P0.field).toBe("input1");
    expect(predicateC1A0P0P0.operator).toBe("lessOrEqual");
    expect(predicateC1A0P0P0.value).toBe("-20");
    const predicateC1A0P0P1: SimplePredicate = predicateC1A0P0[1] as SimplePredicate;
    expect(predicateC1A0P0P1.field).toBe("input2");
    expect(predicateC1A0P0P1.operator).toBe("equal");
    expect(predicateC1A0P0P1.value).toBe("classA");

    //Characteristic 1, Attribute 1, First Compound
    const predicateC1A1: CompoundPredicate = characteristic1Attributes[1].predicate as CompoundPredicate;
    expect(predicateC1A1.booleanOperator).toBe("or");
    expect(predicateC1A1.predicates?.length).toBe(2);
    const predicateC1A1P0: Predicate[] = predicateC1A1.predicates as Predicate[];
    expect(predicateC1A1P0[0]).toBeInstanceOf(CompoundPredicate);
    expect(predicateC1A1P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC1A1P0P1: SimplePredicate = predicateC1A1P0[1] as SimplePredicate;
    expect(predicateC1A1P0P1.field).toBe("input2");
    expect(predicateC1A1P0P1.operator).toBe("equal");
    expect(predicateC1A1P0P1.value).toBe("classC");

    //Characteristic 1, Attribute 1, Second nested Compound
    const predicateC1A1P0P0: CompoundPredicate = predicateC1A1P0[0] as CompoundPredicate;
    expect(predicateC1A1P0P0.booleanOperator).toBe("and");
    expect(predicateC1A1P0P0.predicates?.length).toBe(2);
    const predicateC1A1P0P0P0: Predicate[] = predicateC1A1P0P0.predicates as Predicate[];
    expect(predicateC1A1P0P0P0[0]).toBeInstanceOf(CompoundPredicate);
    expect(predicateC1A1P0P0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC1A1P0P0P0P1: SimplePredicate = predicateC1A1P0P0P0[1] as SimplePredicate;
    expect(predicateC1A1P0P0P0P1.field).toBe("input2");
    expect(predicateC1A1P0P0P0P1.operator).toBe("equal");
    expect(predicateC1A1P0P0P0P1.value).toBe("classB");

    //Characteristic 1, Attribute 1, Third nested Compound
    const predicateC1A1P0P0P0P0: CompoundPredicate = predicateC1A1P0P0P0[0] as CompoundPredicate;
    expect(predicateC1A1P0P0P0P0.booleanOperator).toBe("and");
    expect(predicateC1A1P0P0P0P0.predicates?.length).toBe(2);
    const predicateC1A1P0P0P0P0P0: Predicate[] = predicateC1A1P0P0P0P0.predicates as Predicate[];
    expect(predicateC1A1P0P0P0P0P0[0]).toBeInstanceOf(SimplePredicate);
    expect(predicateC1A1P0P0P0P0P0[1]).toBeInstanceOf(SimplePredicate);
    const predicateC1A1P0P0P0P0P0P0: SimplePredicate = predicateC1A1P0P0P0P0P0[0] as SimplePredicate;
    expect(predicateC1A1P0P0P0P0P0P0.field).toBe("input1");
    expect(predicateC1A1P0P0P0P0P0P0.operator).toBe("greaterOrEqual");
    expect(predicateC1A1P0P0P0P0P0P0.value).toBe("5");
    const predicateC1A1P0P0P0P0P0P1: SimplePredicate = predicateC1A1P0P0P0P0P0[1] as SimplePredicate;
    expect(predicateC1A1P0P0P0P0P0P1.field).toBe("input1");
    expect(predicateC1A1P0P0P0P0P0P1.operator).toBe("lessThan");
    expect(predicateC1A1P0P0P0P0P0P1.value).toBe("12");
  });

  test("Scorecard::BasicComplexPartialScore", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_BASIC_COMPLEX_PARTIAL_SCORE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(scorecard.Characteristics.Characteristic.length).toBe(2);

    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];
    const characteristic0: Characteristic = characteristics[0];
    const characteristic1: Characteristic = characteristics[1];

    //Characteristics
    expect(characteristic0.name).toBe("characteristic1Score");
    expect(characteristic0.baselineScore).toBe(20);
    expect(characteristic0.reasonCode).toBe("characteristic1ReasonCode");

    expect(characteristic1.name).toBe("characteristic2Score");
    expect(characteristic1.baselineScore).toBe(5);
    expect(characteristic1.reasonCode).toBe("characteristic2ReasonCode");

    const characteristic0Attributes: Attribute[] = characteristics[0].Attribute as Attribute[];
    const characteristic1Attributes: Attribute[] = characteristics[1].Attribute as Attribute[];

    //Characteristic 0, Attributes
    expect(characteristic0Attributes.length).toBe(2);
    expect(characteristic0Attributes[0].predicate).toBeInstanceOf(SimplePredicate);
    expect(characteristic0Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 0, Attributes' ComplexPartialScores
    expect(characteristic0Attributes[0].ComplexPartialScore).not.toBeUndefined();
    expect(characteristic0Attributes[1].ComplexPartialScore).toBeUndefined();
    const c0a0cps: string = XMLJS.json2xml(JSON.stringify(characteristic0Attributes[0].ComplexPartialScore));
    expect(c0a0cps).toBe(`<Apply function="+"><FieldRef field="input1"/><FieldRef field="input2"/></Apply>`);

    //Characteristic 1, Attributes
    expect(characteristic1Attributes.length).toBe(2);
    expect(characteristic1Attributes[0].predicate).toBeInstanceOf(SimplePredicate);
    expect(characteristic1Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 1, Attributes' ComplexPartialScores
    expect(characteristic1Attributes[0].ComplexPartialScore).not.toBeUndefined();
    expect(characteristic1Attributes[1].ComplexPartialScore).toBeUndefined();
    const c1a0cps: string = XMLJS.json2xml(JSON.stringify(characteristic1Attributes[0].ComplexPartialScore));
    expect(c1a0cps).toBe(`<Apply function="*"><FieldRef field="input1"/><FieldRef field="input2"/></Apply>`);

    //Check round-trip
    const xml: string = PMML2XML(pmml);
    expect(xml).not.toBeNull();

    const pmml2: PMML = XML2PMML(xml);
    expect(pmml).toEqual(pmml2);
  });

  test("Scorecard::NestedComplexPartialScore", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_NESTED_COMPLEX_PARTIAL_SCORE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;

    expect(scorecard.Characteristics.Characteristic.length).toBe(2);

    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];
    const characteristic0: Characteristic = characteristics[0];
    const characteristic1: Characteristic = characteristics[1];

    //Characteristics
    expect(characteristic0.name).toBe("characteristic1Score");
    expect(characteristic0.baselineScore).toBe(20);
    expect(characteristic0.reasonCode).toBe("characteristic1ReasonCode");

    expect(characteristic1.name).toBe("characteristic2Score");
    expect(characteristic1.baselineScore).toBe(5);
    expect(characteristic1.reasonCode).toBe("characteristic2ReasonCode");

    const characteristic0Attributes: Attribute[] = characteristics[0].Attribute as Attribute[];
    const characteristic1Attributes: Attribute[] = characteristics[1].Attribute as Attribute[];

    //Characteristic 0, Attributes
    expect(characteristic0Attributes.length).toBe(2);
    expect(characteristic0Attributes[0].predicate).toBeInstanceOf(SimplePredicate);
    expect(characteristic0Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 0, Attributes' ComplexPartialScores
    expect(characteristic0Attributes[0].ComplexPartialScore).not.toBeUndefined();
    expect(characteristic0Attributes[1].ComplexPartialScore).toBeUndefined();
    const c0a0cps: string = XMLJS.json2xml(JSON.stringify(characteristic0Attributes[0].ComplexPartialScore));
    expect(c0a0cps).toBe(
      `<Apply function="-"><Apply function="+"><FieldRef field="input1"/><FieldRef field="input2"/></Apply><Constant>5</Constant></Apply>`
    );

    //Characteristic 1, Attributes
    expect(characteristic1Attributes.length).toBe(2);
    expect(characteristic1Attributes[0].predicate).toBeInstanceOf(SimplePredicate);
    expect(characteristic1Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 1, Attributes' ComplexPartialScores
    expect(characteristic1Attributes[0].ComplexPartialScore).not.toBeUndefined();
    expect(characteristic1Attributes[1].ComplexPartialScore).toBeUndefined();
    const c1a0cps: string = XMLJS.json2xml(JSON.stringify(characteristic1Attributes[0].ComplexPartialScore));
    expect(c1a0cps).toBe(
      `<Apply function="*"><Constant>2</Constant><Apply function="*"><FieldRef field="input1"/><Apply function="/"><FieldRef field="input2"/><Constant>2</Constant></Apply></Apply></Apply>`
    );

    //Check round-trip
    const xml: string = PMML2XML(pmml);
    expect(xml).not.toBeNull();

    const pmml2: PMML = XML2PMML(xml);
    expect(pmml).toEqual(pmml2);
  });
});
