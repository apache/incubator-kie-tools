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
import { getModelIconUrl, getModelName, getModelType, isCollection, isSupportedModelType } from "../../../editor/utils";
import { Scorecard, TreeModel } from "@kogito-tooling/pmml-editor-marshaller";

describe("PMMLUtils::getModelIconUrl", () => {
  test("getModelIconUrl::Undefined", () => {
    expect(getModelIconUrl({})).toContain("card-icon-default.svg");
  });

  test("getModelIconUrl::Scorecard", () => {
    expect(
      getModelIconUrl(
        new Scorecard({
          Characteristics: { Characteristic: [] },
          MiningSchema: { MiningField: [] },
          functionName: "regression",
          baselineMethod: "max"
        })
      )
    ).toContain("card-icon-scorecard.svg");
  });
});

describe("PMMLUtils::getModelName", () => {
  test("getModelName::No Model Name", () => {
    expect(getModelName({})).toBeUndefined();
  });

  test("getModelName::With Model Name", () => {
    expect(getModelName({ modelName: "Name" })).toBe("Name");
  });
});

describe("PMMLUtils::getModelType", () => {
  test("getModelType::Undefined", () => {
    expect(getModelType({})).toBeUndefined();
  });

  test("getModelType::Scorecard", () => {
    expect(
      getModelType(
        new Scorecard({
          Characteristics: { Characteristic: [] },
          MiningSchema: { MiningField: [] },
          functionName: "regression",
          baselineMethod: "max"
        })
      )
    ).toBe("Scorecard");
  });

  test("getModelType::TreeModel", () => {
    expect(
      getModelType(
        new TreeModel({
          MiningSchema: { MiningField: [] },
          functionName: "regression",
          missingValueStrategy: "none",
          Node: {}
        })
      )
    ).toBe("Tree Model");
  });
});

describe("PMMLUtils::isCollection", () => {
  test("isCollection::Undefined value", () => {
    expect(isCollection(undefined)).toBeFalsy();
  });

  test("isCollection::Empty array", () => {
    expect(isCollection([])).toBeFalsy();
  });

  test("isCollection::Non-empty array", () => {
    expect(isCollection(["hello"])).toBeTruthy();
  });
});

describe("PMMLUtils::isSupportedModelType", () => {
  test("isSupportedModelType::Undefined", () => {
    expect(isSupportedModelType({})).toBeFalsy();
  });

  test("isSupportedModelType::Scorecard", () => {
    expect(
      getModelType(
        new Scorecard({
          Characteristics: { Characteristic: [] },
          MiningSchema: { MiningField: [] },
          functionName: "regression",
          baselineMethod: "max"
        })
      )
    ).toBeTruthy();
  });

  test("isSupportedModelType::TreeModel", () => {
    expect(
      isSupportedModelType(
        new TreeModel({
          MiningSchema: { MiningField: [] },
          functionName: "regression",
          missingValueStrategy: "none",
          Node: {}
        })
      )
    ).toBeFalsy();
  });
});
