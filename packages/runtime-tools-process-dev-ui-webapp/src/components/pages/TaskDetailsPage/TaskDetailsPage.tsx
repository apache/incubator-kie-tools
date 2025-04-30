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
import React, { useEffect, useState } from "react";
import { RouteComponentProps } from "react-router-dom";
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
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import TaskFormContainer from "../../containers/TaskFormContainer/TaskFormContainer";
import "../../styles.css";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import {
  TaskInboxGatewayApi,
  useTaskInboxGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/TaskInbox";
import { UserTaskInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import { EmbeddedTaskDetails, TaskState } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskDetails";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";

interface Props {
  taskId?: string;
}

const TaskDetailsPage: React.FC<RouteComponentProps<Props> & OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  const taskInboxGatewayApi: TaskInboxGatewayApi = useTaskInboxGatewayApi();
  const appContext = useDevUIAppContext();

  const [taskId] = useState<string>(props.match.params.taskId);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [userTask, setUserTask] = useState<UserTaskInstance>();
  const [notification, setNotification] = useState<Notification>();
  const [error, setError] = useState();
  const [isDetailsExpanded, setIsDetailsExpanded] = useState<boolean>(false);

  useEffect(() => {
    return ouiaPageTypeAndObjectId("task-details-page", taskId);
  });

  const loadTask = async () => {
    try {
      const task = await taskInboxGatewayApi.getTaskById(taskId);
      setUserTask(task);
      if (
        (appContext.getCurrentUser().id && !task?.potentialUsers?.includes(appContext.getCurrentUser()?.id)) ||
        (!appContext.getCurrentUser().id && (task?.potentialUsers?.length ?? 0) > 0)
      ) {
        setIsDetailsExpanded(true);
      } else {
        setIsDetailsExpanded(false);
      }
    } catch (err) {
      setError(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadTask();
  }, []);

  const showNotification = (
    notificationType: Notification["type"],
    submitMessage: string,
    notificationDetails?: string
  ) => {
    setNotification({
      type: notificationType,
      message: submitMessage,
      details: notificationDetails,
      customActions: [
        {
          label: "Go to Tasks",
          onClick: () => {
            setNotification(null);
            goToInbox();
          },
        },
      ],
      close: () => {
        setNotification(null);
      },
    });
  };

  const goToInbox = () => {
    taskInboxGatewayApi.clearOpenTask();
    props.history.push("/Tasks");
  };

  const onSubmitSuccess = (phase: string) => {
    const message = `Task '${userTask.referenceName}' successfully transitioned to phase '${phase}'.`;

    showNotification("success", message);
  };

  const onSubmitError = (phase, details?: string) => {
    const message = `Task '${userTask.referenceName}' couldn't transition to phase '${phase}'.`;

    showNotification("error", message, details);
  };

  if (isLoading) {
    return (
      <PageSection
        {...componentOuiaProps("spinner" + (ouiaId ? "-" + ouiaId : ""), "task-details-page-section", ouiaSafe)}
      >
        <Card className="Dev-ui__card-size">
          <Bullseye>
            <KogitoSpinner spinnerText={`Loading details for task: ${taskId}`} />
          </Bullseye>
        </Card>
      </PageSection>
    );
  }

  if (error) {
    return (
      <PageSection
        {...componentOuiaProps("error" + (ouiaId ? "-" + ouiaId : ""), "task-details-page-section", ouiaSafe)}
      >
        <Grid hasGutter md={1} className={"Dev-ui__card-size"}>
          <GridItem span={12} className={"Dev-ui__card-size"}>
            <Card className={"Dev-ui__card-size"}>
              <ServerErrors error={error} variant="large">
                <Button variant="primary" onClick={() => goToInbox()}>
                  Go to Tasks
                </Button>
              </ServerErrors>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    );
  }

  if (!userTask) {
    return (
      <PageSection
        {...componentOuiaProps("empty" + (ouiaId ? "-" + ouiaId : ""), "task-details-page-section", ouiaSafe)}
      >
        <Grid hasGutter md={1} className={"Dev-ui__card-size"}>
          <GridItem span={12} className={"Dev-ui__card-size"}>
            <Card className={"Dev-ui__card-size"}>
              <KogitoEmptyState
                type={KogitoEmptyStateType.Info}
                title={"Cannot find Task"}
                body={`Cannot find Task with id '${taskId}'`}
              />
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    );
  }

  const onViewDetailsClick = () => {
    setIsDetailsExpanded(!isDetailsExpanded);
  };

  const onDetailsCloseClick = () => {
    setIsDetailsExpanded(false);
  };

  const panelContent = (
    <DrawerPanelContent className={"Dev-ui__card-size"}>
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
        <EmbeddedTaskDetails targetOrigin={appContext.getDevUIUrl()} userTask={userTask} />
      </DrawerPanelBody>
    </DrawerPanelContent>
  );

  return (
    <React.Fragment>
      <PageSection
        variant="light"
        {...componentOuiaProps("header" + (ouiaId ? "-" + ouiaId : ""), "task-details-page-section", ouiaSafe)}
      >
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
        {notification && (
          <div className="kogito-task-console__task-details-page">
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection
        {...componentOuiaProps("content" + (ouiaId ? "-" + ouiaId : ""), "task-details-page-section", ouiaSafe)}
      >
        <Drawer isExpanded={isDetailsExpanded}>
          <DrawerContent panelContent={panelContent}>
            <DrawerContentBody>
              <Grid hasGutter md={1} className={"Dev-ui__card-size"}>
                <GridItem span={12} className={"Dev-ui__card-size"}>
                  <Card className={"Dev-ui__card-size"}>
                    <CardBody className="pf-v5-u-h-100">
                      <TaskFormContainer
                        userTask={userTask}
                        onSubmitSuccess={onSubmitSuccess}
                        onSubmitError={onSubmitError}
                      />
                    </CardBody>
                  </Card>
                </GridItem>
              </Grid>
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      </PageSection>
    </React.Fragment>
  );
};

export default TaskDetailsPage;
