import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { DrgElementWithExpression, useDmnEditorStore } from "../../store/Store";

export function EditExpressionNodePanel(props: {
  isVisible: boolean;
  drgElementWithExpression: DrgElementWithExpression;
}) {
  const { dispatch } = useDmnEditorStore();

  return (
    <>
      {props.isVisible && (
        <Label
          onClick={() => dispatch.boxedExpression.open(props.drgElementWithExpression)}
          className={"kie-dmn-editor--edit-expression-node-panel"}
        >
          Edit
        </Label>
      )}
    </>
  );
}
