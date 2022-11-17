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
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../filesList/FileLabel";
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
import { ImageIcon } from "@patternfly/react-icons/dist/js/icons/image-icon";
import { ThLargeIcon } from "@patternfly/react-icons/dist/js/icons/th-large-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { useWorkspaceDescriptorsPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesHooks";
import { PromiseStateWrapper, useCombinedPromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useWorkspacesFilesPromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspacesFiles";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Card, CardBody, CardHeader, CardHeaderMain } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { useHistory } from "react-router";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { ArrowLeftIcon } from "@patternfly/react-icons/dist/js/icons/arrow-left-icon";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { VariableSizeList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import {
  FileDataList,
  FileLink,
  FileListItem,
  getFileDataListHeight,
  SingleFileWorkspaceDataList,
} from "../filesList/FileDataList";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { WorkspaceListItem } from "../workspace/components/WorkspaceListItem";
import { usePreviewSvg } from "../previewSvgs/PreviewSvgHooks";
import { WorkspaceLoadingMenuItem } from "../workspace/components/WorkspaceLoadingCard";

const ROOT_MENU_ID = "rootMenu";

enum FilesDropdownMode {
  LIST_MODELS,
  LIST_MODELS_AND_OTHERS,
  CAROUSEL,
}

const MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX = 500;
const MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN = 40;

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

  const drillIn = useCallback((fromMenuId, toMenuId, pathId) => {
    setMenuDrilledIn((prev) => [...prev, fromMenuId]);
    setDrilldownPath((prev) => [...prev, pathId]);
    setActiveMenu(toMenuId);
  }, []);

  const drillOut = useCallback((toMenuId) => {
    setMenuDrilledIn((prev) => prev.slice(0, prev.length - 1));
    setDrilldownPath((prev) => prev.slice(0, prev.length - 1));
    setActiveMenu(toMenuId);
  }, []);

  const setHeight = useCallback((menuId: string, height: number) => {
    // do not try to simplify this ternary's condition as some heights are 0, resulting in an infinite loop.
    setMenuHeights((prev) => (prev[menuId] === height ? prev : { ...prev, [menuId]: height }));
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
                width: `${
                  filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS
                    ? MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX * 2
                    : MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX
                }px`,
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
                // MAGIC NUMBER ALERT
                //
                // 204px is the exact number that allows the menu to grow to
                // the maximum size of the screen without adding scroll to the page.
                maxMenuHeight={`calc(100vh - 204px)`}
                menuHeight={activeMenu === ROOT_MENU_ID ? undefined : `${menuHeights[activeMenu]}px`}
                style={{ overflow: "hidden" }}
              >
                <MenuList>
                  <MenuItem
                    itemId={props.workspace.descriptor.workspaceId}
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
                    Current
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
                        borderTop: "var(--pf-global--BorderWidth--sm) solid var(--pf-global--BorderColor--100)",
                      }}
                      className={"kie-tools--file-switcher-no-padding-menu-item"}
                      itemId={descriptor.workspaceId}
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
        pending={<Skeleton height={"180px"} style={{ margin: "10px" }} />}
        rejected={() => (
          <div style={{ height: "180px", margin: "10px", borderRadius: "5px", backgroundColor: "#EEE" }}>
            <Bullseye>
              <ImageIcon size={"xl"} color={"gray"} />
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

export function SearchableFilesMenuGroup(props: {
  shouldFocusOnSearch: boolean;
  filesDropdownMode: FilesDropdownMode;
  label: string;
  allFiles: WorkspaceFile[];
  search: string;
  setSearch: React.Dispatch<React.SetStateAction<string>>;
  children: (args: { filteredFiles: WorkspaceFile[] }) => React.ReactNode;
}) {
  const searchInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (props.shouldFocusOnSearch) {
      const task = setTimeout(() => {
        searchInputRef.current?.focus();
      }, 500);
      return () => {
        clearTimeout(task);
      };
    }
  }, [props.shouldFocusOnSearch, props.filesDropdownMode]);

  const filteredFiles = useMemo(
    () => props.allFiles.filter((file) => file.name.toLowerCase().includes(props.search.toLowerCase())),
    [props.allFiles, props.search]
  );

  const height = useMemo(() => {
    if (
      props.filesDropdownMode === FilesDropdownMode.LIST_MODELS ||
      props.filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS
    ) {
      // No reason to know the exact size.
      const sizeOfFirst50Elements = props.allFiles
        .slice(0, 50)
        .map((f) => getFileDataListHeight(f))
        .reduce((a, b) => a + b, 0);

      // MAGIC NUMBER ALERT
      //
      // 440px is the exact number that allows the menu to grow to the end of the screen without adding scroll  to the
      // entire page, It includes the first menu item, the search bar and the "View other files" button at the bottom.
      return `max(300px, min(calc(100vh - 440px), ${sizeOfFirst50Elements}px))`;
    } else if (props.filesDropdownMode === FilesDropdownMode.CAROUSEL) {
      // MAGIC NUMBER ALERT
      //
      // 384px is the exact number that allows the menu to grow to the end of the screen without
      // adding a scroll to the entire page. It includes the first menu item and the search bar.
      //
      // 280px is the size of a File SVG card.
      return `min(calc(100vh - 384px), calc(${props.allFiles.length} * 280px))`;
    } else {
      return "";
    }
  }, [props.allFiles, props.filesDropdownMode]);

  return (
    <MenuGroup label={props.label}>
      {/* Allows for arrows to work when editing the text. */}
      <MenuInput onKeyDown={(e) => e.stopPropagation()}>
        <TextInput
          ref={searchInputRef}
          value={props.search}
          aria-label={"Other files menu items"}
          iconVariant={"search"}
          type={"search"}
          onChange={(value) => props.setSearch(value)}
        />
      </MenuInput>
      <div style={{ overflowY: "auto", height }}>
        {filteredFiles.length > 0 && props.children({ filteredFiles })}
        {filteredFiles.length <= 0 && (
          <Bullseye>
            <EmptyState>
              <EmptyStateIcon icon={CubesIcon} />
              <Title headingLevel="h4" size="lg">
                {`No files match '${props.search}'.`}
              </Title>
            </EmptyState>
          </Bullseye>
        )}
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
  const history = useHistory();
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  const sortedAndFilteredFiles = useMemo(
    () =>
      props.workspaceFiles
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        .filter((file) => file.relativePath !== props.currentWorkspaceFile?.relativePath),
    [props.workspaceFiles, props.currentWorkspaceFile]
  );

  const models = useMemo(
    () => sortedAndFilteredFiles.filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, sortedAndFilteredFiles]
  );

  const otherFiles = useMemo(
    () => sortedAndFilteredFiles.filter((file) => !editorEnvelopeLocator.hasMappingFor(file.relativePath)),
    [editorEnvelopeLocator, sortedAndFilteredFiles]
  );

  const [search, setSearch] = useState("");
  const [otherFilesSearch, setOtherFilesSearch] = useState("");

  return (
    <>
      <Split>
        <SplitItem isFilled={true}>
          <MenuItem direction="up" itemId={props.workspaceDescriptor.workspaceId}>
            All
          </MenuItem>
        </SplitItem>
        <SplitItem>
          <FilesDropdownModeIcons
            filesDropdownMode={props.filesDropdownMode}
            setFilesDropdownMode={props.setFilesDropdownMode}
          />
          &nbsp; &nbsp;
        </SplitItem>
      </Split>
      <Divider component={"li"} />

      <Split>
        {(props.filesDropdownMode === FilesDropdownMode.LIST_MODELS ||
          props.filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS) && (
          <SplitItem isFilled={true} style={{ minWidth: `${MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX}px` }}>
            <>
              <SearchableFilesMenuGroup
                search={search}
                setSearch={setSearch}
                filesDropdownMode={props.filesDropdownMode}
                shouldFocusOnSearch={props.shouldFocusOnSearch}
                label={`Models in '${props.workspaceDescriptor.name}'`}
                allFiles={models}
              >
                {({ filteredFiles }) => (
                  <>
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
                              onClick={props.onSelectFile}
                              style={style}
                              className={"kie-tools--file-switcher-no-padding-menu-item"}
                            >
                              <FileDataList file={filteredFiles[index]} isEditable={true} />
                            </MenuItem>
                          )}
                        </VariableSizeList>
                      )}
                    </AutoSizer>
                  </>
                )}
              </SearchableFilesMenuGroup>
              {otherFiles.length > 0 && (
                <>
                  <Divider component={"li"} />
                  <MenuGroup>
                    <MenuList>
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
                          ? "View other files"
                          : "Hide other files"}
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
              search={otherFilesSearch}
              setSearch={setOtherFilesSearch}
              filesDropdownMode={props.filesDropdownMode}
              shouldFocusOnSearch={props.shouldFocusOnSearch}
              label={`Other files in '${props.workspaceDescriptor.name}'`}
              allFiles={otherFiles}
            >
              {({ filteredFiles }) => (
                <>
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
                            component={"span"}
                            onClick={() => {}}
                            style={style}
                            className={"kie-tools--file-switcher-no-padding-menu-item"}
                          >
                            <FileDataList file={filteredFiles[index]} isEditable={false} />
                          </MenuItem>
                        )}
                      </VariableSizeList>
                    )}
                  </AutoSizer>
                </>
              )}
            </SearchableFilesMenuGroup>
          </SplitItem>
        )}

        {props.filesDropdownMode === FilesDropdownMode.CAROUSEL && (
          <SplitItem isFilled={true}>
            <SearchableFilesMenuGroup
              search={search}
              setSearch={setSearch}
              filesDropdownMode={props.filesDropdownMode}
              shouldFocusOnSearch={props.shouldFocusOnSearch}
              label={`Models in '${props.workspaceDescriptor.name}'`}
              allFiles={models}
            >
              {({ filteredFiles }) => (
                <Gallery
                  hasGutter={true}
                  style={{
                    padding: "8px",
                    borderTop: "var(--pf-global--BorderWidth--sm) solid var(--pf-global--BorderColor--100)",
                  }}
                >
                  {filteredFiles.slice(0, MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN).map((file) => (
                    <Card
                      key={file.relativePath}
                      isSelectable={true}
                      isRounded={true}
                      isCompact={true}
                      isHoverable={true}
                      isFullHeight={true}
                      onClick={() => {
                        history.push({
                          pathname: routes.workspaceWithFilePath.path({
                            workspaceId: file.workspaceId,
                            fileRelativePath: file.relativePathWithoutExtension,
                            extension: file.extension,
                          }),
                        });

                        props.onSelectFile();
                      }}
                    >
                      <FileLink file={file}>
                        {/* The default display:flex makes the text overflow */}
                        <CardHeader style={{ display: "block" }}>
                          <CardHeaderMain>
                            <FileListItem file={file} isEditable={true} />
                          </CardHeaderMain>
                        </CardHeader>
                      </FileLink>
                      <Divider inset={{ default: "insetMd" }} />
                      <CardBody style={{ padding: 0 }}>
                        <FileSvg workspaceFile={file} />
                      </CardBody>
                    </Card>
                  ))}
                  {filteredFiles.length > MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN && (
                    <Card style={{ border: 0 }}>
                      <CardBody>
                        <Bullseye>
                          <div>...and {filteredFiles.length - MAX_NUMBER_OF_CAROUSEL_ITEMS_SHOWN} more.</div>
                        </Bullseye>
                      </CardBody>
                    </Card>
                  )}
                </Gallery>
              )}
            </SearchableFilesMenuGroup>
          </SplitItem>
        )}
      </Split>
    </>
  );
}

export function FilesDropdownModeIcons(props: {
  filesDropdownMode: FilesDropdownMode;
  setFilesDropdownMode: React.Dispatch<React.SetStateAction<FilesDropdownMode>>;
}) {
  return (
    <>
      {props.filesDropdownMode === FilesDropdownMode.CAROUSEL && (
        <Button
          className={"kie-tools--masthead-hoverable"}
          variant="plain"
          aria-label="Switch to list view"
          onClick={(e) => {
            e.stopPropagation();
            props.setFilesDropdownMode(FilesDropdownMode.LIST_MODELS);
          }}
        >
          <ListIcon />
        </Button>
      )}
      {(props.filesDropdownMode === FilesDropdownMode.LIST_MODELS ||
        props.filesDropdownMode === FilesDropdownMode.LIST_MODELS_AND_OTHERS) && (
        <Button
          className={"kie-tools--masthead-hoverable"}
          variant="plain"
          aria-label="Switch to carousel view"
          onClick={(e) => {
            e.stopPropagation();
            props.setFilesDropdownMode(FilesDropdownMode.CAROUSEL);
          }}
        >
          <ThLargeIcon />
        </Button>
      )}
    </>
  );
}
