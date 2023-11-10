import * as RF from "reactflow";
import * as React from "react";
import { useCallback } from "react";
import { NodeType } from "./connections/graphStructure";
import { NODE_TYPES } from "./nodes/NodeTypes";
import { DiagramNodesPanel, useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
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
import { DrdSelectorPanel } from "./DrdSelectorPanel";
import { addOrGetDrd, getDefaultDrdName } from "../mutations/addOrGetDrd";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons/bars-icon";
import { DrgNodesPanel } from "./DrgNodesPanel";

export const MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE = "application/kie-dmn-editor--new-node-from-palette";

export function Palette({ pulse }: { pulse: boolean }) {
  const onDragStart = useCallback((event: React.DragEvent, nodeType: NodeType) => {
    event.dataTransfer.setData(MIME_TYPE_FOR_DMN_EDITOR_NEW_NODE_FROM_PALETTE, nodeType);
    event.dataTransfer.effectAllowed = "move";
  }, []);

  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const diagram = useDmnEditorStore((s) => s.diagram);
  const thisDmn = useDmnEditorStore((s) => s.dmn.model);
  const rfStoreApi = RF.useStoreApi();

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

      state.dispatch.diagram.setNodeStatus(state, newNodeId, { selected: true });
    });
  }, [diagram.drdIndex, dmnEditorStoreApi, rfStoreApi]);

  const drd = thisDmn.definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"]?.[diagram.drdIndex];

  return (
    <>
      <RF.Panel position={"top-left"}>
        <aside className={"kie-dmn-editor--drd-selector"}>
          <InlineFeelNameInput
            validate={() => true}
            allUniqueNames={new Map()}
            name={drd?.["@_name"] ?? ""}
            id={diagram.drdIndex + ""}
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
            key={`${diagram.drdSelector.isOpen}`}
            aria-label={"DRD Selector Popover"}
            isVisible={diagram.drdSelector.isOpen}
            shouldOpen={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.drdSelector.isOpen = true;
              });
            }}
            shouldClose={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.drdSelector.isOpen = false;
              });
            }}
            enableFlip={true}
            position={"right-start"}
            hideOnOutsideClick={false}
            bodyContent={<DrdSelectorPanel />}
          >
            <button
              title="DRD selector"
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.drdSelector.isOpen = !state.diagram.drdSelector.isOpen;
                });
              }}
            >
              {`>`}
            </button>
          </Popover>
        </aside>
      </RF.Panel>
      <RF.Panel position={"top-left"} style={{ marginTop: "78px" }}>
        <aside className={`kie-dmn-editor--palette ${pulse ? "pulse" : ""}`}>
          <div
            title="Input Data"
            className={"kie-dmn-editor--palette-button dndnode input-data"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.inputData)}
            draggable={true}
          >
            <InputDataIcon />
          </div>
          <div
            title="Decision"
            className={"kie-dmn-editor--palette-button dndnode decision"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.decision)}
            draggable={true}
          >
            <DecisionIcon />
          </div>
          <div
            title="Business Knowledge Model"
            className={"kie-dmn-editor--palette-button dndnode bkm"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.bkm)}
            draggable={true}
          >
            <BkmIcon />
          </div>
          <div
            title="Knowledge Source"
            className={"kie-dmn-editor--palette-button dndnode knowledge-source"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.knowledgeSource)}
            draggable={true}
          >
            <KnowledgeSourceIcon />
          </div>
          <div
            title="Decision Service"
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
            title="Group"
            className={"kie-dmn-editor--palette-button dndnode group"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.group)}
            draggable={true}
            onClick={groupNodes}
          >
            <GroupIcon />
          </div>
          <div
            title="Text Annotation"
            className={"kie-dmn-editor--palette-button dndnode text-annotation"}
            onDragStart={(event) => onDragStart(event, NODE_TYPES.textAnnotation)}
            draggable={true}
          >
            <TextAnnotationIcon />
          </div>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--drg-panel-toggle"}>
          <Popover
            className={"kie-dmn-editor--drg-popover"}
            key={`${diagram.openNodesPanel === DiagramNodesPanel.DRG_NODES}`}
            aria-label={"DRG Panel"}
            isVisible={diagram.openNodesPanel === DiagramNodesPanel.DRG_NODES}
            shouldOpen={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openNodesPanel = DiagramNodesPanel.DRG_NODES;
              });
            }}
            shouldClose={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openNodesPanel === DiagramNodesPanel.NONE;
              });
            }}
            enableFlip={true}
            position={"right-start"}
            hideOnOutsideClick={false}
            bodyContent={<DrgNodesPanel />}
          >
            <button
              title="DRG nodes"
              className={"kie-dmn-editor--drg-panel-toggle-button"}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.openNodesPanel =
                    state.diagram.openNodesPanel === DiagramNodesPanel.DRG_NODES
                      ? DiagramNodesPanel.NONE
                      : DiagramNodesPanel.DRG_NODES;
                });
              }}
            >
              <BarsIcon size={"sm"} />
            </button>
          </Popover>
        </aside>
        <br />
        <aside className={"kie-dmn-editor--external-nodes-panel-toggle"}>
          <Popover
            className={"kie-dmn-editor--external-nodes-popover"}
            key={`${diagram.openNodesPanel === DiagramNodesPanel.EXTERNAL_NODES}`}
            aria-label={"External Nodes Panel"}
            isVisible={diagram.openNodesPanel === DiagramNodesPanel.EXTERNAL_NODES}
            shouldOpen={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openNodesPanel = DiagramNodesPanel.EXTERNAL_NODES;
              });
            }}
            shouldClose={() => {
              dmnEditorStoreApi.setState((state) => {
                state.diagram.openNodesPanel === DiagramNodesPanel.NONE;
              });
            }}
            enableFlip={true}
            position={"right-start"}
            hideOnOutsideClick={false}
            bodyContent={<ExternalNodesPanel />}
          >
            <button
              title="External nodes"
              className={"kie-dmn-editor--external-nodes-panel-toggle-button"}
              onClick={() => {
                dmnEditorStoreApi.setState((state) => {
                  state.diagram.openNodesPanel =
                    state.diagram.openNodesPanel === DiagramNodesPanel.EXTERNAL_NODES
                      ? DiagramNodesPanel.NONE
                      : DiagramNodesPanel.EXTERNAL_NODES;
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
