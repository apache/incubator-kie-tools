import * as React from "react";
import * as RF from "reactflow";

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
import { useDmnEditor } from "../store/Store";
import "./PropertiesPanel.css";
import { GlobalProperties } from "./GlobalProperties";

export function SingleNodeProperties({ nodeId }: { nodeId: string }) {
  const { dmn } = useDmnEditor();
  const node = useMemo(() => {
    return (
      dmn.model.definitions.drgElement?.find((s) => s["@_id"] === nodeId) ??
      dmn.model.definitions.artifact?.find((s) => s["@_id"] === nodeId)
    );
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
                          // case "association":
                          //   return <>Association</>;
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
                return <InputDataProperties inputData={node} />;
              case "decision":
                return <DecisionProperties decision={node} />;
              case "businessKnowledgeModel":
                return <BkmProperties bkm={node} />;
              case "decisionService":
                return <DecisionServiceProperties decisionService={node} />;
              case "knowledgeSource":
                return <KnowledgeSourceProperties knowledgeSource={node} />;
              case "textAnnotation":
                return <TextAnnotationProperties textAnnotation={node} />;
              // case "group":
              //   return <>Group</>;
              // case "association":
              //   return <>Association</>;
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

export function PropertiesPanel({ selectedNodes, onClose }: { selectedNodes: string[]; onClose: () => void }) {
  const { dmn } = useDmnEditor();
  return (
    <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"500px"}>
      <DrawerHead>
        {selectedNodes.length === 1 && (
          <>
            <SingleNodeProperties nodeId={selectedNodes[0]} />
          </>
        )}
        {selectedNodes.length > 1 && (
          <>
            <Flex justifyContent={{ default: "justifyContentCenter" }}>
              <TextContent>
                <Text component={TextVariants.h4}>Multiple nodes selected ({selectedNodes.length})</Text>
              </TextContent>
            </Flex>
            <Form>
              <StyleOptions startExpanded={true} />
            </Form>
          </>
        )}
        {selectedNodes.length <= 0 && <GlobalProperties />}
        <DrawerActions>
          <DrawerCloseButton onClick={onClose} />
        </DrawerActions>
      </DrawerHead>
    </DrawerPanelContent>
  );
}
