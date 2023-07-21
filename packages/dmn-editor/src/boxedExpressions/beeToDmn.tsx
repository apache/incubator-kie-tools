import { ExpressionDefinition } from "@kie-tools/boxed-expression-component/dist/api";
import { DmnExpression } from "./DmnExpression";

/** Converts an ExpressionDefinition to a DMN JSON. This convertion is
 *  necessary for historical reasons, as the Boxed Expression Editor was
 *  created prior to the DMN Editor, needing to declare its own model. */
export function beeToDmn(expression: ExpressionDefinition): DmnExpression {
  return {} as any;
}
