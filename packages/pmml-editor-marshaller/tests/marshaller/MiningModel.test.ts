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
import { DataDictionary, MiningModel, Model, PMML, XML2PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { MINING_MODEL_1 } from "./TestData_MiningModel";

describe("MiningModel tests", () => {
  test("MiningModel::Basics for DMN", () => {
    const pmml: PMML = XML2PMML(MINING_MODEL_1);

    expect(pmml).not.toBeNull();

    const dataDictionary: DataDictionary = pmml.DataDictionary;
    expect(dataDictionary.numberOfFields).toBe(1);
    expect(dataDictionary.DataField.length).toBe(1);
    expect(dataDictionary.DataField[0].name).toBe("field1");
    expect(dataDictionary.DataField[0].dataType).toBe("double");
    expect(dataDictionary.DataField[0].optype).toBe("continuous");

    expect(pmml.models).not.toBeUndefined();
    const models: Model[] = pmml.models as Model[];
    expect(models.length).toBe(1);
    expect(models[0]).toBeInstanceOf(MiningModel);

    const model: MiningModel = models[0] as MiningModel;
    expect(model.modelName).toBe("name");
    expect(model.MiningSchema.MiningField.length).toBe(1);
    expect(model.MiningSchema.MiningField[0].name).toBe("field1");
  });
});
