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
import React, { useCallback, useMemo, useState } from "react";
import { useHistory } from "react-router-dom";
import {
  Drawer,
  DrawerActions,
  DrawerCloseButton,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  TaskInboxGatewayApi,
  useTaskInboxGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskInbox";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";
import { EmbeddedTaskDetails } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskDetails";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { TaskState } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskDetails";
import { TaskForm } from "./TaskForm";
import { FormNotification, Notification } from "./components";
import { useRuntime, useRuntimeDispatch, useRuntimeInfo, useRuntimeSpecificRoutes } from "../runtime/RuntimeContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { isOpenIdConnectAuthSession, useAuthSessions } from "../authSessions";

interface Props {
  taskId?: string;
}

export const TaskDetails: React.FC<Props> = ({ taskId }) => {
  const taskInboxGatewayApi: TaskInboxGatewayApi = useTaskInboxGatewayApi();
  const history = useHistory();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const { username, accessToken } = useRuntimeInfo();
  const { impersonationUsername } = useRuntime();
  const { currentAuthSession } = useAuthSessions();
  const { refreshToken } = useRuntimeDispatch();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [userTask, setUserTask] = useState<UserTaskInstance>();
  const [notification, setNotification] = useState<Notification>();
  const [error, setError] = useState();
  const [isDetailsExpanded, setIsDetailsExpanded] = useState<boolean>(false);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!taskId) {
          return;
        }
        setIsLoading(true);
        setIsDetailsExpanded(false);
        taskInboxGatewayApi
          .getTaskById(taskId)
          .then((taskData) => {
            if (canceled.get()) {
              return;
            }
            setUserTask(taskData);
            if (
              (username && !taskData?.potentialUsers?.includes(username)) ||
              (!(username || impersonationUsername) && (taskData?.potentialUsers?.length ?? 0) > 0)
            ) {
              setIsDetailsExpanded(true);
            } else {
              setIsDetailsExpanded(false);
            }
          })
          .catch((e) => {
            setError(e);
            setIsDetailsExpanded(true);
          })
          .finally(() => {
            setIsLoading(false);
          });
      },
      [impersonationUsername, taskId, taskInboxGatewayApi, username]
    )
  );

  const goToTasks = useCallback(() => {
    taskInboxGatewayApi.clearOpenTask();
    history.push(runtimeRoutes.tasks());
  }, [history, runtimeRoutes, taskInboxGatewayApi]);

  const showNotification = useCallback(
    (notificationType: "error" | "success", submitMessage: string, notificationDetails?: string) => {
      setNotification({
        type: notificationType,
        message: submitMessage,
        details: notificationDetails,
        customAction: {
          label: "Go to Tasks",
          onClick: () => {
            setNotification(undefined);
            goToTasks();
          },
        },
        close: () => {
          setNotification(undefined);
        },
      });
    },
    [goToTasks]
  );

  const onSubmitSuccess = useCallback(
    (phase: string) => {
      const message = `Task '${userTask!.referenceName}' successfully transitioned to phase '${phase}'.`;

      showNotification("success", message);
    },
    [showNotification, userTask]
  );

  const onSubmitError = useCallback(
    (phase: any, details?: string) => {
      const message = `Task '${userTask!.referenceName}' couldn't transition to phase '${phase}'.`;

      showNotification("error", message, details);
    },
    [showNotification, userTask]
  );

  const onUnauthorized = useCallback(
    async (e: any) => {
      if (isOpenIdConnectAuthSession(currentAuthSession)) {
        await refreshToken(currentAuthSession);
      } else {
        throw new Error(`Got unauthorized response for unauthenticated runtime! ${e}`);
      }
    },
    [currentAuthSession, refreshToken]
  );

  const onViewDetailsClick = useCallback(() => {
    setIsDetailsExpanded((prev) => !prev);
  }, []);

  const onDetailsCloseClick = useCallback(() => {
    setIsDetailsExpanded(false);
  }, []);

  const content = useMemo(() => {
    if (isLoading) {
      return (
        <Grid hasGutter md={1} className={"kogito-management-console__full-size"}>
          <GridItem span={12} className={"kogito-management-console__full-size"}>
            <Card className={"kogito-management-console__full-size"}>
              <Bullseye>
                <KogitoSpinner spinnerText={`Loading details for task: ${taskId}`} />
              </Bullseye>
            </Card>
          </GridItem>
        </Grid>
      );
    }

    if (error) {
      return (
        <Grid hasGutter md={1} className={"kogito-management-console__full-size"}>
          <GridItem span={12} className={"kogito-management-console__full-size"}>
            <Card className={"kogito-management-console__full-size"}>
              <ServerErrors error={error} variant="large">
                <Button variant="primary" onClick={() => goToTasks()}>
                  Go to Tasks
                </Button>
              </ServerErrors>
            </Card>
          </GridItem>
        </Grid>
      );
    }

    if (!userTask) {
      return (
        <Grid hasGutter md={1} className={"kogito-management-console__full-size"}>
          <GridItem span={12} className={"kogito-management-console__full-size"}>
            <Card className={"kogito-management-console__full-size"}>
              <KogitoEmptyState
                type={KogitoEmptyStateType.Info}
                title={"Cannot find Task"}
                body={`Cannot find Task with id '${taskId}'`}
              />
            </Card>
          </GridItem>
        </Grid>
      );
    }

    const taskDetailsPanel = (
      <DrawerPanelContent className={"kogito-management-console__full-size"}>
        <DrawerHead>
          <span tabIndex={isDetailsExpanded ? 0 : -1}>
            <Title headingLevel="h3" size="xl">
              Details
            </Title>
          </span>
          <DrawerActions>
            <DrawerCloseButton onClick={onDetailsCloseClick} />
          </DrawerActions>
        </DrawerHead>
        <DrawerPanelBody>
          <EmbeddedTaskDetails targetOrigin={window.location.origin} userTask={userTask} />
        </DrawerPanelBody>
      </DrawerPanelContent>
    );

    return (
      <Drawer isExpanded={isDetailsExpanded}>
        <DrawerContent panelContent={taskDetailsPanel}>
          <DrawerContentBody>
            <Grid hasGutter md={1} className={"kogito-management-console__full-size"}>
              <GridItem span={12} className={"kogito-management-console__full-size"}>
                <Card isPlain className={"kogito-management-console__full-size"}>
                  <CardBody className="pf-v5-u-h-100">
                    <TaskForm
                      userTask={userTask}
                      onSubmitFormSuccess={onSubmitSuccess}
                      onSubmitFormError={onSubmitError}
                      onUnauthorized={onUnauthorized}
                      accessToken={accessToken}
                      username={username}
                    />
                  </CardBody>
                </Card>
              </GridItem>
            </Grid>
          </DrawerContentBody>
        </DrawerContent>
      </Drawer>
    );
  }, [
    accessToken,
    error,
    goToTasks,
    isDetailsExpanded,
    isLoading,
    onDetailsCloseClick,
    onSubmitError,
    onSubmitSuccess,
    onUnauthorized,
    taskId,
    userTask,
    username,
  ]);

  return (
    <>
      {userTask && (
        <Flex justifyContent={{ default: "justifyContentSpaceBetween" }}>
          <FlexItem>
            <PageTitle title={userTask.referenceName} extra={<TaskState task={userTask} variant={"label"} />} />
          </FlexItem>
          <FlexItem>
            <Button variant="secondary" id="view-details" onClick={onViewDetailsClick}>
              View details
            </Button>
          </FlexItem>
        </Flex>
      )}
      {notification && (
        <div className="kogito-management-console__task-details-page">
          <FormNotification notification={notification} />
        </div>
      )}
      <Card
        isPlain
        className="kogito-management-console__card-size"
        style={{ paddingTop: "var(--pf-v5-global--spacer--md)" }}
      >
        {content}
      </Card>
    </>
  );
};
