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

import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import * as React from "react";
import { useMemo } from "react";
import * as RF from "reactflow";
import { BpmnDiagramNodeData, BpmnNodeType } from "../diagram/BpmnDiagramDomain";
import {
  CallActivityIcon,
  DataObjectIcon,
  EndEventIcon,
  GatewayIcon,
  GroupIcon,
  IntermediateCatchEventIcon,
  IntermediateThrowEventIcon,
  LaneIcon,
  StartEventIcon,
  SubProcessIcon,
  TaskIcon,
  TextAnnotationIcon,
  UnknownNodeIcon,
} from "../diagram/nodes/NodeIcons";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../store/StoreContext";
import { assertUnreachable } from "../ts-ext/assertUnreachable";
import { AdHocSubProcessProperties } from "./singleNodeProperties/AdHocSubProcessProperties";
import { BoundaryEventProperties } from "./singleNodeProperties/BoundaryEventProperties";
import { BusinessRuleTaskProperties } from "./singleNodeProperties/BusinessRuleTaskProperties";
import { CallActivityProperties } from "./singleNodeProperties/CallActivityProperties";
import { ComplexGatewayProperties } from "./singleNodeProperties/ComplexGatewayProperties";
import { DataObjectProperties } from "./singleNodeProperties/DataObjectProperties";
import { EndEventProperties } from "./singleNodeProperties/EndEventProperties";
import { EventBasedGatewayProperties } from "./singleNodeProperties/EventBasedGatewayProperties";
import { EventSubProcessProperties } from "./singleNodeProperties/EventSubProcessProperties";
import { ExclusiveGatewayProperties } from "./singleNodeProperties/ExclusiveGatewayProperties";
import { GroupProperties } from "./singleNodeProperties/GroupProperties";
import { InclusiveGatewayProperties } from "./singleNodeProperties/InclusiveGatewayProperties";
import { IntermediateCatchEventProperties } from "./singleNodeProperties/IntermediateCatchEventProperties";
import { IntermediateThrowEventProperties } from "./singleNodeProperties/IntermediateThrowEventProperties";
import { LaneProperties } from "./singleNodeProperties/LaneProperties";
import { ParallelGatewayProperties } from "./singleNodeProperties/ParallelGatewayProperties";
import { ScriptTaskProperties } from "./singleNodeProperties/ScriptTaskProperties";
import { ServiceTaskProperties } from "./singleNodeProperties/ServiceTaskProperties";
import { StartEventProperties } from "./singleNodeProperties/StartEventProperties";
import { SubProcessProperties } from "./singleNodeProperties/SubProcessProperties";
import { TaskProperties } from "./singleNodeProperties/TaskProperties";
import { TextAnnotationProperties } from "./singleNodeProperties/TextAnnotationProperties";
import { TransactionProperties } from "./singleNodeProperties/TransactionProperties";
import { UserTaskProperties } from "./singleNodeProperties/UserTaskProperties";
import { ColumnsIcon } from "@patternfly/react-icons/dist/js/icons/columns-icon";
import { Metadata } from "./metadata/Metadata";
import { useCustomTasks } from "../customTasks/BpmnEditorCustomTasksContextProvider";
import { useBpmnEditorI18n } from "../i18n";

export function SingleNodeProperties() {
  const { i18n, locale } = useBpmnEditorI18n();
  const selectedNode = useBpmnEditorStore(
    (s) =>
      [...s.computed(s).getDiagramData().selectedNodesById.values()][0] as
        | undefined
        | RF.Node<BpmnDiagramNodeData, BpmnNodeType>
  );

  const { customTasks } = useCustomTasks();

  const { properties } = useMemo(() => {
    const bpmnElement = selectedNode?.data.bpmnElement;
    const e = bpmnElement?.__$$element;
    switch (e) {
      // Events
      case "startEvent":
        return {
          properties: <StartEventProperties startEvent={bpmnElement!} />,
        };
      case "endEvent":
        return {
          properties: <EndEventProperties endEvent={bpmnElement!} />,
        };
      case "intermediateCatchEvent":
        return {
          properties: <IntermediateCatchEventProperties intermediateCatchEvent={bpmnElement!} />,
        };
      case "intermediateThrowEvent":
        return {
          properties: <IntermediateThrowEventProperties intermediateThrowEvent={bpmnElement!} />,
        };
      case "boundaryEvent":
        return {
          properties: <BoundaryEventProperties boundaryEvent={bpmnElement!} />,
        };
      // Gateways
      case "complexGateway":
        return {
          properties: <ComplexGatewayProperties complexGateway={bpmnElement!} />,
        };
      case "eventBasedGateway":
        return {
          properties: <EventBasedGatewayProperties eventBasedGateway={bpmnElement!} />,
        };
      case "exclusiveGateway":
        return {
          properties: <ExclusiveGatewayProperties exclusiveGateway={bpmnElement!} />,
        };
      case "inclusiveGateway":
        return {
          properties: <InclusiveGatewayProperties inclusiveGateway={bpmnElement!} />,
        };
      case "parallelGateway":
        return {
          properties: <ParallelGatewayProperties parallelGateway={bpmnElement!} />,
        };
      // Tasks
      case "task":
        for (const ct of customTasks ?? []) {
          if (ct.matches(bpmnElement!)) {
            const CustomTaskProperties = ct.propertiesPanelComponent;
            return {
              properties: <CustomTaskProperties task={bpmnElement!} />,
            };
          }
        }
        return {
          properties: <TaskProperties task={bpmnElement!} />,
        };
      case "businessRuleTask":
        return {
          properties: <BusinessRuleTaskProperties businessRuleTask={bpmnElement!} />,
        };

      case "scriptTask":
        return {
          properties: <ScriptTaskProperties scriptTask={bpmnElement!} />,
        };
      case "serviceTask":
        return {
          properties: <ServiceTaskProperties serviceTask={bpmnElement!} />,
        };
      case "userTask":
        return {
          properties: <UserTaskProperties userTask={bpmnElement!} />,
        };
      case "callActivity":
        return {
          properties: <CallActivityProperties callActivity={bpmnElement!} />,
        };
      // Sub-processes
      case "subProcess":
        if (bpmnElement?.["@_triggeredByEvent"]) {
          return {
            properties: <EventSubProcessProperties eventSubProcess={bpmnElement!} />,
          };
        } else {
          return {
            properties: <SubProcessProperties subProcess={bpmnElement!} />,
          };
        }
      case "adHocSubProcess":
        return {
          properties: <AdHocSubProcessProperties adHocSubProcess={bpmnElement!} />,
        };
      case "transaction":
        return {
          properties: <TransactionProperties transaction={bpmnElement!} />,
        };
      // Misc.
      case "dataObject":
        return {
          properties: <DataObjectProperties dataObject={bpmnElement!} />,
        };
      case "textAnnotation":
        return {
          properties: <TextAnnotationProperties textAnnotation={bpmnElement!} />,
        };
      case "group":
        return {
          properties: <GroupProperties group={bpmnElement!} />,
        };
      case "lane":
        return {
          properties: <LaneProperties lane={bpmnElement!} />,
        };
      // Unsupported
      //// events
      case "event":
      // case "implicitThrowEvent":
      //// data
      // case "dataObjectReference":
      // case "dataStoreReference":
      //// choreography
      // case "manualTask":
      // case "sendTask":
      // case "receiveTask":
      // case "callChoreography":
      // case "choreographyTask":
      // case "subChoreography":
      // undefined
      case undefined:
        return {
          properties: (
            <>
              <FormSection style={{ textAlign: "center" }}>{i18n.propertiesPanel.noPropertiesToEdit}</FormSection>
            </>
          ),
        };
      default:
        assertUnreachable(e);
    }
  }, [customTasks, i18n.propertiesPanel.noPropertiesToEdit, selectedNode?.data.bpmnElement]);

  const [isMetadataSectionExpanded, setMetadataSectionExpanded] = React.useState<boolean>(false);

  return (
    <>
      <Form>
        {properties}

        <FormSection
          title={
            <SectionHeader
              expands={true}
              isSectionExpanded={isMetadataSectionExpanded}
              toogleSectionExpanded={() => setMetadataSectionExpanded((prev) => !prev)}
              icon={<ColumnsIcon width={16} height={36} style={{ marginLeft: "12px" }} />}
              title={i18n.propertiesPanel.metadata}
              locale={locale}
            />
          }
        >
          {isMetadataSectionExpanded && (
            <>
              <FormSection style={{ paddingLeft: "20px", marginTop: "20px", gap: 0 }}>
                <Metadata obj={selectedNode?.data?.bpmnElement} />
              </FormSection>
            </>
          )}
        </FormSection>
      </Form>
    </>
  );
}
