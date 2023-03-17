/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import {
  Alert,
  AlertActionCloseButton,
  AlertGroup,
  AlertProps,
  Page,
  PageSection,
  Pagination,
  SearchInput,
  Toolbar,
  ToolbarContent,
  ToolbarItem,
} from "@patternfly/react-core/dist/js";
import {
  Dropdown,
  DropdownItem,
  DropdownToggle,
  DropdownToggleCheckbox,
} from "@patternfly/react-core/dist/js/components/Dropdown";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { KebabDropdown } from "../../editor/EditorToolbar";
import { useRoutes } from "../../navigation/Hooks";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParam, useQueryParams } from "../../queryParams/QueryParamsContext";
import { ConfirmDeleteModal } from "./ConfirmDeleteModal";
import { WorkspacesTable } from "./WorkspacesTable";

export function RecentModels() {
  const routes = useRoutes();
  const history = useHistory();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();
  const [selectedWorkspaceIds, setSelectedWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>([]);
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const workspaces = useWorkspaces();
  const [alerts, setAlerts] = useState<Partial<AlertProps>[]>([]);

  const onConfirmDeleteModalClose = useCallback(() => setIsConfirmDeleteModalOpen(false), []);

  const addAlert = useCallback(
    (title: string, variant: AlertProps["variant"], key: React.Key = new Date().getTime()) => {
      setAlerts((prevAlerts) => [...prevAlerts, { title, variant, key }]);
    },
    []
  );

  const removeAlert = useCallback((key: React.Key) => {
    setAlerts((prevAlerts) => [...prevAlerts.filter((alert) => alert.key !== key)]);
  }, []);

  const onConfirmDeleteModalDelete = useCallback(
    async (workspaceDescriptors: WorkspaceDescriptor[]) => {
      const modelsWord = selectedWorkspaceIds.length > 1 ? "Models" : "Model";
      setIsConfirmDeleteModalOpen(false);

      Promise.all(
        workspaceDescriptors
          .filter((w) => selectedWorkspaceIds.includes(w.workspaceId))
          .map((w) => workspaces.deleteWorkspace(w))
      )
        .then(() => {
          addAlert(`${modelsWord} deleted successfully`, "success");
        })
        .catch((e) => {
          console.error(e);
          addAlert(`There was an error deleting the ${modelsWord}`, "danger");
        })
        .finally(() => {
          setSelectedWorkspaceIds([]);
        });
    },
    [selectedWorkspaceIds, addAlert, workspaces]
  );

  const onWsToggle = useCallback((workspaceId: WorkspaceDescriptor["workspaceId"], checked: boolean) => {
    setSelectedWorkspaceIds((prevSelected) => {
      const otherSelectedIds = prevSelected.filter((r) => r !== workspaceId);
      return checked ? [...otherSelectedIds, workspaceId] : otherSelectedIds;
    });
  }, []);

  return (
    <PromiseStateWrapper
      promise={workspaceDescriptorsPromise}
      rejected={(e) => <>Error fetching workspaces: {e + ""}</>}
      resolved={(workspaceDescriptors: WorkspaceDescriptor[]) => {
        return (
          <>
            <AlertGroup isToast isLiveRegion>
              {alerts.map(
                ({ key, variant, title }) =>
                  key && (
                    <Alert
                      variant={variant}
                      title={title}
                      timeout
                      onTimeout={() => removeAlert(key)}
                      actionClose={
                        <AlertActionCloseButton
                          title={title as string}
                          variantLabel={`${variant} alert`}
                          onClose={() => removeAlert(key)}
                        />
                      }
                      key={key}
                    />
                  )
              )}
            </AlertGroup>
            <Page>
              <PageSection variant={"light"}>
                <TextContent>
                  <Text component={TextVariants.h1}>Recent models</Text>
                  <Text component={TextVariants.p}>
                    Use your recent models from GitHub Repository, a GitHub Gist or saved in your browser.
                  </Text>
                </TextContent>
              </PageSection>

              <PageSection isFilled aria-label="workspaces-table-section">
                <PageSection variant={"light"} padding={{ default: "noPadding" }}>
                  {workspaceDescriptors.length > 0 && (
                    <>
                      <WorkspacesTable
                        workspaceDescriptors={workspaceDescriptors}
                        selectedWorkspaceIds={selectedWorkspaceIds}
                        setSelectedWorkspaceIds={setSelectedWorkspaceIds}
                        setIsConfirmDeleteModalOpen={setIsConfirmDeleteModalOpen}
                        onWsToggle={onWsToggle}
                      />
                    </>
                  )}
                  {workspaceDescriptors.length === 0 && (
                    <Bullseye>
                      <EmptyState>
                        <EmptyStateIcon icon={CubesIcon} />
                        <Title headingLevel="h4" size="lg">
                          {`Nothing here`}
                        </Title>
                        <EmptyStateBody>{`Start by adding a new model`}</EmptyStateBody>
                      </EmptyState>
                    </Bullseye>
                  )}
                </PageSection>
              </PageSection>
            </Page>
            <ConfirmDeleteModal
              selectedWorkspaceIds={selectedWorkspaceIds}
              isOpen={isConfirmDeleteModalOpen}
              onClose={onConfirmDeleteModalClose}
              onDelete={() => onConfirmDeleteModalDelete(workspaceDescriptors)}
            />
          </>
        );
      }}
    />
  );
}
