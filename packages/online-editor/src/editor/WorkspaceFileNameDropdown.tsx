import { ActiveWorkspace } from "../workspace/model/ActiveWorkspace";
import { decoder, useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
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
import {
  PromiseStateWrapper,
  useCombinedPromiseState,
  useDelay,
  useDelayedPromiseState,
} from "../workspace/hooks/PromiseState";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { WorkspaceDescriptor } from "../workspace/model/WorkspaceDescriptor";
import { useWorkspacesFiles } from "../workspace/hooks/WorkspacesFiles";
import { Skeleton } from "@patternfly/react-core/dist/js/components/Skeleton";
import { Card, CardBody, CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { useCancelableEffect } from "../common/Hooks";
import { useHistory } from "react-router";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { WorkspaceKind } from "../workspace/model/WorkspaceOrigin";
import { Label } from "@patternfly/react-core/dist/js/components/Label";

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

      //FIXME: Not ideal using service directly.
      const exists = await workspaces.service.existsFile({
        fs: await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: newRelativePath,
      });
      setNewFileNameValid(!exists);
    },
    [props.workspaceFile, workspaces.service, workspaces.fsService]
  );

  const onRenameWorkspaceFile = useCallback(
    async (newFileName: string | undefined) => {
      if (!newFileName || !newFileNameValid) {
        resetWorkspaceFileName();
        return;
      }

      if (newFileName === props.workspaceFile.nameWithoutExtension) {
        return;
      }

      await workspaces.renameFile({
        fs: await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
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
    setMenuHeights({});
  }, [props.workspace]);

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
    // do not try to simply this ternary's condition as some heights are 0, resulting in an infinite loop.
    setMenuHeights((prev) => (prev[menuId] !== undefined ? prev : { ...prev, [menuId]: height }));
  }, []);

  const workspacesMenuItems = useMemo(() => {
    if (activeMenu !== ROOT_MENU_ID && activeMenu === `dd${props.workspace.descriptor.workspaceId}`) {
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
                              //FIXME: Change this when it is possible to move a file.
                              if (props.workspaceFile.relativePath !== props.workspaceFile.name) {
                                setPopoverVisible(true);
                              }
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
            <Menu
              style={{
                boxShadow: "none",
                minWidth: filesDropdownMode === FilesDropdownMode.CAROUSEL ? "calc(100vw - 16px)" : "400px",
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
                maxMenuHeight={"600px"}
                menuHeight={activeMenu === ROOT_MENU_ID ? undefined : `${menuHeights[activeMenu]}px`}
                style={{ overflow: "hidden" }}
              >
                <MenuList>
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

function WorkspacesMenuItems(props: {
  activeMenu: string;
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
                      description={`${workspaceFiles.get(descriptor.workspaceId)!.length} files, ${
                        workspaceFiles
                          .get(descriptor.workspaceId)!
                          .filter((f) => SUPPORTED_FILES_EDITABLE.includes(f.extension)).length
                      } models`}
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
                      <>
                        <FolderIcon />
                        &nbsp;&nbsp;
                        {descriptor.name}
                        &nbsp;&nbsp;
                        {descriptor.origin.kind === WorkspaceKind.GIST && <Label>Gist</Label>}
                      </>
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

function FileSvg(props: { workspaceFile: WorkspaceFile }) {
  const workspaces = useWorkspaces();
  const imgRef = useRef<HTMLImageElement>(null);
  const [svg, setSvg] = useDelayedPromiseState<string>(600);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        Promise.resolve()
          .then(async () =>
            workspaces.getFile({
              fs: await workspaces.fsService.getWorkspaceSvgsFs(props.workspaceFile.workspaceId),
              workspaceId: props.workspaceFile.workspaceId,
              relativePath: `${props.workspaceFile.relativePath}.svg`,
            })
          )
          .then(async (file) => {
            if (canceled.get()) {
              return;
            }

            if (file) {
              setSvg({ data: decoder.decode(await file.getFileContents()) });
            } else {
              setSvg({ error: "Can't find SVG for " + props.workspaceFile.relativePath });
            }
          });
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
          <img style={{ height: "200px" }} ref={imgRef} alt={"SVG for " + props.workspaceFile.relativePath} />
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
  shouldFocusOnSearch: boolean;
}) {
  const [search, setSearch] = useState("");
  const searchInputRef = useRef<HTMLInputElement>(null);
  const history = useHistory();
  const globals = useGlobals();

  useEffect(() => {
    if (props.shouldFocusOnSearch) {
      setTimeout(() => {
        searchInputRef.current?.focus();
      }, 500);
    }
  }, [props.shouldFocusOnSearch, props.filesDropdownMode]);

  const workspaceFilesToDisplay = useMemo(
    () =>
      props.workspaceFiles
        .sort((a, b) => a.relativePath.localeCompare(b.relativePath))
        //FIXME: This is a very naive search algorithm.
        .filter((file) => file.name.toLowerCase().includes(search.toLowerCase()))
        .filter((file) => SUPPORTED_FILES_EDITABLE.includes(file.extension))
        .filter((file) => file.relativePath !== props.currentWorkspaceFile?.relativePath),
    [props.currentWorkspaceFile, props.workspaceFiles, search]
  );

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
      <MenuGroup key={props.workspaceDescriptor.workspaceId} label={`Files in ${props.workspaceDescriptor.name}`}>
        <MenuInput>
          <TextInput
            ref={searchInputRef}
            value={search}
            aria-label={"Filter menu items"}
            iconVariant={"search"}
            type={"search"}
            onChange={(value) => setSearch(value)}
          />
        </MenuInput>
        {props.filesDropdownMode === FilesDropdownMode.LIST &&
          workspaceFilesToDisplay.map((file) => (
            <FileMenuItem key={file.relativePath} file={file} onSelectFile={props.onSelectFile} />
          ))}
      </MenuGroup>
      {props.filesDropdownMode === FilesDropdownMode.CAROUSEL && (
        <MenuGroup>
          <Gallery hasGutter={true}>
            {workspaceFilesToDisplay.map((file) => (
              <Card
                key={file.relativePath}
                isSelectable={true}
                isRounded={true}
                isCompact={true}
                isHoverable={true}
                isFullHeight={true}
                onClick={() => {
                  history.push({
                    pathname: globals.routes.workspaceWithFilePath.path({
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
                  <FileSvg workspaceFile={file} />
                </CardBody>
              </Card>
            ))}
          </Gallery>
        </MenuGroup>
      )}
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

export function FilesDropdownModeIcons(props: {
  filesDropdownMode: FilesDropdownMode;
  setFilesDropdownMode: React.Dispatch<React.SetStateAction<FilesDropdownMode>>;
}) {
  return (
    <>
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
    </>
  );
}
