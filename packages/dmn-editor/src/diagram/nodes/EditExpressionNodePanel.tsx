import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DmnNodeWithExpression, useDmnEditor } from "../../store/Store";

export function EditExpressionNodePanel(props: { isVisible: boolean; nodeWithExpression: DmnNodeWithExpression }) {
  const { dispatch } = useDmnEditor();

  return (
    <>
      {props.isVisible && (
        <Label
          onClick={() => dispatch.boxedExpression.open(props.nodeWithExpression)}
          className={"kie-dmn-editor--edit-expression-node-panel"}
        >
          Edit
        </Label>
      )}
    </>
  );
}
