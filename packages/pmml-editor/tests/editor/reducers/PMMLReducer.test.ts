/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions, AllActions, PMMLReducer } from "@kogito-tooling/pmml-editor/dist/editor/reducers";
import { Reducer } from "react";
import { HistoryService } from "@kogito-tooling/pmml-editor/dist/editor/history";
import { ValidationRegistry } from "@kogito-tooling/pmml-editor/dist/editor/validation";

const historyService = new HistoryService([]);
const validationRegistry = new ValidationRegistry();
const pmml: PMML = { Header: {}, DataDictionary: { DataField: [] }, version: "" };
const reducer: Reducer<PMML, AllActions> = PMMLReducer(historyService, validationRegistry);

describe("PMMLReducer::Valid actions", () => {
  test("Actions.SetVersion", () => {
    reducer(pmml, {
      type: Actions.SetVersion,
      payload: {
        version: "1.0",
      },
    });

    const updated: PMML = historyService.commit(pmml) as PMML;

    expect(updated).not.toEqual(pmml);
    expect(updated.version).toBe("1.0");
  });
});

describe("PMMLReducer::Invalid actions", () => {
  test("Actions.SetHeaderDescription", () => {
    const updated: PMML = reducer(pmml, {
      type: Actions.SetHeaderDescription,
      payload: {
        description: "description",
      },
    });
    expect(updated).toEqual(pmml);
  });
});
