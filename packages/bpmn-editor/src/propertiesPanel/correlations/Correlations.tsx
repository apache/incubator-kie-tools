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
import { BPMN20__tCorrelationProperty } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import {
  EditableNodeLabel,
  OnEditableNodeLabelChange,
  useEditableNodeLabel,
} from "@kie-tools/xyflow-react-kie-diagram/dist/nodes/EditableNodeLabel";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button/Button";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  EmptyState,
  EmptyStateActions,
  EmptyStateBody,
  EmptyStateIcon,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { FormGroup, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { Form } from "@patternfly/react-core/dist/js/components/Form/Form";
import { FormSelect } from "@patternfly/react-core/dist/js/components/FormSelect/FormSelect";
import { FormSelectOption } from "@patternfly/react-core/dist/js/components/FormSelect/FormSelectOption";
import { InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { InputGroup } from "@patternfly/react-core/dist/js/components/InputGroup/InputGroup";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Tabs, Tab, TabTitleIcon, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex/Flex";
import { FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex/FlexItem";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { ObjectGroupIcon } from "@patternfly/react-icons/dist/js/icons/object-group-icon";
import { PeopleCarryIcon } from "@patternfly/react-icons/dist/js/icons/people-carry-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { useCallback, useEffect, useMemo, useState } from "react";
import { MessageEventSymbolSvg } from "../../diagram/nodes/NodeSvgs";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import {
  ItemDefinitionRefSelector,
  OnChangeItemDefinitionRefSelector,
} from "../itemDefinitionRefSelector/ItemDefinitionRefSelector";
import { MessageSelector } from "../messageSelector/MessageSelector";
import TimesIcon from "@patternfly/react-icons/dist/js/icons/times-icon";
import "./Correlations.css";
import { useBpmnEditorI18n } from "../../i18n";
import {
  addCorrelationProperty,
  addCorrelationKey,
  addMessageBindingToProperty,
  addPropertyToCorrelationKey,
  addSubscription,
} from "../../mutations/addOrGetCorrelations";
import {
  updateCorrelationPropertyName,
  updateCorrelationPropertyType,
  updateCorrelationKeyName,
  updateMessageBindingExpression,
  updateMessageBindingMessage,
  updateSubscriptionValue,
} from "../../mutations/renameCorrelations";
import {
  deleteCorrelationProperty,
  deleteCorrelationKey,
  deletePropertyBinding,
  deletePropertyFromKey,
  deleteSubscription,
} from "../../mutations/deleteCorrelations";

export function Correlations() {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const addProperty = useCallback(() => {
    const newPropertyId = generateUuid();

    bpmnEditorStoreApi.setState((s) => {
      s.focus.consumableId = newPropertyId;
      addCorrelationProperty({
        definitions: s.bpmn.model.definitions,
        propertyId: newPropertyId,
        propertyName: "New Property",
        propertyType: undefined,
      });
    });

    setSelectedPropertyId(newPropertyId);
  }, [bpmnEditorStoreApi]);

  const addKey = useCallback(() => {
    const newKeyId = generateUuid();
    bpmnEditorStoreApi.setState((s) => {
      s.focus.consumableId = newKeyId;
      addCorrelationKey({
        definitions: s.bpmn.model.definitions,
        keyId: newKeyId,
        keyName: "New Key",
      });
    });

    setSelectedKeyId(newKeyId);
  }, [bpmnEditorStoreApi]);

  const properties = useBpmnEditorStore(
    (s) => s.bpmn.model.definitions.rootElement?.filter((e) => e.__$$element === "correlationProperty") ?? []
  );

  const propertiesById = useBpmnEditorStore((s) =>
    (s.bpmn.model.definitions.rootElement ?? []).reduce((acc, e) => {
      if (e.__$$element === "correlationProperty") {
        acc.set(e["@_id"], e);
      }
      return acc;
    }, new Map<string, BPMN20__tCorrelationProperty>())
  );

  const keys = useBpmnEditorStore(
    (s) =>
      s.bpmn.model.definitions.rootElement
        ?.filter((e) => e.__$$element === "collaboration")
        .flatMap((k) => k.correlationKey ?? []) ?? []
  );

  const [selectedPropertyId, setSelectedPropertyId] = useState(properties[0]?.["@_id"]);
  const [selectedKeyId, setSelectedKeyId] = useState(keys[0]?.["@_id"]);

  const selectedProperty = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement
      ?.filter((e) => e.__$$element === "correlationProperty")
      .find((p) => p["@_id"] === selectedPropertyId)
  );

  const selectedKey = useBpmnEditorStore((s) =>
    s.bpmn.model.definitions.rootElement
      ?.find((e) => e.__$$element === "collaboration")
      ?.correlationKey?.find((k) => k["@_id"] === selectedKeyId)
  );

  useEffect(() => {
    if (!selectedProperty && properties.length > 0) {
      setSelectedPropertyId(properties[0]["@_id"]);
    }
  }, [properties, selectedProperty]);

  useEffect(() => {
    if (!selectedKey && keys.length > 0) {
      setSelectedKeyId(keys[0]["@_id"]);
    }
  }, [keys, selectedKey]);

  const availablePropertiesToAddToKey = useMemo(() => {
    return properties.filter(
      (p) => !selectedKey?.correlationPropertyRef?.map((propertyRef) => propertyRef.__$$text).includes(p["@_id"])
    );
  }, [properties, selectedKey?.correlationPropertyRef]);

  const changeSelectedPropertyType = useCallback<OnChangeItemDefinitionRefSelector>(
    (newItemDefinitionRef) => {
      bpmnEditorStoreApi.setState((s) => {
        updateCorrelationPropertyType({
          definitions: s.bpmn.model.definitions,
          propertyId: selectedPropertyId!,
          newItemDefinitionRef,
        });
      });
    },
    [bpmnEditorStoreApi, selectedPropertyId]
  );
  const changePropertyName = useCallback(
    (propetyId: string) => (newName: string | undefined) => {
      if (newName === undefined) return;
      bpmnEditorStoreApi.setState((s) => {
        updateCorrelationPropertyName({
          definitions: s.bpmn.model.definitions,
          propertyId: propetyId,
          newName,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const onRemoveProperty = useCallback(
    (propertyId: string) => {
      bpmnEditorStoreApi.setState((s) => {
        deleteCorrelationProperty({
          definitions: s.bpmn.model.definitions,
          propertyId,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const onChangeMessageBindingExpression = useCallback(
    (i: number) => (e: React.FormEvent, newExpression: string) => {
      bpmnEditorStoreApi.setState((s) => {
        updateMessageBindingExpression({
          definitions: s.bpmn.model.definitions,
          propertyId: selectedPropertyId!,
          bindingIndex: i,
          newExpression,
        });
      });
    },
    [bpmnEditorStoreApi, selectedPropertyId]
  );

  const onChangeMessageBindingMessage = useCallback(
    (i: number) => (newMessageRef: string) => {
      bpmnEditorStoreApi.setState((s) => {
        updateMessageBindingMessage({
          definitions: s.bpmn.model.definitions,
          propertyId: selectedPropertyId!,
          bindingIndex: i,
          newMessageRef,
        });
      });
    },
    [bpmnEditorStoreApi, selectedPropertyId]
  );

  const removePropertyBinding = useCallback(
    (i: number) => (e: React.FormEvent) => {
      bpmnEditorStoreApi.setState((s) => {
        deletePropertyBinding({
          definitions: s.bpmn.model.definitions,
          propertyId: selectedPropertyId!,
          bindingIndex: i,
        });
      });
    },
    [bpmnEditorStoreApi, selectedPropertyId]
  );

  const changeKeyName = useCallback(
    (keyId: string) => (newName: string | undefined) => {
      if (newName === undefined) return;
      bpmnEditorStoreApi.setState((s) => {
        updateCorrelationKeyName({
          definitions: s.bpmn.model.definitions,
          keyId,
          newName,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const onRemoveKey = useCallback(
    (keyId: string) => {
      bpmnEditorStoreApi.setState((s) => {
        deleteCorrelationKey({
          definitions: s.bpmn.model.definitions,
          keyId,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const subscriptions = useBpmnEditorStore(
    (s) =>
      s.bpmn.model.definitions.rootElement
        ?.find((s) => s.__$$element === "process")
        ?.correlationSubscription?.filter((s) => s["@_correlationKeyRef"] === selectedKeyId) ?? []
  );

  const changeValueOfSubscription = useCallback(
    (subscriptionId: string, propertyBindingIndex: number) => (e: React.FormEvent, newValue: string) => {
      bpmnEditorStoreApi.setState((s) => {
        updateSubscriptionValue({
          definitions: s.bpmn.model.definitions,
          subscriptionId,
          propertyBindingIndex,
          newValue,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const removePropertyFromKey = useCallback(
    (propertyIndex: number) => {
      return (e: React.FormEvent) => {
        bpmnEditorStoreApi.setState((s) => {
          deletePropertyFromKey({
            definitions: s.bpmn.model.definitions,
            keyId: selectedKeyId!,
            propertyIndex,
          });
        });
      };
    },
    [bpmnEditorStoreApi, selectedKeyId]
  );

  const removeSubscription = useCallback(
    (subscriptionId: string) => (e: React.FormEvent) => {
      bpmnEditorStoreApi.setState((s) => {
        deleteSubscription({
          definitions: s.bpmn.model.definitions,
          subscriptionId,
        });
      });
    },
    [bpmnEditorStoreApi]
  );

  const hasAtLeastOnePropertyWithMessageBinding = useMemo(() => {
    return properties.some((p) => (p.correlationPropertyRetrievalExpression ?? []).length > 0);
  }, [properties]);

  const hasAtLeastOneKeyWithSubscriptions = useBpmnEditorStore(
    (s) =>
      (s.bpmn.model.definitions.rootElement?.find((s) => s.__$$element === "process")?.correlationSubscription ?? [])
        .length > 0
  );
  const hasAtLeastOneKeyWithProperties = useMemo(
    () => keys.some((k) => (k.correlationPropertyRef ?? []).length > 0),
    [keys]
  );

  const [activeTabKey, setActiveTabKey] = React.useState<string | number>(0);

  return (
    <div className={"kie-bpmn-editor--correlations"}>
      <>
        <Form>
          {(selectedProperty && (
            <>
              <Tabs
                activeKey={activeTabKey}
                onSelect={(e, eventKey) => setActiveTabKey(eventKey)}
                isBox={true}
                aria-label="Tabs in the icons and text example"
                role="region"
              >
                <Tab
                  eventKey={0}
                  title={
                    <>
                      <TabTitleIcon>
                        <PeopleCarryIcon />
                      </TabTitleIcon>
                      <TabTitleText>{i18n.correlation.properties}</TabTitleText>
                    </>
                  }
                >
                  <Flex
                    alignItems={{ default: "alignItemsFlexStart" }}
                    flexWrap={{ default: "nowrap" }}
                    style={{ gap: "18px" }}
                  >
                    <FlexItem>
                      <FormSection>
                        <FormGroup
                          label={
                            <Flex
                              justifyContent={{ default: "justifyContentSpaceBetween" }}
                              alignItems={{ default: "alignItemsCenter" }}
                            >
                              <span>{i18n.correlation.properties}</span>
                              {!isReadOnly && (
                                <div>
                                  <Button variant={ButtonVariant.link} onClick={addProperty}>
                                    <PlusCircleIcon />
                                  </Button>
                                </div>
                              )}
                            </Flex>
                          }
                          className={"kie-bpmn-editor--correlations--properties"}
                        >
                          <ul>
                            {properties.map((p) => (
                              <EditableNameListItem
                                key={p["@_id"]}
                                id={p["@_id"]}
                                value={p["@_name"]}
                                isSelected={p["@_id"] === selectedPropertyId}
                                onChange={changePropertyName(p["@_id"])}
                                onClick={() => setSelectedPropertyId(p["@_id"])}
                                onRemove={() => onRemoveProperty(p["@_id"])}
                              />
                            ))}
                          </ul>
                        </FormGroup>
                      </FormSection>
                    </FlexItem>
                    <FlexItem>
                      <FormSection>
                        <FormGroup
                          label={i18n.propertiesPanel.dataType}
                          className={"kie-bpmn-editor--correlations--properties--data-type"}
                        >
                          <ItemDefinitionRefSelector
                            value={selectedProperty?.["@_type"]}
                            onChange={changeSelectedPropertyType}
                          />
                        </FormGroup>
                      </FormSection>
                    </FlexItem>
                    <FlexItem grow={{ default: "grow" }}>
                      <FormSection>
                        <FormGroup
                          label={i18n.correlation.messageBindings}
                          className={"kie-bpmn-editor--correlations--properties--bindings"}
                        >
                          <Stack hasGutter={true} style={{ gap: "36px" }}>
                            {(selectedProperty.correlationPropertyRetrievalExpression ?? []).map((cpre, i) => (
                              <Card
                                key={cpre["@_id"]}
                                isCompact={true}
                                className={"kie-bpmn-editor--correlations--properties--bindings--binding"}
                              >
                                <Flex gap={{ default: "gapNone" }} alignItems={{ default: "alignItemsCenter" }}>
                                  <FlexItem grow={{ default: "grow" }}>
                                    <MessageSelector
                                      value={cpre["@_messageRef"]}
                                      onChange={onChangeMessageBindingMessage(i)}
                                      disableValues={selectedProperty?.correlationPropertyRetrievalExpression?.flatMap(
                                        (c) => (c["@_messageRef"] === cpre["@_messageRef"] ? [] : c["@_messageRef"])
                                      )}
                                    />
                                    <InputGroup>
                                      <InputGroupText className={"expression-label"}>{`↳`}</InputGroupText>
                                      <TextInput
                                        style={{ fontFamily: "monospace" }}
                                        value={cpre.messagePath.__$$text ?? ""}
                                        onChange={onChangeMessageBindingExpression(i)}
                                        placeholder={i18n.correlation.expressionPlaceHolder}
                                      />
                                    </InputGroup>
                                  </FlexItem>
                                  <FlexItem>
                                    <Button variant={ButtonVariant.plain} onClick={removePropertyBinding(i)}>
                                      <TimesIcon />
                                    </Button>
                                  </FlexItem>
                                </Flex>
                              </Card>
                            ))}
                            <Button
                              variant={
                                !hasAtLeastOnePropertyWithMessageBinding
                                  ? ButtonVariant.secondary
                                  : ButtonVariant.tertiary
                              }
                              size={"lg"}
                              style={{ width: "100%", lineHeight: "1" }}
                              onClick={() => {
                                bpmnEditorStoreApi.setState((s) => {
                                  addMessageBindingToProperty({
                                    definitions: s.bpmn.model.definitions,
                                    propertyId: selectedPropertyId!,
                                  });
                                });
                              }}
                            >
                              <svg width={30} height={30}>
                                <MessageEventSymbolSvg
                                  stroke={
                                    !hasAtLeastOnePropertyWithMessageBinding
                                      ? "var(--pf-v5-c-button--m-secondary--hover--after--BorderColor)"
                                      : "black"
                                  }
                                  cx={15}
                                  cy={15}
                                  innerCircleRadius={15}
                                  fill={"transparent"}
                                  filled={false}
                                />
                              </svg>
                              <br />
                              Add Message binding
                            </Button>
                          </Stack>
                        </FormGroup>
                      </FormSection>
                    </FlexItem>
                  </Flex>
                </Tab>
                <Tab
                  eventKey={1}
                  title={
                    <>
                      <TabTitleIcon>
                        <ObjectGroupIcon />
                      </TabTitleIcon>
                      <TabTitleText>{i18n.correlation.keys}</TabTitleText>
                    </>
                  }
                  aria-label="icons and text content"
                >
                  {(selectedKey && (
                    <>
                      <Flex
                        alignItems={{ default: "alignItemsFlexStart" }}
                        flexWrap={{ default: "nowrap" }}
                        style={{ gap: "18px" }}
                      >
                        <FlexItem>
                          <FormSection>
                            <FormGroup
                              label={
                                <Flex
                                  justifyContent={{ default: "justifyContentSpaceBetween" }}
                                  alignItems={{ default: "alignItemsCenter" }}
                                >
                                  <span>Keys</span>
                                  {!isReadOnly && (
                                    <div>
                                      <Button variant={ButtonVariant.link} onClick={addKey}>
                                        <PlusCircleIcon />
                                      </Button>
                                    </div>
                                  )}
                                </Flex>
                              }
                              className={"kie-bpmn-editor--correlations--keys"}
                            >
                              <ul>
                                {keys.map((k) => (
                                  <EditableNameListItem
                                    key={k["@_id"]}
                                    id={k["@_id"]}
                                    value={k["@_name"]}
                                    isSelected={k["@_id"] === selectedKeyId}
                                    onChange={changeKeyName(k["@_id"])}
                                    onClick={() => setSelectedKeyId(k["@_id"])}
                                    onRemove={() => onRemoveKey(k["@_id"])}
                                  />
                                ))}
                              </ul>
                            </FormGroup>
                          </FormSection>
                        </FlexItem>
                        <FlexItem>
                          <FormSection>
                            <FormGroup
                              label={i18n.correlation.properties}
                              className={"kie-bpmn-editor--correlations--keys--properties"}
                            >
                              {selectedKey.correlationPropertyRef?.map((propertyRef, i) => (
                                <li
                                  key={propertyRef.__$$text}
                                  className={"kie-bpmn-editor--correlations--keys--properties--property"}
                                >
                                  {propertiesById.get(propertyRef.__$$text)?.["@_name"] ?? (
                                    <i style={{ color: "red" }}>{`<Unknown>`}</i>
                                  )}
                                  <Button onClick={removePropertyFromKey(i)} variant={ButtonVariant.plain}>
                                    <TimesIcon />
                                  </Button>
                                </li>
                              ))}
                              <ul>
                                <li>
                                  <FormSelect
                                    id={`correlation-key-properties-selector-${generateUuid()}`}
                                    className={!hasAtLeastOneKeyWithProperties ? "primary" : ""}
                                    onChange={(e, propertyId) => {
                                      bpmnEditorStoreApi.setState((s) => {
                                        addPropertyToCorrelationKey({
                                          definitions: s.bpmn.model.definitions,
                                          keyId: selectedKeyId!,
                                          propertyId,
                                        });
                                      });
                                    }}
                                    isDisabled={availablePropertiesToAddToKey.length <= 0}
                                  >
                                    <FormSelectOption
                                      key={"select"}
                                      isDisabled={true}
                                      isPlaceholder={true}
                                      style={{
                                        textAlign: availablePropertiesToAddToKey.length > 0 ? undefined : "center",
                                      }}
                                      label={
                                        availablePropertiesToAddToKey.length > 0
                                          ? i18n.correlation.addProperty
                                          : i18n.correlation.propertiesIncluded
                                      }
                                    />
                                    {availablePropertiesToAddToKey.map((p) => (
                                      <FormSelectOption
                                        key={p["@_id"]}
                                        label={p["@_name"] ?? i18n.unknown}
                                        value={p["@_id"]}
                                      />
                                    ))}
                                  </FormSelect>
                                </li>
                              </ul>
                            </FormGroup>
                          </FormSection>
                        </FlexItem>
                        <FlexItem grow={{ default: "grow" }}>
                          <FormSection>
                            <FormGroup
                              label={i18n.correlation.subscriptions}
                              className={"kie-bpmn-editor--correlations--keys--subscriptions"}
                            >
                              {((selectedKey.correlationPropertyRef ?? []).length > 0 && (
                                <Stack hasGutter={true} style={{ gap: "36px" }}>
                                  {subscriptions.map((subs) => (
                                    <Card
                                      key={subs["@_id"]}
                                      isCompact={true}
                                      className={"kie-bpmn-editor--correlations--keys--subscriptions--subscription"}
                                    >
                                      <Flex gap={{ default: "gapNone" }} alignItems={{ default: "alignItemsCenter" }}>
                                        <FlexItem grow={{ default: "grow" }}>
                                          {subs.correlationPropertyBinding?.map((cpb, j) => (
                                            <InputGroup key={cpb["@_id"]}>
                                              <InputGroupText style={{ whiteSpace: "nowrap" }}>
                                                {propertiesById.get(cpb["@_correlationPropertyRef"])?.["@_name"] ?? (
                                                  <i style={{ color: "red" }}>{`<Unknown>`}</i>
                                                )}
                                              </InputGroupText>
                                              <InputGroupText>=</InputGroupText>
                                              <TextInput
                                                style={{ fontFamily: "monospace" }}
                                                value={cpb.dataPath.__$$text ?? ""}
                                                onChange={changeValueOfSubscription(subs["@_id"], j)}
                                                placeholder={i18n.correlation.valueOrExpressionPlaceHolder}
                                              />
                                            </InputGroup>
                                          ))}
                                        </FlexItem>

                                        <FlexItem>
                                          <Button
                                            variant={ButtonVariant.plain}
                                            onClick={removeSubscription(subs["@_id"])}
                                          >
                                            <TimesIcon />
                                          </Button>
                                        </FlexItem>
                                      </Flex>
                                    </Card>
                                  ))}

                                  <Button
                                    variant={
                                      !hasAtLeastOneKeyWithSubscriptions
                                        ? ButtonVariant.secondary
                                        : ButtonVariant.tertiary
                                    }
                                    icon={<PlusCircleIcon />}
                                    size={"lg"}
                                    style={{ width: "100%" }}
                                    onClick={() => {
                                      bpmnEditorStoreApi.setState((s) => {
                                        addSubscription({
                                          definitions: s.bpmn.model.definitions,
                                          keyId: selectedKeyId!,
                                          selectedKey: selectedKey!,
                                        });
                                      });
                                    }}
                                  >
                                    <br />
                                    Add Subscription
                                  </Button>
                                </Stack>
                              )) || (
                                <>
                                  <div className={"kie-bpmn-editor--correlations--empty-state"}>
                                    <EmptyState>
                                      <EmptyStateIcon icon={ObjectGroupIcon} />
                                      <Title headingLevel="h4">
                                        {i18n.correlation.addSubscriptions(`${selectedKey["@_name"]}`)}
                                      </Title>
                                      <EmptyStateBody style={{ padding: "0 25%" }}>
                                        {i18n.correlation.correlationEmptyState(`${selectedKey["@_name"]}`)}
                                      </EmptyStateBody>
                                    </EmptyState>
                                  </div>
                                </>
                              )}
                            </FormGroup>
                          </FormSection>
                        </FlexItem>
                      </Flex>
                    </>
                  )) || (
                    <>
                      <div className={"kie-bpmn-editor--correlations--empty-state"}>
                        <EmptyState>
                          <EmptyStateIcon icon={ObjectGroupIcon} />
                          <Title headingLevel="h4">
                            {isReadOnly ? i18n.correlation.noCorrelationKeys : i18n.correlation.noCorrelationKeysYet}
                          </Title>
                          <EmptyStateBody style={{ padding: "0 25%" }}>
                            {i18n.correlation.correlationsKeyEmptyBody}
                          </EmptyStateBody>
                          <br />
                          <EmptyStateActions>
                            <Button
                              style={{
                                marginTop: hasAtLeastOnePropertyWithMessageBinding ? undefined : "2rem",
                              }}
                              variant={
                                hasAtLeastOnePropertyWithMessageBinding ? ButtonVariant.primary : ButtonVariant.tertiary
                              }
                              onClick={addKey}
                            >
                              {i18n.correlation.addCorrelationKey}
                            </Button>
                          </EmptyStateActions>
                        </EmptyState>
                      </div>
                    </>
                  )}
                </Tab>
              </Tabs>
            </>
          )) || (
            <>
              <div className={"kie-bpmn-editor--correlations--empty-state"}>
                <EmptyState>
                  <EmptyStateIcon icon={PeopleCarryIcon} />
                  <Title headingLevel="h4">
                    {isReadOnly ? i18n.correlation.noCorrelations : i18n.correlation.noCorrelationsYet}
                  </Title>
                  <EmptyStateBody style={{ padding: "0 25%" }}>{i18n.correlation.correlationEmptyBody}</EmptyStateBody>
                  <br />
                  <EmptyStateActions>
                    <Button variant={ButtonVariant.primary} onClick={addProperty}>
                      {i18n.correlation.addCorrelationProperty}
                    </Button>
                  </EmptyStateActions>
                </EmptyState>
              </div>
            </>
          )}
        </Form>
      </>
    </div>
  );
}

function EditableNameListItem({
  id,
  value,
  isSelected,
  onChange,
  onClick,
  onRemove,
}: {
  id: string;
  value: string | undefined;
  isSelected: boolean;
  onChange: OnEditableNodeLabelChange;
  onClick: React.MouseEventHandler<HTMLLIElement>;
  onRemove: () => void;
}) {
  const { isEditingLabel, setEditingLabel, triggerEditing, triggerEditingIfEnter } = useEditableNodeLabel(id);

  const _onRemove = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      onRemove();
    },
    [onRemove]
  );

  return (
    <li key={id} className={isSelected ? "selected" : ""} onClick={onClick} onDoubleClick={triggerEditing}>
      <EditableNodeLabel
        id={id}
        value={value}
        name={value}
        position={"center-left"}
        isEditing={isEditingLabel}
        setEditing={setEditingLabel}
        onChange={onChange}
        shouldCommitOnBlur={true}
        validate={() => true}
      />
      <Button onClick={_onRemove} variant={ButtonVariant.plain}>
        <TimesIcon />
      </Button>
    </li>
  );
}
