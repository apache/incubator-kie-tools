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
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { ActionGroup, Form, FormSection } from "@patternfly/react-core/dist/js/components/Form";
import { SectionHeader } from "@kie-tools/xyflow-react-kie-diagram/dist/propertiesPanel/SectionHeader";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EditIcon } from "@patternfly/react-icons/dist/js/icons/edit-icon";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BPMN20__tDataInputAssociation,
  BPMN20__tDataOutputAssociation,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Modal, ModalVariant } from "@patternfly/react-core/dist/js/components/Modal/Modal";
import { EmptyState, EmptyStateIcon, EmptyStateActions } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { EyeIcon } from "@patternfly/react-icons/dist/js/icons/eye-icon";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { ItemDefinitionRefSelector } from "../itemDefinitionRefSelector/ItemDefinitionRefSelector";
import { VariableSelector } from "../variableSelector/VariableSelector";
import { ImportIcon } from "@patternfly/react-icons/dist/js/icons/import-icon";
import { ExportIcon } from "@patternfly/react-icons/dist/js/icons/export-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import {
  DATA_INPUT_RESERVED_NAMES,
  DataMapping,
  getDataMapping,
  MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS,
  setDataMappingForElement,
  WithDataMapping,
  WithInputDataMapping,
  WithOutputDataMapping,
} from "../../mutations/_dataMapping";
import { CustomTask } from "../../BpmnEditor";
import { useCustomTasks } from "../../customTasks/BpmnEditorCustomTasksContextProvider";
import "./DataMappingFormSection.css";
import { useBpmnEditorI18n } from "../../i18n";

export function DataMappingFormSection({
  sectionLabel,
  elementName,
  children,
  showDataMappingModal,
  setShowDataMappingModal,
}: React.PropsWithChildren<{
  sectionLabel?: string;
  elementName: string | undefined;
  showDataMappingModal: boolean;
  setShowDataMappingModal: React.Dispatch<React.SetStateAction<boolean>>;
}>) {
  const { i18n, locale } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  return (
    <>
      <FormSection
        title={
          <SectionHeader
            expands={"modal"}
            icon={<div style={{ marginLeft: "12px", width: "16px", height: "36px", lineHeight: "36px" }}>{"⇆"}</div>}
            title={i18n.dataMapping.title + sectionLabel}
            toogleSectionExpanded={() => setShowDataMappingModal(true)}
            action={
              <Button
                title={i18n.dataMapping.manage}
                variant={ButtonVariant.plain}
                onClick={() => setShowDataMappingModal(true)}
                style={{ paddingBottom: 0, paddingTop: 0 }}
              >
                {isReadOnly ? <EyeIcon /> : <EditIcon />}
              </Button>
            }
            locale={locale}
          />
        }
      />
      <Modal
        title={i18n.dataMapping.mappingForElement(elementName ?? "undefined")}
        className={"kie-bpmn-editor--data-mappings--modal"}
        aria-labelledby={"Data mapping"}
        variant={ModalVariant.large}
        isOpen={showDataMappingModal}
        onClose={() => setShowDataMappingModal(false)}
      >
        {children}
      </Modal>
    </>
  );
}

function flaggedDataInputs<T extends { name?: string; "@_name"?: string }>(
  element: WithDataMapping,
  array: undefined | T[],
  customTasks: CustomTask[]
): undefined | { dataMapping: T; hide: boolean }[] {
  return array?.map((dataInput) => ({
    dataMapping: dataInput,
    hide:
      (element.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" &&
        element.loopCharacteristics.inputDataItem?.["@_name"] === (dataInput["@_name"] ?? dataInput.name)) ||
      (dataInput["@_name"] ?? dataInput.name) ===
        MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.IN_COLLECTION ||
      (DATA_INPUT_RESERVED_NAMES.get(element.__$$element) ?? new Set()).has((dataInput["@_name"] ?? dataInput.name)!) ||
      (element.__$$element === "task" &&
        !!customTasks.find((c) => {
          if (!c.matches(element)) {
            return false;
          }
          const dataInputName = dataInput["@_name"] ?? dataInput.name;
          return !!c.dataInputReservedNames.find((n) => {
            if (n.startsWith("*")) {
              const suffix = n.slice(1);
              return dataInputName?.endsWith(suffix);
            } else if (n.endsWith("*")) {
              const prefix = n.slice(0, -1);
              return dataInputName?.startsWith(prefix);
            } else {
              return n === dataInputName;
            }
          });
        })),
  }));
}

function flaggedDataOutputs<T extends { name?: string; "@_name"?: string }>(
  element: WithDataMapping,
  array: undefined | T[],
  customTasks: CustomTask[]
): undefined | { dataMapping: T; hide: boolean }[] {
  return array?.map((dataOutput) => ({
    dataMapping: dataOutput,
    hide:
      (element.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" &&
        element.loopCharacteristics.outputDataItem?.["@_name"] === (dataOutput["@_name"] ?? dataOutput.name)) ||
      (dataOutput["@_name"] ?? dataOutput.name) ===
        MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.OUT_COLLECTION ||
      (element.__$$element === "task" &&
        !!customTasks.find(
          (c) =>
            c.matches(element) &&
            !!c.dataOutputReservedNames.find((n) => n === (dataOutput["@_name"] ?? dataOutput.name))
        )),
  }));
}

export function BidirectionalDataMappingFormSection({ element }: { element: WithDataMapping }) {
  const { i18n } = useBpmnEditorI18n();
  const { customTasks } = useCustomTasks();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const inputCount = flaggedDataInputs(element, element.ioSpecification?.dataInput, customTasks ?? [])?.filter(
    (s) => !s.hide
  ).length;
  const outputCount = flaggedDataOutputs(element, element.ioSpecification?.dataOutput, customTasks ?? [])?.filter(
    (s) => !s.hide
  ).length;

  const sectionLabel = useMemo(() => {
    if (inputCount && inputCount > 0 && outputCount && outputCount > 0) {
      return ` (in: ${inputCount}, out: ${outputCount})`;
    } else if (inputCount && inputCount > 0) {
      return ` (in: ${inputCount}, out: -)`;
    } else if (outputCount && outputCount > 0) {
      return ` (in: -, out: ${outputCount})`;
    } else {
      return "";
    }
  }, [inputCount, outputCount]);

  const [showDataMappingModal, setShowDataMappingModal] = useState(false);
  const { handleSubmit, inputDataMapping, outputDataMapping, addDataMapping, removeDataMapping, handleInputChange } =
    useDataMapping(element, setShowDataMappingModal);

  return (
    <DataMappingFormSection
      sectionLabel={sectionLabel}
      elementName={element["@_name"]}
      showDataMappingModal={showDataMappingModal}
      setShowDataMappingModal={setShowDataMappingModal}
    >
      <Form onSubmit={handleSubmit} style={{ gridRowGap: 0 }}>
        <div className="kie-bpmn-editor--data-mappings--modal-section" style={{ height: "50%" }}>
          <FormSection title={i18n.dataMapping.inputs} style={{ gap: "4px" }}>
            <DataMappingsList
              flowElement={element}
              section={"input"}
              inputDataMapping={inputDataMapping}
              outputDataMapping={undefined}
              addDataMapping={addDataMapping}
              removeDataMapping={removeDataMapping}
              handleInputChange={handleInputChange}
            />
          </FormSection>
        </div>
        <br />
        <br />
        <div className="kie-bpmn-editor--data-mappings--modal-section" style={{ height: "50%" }}>
          <FormSection title={i18n.dataMapping.outputs} style={{ gap: "4px" }}>
            <DataMappingsList
              flowElement={element}
              section={"output"}
              inputDataMapping={undefined}
              outputDataMapping={outputDataMapping}
              addDataMapping={addDataMapping}
              removeDataMapping={removeDataMapping}
              handleInputChange={handleInputChange}
            />
          </FormSection>
        </div>
        {!isReadOnly && (
          <ActionGroup>
            <Button variant="primary" type="submit" onMouseUp={(e) => e.currentTarget.blur()}>
              {i18n.dataMapping.save}
            </Button>
          </ActionGroup>
        )}
      </Form>
    </DataMappingFormSection>
  );
}

export function InputOnlyAssociationFormSection({ element }: { element: WithInputDataMapping }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const inputCount = element.dataInputAssociation?.length ?? 0;
  const sectionLabel = useMemo(() => {
    if (inputCount > 0) {
      return ` (in: ${inputCount})`;
    } else {
      return ` (in: -)`;
    }
  }, [inputCount]);

  const [showDataMappingModal, setShowDataMappingModal] = useState(false);
  const { handleSubmit, inputDataMapping, outputDataMapping, addDataMapping, removeDataMapping, handleInputChange } =
    useDataMapping(element, setShowDataMappingModal);

  return (
    <DataMappingFormSection
      sectionLabel={sectionLabel}
      elementName={element["@_name"]}
      showDataMappingModal={showDataMappingModal}
      setShowDataMappingModal={setShowDataMappingModal}
    >
      <div className="kie-bpmn-editor--data-mappings--modal-section" style={{ height: "100%" }}>
        <Form onSubmit={handleSubmit} style={{ gridRowGap: 0 }}>
          <FormSection title={i18n.dataMapping.inputs} style={{ gap: "4px" }}>
            <DataMappingsList
              flowElement={element}
              section={"input"}
              inputDataMapping={inputDataMapping}
              outputDataMapping={undefined}
              addDataMapping={addDataMapping}
              removeDataMapping={removeDataMapping}
              handleInputChange={handleInputChange}
            />
          </FormSection>
          {!isReadOnly && (
            <ActionGroup>
              <Button variant="primary" type="submit" onMouseUp={(e) => e.currentTarget.blur()}>
                {i18n.dataMapping.save}
              </Button>
            </ActionGroup>
          )}
        </Form>
      </div>
    </DataMappingFormSection>
  );
}

export function OutputOnlyAssociationFormSection({ element }: { element: WithOutputDataMapping }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const outputCount = element.dataOutputAssociation?.length ?? 0;
  const sectionLabel = useMemo(() => {
    if (outputCount > 0) {
      return ` (out: ${outputCount})`;
    } else {
      return ` (out: -)`;
    }
  }, [outputCount]);

  const [showDataMappingModal, setShowDataMappingModal] = useState(false);
  const { handleSubmit, inputDataMapping, outputDataMapping, addDataMapping, removeDataMapping, handleInputChange } =
    useDataMapping(element, setShowDataMappingModal);

  return (
    <DataMappingFormSection
      sectionLabel={sectionLabel}
      elementName={element["@_name"]}
      showDataMappingModal={showDataMappingModal}
      setShowDataMappingModal={setShowDataMappingModal}
    >
      <div className="kie-bpmn-editor--data-mappings--modal-section" style={{ height: "100%" }}>
        <Form onSubmit={handleSubmit} style={{ gridRowGap: 0 }}>
          <FormSection title={i18n.dataMapping.outputs} style={{ gap: "4px" }}>
            <DataMappingsList
              flowElement={element}
              section={"output"}
              inputDataMapping={undefined}
              outputDataMapping={outputDataMapping}
              addDataMapping={addDataMapping}
              removeDataMapping={removeDataMapping}
              handleInputChange={handleInputChange}
            />
          </FormSection>
          {!isReadOnly && (
            <ActionGroup>
              <Button variant="primary" type="submit" onMouseUp={(e) => e.currentTarget.blur()}>
                {i18n.dataMapping.save}
              </Button>
            </ActionGroup>
          )}
        </Form>
      </div>
    </DataMappingFormSection>
  );
}

export function DataMappingsList({
  flowElement,
  section,
  inputDataMapping,
  outputDataMapping,
  addDataMapping,
  removeDataMapping,
  handleInputChange,
}:
  | {
      flowElement: WithDataMapping | WithInputDataMapping | WithOutputDataMapping;
      section: "input";
      inputDataMapping: DataMapping[];
      outputDataMapping: undefined;
      addDataMapping: (section: "input" | "output", args: { isExpression: boolean }) => void;
      removeDataMapping: (section: "input" | "output", index: number) => void;
      handleInputChange: (
        index: number,
        propertyName: string,
        value: string | number | undefined,
        section: "input" | "output",
        args: { isExpression: boolean }
      ) => void;
    }
  | {
      flowElement: WithDataMapping | WithInputDataMapping | WithOutputDataMapping;
      section: "output";
      inputDataMapping: undefined;
      outputDataMapping: DataMapping[];
      addDataMapping: (section: "input" | "output", args: { isExpression: boolean }) => void;
      removeDataMapping: (section: "input" | "output", index: number) => void;
      handleInputChange: (
        index: number,
        propertyName: string,
        value: string | number | undefined,
        section: "input" | "output",
        args: { isExpression: boolean }
      ) => void;
    }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const [hoveredIndex, setHoveredIndex] = useState<number | undefined>(undefined);

  const hasMessageEventDefinition =
    "eventDefinition" in flowElement
      ? flowElement.eventDefinition?.some((ed) => ed.__$$element === "messageEventDefinition")
      : false;

  const currentMappings = section === "input" ? inputDataMapping : outputDataMapping;
  const canAddMoreMappings = !hasMessageEventDefinition || currentMappings.length === 0;

  const { lastColumnLabel, entryTitle } = useMemo(() => {
    if (section === "input") {
      return {
        entryTitle: i18n.dataMapping.input,
        lastColumnLabel: "Source",
      } as const;
    } else {
      return {
        entryTitle: i18n.dataMapping.output,
        lastColumnLabel: "Target",
      } as const;
    }
  }, [i18n.dataMapping.input, i18n.dataMapping.output, section]);

  const itemDefinitionIdByDataTypes = useBpmnEditorStore(
    (s) =>
      new Map(
        s.bpmn.model.definitions.rootElement
          ?.filter((r) => r.__$$element === "itemDefinition")
          .map((i) => [i["@_structureRef"], i["@_id"]])
      )
  );

  const { customTasks } = useCustomTasks();

  return (
    <>
      {(((flaggedDataInputs(flowElement as any, inputDataMapping, customTasks ?? [])?.filter((s) => !s.hide) ?? [])
        .length > 0 ||
        (flaggedDataOutputs(flowElement as any, outputDataMapping, customTasks ?? [])?.filter((s) => !s.hide) ?? [])
          .length > 0) && (
        <>
          <div style={{ marginTop: "8px" }}>
            <div style={{ padding: "0 8px" }}>
              <Grid md={6} style={{ alignItems: "center", columnGap: "12px" }}>
                <GridItem span={5}>
                  <div>
                    <b>{i18n.dataMapping.name}</b>
                  </div>
                </GridItem>
                <GridItem span={3}>
                  <div>
                    <b>{i18n.dataMapping.dataType}</b>
                  </div>
                </GridItem>
                <GridItem span={3}>
                  <div>
                    <b>{lastColumnLabel}</b>
                  </div>
                </GridItem>
                <GridItem span={1} style={{ textAlign: "right" }}>
                  <Button
                    variant={ButtonVariant.plain}
                    style={{ paddingLeft: 0 }}
                    onClick={() => addDataMapping(section, { isExpression: false })}
                    isDisabled={!canAddMoreMappings || isReadOnly}
                  >
                    <PlusCircleIcon
                      color={
                        canAddMoreMappings && !isReadOnly
                          ? "var(--pf-c-button--m-primary--BackgroundColor)"
                          : "var(--pf-global--disabled-color--100)"
                      }
                    />
                  </Button>
                </GridItem>
              </Grid>
            </div>
          </div>
          {section === "input" &&
            flaggedDataInputs(flowElement as any, inputDataMapping, customTasks ?? [])?.flatMap(
              ({ hide, dataMapping: entry }, i) =>
                hide ? (
                  []
                ) : (
                  <div key={i} style={{ padding: "0 8px" }}>
                    <Grid
                      md={6}
                      style={{ alignItems: "center", columnGap: "12px", padding: "8px" }}
                      className={"kie-bpmn-editor--properties-panel--data-mapping-entry"}
                      onMouseEnter={() => setHoveredIndex(i)}
                      onMouseLeave={() => setHoveredIndex(undefined)}
                    >
                      <GridItem span={4}>
                        <TextInput
                          pattern="^\S*$"
                          title={i18n.dataMapping.noWhiteSpaces}
                          aria-label={"name"}
                          autoFocus={true}
                          type="text"
                          isRequired={true}
                          placeholder={i18n.dataMapping.namePlaceholder}
                          value={entry.name}
                          onChange={(e, value) => handleInputChange(i, "name", value, "input", { isExpression: false })}
                        />
                      </GridItem>
                      <GridItem span={3}>
                        <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
                          <div style={{ flex: 1 }}>
                            <ItemDefinitionRefSelector
                              value={itemDefinitionIdByDataTypes.get(entry.dtype)}
                              onChange={(_, newDataType) => {
                                handleInputChange(i, "dtype", newDataType!, "input", { isExpression: false });
                              }}
                              isDisabled={hasMessageEventDefinition}
                            />
                          </div>
                          {hasMessageEventDefinition && (
                            <Tooltip content={<div>{i18n.dataMapping.messageInfoIconHelperText}</div>}>
                              <InfoCircleIcon style={{ color: "var(--pf-global--info-color--100)" }} />
                            </Tooltip>
                          )}
                        </div>
                      </GridItem>
                      <GridItem span={4}>
                        <VariableSelector
                          value={entry.isExpression ? entry.value : entry.variableRef}
                          allowExpressions={true}
                          onChange={(value, { isExpression }) => {
                            if (isExpression) {
                              handleInputChange(i, "value" as any, value, "input", { isExpression });
                            } else {
                              handleInputChange(i, "variableRef" as any, value, "input", { isExpression });
                            }
                          }}
                        />
                      </GridItem>
                      <GridItem span={1} style={{ textAlign: "right" }}>
                        {hoveredIndex === i && (
                          <Button
                            tabIndex={9999} // Prevent tab from going to this button
                            variant={ButtonVariant.plain}
                            style={{ paddingLeft: 0 }}
                            onClick={() => removeDataMapping("input", i)}
                          >
                            <TimesIcon />
                          </Button>
                        )}
                      </GridItem>
                    </Grid>
                  </div>
                )
            )}
          {section === "output" &&
            flaggedDataOutputs(flowElement as any, outputDataMapping, customTasks ?? [])?.flatMap(
              ({ hide, dataMapping: entry }, i) =>
                hide ? (
                  []
                ) : (
                  <div key={i} style={{ padding: "0 8px" }}>
                    <Grid
                      md={6}
                      style={{ alignItems: "center", columnGap: "12px", padding: "8px" }}
                      className={"kie-bpmn-editor--properties-panel--data-mapping-entry"}
                      onMouseEnter={() => setHoveredIndex(i)}
                      onMouseLeave={() => setHoveredIndex(undefined)}
                    >
                      <GridItem span={4}>
                        <TextInput
                          aria-label={"name"}
                          autoFocus={true}
                          type="text"
                          isRequired={true}
                          placeholder={i18n.dataMapping.namePlaceholder}
                          value={entry.name}
                          onChange={(e, value) =>
                            handleInputChange(i, "name", value, "output", { isExpression: false })
                          }
                        />
                      </GridItem>
                      <GridItem span={3}>
                        <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
                          <div style={{ flex: 1 }}>
                            <ItemDefinitionRefSelector
                              value={itemDefinitionIdByDataTypes.get(entry.dtype) ?? entry.dtype}
                              onChange={(_, newDataType) => {
                                handleInputChange(i, "dtype", newDataType!, "output", { isExpression: false });
                              }}
                              isDisabled={hasMessageEventDefinition}
                            />
                          </div>
                          {hasMessageEventDefinition && (
                            <Tooltip content={<div>{i18n.dataMapping.messageInfoIconHelperText}</div>}>
                              <InfoCircleIcon style={{ color: "var(--pf-global--info-color--100)" }} />
                            </Tooltip>
                          )}
                        </div>
                      </GridItem>
                      <GridItem span={4}>
                        <VariableSelector
                          value={entry.isExpression ? entry.value : entry.variableRef}
                          allowExpressions={true}
                          onChange={(value, { isExpression }) => {
                            if (isExpression) {
                              handleInputChange(i, "value" as any, value, "output", { isExpression });
                            } else {
                              handleInputChange(i, "variableRef" as any, value, "output", { isExpression });
                            }
                          }}
                        />
                      </GridItem>
                      <GridItem span={1} style={{ textAlign: "right" }}>
                        {hoveredIndex === i && (
                          <Button
                            tabIndex={9999} // Prevent tab from going to this button
                            variant={ButtonVariant.plain}
                            style={{ paddingLeft: 0 }}
                            onClick={() => removeDataMapping("output", i)}
                          >
                            <TimesIcon />
                          </Button>
                        )}
                      </GridItem>
                    </Grid>
                  </div>
                )
            )}
        </>
      )) || (
        <>
          <div className={"kie-bpmn-editor--data-mappings--empty-state"}>
            <Bullseye>
              <EmptyState>
                <EmptyStateIcon icon={section === "input" ? ImportIcon : ExportIcon} />
                <Title headingLevel="h4">
                  {isReadOnly
                    ? i18n.dataMapping.noDataMappings(entryTitle)
                    : i18n.dataMapping.noDataMappingsYet(entryTitle)}
                </Title>
                <br />
                <EmptyStateActions>
                  <Button
                    variant={ButtonVariant.secondary}
                    onClick={() => addDataMapping(section as any, { isExpression: false })}
                  >
                    {i18n.dataMapping.addDataMapping(entryTitle)}
                  </Button>
                </EmptyStateActions>
              </EmptyState>
            </Bullseye>
          </div>
        </>
      )}
    </>
  );
}

export function useDataMapping(
  element:
    | WithDataMapping
    | (WithInputDataMapping & { dataOutputAssociation?: BPMN20__tDataOutputAssociation[] })
    | (WithOutputDataMapping & { dataInputAssociation?: BPMN20__tDataInputAssociation[] }),
  setShowDataMappingModal: React.Dispatch<React.SetStateAction<boolean>>
) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const [inputDataMapping, setInputDataMapping] = useState<DataMapping[]>([]);
  const [outputDataMapping, setOutputDataMapping] = useState<DataMapping[]>([]);

  const handleInputChange = useCallback(
    (
      index: number,
      propertyName: "name" | "dtype" | "value" | "variableRef",
      value: string,
      section: "input" | "output",
      { isExpression }: { isExpression: boolean }
    ) => {
      if (section === "input") {
        setInputDataMapping((prevInputDataMapping) => {
          const updatedInputDataMapping = [...prevInputDataMapping];
          updatedInputDataMapping[index] = { ...updatedInputDataMapping[index] };

          if (propertyName === "value" || propertyName === "variableRef") {
            delete updatedInputDataMapping[index]["value" as keyof DataMapping];
            delete updatedInputDataMapping[index]["variableRef" as keyof DataMapping];
            updatedInputDataMapping[index].isExpression = isExpression;
            if (updatedInputDataMapping[index].isExpression) {
              updatedInputDataMapping[index].value = value as string;
            } else {
              updatedInputDataMapping[index].variableRef = value as string;
            }
          } else {
            updatedInputDataMapping[index][propertyName] = value;
          }

          return updatedInputDataMapping;
        });
      } else {
        setOutputDataMapping((prevOutputDataMapping) => {
          const updatedOutputDataMapping = [...prevOutputDataMapping];
          updatedOutputDataMapping[index] = { ...updatedOutputDataMapping[index] };

          if (propertyName === "value" || propertyName === "variableRef") {
            delete updatedOutputDataMapping[index]["value" as keyof DataMapping];
            delete updatedOutputDataMapping[index]["variableRef" as keyof DataMapping];
            updatedOutputDataMapping[index].isExpression = isExpression;
            if (updatedOutputDataMapping[index].isExpression) {
              updatedOutputDataMapping[index].value = value as string;
            } else {
              updatedOutputDataMapping[index].variableRef = value as string;
            }
          } else {
            updatedOutputDataMapping[index][propertyName] = value;
          }

          return updatedOutputDataMapping;
        });
      }
    },
    []
  );

  const addDataMapping = useCallback(
    (section: "input" | "output", { isExpression }: { isExpression: boolean }) => {
      let initialDataType = "";

      const isMessageEvent =
        "eventDefinition" in element &&
        element.eventDefinition?.some((ed) => ed.__$$element === "messageEventDefinition");

      if (isMessageEvent && "eventDefinition" in element) {
        const messageEventDef = element.eventDefinition?.find((ed) => ed.__$$element === "messageEventDefinition");
        if (messageEventDef && messageEventDef.__$$element === "messageEventDefinition") {
          const messageRef = messageEventDef["@_messageRef"];
          if (messageRef) {
            const message = bpmnEditorStoreApi
              .getState()
              .bpmn.model.definitions.rootElement?.find(
                (el) => el.__$$element === "message" && el["@_id"] === messageRef
              );
            if (message && message.__$$element === "message") {
              const itemDefId = message["@_itemRef"];
              if (itemDefId) {
                const itemDef = bpmnEditorStoreApi
                  .getState()
                  .bpmn.model.definitions.rootElement?.find(
                    (el) => el.__$$element === "itemDefinition" && el["@_id"] === itemDefId
                  );
                if (itemDef && itemDef.__$$element === "itemDefinition") {
                  initialDataType = itemDef["@_structureRef"] || "";
                }
              }
            }
          }
        }
      }

      const newDataMapping: DataMapping = isExpression
        ? { name: "", dtype: initialDataType, isExpression: true, value: "" }
        : { name: "", dtype: initialDataType, isExpression: false, variableRef: undefined };

      if (section === "input") {
        setInputDataMapping([...inputDataMapping, newDataMapping]);
      } else {
        setOutputDataMapping([...outputDataMapping, newDataMapping]);
      }
    },
    [inputDataMapping, outputDataMapping, element, bpmnEditorStoreApi]
  );

  const removeDataMapping = useCallback(
    (section: "input" | "output", index: number) => {
      if (section === "input") {
        setInputDataMapping(inputDataMapping.filter((_, i) => i !== index));
      } else {
        setOutputDataMapping(outputDataMapping.filter((_, i) => i !== index));
      }
    },
    [inputDataMapping, outputDataMapping]
  );

  // populates intermediary data mapping state from the model
  useEffect(() => {
    if (!element) {
      return;
    }

    const { inputDataMapping, outputDataMapping } = getDataMapping(element);

    setInputDataMapping(inputDataMapping);
    setOutputDataMapping(outputDataMapping);
  }, [element]);

  const handleSubmit = useCallback(
    (event: React.FormEvent<HTMLFormElement>) => {
      event.preventDefault();
      if (!event.currentTarget.checkValidity()) {
        event.currentTarget.reportValidity();
        return;
      }

      bpmnEditorStoreApi.setState((s) => {
        setDataMappingForElement({
          definitions: s.bpmn.model.definitions,
          inputDataMapping,
          outputDataMapping,
          elementId: element["@_id"],
          element: element.__$$element,
        });
      });

      setShowDataMappingModal(false);
    },
    [bpmnEditorStoreApi, element, inputDataMapping, outputDataMapping, setShowDataMappingModal]
  );

  return {
    handleSubmit,
    inputDataMapping,
    outputDataMapping,
    addDataMapping,
    removeDataMapping,
    handleInputChange,
  };
}
