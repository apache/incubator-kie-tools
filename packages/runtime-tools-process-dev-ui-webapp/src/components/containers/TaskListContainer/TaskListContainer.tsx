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
import { useNavigate } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { useTaskListChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskList";
import { EmbeddedTaskList, TaskListApi } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskList";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { getActiveTaskStates, getAllTaskStates } from "@kie-tools/runtime-tools-process-webapp-components/dist/utils";

const TaskListContainer: React.FC = () => {
  const navigate = useNavigate();
  const channelApi = useTaskListChannelApi();
  const taskListApiRef = React.useRef<TaskListApi>();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const unsubscriber = channelApi.taskList__onOpenTaskListen({
      onOpen(task: UserTaskInstance) {
        navigate({ pathname: `/TaskDetails/${task.id}` });
      },
    });

    const unsubscribeUserChange = appContext.onUserChange({
      onUserChange(user) {
        taskListApiRef.current.taskList__notify(user.id);
      },
    });
    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
      unsubscribeUserChange.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedTaskList
      channelApi={channelApi}
      allTaskStates={getAllTaskStates()}
      activeTaskStates={getActiveTaskStates()}
      targetOrigin={appContext.getDevUIUrl()}
      ref={taskListApiRef}
    />
  );
};

export default TaskListContainer;
