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
import * as XMLJS from "xml-js";
import {
  Attribute,
  Characteristic,
  Characteristics,
  CompoundPredicate,
  DataDictionary,
  False,
  FieldName,
  MiningField,
  Model,
  Output,
  OutputField,
  PMML,
  PMML2XML,
  Predicate,
  Scorecard,
  SimplePredicate,
  True,
  XML2PMML,
} from "@kogito-tooling/pmml-editor-marshaller";
import {
  SCORE_CARD_BASIC_COMPLEX_PARTIAL_SCORE,
  SCORE_CARD_COMPOUND_PREDICATE,
  SCORE_CARD_NESTED_COMPLEX_PARTIAL_SCORE,
  SCORE_CARD_NESTED_COMPOUND_PREDICATE,
  SCORE_CARD_PROTOTYPES,
  SCORE_CARD_SIMPLE_PREDICATE,
  SCORE_CARD_SIMPLE_PREDICATE_SINGLE,
} from "./TestData_ScoreCards";

describe("Scorecard tests", () => {
  type PredicateAssertion = (predicate: Predicate) => void;

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

    const scorecard: Scorecard = model as Scorecard;
    expect(scorecard.modelName).toBe("SimpleScorecard");
    expect(scorecard.functionName).toBe("regression");
    expect(scorecard.useReasonCodes).toBeTruthy();
    expect(scorecard.reasonCodeAlgorithm).toBe("pointsBelow");
    expect(scorecard.initialScore).toBe(5);
    expect(scorecard.baselineMethod).toBe("other");
    expect(scorecard.baselineScore).toBe(6);
  });

  test("Scorecard::Models::No modelName", () => {
    const xml: string = `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
      <Header/>
      <DataDictionary/>
      <Scorecard/>
    </PMML>`;
    const pmml: PMML = XML2PMML(xml);

    expect(pmml).not.toBeNull();

    expect(pmml.models).not.toBeUndefined();
    const models: Model[] = pmml.models ?? [];
    expect(models.length).toBe(1);

    const model: Model = models[0];
    expect(model).toBeInstanceOf(Scorecard);
    const scorecard: Scorecard = model as Scorecard;
    expect(scorecard.modelName).toBeUndefined();
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

    assertSimplePredicate(characteristic0Attributes[0].predicate, {
      field: "input1" as FieldName,
      operator: "lessOrEqual",
      value: "10",
    });
    assertSimplePredicate(characteristic0Attributes[1].predicate, {
      field: "input1" as FieldName,
      operator: "greaterThan",
      value: "10",
    });
    assertSimplePredicate(characteristic1Attributes[0].predicate, {
      field: "input2" as FieldName,
      operator: "lessOrEqual",
      value: "-5",
    });
    assertSimplePredicate(characteristic1Attributes[1].predicate, {
      field: "input2" as FieldName,
      operator: "greaterThan",
      value: "-5",
    });
  });

  test("Scorecard::Characteristics:Attributes::SimplePredicate::Single", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_SIMPLE_PREDICATE_SINGLE);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;
    const characteristics: Characteristic[] = scorecard.Characteristics?.Characteristic as Characteristic[];
    const characteristic0Attributes: Attribute[] = characteristics[0].Attribute as Attribute[];

    assertSimplePredicate(characteristic0Attributes[0].predicate, {
      field: "input1" as FieldName,
      operator: "lessOrEqual",
      value: "10",
    });

    //Check round-trip
    const xml: string = PMML2XML(pmml);
    expect(xml).not.toBeNull();

    const pmml2: PMML = XML2PMML(xml);
    expect(pmml).toEqual(pmml2);
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
    expect(characteristic0Attributes[2].predicate).toBeInstanceOf(True);

    //Characteristic 0, Attribute 0
    assertCompoundPredicate(characteristic0Attributes[0].predicate, { booleanOperator: "and" }, [
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input1" as FieldName,
          operator: "lessOrEqual",
          value: "-5",
        });
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input2" as FieldName,
          operator: "lessOrEqual",
          value: "-5",
        });
      },
    ]);

    //Characteristic 0, Attribute 1
    assertCompoundPredicate(characteristic0Attributes[1].predicate, { booleanOperator: "and" }, [
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input1" as FieldName,
          operator: "greaterThan",
          value: "-5",
        });
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input2" as FieldName,
          operator: "greaterThan",
          value: "-5",
        });
      },
    ]);

    //Characteristic 1, Attributes
    expect(characteristic1Attributes.length).toBe(4);
    expect(characteristic1Attributes[2].predicate).toBeInstanceOf(False);
    expect(characteristic1Attributes[3].predicate).toBeInstanceOf(True);

    //Characteristic 1, Attribute 0
    assertCompoundPredicate(characteristic1Attributes[0].predicate, { booleanOperator: "or" }, [
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input3" as FieldName,
          operator: "equal",
          value: "classA",
        });
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input4" as FieldName,
          operator: "equal",
          value: "classA",
        });
      },
    ]);

    //Characteristic 1, Attribute 1
    assertCompoundPredicate(characteristic1Attributes[1].predicate, { booleanOperator: "or" }, [
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input3" as FieldName,
          operator: "equal",
          value: "classB",
        });
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input4" as FieldName,
          operator: "equal",
          value: "classB",
        });
      },
    ]);

    //Characteristic 2, Attributes
    expect(characteristic2Attributes.length).toBe(2);
    expect(characteristic2Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 2, Attribute 0
    assertCompoundPredicate(characteristic2Attributes[0].predicate, { booleanOperator: "xor" }, [
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input3" as FieldName,
          operator: "equal",
          value: "classA",
        });
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input4" as FieldName,
          operator: "equal",
          value: "classA",
        });
      },
    ]);
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
    expect(characteristic0Attributes[1].predicate).toBeInstanceOf(True);

    //Characteristic 0, Attribute 0
    assertCompoundPredicate(characteristic0Attributes[0].predicate, { booleanOperator: "and" }, [
      (predicate: Predicate) => {
        assertCompoundPredicate(predicate, { booleanOperator: "and" }, [
          (cp: Predicate) => {
            expect(cp).toBeInstanceOf(True);
          },
          (cp: Predicate) => {
            assertSimplePredicate(cp, {
              field: "input1" as FieldName,
              operator: "greaterThan",
              value: "-15",
            });
          },
          (cp: Predicate) => {
            assertSimplePredicate(cp, {
              field: "input1" as FieldName,
              operator: "lessOrEqual",
              value: "25.4",
            });
          },
        ]);
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input2" as FieldName,
          operator: "notEqual",
          value: "classA",
        });
      },
    ]);

    //Characteristic 1, Attributes
    expect(characteristic1Attributes.length).toBe(3);
    expect(characteristic1Attributes[2].predicate).toBeInstanceOf(True);

    //Characteristic 1, Attribute 0
    assertCompoundPredicate(characteristic1Attributes[0].predicate, { booleanOperator: "or" }, [
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input1" as FieldName,
          operator: "lessOrEqual",
          value: "-20",
        });
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input2" as FieldName,
          operator: "equal",
          value: "classA",
        });
      },
    ]);

    //Characteristic 1, Attribute 1
    assertCompoundPredicate(characteristic1Attributes[1].predicate, { booleanOperator: "or" }, [
      (predicate: Predicate) => {
        assertCompoundPredicate(predicate, { booleanOperator: "and" }, [
          (cp: Predicate) => {
            assertCompoundPredicate(cp, { booleanOperator: "and" }, [
              (cp2: Predicate) => {
                assertSimplePredicate(cp2, {
                  field: "input1" as FieldName,
                  operator: "greaterOrEqual",
                  value: "5",
                });
              },
              (cp2: Predicate) => {
                assertSimplePredicate(cp2, {
                  field: "input1" as FieldName,
                  operator: "lessThan",
                  value: "12",
                });
              },
            ]);
          },
          (cp: Predicate) => {
            assertSimplePredicate(cp, {
              field: "input2" as FieldName,
              operator: "equal",
              value: "classB",
            });
          },
        ]);
      },
      (predicate: Predicate) => {
        assertSimplePredicate(predicate, {
          field: "input2" as FieldName,
          operator: "equal",
          value: "classC",
        });
      },
    ]);
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

  test("Scorecard::prototype::preservation", () => {
    const pmml: PMML = XML2PMML(SCORE_CARD_PROTOTYPES);
    const models: Model[] = pmml.models ?? [];
    const scorecard: Scorecard = models[0] as Scorecard;
    const miningSchema = scorecard.MiningSchema;
    const miningFields: MiningField[] = scorecard.MiningSchema.MiningField;
    const output: Output = scorecard.Output as Output;
    const outputFields: OutputField[] = output.OutputField;
    const characteristics: Characteristics = scorecard.Characteristics;
    const characteristicFields: Characteristic[] = characteristics.Characteristic;

    expect(Object.getPrototypeOf(scorecard)).toBe(Scorecard.prototype);

    expect(Object.getPrototypeOf(miningSchema)).toBe(Object.prototype);
    expect(Object.getPrototypeOf(miningFields)).toBe(Array.prototype);
    expect(Object.getPrototypeOf(miningFields[0])).toBe(Object.prototype);
    miningFields.push({ name: "mf" as FieldName });
    expect(Object.getPrototypeOf(miningFields[1])).toBe(Object.prototype);

    expect(Object.getPrototypeOf(output)).toBe(Object.prototype);
    expect(Object.getPrototypeOf(outputFields)).toBe(Array.prototype);
    expect(Object.getPrototypeOf(outputFields[0])).toBe(Object.prototype);
    outputFields.push({ name: "mf" as FieldName, dataType: "string" });
    expect(Object.getPrototypeOf(outputFields[1])).toBe(Object.prototype);

    expect(Object.getPrototypeOf(characteristics)).toBe(Object.prototype);
    expect(Object.getPrototypeOf(characteristicFields)).toBe(Array.prototype);
    expect(Object.getPrototypeOf(characteristicFields[0])).toBe(Object.prototype);
    characteristicFields.push({ name: "test", Attribute: [] });
    expect(Object.getPrototypeOf(characteristicFields[1])).toBe(Object.prototype);
  });

  test("Scorecard::Models::isScorable::true", () => {
    const scorecard: Scorecard = makeScorecardWithIsScorable(true);
    expect(scorecard.isScorable).toBeTruthy();
  });

  test("Scorecard::Models::isScorable::false", () => {
    const scorecard: Scorecard = makeScorecardWithIsScorable(false);
    expect(scorecard.isScorable).toBeFalsy();
  });

  function makeScorecardWithIsScorable(isScorable: boolean): Scorecard {
    const xml: string = `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
      <Header/>
      <DataDictionary/>
      <Scorecard isScorable="${isScorable}"/>
    </PMML>`;
    const pmml: PMML = XML2PMML(xml);

    expect(pmml).not.toBeNull();

    expect(pmml.models).not.toBeUndefined();
    const models: Model[] = pmml.models ?? [];
    expect(models.length).toBe(1);

    const model: Model = models[0];
    expect(model).toBeInstanceOf(Scorecard);
    return model as Scorecard;
  }

  test("Scorecard::Models::useReasonCodes::true", () => {
    const scorecard: Scorecard = makeScorecardWithUseReasonCodes(true);
    expect(scorecard.useReasonCodes).toBeTruthy();
  });

  test("Scorecard::Models::useReasonCodes::false", () => {
    const scorecard: Scorecard = makeScorecardWithUseReasonCodes(false);
    expect(scorecard.useReasonCodes).toBeFalsy();
  });

  function makeScorecardWithUseReasonCodes(useReasonCodes: boolean): Scorecard {
    const xml: string = `<PMML xmlns="http://www.dmg.org/PMML-4_4" version="4.4"> 
      <Header/>
      <DataDictionary/>
      <Scorecard useReasonCodes="${useReasonCodes}"/>
    </PMML>`;
    const pmml: PMML = XML2PMML(xml);

    expect(pmml).not.toBeNull();

    expect(pmml.models).not.toBeUndefined();
    const models: Model[] = pmml.models ?? [];
    expect(models.length).toBe(1);

    const model: Model = models[0];
    expect(model).toBeInstanceOf(Scorecard);
    return model as Scorecard;
  }

  function assertSimplePredicate(actualPredicate: Predicate | undefined, expectedPredicate: SimplePredicate): void {
    expect(actualPredicate).toBeInstanceOf(SimplePredicate);
    const actualSimplePredicate: SimplePredicate = actualPredicate as SimplePredicate;
    expect(actualSimplePredicate.field).toEqual(expectedPredicate.field);
    expect(actualSimplePredicate.operator).toEqual(expectedPredicate.operator);
    expect(actualSimplePredicate.value).toEqual(expectedPredicate.value);
  }

  function assertCompoundPredicate(
    actualPredicate: Predicate | undefined,
    expectedPredicate: CompoundPredicate,
    assertions: PredicateAssertion[]
  ): void {
    expect(actualPredicate).toBeInstanceOf(CompoundPredicate);
    const actualCompoundPredicate: CompoundPredicate = actualPredicate as CompoundPredicate;
    const actualCompoundPredicatePredicates: Predicate[] = actualCompoundPredicate.predicates as Predicate[];
    expect(actualCompoundPredicate.booleanOperator).toEqual(expectedPredicate.booleanOperator);
    expect(actualCompoundPredicate.predicates?.length).toBe(assertions.length);
    for (let i: number = 0; i < assertions.length; i++) {
      assertions[i](actualCompoundPredicatePredicates[i]);
    }
  }
});
