/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { DrawerPanelContent } from "@patternfly/react-core/dist/js/components/Drawer/DrawerPanelContent";
import { DrawerPanelBody } from "@patternfly/react-core/dist/js/components/Drawer/DrawerPanelBody";
import { GlobalProperties } from "./GlobalProperties";
import { MixedNodesAndEdgesProperties } from "./MixedNodesAndEdgesProperties";
import { MultipleEdgesProperties } from "./MultipleEdgesProperties";
import { MultipleNodeProperties } from "./MultipleNodesProperties";
import { SingleEdgeProperties } from "./SingleEdgeProperties";
import { SingleNodeProperties } from "./SingleNodeProperties";
import { useBpmnEditorStore } from "../store/StoreContext";
import "./PropertiesPanel.css";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

export function PropertiesPanel() {
  const selectedNodesById = useBpmnEditorStore((s) => s.computed(s).getDiagramData().selectedNodesById);
  const selectedEdgesById = useBpmnEditorStore((s) => s.computed(s).getDiagramData().selectedEdgesById);

  return (
    <>
      <DrawerPanelContent
        data-testid={"kie-tools--bpmn-editor--properties-panel-container"}
        isResizable={true}
        minSize={"300px"}
        defaultSize={"500px"}
        onKeyDown={(e) => {
          // In macOS, we can not stopPropagation here because, otherwise, shortcuts are not handled
          // See https://github.com/apache/incubator-kie-issues/issues/1164
          if (!(getOperatingSystem() === OperatingSystem.MACOS && e.metaKey)) {
            // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
            e.stopPropagation();
          }
        }}
      >
        <DrawerPanelBody>
          <>
            {selectedEdgesById.size <= 0 && selectedNodesById.size <= 0 && <GlobalProperties />}
            {selectedEdgesById.size <= 0 && selectedNodesById.size === 1 && <SingleNodeProperties />}
            {selectedEdgesById.size <= 0 && selectedNodesById.size > 1 && <MultipleNodeProperties />}
            {selectedEdgesById.size === 1 && selectedNodesById.size <= 0 && <SingleEdgeProperties />}
            {selectedEdgesById.size > 1 && selectedNodesById.size <= 0 && <MultipleEdgesProperties />}
            {selectedEdgesById.size >= 1 && selectedNodesById.size >= 1 && <MixedNodesAndEdgesProperties />}
          </>
        </DrawerPanelBody>
      </DrawerPanelContent>
    </>
  );
}
