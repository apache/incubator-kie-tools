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
  AllCellsWithPartialContent,
  BeePropertiesPanelComponent,
  generateBeeMap,
  getBeePropertiesPanel,
  getDmnObject,
} from "../boxedExpressions/getBeeMap";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tContext,
  DMN15__tDecision,
  DMN15__tFunctionDefinition,
  DMN15__tInformationItem,
  DMN15__tInputClause,
  DMN15__tLiteralExpression,
  DMN15__tOutputClause,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Form, FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { useDmnEditor } from "../DmnEditorContext";
import { AllExpressionsWithoutTypes } from "../dataTypes/DataTypeSpec";
import { ClipboardCopy } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { InformationItemCell } from "./BeePropertiesPanelComponents.tsx/InformationItemCell";
import { DecisionTableInputHeaderCell } from "./BeePropertiesPanelComponents.tsx/DecisionTableInputHeaderCell";
import { DecisionTableOutputHeaderCell } from "./BeePropertiesPanelComponents.tsx/DecisionTableOutputHeaderCell";
import { FunctionDefinitionParameterCell } from "./BeePropertiesPanelComponents.tsx/FunctionDefinitionParametersCell";
import { LiteralExpressionContentCell } from "./BeePropertiesPanelComponents.tsx/LiteralExpressionContentCell";
import { DmnBuiltInDataType } from "@kie-tools/boxed-expression-component/dist/api";
import { ExpressionRootCell } from "./BeePropertiesPanelComponents.tsx/ExpressionRoot";
import { UnaryTestCell } from "./BeePropertiesPanelComponents.tsx/UnaryTestCell";

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

  /**
   * fix bug on unique names - decision table input
    fix focus???
   */

  // TODO: CHANGE TO IF/ELSE = REMOVE SWITCH!
  // TODO CHECK ALL CASES! MISSING
  const updateDmnObject = useCallback(
    (dmnObject: AllExpressionsWithoutTypes, newContent: AllCellsWithPartialContent) => {
      if (newContent.type === "context") {
        if (newContent.cell === BeePropertiesPanelComponent.INFORMATION_ITEM_CELL) {
          if (newContent.content?.["@_name"]) {
            (dmnObject as DMN15__tInformationItem)["@_name"] = newContent.content["@_name"];
          }
          if (newContent.content?.["@_typeRef"]) {
            (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent.content["@_typeRef"];
          }
        }
      }
      if (newContent.type === "decisionTable") {
        if (newContent.cell === BeePropertiesPanelComponent.UNARY_TEST && newContent.content?.text) {
          (dmnObject as DMN15__tUnaryTests).text = newContent.content.text;
        }
        if (newContent.cell === BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT && newContent.content?.text) {
          (dmnObject as DMN15__tLiteralExpression).text = newContent.content.text;
        }
        if (newContent.cell === BeePropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER) {
          if (newContent.content?.["@_name"]) {
            (dmnObject as DMN15__tOutputClause)["@_name"] = newContent.content["@_name"];
          }
          if (newContent.content?.["@_typeRef"]) {
            (dmnObject as DMN15__tOutputClause)["@_typeRef"] = newContent.content["@_typeRef"];
          }
        }
        if (newContent.cell === BeePropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER) {
          if (newContent.content?.inputExpression?.text) {
            (dmnObject as DMN15__tInputClause).inputExpression.text = newContent.content.inputExpression.text;
          }
          if (newContent.content?.inputExpression?.["@_typeRef"]) {
            (dmnObject as DMN15__tInputClause).inputExpression["@_typeRef"] =
              newContent.content.inputExpression?.["@_typeRef"];
          }
        }
      }
      if (newContent.type === "functionDefinition") {
        // TODO
      }
      if (newContent.type === "invocation") {
        if (newContent.cell === BeePropertiesPanelComponent.INFORMATION_ITEM_CELL) {
          if (newContent.content?.["@_name"]) {
            (dmnObject as DMN15__tInformationItem)["@_name"] = newContent.content["@_name"];
          }
          if (newContent.content?.["@_typeRef"]) {
            (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent.content["@_typeRef"];
          }
        }
      }
      if (newContent.type === "literalExpression") {
        if (newContent.cell === BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT) {
          if (newContent.content?.text) {
            (dmnObject as DMN15__tLiteralExpression).text = newContent.content.text;
          }
        }
      }
      if (newContent.type === "relation") {
        if (newContent.cell === BeePropertiesPanelComponent.INFORMATION_ITEM_CELL) {
          if (newContent.content?.["@_name"]) {
            (dmnObject as DMN15__tInformationItem)["@_name"] = newContent.content["@_name"];
          }
          if (newContent.content?.["@_typeRef"]) {
            (dmnObject as DMN15__tInformationItem)["@_typeRef"] = newContent.content["@_typeRef"];
          }
        }
        if (newContent.cell === BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT && newContent.content) {
          (dmnObject as DMN15__tLiteralExpression).text = newContent.content.text;
        }
      }
    },
    []
  );

  const updateBee = useCallback(
    (newContent: AllCellsWithPartialContent, expressionPath = selectedObjectInfos?.expressionPath) => {
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
              <FormGroup label="ID">
                <ClipboardCopy isReadOnly={true} hoverTip="Copy" clickTip="Copied">
                  {selectedObjectId}
                </ClipboardCopy>
              </FormGroup>
              <FormSection title={propertiesPanel?.title ?? ""}>
                {propertiesPanel?.component === BeePropertiesPanelComponent.EXPRESSION_ROOT && (
                  <ExpressionRootCell
                    isReadonly={isReadonly}
                    expressionPath={selectedObjectInfos?.expressionPath ?? []}
                    description={
                      (selectedObjectInfos?.cell as Pick<DMN15__tContext, "@_label" | "description">)?.description
                        ?.__$$text ?? ""
                    }
                    onChangeDescription={(newDescription: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.EXPRESSION_ROOT,
                        content: { description: { __$$text: newDescription } },
                      })
                    }
                    label={
                      (selectedObjectInfos?.cell as Pick<DMN15__tContext, "@_label" | "description">)?.["@_label"] ?? ""
                    }
                    onChangeLabel={(newLabel: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.EXPRESSION_ROOT,
                        content: { "@_label": newLabel },
                      })
                    }
                  />
                )}
                {propertiesPanel?.component === BeePropertiesPanelComponent.INFORMATION_ITEM_CELL && (
                  <InformationItemCell
                    isReadonly={isReadonly}
                    id={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_id"] ?? ""}
                    allUniqueNames={allFeelVariableUniqueNames}
                    dmnEditorRootElementRef={dmnEditorRootElementRef}
                    expressionPath={selectedObjectInfos?.expressionPath ?? []}
                    description={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.description?.__$$text ?? ""}
                    onChangeDescription={(newDescription: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { description: { __$$text: newDescription } },
                      })
                    }
                    label={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_label"] ?? ""}
                    onChangeLabel={(newLabel: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { "@_label": newLabel },
                      })
                    }
                    name={(selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_name"] ?? ""}
                    onChangeName={(newName: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { "@_name": newName },
                      })
                    }
                    typeRef={
                      (selectedObjectInfos?.cell as DMN15__tInformationItem)?.["@_typeRef"] ??
                      DmnBuiltInDataType.Undefined
                    }
                    onChangeTypeRef={(newTypeRef: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { "@_typeRef": newTypeRef },
                      })
                    }
                  />
                )}

                {propertiesPanel?.component === BeePropertiesPanelComponent.UNARY_TEST && (
                  <UnaryTestCell
                    isReadonly={isReadonly}
                    expressionPath={selectedObjectInfos?.expressionPath ?? []}
                    description={(selectedObjectInfos?.cell as DMN15__tUnaryTests)?.description?.__$$text ?? ""}
                    onChangeDescription={(newDescription: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { description: { __$$text: newDescription } },
                      })
                    }
                    label={(selectedObjectInfos?.cell as DMN15__tUnaryTests)?.["@_label"] ?? ""}
                    onChangeLabel={(newLabel: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { "@_label": newLabel },
                      })
                    }
                    expressionLanguage={
                      (selectedObjectInfos?.cell as DMN15__tUnaryTests)?.["@_expressionLanguage"] ?? ""
                    }
                    onChangeExpressionLanguage={(newExpressionLanguage: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { "@_expressionLanguage": newExpressionLanguage },
                      })
                    }
                    text={(selectedObjectInfos?.cell as DMN15__tUnaryTests)?.text?.__$$text ?? ""}
                    onChangeText={(newText: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { text: { __$$text: newText } },
                      })
                    }
                  />
                )}
                {propertiesPanel?.component === BeePropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER && (
                  <DecisionTableInputHeaderCell
                    isReadonly={isReadonly}
                    expressionPath={selectedObjectInfos?.expressionPath ?? []}
                    description={
                      (selectedObjectInfos?.cell as Pick<DMN15__tInputClause, "@_label" | "description">)?.description
                        ?.__$$text ?? ""
                    }
                    onChangeDescription={(newDescription: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { description: { __$$text: newDescription } },
                      })
                    }
                    label={
                      (selectedObjectInfos?.cell as Pick<DMN15__tInputClause, "@_label" | "description">)?.[
                        "@_label"
                      ] ?? ""
                    }
                    onChangeLabel={(newLabel: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { "@_label": newLabel },
                      })
                    }
                    inputExpression={{
                      description: "",
                      expressionLanguage: "",
                      label: "",
                      text: "",
                      onChangeDescription: () => {},
                      onChangeExpressionLanguage: () => {},
                      onChangeLabel: () => {},
                      onChangeText: () => {},
                    }}
                    inputValues={{
                      description: "",
                      expressionLanguage: "",
                      label: "",
                      text: "",
                      onChangeDescription: () => {},
                      onChangeExpressionLanguage: () => {},
                      onChangeLabel: () => {},
                      onChangeText: () => {},
                    }}
                  />
                )}
                {propertiesPanel?.component === BeePropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER && (
                  <DecisionTableOutputHeaderCell
                    isReadonly={isReadonly}
                    expressionPath={selectedObjectInfos?.expressionPath ?? []}
                    description={
                      (selectedObjectInfos?.cell as Pick<DMN15__tOutputClause, "@_label" | "description">)?.description
                        ?.__$$text ?? ""
                    }
                    onChangeDescription={(newDescription: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { description: { __$$text: newDescription } },
                      })
                    }
                    label={
                      (selectedObjectInfos?.cell as Pick<DMN15__tOutputClause, "@_label" | "description">)?.[
                        "@_label"
                      ] ?? ""
                    }
                    onChangeLabel={(newLabel: string) =>
                      updateBee({
                        type: "context",
                        cell: BeePropertiesPanelComponent.INFORMATION_ITEM_CELL,
                        content: { "@_label": newLabel },
                      })
                    }
                    defaultOutputEntry={{
                      description: "",
                      expressionLanguage: "",
                      label: "",
                      text: "",
                      onChangeDescription: () => {},
                      onChangeExpressionLanguage: () => {},
                      onChangeLabel: () => {},
                      onChangeText: () => {},
                    }}
                    outputValues={{
                      description: "",
                      expressionLanguage: "",
                      label: "",
                      text: "",
                      onChangeDescription: () => {},
                      onChangeExpressionLanguage: () => {},
                      onChangeLabel: () => {},
                      onChangeText: () => {},
                    }}
                  />
                )}
                {/* {propertiesPanel?.component === BeePropertiesPanelComponent.DECISION_TABLE_ROOT && (
                  <DecisionTableRootCell />
                )} */}
                {propertiesPanel?.component === BeePropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS && (
                  <FunctionDefinitionParameterCell
                    formalParameter={(selectedObjectInfos?.cell as DMN15__tFunctionDefinition).formalParameter?.map(
                      (parameter, i) => ({
                        id: parameter["@_id"] ?? "",
                        name: parameter["@_name"] ?? "",
                        typeRef: parameter["@_typeRef"] ?? "",
                        label: parameter["@_label"] ?? "",
                        description: parameter.description?.__$$text ?? "",
                        isReadonly: isReadonly,
                        allUniqueNames: allFeelVariableUniqueNames,
                        dmnEditorRootElementRef,
                        expressionPath: selectedObjectInfos?.expressionPath ?? [],
                        onChangeName: (newName: string) => {},
                        onChangeTypeRef: (newTypeRef: string) => {},
                        onChangeLabel: (newLabel: string) => {},
                        onChangeDescription: (newDescription: string) => {},
                      })
                    )}
                  />
                )}
                {/* {propertiesPanel?.component === BeePropertiesPanelComponent.FUNCTION_DEFINITION_ROOT && (
                  <FunctionDefinitionRootCell />
                )} */}
                {propertiesPanel?.component === BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT && (
                  <LiteralExpressionContentCell
                    isReadonly={isReadonly}
                    expressionPath={selectedObjectInfos?.expressionPath ?? []}
                    description={(selectedObjectInfos?.cell as DMN15__tLiteralExpression)?.description?.__$$text ?? ""}
                    onChangeDescription={(newDescription: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { description: { __$$text: newDescription } },
                      })
                    }
                    label={(selectedObjectInfos?.cell as DMN15__tLiteralExpression)?.["@_label"] ?? ""}
                    onChangeLabel={(newLabel: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { "@_label": newLabel },
                      })
                    }
                    expressionLanguage={
                      (selectedObjectInfos?.cell as DMN15__tLiteralExpression)?.["@_expressionLanguage"] ?? ""
                    }
                    onChangeExpressionLanguage={(newExpressionLanguage: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { "@_expressionLanguage": newExpressionLanguage },
                      })
                    }
                    text={(selectedObjectInfos?.cell as DMN15__tLiteralExpression)?.text?.__$$text ?? ""}
                    onChangeText={(newText: string) =>
                      updateBee({
                        type: "literalExpression",
                        cell: BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT,
                        content: { text: { __$$text: newText } },
                      })
                    }
                  />
                )}
              </FormSection>
            </Form>
          </DrawerHead>
        </DrawerPanelContent>
      )}
    </>
  );
}
