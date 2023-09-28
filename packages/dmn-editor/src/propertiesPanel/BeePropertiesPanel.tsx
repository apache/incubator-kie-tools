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
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlHref } from "../xml/xmlHrefs";

export function BeePropertiesPanel() {
  const dispatch = useDmnEditorStore((s) => s.dispatch);
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { nodesById } = useDmnEditorDerivedStore();

  const shouldDisplayDecisionOrBkmProps = useMemo(
    () => selectedObjectId === undefined || (selectedObjectId && selectedObjectId === activeDrgElementId),
    [activeDrgElementId, selectedObjectId]
  );

  const node = useMemo(() => {
    return activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined;
  }, [activeDrgElementId, nodesById]);

  return (
    <>
      <DrawerPanelContent
        isResizable={true}
        minSize={"300px"}
        defaultSize={"500px"}
        onKeyDown={(e) => e.stopPropagation()} // This prevents ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
      >
        <DrawerHead>
          {shouldDisplayDecisionOrBkmProps && <SingleNodeProperties nodeId={node!.id} />}
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
