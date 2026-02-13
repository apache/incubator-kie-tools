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
import { BPMN20__tEscalation, BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { EscalationEventSymbolSvg } from "../../diagram/nodes/NodeSvgs";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { TypeaheadSelect } from "../../typeaheadSelect/TypeaheadSelect";
import { useCallback, useMemo } from "react";
import { addOrGetEscalations } from "../../mutations/addOrGetEscalations";
import "./EscalationSelector.css";
import { useBpmnEditorI18n } from "../../i18n";

export type WithEscalation =
  | undefined
  | Normalized<
      ElementFilter<
        Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
        "startEvent" | "intermediateCatchEvent" | "intermediateThrowEvent" | "endEvent" | "boundaryEvent"
      >
    >;

export type OnEscalationChange = (newEscalationRef: string, newEscalation: string) => void;

export function EscalationSelector({
  value,
  onChange,
  omitValues,
}: {
  value: string | undefined;
  onChange: OnEscalationChange;
  omitValues?: string[];
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const escalationsById = useBpmnEditorStore(
    (s) =>
      new Map(
        s.bpmn.model.definitions.rootElement
          ?.filter((e) => e.__$$element === "escalation")
          .map((m) => [m["@_id"], m] as [string, BPMN20__tEscalation])
      )
  );

  const omitIdsSet = useMemo(() => new Set<string | undefined>(omitValues), [omitValues]);

  const options = useMemo(
    () =>
      [...escalationsById.values()]
        .filter((m) => !omitIdsSet.has(m["@_id"]))
        .map((m) => ({ value: m["@_id"], children: m["@_name"] })),
    [escalationsById, omitIdsSet]
  );

  const onCreate = useCallback(
    (newEscalationName: string) => {
      let newEscalationId: string;
      bpmnEditorStoreApi.setState((s) => {
        newEscalationId = addOrGetEscalations({
          definitions: s.bpmn.model.definitions,
          escalationName: newEscalationName,
        }).escalationRef;
      });
      return newEscalationId!;
    },
    [bpmnEditorStoreApi]
  );

  return (
    <>
      <InputGroup>
        <InputGroupText>
          <svg width={30} height={30}>
            <EscalationEventSymbolSvg stroke={"black"} cx={16} cy={16} innerCircleRadius={13} filled={false} />
          </svg>
        </InputGroupText>
        <TypeaheadSelect
          isMultiple={false}
          id={`escalation-selector-${generateUuid()}`}
          setSelected={onChange}
          selected={value}
          isDisabled={isReadOnly}
          options={options}
          onCreateNewOption={onCreate}
          createNewOptionLabel={i18n.propertiesPanel.createEscalation as string}
        />
      </InputGroup>
    </>
  );
}
