import * as React from "react";
import * as RF from "reactflow";

import { DMN14__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_4/ts-gen/types";

import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import {
  Form,
  FormFieldGroupExpandable,
  FormFieldGroupHeader,
  FormGroup,
} from "@patternfly/react-core/dist/js/components/Form";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { PficonTemplateIcon } from "@patternfly/react-icons/dist/js/icons/pficon-template-icon";
import { DataSourceIcon } from "@patternfly/react-icons/dist/js/icons/data-source-icon";
import { StyleOptions } from "./StyleOptions";
import { ShapeOptions } from "./ShapeOptions";
import { InputDataProperties } from "./InputDataProperties";
import { DecisionProperties } from "./DecisionProperties";
import { BkmProperties } from "./BkmProperties";
import { DecisionServiceProperties } from "./DecisionServiceProperties";
import { KnowledgeSourceProperties } from "./KnowledgeSourceProperties";
import { TextAnnotationProperties } from "./TextAnnotationProperties";
import { useMemo } from "react";
import "./PropertiesPanel.css";

export type NodeType = "inputData" | "decision" | "bkm" | "decisionService" | "knowledgeSource" | "textAnnotation";

export function SingleNodeProperties({
  dmn,
  setDmn,
  nodeId,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  nodeId: string;
}) {
  const node = useMemo(() => {
    return (
      dmn.definitions.drgElement?.find((s) => s["@_id"] === nodeId) ??
      dmn.definitions.artifact?.find((s) => s["@_id"] === nodeId)
    );
  }, [dmn.definitions.artifact, dmn.definitions.drgElement, nodeId]);

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

export function PropertiesPanel({
  dmn,
  setDmn,
  selectedNodes,
  onClose,
}: {
  dmn: { definitions: DMN14__tDefinitions };
  setDmn: React.Dispatch<React.SetStateAction<{ definitions: DMN14__tDefinitions }>>;
  selectedNodes: string[];
  onClose: () => void;
}) {
  return (
    <DrawerPanelContent isResizable={true} minSize={"300px"} defaultSize={"500px"}>
      <DrawerHead>
        {selectedNodes.length === 1 && (
          <>
            <SingleNodeProperties dmn={dmn} setDmn={setDmn} nodeId={selectedNodes[0]} />
          </>
        )}
        {selectedNodes.length > 1 && (
          <>
            <Form>
              <StyleOptions startExpanded={true} />
            </Form>
          </>
        )}
        {selectedNodes.length <= 0 && (
          <Form>
            <FormFieldGroupExpandable
              isExpanded={true}
              header={
                <FormFieldGroupHeader
                  titleText={{
                    text: (
                      <TextContent>
                        <Text component={TextVariants.h4}>
                          <DataSourceIcon />
                          &nbsp;&nbsp;Global properties
                        </Text>
                      </TextContent>
                    ),
                    id: "properties-panel-shape-options",
                  }}
                />
              }
            >
              <FormGroup label="Name">
                <TextInput
                  aria-label={"Name"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["@_name"]}
                  placeholder={"Enter a name..."}
                />
              </FormGroup>
              <FormGroup label="Description">
                <TextArea
                  aria-label={"Description"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["description"]}
                  placeholder={"Enter a description..."}
                  style={{ resize: "vertical", minHeight: "40px" }}
                  rows={6}
                />
              </FormGroup>

              <br />
              <br />

              <FormGroup label="Namespace">
                <TextInput
                  aria-label={"Namespace"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["@_namespace"]}
                  placeholder={"Enter a namespace..."}
                />
              </FormGroup>
              <FormGroup label="Expression language">
                <TextInput
                  aria-label={"Expression language"}
                  type={"text"}
                  isDisabled={false}
                  value={dmn.definitions["@_expressionLanguage"]}
                  placeholder={"Enter an expression language..."}
                />
              </FormGroup>
              <FormGroup label="ID">
                <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
                  {dmn.definitions["@_id"]}
                </ClipboardCopy>
              </FormGroup>
            </FormFieldGroupExpandable>
          </Form>
        )}
        <DrawerActions>
          <DrawerCloseButton onClick={onClose} />
        </DrawerActions>
      </DrawerHead>
    </DrawerPanelContent>
  );
}
