import { ExpressionDefinition } from "@kie-tools/boxed-expression-component/dist/api";
import { beeToDmn } from "../boxedExpressions/beeToDmn";
import { Dispatch } from "../store/Store";

export function updateExpression({
  dispatch: { dmn },
  expression,
  index,
}: {
  dispatch: { dmn: Dispatch["dmn"] };
  expression: ExpressionDefinition;
  index: number;
}) {
  // TODO: Implement
  console.info(`TIAGO WRITE: Boxed Expression updated! ${beeToDmn(expression)}`); // TODO: Actually mutate the DMN JSON.
}
