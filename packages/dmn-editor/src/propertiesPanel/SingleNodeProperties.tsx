import * as React from "react";
import { useState } from "react";

import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { StyleOptions } from "./StyleOptions";
import { ShapeOptions } from "./ShapeOptions";
import { InputDataProperties } from "./InputDataProperties";
import { DecisionProperties } from "./DecisionProperties";
import { BkmProperties } from "./BkmProperties";
import { DecisionServiceProperties } from "./DecisionServiceProperties";
import { KnowledgeSourceProperties } from "./KnowledgeSourceProperties";
import { TextAnnotationProperties } from "./TextAnnotationProperties";
import { useMemo } from "react";
import { useDmnEditorStoreApi } from "../store/Store";
import { useDmnEditorDerivedStore } from "../store/DerivedStore";
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
import "./SingleNodeProperties.css";

export function SingleNodeProperties({ nodeId }: { nodeId: string }) {
  const dmnEditorStoreApi = useDmnEditorStoreApi();
  const { nodesById } = useDmnEditorDerivedStore();
  const [isSectionExpanded, setSectionExpanded] = useState<boolean>(true);

  const node = useMemo(() => {
    return nodesById.get(nodeId);
  }, [nodeId, nodesById]);

  if (!node) {
    return <>Node not found: {nodeId}</>;
  }

  const Icon = NodeIcon(getNodeTypeFromDmnObject(node!.data!.dmnObject!));

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

        <StyleOptions startExpanded={false} />
        <ShapeOptions startExpanded={false} />
      </FormSection>
    </Form>
  );
}
