import * as React from "react";

import { DrawerHead, DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer";
import { GlobalDiagramProperties } from "./GlobalDiagramProperties";
import "./DiagramPropertiesPanel.css";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { SingleNodeProperties } from "./SingleNodeProperties";
import { MultipleNodeProperties } from "./MultipleNodeProperties";

export function DiagramPropertiesPanel() {
  const { selectedNodesById } = useDmnEditorDerivedStore();

  return (
    <DrawerPanelContent
      isResizable={true}
      minSize={"300px"}
      defaultSize={"500px"}
      onKeyDown={(e) => e.stopPropagation()} // This prevents ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
    >
      <DrawerHead>
        {selectedNodesById.size <= 0 && <GlobalDiagramProperties />}
        {selectedNodesById.size === 1 && <SingleNodeProperties nodeId={[...selectedNodesById.keys()][0]} />}
        {selectedNodesById.size > 1 && (
          <MultipleNodeProperties nodeIds={[...selectedNodesById.keys()]} size={selectedNodesById.size} />
        )}
      </DrawerHead>
    </DrawerPanelContent>
  );
}
