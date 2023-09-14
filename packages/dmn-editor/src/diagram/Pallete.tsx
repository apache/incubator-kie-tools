import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";
import {
  BkmNodeSvg,
  DecisionNodeSvg,
  DecisionServiceNodeSvg,
  GroupNodeSvg,
  InputDataNodeSvg,
  KnowledgeSourceNodeSvg,
  TextAnnotationNodeSvg,
} from "./nodes/NodeSvgs";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { addStandaloneNode } from "../mutations/addStandaloneNode";
import { CONTAINER_NODES_DESIRABLE_PADDING, getBounds } from "./maths/DmnMaths";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { ExternalNodesPanel } from "../externalNodes/ExternalNodesPanel";
import { MigrationIcon } from "@patternfly/react-icons/dist/js/icons/migration-icon";

const radius = 34;
const svgViewboxPadding = Math.sqrt(Math.pow(radius, 2) / 2) - radius / 2; // This lets us create a square that will perfectly fit inside the button circle.

const nodeSvgProps = { width: 200, height: 120, x: 16, y: 48, strokeWidth: 16 };
const nodeSvgViewboxSize = nodeSvgProps.width + 2 * nodeSvgProps.strokeWidth;

export const MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALLETE = "application/kie-dmn-editor--new-node-from-pallete";

export function Pallete() {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALLETE, nodeType);
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

  const toggleExternalNodesPanel = useCallback(() => {
    dmnEditorStoreApi.setState((state) => state.dispatch.diagram.toggleExternalNodesPanel(state));
  }, [dmnEditorStoreApi]);

  return (
    <>
      <RF.Panel position={"top-left"}>
        <aside className={"kie-dmn-editor--pallete"}>
          <button
            className={"kie-dmn-editor--pallete-button dndnode input-data"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.inputData)}
            draggable={true}
          >
            <RoundSvg>
              <InputDataNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode decision"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decision)}
            draggable={true}
          >
            <RoundSvg>
              <DecisionNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode bkm"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.bkm)}
            draggable={true}
          >
            <RoundSvg>
              <BkmNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode knowledge-source"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.knowledgeSource)}
            draggable={true}
          >
            <RoundSvg>
              <KnowledgeSourceNodeSvg {...nodeSvgProps} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode decision-service"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decisionService)}
            draggable={true}
          >
            <RoundSvg>
              <DecisionServiceNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} showSectionLabels={false} />
            </RoundSvg>
          </button>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--pallete"}>
          <button
            className={"kie-dmn-editor--pallete-button dndnode group"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.group)}
            draggable={true}
            onClick={groupNodes}
          >
            <RoundSvg>
              <GroupNodeSvg {...nodeSvgProps} y={12} height={nodeSvgProps.width} strokeDasharray={"28,28"} />
            </RoundSvg>
          </button>
          <button
            className={"kie-dmn-editor--pallete-button dndnode text-annotation"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
            draggable={true}
          >
            <RoundSvg>
              <TextAnnotationNodeSvg {...nodeSvgProps} showPlaceholder={true} />
            </RoundSvg>
          </button>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--external-nodes-panel-toggle"}>
          <Popover
            key={`${diagram.externalNodesPanel.isOpen}`}
            aria-label="ExternalNodes Panel"
            position={"top-end"}
            hideOnOutsideClick={false}
            isVisible={diagram.externalNodesPanel.isOpen}
            enableFlip={true}
            headerContent={<div>External nodes</div>}
            bodyContent={<ExternalNodesPanel />}
          >
            <button className={"kie-dmn-editor--external-nodes-panel-toggle-button"} onClick={toggleExternalNodesPanel}>
              <MigrationIcon size={"sm"} />
            </button>
          </Popover>
        </aside>
      </RF.Panel>
    </>
  );
}

function RoundSvg({ children }: React.PropsWithChildren<{}>) {
  return (
    <svg
      className={"kie-dmn-editor--round-svg-container"}
      viewBox={`0 0 ${nodeSvgViewboxSize} ${nodeSvgViewboxSize}`}
      style={{ padding: `${svgViewboxPadding}px` }}
    >
      {children}
    </svg>
  );
}
