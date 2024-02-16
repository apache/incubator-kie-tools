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

import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";
import { DiagramLhsPanel } from "../store/Store";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "./maths/DmnMaths";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { ExternalNodesPanel } from "../externalNodes/ExternalNodesPanel";
import { MigrationIcon } from "@patternfly/react-icons/dist/js/icons/migration-icon";
import {
  AlternativeInputDataIcon,
  BkmIcon,
  DecisionIcon,
  DecisionServiceIcon,
  GroupIcon,
  InputDataIcon,
  KnowledgeSourceIcon,
  TextAnnotationIcon,
} from "../icons/Icons";
import { DrdSelectorPanel } from "./DrdSelectorPanel";
import { addOrGetDrd, getDefaultDrdName } from "../mutations/addOrGetDrd";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons/bars-icon";
import { DrgNodesPanel } from "./DrgNodesPanel";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { useInViewSelect } from "../responsiveness/useInViewSelect";
import { useDmnEditor } from "../DmnEditorContext";
import { getDrdId } from "./drd/drdId";

export const MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE = "application/kie-dmn-editor--new-node-from-palette";

export function Palette({ pulse }: { pulse: boolean }) {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE, nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  const { dmnEditorRootElementRef } = useDmnEditor();
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmn = useDmnEditorStore((s) => s.dmn.model);
  const rfStoreApi = RF.useStoreApi();
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());

  const groupNodes = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      const selectedNodes = rfStoreApi
        .getState()
        .getNodes()
        .filter((s) => s.selected);

      if (selectedNodes.length <= 0) {
        return;
      }

      const { href: newNodeId } = addStandaloneNode({
        definitions: state.dmn.model.definitions,
        drdIndex: diagram.drdIndex,
        newNode: {
          type: NODE_TYPES.group,
          bounds: getBounds({
            nodes: selectedNodes,
            padding: CONTAINER_NODES_DESIRABLE_PADDING,
          }),
        },
      });

      state.dispatch(state).diagram.setNodeStatus(newNodeId, { selected: true });
    });
  }, [diagram.drdIndex, dmnEditorStoreApi, rfStoreApi]);

  const drd = thisDmn.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[diagram.drdIndex];

  const drdSelectorPopoverRef = React.useRef<HTMLDivElement>(null);
  const nodesPalletePopoverRef = React.useRef<HTMLDivElement>(null);

  const { maxHeight } = useInViewSelect(dmnEditorRootElementRef, nodesPalletePopoverRef);

  return (
    <>
      <RF.Panel position={"top-left"}>
        <aside className={"kie-dmn-editor--drd-selector"} style={{ position: "relative" }}>
          <div ref={drdSelectorPopoverRef} style={{ position: "absolute", left: "56px", height: "100%", zIndex: -1 }} />
          <InlineFeelNameInput
            validate={() => true}
            allUniqueNames={() => new Map()}
            name={drd?.["@_name"] ?? ""}
            prefix={`${diagram.drdIndex + 1}.`}
            id={getDrdId({ drdIndex: diagram.drdIndex })}
            onRenamed={(newName) => {
              dmnEditorStoreApi.setState((state) => {
                const drd = addOrGetDrd({ definitions: state.dmn.model.definitions, drdIndex: diagram.drdIndex });
                drd.diagram["@_name"] = newName;
              });
            }}
            placeholder={getDefaultDrdName({ drdIndex: diagram.drdIndex })}
            isReadonly={false}
            isPlain={true}
            shouldCommitOnBlur={true}
          />
          <Popover
            className={"kie-dmn-editor--drd-selector-popover"}
            key={DiagramLhsPanel.DRD_SELECTOR}
            aria-label={"DRD Selector Popover"}
            isVisible={diagram.openLhsPanel === DiagramLhsPanel.DRD_SELECTOR}
            reference={() => drdSelectorPopoverRef.current!}
            shouldClose={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openLhsPanel = DiagramLhsPanel.NONE;
              });
            }}
            position={"bottom-start"}
            hideOnOutsideClick={false}
            bodyContent={<DrdSelectorPanel />}
          />
          <button
            title={"Select or edit DRD"}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openLhsPanel =
                  state.diagram.openLhsPanel === DiagramLhsPanel.DRD_SELECTOR
                    ? DiagramLhsPanel.NONE
                    : DiagramLhsPanel.DRD_SELECTOR;
              });
            }}
          >
            <CaretDownIcon />
          </button>
        </aside>
      </RF.Panel>
      <RF.Panel position={"top-left"} style={{ marginTop: "78px" }}>
        <div ref={nodesPalletePopoverRef} style={{ position: "absolute", left: 0, height: 0, zIndex: -1 }} />
        <aside className={`kie-dmn-editor--palette ${pulse ? "pulse" : ""}`}>
          <div
            title={"Input Data"}
            className={"kie-dmn-editor--palette-button dndnode input-data"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.inputData)}
            draggable={true}
          >
            {isAlternativeInputDataShape ? <AlternativeInputDataIcon /> : <InputDataIcon />}
          </div>
          <div
            title={"Decision"}
            className={"kie-dmn-editor--palette-button dndnode decision"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decision)}
            draggable={true}
          >
            <DecisionIcon />
          </div>
          <div
            title={"Business Knowledge Model"}
            className={"kie-dmn-editor--palette-button dndnode bkm"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.bkm)}
            draggable={true}
          >
            <BkmIcon />
          </div>
          <div
            title={"Knowledge Source"}
            className={"kie-dmn-editor--palette-button dndnode knowledge-source"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.knowledgeSource)}
            draggable={true}
          >
            <KnowledgeSourceIcon />
          </div>
          <div
            title={"Decision Service"}
            className={"kie-dmn-editor--palette-button dndnode decision-service"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decisionService)}
            draggable={true}
          >
            <DecisionServiceIcon />
          </div>
        </aside>
        <br />
        <aside className={`kie-dmn-editor--palette ${pulse ? "pulse" : ""}`}>
          <div
            title={"Group"}
            className={"kie-dmn-editor--palette-button dndnode group"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.group)}
            draggable={true}
            onClick={groupNodes}
          >
            <GroupIcon />
          </div>
          <div
            title={"Text Annotation"}
            className={"kie-dmn-editor--palette-button dndnode text-annotation"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
            draggable={true}
          >
            <TextAnnotationIcon />
          </div>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--drg-panel-toggle"}>
          {diagram.openLhsPanel === DiagramLhsPanel.DRG_NODES && (
            <div className={"kie-dmn-editor--palette-nodes-popover"} style={{ maxHeight }}>
              <DrgNodesPanel />
            </div>
          )}
          <button
            title={"DRG nodes"}
            className={`kie-dmn-editor--drg-panel-toggle-button ${
              diagram.openLhsPanel === DiagramLhsPanel.DRG_NODES ? "active" : ""
            }`}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openLhsPanel =
                  state.diagram.openLhsPanel === DiagramLhsPanel.DRG_NODES
                    ? DiagramLhsPanel.NONE
                    : DiagramLhsPanel.DRG_NODES;
              });
            }}
          >
            <BarsIcon size={"sm"} />
          </button>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--external-nodes-panel-toggle"}>
          {diagram.openLhsPanel === DiagramLhsPanel.EXTERNAL_NODES && (
            <div className={"kie-dmn-editor--palette-nodes-popover"} style={{ maxHeight }}>
              <ExternalNodesPanel />
            </div>
          )}

          <button
            title={"External nodes"}
            className={`kie-dmn-editor--external-nodes-panel-toggle-button ${
              diagram.openLhsPanel === DiagramLhsPanel.EXTERNAL_NODES ? "active" : ""
            }`}
            onClick={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openLhsPanel =
                  state.diagram.openLhsPanel === DiagramLhsPanel.EXTERNAL_NODES
                    ? DiagramLhsPanel.NONE
                    : DiagramLhsPanel.EXTERNAL_NODES;
              });
            }}
          >
            <MigrationIcon size={"sm"} />
          </button>
        </aside>
      </RF.Panel>
    </>
  );
}
