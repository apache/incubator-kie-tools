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
import { BPMN20__tUserTask } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../../normalization/normalize";
import { NameDocumentationAndId } from "../nameDocumentationAndId/NameDocumentationAndId";
import { BidirectionalDataMappingFormSection } from "../dataMapping/DataMappingFormSection";
import { ReassignmentsFormSection } from "../reassignments/Reassignments";
import { NotificationsFormSection } from "../notifications/Notifications";
import { OnEntryAndExitScriptsFormSection } from "../onEntryAndExitScripts/OnEntryAndExitScriptsFormSection";
import { TaskIcon } from "../../diagram/nodes/NodeIcons";
import { PropertiesPanelHeaderFormSection } from "./_PropertiesPanelHeaderFormSection";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { AsyncCheckbox } from "../asyncCheckbox/AsyncCheckbox";
import { SlaDueDateInput } from "../slaDueDate/SlaDueDateInput";
import { MultiInstanceCheckbox } from "../multiInstanceCheckbox/MultiInstanceCheckbox";
import { MultiInstanceProperties } from "../multiInstance/MultiInstanceProperties";
import { AdhocAutostartCheckbox } from "../adhocAutostartCheckbox/AdhocAutostartCheckbox";
import { FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { TextArea } from "@patternfly/react-core/dist/js/components/TextArea/TextArea";
import { useBpmnEditorStore, useBpmnEditorStoreApi } from "../../store/StoreContext";
import { visitFlowElementsAndArtifacts } from "../../mutations/_elementVisitor";
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { addOrGetProcessAndDiagramElements } from "../../mutations/addOrGetProcessAndDiagramElements";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox/Checkbox";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { useCallback } from "react";
import { USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING } from "@kie-tools/bpmn-marshaller/dist/drools-extension";
import {
  getDataMapping,
  setDataMappingForElement,
  updateDataMappingWithValue,
  UserTaskReservedDataMappingInputNames,
} from "../../mutations/_dataMapping";
import { addOrGetItemDefinitions, DEFAULT_DATA_TYPES } from "../../mutations/addOrGetItemDefinitions";
import { useBpmnEditorI18n } from "../../i18n";

export function UserTaskProperties({
  userTask,
}: {
  userTask: Normalized<BPMN20__tUserTask> & { __$$element: "userTask" };
}) {
  const { i18n } = useBpmnEditorI18n();
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();
  const settings = useBpmnEditorStore((s) => s.settings);

  const handleChange = (fieldName: UserTaskReservedDataMappingInputNames, newValue: string | boolean | undefined) => {
    // Handle empty strings as undefined, but "false" should be properly converted to a string
    const valueAsStringOrUndefined = newValue || typeof newValue === "boolean" ? String(newValue) : undefined;
    bpmnEditorStoreApi.setState((s) => {
      const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });

      // Make sure String data type is available
      addOrGetItemDefinitions({ definitions: s.bpmn.model.definitions, dataType: DEFAULT_DATA_TYPES.STRING });

      visitFlowElementsAndArtifacts(process, ({ element: e }) => {
        if (e["@_id"] === userTask?.["@_id"] && e.__$$element === userTask.__$$element) {
          const { inputDataMapping, outputDataMapping } = getDataMapping(e);

          updateDataMappingWithValue(inputDataMapping, valueAsStringOrUndefined, fieldName);

          setDataMappingForElement({
            definitions: s.bpmn.model.definitions,
            element: e.__$$element,
            elementId: e["@_id"],
            inputDataMapping,
            outputDataMapping,
          });
        }
      });
    });
  };

  const getValue = useCallback(
    (fieldName: string) =>
      getDataMapping(userTask)
        .inputDataMapping.filter((dm) => dm.isExpression)
        .find((dm) => dm.name === fieldName)?.value ?? "",
    [userTask]
  );

  return (
    <>
      <PropertiesPanelHeaderFormSection
        title={userTask["@_name"] || i18n.singleNodeProperties.userTask}
        icon={<TaskIcon variant={userTask.__$$element} isIcon={true} />}
      >
        <NameDocumentationAndId element={userTask} />

        <Divider inset={{ default: "insetXs" }} />

        <FormGroup label={i18n.singleNodeProperties.taskName}>
          <TextInput
            aria-label={"Task Name"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.TASK_NAME)}
            onChange={(e, newTaskName) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.TASK_NAME, newTaskName)
            }
            placeholder={i18n.singleNodeProperties.taskNamePlaceholder}
          />
        </FormGroup>
        <FormGroup label={i18n.propertiesPanel.notificationsProperties.subject}>
          <TextInput
            aria-label={"Subject"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.COMMENT)}
            onChange={(e, newSubject) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.COMMENT, newSubject)
            }
            placeholder={i18n.singleNodeProperties.subjectPlaceholder}
          />
        </FormGroup>
        <FormGroup label={i18n.singleNodeProperties.content}>
          <TextArea
            aria-label={"Content"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CONTENT)}
            onChange={(e, newContent) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CONTENT, newContent)
            }
            placeholder={i18n.singleNodeProperties.contentPlaceholder}
          />
        </FormGroup>

        <FormGroup label={i18n.singleNodeProperties.taskPriority}>
          <TextInput
            aria-label={"Task Priority"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.PRIORITY)}
            onChange={(e, newPriority) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.PRIORITY, newPriority)
            }
            placeholder={i18n.singleNodeProperties.taskPriorityPlaceholder}
          />
        </FormGroup>
        <FormGroup label={i18n.singleNodeProperties.description}>
          <TextArea
            aria-label={"Description"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.DESCRIPTION)}
            onChange={(e, newDescription) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.DESCRIPTION, newDescription)
            }
            placeholder={i18n.singleNodeProperties.descriptionPlaceholder}
            style={{ resize: "vertical", minHeight: "40px" }}
            rows={3}
          />
        </FormGroup>
        <FormGroup>
          <Checkbox
            label={i18n.singleNodeProperties.skippable}
            aria-label={"Skippable"}
            id="kie-bpmn-editor--properties-panel--skippable-checkbox"
            isDisabled={settings.isReadOnly}
            isChecked={
              getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.SKIPPABLE) === "true"
                ? true
                : false
            }
            onChange={(e, newSkippable) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.SKIPPABLE, newSkippable)
            }
          />
        </FormGroup>

        <Divider inset={{ default: "insetXs" }} />

        <FormGroup label={i18n.singleNodeProperties.actors}>
          <TextInput
            aria-label={"Potential Owner"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={
              userTask?.resourceRole
                ?.filter((role) => role.__$$element === "potentialOwner")
                .map((potentialOwner) => {
                  return potentialOwner?.resourceAssignmentExpression?.expression?.__$$text;
                })
                .join(",") ?? ""
            }
            onChange={(e, newValue) =>
              bpmnEditorStoreApi.setState((s) => {
                const { process } = addOrGetProcessAndDiagramElements({
                  definitions: s.bpmn.model.definitions,
                });

                visitFlowElementsAndArtifacts(process, ({ element: e }) => {
                  if (e["@_id"] === userTask?.["@_id"] && e.__$$element === userTask.__$$element) {
                    if (newValue) {
                      e.resourceRole ??= [];
                      const resourceRoleList: typeof e.resourceRole = [];
                      newValue.split(",").forEach((actorName) => {
                        resourceRoleList.push({
                          "@_id": generateUuid(),
                          __$$element: "potentialOwner",
                          resourceAssignmentExpression: {
                            "@_id": generateUuid(),
                            expression: {
                              "@_id": generateUuid(),
                              __$$element: "formalExpression",
                              __$$text: actorName.trimStart(),
                            },
                          },
                        });
                      });
                      e.resourceRole = resourceRoleList;
                    } else if (e.resourceRole) {
                      e.resourceRole = e.resourceRole.filter((role) => role.__$$element !== "potentialOwner");
                      if (!e.resourceRole.length) {
                        delete e.resourceRole;
                      }
                    }
                  }
                });
              })
            }
            placeholder={i18n.singleNodeProperties.actorsPlaceholder}
          />
        </FormGroup>

        <FormGroup label={i18n.singleNodeProperties.groups}>
          <TextInput
            aria-label={"Groups"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.GROUP_ID)}
            onChange={(e, newGroups) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.GROUP_ID, newGroups)
            }
            placeholder={i18n.singleNodeProperties.groupPlaceholder}
          />
        </FormGroup>

        <FormGroup label={i18n.singleNodeProperties.createdBy}>
          <TextInput
            aria-label={"Created by"}
            type={"text"}
            isDisabled={settings.isReadOnly}
            value={getValue(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CREATED_BY)}
            onChange={(e, newCreatedBy) =>
              handleChange(USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CREATED_BY, newCreatedBy)
            }
            placeholder={i18n.singleNodeProperties.createdByPlaceholder}
          />
        </FormGroup>

        <Divider inset={{ default: "insetXs" }} />

        <SlaDueDateInput element={userTask} />
        <AsyncCheckbox element={userTask} />
        <AdhocAutostartCheckbox element={userTask} />

        <Divider inset={{ default: "insetXs" }} />

        <MultiInstanceCheckbox element={userTask} />
        {userTask.loopCharacteristics?.__$$element === "multiInstanceLoopCharacteristics" && (
          <MultiInstanceProperties element={userTask} />
        )}
      </PropertiesPanelHeaderFormSection>

      <BidirectionalDataMappingFormSection element={userTask} />

      <ReassignmentsFormSection element={userTask} />

      <NotificationsFormSection element={userTask} />

      <OnEntryAndExitScriptsFormSection element={userTask} />
    </>
  );
}
