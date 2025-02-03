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
import { useMemo } from "react";
import { buildXmlHref } from "@kie-tools/dmn-marshaller/dist/xml/xmlHrefs";
import { SingleNodeProperties } from "./SingleNodeProperties";
import { generateBoxedExpressionIndex } from "../boxedExpressions/boxedExpressionIndex";
import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { RelationInformationItemCell } from "./BoxedExpressionPropertiesPanelComponents/RelationInformationItemCell";
import { DecisionTableInputHeaderCell } from "./BoxedExpressionPropertiesPanelComponents/DecisionTableInputHeaderCell";
import { DecisionTableOutputHeaderCell } from "./BoxedExpressionPropertiesPanelComponents/DecisionTableOutputHeaderCell";
import { LiteralExpressionContentCell } from "./BoxedExpressionPropertiesPanelComponents/LiteralExpressionContentCell";
import { ExpressionRootCell } from "./BoxedExpressionPropertiesPanelComponents/ExpressionRootCell";
import { DecisionTableInputRule } from "./BoxedExpressionPropertiesPanelComponents/DecisionTableInputRuleCell";
import { DecisionTableRootCell } from "./BoxedExpressionPropertiesPanelComponents/DecisionTableRootCell";
import { InvocationFunctionCallCell } from "./BoxedExpressionPropertiesPanelComponents/InvocationFunctionCallCell";
import { FunctionDefinitionParameterCell } from "./BoxedExpressionPropertiesPanelComponents/FunctionDefinitionParametersCell";
import { FunctionDefinitionRootCell } from "./BoxedExpressionPropertiesPanelComponents/FunctionDefinitionRootCell";
import { WithoutPropertiesCell } from "./BoxedExpressionPropertiesPanelComponents/WithoutPropertiesCell";
import { DecisionTableOutputRuleCell } from "./BoxedExpressionPropertiesPanelComponents/DecisionTableOutputRuleCell";
import { ContextInformationItemCell } from "./BoxedExpressionPropertiesPanelComponents/ContextInformationItemCell";
import { InvocationInformationItemCell } from "./BoxedExpressionPropertiesPanelComponents/InvocationInformationItemCell";
import { SelectExpressionCell } from "./BoxedExpressionPropertiesPanelComponents/SelectExpressionCell";
import {
  BoxedExpressionPropertiesPanelComponent,
  getBoxedExpressionPropertiesPanelComponent,
} from "./BoxedExpressionPropertiesPanelComponents/getBoxedExpressionPropertiesPanelComponent";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import { drgElementToBoxedExpression } from "../boxedExpressions/BoxedExpressionScreen";
import { IteratorVariableCell } from "./BoxedExpressionPropertiesPanelComponents/IteratorVariableCell";
import { useSettings } from "../settings/DmnEditorSettingsContext";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

export function BoxedExpressionPropertiesPanel() {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const thisDmnsNamespace = useDmnEditorStore((s) => s.dmn.model.definitions["@_namespace"]);
  const selectedObjectId = useDmnEditorStore((s) => s.boxedExpressionEditor.selectedObjectId);
  const activeDrgElementId = useDmnEditorStore((s) => s.boxedExpressionEditor.activeDrgElementId);
  const { externalModelsByNamespace } = useExternalModels();
  const settings = useSettings();

  const shouldDisplayDecisionOrBkmProps = useMemo(
    () => selectedObjectId === undefined || (selectedObjectId && selectedObjectId === activeDrgElementId),
    [activeDrgElementId, selectedObjectId]
  );

  const node = useDmnEditorStore((s) =>
    s
      .computed(s)
      .getDiagramData(externalModelsByNamespace)
      .nodesById.get(buildXmlHref({ id: activeDrgElementId ?? "" }))
  );

  const isReadOnly =
    settings.isReadOnly || (!!node?.data.dmnObjectNamespace && node.data.dmnObjectNamespace !== thisDmnsNamespace);

  const boxedExpressionIndex = useMemo(() => {
    if (node?.data.dmnObject === undefined) {
      return;
    }

    if (
      node.data.dmnObject?.__$$element === "businessKnowledgeModel" ||
      node.data.dmnObject?.__$$element === "decision"
    ) {
      const expression = drgElementToBoxedExpression(node.data.dmnObject);
      return expression ? generateBoxedExpressionIndex(expression, new Map(), []) : undefined;
    }
    return;
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
          data-testid={"kie-tools--dmn-editor--bee-properties-panel-container"}
          isResizable={true}
          minSize={"300px"}
          defaultSize={"500px"}
          onKeyDown={(e) => {
            // In macOS, we can not stopPropagation here because, otherwise, shortcuts are not handled
            // See https://github.com/apache/incubator-kie-issues/issues/1164
            if (!(getOperatingSystem() === OperatingSystem.MACOS && e.metaKey)) {
              // Prevent ReactFlow KeyboardShortcuts from triggering when editing stuff on Properties Panel
              e.stopPropagation();
            }
          }}
        >
          <DrawerHead>
            {shouldDisplayDecisionOrBkmProps && <SingleNodeProperties nodeId={node.id} />}
            <DrawerActions>
              <DrawerCloseButton
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.boxedExpressionEditor.propertiesPanel.isOpen = false;
                  });
                }}
              />
            </DrawerActions>
            {!shouldDisplayDecisionOrBkmProps && (
              <Form>
                <FormSection title={boxedExpressionPropertiesPanelComponent?.title ?? ""}>
                  {(((selectedObjectId === undefined || selectedObjectId === "") &&
                    boxedExpressionPropertiesPanelComponent === undefined) ||
                    boxedExpressionPropertiesPanelComponent?.component ===
                      BoxedExpressionPropertiesPanelComponent.WITHOUT_PROPERTIES_CELL) && <WithoutPropertiesCell />}
                  {selectedObjectId !== "" && boxedExpressionPropertiesPanelComponent === undefined && (
                    <SelectExpressionCell selectedObjectId={selectedObjectId!} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.CONTEXT_INFORMATION_ITEM_CELL && (
                    <ContextInformationItemCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_HEADER && (
                    <DecisionTableInputHeaderCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_INPUT_RULE && (
                    <DecisionTableInputRule boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_HEADER && (
                    <DecisionTableOutputHeaderCell
                      boxedExpressionIndex={boxedExpressionIndex}
                      isReadOnly={isReadOnly}
                    />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_OUTPUT_RULE && (
                    <DecisionTableOutputRuleCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.DECISION_TABLE_ROOT && (
                    <DecisionTableRootCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.EXPRESSION_ROOT && (
                    <ExpressionRootCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_PARAMETERS && (
                    <FunctionDefinitionParameterCell
                      boxedExpressionIndex={boxedExpressionIndex}
                      isReadOnly={isReadOnly}
                    />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.FUNCTION_DEFINITION_ROOT && (
                    <FunctionDefinitionRootCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.INVOCATION_FUNCTION_CALL && (
                    <InvocationFunctionCallCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.INVOCATION_INFORMATION_ITEM_CELL && (
                    <InvocationInformationItemCell
                      boxedExpressionIndex={boxedExpressionIndex}
                      isReadOnly={isReadOnly}
                    />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.ITERATOR_VARIABLE_CELL && (
                    <IteratorVariableCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.LITERAL_EXPRESSION_CONTENT && (
                    <LiteralExpressionContentCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
                  )}
                  {boxedExpressionPropertiesPanelComponent?.component ===
                    BoxedExpressionPropertiesPanelComponent.RELATION_INFORMATION_ITEM_CELL && (
                    <RelationInformationItemCell boxedExpressionIndex={boxedExpressionIndex} isReadOnly={isReadOnly} />
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
