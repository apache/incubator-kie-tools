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
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { Button, Checkbox } from "@patternfly/react-core/dist/js";
import {
  Card,
  CardActions,
  CardBody,
  CardHeader,
  CardHeaderMain,
  CardTitle,
} from "@patternfly/react-core/dist/js/components/Card";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import { RelativeDate } from "../../dates/RelativeDate";
import { DeleteDropdownWithConfirmation } from "../../editor/DeleteDropdownWithConfirmation";
import { splitFiles } from "../../extension";
import { useRoutes } from "../../navigation/Hooks";
import { FileLabel } from "../../workspace/components/FileLabel";
import { WorkspaceLabel } from "../../workspace/components/WorkspaceLabel";

export function WorkspaceLoadingCard() {
  return (
    <Card>
      <CardBody>
        <Skeleton fontSize={"sm"} width={"40%"} />
        <br />
        <Skeleton fontSize={"sm"} width={"70%"} />
      </CardBody>
    </Card>
  );
}

export function WorkspaceCardError(props: { workspace: WorkspaceDescriptor }) {
  const workspaces = useWorkspaces();
  return (
    <Card isSelected={false} isSelectable={true} isHoverable={true} isCompact={true}>
      <CardHeader>
        <CardHeaderMain>
          <Flex>
            <FlexItem>
              <CardTitle>
                <TextContent>
                  <Text component={TextVariants.h3}>
                    <ExclamationTriangleIcon />
                    &nbsp;&nbsp;
                    {`There was an error obtaining information for '${props.workspace.workspaceId}'`}
                  </Text>
                </TextContent>
              </CardTitle>
            </FlexItem>
          </Flex>
        </CardHeaderMain>
        <CardActions>
          <DeleteDropdownWithConfirmation
            onDelete={() => {
              workspaces.deleteWorkspace({ workspaceId: props.workspace.workspaceId });
            }}
            item={
              <>
                Delete <b>{`"${props.workspace.name}"`}</b>
              </>
            }
          />
        </CardActions>
      </CardHeader>
    </Card>
  );
}

export function WorkspaceCard(props: {
  workspaceId: string;
  isSelected: boolean;
  onSelect: () => void;
  selectedWorkspaceIds: WorkspaceDescriptor["workspaceId"][];
  setSelectedWorkspaceIds: React.Dispatch<React.SetStateAction<WorkspaceDescriptor["workspaceId"][]>>;
}) {
  const { setSelectedWorkspaceIds, workspaceId } = props;
  const routes = useRoutes();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const { editableFiles, readonlyFiles } = useMemo(
    () => splitFiles(workspacePromise.data?.files ?? []),
    [workspacePromise.data?.files]
  );

  const workspaceName = useMemo(() => {
    return workspacePromise.data ? workspacePromise.data.descriptor.name : null;
  }, [workspacePromise.data]);

  const isWsCheckboxChecked = useMemo(
    () => props.selectedWorkspaceIds.includes(props.workspaceId),
    [props.selectedWorkspaceIds, props.workspaceId]
  );

  const onWsCheckboxChange = useCallback(
    (event: React.MouseEvent<HTMLButtonElement>) => {
      event.stopPropagation();
      setSelectedWorkspaceIds((prevState) =>
        !isWsCheckboxChecked ? [...prevState, workspaceId] : prevState.filter((id) => id !== workspaceId)
      );
    },
    [isWsCheckboxChecked, workspaceId, setSelectedWorkspaceIds]
  );

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={<WorkspaceLoadingCard />}
      rejected={() => <>ERROR</>}
      resolved={(workspace) => (
        <>
          {(editableFiles.length === 1 &&
            readonlyFiles.length === 0 &&
            workspace.descriptor.origin.kind === WorkspaceKind.LOCAL && (
              <Card
                isSelected={props.isSelected}
                isSelectable={true}
                onMouseOver={() => setHovered(true)}
                onMouseLeave={() => setHovered(false)}
                isHoverable={true}
                isCompact={true}
                style={{ cursor: "pointer" }}
                onClick={() => {
                  history.push({
                    pathname: routes.workspaceWithFilePath.path({
                      workspaceId: editableFiles[0].workspaceId,
                      fileRelativePath: editableFiles[0].relativePathWithoutExtension,
                      extension: editableFiles[0].extension,
                    }),
                  });
                }}
              >
                <CardHeader>
                  <Button
                    variant="plain"
                    aria-label="Select"
                    onClick={onWsCheckboxChange}
                    style={{ padding: "0px 4px" }}
                  >
                    <Checkbox
                      id={"checkbox-" + workspace.descriptor.workspaceId}
                      isChecked={isWsCheckboxChecked}
                      onChange={() => {}}
                    />
                  </Button>
                  &nbsp; &nbsp;
                  <Link
                    to={routes.workspaceWithFilePath.path({
                      workspaceId: editableFiles[0].workspaceId,
                      fileRelativePath: editableFiles[0].relativePathWithoutExtension,
                      extension: editableFiles[0].extension,
                    })}
                  >
                    <CardHeaderMain style={{ width: "100%" }}>
                      <Flex>
                        <FlexItem>
                          <CardTitle>
                            <TextContent>
                              <Text
                                component={TextVariants.h3}
                                style={{ textOverflow: "ellipsis", overflow: "hidden" }}
                              >
                                <TaskIcon />
                                &nbsp;&nbsp;
                                {editableFiles[0].nameWithoutExtension}
                              </Text>
                            </TextContent>
                          </CardTitle>
                        </FlexItem>
                        <FlexItem>
                          <b>
                            <FileLabel extension={editableFiles[0].extension} />
                          </b>
                        </FlexItem>
                      </Flex>
                    </CardHeaderMain>
                  </Link>
                  <CardActions>
                    {isHovered && (
                      <DeleteDropdownWithConfirmation
                        onDelete={() => {
                          workspaces.deleteWorkspace({ workspaceId: props.workspaceId });
                        }}
                        item={
                          <Flex flexWrap={{ default: "nowrap" }}>
                            <FlexItem>
                              Delete <b>{`"${editableFiles[0].nameWithoutExtension}"`}</b>
                            </FlexItem>
                            <FlexItem>
                              <b>
                                <FileLabel extension={editableFiles[0].extension} />
                              </b>
                            </FlexItem>
                          </Flex>
                        }
                      />
                    )}
                  </CardActions>
                </CardHeader>
                <CardBody>
                  <TextContent>
                    <Text component={TextVariants.p}>
                      <b>{`Created: `}</b>
                      <RelativeDate date={new Date(workspacePromise.data?.descriptor.createdDateISO ?? "")} />
                      <b>{`, Last updated: `}</b>
                      <RelativeDate date={new Date(workspacePromise.data?.descriptor.lastUpdatedDateISO ?? "")} />
                    </Text>
                  </TextContent>
                </CardBody>
              </Card>
            )) || (
            <Card
              isSelected={props.isSelected}
              isSelectable={true}
              onMouseOver={() => setHovered(true)}
              onMouseLeave={() => setHovered(false)}
              isHoverable={true}
              isCompact={true}
              style={{ cursor: "pointer" }}
              onClick={props.onSelect}
            >
              <CardHeader>
                <Button variant="plain" aria-label="Select" onClick={onWsCheckboxChange} style={{ padding: "0px 4px" }}>
                  <Checkbox
                    id={"checkbox-" + workspace.descriptor.workspaceId}
                    isChecked={isWsCheckboxChecked}
                    onChange={() => {}}
                  />
                </Button>
                &nbsp; &nbsp;
                <CardHeaderMain style={{ width: "100%" }}>
                  <Flex>
                    <FlexItem>
                      <CardTitle>
                        <TextContent>
                          <Text component={TextVariants.h3} style={{ textOverflow: "ellipsis", overflow: "hidden" }}>
                            <FolderIcon />
                            &nbsp;&nbsp;
                            {workspaceName}
                            &nbsp;&nbsp;
                            <WorkspaceLabel descriptor={workspacePromise.data?.descriptor} />
                          </Text>
                        </TextContent>
                      </CardTitle>
                    </FlexItem>
                    <FlexItem>
                      <Text component={TextVariants.p}>
                        {`${editableFiles?.length} editable files(s) in ${workspace.files.length} file(s)`}
                      </Text>
                    </FlexItem>
                  </Flex>
                </CardHeaderMain>
                <CardActions>
                  {isHovered && (
                    <DeleteDropdownWithConfirmation
                      onDelete={() => {
                        workspaces.deleteWorkspace({ workspaceId: props.workspaceId });
                      }}
                      item={
                        <>
                          Delete <b>{`"${workspacePromise.data?.descriptor.name}"`}</b>
                        </>
                      }
                    />
                  )}
                </CardActions>
              </CardHeader>
              <CardBody>
                <TextContent>
                  <Text component={TextVariants.p}>
                    <b>{`Created: `}</b>
                    <RelativeDate date={new Date(workspacePromise.data?.descriptor.createdDateISO ?? "")} />
                    <b>{`, Last updated: `}</b>
                    <RelativeDate date={new Date(workspacePromise.data?.descriptor.lastUpdatedDateISO ?? "")} />
                  </Text>
                </TextContent>
              </CardBody>
            </Card>
          )}
        </>
      )}
    />
  );
}
