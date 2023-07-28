import { ExpressionDefinition } from "@kie-tools/boxed-expression-component/dist/api";
import { beeToDmn } from "../boxedExpressions/beeToDmn";
import { Dispatch } from "../store/Store";

export function updateExpression({
  expression,
  index,
  dispatch: { dmn },
}: {
  expression: ExpressionDefinition;
  index: number;
  dispatch: { dmn: Dispatch["dmn"] };
}) {
  // TODO: Implement
  console.info(`TIAGO WRITE: Boxed Expression updated! ${beeToDmn(expression)}`); // TODO: Actually mutate the DMN JSON.
}
