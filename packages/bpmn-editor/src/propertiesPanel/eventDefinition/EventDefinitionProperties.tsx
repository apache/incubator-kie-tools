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

import { BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import * as React from "react";
import { Normalized } from "../../normalization/normalize";
import { SlaDueDateInput } from "../slaDueDate/SlaDueDateInput";
import { MessageSelector, OnMessageChange } from "../messageSelector/MessageSelector";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { ErrorSelector, OnErrorChange } from "../errorSelector/ErrorSelector";
import { EscalationSelector, OnEscalationChange } from "../escalationSelector/EscalationSelector";
import { OnSignalChange, SignalSelector } from "../signalSelector/SignalSelector";
import { TimerOptions } from "../timerOptions/TimerOptions";
import { ActivitySelector } from "../activitySelector/ActivitySelector";
import { SignalScopeSelector } from "../signalScopeSelector/SignalScopeSelector";
import { ConditionalEventSelector } from "../conditionalExpression/ConditionalExpressionSelector";
import { LinkInput } from "../linkInput/LinkInput";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetMessages } from "../../mutations/addOrGetMessages";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorStoreApi } from "../../store/StoreContext";
import { useBpmnEditorI18n } from "../../i18n";

export type Event = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "startEvent" | "intermediateCatchEvent" | "intermediateThrowEvent" | "endEvent" | "boundaryEvent"
  >
>;

export function EventDefinitionProperties({ event }: { event: Event }) {
  const { i18n } = useBpmnEditorI18n();
  const eventDefinition = event.eventDefinition?.[0];

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const onMessageChange = React.useCallback<OnMessageChange>(
    (newMessageRef, newMessage) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === event?.["@_id"] && e.__$$element === event.__$$element) {
            const messageEventDefinition = e.eventDefinition?.find(
              (event) => event.__$$element === "messageEventDefinition"
            );
            if (messageEventDefinition) {
              messageEventDefinition["@_drools:msgref"] = newMessage;
              messageEventDefinition["@_messageRef"] = newMessageRef;
            }
          }
        });
      });
    },
    [bpmnEditorStoreApi, event]
  );

  const onSignalChange = React.useCallback<OnSignalChange>(
    (newSignalRef) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === event?.["@_id"] && e.__$$element === event.__$$element) {
            const signalEventDefinition = e.eventDefinition?.find(
              (event) => event.__$$element === "signalEventDefinition"
            );
            if (signalEventDefinition) {
              signalEventDefinition["@_signalRef"] = newSignalRef;
            }
          }
        });
      });
    },
    [bpmnEditorStoreApi, event]
  );

  const onErrorChange = React.useCallback<OnErrorChange>(
    (newErrorRef, newError) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === event?.["@_id"] && e.__$$element === event.__$$element) {
            const errorEventDefinition = e.eventDefinition?.find(
              (event) => event.__$$element === "errorEventDefinition"
            );
            if (errorEventDefinition) {
              errorEventDefinition["@_errorRef"] = newErrorRef;
              errorEventDefinition["@_drools:erefname"] = newError;
            }
          }
        });
      });
    },
    [bpmnEditorStoreApi, event]
  );

  const onEscalationChange = React.useCallback<OnEscalationChange>(
    (newEscalationRef, newEscalation) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
        visitFlowElementsAndArtifacts(process, ({ element: e }) => {
          if (e["@_id"] === event?.["@_id"] && e.__$$element === event.__$$element) {
            const escalationEventDefinition = e.eventDefinition?.find(
              (event) => event.__$$element === "escalationEventDefinition"
            );
            if (escalationEventDefinition) {
              escalationEventDefinition["@_escalationRef"] = newEscalationRef;
              escalationEventDefinition["@_drools:esccode"] = newEscalation;
            }
          }
        });
      });
    },
    [bpmnEditorStoreApi, event]
  );

  return (
    <>
      {(event.__$$element === "startEvent" ||
        event.__$$element === "intermediateCatchEvent" ||
        event.__$$element === "boundaryEvent") &&
        eventDefinition?.__$$element !== "linkEventDefinition" && <SlaDueDateInput element={event} />}

      {/* specifics */}
      <>
        {/* start and end */}
        {eventDefinition === undefined && ( //
          <>{/* nothing */}</>
        )}

        {/* all */}
        {eventDefinition?.__$$element === "messageEventDefinition" && ( //
          <>
            <FormSection>
              <FormGroup label={i18n.eventDefinitionProperties.message}>
                <MessageSelector
                  value={
                    event?.eventDefinition?.find((eventDef) => eventDef.__$$element === "messageEventDefinition")?.[
                      "@_messageRef"
                    ]
                  }
                  onChange={onMessageChange}
                />
              </FormGroup>
            </FormSection>
          </>
        )}

        {/* all */}
        {eventDefinition?.__$$element === "signalEventDefinition" && ( //
          <>
            <FormSection>
              <FormGroup label={i18n.eventDefinitionProperties.signal}>
                <SignalSelector
                  value={
                    event?.eventDefinition?.find((eventDef) => eventDef.__$$element === "signalEventDefinition")?.[
                      "@_signalRef"
                    ]
                  }
                  onChange={onSignalChange}
                />
                {(event.__$$element === "intermediateThrowEvent" || event.__$$element === "endEvent") && (
                  <SignalScopeSelector element={event} />
                )}
              </FormGroup>
            </FormSection>
          </>
        )}

        {eventDefinition?.__$$element === "linkEventDefinition" &&
          (event.__$$element === "intermediateCatchEvent" || //
            event.__$$element === "intermediateThrowEvent") && ( //
            <LinkInput element={event} />
          )}

        {eventDefinition?.__$$element === "errorEventDefinition" && //
          (event.__$$element === "startEvent" ||
            event.__$$element === "intermediateCatchEvent" ||
            event.__$$element === "endEvent" ||
            event.__$$element === "boundaryEvent") && ( //
            <FormSection>
              <FormGroup label={i18n.eventDefinitionProperties.errorCode}>
                <ErrorSelector
                  value={
                    event?.eventDefinition?.find((eventDef) => eventDef.__$$element === "errorEventDefinition")?.[
                      "@_errorRef"
                    ]
                  }
                  onChange={onErrorChange}
                />
              </FormGroup>
            </FormSection>
          )}

        {/* all */}
        {eventDefinition?.__$$element === "escalationEventDefinition" && (
          <>
            <>
              <FormSection>
                <FormGroup label={i18n.eventDefinitionProperties.escalationCode}>
                  <EscalationSelector
                    value={
                      event?.eventDefinition?.find(
                        (eventDef) => eventDef.__$$element === "escalationEventDefinition"
                      )?.["@_escalationRef"]
                    }
                    onChange={onEscalationChange}
                  />
                </FormGroup>
              </FormSection>
            </>
          </>
        )}

        {/* all */}
        {eventDefinition?.__$$element === "compensateEventDefinition" &&
          (event.__$$element === "intermediateThrowEvent" || event.__$$element === "endEvent") && ( //
            <ActivitySelector element={event} />
          )}

        {eventDefinition?.__$$element === "conditionalEventDefinition" &&
          (event.__$$element === "startEvent" ||
            event.__$$element === "intermediateCatchEvent" ||
            event.__$$element === "boundaryEvent") && ( //
            <ConditionalEventSelector element={event} />
          )}

        {eventDefinition?.__$$element === "timerEventDefinition" &&
          (event.__$$element === "startEvent" ||
            event.__$$element === "intermediateCatchEvent" ||
            event.__$$element === "boundaryEvent") && ( //
            <TimerOptions element={event} />
          )}

        {eventDefinition?.__$$element === "terminateEventDefinition" && //
          event.__$$element === "endEvent" && ( //
            <>{/* nothing */}</>
          )}

        {/* unsupported */}
        {(eventDefinition?.__$$element as any) === "multiple" && <>{/* nothing */}</>}
        {(eventDefinition?.__$$element as any) === "parallel multiple" && <>{/* nothing */}</>}
      </>
    </>
  );
}
