import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";

export function EditExpressionNodePanel(props: { isVisible: boolean; onClick: () => void }) {
  return (
    <>
      {props.isVisible && (
        <Label onClick={props.onClick} className={"kie-dmn-editor--edit-expression-node-panel"}>
          Edit
        </Label>
      )}
    </>
  );
}
