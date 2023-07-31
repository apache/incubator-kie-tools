import { ExpressionDefinition } from "@kie-tools/boxed-expression-component/dist/api";
import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";
import { beeToDmn } from "../boxedExpressions/beeToDmn";

export function updateExpression({
  definitions,
  expression,
  index,
}: {
  definitions: DMN14__tDefinitions;
  expression: ExpressionDefinition;
  index: number;
}) {
  // TODO: Implement
  console.info(`TIAGO WRITE: Boxed Expression updated! ${beeToDmn(expression)}`); // TODO: Actually mutate the DMN JSON.
}
