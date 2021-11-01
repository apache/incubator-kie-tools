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

import { DataType, DecisionTableProps, HitPolicy, LogicType } from "../../../api";
import { fireEvent, render } from "@testing-library/react";
import { flushPromises, usingTestingBoxedExpressionI18nContext, wrapComponentInContext } from "../test-utils";
import * as React from "react";
import { DecisionTableExpression } from "../../../components/DecisionTableExpression";
import { openContextMenu } from "../Table/Table.test";
import * as _ from "lodash";
import { act } from "react-dom/test-utils";

describe("DecisionTableExpression tests", () => {
  test("should show a table with three columns: input, output and annotation, and one row", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        wrapComponentInContext(<DecisionTableExpression logicType={LogicType.DecisionTable} />)
      ).wrapper
    );

    expect(container.querySelector(".decision-table-expression")).toBeTruthy();
    expect(container.querySelector(".decision-table-expression table")).toBeTruthy();
    expect(
      container.querySelectorAll(".decision-table-expression table thead tr:last-of-type th:not(.fixed-column)")
    ).toHaveLength(3);
    expect(container.querySelectorAll(".decision-table-expression table thead tr:last-of-type th.input")).toHaveLength(
      1
    );
    expect(container.querySelectorAll(".decision-table-expression table thead tr:last-of-type th.output")).toHaveLength(
      1
    );
    expect(
      container.querySelectorAll(".decision-table-expression table thead tr:last-of-type th.annotation")
    ).toHaveLength(1);
    expect(container.querySelectorAll(".decision-table-expression table tbody tr")).toHaveLength(1);
  });

  test("should show as default hit policy, unique", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        wrapComponentInContext(<DecisionTableExpression logicType={LogicType.DecisionTable} />)
      ).wrapper
    );

    expect(container.querySelector(".decision-table-expression .selected-hit-policy")).toContainHTML("U");
  });

  test("should show the passed hit policy", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        wrapComponentInContext(
          <DecisionTableExpression logicType={LogicType.DecisionTable} hitPolicy={HitPolicy.First} />
        )
      ).wrapper
    );

    expect(container.querySelector(".decision-table-expression .selected-hit-policy")).toContainHTML("F");
  });

  test("should show as default a row, with empty values, except for input column, whose value is dash symbol", () => {
    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        wrapComponentInContext(<DecisionTableExpression logicType={LogicType.DecisionTable} />)
      ).wrapper
    );

    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")).toHaveLength(3);
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")[0]).toHaveTextContent(
      "-"
    );
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")[1]).toHaveTextContent(
      ""
    );
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")[2]).toHaveTextContent(
      ""
    );
  });

  test("should show the passed input, output and annotations", () => {
    const inputName = "input name";
    const outputName = "output name";
    const annotationName = "annotation name";
    const input = [{ name: inputName, dataType: DataType.Undefined }];
    const output = [{ name: outputName, dataType: DataType.Undefined }];
    const annotations = [{ name: annotationName }];
    const inputEntry = "input entry";
    const outputEntry = "output entry";
    const annotation = "annotation";
    const rules = [{ inputEntries: [inputEntry], outputEntries: [outputEntry], annotationEntries: [annotation] }];

    const { container } = render(
      usingTestingBoxedExpressionI18nContext(
        wrapComponentInContext(
          <DecisionTableExpression
            logicType={LogicType.DecisionTable}
            input={input}
            output={output}
            annotations={annotations}
            rules={rules}
          />
        )
      ).wrapper
    );

    expect(
      container.querySelector(".decision-table-expression table thead tr:last-of-type th.input")
    ).toHaveTextContent(inputName);
    expect(
      container.querySelector(".decision-table-expression table thead tr:last-of-type th.output")
    ).toHaveTextContent(outputName);
    expect(
      container.querySelector(".decision-table-expression table thead tr:last-of-type th.annotation")
    ).toHaveTextContent(annotationName);
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")).toHaveLength(3);
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")[0]).toHaveTextContent(
      inputEntry
    );
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")[1]).toHaveTextContent(
      outputEntry
    );
    expect(container.querySelectorAll(".decision-table-expression table tbody tr td.data-cell")[2]).toHaveTextContent(
      annotation
    );
  });

  test("should append a row, with empty values, except for input column, whose value is dash symbol, when user inserts a row below", async () => {
    const mockedBroadcastDefinition = jest.fn();
    mockBroadcastDefinition(mockedBroadcastDefinition);
    const { container, baseElement } = render(
      wrapComponentInContext(
        usingTestingBoxedExpressionI18nContext(<DecisionTableExpression logicType={LogicType.DecisionTable} />).wrapper
      )
    );

    await openContextMenu(container.querySelector(".decision-table-expression table tbody tr td.counter-cell")!);
    await act(async () => {
      fireEvent.click(
        baseElement.querySelector("[data-ouia-component-id='expression-table-handler-menu-Insert below'] button")!
      );
      await flushPromises();
      jest.runAllTimers();
    });

    expect(mockedBroadcastDefinition).toHaveBeenLastCalledWith(
      expect.objectContaining({
        rules: [
          {
            inputEntries: ["-"],
            outputEntries: [""],
            annotationEntries: [""],
          },
          {
            inputEntries: ["-"],
            outputEntries: [""],
            annotationEntries: [""],
          },
        ],
      })
    );
  });

  function mockBroadcastDefinition(mockedBroadcastDefinition: jest.Mock) {
    window.beeApi = _.extend(window.beeApi || {}, {
      broadcastDecisionTableExpressionDefinition: (definition: DecisionTableProps) =>
        mockedBroadcastDefinition(definition),
    });
  }
});
