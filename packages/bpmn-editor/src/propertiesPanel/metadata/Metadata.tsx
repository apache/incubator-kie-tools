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
import {
  Bpmn20KnownMetaDataKey,
  addBpmn20Drools10MetaData,
  deleteBpmn20Drools10MetaDataEntry,
  renameBpmn20Drools10MetaDataEntry,
  setBpmn20Drools10MetaData,
} from "@kie-tools/bpmn-marshaller/dist/drools-extension-metaData";
import { WithMetaData } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button/Button";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { visitFlowElementsAndArtifacts, visitLanes } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { PropertiesPanelListEmptyState } from "../emptyState/PropertiesPanelListEmptyState";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useMemo, useState } from "react";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import "./Metadata.css";
import { useBpmnEditorI18n } from "../../i18n";

export function Metadata({ obj }: { obj: undefined | { "@_id": string; extensionElements?: WithMetaData } }) {
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
            const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
            if (!obj || process["@_id"] === obj?.["@_id"]) {
              addBpmn20Drools10MetaData(process, "" as Bpmn20KnownMetaDataKey, "");
            } else {
              visitFlowElementsAndArtifacts(process, ({ element }) => {
                if (element["@_id"] === obj?.["@_id"]) {
                  addBpmn20Drools10MetaData(element as typeof obj, "" as Bpmn20KnownMetaDataKey, "");
                }
              });
              visitLanes(process, ({ lane }) => {
                if (lane["@_id"] === obj["@_id"]) {
                  addBpmn20Drools10MetaData(lane as typeof obj, "" as Bpmn20KnownMetaDataKey, "");
                }
              });
            }
          });
        }}
      >
        <PlusCircleIcon />
      </Button>
    ),
    [bpmnEditorStoreApi, obj]
  );

  const entryStyle = {
    padding: "4px",
    margin: "8px",
    width: "calc(100% - 2 * 4px - 2 * 8px)",
  };

  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);

  return (
    <>
      {((obj?.extensionElements?.["drools:metaData"]?.length ?? 0) > 0 && (
        <>
          <div style={{ padding: "0 8px" }}>
            <Grid md={6} style={{ alignItems: "center" }}>
              <GridItem span={5}>
                <div style={entryStyle}>
                  <b>{i18n.propertiesPanel.name}</b>
                </div>
              </GridItem>
              <GridItem span={6}>
                <div style={entryStyle}>
                  <b>{i18n.propertiesPanel.value}</b>
                </div>
              </GridItem>
              <GridItem span={1}>
                <div style={{ textAlign: "right" }}>{!isReadOnly && addButton}</div>
              </GridItem>
            </Grid>
          </div>
          {obj?.extensionElements?.["drools:metaData"]?.map((entry, i) => (
            <div key={i} style={{ padding: "0 8px" }}>
              <Grid
                md={6}
                className={"kie-bpmn-editor--properties-panel--metadata-entry"}
                onMouseEnter={() => setHoveredIndex(i)}
                onMouseLeave={() => setHoveredIndex(undefined)}
              >
                <GridItem span={5}>
                  <input
                    autoFocus={true}
                    style={entryStyle}
                    type="text"
                    placeholder={i18n.propertiesPanel.namePlaceholder}
                    value={entry["@_name"]}
                    onChange={(e) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (process["@_id"] === obj["@_id"]) {
                          renameBpmn20Drools10MetaDataEntry(process, i, e.target.value);
                        } else {
                          visitFlowElementsAndArtifacts(process, ({ element }) => {
                            if (element["@_id"] === obj["@_id"]) {
                              renameBpmn20Drools10MetaDataEntry(element as typeof obj, i, e.target.value);
                            }
                          });
                          visitLanes(process, ({ lane }) => {
                            if (lane["@_id"] === obj["@_id"]) {
                              renameBpmn20Drools10MetaDataEntry(lane as typeof obj, i, e.target.value);
                            }
                          });
                        }
                      })
                    }
                  />
                </GridItem>
                <GridItem span={6}>
                  <input
                    style={entryStyle}
                    type="text"
                    placeholder={i18n.propertiesPanel.valuePlaceholder}
                    value={entry["drools:metaValue"].__$$text}
                    onChange={(e) =>
                      bpmnEditorStoreApi.setState((s) => {
                        const { process } = addOrGetProcessAndDiagramElements({
                          definitions: s.bpmn.model.definitions,
                        });
                        if (process["@_id"] === obj["@_id"]) {
                          setBpmn20Drools10MetaData(process, i, e.target.value);
                        } else {
                          visitFlowElementsAndArtifacts(process, ({ element }) => {
                            if (element["@_id"] === obj["@_id"]) {
                              setBpmn20Drools10MetaData(element as typeof obj, i, e.target.value);
                            }
                          });
                          visitLanes(process, ({ lane }) => {
                            if (lane["@_id"] === obj["@_id"]) {
                              setBpmn20Drools10MetaData(lane as typeof obj, i, e.target.value);
                            }
                          });
                        }
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
                          if (process["@_id"] === obj["@_id"]) {
                            deleteBpmn20Drools10MetaDataEntry(process, i);
                          } else {
                            visitFlowElementsAndArtifacts(process, ({ element }) => {
                              if (element["@_id"] === obj["@_id"]) {
                                deleteBpmn20Drools10MetaDataEntry(element as typeof obj, i);
                              }
                            });
                            visitLanes(process, ({ lane }) => {
                              if (lane["@_id"] === obj["@_id"]) {
                                deleteBpmn20Drools10MetaDataEntry(lane as typeof obj, i);
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
