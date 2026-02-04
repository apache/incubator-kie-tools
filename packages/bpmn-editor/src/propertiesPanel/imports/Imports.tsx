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
import { BPMN20__tDefinitions, BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button/Button";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { PropertiesPanelListEmptyState } from "../emptyState/PropertiesPanelListEmptyState";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useCallback, useMemo, useState } from "react";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { Normalized } from "../../normalization/normalize";
import "./Imports.css";
import { useBpmnEditorI18n } from "../../i18n";

export function Imports({ p }: { p: undefined | Normalized<BPMN20__tProcess> }) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const addAtEnd = useCallback(() => {
    bpmnEditorStoreApi.setState((s) => {
      const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
      process.extensionElements ??= {};
      process.extensionElements["drools:import"] ??= [];
      process.extensionElements["drools:import"].push({
        "@_name": "",
      });
    });
  }, [bpmnEditorStoreApi]);

  const count = p?.extensionElements?.["drools:import"]?.length ?? 0;

  const addButton = useMemo(
    () => (
      <Button variant={ButtonVariant.plain} style={{ paddingLeft: 0 }} onClick={addAtEnd}>
        <PlusCircleIcon />
      </Button>
    ),
    [addAtEnd]
  );

  const onKeyPress = useCallback(
    (i: number) => (e: React.KeyboardEvent<HTMLInputElement>) => {
      const isLast = i === count - 1;
      if (e.key === "Enter" && isLast && (e.target as HTMLInputElement).value.trim() !== "") {
        addAtEnd();
      }
    },
    [addAtEnd, count]
  );

  const entryStyle = {
    padding: "4px",
    margin: "8px",
    width: "calc(100% - 2 * 4px - 2 * 8px)",
  };

  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);

  return (
    <>
      {(count > 0 && (
        <>
          <div style={{ padding: "0 8px" }}>
            <Grid md={6} style={{ alignItems: "center" }}>
              <GridItem span={11}>
                <div style={entryStyle}>
                  <b>{i18n.propertiesPanel.name}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div style={{ textAlign: "right" }}>{!isReadOnly && addButton}</div>
              </GridItem>
            </Grid>
          </div>
          {p?.extensionElements?.["drools:import"]?.map((entry, i) => (
            <div key={i} style={{ padding: "0 8px" }}>
              <Grid
                md={6}
                className={"kie-bpmn-editor--properties-panel--import-entry"}
                onMouseEnter={() => setHoveredIndex(i)}
                onMouseLeave={() => setHoveredIndex(undefined)}
              >
                <GridItem span={11}>
                  <input
                    autoFocus={true}
                    style={entryStyle}
                    type="text"
                    placeholder={i18n.propertiesPanel.classNamePlaceholder}
                    value={entry["@_name"]}
                    onKeyPress={onKeyPress(i)}
                    onChange={(e) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        process.extensionElements!["drools:import"]![i]["@_name"] = e.target.value.trim(); // Spaces are not allowed.
                      })
                    }
                  />
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
                          process.extensionElements?.["drools:import"]?.splice(i, 1);
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
      )) || (
        <div style={{ position: "relative" }}>
          <PropertiesPanelListEmptyState />
          {!isReadOnly && (
            <>
              <div style={{ position: "absolute", top: "calc(50% - 16px)", right: "0" }}>{addButton}</div>
            </>
          )}
        </div>
      )}
    </>
  );
}
