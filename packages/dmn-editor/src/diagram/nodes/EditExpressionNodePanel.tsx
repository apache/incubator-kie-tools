import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../../store/Store";

export function EditExpressionNodePanel(props: { isVisible: boolean; id: string }) {
  const dispatch = useDmnEditorStore((s) => s.dispatch);
  const dmnEditorStoreApi = useDmnEditorStoreApi();

  return (
    <>
      {props.isVisible && (
        <Label
          onClick={() =>
            dmnEditorStoreApi.setState((state) => {
              dispatch.boxedExpressionEditor.open(state, props.id);
            })
          }
          className={"kie-dmn-editor--edit-expression-node-panel"}
        >
          Edit
        </Label>
      )}
    </>
  );
}
