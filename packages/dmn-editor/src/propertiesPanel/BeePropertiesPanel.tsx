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
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/Store";
import { useCallback, useMemo, useState } from "react";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlHref } from "../xml/xmlHrefs";
import { SingleNodeProperties } from "./SingleNodeProperties";
import {
  BeePanelType,
  CellContent,
  ContextExpressionVariableCell,
  DecisionTableCell,
  ExpressionPath,
  InvocationParameterCell,
  LiteralExpressionCell,
  RelationCell,
  generateBeeMap,
  getBeePropertiesPanel,
  getDmnObject,
} from "../boxedExpressions/getBeeMap";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tInformationItem,
  DMN15__tInputClause,
  DMN15__tLiteralExpression,
  DMN15__tOutputClause,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Form, FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea";
import { TypeRefSelector } from "../dataTypes/TypeRefSelector";
import { useDmnEditor } from "../DmnEditorContext";
import { InlineFeelNameInput } from "../feel/InlineFeelNameInput";
import { AllExpressionTypes, AllExpressionsWithoutTypes } from "../dataTypes/DataTypeSpec";

export function BeePropertiesPanel() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { allFeelVariableUniqueNames, nodesById } = useDmnEditorDerivedStore();
  const { dmnEditorRootElementRef } = useDmnEditor();

  const shouldDisplayDecisionOrBkmProps = useMemo(
    () => selectedObjectId === undefined || (selectedObjectId && selectedObjectId === activeDrgElementId),
    [activeDrgElementId, selectedObjectId]
  );

  const node = useMemo(
    () => (activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined),
    [activeDrgElementId, nodesById]
  );
  const isReadonly = !!node?.data.dmnObjectNamespace && node.data.dmnObjectNamespace !== thisDmnsNamespace;

  const expression = useMemo(() => {
    if (!node?.data.dmnObject) {
      return undefined;
    }
    if (node?.data.dmnObject.__$$element === "businessKnowledgeModel") {
      return node.data.dmnObject.encapsulatedLogic?.expression;
    }
    if (node?.data.dmnObject.__$$element === "decision") {
      return node.data.dmnObject.expression;
    }
  }, [node?.data.dmnObject]);

  const beeMap = useMemo(() => (expression ? generateBeeMap(expression, new Map(), []) : undefined), [expression]);

  const selectedObjectInfos = useMemo(() => beeMap?.get(selectedObjectId ?? ""), [beeMap, selectedObjectId]);

  const selectedObjectPath = useMemo(
    () => selectedObjectInfos?.expressionPath[selectedObjectInfos.expressionPath.length - 1],
    [selectedObjectInfos?.expressionPath]
  );

  const propertiesPanel = useMemo(() => {
    if (!selectedObjectPath) {
      return;
    }
    return getBeePropertiesPanel(selectedObjectPath);
  }, [selectedObjectPath]);

  const updateDmnObject = useCallback((dmnObject: AllExpressionsWithoutTypes, newContent: CellContent) => {
    switch (newContent.type) {
      case "literalExpression":
        (dmnObject as DMN15__tLiteralExpression).text = newContent.text;
        break;
      case "context":
        if (newContent["@_name"]) {
          (dmnObject as DMN15__tInformationItem)["@_name"] = newContent["@_name"];
        }
        if (newContent["@_typeRef"]) {
          (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent["@_typeRef"];
        }
        break;
      case "decisionTable":
        if (newContent.cell === "rule") {
          (dmnObject as DMN15__tUnaryTests | DMN15__tLiteralExpression).text = newContent.text;
        }
        if (newContent.cell === "outputHeader") {
          if (newContent["@_name"]) {
            (dmnObject as DMN15__tOutputClause)["@_name"] = newContent["@_name"];
          }
          if (newContent["@_typeRef"]) {
            (dmnObject as DMN15__tOutputClause)["@_typeRef"] = newContent["@_typeRef"];
          }
        }
        if (newContent.cell === "inputHeader") {
          if (newContent.text) {
            (dmnObject as DMN15__tLiteralExpression).text = newContent.text;
          }
          if (newContent["@_typeRef"]) {
            (dmnObject as DMN15__tLiteralExpression)["@_typeRef"] = newContent["@_typeRef"];
          }
        }
        break;
      case "relation":
        if (newContent.cell === "header") {
          if (newContent["@_name"]) {
            (dmnObject as DMN15__tInformationItem)["@_name"] = newContent["@_name"];
          }
          if (newContent["@_typeRef"]) {
            (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent["@_typeRef"];
          }
        }
        if (newContent.cell === "content") {
          (dmnObject as DMN15__tLiteralExpression).text = newContent.text;
        }
        break;
      case "invocation":
        if (newContent["@_name"]) {
          (dmnObject as DMN15__tInformationItem)["@_name"] = newContent["@_name"];
        }
        if (newContent["@_typeRef"]) {
          (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent["@_typeRef"];
        }
        break;
    }
  }, []);

  const updateBee = useCallback(
    (newContent: CellContent, expressionPath = selectedObjectInfos?.expressionPath) => {
      dmnEditorStoreApi.setState((state) => {
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "businessKnowledgeModel") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tBusinessKnowledgeModel)
              ?.encapsulatedLogic?.expression
          );
          dmnObject && updateDmnObject(dmnObject, newContent);
        }
        if (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0]?.__$$element === "decision") {
          const dmnObject = getDmnObject(
            expressionPath ?? [],
            (state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tDecision)?.expression
          );
          dmnObject && updateDmnObject(dmnObject, newContent);
        }
      });
    },
    [dmnEditorStoreApi, node?.data.index, selectedObjectInfos?.expressionPath, updateDmnObject]
  );

  return (
    <>
      {node && (
        <DrawerPanelContent
          isResizable={true}
          minSize={"300px"}
          defaultSize={"500px"}
          onKeyDown={(e) => e.stopPropagation()} // This prevents ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
        >
          <DrawerHead>
            {shouldDisplayDecisionOrBkmProps && <SingleNodeProperties nodeId={node.id} />}
            {!shouldDisplayDecisionOrBkmProps && selectedObjectId === "" && <></>}
            <DrawerActions>
              <DrawerCloseButton
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.boxedExpressionEditor.propertiesPanel.isOpen = false;
                  });
                }}
              />
            </DrawerActions>
            <Form>
              {/* 
                fix bug on unique names - decision table input
                add ID
                add question
                add description
              */}
              {propertiesPanel?.type === BeePanelType.NAME_TYPE && (
                <FormSection title={propertiesPanel?.title ?? ""}>
                  <FormGroup label="Name">
                    <InlineFeelNameInput
                      enableAutoFocusing={false}
                      isPlain={false}
                      id={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_id"] ?? ""}
                      name={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_name"] ?? ""}
                      isReadonly={isReadonly}
                      shouldCommitOnBlur={true}
                      className={"pf-c-form-control"}
                      onRenamed={(newContent) => {
                        switch (selectedObjectPath!.type) {
                          case "context":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_name": newContent,
                            } as ContextExpressionVariableCell);
                          case "decisionTable":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_name": newContent,
                              cell: "outputHeader",
                            } as DecisionTableCell);
                          case "relation":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_name": newContent,
                              cell: "header",
                            } as RelationCell);
                          case "invocation":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_name": newContent,
                            } as InvocationParameterCell);
                          default:
                            return;
                        }
                      }}
                      allUniqueNames={allFeelVariableUniqueNames}
                    />
                  </FormGroup>
                  <FormGroup label="Data type">
                    <TypeRefSelector
                      heightRef={dmnEditorRootElementRef}
                      typeRef={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_typeRef"]}
                      isDisabled={isReadonly}
                      onChange={(newTypeRef) => {
                        switch (selectedObjectPath!.type) {
                          case "context":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_typeRef": newTypeRef,
                            } as ContextExpressionVariableCell);
                          case "decisionTable":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_typeRef": newTypeRef,
                              cell: "outputHeader",
                            } as DecisionTableCell);
                          case "relation":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_typeRef": newTypeRef,
                              cell: "header",
                            } as RelationCell);
                          case "invocation":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_typeRef": newTypeRef,
                            } as InvocationParameterCell);
                          default:
                            return;
                        }
                      }}
                    />
                  </FormGroup>
                </FormSection>
              )}
              {propertiesPanel?.type === BeePanelType.TEXT && (
                <FormSection title={propertiesPanel?.title ?? ""}>
                  <FormGroup label="Content">
                    <CellContentTextArea
                      initialValue={(selectedObjectInfos?.cell as DMN15__tLiteralExpression)?.text?.__$$text ?? ""}
                      type={selectedObjectPath!.type}
                      onChange={updateBee}
                      expressionPath={selectedObjectInfos?.expressionPath ?? []}
                      isReadonly={isReadonly}
                    />
                  </FormGroup>
                </FormSection>
              )}
              {propertiesPanel?.type === BeePanelType.DECISION__TABLE_INPUT_HEADER && (
                <FormSection title={propertiesPanel?.title ?? ""}>
                  <FormGroup label="Name">
                    <InlineFeelNameInput
                      enableAutoFocusing={false}
                      isPlain={false}
                      id={(selectedObjectInfos?.cell as DMN15__tInputClause)?.inputExpression["@_id"] ?? ""}
                      name={(selectedObjectInfos?.cell as DMN15__tInputClause)?.inputExpression.text?.__$$text ?? ""}
                      isReadonly={isReadonly}
                      shouldCommitOnBlur={true}
                      className={"pf-c-form-control"}
                      onRenamed={(newContent) => {
                        switch (selectedObjectPath!.type) {
                          case "decisionTable":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              text: { __$$text: newContent },
                              cell: "inputHeader",
                            } as DecisionTableCell);
                          default:
                            return;
                        }
                      }}
                      allUniqueNames={allFeelVariableUniqueNames}
                    />
                  </FormGroup>
                  <FormGroup label="Data type">
                    <TypeRefSelector
                      heightRef={dmnEditorRootElementRef}
                      typeRef={(selectedObjectInfos?.cell as DMN15__tInputClause)?.inputExpression["@_typeRef"]}
                      isDisabled={isReadonly}
                      onChange={(newTypeRef) => {
                        switch (selectedObjectPath!.type) {
                          case "decisionTable":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              "@_typeRef": newTypeRef,
                              cell: "inputHeader",
                            } as DecisionTableCell);
                          default:
                            return;
                        }
                      }}
                    />
                  </FormGroup>
                </FormSection>
              )}
            </Form>
          </DrawerHead>
        </DrawerPanelContent>
      )}
    </>
  );
}

function CellContentTextArea(props: {
  initialValue: string;
  type: AllExpressionTypes;
  onChange: (cellContent: CellContent, expressionPath: ExpressionPath[]) => void;
  expressionPath: ExpressionPath[];
  isReadonly: boolean;
}) {
  // used to save the expression path value until the flush operation
  const [expressionPath, setExpressionPath] = useState(props.expressionPath);
  const [textAreaValue, setTextAreaValue] = useState("");
  const [isEditing, setEditing] = useState(false);

  React.useEffect(() => {
    if (!isEditing) {
      setTextAreaValue(props.initialValue);
    }
  }, [props.initialValue, isEditing]);

  React.useEffect(() => {
    if (!isEditing) {
      setExpressionPath(props.expressionPath);
    }
  }, [props.expressionPath, isEditing]);

  return (
    <>
      <TextArea
        aria-label={"Content"}
        type={"text"}
        isDisabled={props.isReadonly}
        value={textAreaValue}
        onChange={(newContent) => {
          setTextAreaValue(newContent);
          setEditing(true);
        }}
        onBlur={() => {
          switch (props.type) {
            case "literalExpression":
              props.onChange(
                {
                  type: props.type,
                  text: { __$$text: textAreaValue },
                } as LiteralExpressionCell,
                expressionPath
              );
              break;
            case "decisionTable":
              props.onChange(
                {
                  type: props.type,
                  text: { __$$text: textAreaValue },
                  cell: "rule",
                } as DecisionTableCell,
                expressionPath
              );
              break;
            case "relation":
              props.onChange(
                {
                  type: props.type,
                  text: { __$$text: textAreaValue },
                  cell: "content",
                } as RelationCell,
                expressionPath
              );
              break;
            default:
              break;
          }
          setEditing(false);
        }}
        placeholder={"Enter the expression content..."}
        style={{ resize: "vertical", minHeight: "40px" }}
        rows={6}
      />
    </>
  );
}
