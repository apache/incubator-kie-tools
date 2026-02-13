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

export function useTaskNodeMorphingActions(task: Task) {
  const bpmnEditorStoreApi = useBpmnEditorStoreApi();

  const { customTasks } = useCustomTasks();

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
        _morphTo(s, newTaskElement, [], []);
      });
    },
    [bpmnEditorStoreApi, _morphTo]
  );

  const morphToCustom = useCallback(
    (customTask: CustomTask) => {
      bpmnEditorStoreApi.setState((s) => {
        _morphTo(s, "task", customTask.dataInputReservedNames, customTask.dataOutputReservedNames);
        const { process } = addOrGetProcessAndDiagramElements({ definitions: s.bpmn.model.definitions });
        visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
          if (element["@_id"] === task["@_id"] && element.__$$element === "task") {
            const { "@_id": ignore_id, "@_name": ignore_name, ...rest } = customTask.produce();
            array[index] = { ...array[index], ...rest };
            customTask.onAdded?.(s, array[index]);
            return false; // Will stop visiting.
          }
        });
      });
    },
    [bpmnEditorStoreApi, _morphTo, task]
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
