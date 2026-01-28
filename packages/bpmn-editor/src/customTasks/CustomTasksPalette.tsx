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
import { useCustomTasks } from "./BpmnEditorCustomTasksContextProvider";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { NODE_TYPES } from "../diagram/BpmnDiagramDomain";
import "./CustomTasksPalette.css";
import { CustomTask } from "../BpmnEditor";
import { useMemo } from "react";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useBpmnEditorI18n } from "../i18n";

export function CustomTasksPalette({ onDragStart }: { onDragStart: any }) {
  const { i18n } = useBpmnEditorI18n();
  const { customTasks } = useCustomTasks();

  const customTasksByGroup = useMemo(() => {
    return (customTasks ?? []).reduce((acc, ct) => {
      let a = acc.get(ct.displayGroup);
      if (a) {
        a.push(ct);
      } else {
        a = [ct];
      }
      return acc.set(ct.displayGroup, a);
    }, new Map<string, CustomTask[]>());
  }, [customTasks]);

  return (
    <>
      {(customTasks ?? []).length <= 0 ? (
        <EmptyState>
          <EmptyStateIcon icon={CubesIcon} color={"darkgray"} />
          <EmptyStateBody style={{ color: "darkgray" }}>{i18n.customTasks.emptyStateBody}</EmptyStateBody>
        </EmptyState>
      ) : (
        [...customTasksByGroup.entries()].map(([group, customTasks]) => (
          <React.Fragment key={group}>
            <h1>{group}</h1>
            {(customTasks ?? []).map((customTask) => (
              <Flex
                key={customTask.id}
                className={"kie-bpmn-editor--custom-tasks-palette--custom-task"}
                gap={{ default: "gapSm" }}
                draggable={true}
                onDragStart={(event) => onDragStart(event, NODE_TYPES.task, "task", customTask.produce())}
              >
                <FlexItem>{customTask.iconSvgElement}</FlexItem>
                <FlexItem
                  shrink={{ default: "shrink" }}
                  grow={{ default: "grow" }}
                  className={"kie-bpmn-editor--custom-tasks-palette--custom-task--name"}
                >
                  {customTask.displayName}
                </FlexItem>
              </Flex>
            ))}
          </React.Fragment>
        ))
      )}
    </>
  );
}
