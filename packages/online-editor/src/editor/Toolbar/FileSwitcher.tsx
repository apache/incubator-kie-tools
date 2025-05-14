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

import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { join } from "path";
import { Dropdown } from "@patternfly/react-core/deprecated";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../../filesList/FileLabel";
import { Toggle } from "@patternfly/react-core/dist/js/deprecated/components/Dropdown/Toggle";
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
  MenuSearch,
  MenuItem,
  MenuList,
  MenuSearchInput,
} from "@patternfly/react-core/dist/js/components/Menu";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { ThLargeIcon } from "@patternfly/react-icons/dist/js/icons/th-large-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { PromiseStateWrapper, useCombinedPromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { useWorkspacesFilesPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesFiles";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { EmptyState, EmptyStateIcon, EmptyStateHeader } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { useEditorEnvelopeLocator } from "../../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { VariableSizeList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import {
  FileDataList,
  FileLink,
  FileListItem,
  FileListItemDisplayMode,
  getFileDataListHeight,
  SingleFileWorkspaceDataList,
} from "../../filesList/FileDataList";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { WorkspaceListItem } from "../../workspace/components/WorkspaceListItem";
import { usePreviewSvg } from "../../previewSvgs/PreviewSvgHooks";
import { WorkspaceLoadingMenuItem } from "../../workspace/components/WorkspaceLoadingCard";
import {
  listDeletedFiles,
  resolveGitLocalChangesStatus,
  WorkspaceGitLocalChangesStatus,
} from "../../workspace/components/WorkspaceStatusIndicator";
import { Checkbox } from "@patternfly/react-core/dist/js/components/Checkbox";
import { SearchInput } from "@patternfly/react-core/dist/js/components/SearchInput";
import { switchExpression } from "@kie-tools-core/switch-expression-ts";
import { GitStatusProps } from "../../workspace/components/GitStatusIndicatorActions";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

const ROOT_MENU_ID = "rootMenu";

enum FilesMenuMode {
  LIST,
  CAROUSEL,
}

const MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX = 500;
const MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN = 40;
// properties to be used for menu height calculation
const FILE_SWITCHER_INITIAL_HEIGHT_OFFSET_IN_PX = 204;
const MENU_HEIGHT_MAX_LIMIT_CSS = `calc(100vh - ${FILE_SWITCHER_INITIAL_HEIGHT_OFFSET_IN_PX}px)` as const;
const DRILLDOWN_NAVIGATION_MENU_ITEM_HEIGHT_IN_PX = 40;
const MENU_SEARCH_HEIGHT_IN_PX = 64;
const MENU_SHOW_CHANGED_CHECKBOX_HEIGHT_IN_PX = 36;
const MENU_DIVIDER_HEIGHT_IN_PX = 17;

export function FileSwitcher(props: {
  workspace: ActiveWorkspace;
  gitStatusProps?: GitStatusProps;
  workspaceFile: WorkspaceFile;
  onDeletedWorkspaceFile: () => void;
}) {
  const workspaces = useWorkspaces();
  const workspaceFileNameRef = useRef<HTMLInputElement>(null);
  const [newFileNameValid, setNewFileNameValid] = useState<boolean>(true);

  const resetWorkspaceFileName = useCallback(() => {
    if (workspaceFileNameRef.current) {
      workspaceFileNameRef.current.value = props.workspaceFile.nameWithoutExtension;
      setNewFileNameValid(true);
    }
  }, [props.workspaceFile]);

  const checkNewFileName = useCallback(
    async (_event: React.FormEvent<HTMLInputElement>, newFileNameWithoutExtension: string) => {
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

      const hasForbiddenCharacters = !/^[\w\d_.'\-()\s]+$/gi.test(newFileNameWithoutExtension);

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
      if (newFileNameValid && e.keyCode === 13 /* Enter */) {
        e.currentTarget.blur();
        setPopoverVisible(false);
      } else if (e.keyCode === 27 /* ESC */) {
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

  const [filesMenuMode, setFilesMenuMode] = useState(FilesMenuMode.LIST);

  useEffect(() => {
    setMenuHeights({});
  }, [props.workspace, filesMenuMode, activeMenu]);

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
        filesMenuMode={filesMenuMode}
        setFilesMenuMode={setFilesMenuMode}
      />
    );
  }, [activeMenu, filesMenuMode, props.workspace]);

  return (
    <>
      <Flex
        alignItems={{ default: "alignItemsCenter" }}
        flexWrap={{ default: "nowrap" }}
        className={"kie-sandbox--file-switcher"}
      >
        <FlexItem style={{ display: "flex", alignItems: "baseline", minWidth: 0 }}>
          <Dropdown
            style={{ position: "relative" }}
            position={"left"}
            className={"kie-tools--masthead-hoverable"}
            isOpen={isFilesDropdownOpen}
            isPlain={true}
            toggle={
              <Toggle
                onToggle={(_event, isOpen) =>
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
                <Flex
                  flexWrap={{ default: "nowrap" }}
                  alignItems={{ default: "alignItemsCenter" }}
                  gap={{ default: "gapMd" }}
                >
                  <FlexItem />
                  <FlexItem>
                    <b>
                      <FileLabel extension={props.workspaceFile.extension} />
                    </b>
                  </FlexItem>

                  <FlexItem style={{ minWidth: 0 }}>
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
                      <div
                        data-testid={"toolbar-title"}
                        className={`kogito--editor__toolbar-name-container ${newFileNameValid ? "" : "invalid"}`}
                        style={{ width: "100%" }}
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
                          />
                        </Tooltip>
                      </div>
                    </Popover>
                  </FlexItem>
                  <FlexItem>
                    <CaretDownIcon color={"rgb(21, 21, 21)"} />
                  </FlexItem>
                </Flex>
              </Toggle>
            }
          >
            <Menu
              style={{
                boxShadow: "none",
                minWidth: `${MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX}px`,
              }}
              id={ROOT_MENU_ID}
              containsDrilldown={true}
              drilldownItemPath={drilldownPath}
              drilledInMenus={menuDrilledIn}
              activeMenu={activeMenu}
              onDrillIn={drillIn}
              onDrillOut={drillOut}
              onGetMenuHeight={setHeight}
              className={"kie-sandbox--files-menu"}
            >
              <MenuContent
                // MAGIC NUMBER ALERT
                //
                // 204px is the exact number that allows the menu to grow to
                // the maximum size of the screen without adding scroll to the page.
                maxMenuHeight={MENU_HEIGHT_MAX_LIMIT_CSS}
                menuHeight={activeMenu === ROOT_MENU_ID ? undefined : `${menuHeights[activeMenu]}px`}
              >
                <MenuList style={{ padding: 0 }}>
                  <MenuItem
                    itemId={props.workspace.descriptor.workspaceId}
                    direction={"down"}
                    drilldownMenu={
                      <DrilldownMenu id={`dd${props.workspace.descriptor.workspaceId}`}>
                        <FilesMenuItems
                          shouldFocusOnSearch={activeMenu.startsWith(`dd${props.workspace.descriptor.workspaceId}`)}
                          filesMenuMode={filesMenuMode}
                          setFilesMenuMode={setFilesMenuMode}
                          workspace={props.workspace}
                          currentWorkspaceFile={props.workspaceFile}
                          onSelectFile={() => setFilesDropdownOpen(false)}
                          currentWorkspaceGitStatusProps={props.gitStatusProps}
                          onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
                        />
                      </DrilldownMenu>
                    }
                  >
                    Current
                  </MenuItem>
                  <MenuGroup
                    style={{
                      maxHeight: `calc(${MENU_HEIGHT_MAX_LIMIT_CSS} - ${DRILLDOWN_NAVIGATION_MENU_ITEM_HEIGHT_IN_PX}px)` /* height of menu minus the height of Current item*/,
                      overflowY: "auto",
                    }}
                  >
                    {workspacesMenuItems}
                  </MenuGroup>
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
  filesMenuMode: FilesMenuMode;
  setFilesMenuMode: React.Dispatch<React.SetStateAction<FilesMenuMode>>;
}) {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
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
          <>
            <WorkspaceLoadingMenuItem />
            <WorkspaceLoadingMenuItem />
            <WorkspaceLoadingMenuItem />
            <WorkspaceLoadingMenuItem />
          </>
        }
        resolved={({ workspaceDescriptors, workspaceFiles }) => (
          <>
            {workspaceDescriptors
              .sort((a, b) => (new Date(a.lastUpdatedDateISO) < new Date(b.lastUpdatedDateISO) ? 1 : -1))
              .filter((descriptor) => descriptor.workspaceId !== props.currentWorkspace.descriptor.workspaceId)
              .map((descriptor) => (
                <React.Fragment key={descriptor.workspaceId}>
                  {workspaceFiles.get(descriptor.workspaceId)!.length === 1 && (
                    <MenuItem onClick={props.onSelectFile} className={"kie-tools--file-switcher-no-padding-menu-item"}>
                      <SingleFileWorkspaceDataList
                        workspaceDescriptor={descriptor}
                        file={workspaceFiles.get(descriptor.workspaceId)![0]}
                      />
                    </MenuItem>
                  )}
                  {workspaceFiles.get(descriptor.workspaceId)!.length > 1 && (
                    <MenuItem
                      style={{
                        borderTop: "var(--pf-v5-global--BorderWidth--sm) solid var(--pf-v5-global--BorderColor--100)",
                      }}
                      className={"kie-tools--file-switcher-no-padding-menu-item"}
                      itemId={descriptor.workspaceId}
                      direction={"down"}
                      drilldownMenu={
                        <DrilldownMenu id={`dd${descriptor.workspaceId}`} style={{ position: "fixed" }}>
                          {/* position:fixed important to render properly inside menugroup*/}
                          <FilesMenuItems
                            shouldFocusOnSearch={props.activeMenu.startsWith(`dd${descriptor.workspaceId}`)}
                            filesMenuMode={props.filesMenuMode}
                            setFilesMenuMode={props.setFilesMenuMode}
                            workspace={{ descriptor, files: workspaceFiles.get(descriptor.workspaceId) ?? [] }}
                            onSelectFile={props.onSelectFile}
                          />
                        </DrilldownMenu>
                      }
                    >
                      <DataList aria-label="workspace-data-list" style={{ border: 0 }}>
                        {/* Need to replicate DatList's border here because of the angle bracket of drilldown menus */}
                        <DataListItem style={{ border: 0, backgroundColor: "transparent" }}>
                          <DataListItemRow>
                            <DataListItemCells
                              dataListCells={[
                                <DataListCell key="link" isFilled={false}>
                                  <WorkspaceListItem
                                    isBig={false}
                                    workspaceDescriptor={descriptor}
                                    allFiles={workspaceFiles.get(descriptor.workspaceId)!}
                                    editableFiles={workspaceFiles
                                      .get(descriptor.workspaceId)!
                                      .filter((f) => editorEnvelopeLocator.hasMappingFor(f.relativePath))}
                                  />
                                </DataListCell>,
                              ]}
                            />
                          </DataListItemRow>
                        </DataListItem>
                      </DataList>
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

export function FileSvg(props: { workspaceFile: WorkspaceFile }) {
  const imgRef = useRef<HTMLImageElement>(null);
  const { previewSvgString } = usePreviewSvg(props.workspaceFile.workspaceId, props.workspaceFile.relativePath);

  useEffect(() => {
    if (previewSvgString.data) {
      const blob = new Blob([previewSvgString.data], { type: "image/svg+xml" });
      const url = URL.createObjectURL(blob);
      imgRef.current!.addEventListener("load", () => URL.revokeObjectURL(url), { once: true });
      imgRef.current!.src = url;
    }
  }, [previewSvgString]);

  return (
    <>
      <PromiseStateWrapper
        pending={
          imgRef.current ? (
            <Bullseye>
              <img
                style={{ height: "180px", margin: "10px" }}
                ref={imgRef}
                alt={`SVG for ${props.workspaceFile.relativePath}`}
              />
            </Bullseye>
          ) : (
            <Skeleton height={"180px"} style={{ margin: "10px" }} />
          )
        }
        rejected={() => (
          <div style={{ height: "180px", margin: "10px", borderRadius: "5px", backgroundColor: "#EEE" }}>
            <Bullseye>
              <Icon size={"xl"}>
                <ImageIcon color={"gray"} />
              </Icon>
            </Bullseye>
          </div>
        )}
        promise={previewSvgString}
        resolved={() => (
          <Bullseye>
            <img
              style={{ height: "180px", margin: "10px" }}
              ref={imgRef}
              alt={`SVG for ${props.workspaceFile.relativePath}`}
            />
          </Bullseye>
        )}
      />
    </>
  );
}

export function FilteredFilesMenuGroup(props: {
  filesMenuMode: FilesMenuMode;
  allFiles: WorkspaceFile[];
  filteredFiles: WorkspaceFile[];
  search: string;
  children: (args: { filteredFiles: WorkspaceFile[] }) => React.ReactNode;
  heightMaxLimitCss: string;
  style?: React.CSSProperties;
}) {
  const height = useMemo(() => {
    if (props.filesMenuMode === FilesMenuMode.LIST) {
      // No reason to know the exact size.
      const sizeOfFirst50Elements = props.allFiles
        .slice(0, 50)
        .map((f) => getFileDataListHeight(f))
        .reduce((a, b) => a + b, 0);
      return `max(300px, min(${props.heightMaxLimitCss}, ${sizeOfFirst50Elements}px))`;
    } else if (props.filesMenuMode === FilesMenuMode.CAROUSEL) {
      // 280px is the size of a File SVG card.
      return `min(${props.heightMaxLimitCss}, calc(${props.allFiles.length} * 280px))`;
    } else {
      return "";
    }
  }, [props.allFiles, props.filesMenuMode, props.heightMaxLimitCss]);

  return (
    <MenuGroup>
      {/* Allows for arrows to work when editing the text. */}
      <div style={{ ...props.style, height }}>
        {props.filteredFiles.length > 0 && props.children({ filteredFiles: props.filteredFiles })}
        {props.filteredFiles.length <= 0 && (
          <Bullseye>
            <EmptyState>
              <EmptyStateHeader
                titleText={<>{`No files match '${props.search}'.`}</>}
                icon={<EmptyStateIcon icon={CubesIcon} />}
                headingLevel="h4"
              />
            </EmptyState>
          </Bullseye>
        )}
      </div>
    </MenuGroup>
  );
}

export function FilesMenuItems(props: {
  workspace: ActiveWorkspace;
  currentWorkspaceGitStatusProps?: GitStatusProps;
  currentWorkspaceFile?: WorkspaceFile;
  onDeletedWorkspaceFile?: () => void;
  onSelectFile: () => void;
  filesMenuMode: FilesMenuMode;
  setFilesMenuMode: React.Dispatch<React.SetStateAction<FilesMenuMode>>;
  shouldFocusOnSearch: boolean;
}) {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  const [topSectionHeightInPx, setTopSectionHeightInPx] = useState<number>(
    DRILLDOWN_NAVIGATION_MENU_ITEM_HEIGHT_IN_PX +
      MENU_SEARCH_HEIGHT_IN_PX +
      MENU_SHOW_CHANGED_CHECKBOX_HEIGHT_IN_PX +
      MENU_DIVIDER_HEIGHT_IN_PX
  );
  const [bottomSectionHeightInPx, setBottomSectionHeightInPx] = useState<number>(
    DRILLDOWN_NAVIGATION_MENU_ITEM_HEIGHT_IN_PX + MENU_DIVIDER_HEIGHT_IN_PX
  );

  const [isModelsMenuActive, setModelsMenuActive] = useState(true);
  const [searchValue, setSearchValue] = useState("");
  const [filteredGitSyncStatus, setFilteredGitSyncStatus] = useState<WorkspaceGitLocalChangesStatus>();

  const searchInputRef = useRef<HTMLInputElement>(null);
  const carouselScrollRef = useRef<HTMLDivElement>(null);
  const listScrollRef = useRef<VariableSizeList>(null);

  const deletedWorkspaceFiles = useMemo(() => {
    if (!props.currentWorkspaceGitStatusProps) {
      return [];
    }
    return listDeletedFiles(props.currentWorkspaceGitStatusProps);
  }, [props.currentWorkspaceGitStatusProps]);

  const allFiles = useMemo(() => {
    return [...props.workspace.files, ...deletedWorkspaceFiles];
  }, [deletedWorkspaceFiles, props.workspace.files]);

  const sortedFiles = useMemo(() => allFiles.sort((a, b) => a.relativePath.localeCompare(b.relativePath)), [allFiles]);

  const models = useMemo(
    () => sortedFiles.filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, sortedFiles]
  );

  const otherFiles = useMemo(
    () => sortedFiles.filter((file) => !editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, sortedFiles]
  );

  const filteredModels = useMemo(
    () =>
      models
        .filter((file) => file.name.toLowerCase().includes(searchValue.toLowerCase()))
        .filter(
          (file) =>
            !filteredGitSyncStatus ||
            resolveGitLocalChangesStatus({
              workspaceGitStatus: props.currentWorkspaceGitStatusProps?.workspaceGitStatusPromise.data,
              file,
            }) === filteredGitSyncStatus
        ),
    [filteredGitSyncStatus, models, props.currentWorkspaceGitStatusProps?.workspaceGitStatusPromise.data, searchValue]
  );

  const filteredOtherFiles = useMemo(
    () => otherFiles.filter((file) => file.name.toLowerCase().includes(searchValue.toLowerCase())),
    [otherFiles, searchValue]
  );

  const computedInitialScrollOffset = useMemo(() => {
    if (props.filesMenuMode !== FilesMenuMode.LIST || !isModelsMenuActive || !props.currentWorkspaceFile) {
      return;
    }
    const index = filteredModels.findIndex((it) => it.relativePath === props.currentWorkspaceFile?.relativePath);
    if (index < 0) {
      return 0;
    }
    return filteredModels.slice(0, index).reduce((sum, current) => sum + getFileDataListHeight(current), 0);
  }, [filteredModels, isModelsMenuActive, props.currentWorkspaceFile, props.filesMenuMode]);

  const searchInput = useCallback(
    (searchRef?: React.RefObject<HTMLInputElement>) => {
      return (
        <MenuSearch>
          <MenuSearchInput onKeyDown={(e) => e.stopPropagation()}>
            <SearchInput
              ref={searchRef}
              value={searchValue}
              type={"search"}
              onChange={(_ev, value) => {
                setSearchValue(value);
              }}
              placeholder={`In '${props.workspace.descriptor.name}'`}
              style={{ fontSize: "small" }}
              onClick={(e) => {
                e.stopPropagation();
              }}
            />
          </MenuSearchInput>
        </MenuSearch>
      );
    },
    [props.workspace.descriptor.name, searchValue]
  );

  const fileListMenuItemsHeightMaxLimitCss = useMemo(() => {
    return `calc(${MENU_HEIGHT_MAX_LIMIT_CSS} - ${
      topSectionHeightInPx + (otherFiles.length ? bottomSectionHeightInPx : 0)
    }px)`;
  }, [bottomSectionHeightInPx, otherFiles.length, topSectionHeightInPx]);

  const isCurrentWorkspaceFile = useCallback(
    (file: WorkspaceFile) => {
      return (
        props.workspace.descriptor.workspaceId === file.workspaceId &&
        props.currentWorkspaceFile?.relativePath === file.relativePath
      );
    },
    [props.currentWorkspaceFile?.relativePath, props.workspace.descriptor.workspaceId]
  );

  const getFileListItemDisplayMode = useCallback(
    (file: WorkspaceFile) =>
      deletedWorkspaceFiles.some((it) => it.relativePath === file.relativePath)
        ? FileListItemDisplayMode.deleted
        : FileListItemDisplayMode.enabled,
    [deletedWorkspaceFiles]
  );

  useEffect(() => {
    const task = setTimeout(() => {
      if (props.filesMenuMode === FilesMenuMode.CAROUSEL && isModelsMenuActive) {
        carouselScrollRef.current?.scrollIntoView({ block: "nearest", behavior: "smooth" });
      }
      if (props.filesMenuMode === FilesMenuMode.LIST && isModelsMenuActive) {
        listScrollRef.current?.scrollToItem(
          filteredModels.findIndex((it) => it.relativePath === props.currentWorkspaceFile?.relativePath)
        );
      }
      if (props.shouldFocusOnSearch) {
        searchInputRef.current?.focus({ preventScroll: true });
      }
    }, 500);
    return () => {
      clearTimeout(task);
    };
  }, [
    props.filesMenuMode,
    isModelsMenuActive,
    filteredGitSyncStatus,
    props.currentWorkspaceFile?.relativePath,
    filteredModels,
    props.shouldFocusOnSearch,
  ]);

  return (
    <>
      <MenuItem direction={"up"} itemId={`${props.workspace.descriptor.workspaceId}-breadcrumb`}>
        All
      </MenuItem>
      {searchInput(isModelsMenuActive ? searchInputRef : undefined)}
      <Flex
        justifyContent={{ default: "justifyContentFlexEnd" }}
        alignItems={{ default: "alignItemsCenter" }}
        style={{ paddingRight: "16px" }}
      >
        <FlexItem>
          {props.currentWorkspaceGitStatusProps !== undefined && (
            <Tooltip content={"Select to display only modified, added, or deleted files"} position={"bottom"}>
              <Checkbox
                label={"Only modified"}
                id={"filter-git-status-modified-locally"}
                aria-label={"Select to display only modified, added, or deleted files"}
                isChecked={filteredGitSyncStatus === WorkspaceGitLocalChangesStatus.pending}
                onChange={(_event, checked) => {
                  setFilteredGitSyncStatus(checked ? WorkspaceGitLocalChangesStatus.pending : undefined);
                }}
              />
            </Tooltip>
          )}
        </FlexItem>
        <FlexItem>
          <Flex justifyContent={{ default: "justifyContentCenter" }}>
            <FilesMenuModeIcons filesMenuMode={props.filesMenuMode} setFilesMenuMode={props.setFilesMenuMode} />
          </Flex>
        </FlexItem>
      </Flex>
      <Divider component={"li"} />

      {props.filesMenuMode === FilesMenuMode.LIST && (
        <FilteredFilesMenuGroup
          search={searchValue}
          filesMenuMode={props.filesMenuMode}
          allFiles={models}
          filteredFiles={filteredModels}
          heightMaxLimitCss={fileListMenuItemsHeightMaxLimitCss}
        >
          {({ filteredFiles }) => {
            return (
              <AutoSizer>
                {({ height, width }) => (
                  <VariableSizeList
                    height={height}
                    itemCount={filteredFiles.length}
                    itemSize={(index) => getFileDataListHeight(filteredFiles[index])}
                    width={width}
                    initialScrollOffset={computedInitialScrollOffset}
                    ref={listScrollRef}
                  >
                    {({ index, style }) => {
                      const fileListItemDisplayMode = getFileListItemDisplayMode(filteredFiles[index]);
                      return (
                        <MenuItem
                          key={filteredFiles[index].relativePath}
                          onClick={
                            fileListItemDisplayMode === FileListItemDisplayMode.enabled ? props.onSelectFile : undefined
                          }
                          className={"kie-tools--file-switcher-no-padding-menu-item"}
                          isFocused={isCurrentWorkspaceFile(filteredFiles[index])}
                          isActive={isCurrentWorkspaceFile(filteredFiles[index])}
                          style={style}
                          component={"div"}
                        >
                          <FileDataList
                            file={filteredFiles[index]}
                            displayMode={fileListItemDisplayMode}
                            gitStatusProps={props.currentWorkspaceGitStatusProps}
                            isCurrentWorkspaceFile={isCurrentWorkspaceFile(filteredFiles[index])}
                            onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
                          />
                        </MenuItem>
                      );
                    }}
                  </VariableSizeList>
                )}
              </AutoSizer>
            );
          }}
        </FilteredFilesMenuGroup>
      )}

      {props.filesMenuMode === FilesMenuMode.CAROUSEL && (
        <FilteredFilesMenuGroup
          search={searchValue}
          filesMenuMode={props.filesMenuMode}
          allFiles={models}
          filteredFiles={filteredModels}
          heightMaxLimitCss={fileListMenuItemsHeightMaxLimitCss}
          style={{ overflowY: "auto" }}
        >
          {({ filteredFiles }) => {
            const isCurrentFileOutsideOfShownList =
              props.currentWorkspaceFile &&
              filteredFiles.findIndex((file) => file.relativePath === props.currentWorkspaceFile?.relativePath) >=
                MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN;
            const shouldScrollToTop =
              props.currentWorkspaceFile &&
              filteredFiles.findIndex((file) => file.relativePath === props.currentWorkspaceFile?.relativePath) < 0;
            return (
              <Gallery
                hasGutter={true}
                style={{
                  paddingLeft: "8px",
                  paddingRight: "8px",
                  borderTop: "var(--pf-v5-global--BorderWidth--sm) solid var(--pf-v5-global--BorderColor--100)",
                }}
              >
                {shouldScrollToTop && <div ref={carouselScrollRef} />}
                {filteredFiles.slice(0, MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN).map((file, index) => (
                  <FilesMenuItemCarouselCard
                    key={index}
                    file={file}
                    gitStatusProps={props.currentWorkspaceGitStatusProps}
                    isCurrentWorkspaceFile={isCurrentWorkspaceFile(file)}
                    displayMode={getFileListItemDisplayMode(file)}
                    onSelectFile={props.onSelectFile}
                    onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
                    scrollRef={carouselScrollRef}
                  />
                ))}
                {filteredFiles.length > MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN && (
                  <>
                    {props.currentWorkspaceFile && isCurrentFileOutsideOfShownList && (
                      <FilesMenuItemCarouselCard
                        file={props.currentWorkspaceFile}
                        gitStatusProps={props.currentWorkspaceGitStatusProps}
                        isCurrentWorkspaceFile={isCurrentWorkspaceFile(props.currentWorkspaceFile)}
                        displayMode={getFileListItemDisplayMode(props.currentWorkspaceFile)}
                        onSelectFile={props.onSelectFile}
                        onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
                        scrollRef={carouselScrollRef}
                      />
                    )}
                    <Card style={{ border: 0 }}>
                      <CardBody>
                        <Bullseye>
                          <div>
                            ...and{" "}
                            {filteredFiles.length -
                              MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN -
                              (isCurrentFileOutsideOfShownList ? 1 : 0)}{" "}
                            more.
                          </div>
                        </Bullseye>
                      </CardBody>
                    </Card>
                  </>
                )}
              </Gallery>
            );
          }}
        </FilteredFilesMenuGroup>
      )}
      {otherFiles.length > 0 && (
        <>
          <Divider component={"li"} />
          <MenuItem
            itemId={`${props.workspace.descriptor.workspaceId}-other`}
            direction={"down"}
            onClick={(_event) => {
              setModelsMenuActive(false);
              setBottomSectionHeightInPx(0);
              setTopSectionHeightInPx((prev) => prev - MENU_SHOW_CHANGED_CHECKBOX_HEIGHT_IN_PX);
            }}
            drilldownMenu={
              <DrilldownMenu id={`dd${props.workspace.descriptor.workspaceId}-other`} style={{ position: "fixed" }}>
                <MenuItem
                  itemId={"back-up"}
                  direction={"up"}
                  onClick={(_event) => {
                    setModelsMenuActive(true);
                    setBottomSectionHeightInPx(MENU_DIVIDER_HEIGHT_IN_PX + DRILLDOWN_NAVIGATION_MENU_ITEM_HEIGHT_IN_PX);
                    setTopSectionHeightInPx((prev) => prev + MENU_SHOW_CHANGED_CHECKBOX_HEIGHT_IN_PX);
                  }}
                >
                  Back
                </MenuItem>
                {searchInput(isModelsMenuActive ? undefined : searchInputRef)}
                <Divider component={"li"} />
                <FilteredFilesMenuGroup
                  search={searchValue}
                  filesMenuMode={FilesMenuMode.LIST} // always LIST for otherFiles, even for mode CAROUSEL
                  allFiles={otherFiles}
                  filteredFiles={filteredOtherFiles}
                  heightMaxLimitCss={fileListMenuItemsHeightMaxLimitCss}
                >
                  {({ filteredFiles }) => (
                    <AutoSizer>
                      {({ height, width }) => (
                        <VariableSizeList
                          height={height}
                          itemCount={filteredFiles.length}
                          itemSize={(index) => getFileDataListHeight(filteredFiles[index])}
                          width={width}
                        >
                          {({ index, style }) => (
                            <MenuItem
                              key={filteredFiles[index].relativePath}
                              style={style}
                              component={"div"}
                              className={"kie-tools--file-switcher-no-padding-menu-item"}
                            >
                              <FileDataList
                                file={filteredFiles[index]}
                                displayMode={FileListItemDisplayMode.readonly}
                                gitStatusProps={props.currentWorkspaceGitStatusProps}
                                onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
                              />
                            </MenuItem>
                          )}
                        </VariableSizeList>
                      )}
                    </AutoSizer>
                  )}
                </FilteredFilesMenuGroup>
              </DrilldownMenu>
            }
          >
            Other files
          </MenuItem>
        </>
      )}
    </>
  );
}

const FilesMenuItemCarouselCard = (props: {
  file: WorkspaceFile;
  gitStatusProps?: GitStatusProps;
  isCurrentWorkspaceFile?: boolean;
  onDeletedWorkspaceFile?: () => void;
  displayMode: FileListItemDisplayMode;
  onSelectFile: () => void;
  scrollRef: React.RefObject<HTMLDivElement>;
}) => {
  const cardInternals = [
    <CardHeader style={{ display: "block" }} key={0}>
      <FileListItem
        file={props.file}
        displayMode={props.displayMode}
        gitStatusProps={props.gitStatusProps}
        isCurrentWorkspaceFile={props.isCurrentWorkspaceFile}
        onDeletedWorkspaceFile={props.onDeletedWorkspaceFile}
      />
    </CardHeader>,
    <Divider inset={{ default: "insetMd" }} key={1} />,
    <CardBody style={{ padding: 0 }} key={2}>
      <FileSvg workspaceFile={props.file} />
    </CardBody>,
  ];
  return (
    <Card
      key={props.file.relativePath}
      isSelectable={props.displayMode === FileListItemDisplayMode.enabled}
      isRounded={true}
      isCompact={true}
      isFullHeight={true}
      onClick={props.displayMode === FileListItemDisplayMode.enabled ? props.onSelectFile : undefined}
      className={switchExpression(props.displayMode, {
        enabled: "kie-tools--file-list-item-enabled",
        deleted: "kie-tools--file-list-item-deleted",
        readonly: "kie-tools--file-list-item-readonly",
      })}
    >
      <div id={`scrollRef-${props.file.relativePath}`} ref={props.isCurrentWorkspaceFile ? props.scrollRef : undefined}>
        {props.displayMode === FileListItemDisplayMode.enabled ? (
          <FileLink file={props.file}>{cardInternals}</FileLink>
        ) : (
          cardInternals
        )}
      </div>
    </Card>
  );
};

export function FilesMenuModeIcons(props: {
  filesMenuMode: FilesMenuMode;
  setFilesMenuMode: React.Dispatch<React.SetStateAction<FilesMenuMode>>;
}) {
  return (
    <>
      {props.filesMenuMode === FilesMenuMode.CAROUSEL && (
        <Button
          className={"kie-tools--masthead-hoverable"}
          variant="plain"
          aria-label="Switch to list view"
          onClick={(e) => {
            e.stopPropagation();
            props.setFilesMenuMode(FilesMenuMode.LIST);
          }}
        >
          <ListIcon />
        </Button>
      )}
      {props.filesMenuMode === FilesMenuMode.LIST && (
        <Button
          className={"kie-tools--masthead-hoverable"}
          variant="plain"
          aria-label="Switch to carousel view"
          onClick={(e) => {
            e.stopPropagation();
            props.setFilesMenuMode(FilesMenuMode.CAROUSEL);
          }}
        >
          <ThLargeIcon />
        </Button>
      )}
    </>
  );
}
