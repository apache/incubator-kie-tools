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

export function SingleNodeProperties({ nodeId }: { nodeId: string }) {
  const dmn = useDmnEditorStore((s) => s.dmn);

  const { node, index } = useMemo(() => {
    for (let i = 0; i < (dmn.model.definitions.drgElement ?? []).length; i++) {
      const element = (dmn.model.definitions.drgElement ?? [])[i];
      if (element["@_id"] === nodeId) {
        return { node: element, index: i };
      }
    }

    for (let i = 0; i < (dmn.model.definitions.artifact ?? []).length; i++) {
      const element = (dmn.model.definitions.artifact ?? [])[i];
      if (element["@_id"] === nodeId) {
        return { node: element, index: i };
      }
    }

    return { node: undefined, index: -1 };
  }, [dmn.model.definitions.artifact, dmn.model.definitions.drgElement, nodeId]);

  return (
    <>
      <Form>
        <FormFieldGroupExpandable
          isExpanded={true}
          header={
            <FormFieldGroupHeader
              titleText={{
                text: (
                  <TextContent>
                    <Text component={TextVariants.h4}>
                      <PficonTemplateIcon />
                      &nbsp;&nbsp;
                      {(() => {
                        switch (node?.__$$element) {
                          case "inputData":
                            return <>Input</>;
                          case "decision":
                            return <>Decision</>;
                          case "businessKnowledgeModel":
                            return <>Business Knowledge Model</>;
                          case "decisionService":
                            return <>Decision service</>;
                          case "knowledgeSource":
                            return <>Knowledge source</>;
                          case "textAnnotation":
                            return <>Text annotation</>;
                          // case "group":
                          //   return <>Group</>;
                          default:
                            throw new Error(`Unknown type of node ${node?.__$$element}`);
                        }
                      })()}
                    </Text>
                  </TextContent>
                ),
                id: "properties-panel-shape-options",
              }}
            />
          }
        >
          {(() => {
            switch (node?.__$$element) {
              case "inputData":
                return <InputDataProperties inputData={node} index={index} />;
              case "decision":
                return <DecisionProperties decision={node} index={index} />;
              case "businessKnowledgeModel":
                return <BkmProperties bkm={node} index={index} />;
              case "decisionService":
                return <DecisionServiceProperties decisionService={node} index={index} />;
              case "knowledgeSource":
                return <KnowledgeSourceProperties knowledgeSource={node} index={index} />;
              case "textAnnotation":
                return <TextAnnotationProperties textAnnotation={node} index={index} />;
              // case "group":
              //   return <>Group</>;
              default:
                throw new Error("");
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
