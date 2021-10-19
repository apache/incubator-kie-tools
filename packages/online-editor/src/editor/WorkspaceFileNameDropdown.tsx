import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { useGlobals } from "../common/GlobalContext";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { join } from "path";
import { SUPPORTED_FILES_EDITABLE } from "../workspace/SupportedFiles";
import { Dropdown } from "@patternfly/react-core/dist/js/components/Dropdown";
import { Link } from "react-router-dom";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { FileLabel } from "../workspace/pages/FileLabel";
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
  MenuItem,
  MenuList,
} from "@patternfly/react-core/dist/js/components/Menu";
import { CaretDownIcon } from "@patternfly/react-icons/dist/js/icons/caret-down-icon";
import { FolderIcon } from "@patternfly/react-icons/dist/js/icons/folder-icon";
import { ThLargeIcon } from "@patternfly/react-icons/dist/js/icons/th-large-icon";
import { ListIcon } from "@patternfly/react-icons/dist/js/icons/list-icon";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { useWorkspaceDescriptorsPromise } from "../workspace/hooks/WorkspacesHooks";
import {
  PromiseStateWrapper,
  useCombinedPromiseState,
  useDelay,
  usePromiseState,
} from "../workspace/hooks/PromiseState";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { WorkspaceDescriptor } from "../workspace/model/WorkspaceDescriptor";
import { Holder, useCancelableEffect } from "../common/Hooks";
import { useWorkspacesFiles } from "../workspace/hooks/WorkspacesFiles";

const ROOT_MENU_ID = "rootMenu";

enum FilesDropdownMode {
  LIST,
  CAROUSEL,
}

export function WorkspaceFileNameDropdown(props: { workspace: ActiveWorkspace; workspaceFile: WorkspaceFile }) {
  const workspaces = useWorkspaces();
  const workspaceFileNameRef = useRef<HTMLInputElement>(null);
  const [newFileNameValid, setNewFileNameValid] = useState<boolean>(true);
  const [filesDropdownMode, setFilesDropdownMode] = useState(FilesDropdownMode.LIST);

  const resetWorkspaceFileName = useCallback(() => {
    if (workspaceFileNameRef.current) {
      workspaceFileNameRef.current.value = props.workspaceFile.nameWithoutExtension;
      setNewFileNameValid(true);
    }
  }, [props.workspaceFile]);

  const checkNewFileName = useCallback(
    async (newFileNameWithoutExtension: string) => {
      if (newFileNameWithoutExtension === props.workspaceFile.nameWithoutExtension) {
        setNewFileNameValid(true);
        return;
      }

      const newRelativePath = join(
        props.workspaceFile.relativeDirPath,
        `${newFileNameWithoutExtension}.${props.workspaceFile.extension}`
      );

      const exists = await workspaces.service.existsFile({
        fs: workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: newRelativePath,
      });
      setNewFileNameValid(!exists);
    },
    [props.workspaceFile, workspaces.service, workspaces.fsService]
  );

  const onRenameWorkspaceFile = useCallback(
    (newFileName: string | undefined) => {
      if (!newFileName || !newFileNameValid) {
        resetWorkspaceFileName();
        return;
      }

      if (newFileName === props.workspaceFile.nameWithoutExtension) {
        return;
      }

      workspaces.renameFile({
        fs: workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
        file: props.workspaceFile,
        newFileName,
      });
    },
    [props.workspaceFile, workspaces, resetWorkspaceFileName, newFileNameValid]
  );

  const onWorkspaceFileNameKeyDown = useCallback(
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
    if (isFilesDropdownOpen) {
      return;
    }

    setMenuDrilledIn([ROOT_MENU_ID]);
    setDrilldownPath([props.workspace.descriptor.workspaceId]);
    setActiveMenu(props.workspace.descriptor.workspaceId);
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
    // do not try to simply this ternary's condition as some heights are 0, resulting in an infinite loop.
    setMenuHeights((prev) => (prev[menuId] !== undefined ? prev : { ...prev, [menuId]: height }));
  }, []);

  const workspacesMenuItems = useMemo(() => {
    if (activeMenu !== ROOT_MENU_ID) {
      return <></>;
    }

    return (
      <WorkspacesMenuItems
        currentWorkspace={props.workspace}
        onSelectFile={() => setFilesDropdownOpen(false)}
        filesDropdownMode={filesDropdownMode}
        setFilesDropdownMode={setFilesDropdownMode}
      />
    );
  }, [activeMenu, filesDropdownMode, props.workspace]);

  const dropdownContainerRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (!dropdownContainerRef.current?.parentNode) {
      return;
    }

    const dropdownContainerRefParent = dropdownContainerRef.current.parentNode as HTMLElement;

    if (filesDropdownMode === FilesDropdownMode.LIST || activeMenu === ROOT_MENU_ID) {
      dropdownContainerRefParent.style.position = "";
      dropdownContainerRefParent.style.bottom = "";
      dropdownContainerRefParent.style.left = "";
      dropdownContainerRefParent.style.width = "";
      dropdownContainerRefParent.style.height = "";
    } else {
      dropdownContainerRefParent.style.position = "absolute";
      dropdownContainerRefParent.style.bottom = "0";
      dropdownContainerRefParent.style.left = "0";
      dropdownContainerRefParent.style.width = "calc(100vw - 16px)";
      dropdownContainerRefParent.style.height = `${menuHeights[activeMenu] + 8}px`;
    }
  }, [filesDropdownMode, activeMenu, menuHeights]);

  return (
    <>
      <Flex alignItems={{ default: "alignItemsCenter" }} flexWrap={{ default: "nowrap" }}>
        <FlexItem style={{ display: "flex", alignItems: "baseline" }}>
          <Dropdown
            style={{ position: "relative" }}
            position={"right"}
            className={"kogito-tooling--masthead-hoverable"}
            isOpen={isFilesDropdownOpen}
            isPlain={true}
            toggle={
              <Toggle onToggle={setFilesDropdownOpen} id={"editor-page-masthead-files-dropdown-toggle"}>
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
                              {`A file already exists at this location. Please choose a different name.`}
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
                              setPopoverVisible(true);
                            }}
                            onKeyPress={(e) => e.stopPropagation()}
                            onKeyUp={(e) => e.stopPropagation()}
                            onKeyDown={onWorkspaceFileNameKeyDown}
                            onChange={checkNewFileName}
                            ref={workspaceFileNameRef}
                            type={"text"}
                            aria-label={"Edit file name"}
                            className={"kogito--editor__toolbar-title"}
                            onBlur={(e) => onRenameWorkspaceFile(e.target.value)}
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
            <div ref={dropdownContainerRef}>
              <Menu
                style={{ boxShadow: "none" }}
                id="rootMenu"
                containsDrilldown={true}
                drilldownItemPath={drilldownPath}
                drilledInMenus={menuDrilledIn}
                activeMenu={activeMenu}
                onDrillIn={drillIn}
                onDrillOut={drillOut}
                onGetMenuHeight={setHeight}
              >
                <MenuContent
                  maxMenuHeight={"70%"}
                  menuHeight={activeMenu === ROOT_MENU_ID ? undefined : `${menuHeights[activeMenu]}px`}
                  style={{ overflow: "hidden" }}
                >
                  <MenuList>
                    <MenuItem
                      itemId={props.workspace.descriptor.workspaceId}
                      description={"Current"}
                      direction={"down"}
                      drilldownMenu={
                        <DrilldownMenu id={props.workspace.descriptor.workspaceId}>
                          <FilesMenuItems
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
            </div>
          </Dropdown>
        </FlexItem>
      </Flex>
    </>
  );
}

function WorkspacesMenuItems(props: {
  currentWorkspace: ActiveWorkspace;
  onSelectFile: () => void;
  filesDropdownMode: FilesDropdownMode;
  setFilesDropdownMode: React.Dispatch<React.SetStateAction<FilesDropdownMode>>;
}) {
  const workspaceDescriptorsPromise = useWorkspaceDescriptorsPromise();
  const delay = useDelay(1000);
  const workspaceFiles = useWorkspacesFiles(workspaceDescriptorsPromise.data);
  const combined = useCombinedPromiseState({
    workspaceDescriptors: workspaceDescriptorsPromise,
    workspaceFiles,
    delay,
  });

  return (
    <>
      <Divider component={"li"} />
      <PromiseStateWrapper
        promise={combined}
        pending={
          <>
            <br />
            <br />
            <br />
            <br />
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner size={"md"} />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Loading...`}</Text>
              </TextContent>
            </Bullseye>
            <br />
          </>
        }
        resolved={({ workspaceDescriptors, workspaceFiles }) => (
          <>
            {workspaceDescriptors
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
                      direction={"down"}
                      drilldownMenu={
                        <DrilldownMenu id={descriptor.workspaceId}>
                          <FilesMenuItems
                            filesDropdownMode={props.filesDropdownMode}
                            setFilesDropdownMode={props.setFilesDropdownMode}
                            workspaceDescriptor={descriptor}
                            workspaceFiles={workspaceFiles.get(descriptor.workspaceId) ?? []}
                            onSelectFile={props.onSelectFile}
                          />
                        </DrilldownMenu>
                      }
                    >
                      {descriptor.name}
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

function FilesMenuItems(props: {
  workspaceDescriptor: WorkspaceDescriptor;
  workspaceFiles: WorkspaceFile[];
  currentWorkspaceFile?: WorkspaceFile;
  onSelectFile: () => void;
  filesDropdownMode: FilesDropdownMode;
  setFilesDropdownMode: React.Dispatch<React.SetStateAction<FilesDropdownMode>>;
}) {
  return (
    <>
      <Split>
        <SplitItem isFilled={true}>
          <MenuItem direction="up">All</MenuItem>
        </SplitItem>
        <SplitItem>
          {props.filesDropdownMode === FilesDropdownMode.CAROUSEL && (
            <Button
              className={"kogito-tooling--masthead-hoverable"}
              variant="plain"
              aria-label="Switch to list view"
              onClick={(e) => {
                e.stopPropagation();
                props.setFilesDropdownMode(FilesDropdownMode.LIST);
              }}
            >
              <ListIcon />
            </Button>
          )}
          {props.filesDropdownMode === FilesDropdownMode.LIST && (
            <Button
              className={"kogito-tooling--masthead-hoverable"}
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
          &nbsp; &nbsp;
        </SplitItem>
      </Split>
      <Divider component="li" />
      <MenuGroup key={props.workspaceDescriptor.workspaceId} label={`Files in ${props.workspaceDescriptor.name}`}>
        <MenuList>
          {props.workspaceFiles
            .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
            .filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension))
            .filter((file) => file.relativePath !== props.currentWorkspaceFile?.relativePath)
            .map((file) => (
              <FileMenuItem key={file.relativePath} file={file} onSelectFile={props.onSelectFile} />
            ))}
        </MenuList>
      </MenuGroup>
    </>
  );
}

export function FileMenuItem(props: { file: WorkspaceFile; onSelectFile: () => void }) {
  const globals = useGlobals();
  const workspaces = useWorkspaces();
  return (
    <MenuItem id={workspaces.getUniqueFileIdentifier(props.file)} onClick={props.onSelectFile}>
      <Link
        to={globals.routes.workspaceWithFilePath.path({
          workspaceId: props.file.workspaceId,
          fileRelativePath: props.file.relativePathWithoutExtension,
          extension: props.file.extension,
        })}
      >
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
      </Link>
    </MenuItem>
  );
}
