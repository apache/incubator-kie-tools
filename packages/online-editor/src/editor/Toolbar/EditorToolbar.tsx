/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { useCallback, useMemo, useRef, useState } from "react";
import { Dropdown, DropdownItem, DropdownPosition, DropdownToggle } from "@patternfly/react-core/deprecated";
import {
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
  ToolbarItemProps,
} from "@patternfly/react-core/dist/js/components/Toolbar";
import { SaveIcon } from "@patternfly/react-icons/dist/js/icons/save-icon";
import { useOnlineI18n } from "../../i18n";
import { ExtendedServicesButtons } from "../ExtendedServices/ExtendedServicesButtons";
import { useRoutes } from "../../navigation/Hooks";
import { EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { useHistory } from "react-router";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { PlusIcon } from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { NewFileDropdownMenu } from "./NewFileDropdownMenu";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { PageHeaderToolsItem } from "@patternfly/react-core/deprecated";
import { FileLabel } from "../../filesList/FileLabel";
import {
  useWorkspaceGitStatusPromise,
  useWorkspacePromise,
  WorkspaceGitStatusType,
} from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceHooks";
import { FileSwitcher } from "./FileSwitcher";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { ExtendedServicesDropdownGroup } from "../ExtendedServices/ExtendedServicesDropdownGroup";
import { TrashIcon } from "@patternfly/react-icons/dist/js/icons/trash-icon";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { WorkspaceKind } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceOrigin";
import { useEditorEnvelopeLocator } from "../../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { ResponsiveDropdown } from "../../ResponsiveDropdown/ResponsiveDropdown";
import { ResponsiveDropdownToggle } from "../../ResponsiveDropdown/ResponsiveDropdownToggle";
import { useAuthSession } from "../../authSessions/AuthSessionsContext";
import { KebabDropdown } from "./KebabDropdown";
import { VsCodeDropdownMenu } from "./VsCodeDropdownMenu";
import { DownloadDropdownGroup } from "./Share/DownloadDropdownGroup";
import { GitDropdownGroup } from "./Share/GitDropdownGroup";
import { EmbedDropdownGroup } from "./Share/EmbedDropdownGroup";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useGitIntegration } from "./GitIntegration/GitIntegrationContextProvider";
import {
  EditorToolbarContextProvider,
  useEditorToolbarContext,
  useEditorToolbarDispatchContext,
} from "./EditorToolbarContextProvider";
import { WorkspaceToolbar } from "./Workspace/WorkspaceToolbar";
import { useWorkspaceNavigationBlocker } from "./Workspace/Hooks";
import { FileStatus } from "./FileStatus";
import { SyncDropdownMenu } from "./SyncDropdownMenu";
import { AcceleratorsDropdown } from "./Accelerators/AcceleratorsDropdown";
import { listDeletedFiles } from "../../workspace/components/WorkspaceStatusIndicator";
import { PromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useEnv } from "../../env/hooks/EnvContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
// import { Toggle } from "@patternfly/react-core/dist/js/components/Dropdown/Toggle";
import BellIcon from "@patternfly/react-icons/dist/js/icons/bell-icon";
import { SettingsTabs } from "../../settings/SettingsModalBody";

export interface Props {
  editor: EmbeddedEditorRef | undefined;
  workspaceFile: WorkspaceFile;
}

const showWhenSmall: ToolbarItemProps["visibility"] = {
  default: "visible",
  "2xl": "hidden",
  xl: "hidden",
  lg: "visible",
  md: "visible",
};

const hideWhenSmall: ToolbarItemProps["visibility"] = {
  default: "hidden",
  "2xl": "visible",
  xl: "visible",
  lg: "hidden",
  md: "hidden",
};

const hideWhenTiny: ToolbarItemProps["visibility"] = {
  default: "hidden",
  "2xl": "visible",
  xl: "visible",
  lg: "visible",
  md: "hidden",
};

export function EditorToolbarWithWorkspace(
  props: Props & { workspace: ActiveWorkspace; workspaceGitStatusPromise: PromiseState<WorkspaceGitStatusType> }
) {
  const { env } = useEnv();
  const { settings } = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { i18n } = useOnlineI18n();
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);

  const { alerts } = useGitIntegration();

  const { isNewFileDropdownMenuOpen, isShareDropdownOpen, isLargeKebabOpen, isSmallKebabOpen } =
    useEditorToolbarContext();

  const { setNewFileDropdownMenuOpen, setShareDropdownOpen, setLargeKebabOpen, setSmallKebabOpen } =
    useEditorToolbarDispatchContext();

  useWorkspaceNavigationBlocker(props.workspace);

  const { gitConfig } = useAuthSession(props.workspace.descriptor.gitAuthSessionId);

  const shareDropdownItems = useMemo(
    () => [
      <DownloadDropdownGroup
        editor={props.editor}
        workspaceFile={props.workspaceFile}
        workspace={props.workspace}
        key="download-dropdown-group"
      />,
      <EmbedDropdownGroup workspaceFile={props.workspaceFile} workspace={props.workspace} key="embed-dropdown-group" />,
      <GitDropdownGroup workspace={props.workspace} key="git-dropdown-group" />,
    ],
    [props.editor, props.workspaceFile, props.workspace]
  );

  const handleDeletedWorkspaceFile = useCallback(() => {
    const nextFile = props.workspace.files
      .filter((f) => {
        return (
          f.relativePath !== props.workspaceFile.relativePath && editorEnvelopeLocator.hasMappingFor(f.relativePath)
        );
      })
      .pop();
    if (!nextFile) {
      history.push({ pathname: routes.home.path({}) });
      return;
    }

    history.push({
      pathname: routes.workspaceWithFilePath.path({
        workspaceId: nextFile.workspaceId,
        fileRelativePath: nextFile.relativePathWithoutExtension,
        extension: nextFile.extension,
      }),
    });
  }, [
    editorEnvelopeLocator,
    history,
    props.workspace.files,
    props.workspaceFile.relativePath,
    routes.home,
    routes.workspaceWithFilePath,
  ]);

  const deleteWorkspaceFile = useCallback(async () => {
    if (props.workspace.files.length === 1) {
      await workspaces.deleteWorkspace({ workspaceId: props.workspaceFile.workspaceId });
      history.push({ pathname: routes.home.path({}) });
      return;
    }

    await workspaces.deleteFile({
      file: props.workspaceFile,
    });

    handleDeletedWorkspaceFile();
  }, [props.workspace.files.length, props.workspaceFile, workspaces, handleDeletedWorkspaceFile, history, routes.home]);

  const deleteFileDropdownItem = useMemo(() => {
    return (
      <DropdownItem key={"delete-dropdown-item"} onClick={deleteWorkspaceFile}>
        <Flex flexWrap={{ default: "nowrap" }}>
          <FlexItem>
            <TrashIcon />
            &nbsp;&nbsp;Delete <b>{`"${props.workspaceFile.nameWithoutExtension}"`}</b>
          </FlexItem>
          <FlexItem>
            <b>
              <FileLabel extension={props.workspaceFile.extension} />
            </b>
          </FlexItem>
        </Flex>
      </DropdownItem>
    );
  }, [deleteWorkspaceFile, props.workspaceFile]);

  const createSavePointDropdownItem = useMemo(() => {
    return (
      <DropdownItem
        key={"commit-dropdown-item"}
        icon={<SaveIcon />}
        onClick={async () => {
          if (!(await workspaces.hasLocalChanges({ workspaceId: props.workspaceFile.workspaceId }))) {
            alerts.nothingToCommitAlert.show();
            return;
          }
          alerts.comittingAlert.show();
          try {
            await workspaces.createSavePoint({
              workspaceId: props.workspaceFile.workspaceId,
              gitConfig,
            });
            alerts.comittingAlert.close();
            alerts.commitSuccessAlert.show();
          } catch (e) {
            alerts.comittingAlert.close();
            alerts.commitFailAlert.show({ reason: e.message });
          }
        }}
        description={"Create a save point"}
      >
        Commit
      </DropdownItem>
    );
  }, [
    workspaces,
    props.workspaceFile.workspaceId,
    alerts.comittingAlert,
    alerts.nothingToCommitAlert,
    alerts.commitSuccessAlert,
    alerts.commitFailAlert,
    gitConfig,
  ]);

  const canSeeWorkspaceToolbar = useMemo(
    () =>
      props.workspace.descriptor.origin.kind !== WorkspaceKind.LOCAL ||
      props.workspace.files.length +
        listDeletedFiles({
          workspaceDescriptor: props.workspace.descriptor,
          workspaceGitStatusPromise: props.workspaceGitStatusPromise,
        }).length >
        1,
    [props.workspace.descriptor, props.workspace.files.length, props.workspaceGitStatusPromise]
  );

  const [isNewDmnEditorDropdownOpen, setNewDmnEditorDropdownOpen] = useState(false);

  return (
    <>
      <PageSection type={"nav"} variant={"light"} padding={{ default: "noPadding" }}>
        {canSeeWorkspaceToolbar && (
          <Flex
            justifyContent={{ default: "justifyContentSpaceBetween" }}
            flexWrap={{ default: "nowrap" }}
            spaceItems={{ default: "spaceItemsMd" }}
          >
            <FlexItem style={{ minWidth: 0 }}>
              <WorkspaceToolbar
                workspace={props.workspace}
                workspaceGitStatusPromise={props.workspaceGitStatusPromise}
                currentWorkspaceFile={props.workspaceFile}
                onDeletedWorkspaceFile={handleDeletedWorkspaceFile}
              />
            </FlexItem>
            <VsCodeDropdownMenu workspace={props.workspace} />
          </Flex>
        )}
      </PageSection>
      <PageSection type={"nav"} variant={"light"} style={{ paddingTop: 0, paddingBottom: 0 }}>
        <Flex
          justifyContent={{ default: "justifyContentSpaceBetween" }}
          alignItems={{ default: "alignItemsCenter" }}
          flexWrap={{ default: "nowrap" }}
        >
          <FlexItem style={{ minWidth: 0 }}>
            <PageHeaderToolsItem visibility={{ default: "visible" }}>
              <Flex flexWrap={{ default: "nowrap" }} alignItems={{ default: "alignItemsCenter" }}>
                <FlexItem style={{ minWidth: 0 }}>
                  <FileSwitcher
                    workspace={props.workspace}
                    gitStatusProps={
                      canSeeWorkspaceToolbar
                        ? {
                            workspaceDescriptor: props.workspace.descriptor,
                            workspaceGitStatusPromise: props.workspaceGitStatusPromise,
                          }
                        : undefined
                    }
                    workspaceFile={props.workspaceFile}
                    onDeletedWorkspaceFile={handleDeletedWorkspaceFile}
                  />
                </FlexItem>
                <FileStatus workspace={props.workspace} workspaceFile={props.workspaceFile} editor={props.editor} />
              </Flex>
            </PageHeaderToolsItem>
          </FlexItem>
          <FlexItem>
            <Toolbar>
              <ToolbarContent style={{ paddingRight: 0 }}>
                <ToolbarGroup>
                  {props.workspaceFile.extension === "dmn" && !settings.editors.useLegacyDmnEditor && (
                    <>
                      <Dropdown
                        toggle={
                          <DropdownToggle
                            onToggle={(_event, val) => setNewDmnEditorDropdownOpen(val)}
                            id="new-dmn-editor-dropdown-toggle"
                          >
                            <Label color="cyan" variant={"outline"}>
                              &nbsp;{`New DMN Editor`}&nbsp;&nbsp;
                              <CaretDownIcon />
                            </Label>
                          </DropdownToggle>
                        }
                        onSelect={() => setNewDmnEditorDropdownOpen(false)}
                        isOpen={isNewDmnEditorDropdownOpen}
                        isPlain={true}
                        dropdownItems={[
                          <DropdownItem key="give-feedback" description={"We'd love to hear from you!"}>
                            <small>
                              <a href={env.KIE_SANDBOX_FEEDBACK_URL} target="_blank">
                                Give feedback&nbsp;
                                <ExternalLinkAltIcon />
                              </a>
                            </small>
                          </DropdownItem>,
                          <DropdownItem
                            key="switch-back"
                            description="...and consider telling us why."
                            onClick={() => {
                              settingsDispatch.open(SettingsTabs.EDITORS);
                              settingsDispatch.set((settings) => {
                                settings.editors.useLegacyDmnEditor = true;
                              });
                            }}
                          >
                            <small>Switch back to the classic DMN Editor</small>
                          </DropdownItem>,
                        ]}
                      />
                    </>
                  )}
                  {props.workspaceFile.extension === "dmn" && settings.editors.useLegacyDmnEditor && (
                    <>
                      <Button
                        icon={<BellIcon />}
                        onClick={() => {
                          settingsDispatch.set((settings) => {
                            settings.editors.useLegacyDmnEditor = false;
                          });
                        }}
                        variant={ButtonVariant.link}
                      >
                        Try the new DMN Editor!
                      </Button>
                    </>
                  )}

                  <ToolbarItem>
                    <AcceleratorsDropdown workspaceFile={props.workspaceFile} />
                  </ToolbarItem>
                  <ToolbarItem>
                    <ResponsiveDropdown
                      title={"Add file"}
                      onClose={() => setNewFileDropdownMenuOpen(false)}
                      position={"right"}
                      isOpen={isNewFileDropdownMenuOpen}
                      toggle={
                        <ResponsiveDropdownToggle
                          onToggle={() => setNewFileDropdownMenuOpen((prev) => !prev)}
                          // isPrimary={true}
                          toggleVariant="primary"
                          toggleIndicator={CaretDownIcon}
                        >
                          <PlusIcon />
                          &nbsp;&nbsp;New file
                        </ResponsiveDropdownToggle>
                      }
                    >
                      <NewFileDropdownMenu
                        workspaceDescriptor={props.workspace.descriptor}
                        destinationDirPath={props.workspaceFile.relativeDirPath}
                        onAddFile={async (file) => {
                          setNewFileDropdownMenuOpen(false);
                          if (!file) {
                            return;
                          }

                          history.push({
                            pathname: routes.workspaceWithFilePath.path({
                              workspaceId: file.workspaceId,
                              fileRelativePath: file.relativePathWithoutExtension,
                              extension: file.extension,
                            }),
                          });
                        }}
                      />
                    </ResponsiveDropdown>
                  </ToolbarItem>
                  <ToolbarItem visibility={hideWhenSmall}>
                    <ToolbarGroup>
                      <ExtendedServicesButtons workspace={props.workspace} workspaceFile={props.workspaceFile} />
                    </ToolbarGroup>
                  </ToolbarItem>
                  {props.workspace.descriptor.origin.kind !== WorkspaceKind.LOCAL && (
                    <ToolbarItem>
                      <SyncDropdownMenu workspace={props.workspace} />
                    </ToolbarItem>
                  )}
                  <ToolbarItem visibility={hideWhenSmall}>
                    <Dropdown
                      onSelect={() => setShareDropdownOpen(false)}
                      isOpen={isShareDropdownOpen}
                      dropdownItems={shareDropdownItems}
                      position={DropdownPosition.right}
                      toggle={
                        <DropdownToggle
                          id={"share-dropdown"}
                          data-testid={"share-dropdown"}
                          onToggle={(_event, isOpen) => setShareDropdownOpen(isOpen)}
                        >
                          {i18n.editorToolbar.share}
                        </DropdownToggle>
                      }
                    />
                  </ToolbarItem>
                  <ToolbarItem visibility={hideWhenSmall} style={{ marginRight: 0 }}>
                    <KebabDropdown
                      id={"kebab-lg"}
                      state={[isLargeKebabOpen, setLargeKebabOpen]}
                      items={[deleteFileDropdownItem, <Divider key={"divider-0"} />, createSavePointDropdownItem]}
                    />
                  </ToolbarItem>
                  <ToolbarItem visibility={showWhenSmall} style={{ marginRight: 0 }}>
                    <KebabDropdown
                      id={"kebab-sm"}
                      state={[isSmallKebabOpen, setSmallKebabOpen]}
                      items={[
                        deleteFileDropdownItem,
                        <Divider key={"divider-0"} />,
                        createSavePointDropdownItem,
                        <Divider key={"divider-1"} />,
                        ...shareDropdownItems,
                        ...(props.workspaceFile.extension !== "dmn"
                          ? []
                          : [
                              <Divider key={"divider-2"} />,
                              <ExtendedServicesDropdownGroup
                                workspace={props.workspace}
                                key="extended-services-group"
                              />,
                            ]),
                      ]}
                    />
                  </ToolbarItem>
                </ToolbarGroup>
              </ToolbarContent>
            </Toolbar>
          </FlexItem>
        </Flex>
      </PageSection>
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
    </>
  );
}

export function EditorToolbar(props: Props) {
  const workspacePromise = useWorkspacePromise(props.workspaceFile.workspaceId);
  const workspaceGitStatusPromise = useWorkspaceGitStatusPromise(workspacePromise.data?.descriptor);
  if (!workspacePromise.data) {
    return <></>;
  }

  return (
    <EditorToolbarContextProvider
      {...props}
      workspace={workspacePromise.data}
      workspaceGitStatusPromise={workspaceGitStatusPromise}
    >
      <EditorToolbarWithWorkspace
        {...props}
        workspace={workspacePromise.data}
        workspaceGitStatusPromise={workspaceGitStatusPromise}
      />
    </EditorToolbarContextProvider>
  );
}
