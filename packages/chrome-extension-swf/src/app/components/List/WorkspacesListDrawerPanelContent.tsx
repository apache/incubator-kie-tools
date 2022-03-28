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
import { Link } from "react-router-dom";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { ExpandableSection } from "@patternfly/react-core/dist/js/components/ExpandableSection";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { useRoutes } from "../../navigation/Hooks";
import { PromiseStateWrapper } from "../../workspace/hooks/PromiseState";
import { useEditorEnvelopeLocator } from "../../common/GlobalContext";
import { useWorkspacePromise } from "../../workspace/hooks/WorkspaceHooks";
import { useController } from "../../reactExt/Hooks";
import { Alerts, AlertsController } from "../../alerts/Alerts";
import { WorkspaceKind } from "../../workspace/model/WorkspaceOrigin";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { FileLabel } from "../../workspace/components/FileLabel";

export function WorkspacesListDrawerPanelContent(props: { workspaceId: string | undefined; onClose: () => void }) {
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const otherFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => !editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, workspacePromise.data?.files]
  );

  const models = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, workspacePromise.data?.files]
  );

  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);

  return (
    <DrawerPanelContent isResizable={true} minSize={"40%"} maxSize={"80%"}>
      <PromiseStateWrapper
        promise={workspacePromise}
        pending={
          <DrawerPanelBody>
            <Bullseye>
              <Spinner />
            </Bullseye>
          </DrawerPanelBody>
        }
        resolved={(workspace) => (
          <>
            <DrawerHead>
              <Flex>
                <FlexItem>
                  <TextContent>
                    <Text component={TextVariants.h3}>{`Models in '${workspacePromise.data?.descriptor.name}'`}</Text>
                  </TextContent>
                </FlexItem>
                {/* <FlexItem>
                  <Dropdown
                    isPlain={true}
                    position={"left"}
                    isOpen={isNewFileDropdownMenuOpen}
                    toggle={
                      <DropdownToggle
                        className={"kie-tools--masthead-hoverable"}
                        toggleIndicator={null}
                        onToggle={setNewFileDropdownMenuOpen}
                      >
                        <PlusIcon />
                      </DropdownToggle>
                    }
                  >
                    <NewFileDropdownMenu
                      alerts={alerts}
                      workspaceId={workspace.descriptor.workspaceId}
                      destinationDirPath={""}
                      onAddFile={async () => setNewFileDropdownMenuOpen(false)}
                    />
                  </Dropdown>
                </FlexItem> */}
              </Flex>
              {(workspace.descriptor.origin.kind === WorkspaceKind.GITHUB_GIST ||
                workspace.descriptor.origin.kind === WorkspaceKind.GIT) && (
                <TextContent>
                  <Text component={TextVariants.small}>
                    <i>{workspace.descriptor.origin.url.toString()}</i>
                  </Text>
                </TextContent>
              )}
              <DrawerActions>
                <DrawerCloseButton onClick={props.onClose} />
              </DrawerActions>
            </DrawerHead>
            <DrawerPanelBody>
              <DataList aria-label="models-data-list">
                {models.map((file) => (
                  <Link
                    key={file.relativePath}
                    to={routes.workspaceWithFilePath.path({
                      workspaceId: workspace.descriptor.workspaceId ?? "",
                      fileRelativePath: file.relativePath,
                    })}
                  >
                    <FileDataListItem file={file} />
                  </Link>
                ))}
              </DataList>
              <br />
              {otherFiles.length > 0 && (
                <ExpandableSection
                  toggleTextCollapsed="View other files"
                  toggleTextExpanded="Hide other files"
                  className={"plain"}
                >
                  <DataList aria-label="other-files-data-list">
                    {otherFiles.map((file) => (
                      <FileDataListItem key={file.relativePath} file={file} />
                    ))}
                  </DataList>
                </ExpandableSection>
              )}
            </DrawerPanelBody>
          </>
        )}
      />
    </DrawerPanelContent>
  );
}

export function FileDataListItem(props: { file: WorkspaceFile }) {
  return (
    <DataListItem>
      <DataListItemRow>
        <DataListItemCells
          dataListCells={[
            <DataListCell key="link" isFilled={false}>
              <Flex flexWrap={{ default: "nowrap" }}>
                <FlexItem>{props.file.nameWithoutExtension}</FlexItem>
                <FlexItem>
                  <FileLabel extension={props.file.extension} />
                </FlexItem>
              </Flex>
              <TextContent>
                <Text component={TextVariants.small}>{props.file.relativeDirPath.split("/").join(" > ")}</Text>
              </TextContent>
            </DataListCell>,
          ]}
        />
      </DataListItemRow>
    </DataListItem>
  );
}
