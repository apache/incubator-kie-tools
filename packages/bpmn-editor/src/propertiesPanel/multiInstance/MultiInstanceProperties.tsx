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
  BPMN20__tDataInputAssociation,
  BPMN20__tDataOutputAssociation,
  BPMN20__tProcess,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { CodeInput } from "../codeInput/CodeInput";
import { ToggleGroup, ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { ItemDefinitionRefSelector } from "../itemDefinitionRefSelector/ItemDefinitionRefSelector";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { VariableSelector } from "../variableSelector/VariableSelector";
import "./MultiInstanceProperties.css";
import {
  getDataMapping,
  setInputDataMapping,
  MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS,
  updateDataMappingWithVariableRef,
  setOutputDataMapping,
} from "../../mutations/_dataMapping";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "../../mutations/addOrGetItemDefinitions";
import { useBpmnEditorI18n } from "../../i18n";

export type WithMultiInstanceProperties = Normalized<
  ElementFilter<
    Unpacked<NonNullable<BPMN20__tProcess["flowElement"]>>,
    "callActivity" | "subProcess" | "userTask" | "serviceTask" | "businessRuleTask"
  >
>;

export function MultiInstanceProperties({ element }: { element: WithMultiInstanceProperties }) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const ioSpecificationInputRefToVariableName = React.useCallback(
    (
      ioSpecificationReference: string | undefined,
      dataInputAssociation: BPMN20__tDataInputAssociation[] | undefined
    ) => {
      if (ioSpecificationReference === undefined || dataInputAssociation === undefined) {
        return undefined;
      }
      return dataInputAssociation.find((dia) => dia.targetRef.__$$text === ioSpecificationReference)?.sourceRef?.[0]
        .__$$text;
    },
    []
  );

  const ioSpecificationOutputRefToVariableName = React.useCallback(
    (
      ioSpecificationReference: string | undefined,
      dataOutputAssociation: BPMN20__tDataOutputAssociation[] | undefined
    ) => {
      if (ioSpecificationReference === undefined || dataOutputAssociation === undefined) {
        return undefined;
      }
      return dataOutputAssociation.find((doa) => doa.sourceRef?.[0].__$$text === ioSpecificationReference)?.targetRef
        .__$$text;
    },
    []
  );

  return (
    <>
      <FormGroup label={i18n.propertiesPanel.executionMode}>
        <ToggleGroup aria-label="Execution mode">
          <ToggleGroupItem
            text="Parallel"
            isDisabled={isReadOnly}
            isSelected={
              element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                ? element?.loopCharacteristics["@_isSequential"] === undefined
                : false
            }
            onChange={() => {
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (
                    e["@_id"] === element["@_id"] &&
                    e.__$$element === element.__$$element &&
                    e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                  ) {
                    e.loopCharacteristics["@_isSequential"] = undefined;
                  }
                });
              });
            }}
          />
          <ToggleGroupItem
            text={i18n.propertiesPanel.sequential}
            isDisabled={isReadOnly}
            isSelected={
              element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                ? element?.loopCharacteristics["@_isSequential"] === true
                : false
            }
            onChange={() => {
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (
                    e["@_id"] === element["@_id"] &&
                    e.__$$element === element.__$$element &&
                    e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                  ) {
                    e.loopCharacteristics["@_isSequential"] = true;
                  }
                });
              });
            }}
          />
        </ToggleGroup>
      </FormGroup>

      <CodeInput
        label={i18n.propertiesPanel.completionCondition}
        languages={["MVEL"]}
        value={
          element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
            ? element?.loopCharacteristics?.completionCondition?.__$$text ?? ""
            : undefined
        }
        onChange={(e, newCompletionCondition) => {
          bpmnEditorStoreApi.setState((s) => {
            const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
            visitFlowElementsAndArtifacts(process, ({ element: e }) => {
              if (
                e["@_id"] === element?.["@_id"] &&
                e.__$$element === element.__$$element &&
                e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ) {
                e.loopCharacteristics.completionCondition ??= { "@_id": generateUuid(), __$$text: "" };
                e.loopCharacteristics.completionCondition.__$$text = newCompletionCondition;
              }
            });
          });
        }}
      />

      <Divider style={{ margin: "16px" }} />

      <FormGroup label={i18n.propertiesPanel.collectionInput}>
        <VariableSelector
          value={
            element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ? ioSpecificationInputRefToVariableName(
                  element?.loopCharacteristics["loopDataInputRef"]?.__$$text,
                  element?.dataInputAssociation
                )
              : undefined
          }
          allowExpressions={false}
          onChange={(newVariableRef) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (
                  e["@_id"] === element?.["@_id"] &&
                  e.__$$element === element.__$$element &&
                  e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                ) {
                  const { inputDataMapping } = getDataMapping(element);

                  updateDataMappingWithVariableRef(
                    inputDataMapping,
                    newVariableRef,
                    MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.IN_COLLECTION
                  );

                  const itemDefinitionIdByDataTypes = new Map(
                    s.bpmn.model.definitions.rootElement
                      ?.filter((r) => r.__$$element === "itemDefinition")
                      .map((i) => [i["@_structureRef"]!, i["@_id"]!])
                  );

                  if (e.ioSpecification) {
                    setInputDataMapping(itemDefinitionIdByDataTypes, inputDataMapping, e.ioSpecification, e);
                  }

                  if (newVariableRef) {
                    const ioSpecificationReference = e.ioSpecification?.dataInput?.find(
                      (dIn) =>
                        dIn["@_name"] === MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.IN_COLLECTION
                    )?.["@_id"];
                    e.loopCharacteristics.loopDataInputRef = { __$$text: ioSpecificationReference ?? "" };
                  } else {
                    e.loopCharacteristics.loopDataInputRef = undefined;
                  }

                  return false; // Will stop visiting.
                }
              });
            });
          }}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.dataInput}>
        <TextInput
          aria-label={"Data input"}
          type={"text"}
          isDisabled={isReadOnly}
          placeholder={i18n.propertiesPanel.dataInputPlaceholder}
          value={
            element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ? element?.loopCharacteristics["inputDataItem"]?.["@_name"] ?? ""
              : undefined
          }
          onChange={(e, newDataInput) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });

              // Ensure default data type exists
              addOrGetItemDefinitions({ definitions: s.bpmn.model.definitions, dataType: DEFAULT_DATA_TYPES.OBJECT });

              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
                  const { inputDataMapping } = getDataMapping(element);

                  if (e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics") {
                    // Remove previous mapping
                    const prevName = e.loopCharacteristics.inputDataItem?.["@_name"];
                    if (prevName !== undefined) {
                      updateDataMappingWithVariableRef(inputDataMapping, undefined, prevName);
                    }

                    // Add new mapping
                    updateDataMappingWithVariableRef(inputDataMapping, newDataInput, newDataInput);

                    const itemDefinitionIdByDataTypes = new Map(
                      s.bpmn.model.definitions.rootElement
                        ?.filter((r) => r.__$$element === "itemDefinition")
                        .map((i) => [i["@_structureRef"]!, i["@_id"]!])
                    );

                    if (e.ioSpecification) {
                      setInputDataMapping(itemDefinitionIdByDataTypes, inputDataMapping, e.ioSpecification, e);

                      const ioSpecificationReference = e.ioSpecification?.dataInput?.find(
                        (dIn) =>
                          dIn["@_name"] === MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.IN_COLLECTION
                      )?.["@_id"];

                      e.loopCharacteristics.loopDataInputRef = ioSpecificationReference
                        ? { __$$text: ioSpecificationReference }
                        : undefined;
                    }

                    // Update inputDataItem name
                    e.loopCharacteristics.inputDataItem ??= { "@_id": generateUuid() };
                    e.loopCharacteristics.inputDataItem["@_name"] = newDataInput;
                  }

                  return false; // Stop visiting
                }
              });
            })
          }
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.dataType}>
        <ItemDefinitionRefSelector
          value={
            element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ? element.loopCharacteristics["inputDataItem"]?.["@_itemSubjectRef"]
              : undefined
          }
          onChange={(newDataInputDataType) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (
                  e["@_id"] === element?.["@_id"] &&
                  e.__$$element === element.__$$element &&
                  e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                ) {
                  e.loopCharacteristics.inputDataItem ??= { "@_id": generateUuid() };
                  e.loopCharacteristics.inputDataItem["@_itemSubjectRef"] = newDataInputDataType;

                  return false; // Will stop visiting.
                }
              });
            })
          }
        />
      </FormGroup>

      <Divider style={{ margin: "16px" }} />

      <FormGroup label={i18n.propertiesPanel.collectionOutput}>
        <VariableSelector
          value={
            element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ? ioSpecificationOutputRefToVariableName(
                  element?.loopCharacteristics["loopDataOutputRef"]?.__$$text,
                  element?.dataOutputAssociation
                )
              : undefined
          }
          allowExpressions={false}
          onChange={(newVariableRef) => {
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({
                definitions: s.bpmn.model.definitions,
              });

              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (
                  e["@_id"] === element?.["@_id"] &&
                  e.__$$element === element.__$$element &&
                  e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                ) {
                  const { outputDataMapping } = getDataMapping(element);

                  updateDataMappingWithVariableRef(
                    outputDataMapping,
                    newVariableRef,
                    MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.OUT_COLLECTION
                  );

                  const itemDefinitionIdByDataTypes = new Map(
                    s.bpmn.model.definitions.rootElement
                      ?.filter((r) => r.__$$element === "itemDefinition")
                      .map((i) => [i["@_structureRef"]!, i["@_id"]!])
                  );

                  if (e.ioSpecification) {
                    setOutputDataMapping(itemDefinitionIdByDataTypes, outputDataMapping, e.ioSpecification, e);
                  }

                  if (newVariableRef) {
                    const ioSpecificationReference = e.ioSpecification?.dataOutput?.find(
                      (dOut) =>
                        dOut["@_name"] === MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.OUT_COLLECTION
                    )?.["@_id"];
                    e.loopCharacteristics.loopDataOutputRef = { __$$text: ioSpecificationReference ?? "" };
                  } else {
                    e.loopCharacteristics.loopDataOutputRef = undefined;
                  }

                  return false; // Will stop visiting.
                }
              });
            });
          }}
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.dataOutput}>
        <TextInput
          aria-label={"Data output"}
          type={"text"}
          isDisabled={isReadOnly}
          placeholder={i18n.propertiesPanel.dataOutputPlaceholder}
          value={
            element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ? element?.loopCharacteristics["outputDataItem"]?.["@_name"] ?? ""
              : undefined
          }
          onChange={(e, newDataOutput) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });

              // Make sure String data type is available
              addOrGetItemDefinitions({ definitions: s.bpmn.model.definitions, dataType: DEFAULT_DATA_TYPES.OBJECT });

              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (e["@_id"] === element?.["@_id"] && e.__$$element === element.__$$element) {
                  const { outputDataMapping } = getDataMapping(element);

                  if (e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics") {
                    // Remove prev
                    if (e.loopCharacteristics.outputDataItem?.["@_name"] !== undefined) {
                      updateDataMappingWithVariableRef(
                        outputDataMapping,
                        undefined,
                        e.loopCharacteristics.outputDataItem?.["@_name"]
                      );
                    }

                    // Add new
                    updateDataMappingWithVariableRef(outputDataMapping, newDataOutput, newDataOutput);

                    const itemDefinitionIdByDataTypes = new Map(
                      s.bpmn.model.definitions.rootElement
                        ?.filter((r) => r.__$$element === "itemDefinition")
                        .map((i) => [i["@_structureRef"]!, i["@_id"]!])
                    );

                    if (e.ioSpecification) {
                      setOutputDataMapping(itemDefinitionIdByDataTypes, outputDataMapping, e.ioSpecification, e);

                      const ioSpecificationReference = e.ioSpecification?.dataOutput?.find(
                        (dOut) =>
                          dOut["@_name"] === MULTI_INSTANCE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS.OUT_COLLECTION
                      )?.["@_id"];

                      e.loopCharacteristics.loopDataOutputRef = ioSpecificationReference
                        ? { __$$text: ioSpecificationReference }
                        : undefined;
                    }

                    e.loopCharacteristics.outputDataItem ??= { "@_id": generateUuid() };
                    e.loopCharacteristics.outputDataItem["@_name"] = newDataOutput;
                  }

                  return false; // Will stop visiting.
                }
              });
            })
          }
        />
      </FormGroup>

      <FormGroup label={i18n.propertiesPanel.dataType}>
        <ItemDefinitionRefSelector
          value={
            element?.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
              ? element.loopCharacteristics.outputDataItem?.["@_itemSubjectRef"]
              : undefined
          }
          onChange={(newDataOutputDataType) =>
            bpmnEditorStoreApi.setState((s) => {
              const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
              visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                if (
                  e["@_id"] === element?.["@_id"] &&
                  e.__$$element === element.__$$element &&
                  e.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics"
                ) {
                  e.loopCharacteristics.outputDataItem ??= { "@_id": generateUuid() };
                  e.loopCharacteristics.outputDataItem["@_itemSubjectRef"] = newDataOutputDataType;
                }
              });
            })
          }
        />
      </FormGroup>
    </>
  );
}
