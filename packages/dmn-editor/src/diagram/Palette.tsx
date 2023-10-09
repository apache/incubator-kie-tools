import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "./maths/DmnMaths";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { ExternalNodesPanel } from "../externalNodes/ExternalNodesPanel";
import { MigrationIcon } from "@patternfly/react-icons/dist/js/icons/migration-icon";
import {
  BkmIcon,
  DecisionIcon,
  DecisionServiceIcon,
  GroupIcon,
  InputDataIcon,
  KnowledgeSourceIcon,
  TextAnnotationIcon,
} from "../icons/Icons";

export const MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE = "application/kie-dmn-editor--new-node-from-palette";

export function Palette() {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE, nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const diagram = useDmnEditorStore((s) => s.diagram);
  const rfStoreApi = RF.useStoreApi();

  const groupNodes = useCallback(() => {
    dmnEditorStoreApi.setState((state) => {
      if (state.diagram.selectedNodes.length <= 0) {
        return;
      }

      const newNodeId = addStandaloneNode({
        definitions: state.dmn.model.definitions,
        newNode: {
          type: NODE_TYPES.group,
          bounds: getBounds({
            nodes: rfStoreApi.getState().getNodes(),
            padding: CONTAINER_NODES_DESIRABLE_PADDING,
          }),
        },
      });

      state.dispatch.diagram.setNodeStatus(state, newNodeId, { selected: true });
    });
  }, [dmnEditorStoreApi, rfStoreApi]);

  return (
    <>
      <RF.Panel position={"top-left"}>
        <aside className={"kie-dmn-editor--palette"}>
          <button
            title="Input Data"
            className={"kie-dmn-editor--palette-button dndnode input-data"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.inputData)}
            draggable={true}
          >
            <InputDataIcon />
          </button>
          <button
            title="Decision"
            className={"kie-dmn-editor--palette-button dndnode decision"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decision)}
            draggable={true}
          >
            <DecisionIcon />
          </button>
          <button
            title="Business Knowledge Model"
            className={"kie-dmn-editor--palette-button dndnode bkm"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.bkm)}
            draggable={true}
          >
            <BkmIcon />
          </button>
          <button
            title="Knowledge Source"
            className={"kie-dmn-editor--palette-button dndnode knowledge-source"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.knowledgeSource)}
            draggable={true}
          >
            <KnowledgeSourceIcon />
          </button>
          <button
            title="Decision Service"
            className={"kie-dmn-editor--palette-button dndnode decision-service"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decisionService)}
            draggable={true}
          >
            <DecisionServiceIcon />
          </button>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--palette"}>
          <button
            title="Group"
            className={"kie-dmn-editor--palette-button dndnode group"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.group)}
            draggable={true}
            onClick={groupNodes}
          >
            <GroupIcon />
          </button>
          <button
            title="Text Annotation"
            className={"kie-dmn-editor--palette-button dndnode text-annotation"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
            draggable={true}
          >
            <TextAnnotationIcon />
          </button>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--external-nodes-panel-toggle"}>
          <Popover
            className={"kie-dmn-editor--external-nodes-popover"}
            key={`${diagram.externalNodesPanel.isOpen}`}
            aria-label={"ExternalNodes Panel"}
            isVisible={diagram.externalNodesPanel.isOpen}
            shouldOpen={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.externalNodesPanel.isOpen = true;
              });
            }}
            shouldClose={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.externalNodesPanel.isOpen = false;
              });
            }}
            enableFlip={true}
            position={"top-end"}
            hideOnOutsideClick={false}
            bodyContent={<ExternalNodesPanel />}
          >
            <button
              title="External nodes"
              className={"kie-dmn-editor--external-nodes-panel-toggle-button"}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.dispatch.diagram.toggleExternalNodesPanel(state);
                });
              }}
            >
              <MigrationIcon size={"sm"} />
            </button>
          </Popover>
        </aside>
      </RF.Panel>
    </>
  );
}
