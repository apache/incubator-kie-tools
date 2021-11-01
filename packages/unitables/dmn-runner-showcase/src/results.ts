import { EvaluationStatus } from "./unitables";

export const normalResults = [
  [
    {
      decisionId: "",
      decisionName: "StringDecision",
      result: "this is a test",
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const multipleResults = [
  [
    {
      decisionId: "",
      decisionName: "BooleanDecision",
      result: true,
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
    {
      decisionId: "",
      decisionName: "NumberDecision",
      result: 10,
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const arrayResult = [
  [
    {
      decisionId: "",
      decisionName: "Array",
      result: [{ test: "this is a test" }],
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const arrayMultipleResults = [
  [
    {
      decisionId: "",
      decisionName: "Array",
      result: [{ test: "this is a test", fee: "1" }, { test: "second test" }],
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const objectResults = [
  [
    {
      decisionId: "",
      decisionName: "Object",
      result: { test: "abc" },
      messages: [],
      evaluationStatus: EvaluationStatus.SUCCEEDED,
    },
  ],
];

export const skippedResult = [
  [
    {
      decisionId: "",
      decisionName: "Skipped",
      result: null,
      messages: [],
      evaluationStatus: EvaluationStatus.SKIPPED,
    },
  ],
];

export const failedResult = [
  [
    {
      decisionId: "",
      decisionName: "Failed",
      result: null,
      messages: [],
      evaluationStatus: EvaluationStatus.FAILED,
    },
  ],
];
