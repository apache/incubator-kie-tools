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
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { useWorkspacePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import {
  DrawerActions,
  DrawerCloseButton,
  DrawerHead,
  DrawerPanelBody,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { Dropdown, DropdownToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { useMemo, useState } from "react";
import AutoSizer from "react-virtualized-auto-sizer";
import { VariableSizeList } from "react-window";
import { Alerts, AlertsController } from "../../alerts/Alerts";
import { NewFileDropdownMenu } from "../../editor/NewFileDropdownMenu";
import { isEditable } from "../../extension";
import { FileDataList, getFileDataListHeight } from "../../fileList/FileDataList";

export function WorkspacesListDrawerPanelContent(props: { workspaceId: string | undefined; onClose: () => void }) {
  const workspacePromise = useWorkspacePromise(props.workspaceId);

  const readonlyFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => !isEditable(file.relativePath)),
    [workspacePromise.data?.files]
  );

  const editableFiles = useMemo(
    () =>
      (workspacePromise.data?.files ?? [])
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => isEditable(file.relativePath)),
    [workspacePromise.data?.files]
  );

  const arrayWithModelsThenOtherFiles = useMemo(() => {
    return [...editableFiles, ...readonlyFiles];
  }, [editableFiles, readonlyFiles]);

  const [isNewFileDropdownMenuOpen, setNewFileDropdownMenuOpen] = useState(false);
  const [alerts, alertsRef] = useController<AlertsController>();

  return (
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
          <Alerts width={"100%"} ref={alertsRef} />
          <DrawerHead>
            <Flex>
              <FlexItem>
                <TextContent>
                  <Text
                    component={TextVariants.h3}
                  >{`Editable files in '${workspacePromise.data?.descriptor.name}'`}</Text>
                </TextContent>
              </FlexItem>
              <FlexItem>
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
              </FlexItem>
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
            <AutoSizer>
              {({ height, width }) => (
                <VariableSizeList
                  height={height}
                  itemCount={arrayWithModelsThenOtherFiles.length}
                  itemSize={(index) => getFileDataListHeight(arrayWithModelsThenOtherFiles[index])}
                  width={width}
                >
                  {({ index, style }) => (
                    <FileDataList
                      file={arrayWithModelsThenOtherFiles[index]}
                      isEditable={index < editableFiles.length}
                      style={style}
                    />
                  )}
                </VariableSizeList>
              )}
            </AutoSizer>
          </DrawerPanelBody>
        </>
      )}
    />
  );
}
