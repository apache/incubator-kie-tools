/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { Builder } from "@kogito-tooling/pmml-editor/dist/editor/paths";

describe("PathBuilders::Basic tests", () => {
  test("Header", () => {
    expect(Builder().forHeader().build().path).toBe("Header");
  });

  test("DataDictionary", () => {
    expect(Builder().forDataDictionary().build().path).toBe("DataDictionary");
  });

  test("DataField", () => {
    expect(Builder().forDataDictionary().forDataField().build().path).toBe("DataDictionary.DataField");
  });

  test("DataField::Indexed", () => {
    expect(Builder().forDataDictionary().forDataField(0).build().path).toBe("DataDictionary.DataField[0]");
  });

  test("Interval", () => {
    expect(Builder().forDataDictionary().forDataField(0).forInterval().build().path).toBe(
      "DataDictionary.DataField[0].Interval"
    );
  });

  test("Interval::Indexed", () => {
    expect(Builder().forDataDictionary().forDataField(0).forInterval(1).build().path).toBe(
      "DataDictionary.DataField[0].Interval[1]"
    );
  });

  test("Value", () => {
    expect(Builder().forDataDictionary().forDataField(0).forValue().build().path).toBe(
      "DataDictionary.DataField[0].Value"
    );
  });

  test("Value::Indexed", () => {
    expect(Builder().forDataDictionary().forDataField(0).forValue(1).build().path).toBe(
      "DataDictionary.DataField[0].Value[1]"
    );
  });

  test("Model", () => {
    expect(Builder().forModel().build().path).toBe("models");
  });

  test("Model::Indexed", () => {
    expect(Builder().forModel(0).build().path).toBe("models[0]");
  });

  test("Model::baselineScore", () => {
    expect(Builder().forModel(0).forBaselineScore().build().path).toBe("models[0].baselineScore");
  });

  test("Model::useReasonCodes", () => {
    expect(Builder().forModel(0).forUseReasonCodes().build().path).toBe("models[0].useReasonCodes");
  });

  test("MiningSchema", () => {
    expect(Builder().forModel(0).forMiningSchema().build().path).toBe("models[0].MiningSchema");
  });

  test("MiningField", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField().build().path).toBe(
      "models[0].MiningSchema.MiningField"
    );
  });

  test("MiningField::Indexed", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).build().path).toBe(
      "models[0].MiningSchema.MiningField[1]"
    );
  });

  test("MiningField::importance", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).forImportance().build().path).toBe(
      "models[0].MiningSchema.MiningField[1].importance"
    );
  });

  test("MiningField::highValue", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).forHighValue().build().path).toBe(
      "models[0].MiningSchema.MiningField[1].highValue"
    );
  });

  test("MiningField::lowValue", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).forLowValue().build().path).toBe(
      "models[0].MiningSchema.MiningField[1].lowValue"
    );
  });

  test("MiningField::invalidValueReplacement", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).forInvalidValueReplacement().build().path).toBe(
      "models[0].MiningSchema.MiningField[1].invalidValueReplacement"
    );
  });

  test("MiningField::missingValueReplacement", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).forMissingValueReplacement().build().path).toBe(
      "models[0].MiningSchema.MiningField[1].missingValueReplacement"
    );
  });

  test("MiningField::lowValue", () => {
    expect(Builder().forModel(0).forMiningSchema().forMiningField(1).forDataFieldMissing().build().path).toBe(
      "models[0].MiningSchema.MiningField[1].dataFieldMissing"
    );
  });

  test("Characteristics", () => {
    expect(Builder().forModel(0).forCharacteristics().build().path).toBe("models[0].Characteristics");
  });

  test("Characteristic", () => {
    expect(Builder().forModel(0).forCharacteristics().forCharacteristic().build().path).toBe(
      "models[0].Characteristics.Characteristic"
    );
  });

  test("Characteristic::Indexed", () => {
    expect(Builder().forModel(0).forCharacteristics().forCharacteristic(1).build().path).toBe(
      "models[0].Characteristics.Characteristic[1]"
    );
  });

  test("Characteristic::baselineScore", () => {
    expect(Builder().forModel(0).forCharacteristics().forCharacteristic(1).forBaselineScore().build().path).toBe(
      "models[0].Characteristics.Characteristic[1].baselineScore"
    );
  });

  test("Characteristic::reasonCode", () => {
    expect(Builder().forModel(0).forCharacteristics().forCharacteristic(1).forReasonCode().build().path).toBe(
      "models[0].Characteristics.Characteristic[1].reasonCode"
    );
  });

  test("Attribute", () => {
    expect(Builder().forModel(0).forCharacteristics().forCharacteristic(1).forAttribute().build().path).toBe(
      "models[0].Characteristics.Characteristic[1].Attribute"
    );
  });

  test("Attribute::Indexed", () => {
    expect(Builder().forModel(0).forCharacteristics().forCharacteristic(1).forAttribute(2).build().path).toBe(
      "models[0].Characteristics.Characteristic[1].Attribute[2]"
    );
  });

  test("Attribute::reasonCode", () => {
    expect(
      Builder().forModel(0).forCharacteristics().forCharacteristic(1).forAttribute(2).forReasonCode().build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].reasonCode");
  });

  test("Attribute::partialScore", () => {
    expect(
      Builder().forModel(0).forCharacteristics().forCharacteristic(1).forAttribute(2).forPartialScore().build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].partialScore");
  });

  test("Predicate", () => {
    expect(
      Builder().forModel(0).forCharacteristics().forCharacteristic(1).forAttribute(2).forPredicate().build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].predicate");
  });

  test("Predicate::fieldName", () => {
    expect(
      Builder()
        .forModel(0)
        .forCharacteristics()
        .forCharacteristic(1)
        .forAttribute(2)
        .forPredicate()
        .forFieldName()
        .build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].predicate.fieldName");
  });

  test("Predicates", () => {
    expect(
      Builder().forModel(0).forCharacteristics().forCharacteristic(1).forAttribute(2).forPredicate(3).build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].predicates[3]");
  });

  test("Predicates::fieldName", () => {
    expect(
      Builder()
        .forModel(0)
        .forCharacteristics()
        .forCharacteristic(1)
        .forAttribute(2)
        .forPredicate(3)
        .forFieldName()
        .build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].predicates[3].fieldName");
  });

  test("CompositePredicates", () => {
    expect(
      Builder()
        .forModel(0)
        .forCharacteristics()
        .forCharacteristic(1)
        .forAttribute(2)
        .forPredicate(3)
        .forPredicate(4)
        .build().path
    ).toBe("models[0].Characteristics.Characteristic[1].Attribute[2].predicates[3].predicates[4]");
  });

  test("Output", () => {
    expect(Builder().forModel(0).forOutput().build().path).toBe("models[0].Output");
  });

  test("OutputField", () => {
    expect(Builder().forModel(0).forOutput().forOutputField().build().path).toBe("models[0].Output.OutputField");
  });

  test("OutputField::Indexed", () => {
    expect(Builder().forModel(0).forOutput().forOutputField(1).build().path).toBe("models[0].Output.OutputField[1]");
  });

  test("OutputField::targetField", () => {
    expect(Builder().forModel(0).forOutput().forOutputField(1).forTargetField().build().path).toBe(
      "models[0].Output.OutputField[1].targetField"
    );
  });
});
