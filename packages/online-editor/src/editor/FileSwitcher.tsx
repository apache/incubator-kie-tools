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

import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
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
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
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
import { useWorkspaceDescriptorsPromise } from "../workspace/hooks/WorkspacesHooks";
import { PromiseStateWrapper, useCombinedPromiseState, usePromiseState } from "../workspace/hooks/PromiseState";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { WorkspaceDescriptor } from "../workspace/worker/api/WorkspaceDescriptor";
import { useWorkspacesFilesPromise } from "../workspace/hooks/WorkspacesFiles";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Card, CardBody, CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { useCancelableEffect } from "../reactExt/Hooks";
import { useHistory } from "react-router";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons/cubes-icon";
import { ArrowRightIcon } from "@patternfly/react-icons/dist/js/icons/arrow-right-icon";
import { ArrowLeftIcon } from "@patternfly/react-icons/dist/js/icons/arrow-left-icon";
import { WorkspaceLabel } from "../workspace/components/WorkspaceLabel";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { VariableSizeList } from "react-window";
import AutoSizer from "react-virtualized-auto-sizer";
import { FileDataList, getFileDataListHeight, SingleFileWorkspaceDataList } from "../filesList/FileDataList";
import {
  DataList,
  DataListCell,
  DataListItem,
  DataListItemCells,
  DataListItemRow,
} from "@patternfly/react-core/dist/js/components/DataList";
import { WorkspaceDescriptorDates } from "../workspace/components/WorkspaceDescriptorDates";

const ROOT_MENU_ID = "rootMenu";

enum FilesDropdownMode {
  LIST_MODELS,
  LIST_MODELS_AND_OTHERS,
  CAROUSEL,
}

const MIN_FILE_SWITCHER_PANEL_WIDTH_IN_PX = 500;

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
                    ? "500px"
                    : filesDropdownMode === FilesDropdownMode.CAROUSEL
                    ? "calc(100vw - 16px)"
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
                                  <Flex>
                                    <WorkspaceLabel descriptor={descriptor} />
                                    <TextContent>
                                      <Text
                                        component={TextVariants.small}
                                        style={{
                                          whiteSpace: "nowrap",
                                          overflow: "hidden",
                                          textOverflow: "ellipsis",
                                        }}
                                      >
                                        {`${workspaceFiles.get(descriptor.workspaceId)!.length} files, ${
                                          workspaceFiles
                                            .get(descriptor.workspaceId)!
                                            .filter((f) => editorEnvelopeLocator.hasMappingFor(f.relativePath)).length
                                        } models`}
                                      </Text>
                                    </TextContent>
                                  </Flex>
                                  <br />
                                  <TextContent>
                                    <Text
                                      component={TextVariants.p}
                                      style={{
                                        whiteSpace: "nowrap",
                                        overflow: "hidden",
                                        textOverflow: "ellipsis",
                                      }}
                                    >
                                      <FolderIcon />
                                      &nbsp;&nbsp;
                                      {descriptor.name}
                                    </Text>
                                  </TextContent>
                                  <WorkspaceDescriptorDates workspaceDescriptor={descriptor} />
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
  const workspaces = useWorkspaces();
  const imgRef = useRef<HTMLImageElement>(null);
  const [svg, setSvg] = usePromiseState<string>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        // FIXME: Uncomment when working on KOGITO-7805
        // Promise.resolve()
        //   .then(async () => svgService.getSvg(props.workspaceFile))
        //   .then(async (file) => {
        //     if (canceled.get()) {
        //       return;
        //     }
        //
        //     if (file) {
        //       setSvg({ data: decoder.decode(await file.getFileContents()) });
        //     } else {
        //       setSvg({ error: `Can't find SVG for '${props.workspaceFile.relativePath}'` });
        //     }
        //   });
      },
      [props.workspaceFile, workspaces, setSvg]
    )
  );

  useEffect(() => {
    if (svg.data) {
      const blob = new Blob([svg.data], { type: "image/svg+xml" });
      const url = URL.createObjectURL(blob);
      imgRef.current!.addEventListener("load", () => URL.revokeObjectURL(url), { once: true });
      imgRef.current!.src = url;
    }
  }, [svg]);

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
        promise={svg}
        resolved={() => (
          // for some reason, the CardBody adds an extra 6px after the image is loaded. 200 - 6 = 194px.
          <img style={{ height: "194px" }} ref={imgRef} alt={"SVG for " + props.workspaceFile.relativePath} />
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
      const task = setTimeout(() => {
        searchInputRef.current?.focus();
      }, 500);
      return () => {
        clearTimeout(task);
      };
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
          aria-label={"Other files menu items"}
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
      <div style={{ maxHeight: props.maxHeight, overflowY: "auto", height: props.maxHeight }}>
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

  return (
    <>
      <Split>
        <SplitItem isFilled={true}>
          <MenuItem direction="up" itemId={props.workspaceDescriptor.workspaceId}>
            All
          </MenuItem>
        </SplitItem>
        {/* FIXME: Uncomment when working on KOGITO-7805 */}
        {/*<SplitItem>*/}
        {/*  <FilesDropdownModeIcons*/}
        {/*    filesDropdownMode={props.filesDropdownMode}*/}
        {/*    setFilesDropdownMode={props.setFilesDropdownMode}*/}
        {/*  />*/}
        {/*  &nbsp; &nbsp;*/}
        {/*</SplitItem>*/}
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
              maxHeight={"500px"}
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
              maxHeight={"500px"}
              filesDropdownMode={props.filesDropdownMode}
              shouldFocusOnSearch={props.shouldFocusOnSearch}
              label={`Models in '${props.workspaceDescriptor.name}'`}
              allFiles={models}
            >
              {({ filteredFiles }) => (
                <Gallery hasGutter={true} style={{ padding: "8px" }}>
                  {filteredFiles.map((file) => (
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
                      <CardHeader>
                        <CardHeaderMain>
                          <CardTitle>
                            <Flex flexWrap={{ default: "nowrap" }}>
                              <FlexItem>
                                <TextContent>
                                  <Text component={TextVariants.h4}>{file.nameWithoutExtension}</Text>
                                </TextContent>
                              </FlexItem>
                              <FlexItem>
                                <FileLabel extension={file.extension} />
                              </FlexItem>
                            </Flex>
                          </CardTitle>
                        </CardHeaderMain>
                      </CardHeader>
                      <CardBody style={{ padding: 0 }}>
                        {/* FIXME: Uncomment when working on KOGITO-7805 */}
                        {/* <FileSvg workspaceFile={file} />*/}
                      </CardBody>
                    </Card>
                  ))}
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
