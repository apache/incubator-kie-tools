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
import { Header, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { ROOT } from "../../editor/reducers/PMMLReducer";
import { HistoryService } from "../../editor/history/HistoryProvider";

const service: HistoryService = new HistoryService();

const pmml: PMML = {
  version: "1.0",
  Header: {},
  DataDictionary: {
    DataField: []
  }
};

const header1: Header = {
  copyright: "copyright",
  modelVersion: "1.0",
  description: "description"
};

describe("HistoryProvider", () => {
  test("Mutation applied", () => {
    const updated: PMML = service.mutate(pmml, ROOT, draft => {
      draft.Header = header1;
    });
    expect(updated.Header).toBe(header1);
  });

  test("Mutation undo", () => {
    const updated1: PMML = service.mutate(pmml, ROOT, draft => {
      draft.Header = header1;
    });
    expect(updated1).not.toStrictEqual(pmml);

    const updated2: PMML = service.undo(updated1);
    expect(updated2).toStrictEqual(pmml);
  });

  test("Mutation redo", () => {
    const updated1: PMML = service.mutate(pmml, ROOT, draft => {
      draft.Header = header1;
    });
    expect(updated1).not.toStrictEqual(pmml);

    const updated2: PMML = service.undo(updated1);
    expect(updated2).toStrictEqual(pmml);

    const updated3: PMML = service.redo(updated2);
    expect(updated3).toStrictEqual(updated1);
  });

  test("Mutation undo beyond start", () => {
    const updated1: PMML = service.mutate(pmml, ROOT, draft => {
      draft.Header = header1;
    });
    expect(updated1).not.toStrictEqual(pmml);

    const updated2: PMML = service.undo(updated1);
    expect(updated2).toStrictEqual(pmml);

    const updated3: PMML = service.undo(updated2);
    expect(updated3).toStrictEqual(pmml);
  });

  test("Mutation redo beyond end", () => {
    const updated1: PMML = service.mutate(pmml, ROOT, draft => {
      draft.Header = header1;
    });
    expect(updated1).not.toStrictEqual(pmml);

    const updated2: PMML = service.undo(updated1);
    expect(updated2).toStrictEqual(pmml);

    const updated3: PMML = service.redo(updated2);
    expect(updated3).toStrictEqual(updated1);

    const updated4: PMML = service.redo(updated3);
    expect(updated4).toStrictEqual(updated1);
  });
});
