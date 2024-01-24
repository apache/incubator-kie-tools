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
import { BeePropertiesPanelComponent, generateBeeMap, getBeePropertiesPanel } from "../boxedExpressions/getBeeMap";
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

export function BeePropertiesPanel() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const { selectedObjectId, activeDrgElementId } = useDmnEditorStore((s) => s.boxedExpressionEditor);
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

  const beeMap = useMemo(() => {
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

    return expression ? generateBeeMap(expression, new Map(), []) : undefined;
  }, [node?.data.dmnObject]);

  const propertiesPanel = useMemo(() => {
    const selectedObjectInfos = beeMap?.get(selectedObjectId ?? "");
    const selectedObjectPath = selectedObjectInfos?.expressionPath[selectedObjectInfos.expressionPath.length - 1];
    if (!selectedObjectPath) {
      return;
    }
    return getBeePropertiesPanel(selectedObjectPath);
  }, [beeMap, selectedObjectId]);

  /**
   * fix bug on unique names
   * fix focus???
   */
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
                <FormSection title={propertiesPanel?.title ?? ""}>
                  {propertiesPanel?.component === BeePropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER && (
                    <DecisionTableInputHeaderCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER && (
                    <DecisionTableOutputHeaderCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.DECISION_TABLE_ROOT && (
                    <DecisionTableRootCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.EXPRESSION_ROOT && (
                    <ExpressionRootCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS && (
                    <FunctionDefinitionParameterCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.FUNCTION_DEFINITION_ROOT && (
                    <FunctionDefinitionRootCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.INFORMATION_ITEM_CELL && (
                    <InformationItemCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.INVOCATION_FUNCTION_CALL && (
                    <InvocationFunctionCallCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT && (
                    <LiteralExpressionContentCell beeMap={beeMap} isReadonly={isReadonly} />
                  )}
                  {propertiesPanel?.component === BeePropertiesPanelComponent.UNARY_TEST && (
                    <UnaryTestCell beeMap={beeMap} isReadonly={isReadonly} />
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
