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
import { useMemo, useState } from "react";
import { useHistory } from "react-router";
import { Link } from "react-router-dom";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { TaskIcon } from "@patternfly/react-icons/dist/js/icons/task-icon";
import {
  Card,
  CardActions,
  CardBody,
  CardHeader,
  CardHeaderMain,
  CardTitle,
} from "@patternfly/react-core/dist/js/components/Card";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { useRoutes } from "../../navigation/Hooks";
import { PromiseStateWrapper } from "../../workspace/hooks/PromiseState";
import { useEditorEnvelopeLocator } from "../../common/GlobalContext";
import { useWorkspacePromise } from "../../workspace/hooks/WorkspaceHooks";
import { WorkspaceKind } from "../../workspace/model/WorkspaceOrigin";
import { useWorkspaces } from "../../workspace/WorkspacesContext";
import { FileLabel } from "../../workspace/components/FileLabel";
import { WorkspaceDescriptor } from "../../workspace/model/WorkspaceDescriptor";
import { DeleteDropdownWithConfirmation } from "./DeleteDropdownWithConfirmation";
import { RelativeDate } from "../../dates/RelativeDate";
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

export function WorkspaceCard(props: { workspaceId: string; isSelected: boolean; onSelect: () => void }) {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const routes = useRoutes();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const [isHovered, setHovered] = useState(false);
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const editableFiles = useMemo(() => {
    return workspacePromise.data?.files.filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)) ?? [];
  }, [editorEnvelopeLocator, workspacePromise.data?.files]);

  const workspaceName = useMemo(() => {
    return workspacePromise.data ? workspacePromise.data.descriptor.name : null;
  }, [workspacePromise.data]);

  return (
    <PromiseStateWrapper
      promise={workspacePromise}
      pending={<WorkspaceLoadingCard />}
      rejected={() => <>ERROR</>}
      resolved={(workspace) => (
        <>
          {(editableFiles.length === 1 && workspace.descriptor.origin.kind === WorkspaceKind.LOCAL && (
            <Card
              isSelected={props.isSelected}
              isSelectable={true}
              onMouseOver={() => setHovered(true)}
              onMouseLeave={() => setHovered(false)}
              isHoverable={true}
              isCompact={true}
              style={{ cursor: "pointer" }}
              onClick={() => {
                history.replace({
                  pathname: routes.workspaceWithFilePath.path({
                    workspaceId: editableFiles[0].workspaceId,
                    fileRelativePath: editableFiles[0].relativePath,
                  }),
                });
              }}
            >
              <CardHeader>
                <Link
                  to={routes.workspaceWithFilePath.path({
                    workspaceId: editableFiles[0].workspaceId,
                    fileRelativePath: editableFiles[0].relativePath,
                  })}
                >
                  <CardHeaderMain style={{ width: "100%" }}>
                    <Flex>
                      <FlexItem>
                        <CardTitle>
                          <TextContent>
                            <Text component={TextVariants.h3} style={{ textOverflow: "ellipsis", overflow: "hidden" }}>
                              <TaskIcon />
                              &nbsp;&nbsp;
                              {workspace.descriptor.name}
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
                            Delete <b>{workspace.descriptor.name}</b>
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
                        {`${workspace.files.length} files, ${editableFiles?.length} models`}
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
