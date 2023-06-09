/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useRoutes } from "../navigation/Hooks";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { join } from "path";
import { Dropdown } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Link } from "react-router-dom";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../workspace/components/FileLabel";
import { Toggle } from "@patternfly/react-core/dist/js/components/Dropdown/Toggle";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Text, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrilldownMenu,
  Menu,
  MenuContent,
  MenuGroup,
  MenuInput,
  MenuItem,
  MenuList,
} from "@patternfly/react-core/dist/js/components/Menu";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { PromiseStateWrapper, useCombinedPromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { useWorkspacesFilesPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesFiles";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { ArrowLeftIcon } from "@patternfly/react-icons/dist/js/icons/arrow-left-icon";
import { WorkspaceLabel } from "../workspace/components/WorkspaceLabel";
import { isEditable } from "../extension";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

const ROOT_MENU_ID = "rootMenu";

enum FilesDropdownMode {
  LIST_MODELS,
  LIST_MODELS_AND_OTHERS,
}

const MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX = 400;

export function FileSwitcher(props: { workspace: ActiveWorkspace; workspaceFile: WorkspaceFile }) {
  const workspaces = useWorkspaces();
  const workspaceFileNameRef = useRef<HTMLInputElement>(null);
  const [newFileNameValid, setNewFileNameValid] = useState<boolean>(true);
  const [filesDropdownMode, setFilesDropdownMode] = useState(FilesDropdownMode.LIST_MODELS);

  const resetWorkspaceFileName = useCallback(() => {
    if (workspaceFileNameRef.current) {
      workspaceFileNameRef.current.value = props.workspaceFile.nameWithoutExtension;
      setNewFileNameValid(true);
    }
  }, [props.workspaceFile]);

  const checkNewFileName = useCallback(
    async (newFileNameWithoutExtension: string) => {
      const trimmedNewFileNameWithoutExtension = newFileNameWithoutExtension.trim();
      if (trimmedNewFileNameWithoutExtension === props.workspaceFile.nameWithoutExtension) {
        setNewFileNameValid(true);
        return;
      }

      const newRelativePath = join(
        props.workspaceFile.relativeDirPath,
        `${trimmedNewFileNameWithoutExtension}.${props.workspaceFile.extension}`
      );

      const hasConflictingFileName = await workspaces.existsFile({
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: newRelativePath,
      });

      const hasForbiddenCharacters = !/^[\w\d_.\-()\s]+$/gi.test(newFileNameWithoutExtension);

      setNewFileNameValid(!hasConflictingFileName && !hasForbiddenCharacters);
    },
    [props.workspaceFile, workspaces]
  );

  const renameWorkspaceFile = useCallback(
    async (newFileName: string | undefined) => {
      const trimmedNewFileName = newFileName?.trim();
      if (!trimmedNewFileName || !newFileNameValid) {
        resetWorkspaceFileName();
        return;
      }

      if (trimmedNewFileName === props.workspaceFile.nameWithoutExtension) {
        resetWorkspaceFileName();
        return;
      }

      await workspaces.renameFile({
        file: props.workspaceFile,
        newFileNameWithoutExtension: trimmedNewFileName.trim(),
      });
    },
    [props.workspaceFile, workspaces, resetWorkspaceFileName, newFileNameValid]
  );

  const handleWorkspaceFileNameKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLInputElement>) => {
      e.stopPropagation();
      if (newFileNameValid && e.key === "Enter") {
        e.currentTarget.blur();
        setPopoverVisible(false);
      } else if (e.key === "Escape") {
        resetWorkspaceFileName();
        e.currentTarget.blur();
        setPopoverVisible(false);
      }
    },
    [newFileNameValid, resetWorkspaceFileName]
  );

  useEffect(resetWorkspaceFileName, [resetWorkspaceFileName]);
  const [isFilesDropdownOpen, setFilesDropdownOpen] = useState(false);
  const [isPopoverVisible, setPopoverVisible] = useState(false);

  const [menuDrilledIn, setMenuDrilledIn] = useState<string[]>([]);
  const [drilldownPath, setDrilldownPath] = useState<string[]>([]);
  const [menuHeights, setMenuHeights] = useState<{ [key: string]: number }>({});
  const [activeMenu, setActiveMenu] = useState(ROOT_MENU_ID);

  useEffect(() => {
    setMenuHeights({});
  }, [props.workspace, filesDropdownMode, activeMenu]);

  useEffect(() => {
    setFilesDropdownMode((prev) =>
      prev === FilesDropdownMode.LIST_MODELS_AND_OTHERS ? FilesDropdownMode.LIST_MODELS : prev
    );
  }, [activeMenu]);

  useEffect(() => {
    if (isFilesDropdownOpen) {
      return;
    }

    setMenuDrilledIn([ROOT_MENU_ID]);
    setDrilldownPath([props.workspace.descriptor.workspaceId]);
    setActiveMenu(`dd${props.workspace.descriptor.workspaceId}`);
  }, [isFilesDropdownOpen, props.workspace.descriptor.workspaceId]);

  const drillIn = useCallback((_event, fromMenuId, toMenuId, pathId) => {
    setMenuDrilledIn((prev) => [...prev, fromMenuId]);
    setDrilldownPath((prev) => [...prev, pathId]);
    setActiveMenu(toMenuId);
  }, []);

  const drillOut = useCallback((_event, toMenuId) => {
    setMenuDrilledIn((prev) => prev.slice(0, prev.length - 1));
    setDrilldownPath((prev) => prev.slice(0, prev.length - 1));
    setActiveMenu(toMenuId);
  }, []);

  const setHeight = useCallback((menuId: string, height: number) => {
    setMenuHeights((prev) => {
      if (prev[menuId] === undefined || (menuId !== ROOT_MENU_ID && prev[menuId] !== height)) {
        return { ...prev, [menuId]: height };
      }
      return prev;
    });
  }, []);

  const workspacesMenuItems = useMemo(() => {
    if (activeMenu === `dd${props.workspace.descriptor.workspaceId}`) {
      return <></>;
    }

    return (
      <WorkspacesMenuItems
        activeMenu={activeMenu}
        currentWorkspace={props.workspace}
        onSelectFile={() => setFilesDropdownOpen(false)}
        filesDropdownMode={filesDropdownMode}
        setFilesDropdownMode={setFilesDropdownMode}
      />
    );
  }, [activeMenu, filesDropdownMode, props.workspace]);

  return (
    <>
      <Flex alignItems={{ default: "alignItemsCenter" }} flexWrap={{ default: "nowrap" }}>
        <FlexItem style={{ display: "flex", alignItems: "baseline" }}>
          <Dropdown
            style={{ position: "relative" }}
            position={"left"}
            className={"kie-tools--masthead-hoverable"}
            isOpen={isFilesDropdownOpen}
            isPlain={true}
            toggle={
              <Toggle
                onToggle={(isOpen) =>
                  setFilesDropdownOpen((prev) => {
                    if (workspaceFileNameRef.current === document.activeElement) {
                      return prev;
                    } else {
                      return isOpen;
                    }
                  })
                }
                id={"editor-page-masthead-files-dropdown-toggle"}
              >
                <Flex flexWrap={{ default: "nowrap" }} alignItems={{ default: "alignItemsCenter" }}>
                  <FlexItem />
                  <FlexItem>
                    <b>
                      <FileLabel extension={props.workspaceFile.extension} />
                    </b>
                  </FlexItem>
                  <Popover
                    hasAutoWidth={true}
                    distance={15}
                    showClose={false}
                    shouldClose={() => setPopoverVisible(false)}
                    hideOnOutsideClick={true}
                    enableFlip={false}
                    withFocusTrap={false}
                    bodyContent={
                      <>
                        <FolderIcon />
                        &nbsp;&nbsp;{props.workspaceFile.relativeDirPath.split("/").join(" > ")}
                      </>
                    }
                    isVisible={isPopoverVisible}
                    position={"bottom-start"}
                  >
                    <FlexItem>
                      <div
                        data-testid={"toolbar-title"}
                        className={`kogito--editor__toolbar-name-container ${newFileNameValid ? "" : "invalid"}`}
                      >
                        <Title
                          aria-label={"EmbeddedEditorFile name"}
                          headingLevel={"h3"}
                          size={"2xl"}
                          style={{ fontWeight: "bold" }}
                        >
                          {props.workspaceFile.nameWithoutExtension}
                        </Title>
                        <Tooltip
                          content={
                            <Text component={TextVariants.p}>
                              {`A file already exists at this location or this name has invalid characters. Please choose a different name.`}
                            </Text>
                          }
                          position={"bottom"}
                          trigger={"manual"}
                          isVisible={!newFileNameValid}
                          className="kogito--editor__light-tooltip"
                        >
                          <TextInput
                            style={{ fontWeight: "bold" }}
                            onClick={(e) => {
                              e.stopPropagation();
                              //FIXME: Change this when it is possible to move a file.
                              if (props.workspaceFile.relativePath !== props.workspaceFile.name) {
                                setPopoverVisible(true);
                              }
                            }}
                            onKeyDown={handleWorkspaceFileNameKeyDown}
                            onChange={checkNewFileName}
                            ref={workspaceFileNameRef}
                            type={"text"}
                            aria-label={"Edit file name"}
                            className={"kogito--editor__toolbar-title"}
                            onBlur={(e) => renameWorkspaceFile(e.target.value)}
                            ouiaId="file-name-input"
                          />
                        </Tooltip>
                      </div>
                    </FlexItem>
                  </Popover>
                  <FlexItem>
                    <CaretDownIcon />
                  </FlexItem>
                </Flex>
              </Toggle>
            }
          >
            <Menu
              style={{
                boxShadow: "none",
                minWidth:
                  activeMenu === ROOT_MENU_ID
                    ? "400px"
                    : filesDropdownMode === FilesDropdownMode.LIST_MODELS
                    ? `${MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX}px`
                    : filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS
                    ? `${MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX * 2}px`
                    : "",
              }}
              id={ROOT_MENU_ID}
              containsDrilldown={true}
              drilldownItemPath={drilldownPath}
              drilledInMenus={menuDrilledIn}
              activeMenu={activeMenu}
              onDrillIn={drillIn}
              onDrillOut={drillOut}
              onGetMenuHeight={setHeight}
            >
              <MenuContent
                maxMenuHeight={"800px"}
                menuHeight={activeMenu === ROOT_MENU_ID ? undefined : `${menuHeights[activeMenu]}px`}
                style={{ overflow: "hidden" }}
              >
                <MenuList style={{ padding: 0 }}>
                  <MenuItem
                    itemId={props.workspace.descriptor.workspaceId}
                    description={"Current"}
                    direction={"down"}
                    drilldownMenu={
                      <DrilldownMenu id={`dd${props.workspace.descriptor.workspaceId}`}>
                        <FilesMenuItems
                          shouldFocusOnSearch={activeMenu === `dd${props.workspace.descriptor.workspaceId}`}
                          filesDropdownMode={filesDropdownMode}
                          setFilesDropdownMode={setFilesDropdownMode}
                          workspaceDescriptor={props.workspace.descriptor}
                          workspaceFiles={props.workspace.files}
                          currentWorkspaceFile={props.workspaceFile}
                          onSelectFile={() => setFilesDropdownOpen(false)}
                        />
                      </DrilldownMenu>
                    }
                  >
                    {props.workspace.descriptor.name}
                  </MenuItem>
                  {workspacesMenuItems}
                </MenuList>
              </MenuContent>
            </Menu>
          </Dropdown>
        </FlexItem>
      </Flex>
    </>
  );
}

export function WorkspacesMenuItems(props: {
  activeMenu: string;
  currentWorkspace: ActiveWorkspace;
  onSelectFile: () => void;
  filesDropdownMode: FilesDropdownMode;
  setFilesDropdownMode: React.Dispatch<React.SetStateAction<FilesDropdownMode>>;
}) {
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const workspaceFilesPromise = useWorkspacesFilesPromise(workspaceDescriptorsPromise.data);
  const combined = useCombinedPromiseState({
    workspaceDescriptors: workspaceDescriptorsPromise,
    workspaceFiles: workspaceFilesPromise,
  });

  return (
    <>
      <Divider component={"li"} />
      <PromiseStateWrapper
        promise={combined}
        pending={
          <div style={{ padding: "8px" }}>
            <Skeleton />
            <br />
            <Skeleton width={"80%"} />
            <br />
            <Skeleton />
            <br />
            <Skeleton width={"80%"} />
            <br />
            <Skeleton />
            <br />
            <Skeleton width={"80%"} />
          </div>
        }
        resolved={({ workspaceDescriptors, workspaceFiles }) => (
          <>
            {workspaceDescriptors
              .sort((a, b) => (new Date(a.lastUpdatedDateISO) < new Date(b.lastUpdatedDateISO) ? 1 : -1))
              .filter((descriptor) => descriptor.workspaceId !== props.currentWorkspace.descriptor.workspaceId)
              .map((descriptor) => (
                <React.Fragment key={descriptor.workspaceId}>
                  {workspaceFiles.get(descriptor.workspaceId)!.length === 1 && (
                    <FileMenuItem
                      file={workspaceFiles.get(descriptor.workspaceId)![0]}
                      onSelectFile={props.onSelectFile}
                    />
                  )}
                  {workspaceFiles.get(descriptor.workspaceId)!.length > 1 && (
                    <MenuItem
                      itemId={descriptor.workspaceId}
                      description={`${
                        workspaceFiles.get(descriptor.workspaceId)!.filter((f) => isEditable(f.relativePath)).length
                      } editable file(s) in ${workspaceFiles.get(descriptor.workspaceId)!.length} file(s)`}
                      direction={"down"}
                      drilldownMenu={
                        <DrilldownMenu id={`dd${descriptor.workspaceId}`}>
                          <FilesMenuItems
                            shouldFocusOnSearch={props.activeMenu === `dd${descriptor.workspaceId}`}
                            filesDropdownMode={props.filesDropdownMode}
                            setFilesDropdownMode={props.setFilesDropdownMode}
                            workspaceDescriptor={descriptor}
                            workspaceFiles={workspaceFiles.get(descriptor.workspaceId) ?? []}
                            onSelectFile={props.onSelectFile}
                          />
                        </DrilldownMenu>
                      }
                    >
                      <FolderIcon />
                      &nbsp;&nbsp;
                      {descriptor.name}
                      &nbsp;&nbsp;
                      <WorkspaceLabel descriptor={descriptor} />
                    </MenuItem>
                  )}
                </React.Fragment>
              ))}
          </>
        )}
      />
    </>
  );
}

export function SearchableFilesMenuGroup(props: {
  maxHeight: string;
  shouldFocusOnSearch: boolean;
  filesDropdownMode: FilesDropdownMode;
  label: string;
  allFiles: WorkspaceFile[];
  children: (args: { filteredFiles: WorkspaceFile[] }) => React.ReactNode;
}) {
  const [search, setSearch] = useState("");
  const searchInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (props.shouldFocusOnSearch) {
      setTimeout(() => {
        searchInputRef.current?.focus();
      }, 500);
    }
  }, [props.shouldFocusOnSearch, props.filesDropdownMode]);

  const filteredFiles = useMemo(
    () => props.allFiles.filter((file) => file.name.toLowerCase().includes(search.toLowerCase())),
    [props.allFiles, search]
  );

  return (
    <MenuGroup label={props.label}>
      <MenuInput>
        <TextInput
          ref={searchInputRef}
          value={search}
          aria-label={"Readonly files menu items"}
          iconVariant={"search"}
          type={"search"}
          onChange={(value) => setSearch(value)}
        />
      </MenuInput>
      {filteredFiles.length === 0 && search && (
        <Bullseye>
          <EmptyState>
            <EmptyStateIcon icon={CubesIcon} />
            <Title headingLevel="h4" size="lg">
              {`No files match '${search}'.`}
            </Title>
          </EmptyState>
        </Bullseye>
      )}
      <div style={{ maxHeight: props.maxHeight, overflowY: "auto" }}>
        {filteredFiles.length > 0 && props.children({ filteredFiles })}
      </div>
    </MenuGroup>
  );
}

export function FilesMenuItems(props: {
  workspaceDescriptor: WorkspaceDescriptor;
  workspaceFiles: WorkspaceFile[];
  currentWorkspaceFile?: WorkspaceFile;
  onSelectFile: () => void;
  filesDropdownMode: FilesDropdownMode;
  setFilesDropdownMode: React.Dispatch<React.SetStateAction<FilesDropdownMode>>;
  shouldFocusOnSearch: boolean;
}) {
  const sortedAndFilteredFiles = useMemo(
    () =>
      props.workspaceFiles
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => file.relativePath !== props.currentWorkspaceFile?.relativePath),
    [props.workspaceFiles, props.currentWorkspaceFile]
  );

  const editableFiles = useMemo(
    () => sortedAndFilteredFiles.filter((file) => isEditable(file.relativePath)),
    [sortedAndFilteredFiles]
  );

  const readonlyFiles = useMemo(
    () => sortedAndFilteredFiles.filter((file) => !isEditable(file.relativePath)),
    [sortedAndFilteredFiles]
  );

  return (
    <>
      <Split>
        <SplitItem isFilled={true}>
          <MenuItem direction="up" itemId={props.workspaceDescriptor.workspaceId}>
            All
          </MenuItem>
        </SplitItem>
      </Split>
      <Divider component={"li"} />

      <Split>
        {(props.filesDropdownMode === FilesDropdownMode.LIST_MODELS ||
          props.filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS) && (
          <SplitItem isFilled={true} style={{ minWidth: `${MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX}px` }}>
            <>
              <SearchableFilesMenuGroup
                maxHeight={"500px"}
                filesDropdownMode={props.filesDropdownMode}
                shouldFocusOnSearch={props.shouldFocusOnSearch}
                label={`Editable files in '${props.workspaceDescriptor.name}'`}
                allFiles={editableFiles}
              >
                {({ filteredFiles }) =>
                  filteredFiles.map((file) => (
                    <FileMenuItem key={file.relativePath} file={file} onSelectFile={props.onSelectFile} />
                  ))
                }
              </SearchableFilesMenuGroup>
              {readonlyFiles.length > 0 && (
                <>
                  <Divider component={"li"} />
                  <MenuGroup>
                    <MenuList style={{ padding: 0 }}>
                      <MenuItem
                        onClick={(e) => {
                          e.stopPropagation();
                          props.setFilesDropdownMode((prev) =>
                            prev === FilesDropdownMode.LIST_MODELS
                              ? FilesDropdownMode.LIST_MODELS_AND_OTHERS
                              : FilesDropdownMode.LIST_MODELS
                          );
                        }}
                      >
                        {props.filesDropdownMode === FilesDropdownMode.LIST_MODELS
                          ? "View readonly files"
                          : "Hide readonly files"}
                        &nbsp;&nbsp;
                        {props.filesDropdownMode === FilesDropdownMode.LIST_MODELS ? (
                          <ArrowRightIcon />
                        ) : (
                          <ArrowLeftIcon />
                        )}
                      </MenuItem>
                    </MenuList>
                  </MenuGroup>
                </>
              )}
            </>
          </SplitItem>
        )}

        {props.filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS && (
          <SplitItem isFilled={true} style={{ minWidth: `${MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX}px` }}>
            <SearchableFilesMenuGroup
              maxHeight={"500px"}
              filesDropdownMode={props.filesDropdownMode}
              shouldFocusOnSearch={props.shouldFocusOnSearch}
              label={`Readonly files in '${props.workspaceDescriptor.name}'`}
              allFiles={readonlyFiles}
            >
              {({ filteredFiles }) =>
                filteredFiles.map((file) => (
                  <FileMenuItem key={file.relativePath} file={file} onSelectFile={props.onSelectFile} />
                ))
              }
            </SearchableFilesMenuGroup>
          </SplitItem>
        )}
      </Split>
    </>
  );
}

export function FileName(props: { file: WorkspaceFile }) {
  return (
    <>
      <Flex flexWrap={{ default: "nowrap" }}>
        <FlexItem>{props.file.nameWithoutExtension}</FlexItem>
        <FlexItem>
          <FileLabel extension={props.file.extension} />
        </FlexItem>
      </Flex>
      <div className={"pf-c-dropdown__menu-item-description"}>
        {props.file.relativeDirPath.split("/").join(" > ")}
        &nbsp;
      </div>
    </>
  );
}

export function FileMenuItem(props: { file: WorkspaceFile; onSelectFile: () => void }) {
  const routes = useRoutes();
  return (
    <MenuItem onClick={props.onSelectFile}>
      <Link
        to={routes.workspaceWithFilePath.path({
          workspaceId: props.file.workspaceId,
          fileRelativePath: props.file.relativePathWithoutExtension,
          extension: props.file.extension,
        })}
      >
        <FileName file={props.file} />
      </Link>
    </MenuItem>
  );
}
