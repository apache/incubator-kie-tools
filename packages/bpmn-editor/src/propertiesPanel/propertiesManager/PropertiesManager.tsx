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
import { useCallback, useState } from "react";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { Normalized } from "../../normalization/normalize";
import { BPMN20__tDefinitions, BPMN20__tProcess } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import "./PropertiesManager.css";
import {
  EmptyState,
  EmptyStateActions,
  EmptyStateBody,
  EmptyStateIcon,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import ListIcon from "@patternfly/react-icons/dist/js/icons/list-icon";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import {
  ErrorEventSymbolSvg,
  EscalationEventSymbolSvg,
  MessageEventSymbolSvg,
  SignalEventSymbolSvg,
} from "../../diagram/nodes/NodeSvgs";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "../../mutations/addOrGetItemDefinitions";
import { deleteItemDefinition } from "../../mutations/deleteItemDefinition";
import { renameItemDefinition } from "../../mutations/renameItemDefinition";
import { addOrGetMessages } from "../../mutations/addOrGetMessages";
import { renameMessage } from "../../mutations/renameMessage";
import { deleteMessage } from "../../mutations/deleteMessage";
import { addOrGetSignals } from "../../mutations/addOrGetSignals";
import { renameSignal } from "../../mutations/renameSignal";
import { deleteSignal } from "../../mutations/deleteSignal";
import { addOrGetErrors } from "../../mutations/addOrGetErrors";
import { addOrGetEscalations } from "../../mutations/addOrGetEscalations";
import { deleteEscalation } from "../../mutations/deleteEscalation";
import { renameEscalation } from "../../mutations/renameEscalation";
import { deleteError } from "../../mutations/deleteError";
import { renameError } from "../../mutations/renameError";
import { useBpmnEditorI18n } from "../../i18n";

export type WithVariables = Normalized<
  | ElementFilter<Unpacked<NonNullable<BPMN20__tDefinitions["rootElement"]>>, "process">
  | ElementFilter<
      Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
      "subProcess" | "adHocSubProcess" | "transaction"
    >
>;

export function PropertiesManager({ p }: { p: undefined | WithVariables }) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const { i18n } = useBpmnEditorI18n();

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const errors = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.filter((s) => s.__$$element === "error")
  );
  const escalations = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.filter((s) => s.__$$element === "escalation")
  );
  const itemDefinitions = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.filter((s) => s.__$$element === "itemDefinition")
  )?.filter(
    (s) => Object.values(DEFAULT_DATA_TYPES).findIndex((defaultDataType) => defaultDataType === s["@_structureRef"]) < 0
  );
  const messages = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.filter((s) => s.__$$element === "message")
  );
  const signals = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement?.filter((s) => s.__$$element === "signal")
  );

  const [activeTab, setActiveTab] = useState<string | number>();
  const onSelectTab = useCallback(
    (e: React.MouseEvent<any> | React.KeyboardEvent | MouseEvent, tabIndex: string | number) => {
      setActiveTab(tabIndex);
    },
    []
  );

  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);
  const entryColumnStyle = {
    padding: "4px",
    margin: "8px",
    width: "calc(100% - 2 * 4px - 2 * 8px)",
  };

  return (
    <>
      <Tabs activeKey={activeTab} onSelect={onSelectTab} isBox={false} aria-label={"Properties tabs"}>
        <Tab
          eventKey={0}
          title={<TabTitleText>{i18n.propertiesManager.dataTypes}</TabTitleText>}
          aria-label="Properties - Data Types"
        >
          {((itemDefinitions?.length ?? 0) > 0 && (
            <>
              <div style={{ padding: "0 8px" }}>
                <Grid md={12} style={{ alignItems: "center", columnGap: "12px" }}>
                  <GridItem span={11}>
                    <div style={entryColumnStyle}>
                      <b>{i18n.propertiesManager.dataType}</b>
                    </div>
                  </GridItem>
                  <GridItem span={1}>
                    <div style={{ textAlign: "right" }}>
                      {!isReadOnly && (
                        <Button
                          variant={ButtonVariant.plain}
                          style={{ paddingLeft: 0 }}
                          onClick={() => {
                            bpmnEditorStoreApi.setState((s) => {
                              addOrGetItemDefinitions({ definitions: s.bpmn.model.definitions, dataType: "" });
                            });
                          }}
                        >
                          <PlusCircleIcon />
                        </Button>
                      )}
                    </div>
                  </GridItem>
                </Grid>
              </div>
              {itemDefinitions?.map((entry, i) => (
                <div key={i} style={{ padding: "0 8px", marginBottom: "4px" }}>
                  <Grid
                    md={12}
                    className={"kie-bpmn-editor--properties-panel--properties-manager-entry"}
                    onMouseEnter={() => setHoveredIndex(i)}
                    onMouseLeave={() => setHoveredIndex(undefined)}
                    style={{ columnGap: "12px" }}
                  >
                    <GridItem span={11}>
                      <input
                        autoFocus={true}
                        style={entryColumnStyle}
                        type="text"
                        placeholder={i18n.propertiesManager.dataTypeplaceholder}
                        value={entry["@_structureRef"]}
                        onChange={(e) => {
                          bpmnEditorStoreApi.setState((s) => {
                            renameItemDefinition({
                              definitions: s.bpmn.model.definitions,
                              id: entry["@_id"],
                              newItemDefinitionName: e.target.value,
                            });
                          });
                        }}
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
                              if (entry["@_structureRef"] !== undefined) {
                                deleteItemDefinition({
                                  definitions: s.bpmn.model.definitions,
                                  itemDefinition: entry["@_structureRef"],
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
            <div className={"kie-bpmn-editor--properties-manager--empty-state"}>
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon
                    icon={() => <ListIcon style={{ marginTop: "20px" }} />}
                    style={{ marginTop: "20px" }}
                  />
                  <Title headingLevel="h4" style={{ paddingTop: "33px" }}>
                    {isReadOnly
                      ? i18n.propertiesManager.noDataTypeProperties
                      : i18n.propertiesManager.noDataTypePropertiesYet}
                  </Title>
                  <EmptyStateBody style={{ padding: "0 25%" }}>{i18n.propertiesManager.noProperty}</EmptyStateBody>
                  <br />
                  <EmptyStateActions>
                    <Button
                      variant="primary"
                      onClick={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          addOrGetItemDefinitions({ definitions: s.bpmn.model.definitions, dataType: "" });
                        });
                      }}
                    >
                      {i18n.propertiesManager.addDataType}
                    </Button>
                  </EmptyStateActions>
                </EmptyState>
              </Bullseye>
            </div>
          )}
        </Tab>

        <Tab
          eventKey={1}
          title={<TabTitleText>{i18n.propertiesManager.messages}</TabTitleText>}
          aria-label="Properties - Messages"
        >
          {((messages?.length ?? 0) > 0 && (
            <>
              <div style={{ padding: "0 8px" }}>
                <Grid md={12} style={{ alignItems: "center", columnGap: "12px" }}>
                  <GridItem span={11}>
                    <div style={entryColumnStyle}>
                      <b>{i18n.propertiesManager.messages}</b>
                    </div>
                  </GridItem>
                  <GridItem span={1}>
                    <div style={{ textAlign: "right" }}>
                      {!isReadOnly && (
                        <Button
                          variant={ButtonVariant.plain}
                          style={{ paddingLeft: 0 }}
                          onClick={() => {
                            bpmnEditorStoreApi.setState((s) => {
                              addOrGetMessages({ definitions: s.bpmn.model.definitions, messageName: "" });
                            });
                          }}
                        >
                          <PlusCircleIcon />
                        </Button>
                      )}
                    </div>
                  </GridItem>
                </Grid>
              </div>
              {messages?.map((entry, i) => (
                <div key={i} style={{ padding: "0 8px", marginBottom: "4px" }}>
                  <Grid
                    md={12}
                    className={"kie-bpmn-editor--properties-panel--properties-manager-entry"}
                    onMouseEnter={() => setHoveredIndex(i)}
                    onMouseLeave={() => setHoveredIndex(undefined)}
                    style={{ columnGap: "12px" }}
                  >
                    <GridItem span={11}>
                      <input
                        autoFocus={true}
                        style={entryColumnStyle}
                        type="text"
                        placeholder={i18n.propertiesManager.name}
                        value={entry["@_name"]}
                        onChange={(e) => {
                          bpmnEditorStoreApi.setState((s) => {
                            renameMessage({
                              definitions: s.bpmn.model.definitions,
                              id: entry["@_id"],
                              newMessageName: e.target.value,
                            });
                          });
                        }}
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
                              if (entry["@_name"] !== undefined) {
                                deleteMessage({
                                  definitions: s.bpmn.model.definitions,
                                  message: entry["@_name"],
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
            <div className={"kie-bpmn-editor--properties-manager--empty-state"}>
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon
                    icon={() => (
                      <svg width={100} height={100}>
                        <MessageEventSymbolSvg
                          stroke={"#6a6e6e"}
                          strokeWidth={3}
                          cx={50}
                          cy={50}
                          innerCircleRadius={40}
                          fill={"transparent"}
                          filled={false}
                        />
                      </svg>
                    )}
                  />
                  <Title headingLevel="h4">
                    {isReadOnly
                      ? i18n.propertiesManager.noMessageproperties
                      : i18n.propertiesManager.noMessagepropertiesYet}
                  </Title>
                  <EmptyStateBody style={{ padding: "0 25%" }}>{i18n.propertiesManager.anyMessage}</EmptyStateBody>
                  <br />
                  <EmptyStateActions>
                    <Button
                      variant="primary"
                      onClick={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          addOrGetMessages({ definitions: s.bpmn.model.definitions, messageName: "" });
                        });
                      }}
                    >
                      {i18n.propertiesManager.addMessage}
                    </Button>
                  </EmptyStateActions>
                </EmptyState>
              </Bullseye>
            </div>
          )}
        </Tab>

        <Tab
          eventKey={2}
          title={<TabTitleText>{i18n.propertiesManager.signals}</TabTitleText>}
          aria-label="Properties - Signals"
        >
          {((signals?.length ?? 0) > 0 && (
            <>
              <div style={{ padding: "0 8px" }}>
                <Grid md={12} style={{ alignItems: "center", columnGap: "12px" }}>
                  <GridItem span={11}>
                    <div style={entryColumnStyle}>
                      <b>{i18n.propertiesManager.signals}</b>
                    </div>
                  </GridItem>
                  <GridItem span={1}>
                    <div style={{ textAlign: "right" }}>
                      {!isReadOnly && (
                        <Button
                          variant={ButtonVariant.plain}
                          style={{ paddingLeft: 0 }}
                          onClick={() => {
                            bpmnEditorStoreApi.setState((s) => {
                              addOrGetSignals({ definitions: s.bpmn.model.definitions, signalName: "" });
                            });
                          }}
                        >
                          <PlusCircleIcon />
                        </Button>
                      )}
                    </div>
                  </GridItem>
                </Grid>
              </div>
              {signals?.map((entry, i) => (
                <div key={i} style={{ padding: "0 8px", marginBottom: "4px" }}>
                  <Grid
                    md={12}
                    className={"kie-bpmn-editor--properties-panel--properties-manager-entry"}
                    onMouseEnter={() => setHoveredIndex(i)}
                    onMouseLeave={() => setHoveredIndex(undefined)}
                    style={{ columnGap: "12px" }}
                  >
                    <GridItem span={11}>
                      <input
                        autoFocus={true}
                        style={entryColumnStyle}
                        type="text"
                        placeholder={i18n.propertiesManager.name}
                        value={entry["@_name"]}
                        onChange={(e) => {
                          bpmnEditorStoreApi.setState((s) => {
                            renameSignal({
                              definitions: s.bpmn.model.definitions,
                              id: entry["@_id"],
                              newSignalName: e.target.value,
                            });
                          });
                        }}
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
                              if (entry["@_name"] !== undefined) {
                                deleteSignal({
                                  definitions: s.bpmn.model.definitions,
                                  signal: entry["@_name"],
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
            <div className={"kie-bpmn-editor--properties-manager--empty-state"}>
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon
                    icon={() => (
                      <svg width={100} height={100}>
                        <SignalEventSymbolSvg
                          filled={false}
                          stroke={"#6a6e6e"}
                          strokeWidth={3}
                          x={0}
                          y={0}
                          cx={50}
                          cy={50}
                          innerCircleRadius={40}
                          outerCircleRadius={50}
                        />
                      </svg>
                    )}
                  />
                  <Title headingLevel="h4">
                    {isReadOnly
                      ? i18n.propertiesManager.noSignalproperties
                      : i18n.propertiesManager.noSignalpropertiesYet}
                  </Title>
                  <EmptyStateBody style={{ padding: "0 25%" }}>{i18n.propertiesManager.anySignal}</EmptyStateBody>
                  <br />
                  <EmptyStateActions>
                    <Button
                      variant="primary"
                      onClick={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          addOrGetSignals({ definitions: s.bpmn.model.definitions, signalName: "" });
                        });
                      }}
                    >
                      {i18n.propertiesManager.addSignal}
                    </Button>
                  </EmptyStateActions>
                </EmptyState>
              </Bullseye>
            </div>
          )}
        </Tab>

        <Tab
          eventKey={3}
          title={<TabTitleText>{i18n.propertiesManager.escalations}</TabTitleText>}
          aria-label="Properties - Escalations"
        >
          {((escalations?.length ?? 0) > 0 && (
            <>
              <div style={{ padding: "0 8px" }}>
                <Grid md={12} style={{ alignItems: "center", columnGap: "12px" }}>
                  <GridItem span={11}>
                    <div style={entryColumnStyle}>
                      <b>{i18n.propertiesManager.escalations}</b>
                    </div>
                  </GridItem>
                  <GridItem span={1}>
                    <div style={{ textAlign: "right" }}>
                      {!isReadOnly && (
                        <Button
                          variant={ButtonVariant.plain}
                          style={{ paddingLeft: 0 }}
                          onClick={() => {
                            bpmnEditorStoreApi.setState((s) => {
                              addOrGetEscalations({ definitions: s.bpmn.model.definitions, escalationName: "" });
                            });
                          }}
                        >
                          <PlusCircleIcon />
                        </Button>
                      )}
                    </div>
                  </GridItem>
                </Grid>
              </div>
              {escalations?.map((entry, i) => (
                <div key={i} style={{ padding: "0 8px", marginBottom: "4px" }}>
                  <Grid
                    md={12}
                    className={"kie-bpmn-editor--properties-panel--properties-manager-entry"}
                    onMouseEnter={() => setHoveredIndex(i)}
                    onMouseLeave={() => setHoveredIndex(undefined)}
                    style={{ columnGap: "12px" }}
                  >
                    <GridItem span={11}>
                      <input
                        autoFocus={true}
                        style={entryColumnStyle}
                        type="text"
                        placeholder={i18n.propertiesManager.name}
                        value={entry["@_name"]}
                        onChange={(e) => {
                          bpmnEditorStoreApi.setState((s) => {
                            renameEscalation({
                              definitions: s.bpmn.model.definitions,
                              id: entry["@_id"],
                              newEscalationName: e.target.value,
                            });
                          });
                        }}
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
                              if (entry["@_name"] !== undefined) {
                                deleteEscalation({
                                  definitions: s.bpmn.model.definitions,
                                  escalation: entry["@_name"],
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
            <div className={"kie-bpmn-editor--properties-manager--empty-state"}>
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon
                    icon={() => (
                      <svg width={100} height={100}>
                        <EscalationEventSymbolSvg
                          filled={false}
                          stroke={"#6a6e6e"}
                          strokeWidth={3}
                          cx={50}
                          cy={50}
                          innerCircleRadius={40}
                        />
                      </svg>
                    )}
                  />
                  <Title headingLevel="h4">
                    {isReadOnly
                      ? i18n.propertiesManager.noEscalationproperties
                      : i18n.propertiesManager.noEscalationpropertiesYet}
                  </Title>
                  <EmptyStateBody style={{ padding: "0 26%" }}>{i18n.propertiesManager.anyEscalation}</EmptyStateBody>
                  <br />
                  <EmptyStateActions>
                    <Button
                      variant="primary"
                      onClick={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          addOrGetEscalations({ definitions: s.bpmn.model.definitions, escalationName: "" });
                        });
                      }}
                    >
                      {i18n.propertiesManager.addEscalation}
                    </Button>
                  </EmptyStateActions>
                </EmptyState>
              </Bullseye>
            </div>
          )}
        </Tab>

        <Tab
          eventKey={4}
          title={<TabTitleText>{i18n.propertiesManager.errors}</TabTitleText>}
          aria-label="Properties - Errors"
        >
          {((errors?.length ?? 0) > 0 && (
            <>
              <div style={{ padding: "0 8px" }}>
                <Grid md={12} style={{ alignItems: "center", columnGap: "12px" }}>
                  <GridItem span={11}>
                    <div style={entryColumnStyle}>
                      <b>{i18n.propertiesManager.errors}</b>
                    </div>
                  </GridItem>
                  <GridItem span={1}>
                    <div style={{ textAlign: "right" }}>
                      {!isReadOnly && (
                        <Button
                          variant={ButtonVariant.plain}
                          style={{ paddingLeft: 0 }}
                          onClick={() => {
                            bpmnEditorStoreApi.setState((s) => {
                              addOrGetErrors({ definitions: s.bpmn.model.definitions, errorName: "" });
                            });
                          }}
                        >
                          <PlusCircleIcon />
                        </Button>
                      )}
                    </div>
                  </GridItem>
                </Grid>
              </div>
              {errors?.map((entry, i) => (
                <div key={i} style={{ padding: "0 8px", marginBottom: "4px" }}>
                  <Grid
                    md={12}
                    className={"kie-bpmn-editor--properties-panel--properties-manager-entry"}
                    onMouseEnter={() => setHoveredIndex(i)}
                    onMouseLeave={() => setHoveredIndex(undefined)}
                    style={{ columnGap: "12px" }}
                  >
                    <GridItem span={11}>
                      <input
                        autoFocus={true}
                        style={entryColumnStyle}
                        type="text"
                        placeholder={i18n.propertiesManager.name}
                        value={entry["@_name"]}
                        onChange={(e) => {
                          bpmnEditorStoreApi.setState((s) => {
                            renameError({
                              definitions: s.bpmn.model.definitions,
                              id: entry["@_id"],
                              newErrorName: e.target.value,
                            });
                          });
                        }}
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
                              if (entry["@_name"] !== undefined) {
                                deleteError({
                                  definitions: s.bpmn.model.definitions,
                                  error: entry["@_name"],
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
            <div className={"kie-bpmn-editor--properties-manager--empty-state"}>
              <Bullseye>
                <EmptyState>
                  <EmptyStateIcon
                    icon={() => (
                      <svg width={100} height={100}>
                        <ErrorEventSymbolSvg
                          filled={false}
                          stroke={"#6a6e6e"}
                          strokeWidth={3}
                          cx={50}
                          cy={50}
                          innerCircleRadius={40}
                          outerCircleRadius={50}
                        />
                      </svg>
                    )}
                  />
                  <Title headingLevel="h4">
                    {isReadOnly
                      ? i18n.propertiesManager.noErrorproperties
                      : i18n.propertiesManager.noErrorpropertiesYet}
                  </Title>
                  <EmptyStateBody style={{ padding: "0 25%" }}>{i18n.propertiesManager.anyError}</EmptyStateBody>
                  <br />
                  <EmptyStateActions>
                    <Button
                      variant="primary"
                      onClick={() => {
                        bpmnEditorStoreApi.setState((s) => {
                          addOrGetErrors({ definitions: s.bpmn.model.definitions, errorName: "" });
                        });
                      }}
                    >
                      {i18n.propertiesManager.addError}
                    </Button>
                  </EmptyStateActions>
                </EmptyState>
              </Bullseye>
            </div>
          )}
        </Tab>
      </Tabs>
    </>
  );
}
