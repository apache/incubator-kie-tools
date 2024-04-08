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
import { useState } from "react";

import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { FontOptions } from "./FontOptions";
import { ShapeOptions } from "./ShapeOptions";
import { InputDataProperties } from "./InputDataProperties";
import { DecisionProperties } from "./DecisionProperties";
import { BkmProperties } from "./BkmProperties";
import { DecisionServiceProperties } from "./DecisionServiceProperties";
import { KnowledgeSourceProperties } from "./KnowledgeSourceProperties";
import { TextAnnotationProperties } from "./TextAnnotationProperties";
import { useMemo } from "react";
import { useDmnEditorStore, useDmnEditorStoreApi } from "../store/StoreContext";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import {
  DMN15__tBusinessKnowledgeModel,
  DMN15__tDecision,
  DMN15__tDecisionService,
  DMN15__tGroup,
  DMN15__tInputData,
  DMN15__tKnowledgeSource,
  DMN15__tTextAnnotation,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { getNodeTypeFromDmnObject } from "../diagram/maths/DmnMaths";
import { NodeIcon } from "../icons/Icons";
import { GroupProperties } from "./GroupProperties";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { PropertiesPanelHeader } from "./PropertiesPanelHeader";
import { UnknownProperties } from "./UnknownProperties";
import { useExternalModels } from "../includedModels/DmnEditorDependenciesContext";
import "./SingleNodeProperties.css";

export function SingleNodeProperties({ nodeId }: { nodeId: string }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { externalModelsByNamespace } = useExternalModels();
  const node = useDmnEditorStore((s) => s.computed(s).getDiagramData(externalModelsByNamespace).nodesById.get(nodeId));
  const [isSectionExpanded, setSectionExpanded] = useState<boolean>(true);
  const isAlternativeInputDataShape = useDmnEditorStore((s) => s.computed(s).isAlternativeInputDataShape());
  const nodeIds = useMemo(() => (node?.id ? [node.id] : []), [node?.id]);

  const Icon = useMemo(() => {
    if (node?.data?.dmnObject === undefined) {
      throw new Error("Icon can't be defined without a DMN object");
    }
    const nodeType = getNodeTypeFromDmnObject(node.data.dmnObject);
    if (nodeType === undefined) {
      throw new Error("Can't determine node icon with undefined node type");
    }
    return NodeIcon({ nodeType, isAlternativeInputDataShape });
  }, [isAlternativeInputDataShape, node?.data.dmnObject]);

  if (!node) {
    return <>Node not found: {nodeId}</>;
  }

  return (
    <Form>
      <FormSection
        className={!isSectionExpanded ? "kie-dmn-editor--single-node-properties-title-colapsed" : ""}
        title={
          <PropertiesPanelHeader
            expands={true}
            fixed={true}
            isSectionExpanded={isSectionExpanded}
            toogleSectionExpanded={() => setSectionExpanded((prev) => !prev)}
            icon={<Icon />}
            title={(() => {
              switch (node.type) {
                case NODE_TYPES.inputData:
                  return "Input";
                case NODE_TYPES.decision:
                  return "Decision";
                case NODE_TYPES.bkm:
                  return "Business Knowledge Model";
                case NODE_TYPES.decisionService:
                  return "Decision Service";
                case NODE_TYPES.knowledgeSource:
                  return "Knowledge Source";
                case NODE_TYPES.textAnnotation:
                  return "Text Annotation";
                case NODE_TYPES.group:
                  return "Group";
                case NODE_TYPES.unknown:
                  return <>Unknown</>;
                default:
                  throw new Error(`Unknown type of node ${node.type}`);
              }
            })()}
            action={
              <Button
                title={"Close"}
                variant={ButtonVariant.plain}
                onClick={() => {
                  dmnEditorStoreApi.setState((state) => {
                    state.boxedExpressionEditor.propertiesPanel.isOpen = false;
                    state.diagram.propertiesPanel.isOpen = false;
                  });
                }}
              >
                <TimesIcon />
              </Button>
            }
          />
        }
      >
        {isSectionExpanded && (
          <>
            {/* TODO: LUIZ */}
            <FormSection style={{ paddingLeft: "20px" }}>
              {(() => {
                switch (node.type) {
                  case NODE_TYPES.inputData:
                    return (
                      <InputDataProperties
                        inputData={node.data!.dmnObject as DMN15__tInputData}
                        namespace={node.data.dmnObjectNamespace}
                        index={node.data.index}
                      />
                    );
                  case NODE_TYPES.decision:
                    return (
                      <DecisionProperties
                        decision={node.data!.dmnObject as DMN15__tDecision}
                        namespace={node.data.dmnObjectNamespace}
                        index={node.data.index}
                      />
                    );
                  case NODE_TYPES.bkm:
                    return (
                      <BkmProperties
                        bkm={node.data!.dmnObject as DMN15__tBusinessKnowledgeModel}
                        namespace={node.data.dmnObjectNamespace}
                        index={node.data.index}
                      />
                    );
                  case NODE_TYPES.decisionService:
                    return (
                      <DecisionServiceProperties
                        decisionService={node.data!.dmnObject as DMN15__tDecisionService}
                        namespace={node.data.dmnObjectNamespace}
                        index={node.data.index}
                      />
                    );
                  case NODE_TYPES.knowledgeSource:
                    return (
                      <KnowledgeSourceProperties
                        knowledgeSource={node.data!.dmnObject as DMN15__tKnowledgeSource}
                        namespace={node.data.dmnObjectNamespace}
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
                    return <GroupProperties group={node.data!.dmnObject as DMN15__tGroup} index={node.data.index} />;
                  case NODE_TYPES.unknown:
                    return <UnknownProperties shape={node.data.shape} dmnElementRefQName={node.data.dmnObjectQName} />;
                  default:
                    throw new Error(`Unknown type of node ${(node as any)?.__$$element}`);
                }
              })()}
            </FormSection>
          </>
        )}

        <FontOptions startExpanded={false} nodeIds={nodeIds} />
        <ShapeOptions
          startExpanded={false}
          nodeIds={nodeIds}
          isDimensioningEnabled={true}
          isPositioningEnabled={true}
        />
      </FormSection>
    </Form>
  );
}
