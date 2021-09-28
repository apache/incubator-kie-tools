/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useMemo, useState } from "react";
import { File } from "@kie-tooling-core/editor/dist/channel";
import { Link } from "react-router-dom";
import { extractFileExtension, removeFileExtension } from "../common/utils";
import { ActiveWorkspace } from "./model/ActiveWorkspace";
import { SUPPORTED_FILES_EDITABLE, SUPPORTED_FILES_PATTERN } from "./SupportedFiles";
import { useWorkspaces } from "./WorkspaceContext";
import { OnlineEditorPage } from "../home/pageTemplate/OnlineEditorPage";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  Text,
  TextContent,
  TextList,
  TextListItem,
  TextListItemVariants,
  TextListVariants,
  TextVariants,
} from "@patternfly/react-core/dist/js/components/Text";
import { useGlobals } from "../common/GlobalContext";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateSecondaryActions,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

export interface Props {
  workspaceId: string;
}

export function WorkspaceOverviewPage(props: Props) {
  const workspaces = useWorkspaces();
  const [workspace, setWorkspace] = useState<ActiveWorkspace | undefined>();
  const [error, setError] = useState<string>();
  const globals = useGlobals();

  useEffect(() => {
    workspaces.openWorkspaceById(props.workspaceId);
  }, [workspaces, props.workspaceId]);

  const files = useMemo(() => {
    if (!workspace) {
      return null;
    }

    return workspace.files.map((file: File) => {
      const filePath = file.path!.replace("/" + workspace.descriptor.context + "/", "");
      const extension = extractFileExtension(filePath)!;
      const isSupported = SUPPORTED_FILES_EDITABLE.includes(extension);
      return (
        <div key={file.path}>
          {isSupported ? (
            <Link
              to={globals.routes.workspaceWithFilePath.path({
                workspaceId: workspace.descriptor.context,
                filePath: removeFileExtension(filePath),
                extension: file.fileExtension,
              })}
            >
              {file.path}
            </Link>
          ) : (
            <h2>{filePath}</h2>
          )}
        </div>
      );
    });
  }, [globals, workspace]);

  useEffect(() => {
    setError(undefined);
    workspaces.workspaceService.get(props.workspaceId).then(async (descriptor) => {
      if (!descriptor) {
        setError("Workspace not found");
        return;
      }

      const files = await workspaces.workspaceService.listFiles(descriptor, SUPPORTED_FILES_PATTERN);
      setWorkspace({ descriptor, files });
    });
  }, [props.workspaceId, workspaces.workspaceService]);

  return (
    <OnlineEditorPage>
      <PageSection isFilled={true}>
        <PageSection variant={"light"} isFilled={true} style={{ height: "100%" }}>
          {error && <div>{error}</div>}
          {!error && workspace && (
            <>
              <TextContent>
                <Text component={TextVariants.h1}>{workspace.descriptor.name}</Text>
              </TextContent>

              <br />
              <TextContent>
                <TextList component={TextListVariants.dl}>
                  <TextListItem component={TextListItemVariants.dt}>ID</TextListItem>
                  <TextListItem component={TextListItemVariants.dd}>{workspace.descriptor.context}</TextListItem>

                  <TextListItem component={TextListItemVariants.dt}>Type</TextListItem>
                  <TextListItem component={TextListItemVariants.dd}>{workspace.descriptor.origin.kind}</TextListItem>

                  <TextListItem component={TextListItemVariants.dt}>Created in</TextListItem>
                  <TextListItem component={TextListItemVariants.dd}>{workspace.descriptor.createdIn}</TextListItem>
                </TextList>
              </TextContent>

              <br />
              <br />
              {!files ||
                (files.length === 0 && (
                  <EmptyState>
                    <EmptyStateIcon icon={CubesIcon} />
                    <Title headingLevel="h4" size="lg">
                      {`You currently don't have any files.`}
                    </Title>
                    <EmptyStateBody>{`Create a new file`}</EmptyStateBody>
                    <EmptyStateSecondaryActions>
                      <Flex grow={{ default: "grow" }}>
                        <FlexItem>
                          <Button
                            isLarge
                            variant={ButtonVariant.secondary}
                            onClick={() => workspaces.addEmptyFile("bpmn")}
                          >
                            BPMN
                          </Button>
                        </FlexItem>
                        <FlexItem>
                          <Button
                            isLarge
                            variant={ButtonVariant.secondary}
                            onClick={() => workspaces.addEmptyFile("dmn")}
                          >
                            DMN
                          </Button>
                        </FlexItem>
                        <FlexItem>
                          <Button
                            isLarge
                            variant={ButtonVariant.secondary}
                            onClick={() => workspaces.addEmptyFile("pmml")}
                          >
                            PMML
                          </Button>
                        </FlexItem>
                      </Flex>
                    </EmptyStateSecondaryActions>
                  </EmptyState>
                ))}

              {files && files.length > 0 && (
                <>
                  <TextContent>
                    <Text component={TextVariants.h3}>Files</Text>
                  </TextContent>
                  {files}
                </>
              )}
            </>
          )}
        </PageSection>
      </PageSection>
    </OnlineEditorPage>
  );
}
