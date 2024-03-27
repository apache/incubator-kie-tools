/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import {
  useTaskInboxGatewayApi,
  TaskInboxGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskInbox";
import { EmbeddedTaskInbox, TaskInboxApi } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskInbox";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { getActiveTaskStates, getAllTaskStates } from "@kie-tools/runtime-tools-process-webapp-components/dist/utils";

const TaskInboxContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const history = useHistory();
  const gatewayApi: TaskInboxGatewayApi = useTaskInboxGatewayApi();
  const taskInboxApiRef = React.useRef<TaskInboxApi>();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const unsubscriber = gatewayApi.onOpenTaskListen({
      onOpen(task: UserTaskInstance) {
        history.push(`/TaskDetails/${task.id}`);
      },
    });

    const unsubscribeUserChange = appContext.onUserChange({
      onUserChange(user) {
        taskInboxApiRef.current.taskInbox__notify(user.id);
      },
    });
    return () => {
      unsubscriber.unSubscribe();
      unsubscribeUserChange.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedTaskInbox
      {...componentOuiaProps(ouiaId, "task-inbox-container", ouiaSafe)}
      initialState={gatewayApi.taskInboxState}
      driver={gatewayApi}
      allTaskStates={getAllTaskStates()}
      activeTaskStates={getActiveTaskStates()}
      targetOrigin={appContext.getDevUIUrl()}
      ref={taskInboxApiRef}
    />
  );
};

export default TaskInboxContainer;
