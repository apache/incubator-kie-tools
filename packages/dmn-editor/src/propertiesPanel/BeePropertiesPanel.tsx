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
import { useCallback, useMemo } from "react";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlHref } from "../xml/xmlHrefs";
import { SingleNodeProperties } from "./SingleNodeProperties";
import {
  BeePanelType,
  CellContent,
  DecisionTableCell,
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
import { AllExpressions } from "../dataTypes/DataTypeSpec";

export function BeePropertiesPanel() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
  const { allFeelVariableUniqueNames, nodesById } = useDmnEditorDerivedStore();
  const { dmnEditorRootElementRef } = useDmnEditor();

  const shouldDisplayDecisionOrBkmProps = useMemo(
    () => selectedObjectId === undefined || (selectedObjectId && selectedObjectId === activeDrgElementId),
    [activeDrgElementId, selectedObjectId]
  );

  const node = useMemo(() => {
    return activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined;
  }, [activeDrgElementId, nodesById]);

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

  const beeMap = useMemo(() => {
    return expression ? generateBeeMap(expression, new Map(), []) : undefined;
  }, [expression]);

  const selectedObjectInfos = useMemo(() => {
    return beeMap?.get(selectedObjectId ?? "");
  }, [beeMap, selectedObjectId]);

  const selectedObjectPath = useMemo(
    () => selectedObjectInfos?.path[selectedObjectInfos.path.length - 1],
    [selectedObjectInfos?.path]
  );

  const propertiesPanel = useMemo(() => {
    if (!selectedObjectPath) {
      return;
    }
    return getBeePropertiesPanel(selectedObjectPath);
  }, [selectedObjectPath]);

  const updateBee = useCallback(
    (newContent: CellContent) => {
      dmnEditorStoreApi.setState((state) => {
        const dmnObject = state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0];

        if (dmnObject?.__$$element === "businessKnowledgeModel") {
          (
            state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tBusinessKnowledgeModel
          ).encapsulatedLogic!.expression = { __$$element: "functionDefinition" };
        }
        if (dmnObject?.__$$element === "decision") {
          const dmnObject = getDmnObject(
            selectedObjectInfos?.path ?? [],
            state.dmn.model.definitions.drgElement?.[node?.data.index ?? 0] as DMN15__tDecision
          );

          switch (newContent.type) {
            case "literalExpression":
              (dmnObject as DMN15__tLiteralExpression).text = newContent.text;
              break;
            case "context":
              (dmnObject as DMN15__tInformationItem)["@_name"] = newContent["@_name"];
              (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent["@_typeRef"];
              break;
            case "decisionTable":
              if (newContent.cell === "rule") {
                (dmnObject as DMN15__tUnaryTests | DMN15__tLiteralExpression).text = newContent.text;
              }
              if (newContent.cell === "outputHeader") {
                (dmnObject as DMN15__tOutputClause)["@_name"] = newContent["@_name"];
                (dmnObject as DMN15__tOutputClause)["@_typeRef"] = newContent["@_typeRef"];
              }
              if (newContent.cell === "inputHeader") {
                (dmnObject as DMN15__tInputClause).inputExpression.text = newContent.inputExpression.text;
                (dmnObject as DMN15__tInputClause).inputExpression["@_typeRef"] =
                  newContent.inputExpression["@_typeRef"];
              }
              break;
            case "relation":
              if (newContent.cell === "header") {
                (dmnObject as DMN15__tInformationItem)["@_name"] = newContent["@_name"];
                (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent["@_typeRef"];
              }
              if (newContent.cell === "content") {
                (dmnObject as DMN15__tLiteralExpression).text = newContent.text;
              }
              break;
            case "invocation":
              (dmnObject as DMN15__tInformationItem)["@_name"] = newContent["@_name"];
              (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent["@_typeRef"];
              break;
          }
        }
      });
    },
    [dmnEditorStoreApi, node?.data.index, selectedObjectInfos?.path]
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
            {!shouldDisplayDecisionOrBkmProps && selectedObjectId === "" && <div></div>}
            {/* {!shouldDisplayDecisionOrBkmProps && selectedObjectId !== "" && (
              <div>{JSON.stringify(beeMap?.get(selectedObjectId ?? ""))}</div>
            )} */}
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
              {propertiesPanel?.panelType === BeePanelType.NAME_TYPE && (
                <FormSection title={propertiesPanel?.title ?? ""}>
                  <FormGroup label="Name">
                    <InlineFeelNameInput
                      enableAutoFocusing={false}
                      isPlain={false}
                      id={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_id"] ?? ""}
                      name={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_name"] ?? ""}
                      isReadonly={false} // TODO: LUIZ
                      shouldCommitOnBlur={true}
                      className={"pf-c-form-control"}
                      onRenamed={(newName) => {
                        // setState((state) => {
                        //   renameDrgElement({
                        //     definitions: state.dmn.model.definitions,
                        //     index,
                        //     newName,
                        //   });
                        // });
                      }}
                      allUniqueNames={allFeelVariableUniqueNames}
                    />
                  </FormGroup>
                  <FormGroup label="Data type">
                    <TypeRefSelector
                      heightRef={dmnEditorRootElementRef}
                      typeRef={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_typeRef"]}
                      onChange={(newTypeRef) => {
                        // setState((state) => {
                        //   const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tDecision;
                        //   drgElement.variable ??= { "@_name": decision["@_name"] };
                        //   drgElement.variable["@_typeRef"] = newTypeRef;
                        // });
                      }}
                    />
                  </FormGroup>
                </FormSection>
              )}
              {propertiesPanel?.panelType === BeePanelType.TEXT && (
                <FormSection title={propertiesPanel?.title ?? ""}>
                  <FormGroup label="Content">
                    <TextArea
                      aria-label={"Content"}
                      type={"text"}
                      isDisabled={false} // TODO: LUIZ
                      value={(selectedObjectInfos?.cell as DMN15__tLiteralExpression)?.text?.__$$text ?? ""}
                      onChange={(newContent) => {
                        switch (selectedObjectPath!.type) {
                          case "literalExpression":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              text: { __$$text: newContent },
                            } as LiteralExpressionCell);
                          case "decisionTable":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              text: { __$$text: newContent },
                              cell: "rule",
                            } as DecisionTableCell);
                          case "relation":
                            return updateBee({
                              type: selectedObjectPath!.type,
                              text: { __$$text: newContent },
                              cell: "content",
                            } as RelationCell);
                          default:
                            return;
                        }
                      }}
                      placeholder={"Enter the expression content..."}
                      style={{ resize: "vertical", minHeight: "40px" }}
                      rows={6}
                    />
                  </FormGroup>
                </FormSection>
              )}
              {propertiesPanel?.panelType === BeePanelType.DECISION__TABLE_INPUT_HEADER && (
                <FormSection title={propertiesPanel?.title ?? ""}>
                  <FormGroup label="Name">
                    <InlineFeelNameInput
                      enableAutoFocusing={false}
                      isPlain={false}
                      id={(selectedObjectInfos?.cell as DMN15__tInputClause)?.inputExpression["@_id"] ?? ""}
                      name={(selectedObjectInfos?.cell as DMN15__tInputClause)?.inputExpression.text?.__$$text ?? ""}
                      isReadonly={false} // TODO: LUIZ
                      shouldCommitOnBlur={true}
                      className={"pf-c-form-control"}
                      onRenamed={(newName) => {
                        // setState((state) => {
                        //   renameDrgElement({
                        //     definitions: state.dmn.model.definitions,
                        //     index,
                        //     newName,
                        //   });
                        // });
                      }}
                      allUniqueNames={allFeelVariableUniqueNames}
                    />
                  </FormGroup>
                  <FormGroup label="Data type">
                    <TypeRefSelector
                      heightRef={dmnEditorRootElementRef}
                      typeRef={(selectedObjectInfos?.cell as DMN15__tInputClause)?.inputExpression["@_typeRef"]}
                      onChange={(newTypeRef) => {
                        // setState((state) => {
                        //   const drgElement = state.dmn.model.definitions.drgElement![index] as DMN15__tDecision;
                        //   drgElement.variable ??= { "@_name": decision["@_name"] };
                        //   drgElement.variable["@_typeRef"] = newTypeRef;
                        // });
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
