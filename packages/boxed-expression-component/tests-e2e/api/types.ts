import {
  DMN15__tConditional,
  DMN15__tContext,
  DMN15__tDecisionTable,
  DMN15__tFilter,
  DMN15__tFor,
  DMN15__tFunctionDefinition,
  DMN15__tInvocation,
  DMN15__tList,
  DMN15__tLiteralExpression,
  DMN15__tQuantified,
  DMN15__tRelation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type BoxedLiteral = { __$$element: "literalExpression" } & DMN15__tLiteralExpression;
export type BoxedRelation = { __$$element: "relation" } & DMN15__tRelation;
export type BoxedContext = { __$$element: "context" } & DMN15__tContext;
export type BoxedDecisionTable = { __$$element: "decisionTable" } & DMN15__tDecisionTable;
export type BoxedList = { __$$element: "list" } & DMN15__tList;
export type BoxedInvocation = { __$$element: "invocation" } & DMN15__tInvocation;
export type BoxedFunction = { __$$element: "functionDefinition" } & DMN15__tFunctionDefinition;
export type BoxedFor = { __$$element: "for" } & DMN15__tFor;
export type BoxedEvery = { __$$element: "every" } & DMN15__tQuantified;
export type BoxedSome = { __$$element: "some" } & DMN15__tQuantified;
export type BoxedConditional = { __$$element: "conditional" } & DMN15__tConditional;
export type BoxedFilter = { __$$element: "filter" } & DMN15__tFilter;

export type Normalized<T> = WithRequiredDeep<T, "@_id">;

type WithRequiredDeep<T, K extends keyof any> = T extends undefined
  ? T
  : T extends Array<infer U>
    ? Array<WithRequiredDeep<U, K>>
    : { [P in keyof T]: WithRequiredDeep<T[P], K> } & (K extends keyof T
        ? { [P in K]-?: NonNullable<WithRequiredDeep<T[P], K>> }
        : T);
