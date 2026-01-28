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
  parseBpmn20Drools10MetaData,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useMemo } from "react";
import { WithVariables } from "./Variables";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { TypeaheadSelect } from "../../typeaheadSelect/TypeaheadSelect";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { useBpmnEditorI18n } from "../../i18n";

const tags = ["internal", "required", "readonly", "input", "output", "business_relevant", "tracked"];

function getTagArray(p: WithVariables | undefined, i: number) {
  return (parseBpmn20Drools10MetaData(p?.property?.[i]).get("customTags") || undefined)?.split(",") ?? [];
}

export function VariableTagSelector({ p, i }: { p: undefined | WithVariables; i: number }) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const selections = useMemo(() => getTagArray(p, i), [p, i]);

  const pid = p?.["@_id"];
  const pelement = p?.__$$element;

  const ackSelectionsChanged = React.useCallback(
    (_: string | undefined, newTag: string | undefined, args: { triggeredByCreateNewOption: boolean }) => {
      if (!args.triggeredByCreateNewOption) {
        bpmnEditorStoreApi.setState((s) => {
          const { process } = addOrGetProcessAndDiagramElements({
            definitions: s.bpmn.model.definitions,
          });
          if (!process || pid === process["@_id"]) {
            if (process.property?.[i]) {
              const prev = getTagArray(process, i);
              if (prev.includes(newTag as string)) {
                prev.splice(prev.indexOf(newTag as string), 1);
              } else {
                prev.push(newTag as string);
              }
              process.property[i].extensionElements ??= {};
              process.property[i].extensionElements["drools:metaData"] ??= [];
              setBpmn20Drools10MetaData(process.property[i], "customTags", prev.join(","));
            }
          } else {
            visitFlowElementsAndArtifacts(process, ({ element }) => {
              if (element["@_id"] === pid && element.__$$element === pelement) {
                const prev = getTagArray(element, i);
                if (prev.includes(newTag as string)) {
                  prev.splice(prev.indexOf(newTag as string), 1);
                } else {
                  prev.push(newTag as string);
                }
                if (element.property?.[i]) {
                  element.property[i].extensionElements ??= {};
                  element.property[i].extensionElements["drools:metaData"] ??= [];
                  setBpmn20Drools10MetaData(element.property[i], "customTags", prev.join(","));
                }
              }
            });
          }
        });
      }

      return String(newTag ?? "");
    },
    [bpmnEditorStoreApi, i, pelement, pid]
  );

  const options = useMemo(() => {
    return [...new Set([...tags, ...selections])]
      .map((s) => ({ value: s, children: s, isSelected: selections.indexOf(s) > -1 }))
      .sort();
  }, [selections]);

  return (
    <TypeaheadSelect
      isMultiple={true}
      closeAfterSelect={false}
      id={`variable-tag-selector-${generateUuid()}`}
      setSelected={ackSelectionsChanged}
      selected={undefined}
      isDisabled={isReadOnly}
      options={options}
      placeholder={i18n.propertiesPanel.addCustomtagPlaceholder}
      onCreateNewOption={(newTag) => ackSelectionsChanged(newTag, newTag, { triggeredByCreateNewOption: false })}
      createNewOptionLabel={i18n.propertiesPanel.addCustomtag}
    />
  );
}
