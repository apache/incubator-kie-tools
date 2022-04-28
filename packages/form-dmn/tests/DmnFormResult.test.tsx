/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { render } from "@testing-library/react";
import { DecisionResult, DmnFormResult, DmnFormResultProps, EvaluationStatus } from "../src";

const props: DmnFormResultProps = {
  results: [],
  differences: [{}],
  locale: "en",
  notificationsPanel: true,
  openExecutionTab: () => {},
};

describe("DmnFormResult tests", () => {
  it("should render the DMNFormResult with the empty state", async () => {
    const { getByText } = render(<DmnFormResult {...props} />);

    expect(getByText("No response")).toMatchSnapshot();
  });

  it("should render the DmnFormResult with one result", async () => {
    const results: DecisionResult[] = [
      {
        decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
        decisionName: "Decision-1",
        result: null,
        messages: [],
        evaluationStatus: EvaluationStatus.FAILED,
      },
    ];

    const { getByText } = render(<DmnFormResult {...props} results={results} />);

    expect(getByText("Decision-1")).toMatchSnapshot();
  });

  it("should render the DmnFormResult with more then one result", async () => {
    const results: DecisionResult[] = [
      {
        decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
        decisionName: "Decision-1",
        result: null,
        messages: [],
        evaluationStatus: EvaluationStatus.FAILED,
      },
      {
        decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
        decisionName: "Decision-2",
        result: null,
        messages: [],
        evaluationStatus: EvaluationStatus.SUCCEEDED,
      },
      {
        decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
        decisionName: "Decision-3",
        result: null,
        messages: [],
        evaluationStatus: EvaluationStatus.SKIPPED,
      },
    ];

    const { getByText } = render(<DmnFormResult {...props} results={results} />);

    expect(getByText("Decision-1")).toMatchSnapshot();
    expect(getByText("Decision-2")).toMatchSnapshot();
    expect(getByText("Decision-3")).toMatchSnapshot();
  });

  it("should render an anchor tag", async () => {
    const results: DecisionResult[] = [
      {
        decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
        decisionName: "Decision-1",
        result: null,
        messages: [],
        evaluationStatus: EvaluationStatus.FAILED,
      },
    ];

    const { getByText } = render(<DmnFormResult {...props} results={results} />);

    expect(getByText("Evaluation failed").tagName).toEqual("A");
    expect(getByText("Evaluation failed")).toMatchSnapshot();
  });

  it("should render an paragraph tag", async () => {
    const results: DecisionResult[] = [
      {
        decisionId: "_9BD7BB23-0B23-488F-8DED-F5462CF89E0B",
        decisionName: "Decision-1",
        result: null,
        messages: [],
        evaluationStatus: EvaluationStatus.FAILED,
      },
    ];

    const { getByText } = render(<DmnFormResult {...props} notificationsPanel={false} results={results} />);

    expect(getByText("Evaluation failed").tagName).toEqual("P");
    expect(getByText("Evaluation failed")).toMatchSnapshot();
  });
});
