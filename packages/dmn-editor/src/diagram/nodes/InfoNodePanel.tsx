import * as React from "react";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { InfoIcon } from "@patternfly/react-icons/dist/js/icons/info-icon";
import { useDmnEditorStore } from "../../store/Store";

export function InfoNodePanel(props: { isVisible: boolean }) {
  const { dispatch } = useDmnEditorStore();

  return (
    <>
      {props.isVisible && (
        <div className={"kie-dmn-editor--info-node-panel"}>
          <Label onClick={dispatch.propertiesPanel.open} className={"kie-dmn-editor--info-label"}>
            <InfoIcon style={{ width: "0.7em", height: "0.7em" }} />
          </Label>
        </div>
      )}
    </>
  );
}
