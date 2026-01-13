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
import "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button/Button";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useCallback, useMemo, useState } from "react";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { Normalized } from "../../normalization/normalize";
import { BPMN20__tDefinitions, BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addVariable } from "../../mutations/addVariable";
import { ItemDefinitionRefSelector } from "../itemDefinitionRefSelector/ItemDefinitionRefSelector";
import { VariableTagSelector } from "./VariableTagSelector";
import "./Variables.css";
import { useBpmnEditorI18n } from "../../i18n";

export type WithVariables = Normalized<
  | ElementFilter<Unpacked<NonNullable<BPMN20__tDefinitions["rootElement"]>>, "process">
  | ElementFilter<
      Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
      "subProcess" | "adHocSubProcess" | "transaction"
    >
>;

export function Variables({
  p,
  EmptyState,
}: {
  p: undefined | WithVariables;
  EmptyState: React.ComponentType<{ addButton: JSX.Element }>;
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const addButton = useMemo(
    () => (
      <Button
        variant={ButtonVariant.plain}
        style={{ paddingLeft: 0 }}
        onClick={() => {
          bpmnEditorStoreApi.setState((s) => {
            addVariable({ definitions: s.bpmn.model.definitions, pId: p?.["@_id"] });
          });
        }}
      >
        <PlusCircleIcon />
      </Button>
    ),
    [bpmnEditorStoreApi, p]
  );

  const entryColumnStyle = useMemo(
    () => ({
      padding: "4px",
      margin: "8px",
      width: "calc(100% - 2 * 4px - 2 * 8px)",
    }),
    []
  );

  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);

  const hasWhitespace = useCallback((value) => {
    return /\s/.test(value);
  }, []);
  const [variableNames, setVariableNames] = useState<Map<number, string>>(new Map());

  const variableInputStyle = (variableName: string | undefined) => ({
    ...entryColumnStyle,
    color: hasWhitespace(variableName) ? "red" : "black",
    textDecorationLine: hasWhitespace(variableName) ? "underline" : undefined,
    textDecorationStyle: hasWhitespace(variableName) ? ("dotted" as const) : undefined,
    textDecorationColor: hasWhitespace(variableName) ? "red" : undefined,
  });

  return (
    <>
      {((p?.property?.length ?? 0) > 0 && (
        <>
          <div style={{ padding: "0 8px" }}>
            <Grid md={6} style={{ alignItems: "center", columnGap: "12px" }}>
              <GridItem span={4}>
                <div style={entryColumnStyle}>
                  <b>{i18n.propertiesPanel.name}</b>
                </div>
              </GridItem>
              <GridItem span={4}>
                <div style={entryColumnStyle}>
                  <b>{i18n.propertiesPanel.dataType}</b>
                </div>
              </GridItem>
              <GridItem span={3}>
                <div style={entryColumnStyle}>
                  <b>{i18n.propertiesPanel.tags}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div style={{ textAlign: "right" }}>{!isReadOnly && addButton}</div>
              </GridItem>
            </Grid>
          </div>
          {p?.property?.map((entry, i) => (
            <div key={i} style={{ padding: "0 8px", marginBottom: "4px" }}>
              <Grid
                md={6}
                className={"kie-bpmn-editor--properties-panel--variables-entry"}
                onMouseEnter={() => setHoveredIndex(i)}
                onMouseLeave={() => setHoveredIndex(undefined)}
                style={{ columnGap: "12px" }}
              >
                <GridItem span={3}>
                  <input
                    autoFocus={true}
                    style={variableInputStyle(variableNames.get(i) || entry["@_name"])}
                    type="text"
                    placeholder={i18n.propertiesPanel.namePlaceholder}
                    value={variableNames.get(i) || entry["@_name"]}
                    onChange={(e) => {
                      setVariableNames((prevMap) => {
                        const newMap = new Map(prevMap);
                        newMap.set(i, e.target.value);
                        return newMap;
                      });
                      if (hasWhitespace(e.target.value)) {
                        return;
                      }
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (!p || p["@_id"] === process["@_id"]) {
                          if (process.property?.[i]) {
                            process.property[i]["@_name"] = e.target.value;
                          }
                        } else {
                          visitFlowElementsAndArtifacts(process, ({ element }) => {
                            if (element["@_id"] === p["@_id"] && element.__$$element === p.__$$element) {
                              if (element.property?.[i]) {
                                element.property[i]["@_name"] = e.target.value;
                              }
                            }
                          });
                        }
                      });
                    }}
                  />
                </GridItem>
                <GridItem span={4}>
                  <ItemDefinitionRefSelector
                    value={entry["@_itemSubjectRef"]}
                    onChange={(newItemDefinitionRef) => {
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (!p || p["@_id"] === process["@_id"]) {
                          if (process.property?.[i]) {
                            process.property[i]["@_itemSubjectRef"] = newItemDefinitionRef;
                          }
                        } else {
                          visitFlowElementsAndArtifacts(process, ({ element }) => {
                            if (element["@_id"] === p["@_id"] && element.__$$element === p.__$$element) {
                              if (element.property?.[i]) {
                                element.property[i]["@_itemSubjectRef"] = newItemDefinitionRef;
                              }
                            }
                          });
                        }
                      });
                    }}
                  />
                </GridItem>
                <GridItem span={4}>
                  <VariableTagSelector p={p} i={i} />
                </GridItem>
                <GridItem span={1} style={{ textAlign: "right" }}>
                  {hoveredIndex === i && (
                    <Button
                      tabIndex={9999} // Prevent tab from going to this button
                      variant={ButtonVariant.plain}
                      style={{ paddingLeft: 0 }}
                      onClick={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          const { process } = addOrGetProcessAndDiagramElements({
                            definitions: s.bpmn.model.definitions,
                          });
                          if (!p || p["@_id"] === process["@_id"]) {
                            process.property?.splice(i, 1);
                          } else {
                            visitFlowElementsAndArtifacts(process, ({ element }) => {
                              if (element["@_id"] === p["@_id"] && element.__$$element === p.__$$element) {
                                element.property?.splice(i, 1);
                              }
                            });
                          }
                        });
                      }}
                    >
                      <TimesIcon />
                    </Button>
                  )}
                </GridItem>
              </Grid>
            </div>
          ))}
        </>
      )) || <EmptyState addButton={addButton} />}
    </>
  );
}
