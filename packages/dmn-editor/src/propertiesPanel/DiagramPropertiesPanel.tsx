import * as React from "react";

import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Form, FormFieldGroupExpandable, FormFieldGroupHeader } from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { StyleOptions } from "./StyleOptions";
import { ShapeOptions } from "./ShapeOptions";
import { InputDataProperties } from "./InputDataProperties";
import { DecisionProperties } from "./DecisionProperties";
import { BkmProperties } from "./BkmProperties";
import { DecisionServiceProperties } from "./DecisionServiceProperties";
import { KnowledgeSourceProperties } from "./KnowledgeSourceProperties";
import { TextAnnotationProperties } from "./TextAnnotationProperties";
import { useMemo } from "react";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useDmnEditorStore } from "../store/Store";
import { GlobalDiagramProperties } from "./GlobalDiagramProperties";
import "./DiagramPropertiesPanel.css";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tInputData,
  DMN15__tKnowledgeSource,
  DMN15__tTextAnnotation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { NodeIcon } from "../icons/Icons";

export function SingleNodeProperties({ nodeId }: { nodeId: string }) {
  const { nodesById } = useDmnEditorDerivedStore();

  const node = useMemo(() => {
    return nodesById.get(nodeId);
  }, [nodeId, nodesById]);

  if (!node) {
    return <>Node not found: {nodeId}</>;
  }

  const Icon = NodeIcon(getNodeTypeFromDmnObject(node!.data!.dmnObject!));

  return (
    <>
      <Form>
        <FormFieldGroupExpandable
          isExpanded={true}
          header={
            <FormFieldGroupHeader
              style={{ paddingTop: "8px" }}
              titleText={{
                text: (
                  <TextContent>
                    <Text component={TextVariants.h4}>
                      <Flex alignItems={{ default: "alignItemsCenter" }}>
                        <div style={{ width: "40px", height: "40px", marginRight: 0 }}>
                          <Icon />
                        </div>
                        &nbsp;&nbsp;
                        {(() => {
                          switch (node.type) {
                            case NODE_TYPES.inputData:
                              return <>Input</>;
                            case NODE_TYPES.decision:
                              return <>Decision</>;
                            case NODE_TYPES.bkm:
                              return <>Business Knowledge Model</>;
                            case NODE_TYPES.decisionService:
                              return <>Decision service</>;
                            case NODE_TYPES.knowledgeSource:
                              return <>Knowledge source</>;
                            case NODE_TYPES.textAnnotation:
                              return <>Text annotation</>;
                            case NODE_TYPES.group:
                              return <>Group</>;
                            default:
                              throw new Error(`Unknown type of node ${node.type}`);
                          }
                        })()}
                      </Flex>
                    </Text>
                  </TextContent>
                ),
                id: "properties-panel-shape-options",
              }}
            />
          }
        >
          {(() => {
            switch (node.type) {
              case NODE_TYPES.inputData:
                return (
                  <InputDataProperties inputData={node.data!.dmnObject as DMN15__tInputData} index={node.data.index} />
                );
              case NODE_TYPES.decision:
                return (
                  <DecisionProperties decision={node.data!.dmnObject as DMN15__tDecision} index={node.data.index} />
                );
              case NODE_TYPES.bkm:
                return (
                  <BkmProperties bkm={node.data!.dmnObject as DMN15__tBusinessKnowledgeModel} index={node.data.index} />
                );
              case NODE_TYPES.decisionService:
                return (
                  <DecisionServiceProperties
                    decisionService={node.data!.dmnObject as DMN15__tDecisionService}
                    decisionServiceNamespace={node.data.dmnObjectNamespace}
                    index={node.data.index}
                  />
                );
              case NODE_TYPES.knowledgeSource:
                return (
                  <KnowledgeSourceProperties
                    knowledgeSource={node.data!.dmnObject as DMN15__tKnowledgeSource}
                    index={node.data.index}
                  />
                );
              case NODE_TYPES.textAnnotation:
                return (
                  <TextAnnotationProperties
                    textAnnotation={node.data!.dmnObject as DMN15__tTextAnnotation}
                    index={node.data.index}
                  />
                );
              case NODE_TYPES.group:
                return <>Group</>;
              default:
                throw new Error(`Unknown type of node ${(node as any)?.__$$element}`);
            }
          })()}
        </FormFieldGroupExpandable>

        <StyleOptions startExpanded={false} />
        <ShapeOptions startExpanded={false} />
      </Form>
    </>
  );
}

export function DiagramPropertiesPanel() {
  const diagram = useDmnEditorStore((s) => s.diagram);
  const dispatch = useDmnEditorStore((s) => s.dispatch);

  return (
    <DrawerPanelContent
      isResizable={true}
      minSize={"300px"}
      defaultSize={"500px"}
      onKeyDown={(e) => e.stopPropagation()} // This prevents ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
    >
      <DrawerHead>
        {diagram.selectedNodes.length <= 0 && <GlobalDiagramProperties />}
        {diagram.selectedNodes.length === 1 && <SingleNodeProperties nodeId={diagram.selectedNodes[0]} />}
        {diagram.selectedNodes.length > 1 && (
          <>
            <Flex justifyContent={{ default: "justifyContentCenter" }}>
              <TextContent>
                <Text component={TextVariants.h4}>Multiple nodes selected ({diagram.selectedNodes.length})</Text>
              </TextContent>
            </Flex>
            <Form>
              <StyleOptions startExpanded={true} />
            </Form>
          </>
        )}

        <DrawerActions>
          <DrawerCloseButton onClick={() => dispatch.diagram.propertiesPanel.close()} />
        </DrawerActions>
      </DrawerHead>
    </DrawerPanelContent>
  );
}
