/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useMemo, useCallback, useEffect, useState, useRef } from "react";
import { useHistory } from "react-router";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { PlusCircleIcon } from "@patternfly/react-icons/dist/js/icons/plus-circle-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Table, TableHeader, TableBody, TableProps } from "@patternfly/react-table";
import { useRoutes } from "../../navigation/Hooks";
import { PromiseStateWrapper } from "../../workspace/hooks/PromiseState";
import { useWorkspaceDescriptorsPromise } from "../../workspace/hooks/WorkspacesHooks";
import { OnlineEditorPage } from "../../pageTemplate/OnlineEditorPage";
import { SW_JSON_EXTENSION, useOpenShift } from "../../openshift/OpenShiftContext";
import { useSettings } from "../../settings/SettingsContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useWorkspaces } from "../../workspace/WorkspacesContext";
import { NEW_FILE_DEFAULT_NAME } from "../../workspace/WorkspacesContextProvider";
import { WorkspaceDescriptor } from "../../workspace/model/WorkspaceDescriptor";

const DEPLOYMENTS_DETAILS_POLLING_INTERVAL = 5000;

const cells = ["Name", "Last updated", "Resource Name", "Deployed", "Pods"];

export interface DeploymentDetails {
  workspaceId: string;
  url?: string;
  pods?: number;
  resourceName?: string;
  namespace?: string;
  creationTimestamp?: Date;
  processed: boolean;
}

interface ServerlessWorkflowProps {
  cells: TableProps["cells"];
  rows: TableProps["rows"];
  actions?: TableProps["actions"];
}

export function ServerlessWorkflowList() {
  const routes = useRoutes();
  const history = useHistory();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const workspaces = useWorkspaces();
  const openshiftService = useOpenShift();
  const { openshift } = useSettings();
  const [deploymentDetailsMap, setDeploymentDetailsMap] = useState(new Map<string, DeploymentDetails>());
  const deploymentsDetailsPolling = useRef<number>();
  const [tableData, setTableData] = useState<ServerlessWorkflowProps>({
    cells,
    rows: [],
  });

  useEffect(() => {
    const mapDeployments = async () => {
      if (!workspaceDescriptorsPromise.data || openshift.status !== OpenShiftInstanceStatus.CONNECTED) {
        return;
      }
      const deployments = await openshiftService.listDeployments(openshift.config);
      const services = await openshiftService.listServices(openshift.config);

      const map = new Map<string, DeploymentDetails>();

      workspaceDescriptorsPromise.data.forEach((descriptor) => {
        if (descriptor.deploymentResourceName) {
          map.set(descriptor.deploymentResourceName, {
            workspaceId: descriptor.workspaceId,
            resourceName: descriptor.deploymentResourceName,
            processed: false,
          });
        }
      });

      deployments?.items.forEach((deployment) => {
        const resourceName = deployment.metadata.labels["serving.knative.dev/service"];
        if (resourceName) {
          const details = map.get(resourceName);
          if (details) {
            details.pods = deployment.status.replicas ?? 0;
            details.processed = true;
            map.set(resourceName, details);
          }
        }
      });

      services?.items.forEach((service) => {
        const resourceName = service.metadata.name;
        if (resourceName) {
          const details = map.get(resourceName);
          if (details) {
            details.creationTimestamp = service.metadata.creationTimestamp
              ? new Date(service.metadata.creationTimestamp)
              : undefined;
            details.namespace = service.metadata.namespace;
            details.url = service.status.url;
            details.processed = true;
            map.set(resourceName, details);
          }
        }
      });

      // Clean up outdated items
      map.forEach(async (details, resourceName) => {
        if (!details.processed) {
          await workspaces.descriptorService.setDeploymentResourceName(details.workspaceId, undefined);
          const fs = await workspaces.fsService.getWorkspaceFs(details.workspaceId);
          const openApiFile = await workspaces.getFile({
            fs: fs,
            workspaceId: details.workspaceId,
            relativePath: "openapi.json",
          });

          if (openApiFile) {
            await workspaces.deleteFile({
              fs: fs,
              file: openApiFile,
            });
          }

          map.delete(resourceName);
        }
      });

      setDeploymentDetailsMap(map);
    };
    mapDeployments();
    deploymentsDetailsPolling.current = window.setInterval(
      () => mapDeployments(),
      DEPLOYMENTS_DETAILS_POLLING_INTERVAL
    );

    return () => {
      window.clearInterval(deploymentsDetailsPolling.current);
    };
  }, [
    openshift.config,
    openshift.status,
    openshiftService,
    workspaceDescriptorsPromise.data,
    workspaces,
    workspaces.descriptorService,
  ]);

  const onClickTableItem = useCallback(
    async (descriptor: WorkspaceDescriptor) => {
      const fs = await workspaces.fsService.getWorkspaceFs(descriptor.workspaceId);
      const files = await workspaces.getFiles({ fs: fs, workspaceId: descriptor.workspaceId });
      // For the sake of simplicity, we assume that there is only one workflow file in the workspace
      const workflowFile = files.find((file) => file.name === `${NEW_FILE_DEFAULT_NAME}.${SW_JSON_EXTENSION}`);
      history.replace({
        pathname: routes.workspaceWithFilePath.path({
          workspaceId: descriptor.workspaceId,
          fileRelativePath: workflowFile?.relativePath ?? `./${NEW_FILE_DEFAULT_NAME}.${SW_JSON_EXTENSION}`,
        }),
      });
    },
    [history, routes.workspaceWithFilePath, workspaces]
  );

  useEffect(() => {
    const rows: ServerlessWorkflowProps["rows"] = [];
    const actions: ServerlessWorkflowProps["actions"] = [];
    workspaceDescriptorsPromise.data?.forEach((descriptor) => {
      const details = descriptor.deploymentResourceName && deploymentDetailsMap.get(descriptor.deploymentResourceName);
      const rowDetails = details
        ? [
            details.url ? details.resourceName : "-",
            details.creationTimestamp ? details.creationTimestamp.toLocaleString() : "-",
            details.pods ?? "-",
          ]
        : ["-", "-", "-"];
      rows.push({
        cells: [
          {
            title: (
              <Button
                key={descriptor.workspaceId}
                isInline={true}
                variant={ButtonVariant.link}
                onClick={() => onClickTableItem(descriptor)}
              >
                {descriptor.name}
              </Button>
            ),
          },
          new Date(descriptor.lastUpdatedDateISO).toLocaleString(),
          ...rowDetails,
        ],
        props: { workspaceId: descriptor.workspaceId },
      });
    });
    actions.push({
      title: "Delete",
      onClick: async (_event, _rowId, rowData) => {
        await workspaces.deleteWorkspace({ workspaceId: rowData.props.workspaceId });
      },
    });
    setTableData({ cells, rows, actions });
  }, [deploymentDetailsMap, onClickTableItem, workspaceDescriptorsPromise.data, workspaces]);

  const emptyState = useMemo(
    () => (
      <EmptyState>
        <EmptyStateIcon icon={PlusCircleIcon} />
        <Title headingLevel="h4" size="lg">
          Your Serverless Workflows are shown here
        </Title>
        <EmptyStateBody>
          For help getting started, access the <a>quick start guide</a>.
        </EmptyStateBody>
        <Button variant="primary" onClick={() => history.replace({ pathname: routes.newWorskapce.path({}) })}>
          Create Serverless Workflow
        </Button>
      </EmptyState>
    ),
    [routes, history]
  );

  return (
    <OnlineEditorPage>
      <Page style={{ position: "relative" }}>
        <PageSection>
          <Flex direction={{ default: "column" }} fullWidth={{ default: "fullWidth" }} style={{ height: "100%" }}>
            <PromiseStateWrapper
              promise={workspaceDescriptorsPromise}
              rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
              resolved={(workspaceDescriptors) => {
                if (workspaceDescriptors.length === 0) {
                  return emptyState;
                }
                return (
                  <Table
                    caption={
                      <FlexItem>
                        <Button
                          variant="primary"
                          onClick={() => history.replace({ pathname: routes.newWorskapce.path({}) })}
                        >
                          Create Serverless Workflow
                        </Button>
                      </FlexItem>
                    }
                    cells={tableData.cells}
                    rows={tableData.rows}
                    actions={tableData.actions}
                  >
                    <TableHeader />
                    <TableBody />
                  </Table>
                );
              }}
            />
          </Flex>
        </PageSection>
      </Page>
    </OnlineEditorPage>
  );
}
