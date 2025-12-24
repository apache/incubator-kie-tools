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

import { BPMN20__tBusinessRuleTask } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import * as React from "react";
import { Normalized } from "../../normalization/normalize";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { BidirectionalDataMappingFormSection } from "../dataMapping/DataMappingFormSection";
import { OnEntryAndExitScriptsFormSection } from "../onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { TaskIcon } from "../../diagram/nodes/NodeIcons";
import { HelperText, HelperTextItem } from "@patternfly/react-core/dist/js/components/HelperText";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { AdhocAutostartCheckbox } from "../adhocAutostartCheckbox/AdhocAutostartCheckbox";
import { AsyncCheckbox } from "../asyncCheckbox/AsyncCheckbox";
import { SlaDueDateInput } from "../slaDueDate/SlaDueDateInput";
import { FormGroup, FormHelperText } from "@patternfly/react-core/dist/js/components/Form";
import { BUSINESS_RULE_TASK_IMPLEMENTATIONS } from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import { ToggleGroup } from "@patternfly/react-core/dist/js/components/ToggleGroup/ToggleGroup";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { ToggleGroupItem } from "@patternfly/react-core/dist/js/components/ToggleGroup";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useExternalModels } from "../../externalModels/BpmnEditorExternalModelsContext";
import { useMemo, useState } from "react";
import { Select, SelectOption } from "@patternfly/react-core/dist/js/components/Select";
import {
  associateBusinessRuleTaskWithDmnModel,
  getDmnModelBinding,
} from "../../mutations/associateBusinessRuleTaskWithDmnModel";
import { deassociateBusinessRuleTaskWithDmnModel } from "../../mutations/deassociateBusinessRuleTaskWithDmnModel";
import { MenuToggle } from "@patternfly/react-core/dist/js/components/MenuToggle";
import { MultiInstanceCheckbox } from "../multiInstanceCheckbox/MultiInstanceCheckbox";
import { MultiInstanceProperties } from "../multiInstance/MultiInstanceProperties";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useBpmnEditorI18n } from "../../i18n";

export function BusinessRuleTaskProperties({
  businessRuleTask,
}: {
  businessRuleTask: Normalized<BPMN20__tBusinessRuleTask> & { __$$element: "businessRuleTask" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const isReadOnly = useBpmnEditorStore((s) => s.settings.isReadOnly);

  const [
    availableExternalModelsNormalizedPosixPathsRelativeToTheOpenFile,
    setAvailableExternalModelsNormalizedPosixPathRelativeToTheOpenFile,
  ] = useState<string[]>([]);

  const [isDmnFilesSelectOpen, setDmnFilesSelectOpen] = useState<boolean>(false);

  const { onRequestExternalModelsAvailableToInclude, onRequestExternalModelByPath } = useExternalModels();

  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const dmnModelBinding = useMemo(() => getDmnModelBinding(businessRuleTask), [businessRuleTask]);

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={businessRuleTask["@_name"] || i18n.singleNodeProperties.businessRuleTask}
        icon={<TaskIcon variant={businessRuleTask.__$$element} isIcon={true} />}
      >
        <NameDocumentationAndId element={businessRuleTask} />
        <Divider inset={{ default: "insetXs" }} />
        <FormGroup label={i18n.singleNodeProperties.implementation}>
          <FormHelperText>
            <HelperText>
              <HelperTextItem>{i18n.singleNodeProperties.chooseRules}</HelperTextItem>
            </HelperText>
          </FormHelperText>
        </FormGroup>
        <FormGroup>
          <Flex>
            <FlexItem>
              <ToggleGroup aria-label="Implementation">
                <ToggleGroupItem
                  text={i18n.singleNodeProperties.drl}
                  isDisabled={isReadOnly}
                  isSelected={businessRuleTask["@_implementation"] === BUSINESS_RULE_TASK_IMPLEMENTATIONS.drools}
                  onChange={() => {
                    bpmnEditorStoreApi.setState((s) => {
                      const { process } = addOrGetProcessAndDiagramElements({
                        definitions: s.bpmn.model.definitions,
                      });

                      deassociateBusinessRuleTaskWithDmnModel({
                        definitions: s.bpmn.model.definitions,
                        __readonly_businessRuleTaskId: businessRuleTask["@_id"],
                      });

                      visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                        if (e["@_id"] === businessRuleTask["@_id"] && e.__$$element === businessRuleTask.__$$element) {
                          e["@_implementation"] = BUSINESS_RULE_TASK_IMPLEMENTATIONS.drools;
                          return false; // Will stop visiting.
                        }
                      });
                    });
                  }}
                />
                <ToggleGroupItem
                  text={i18n.names.dmn}
                  isDisabled={isReadOnly}
                  isSelected={businessRuleTask["@_implementation"] === BUSINESS_RULE_TASK_IMPLEMENTATIONS.dmn}
                  onChange={() => {
                    bpmnEditorStoreApi.setState((s) => {
                      const { process } = addOrGetProcessAndDiagramElements({
                        definitions: s.bpmn.model.definitions,
                      });
                      visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                        if (e["@_id"] === businessRuleTask["@_id"] && e.__$$element === businessRuleTask.__$$element) {
                          e["@_implementation"] = BUSINESS_RULE_TASK_IMPLEMENTATIONS.dmn;
                          return false; // Will stop visiting.
                        }
                      });
                    });
                  }}
                />
              </ToggleGroup>
            </FlexItem>
            {businessRuleTask["@_implementation"] === BUSINESS_RULE_TASK_IMPLEMENTATIONS.dmn && (
              <FlexItem grow={{ default: "grow" }}>
                <Select
                  toggle={(toggleRef) => (
                    <MenuToggle
                      style={{ width: "100%" }}
                      ref={toggleRef}
                      onClick={(e) => {
                        setDmnFilesSelectOpen((isOpen) => {
                          if (!(isOpen ?? false)) {
                            setAvailableExternalModelsNormalizedPosixPathRelativeToTheOpenFile([]);
                            onRequestExternalModelsAvailableToInclude?.()
                              .then(setAvailableExternalModelsNormalizedPosixPathRelativeToTheOpenFile)
                              .then(() => setDmnFilesSelectOpen(true));
                            return false;
                          } else {
                            return false;
                          }
                        });
                      }}
                      isExpanded={isDmnFilesSelectOpen}
                      isDisabled={isReadOnly}
                    >
                      {i18n.singleNodeProperties.autofill}
                    </MenuToggle>
                  )}
                  id={"select-dmn-model-file"}
                  selected={undefined}
                  isOpen={isDmnFilesSelectOpen}
                  onSelect={(e, value) => {
                    setDmnFilesSelectOpen(false);
                    if (!value) {
                      bpmnEditorStoreApi.setState((s) => {
                        deassociateBusinessRuleTaskWithDmnModel({
                          definitions: s.bpmn.model.definitions,
                          __readonly_businessRuleTaskId: businessRuleTask["@_id"],
                        });
                      });
                    } else {
                      onRequestExternalModelByPath?.(value as string).then((dmnModel) => {
                        if (!dmnModel) {
                          //FIXME: Tiago --> Treat error.
                          return;
                        }

                        bpmnEditorStoreApi.setState((s) => {
                          associateBusinessRuleTaskWithDmnModel({
                            definitions: s.bpmn.model.definitions,
                            __readonly_businessRuleTaskId: businessRuleTask["@_id"],
                            __readonly_dmnModel: {
                              normalizedPosixPathRelativeToTheOpenFile: value as string,
                              namespace: dmnModel.model.definitions["@_namespace"],
                              name: dmnModel.model.definitions["@_name"]!,
                            },
                          });
                        });
                      });
                    }
                  }}
                >
                  {availableExternalModelsNormalizedPosixPathsRelativeToTheOpenFile.map((p) => (
                    <SelectOption key={p} value={p}>
                      {p}
                    </SelectOption>
                  ))}
                </Select>
              </FlexItem>
            )}
          </Flex>
        </FormGroup>
        {businessRuleTask["@_implementation"] === BUSINESS_RULE_TASK_IMPLEMENTATIONS.drools && (
          <>
            <FormGroup label={i18n.singleNodeProperties.drlRuleFlowGroup}>
              <TextInput
                aria-label={"Signal"}
                type={"text"}
                isDisabled={isReadOnly}
                value={businessRuleTask["@_drools:ruleFlowGroup"] || ""}
                onChange={(e, newRuleFlowGroup: string) => {
                  bpmnEditorStoreApi.setState((s) => {
                    const { process } = addOrGetProcessAndDiagramElements({
                      definitions: s.bpmn.model.definitions,
                    });
                    visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                      if (e["@_id"] === businessRuleTask["@_id"] && e.__$$element === businessRuleTask.__$$element) {
                        e["@_drools:ruleFlowGroup"] = newRuleFlowGroup;
                        return false; // Will stop visiting.
                      }
                    });
                  });
                }}
                placeholder={i18n.singleNodeProperties.drlRuleFlowPlaceholder}
              />
            </FormGroup>
          </>
        )}{" "}
        {businessRuleTask["@_implementation"] === BUSINESS_RULE_TASK_IMPLEMENTATIONS.dmn && (
          <>
            <FormGroup label={i18n.singleNodeProperties.dmnModelRelativePath}>
              <TextInput
                aria-label={"DMN model relative path"}
                type={"text"}
                isDisabled={isReadOnly}
                placeholder={i18n.singleNodeProperties.dmnModelRelativePathPlaceholder}
                value={dmnModelBinding?.normalizedPosixPathRelativeToTheOpenFile ?? ""}
                onChange={(e, newNormalizedPosixPathRelativeToTheOpenFile) =>
                  bpmnEditorStoreApi.setState((s) => {
                    associateBusinessRuleTaskWithDmnModel({
                      definitions: s.bpmn.model.definitions,
                      __readonly_businessRuleTaskId: businessRuleTask["@_id"],
                      __readonly_dmnModel: {
                        normalizedPosixPathRelativeToTheOpenFile: newNormalizedPosixPathRelativeToTheOpenFile,
                        namespace: dmnModelBinding?.modelNamespace ?? "",
                        name: dmnModelBinding?.modelName ?? "",
                      },
                    });
                  })
                }
              />
            </FormGroup>

            <FormGroup label={i18n.singleNodeProperties.dmnModelNamespace}>
              <TextInput
                aria-label={"DMN model namespace"}
                type={"text"}
                isDisabled={isReadOnly}
                placeholder={i18n.singleNodeProperties.dmnModelNamePlaceholder}
                value={dmnModelBinding?.modelNamespace ?? ""}
                onChange={(e, newNamespace) =>
                  bpmnEditorStoreApi.setState((s) => {
                    associateBusinessRuleTaskWithDmnModel({
                      definitions: s.bpmn.model.definitions,
                      __readonly_businessRuleTaskId: businessRuleTask["@_id"],
                      __readonly_dmnModel: {
                        normalizedPosixPathRelativeToTheOpenFile:
                          dmnModelBinding?.normalizedPosixPathRelativeToTheOpenFile ?? "",
                        namespace: newNamespace,
                        name: dmnModelBinding?.modelName ?? "",
                      },
                    });
                  })
                }
              />
            </FormGroup>

            <FormGroup label={i18n.singleNodeProperties.dmnModelName}>
              <TextInput
                aria-label={"DMN model name"}
                type={"text"}
                isDisabled={isReadOnly}
                placeholder={i18n.singleNodeProperties.dmnModelNamePlaceholder}
                value={dmnModelBinding?.modelName ?? ""}
                onChange={(e, newName) =>
                  bpmnEditorStoreApi.setState((s) => {
                    associateBusinessRuleTaskWithDmnModel({
                      definitions: s.bpmn.model.definitions,
                      __readonly_businessRuleTaskId: businessRuleTask["@_id"],
                      __readonly_dmnModel: {
                        normalizedPosixPathRelativeToTheOpenFile:
                          dmnModelBinding?.normalizedPosixPathRelativeToTheOpenFile ?? "",
                        namespace: dmnModelBinding?.modelNamespace ?? "",
                        name: newName,
                      },
                    });
                  })
                }
              />
            </FormGroup>
          </>
        )}
        <Divider inset={{ default: "insetXs" }} />
        <SlaDueDateInput element={businessRuleTask} />
        <AsyncCheckbox element={businessRuleTask} />
        <AdhocAutostartCheckbox element={businessRuleTask} />
        <Divider inset={{ default: "insetXs" }} />
        <MultiInstanceCheckbox element={businessRuleTask} />
        {businessRuleTask.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" && (
          <MultiInstanceProperties element={businessRuleTask} />
        )}
      </PropertiesPanelHeaderFormSection>

      <BidirectionalDataMappingFormSection element={businessRuleTask} />

      <OnEntryAndExitScriptsFormSection element={businessRuleTask} />
    </>
  );
}
