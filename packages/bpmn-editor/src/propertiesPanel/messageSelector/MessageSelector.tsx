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
import { BPMN20__tMessage, BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { MessageEventSymbolSvg } from "../../diagram/nodes/NodeSvgs";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { TypeaheadSelect } from "../../typeaheadSelect/TypeaheadSelect";
import { useCallback, useMemo } from "react";
import { addOrGetMessages } from "../../mutations/addOrGetMessages";
import "./MessageSelector.css";
import { useBpmnEditorI18n } from "../../i18n";

export type EventWithMessage =
  | undefined
  | Normalized<
      ElementFilter<
        Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
        "startEvent" | "intermediateCatchEvent" | "intermediateThrowEvent" | "endEvent" | "boundaryEvent"
      >
    >;

export type OnMessageChange = (newMessageRef: string, newMessage: string) => void;

export function MessageSelector({
  value,
  onChange,
  disableValues,
}: {
  value: string | undefined;
  onChange: OnMessageChange;
  disableValues?: string[];
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const messagesById = useBpmnEditorStore(
    (s) =>
      new Map(
        s.bpmn.model.definitions.rootElement
          ?.filter((e) => e.__$$element === "message")
          .map((m) => [m["@_id"], m] as [string, BPMN20__tMessage])
      )
  );

  const disableIdsSet = useMemo(() => new Set<string | undefined>(disableValues), [disableValues]);

  const options = useMemo(
    () =>
      [...messagesById.values()].map((m) => ({
        value: m["@_id"],
        children: m["@_name"],
        isDisabled: disableIdsSet.has(m["@_id"]),
      })),
    [messagesById, disableIdsSet]
  );

  const onCreate = useCallback(
    (newMessageName: string) => {
      let newMessageId: string;
      bpmnEditorStoreApi.setState((s) => {
        newMessageId = addOrGetMessages({
          definitions: s.bpmn.model.definitions,
          messageName: newMessageName,
        }).messageRef;
      });
      return newMessageId!;
    },
    [bpmnEditorStoreApi]
  );

  return (
    <>
      <InputGroup>
        <InputGroupText>
          <svg width={30} height={30}>
            <MessageEventSymbolSvg
              stroke={"black"}
              cx={15}
              cy={15}
              innerCircleRadius={15}
              fill={"white"}
              filled={false}
            />
          </svg>
        </InputGroupText>
        <TypeaheadSelect
          isMultiple={false}
          id={`message-selector-${generateUuid()}`}
          setSelected={onChange}
          selected={value}
          isDisabled={isReadOnly}
          options={options}
          onCreateNewOption={onCreate}
          createNewOptionLabel={i18n.propertiesPanel.createMessage as string}
        />
      </InputGroup>
    </>
  );
}
