import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { useDmnEditorStore } from "../../store/Store";

export function EditExpressionNodePanel(props: { isVisible: boolean; id: string }) {
  const { dispatch } = useDmnEditorStore();

  return (
    <>
      {props.isVisible && (
        <Label
          onClick={() => dispatch.boxedExpressionEditor.open(props.id)}
          className={"kie-dmn-editor--edit-expression-node-panel"}
        >
          Edit
        </Label>
      )}
    </>
  );
}
