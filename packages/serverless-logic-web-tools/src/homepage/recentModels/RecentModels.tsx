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

import * as React from "react";
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
  Toolbar,
  ToolbarContent,
  ToolbarItem,
} from "@patternfly/react-core/dist/js";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
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
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { KebabDropdown } from "../../editor/EditorToolbar";
import { useRoutes } from "../../navigation/Hooks";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParam, useQueryParams } from "../../queryParams/QueryParamsContext";
import { ErrorBoundary } from "../../reactExt/ErrorBoundary";
import { ConfirmDeleteModal } from "./ConfirmDeleteModal";
import { WorkspaceCard, WorkspaceCardError } from "./WorkspaceCard";
import { WorkspacesListDrawerPanelContent } from "./WorkspacesListDrawerPanelContent";

export function RecentModels() {
  const routes = useRoutes();
  const history = useHistory();
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();
  const [isLargeKebabOpen, setLargeKebabOpen] = useState(false);
  const [isBulkDropDownOpen, setIsBulkDropDownOpen] = useState(false);
  const [selectedWorkspaceIds, setSelectedWorkspaceIds] = useState<WorkspaceDescriptor["workspaceId"][]>([]);
  const modelsListPadding = "10px";
  const [isConfirmDeleteModalOpen, setIsConfirmDeleteModalOpen] = useState(false);
  const workspaces = useWorkspaces();
  const [alerts, setAlerts] = useState<Partial<AlertProps>[]>([]);

  const isBulkCheckBoxChecked = useMemo(() => {
    if (workspaceDescriptorsPromise.data && selectedWorkspaceIds.length) {
      return selectedWorkspaceIds.length === workspaceDescriptorsPromise.data.length ? true : null;
    }
    return false;
  }, [workspaceDescriptorsPromise, selectedWorkspaceIds]);

  const closeExpandedWorkspace = useCallback(() => {
    history.replace({
      pathname: "/RecentModels",
      search: queryParams.without(QueryParams.EXPAND).toString(),
    });
  }, [history, queryParams]);

  const expandWorkspace = useCallback(
    (workspaceId: string) => {
      const expand = workspaceId !== expandedWorkspaceId ? workspaceId : undefined;
      if (!expand) {
        closeExpandedWorkspace();
        return;
      }

      history.replace({
        pathname: "/RecentModels",
        search: routes.home.queryString({ expand }),
      });
    },
    [closeExpandedWorkspace, history, routes, expandedWorkspaceId]
  );

  const onBulkDropDownToggle = (isOpen: boolean) => {
    setIsBulkDropDownOpen(isOpen);
  };

  const onBulkDropDownSelect = () => {
    setIsBulkDropDownOpen(false);
  };

  useEffect(() => {
    if (
      workspaceDescriptorsPromise.data &&
      !workspaceDescriptorsPromise.data.map((f) => f.workspaceId).includes(expandedWorkspaceId!)
    ) {
      closeExpandedWorkspace();
    }
  }, [workspaceDescriptorsPromise, closeExpandedWorkspace, expandedWorkspaceId]);

  const onSelectAllWorkspace = useCallback((checked: boolean, workspaceDescriptors: WorkspaceDescriptor[]) => {
    setSelectedWorkspaceIds(checked ? workspaceDescriptors.map((e) => e.workspaceId) : []);
  }, []);

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
    (workspaceDescriptors: WorkspaceDescriptor[]) => {
      const modelsWord = selectedWorkspaceIds.length > 1 ? "Models" : "Model";
      setIsConfirmDeleteModalOpen(false);

      /* TODO: RecentModels: to delete all models use https://github.com/kiegroup/kie-tools/blob/07229b6b4a2f64e0c86a63274cf9f35a10628bde/packages/serverless-logic-sandbox/src/workspace/startupBlockers/FsChanged.tsx#L57*/

      Promise.all(
        workspaceDescriptors
          .filter((w) => selectedWorkspaceIds.includes(w.workspaceId))
          /* TODO: RecentModels: uncomment me */
          // .map((w) => workspaces.deleteWorkspace(w))
          .map((w) => () => true)
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

  const onBulkDeleteButtonClick = useCallback(() => setIsConfirmDeleteModalOpen(true), []);

  const deleteFileDropdownItem = useMemo(() => {
    return (
      <DropdownItem
        key={"delete-dropdown-item"}
        isDisabled={!selectedWorkspaceIds.length}
        onClick={onBulkDeleteButtonClick}
        ouiaId={"delete-file-button"}
        aria-label="Open confirm delete modal"
      >
        <Flex flexWrap={{ default: "nowrap" }}>
          <FlexItem>
            <TrashIcon />
            &nbsp;&nbsp;Delete <b>selected {selectedWorkspaceIds.length > 1 ? "models" : "model"}</b>
          </FlexItem>
        </Flex>
      </DropdownItem>
    );
  }, [onBulkDeleteButtonClick, selectedWorkspaceIds]);

  const bulkDropDownItems = useCallback(
    (workspaceDescriptors: WorkspaceDescriptor[]) => [
      <DropdownItem onClick={() => setSelectedWorkspaceIds([])} key="none" aria-label="Select none">
        Select none (0)
      </DropdownItem>,
      <DropdownItem
        onClick={() => setSelectedWorkspaceIds(workspaceDescriptors.map((e) => e.workspaceId))}
        key="all"
        aria-label="Select All"
      >
        Select all({workspaceDescriptors.length})
      </DropdownItem>,
    ],
    []
  );

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

              <PageSection hasOverflowScroll isFilled aria-label="workspaces-table-section">
                <PageSection variant={"light"} isFilled style={{ height: "100%" }}>
                  {workspaceDescriptors.length > 0 && (
                    <Toolbar>
                      <ToolbarContent style={{ paddingLeft: modelsListPadding, paddingRight: modelsListPadding }}>
                        <ToolbarItem alignment={{ default: "alignLeft" }}>
                          <Dropdown
                            onSelect={onBulkDropDownSelect}
                            toggle={
                              <DropdownToggle
                                splitButtonItems={[
                                  <DropdownToggleCheckbox
                                    onChange={(checked) => onSelectAllWorkspace(checked, workspaceDescriptors)}
                                    isChecked={isBulkCheckBoxChecked}
                                    id="split-button-text-checkbox"
                                    key="bulk-check-box"
                                    aria-label="Select all"
                                  >
                                    {selectedWorkspaceIds.length ? `${selectedWorkspaceIds.length} selected` : ""}
                                  </DropdownToggleCheckbox>,
                                ]}
                                onToggle={onBulkDropDownToggle}
                                id="toggle-split-button-text"
                              />
                            }
                            isOpen={isBulkDropDownOpen}
                            dropdownItems={bulkDropDownItems(workspaceDescriptors)}
                            aria-label="Bulk selection dropdown"
                          />
                        </ToolbarItem>
                        <ToolbarItem alignment={{ default: "alignRight" }}>
                          <KebabDropdown
                            id={"kebab-lg"}
                            state={[isLargeKebabOpen, setLargeKebabOpen]}
                            items={[deleteFileDropdownItem]}
                            menuAppendTo="parent"
                          />
                        </ToolbarItem>
                      </ToolbarContent>
                    </Toolbar>
                  )}
                  <Drawer isExpanded={!!expandedWorkspaceId} isInline={true}>
                    <DrawerContent
                      panelContent={
                        <DrawerPanelContent isResizable={true} minSize={"40%"} maxSize={"80%"}>
                          <WorkspacesListDrawerPanelContent
                            key={expandedWorkspaceId}
                            workspaceId={expandedWorkspaceId}
                            onClose={closeExpandedWorkspace}
                          />
                        </DrawerPanelContent>
                      }
                    >
                      <DrawerContentBody>
                        {workspaceDescriptors.length > 0 && (
                          <Stack hasGutter={true} style={{ padding: modelsListPadding }}>
                            {workspaceDescriptors
                              .sort((a, b) =>
                                new Date(a.lastUpdatedDateISO) < new Date(b.lastUpdatedDateISO) ? 1 : -1
                              )
                              .map((workspace) => (
                                <StackItem key={workspace.workspaceId}>
                                  <ErrorBoundary error={<WorkspaceCardError workspace={workspace} />}>
                                    <WorkspaceCard
                                      workspaceId={workspace.workspaceId}
                                      onSelect={() => expandWorkspace(workspace.workspaceId)}
                                      isSelected={workspace.workspaceId === expandedWorkspaceId}
                                      selectedWorkspaceIds={selectedWorkspaceIds}
                                      setSelectedWorkspaceIds={setSelectedWorkspaceIds}
                                    />
                                  </ErrorBoundary>
                                </StackItem>
                              ))}
                          </Stack>
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
                      </DrawerContentBody>
                    </DrawerContent>
                  </Drawer>
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
