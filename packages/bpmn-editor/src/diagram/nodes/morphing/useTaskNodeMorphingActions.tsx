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
import { useCallback, useMemo } from "react";
import { visitFlowElementsAndArtifacts } from "../../../mutations/_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "../../../mutations/addOrGetProcessAndDiagramElements";
import { useBpmnEditorStoreApi } from "../../../store/StoreContext";
import { CallActivityIcon, TaskIcon } from "../NodeIcons";
import { keepIntersection } from "./keepIntersection";
import {
  DATA_INPUT_RESERVED_NAMES,
  getDataMapping,
  setDataMappingForElement,
  Task,
} from "../../../mutations/_dataMapping";
import { useCustomTasks } from "../../../customTasks/BpmnEditorCustomTasksContextProvider";
import { CustomTask } from "../../../BpmnEditor";
import { WritableDraft } from "immer";
import { State } from "../../../store/Store";
import { deleteInterfaceAndOperation } from "../../../mutations/deleteInterfaceAndOperation";

function getTaskDataMappings(
  process: ReturnType<typeof addOrGetProcessAndDiagramElements>["process"],
  taskId: string,
  taskElement: Task["__$$element"]
): ReturnType<typeof getDataMapping> {
  let result: ReturnType<typeof getDataMapping> = { inputDataMapping: [], outputDataMapping: [] };

  visitFlowElementsAndArtifacts(process, ({ element }) => {
    if (element["@_id"] === taskId && element.__$$element === taskElement) {
      result = getDataMapping(element as Task);
      return false;
    }
  });

  return result;
}

function getSystemDefinedMappings(
  s: WritableDraft<State>,
  task: Task,
  customTask: CustomTask | undefined
): { inputNames: string[]; outputNames: string[] } {
  if (!customTask) {
    return { inputNames: [], outputNames: [] };
  }

  if (!customTask.onAdded) {
    return { inputNames: [], outputNames: [] };
  }

  const tempTask = { ...task, __$$element: "task" as const };
  customTask.onAdded(s, tempTask);
  const { inputDataMapping, outputDataMapping } = getDataMapping(tempTask);

  return {
    inputNames: inputDataMapping.map((dm) => dm.name),
    outputNames: outputDataMapping.map((dm) => dm.name),
  };
}

export function useTaskNodeMorphingActions(task: Task) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const { customTasks } = useCustomTasks();

  const matchingCustomTask = useMemo(() => {
    return task.__$$element === "task" ? customTasks?.find((ct) => ct.matches(task)) : undefined;
  }, [task, customTasks]);

  const _morphTo = useCallback(
    (
      s: WritableDraft<State>,
      newTaskElement: Task["__$$element"],
      reservedInputs: string[],
      reservedOutputs: string[]
    ) => {
      const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
      visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
        if (element["@_id"] === task["@_id"] && element.__$$element === task.__$$element) {
          keepIntersection({
            fromElement: element.__$$element,
            toElement: newTaskElement,
            srcObj: element,
            targetObj: array[index],
          });

          if (
            array[index].__$$element === "businessRuleTask" ||
            array[index].__$$element === "userTask" ||
            array[index].__$$element === "serviceTask"
          ) {
            // cleanup implementation between these types because the value is not comptabible between them.
            array[index]["@_implementation"] = undefined;
          }

          if (array[index].__$$element === "task") {
            // cleanup custom task modifiers
            array[index]["@_drools:taskName"] = undefined;
          }

          const { inputDataMapping, outputDataMapping } = getDataMapping(element);

          const filteredInputDataMapping = inputDataMapping.filter(
            (dm) =>
              ![...(DATA_INPUT_RESERVED_NAMES.get(element.__$$element) ?? []), ...reservedInputs].find(
                (n) => dm.name === n
              )
          );

          const filteredOutputDataMapping = outputDataMapping.filter(
            (dm) => !reservedOutputs.find((n) => dm.name === n)
          );

          setDataMappingForElement({
            definitions: s.bpmn.model.definitions,
            elementId: element["@_id"],
            element: element.__$$element,
            inputDataMapping: filteredInputDataMapping,
            outputDataMapping: filteredOutputDataMapping,
          });

          array[index].__$$element = newTaskElement;
          return false; // Will stop visiting.
        }
      });

      if (task.__$$element === "serviceTask" && newTaskElement !== "serviceTask" && task["@_operationRef"]) {
        deleteInterfaceAndOperation({
          definitions: s.bpmn.model.definitions,
          operationRef: task["@_operationRef"],
        });
      }
    },
    [task]
  );

  const morphTo = useCallback(
    (newTaskElement: Task["__$$element"]) => {
      // 1 - Task
      // 2 - User
      // 3 - Business Rule
      // 4 - Service
      // 5 - Script
      // 6 - Call activity
      bpmnEditorStoreApi.setState((s) => {
        // Get system-defined mappings from source custom task (if any)
        const systemMappings = getSystemDefinedMappings(s, task, matchingCustomTask);

        // Remove system-defined mappings when morphing away from custom task
        _morphTo(s, newTaskElement, systemMappings.inputNames, systemMappings.outputNames);
      });
    },
    [bpmnEditorStoreApi, _morphTo, task, matchingCustomTask]
  );

  const morphToCustom = useCallback(
    (customTask: CustomTask) => {
      bpmnEditorStoreApi.setState((s) => {
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });

        // Get system-defined mappings from source custom task (if any)
        const sourceSystemMappings = getSystemDefinedMappings(s, task, matchingCustomTask);

        // Morph to task and filter out:
        // 1. Source custom task's system-defined mappings
        // 2. Target custom task's reserved names (to avoid conflicts)
        _morphTo(
          s,
          "task",
          [...sourceSystemMappings.inputNames, ...customTask.dataInputReservedNames],
          [...sourceSystemMappings.outputNames, ...customTask.dataOutputReservedNames]
        );

        // Get user-defined mappings after filtering
        const userDefinedMappings = getTaskDataMappings(process, task["@_id"], "task");

        // Apply custom task properties and merge mappings
        visitFlowElementsAndArtifacts(process, ({ array, index, element }) => {
          if (element["@_id"] === task["@_id"] && element.__$$element === "task") {
            // Apply custom task properties
            const { "@_id": ignore_id, "@_name": ignore_name, ...rest } = customTask.produce();
            array[index] = { ...array[index], ...rest };

            // Call onAdded to create system-defined mappings
            customTask.onAdded?.(s, array[index]);

            // Get system-defined mappings created by onAdded
            const systemMappings = getDataMapping(array[index]);

            // Merge: system-defined first, then user-defined
            const mergedInputs = [
              ...systemMappings.inputDataMapping,
              ...userDefinedMappings.inputDataMapping.filter(
                (existing) => !systemMappings.inputDataMapping.find((sys) => sys.name === existing.name)
              ),
            ];

            const mergedOutputs = [
              ...systemMappings.outputDataMapping,
              ...userDefinedMappings.outputDataMapping.filter(
                (existing) => !systemMappings.outputDataMapping.find((sys) => sys.name === existing.name)
              ),
            ];

            // Set final merged mappings
            setDataMappingForElement({
              definitions: s.bpmn.model.definitions,
              elementId: element["@_id"],
              element: element.__$$element,
              inputDataMapping: mergedInputs,
              outputDataMapping: mergedOutputs,
            });

            return false; // Will stop visiting.
          }
        });
      });
    },
    [bpmnEditorStoreApi, _morphTo, task, matchingCustomTask]
  );

  const morphingActions = useMemo(() => {
    return [
      {
        icon: <TaskIcon />,
        key: "1",
        title: "Task",
        id: "task",
        action: () => morphTo("task"),
      } as const,
      {
        icon: <TaskIcon variant={"userTask"} isIcon={true} />,
        key: "2",
        title: "User task",
        id: "userTask",
        action: () => morphTo("userTask"),
      } as const,
      {
        icon: <TaskIcon variant={"businessRuleTask"} isIcon={true} />,
        key: "3",
        title: "Business Rule task",
        id: "businessRuleTask",
        action: () => morphTo("businessRuleTask"),
      } as const,
      {
        icon: <TaskIcon variant={"serviceTask"} isIcon={true} />,
        key: "4",
        title: "Service task",
        id: "serviceTask",
        action: () => morphTo("serviceTask"),
      } as const,
      {
        icon: <TaskIcon variant={"scriptTask"} isIcon={true} />,
        key: "5",
        title: "Script task",
        id: "scriptTask",
        action: () => morphTo("scriptTask"),
      } as const,
      {
        icon: <CallActivityIcon />,
        key: "6",
        title: "Call activity",
        id: "callActivity",
        action: () => morphTo("callActivity"),
      } as const,
      ...(customTasks ?? []).map(
        (ct) =>
          ({
            icon: ct.iconSvgElement,
            key: undefined,
            title: ct.displayName,
            id: ct.id,
            action: () => morphToCustom(ct),
          }) as const
      ),
    ];
  }, [customTasks, morphTo, morphToCustom]);

  return morphingActions;
}
