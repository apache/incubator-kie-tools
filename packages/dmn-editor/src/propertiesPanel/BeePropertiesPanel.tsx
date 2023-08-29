import * as React from "react";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { SingleNodeProperties } from "./DiagramPropertiesPanel";
import { useDmnEditorStore } from "../store/Store";
import { useMemo } from "react";

export function BeePropertiesPanel() {
  const {
    dispatch,
    boxedExpressionEditor: { selectedObjectId, openExpressionId },
  } = useDmnEditorStore();

  const shouldDisplayDecisionOrBkmProps = useMemo(
    () => selectedObjectId === undefined || (selectedObjectId && selectedObjectId === openExpressionId),
    [openExpressionId, selectedObjectId]
  );

  return (
    <>
      <DrawerPanelContent
        isResizable={true}
        minSize={"300px"}
        defaultSize={"500px"}
        onKeyDown={(e) => e.stopPropagation()} // This prevents ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
      >
        <DrawerHead>
          {shouldDisplayDecisionOrBkmProps && <SingleNodeProperties nodeId={openExpressionId!} />}
          {!shouldDisplayDecisionOrBkmProps && selectedObjectId === "" && <div>{`Nothing to show`}</div>}
          {!shouldDisplayDecisionOrBkmProps && selectedObjectId !== "" && <div>{selectedObjectId}</div>}
          <DrawerActions>
            <DrawerCloseButton onClick={() => dispatch.boxedExpressionEditor.propertiesPanel.close()} />
          </DrawerActions>
        </DrawerHead>
      </DrawerPanelContent>
    </>
  );
}
