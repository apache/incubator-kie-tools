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
  CategoricalPredictor,
  DataDictionary,
  MiningField,
  Model,
  NumericPredictor,
  PMML,
  PMML2XML,
  RegressionModel,
  RegressionTable,
  XML2PMML,
} from "@kogito-tooling/pmml-editor-marshaller";
import {
  LINEAR_REGRESSION_MODEL_1,
  LINEAR_REGRESSION_MODEL_2,
  LINEAR_REGRESSION_MODEL_3,
} from "./TestData_LinearRegressions";

describe("RegressionModel tests", () => {
  test("RegressionModel::DataDictionary", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary.DataField.length).toBe(4);
    expect(dataDictionary.DataField[0].name).toBe("age");
    expect(dataDictionary.DataField[0].dataType).toBe("double");
    expect(dataDictionary.DataField[0].optype).toBe("continuous");

    expect(dataDictionary.DataField[1].name).toBe("salary");
    expect(dataDictionary.DataField[1].dataType).toBe("double");
    expect(dataDictionary.DataField[1].optype).toBe("continuous");

    expect(dataDictionary.DataField[2].name).toBe("car_location");
    expect(dataDictionary.DataField[2].dataType).toBe("string");
    expect(dataDictionary.DataField[2].optype).toBe("categorical");

    expect(dataDictionary.DataField[3].name).toBe("number_of_claims");
    expect(dataDictionary.DataField[3].dataType).toBe("integer");
    expect(dataDictionary.DataField[3].optype).toBe("continuous");
  });

  test("RegressionModel::Models", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);

    expect(pmml).not.toBeNull();

    expect(pmml.models).not.toBeUndefined();
    const models: Model[] = pmml.models ?? [];
    expect(models.length).toBe(1);

    const model: Model = models[0];
    expect(model).toBeInstanceOf(RegressionModel);

    const regressionModel: RegressionModel = model as RegressionModel;
    expect(regressionModel.modelName).toBe("Sample for linear regression");
    expect(regressionModel.functionName).toBe("regression");
    expect(regressionModel.algorithmName).toBe("linearRegression");
    expect(regressionModel.targetFieldName).toBe("number_of_claims");
    expect(regressionModel.modelType).toBeUndefined();
    expect(regressionModel.isScorable).toBeUndefined();
  });

  test("RegressionModel::MiningSchema", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);
    const models: Model[] = pmml.models ?? [];
    const regressionModel: RegressionModel = models[0] as RegressionModel;

    expect(regressionModel.MiningSchema.MiningField.length).toBe(4);
    const miningFields: MiningField[] = regressionModel.MiningSchema.MiningField;
    expect(miningFields[0].name).toBe("age");
    expect(miningFields[0].usageType).toBeUndefined();
    expect(miningFields[0].invalidValueTreatment).toBeUndefined();

    expect(miningFields[1].name).toBe("salary");
    expect(miningFields[1].usageType).toBeUndefined();
    expect(miningFields[1].invalidValueTreatment).toBeUndefined();

    expect(miningFields[2].name).toBe("car_location");
    expect(miningFields[2].usageType).toBeUndefined();
    expect(miningFields[2].invalidValueTreatment).toBeUndefined();

    expect(miningFields[3].name).toBe("number_of_claims");
    expect(miningFields[3].usageType).toBe("target");
    expect(miningFields[3].invalidValueTreatment).toBeUndefined();
  });

  test("RegressionModel::Output", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);
    const models: Model[] = pmml.models ?? [];
    const regressionModel: RegressionModel = models[0] as RegressionModel;

    expect(regressionModel.Output?.OutputField.length).toBe(0);
  });

  test("RegressionModel::RegressionTable", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);
    const models: Model[] = pmml.models ?? [];
    const regressionModel: RegressionModel = models[0] as RegressionModel;

    expect(regressionModel.RegressionTable?.length).toBe(1);

    const regressionTable: RegressionTable = regressionModel.RegressionTable[0];

    expect(regressionTable.intercept).toBe(132.37);
  });

  test("RegressionModel::RegressionTable::NumericPredictor", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);
    const models: Model[] = pmml.models ?? [];
    const regressionModel: RegressionModel = models[0] as RegressionModel;
    const regressionTable: RegressionTable = regressionModel.RegressionTable[0];

    expect(regressionTable.NumericPredictor).not.toBeUndefined();

    const numericPredicators: NumericPredictor[] = regressionTable.NumericPredictor as NumericPredictor[];
    expect(numericPredicators.length).toBe(1);

    const numericPredictor: NumericPredictor = numericPredicators[0];

    expect(numericPredictor.name).toBe("age");
    expect(numericPredictor.coefficient).toBe(7.1);
    expect(numericPredictor.exponent).toBe(1);
  });

  test("RegressionModel::RegressionTable::CategoricalPredictor", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);
    const models: Model[] = pmml.models ?? [];
    const regressionModel: RegressionModel = models[0] as RegressionModel;
    const regressionTable: RegressionTable = regressionModel.RegressionTable[0];

    expect(regressionTable.CategoricalPredictor).not.toBeUndefined();

    const categoricalPredicators: CategoricalPredictor[] =
      regressionTable.CategoricalPredictor as CategoricalPredictor[];
    expect(categoricalPredicators.length).toBe(3);

    const categoricalPredicator0: CategoricalPredictor = categoricalPredicators[0];
    expect(categoricalPredicator0.name).toBe("car_location");
    expect(categoricalPredicator0.coefficient).toBe(41.1);
    expect(categoricalPredicator0.value).toBe("carpark");

    const categoricalPredicator1: CategoricalPredictor = categoricalPredicators[1];
    expect(categoricalPredicator1.name).toBe("car_location");
    expect(categoricalPredicator1.coefficient).toBe(325.03);
    expect(categoricalPredicator1.value).toBe("street");

    const categoricalPredicator2: CategoricalPredictor = categoricalPredicators[2];
    expect(categoricalPredicator2.name).toBe("car_location");
    expect(categoricalPredicator2.coefficient).toBe(-500.0);
    expect(categoricalPredicator2.value).toBe("garage");
  });

  test("RegressionModel::RoundTrip1", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_1);
    const xml: string = PMML2XML(pmml);
    expect(xml).not.toBeNull();

    const pmml2: PMML = XML2PMML(xml);
    expect(pmml).toEqual(pmml2);
  });

  test("RegressionModel::RoundTrip2", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_2);
    const xml: string = PMML2XML(pmml);
    expect(xml).not.toBeNull();

    const pmml2: PMML = XML2PMML(xml);
    expect(pmml).toEqual(pmml2);
  });

  test("RegressionModel::RoundTrip3", () => {
    const pmml: PMML = XML2PMML(LINEAR_REGRESSION_MODEL_3);
    const xml: string = PMML2XML(pmml);
    expect(xml).not.toBeNull();

    const pmml2: PMML = XML2PMML(xml);
    expect(pmml).toEqual(pmml2);
  });
});
