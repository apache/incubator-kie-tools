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
import { useMemo } from "react";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
import { buildXmlHref } from "../xml/xmlHrefs";
import { SingleNodeProperties } from "./SingleNodeProperties";
import {
  BoxedExpressionPropertiesPanelComponent,
  generateBoxedExpressionIndex,
  getBoxedExpressionPropertiesPanelComponent,
} from "../boxedExpressions/getBeeMap";
import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { InformationItemCell } from "./BeePropertiesPanelComponents.tsx/InformationItemCell";
import { DecisionTableInputHeaderCell } from "./BeePropertiesPanelComponents.tsx/DecisionTableInputHeaderCell";
import { DecisionTableOutputHeaderCell } from "./BeePropertiesPanelComponents.tsx/DecisionTableOutputHeaderCell";
import { LiteralExpressionContentCell } from "./BeePropertiesPanelComponents.tsx/LiteralExpressionContentCell";
import { ExpressionRootCell } from "./BeePropertiesPanelComponents.tsx/ExpressionRootCell";
import { UnaryTestCell } from "./BeePropertiesPanelComponents.tsx/UnaryTestCell";
import { AllExpressions } from "../dataTypes/DataTypeSpec";
import { DecisionTableRootCell } from "./BeePropertiesPanelComponents.tsx/DecisionTableRootCell";
import { InvocationFunctionCallCell } from "./BeePropertiesPanelComponents.tsx/InvocationFunctionCallCell";
import { FunctionDefinitionParameterCell } from "./BeePropertiesPanelComponents.tsx/FunctionDefinitionParametersCell";
import { FunctionDefinitionRootCell } from "./BeePropertiesPanelComponents.tsx/FunctionDefinitionRootCell";
import { NoneCell } from "./BeePropertiesPanelComponents.tsx/NoneCell";

export function BeePropertiesPanel() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const activeDrgElementId = useDmnEditorStore((s) => s.boxedExpressionEditor.activeDrgElementId);
  const { nodesById } = useDmnEditorDerivedStore();

  const shouldDisplayDecisionOrBkmProps = useMemo(
    () => selectedObjectId === undefined || (selectedObjectId && selectedObjectId === activeDrgElementId),
    [activeDrgElementId, selectedObjectId]
  );

  const node = useMemo(
    () => (activeDrgElementId ? nodesById.get(buildXmlHref({ id: activeDrgElementId })) : undefined),
    [activeDrgElementId, nodesById]
  );
  const isReadonly = !!node?.data.dmnObjectNamespace && node.data.dmnObjectNamespace !== thisDmnsNamespace;

  const boxedExpressionIndex = useMemo(() => {
    if (!node?.data.dmnObject) {
      return undefined;
    }

    let expression: AllExpressions | undefined;
    if (node?.data.dmnObject.__$$element === "businessKnowledgeModel") {
      expression = { __$$element: "functionDefinition", ...node.data.dmnObject.encapsulatedLogic };
    }
    if (node?.data.dmnObject.__$$element === "decision") {
      expression = node.data.dmnObject.expression;
    }

    return expression ? generateBoxedExpressionIndex(expression, new Map(), []) : undefined;
  }, [node?.data.dmnObject]);

  const boxedExpressionPropertiesPanelComponent = useMemo(() => {
    const selectedObjectInfos = boxedExpressionIndex?.get(selectedObjectId ?? "");
    const selectedObjectPath = selectedObjectInfos?.expressionPath[selectedObjectInfos.expressionPath.length - 1];
    if (!selectedObjectPath) {
      return;
    }
    return getBoxedExpressionPropertiesPanelComponent(selectedObjectPath);
  }, [boxedExpressionIndex, selectedObjectId]);

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
            {!shouldDisplayDecisionOrBkmProps && selectedObjectId !== "" && (
              <Form>
                <FormSection title={boxedExpressionPropertiesPanelComponent?.title ?? ""}>
                  {(boxedExpressionPropertiesPanelComponent === undefined ||
                    boxedExpressionPropertiesPanelComponent?.component ===
                      BoxedExpressionPropertiesPanelComponent.NONE) && <NoneCell />}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER && (
                    <DecisionTableInputHeaderCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER && (
                    <DecisionTableOutputHeaderCell
                      boxedExpressionIndex={boxedExpressionIndex}
                      isReadonly={isReadonly}
                    />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_ROOT && (
                    <DecisionTableRootCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT && (
                    <ExpressionRootCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS && (
                    <FunctionDefinitionParameterCell
                      boxedExpressionIndex={boxedExpressionIndex}
                      isReadonly={isReadonly}
                    />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_ROOT && (
                    <FunctionDefinitionRootCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.INFORMATION_ITEM_CELL && (
                    <InformationItemCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.INVOCATION_FUNCTION_CALL && (
                    <InvocationFunctionCallCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT && (
                    <LiteralExpressionContentCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.UNARY_TEST && (
                    <UnaryTestCell boxedExpressionIndex={boxedExpressionIndex} isReadonly={isReadonly} />
                  )}
                </FormSection>
              </Form>
            )}
          </DrawerHead>
        </DrawerPanelContent>
      )}
    </>
  );
}
