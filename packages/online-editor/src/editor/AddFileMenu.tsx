import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { FileLabel } from "../workspace/pages/FileLabel";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrilldownMenu,
  Menu,
  MenuContent,
  MenuInput,
  MenuItem,
  MenuList,
} from "@patternfly/react-core/dist/js/components/Menu";
import { SupportedFileExtensions, useGlobals } from "../common/GlobalContext";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { extractFileExtension, removeDirectories, removeFileExtension } from "../common/utils";

export function AddFileMenu(props: {
  destinationDirPath: string;
  workspaceId: string;
  onAddFile: (file: WorkspaceFile) => Promise<void>;
}) {
  const [menuDrilledIn, setMenuDrilledIn] = useState<string[]>([]);
  const [drilldownPath, setDrilldownPath] = useState<string[]>([]);
  const [menuHeights, setMenuHeights] = useState<{ [key: string]: number }>({});
  const [activeMenu, setActiveMenu] = useState("addFileRootMenu");

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

  const workspaces = useWorkspaces();
  const globals = useGlobals();

  const addEmptyFile = useCallback(
    async (extension: SupportedFileExtensions) => {
      const file = await workspaces.addEmptyFile({
        fs: await workspaces.fsService.getWorkspaceFs(props.workspaceId),
        workspaceId: props.workspaceId,
        destinationDirRelativePath: props.destinationDirPath,
        extension,
      });
      await props.onAddFile(file);
    },
    [props, workspaces]
  );

  const urlInputRef = useRef<HTMLInputElement>(null);
  useEffect(() => {
    if (activeMenu === "importFromUrlMenu") {
      setTimeout(() => {
        urlInputRef.current?.focus();
      }, 500);
    }
  }, [activeMenu, urlInputRef]);

  const [isImporting, setImporting] = useState(false);

  //FIXME: We have to unify this logic with `NewWorkspaceFromUrlPage.tsx`
  const importFromUrl = useCallback(
    async (url: string) => {
      setImporting(true);
      try {
        const response = await fetch(url);
        if (!response.ok) {
          return;
        }

        const content = await response.text();
        const file = await workspaces.addFile({
          fs: await workspaces.fsService.getWorkspaceFs(props.workspaceId),
          workspaceId: props.workspaceId,
          name: decodeURIComponent(removeFileExtension(removeDirectories(url) ?? "Imported file")),
          extension: extractFileExtension(url)!,
          content,
          destinationDirRelativePath: props.destinationDirPath,
        });
        await props.onAddFile(file);
      } finally {
        setImporting(false);
      }
    },
    [props, workspaces]
  );
  const addSample = useCallback(
    (extension: SupportedFileExtensions) => importFromUrl(globals.routes.static.sample.path({ type: extension })),
    [importFromUrl, globals]
  );

  return (
    <Menu
      style={{ boxShadow: "none", minWidth: "400px" }}
      id="addFileRootMenu"
      containsDrilldown={true}
      onDrillIn={drillIn}
      onDrillOut={drillOut}
      activeMenu={activeMenu}
      onGetMenuHeight={setHeight}
      drilldownItemPath={drilldownPath}
      drilledInMenus={menuDrilledIn}
    >
      <MenuContent menuHeight={`${menuHeights[activeMenu]}px`}>
        <MenuList>
          <MenuItem
            itemId={"newBpmnItemId"}
            onClick={() => addEmptyFile("bpmn")}
            description="BPMN files are used to generate business processes"
          >
            <b>
              <Flex>
                <FlexItem>Process</FlexItem>
                <FlexItem>
                  <FileLabel extension={"bpmn"} />
                </FlexItem>
              </Flex>
            </b>
          </MenuItem>
          <MenuItem
            itemId={"newDmnItemId"}
            onClick={() => addEmptyFile("dmn")}
            description="DMN files are used to generate decision models"
          >
            <b>
              <Flex>
                <FlexItem>Decision</FlexItem>
                <FlexItem>
                  <FileLabel extension={"dmn"} />
                </FlexItem>
              </Flex>
            </b>
          </MenuItem>
          <MenuItem
            itemId={"newPmmlItemId"}
            onClick={() => addEmptyFile("pmml")}
            description="PMML files are used to generate scorecards"
          >
            <b>
              <Flex>
                <FlexItem>Scorecard</FlexItem>
                <FlexItem>
                  <FileLabel extension={"pmml"} />
                </FlexItem>
              </Flex>
            </b>
          </MenuItem>
          <MenuItem
            description={"Try model samples"}
            itemId="samplesItemId"
            direction={"down"}
            drilldownMenu={
              <DrilldownMenu id={"samplesMenu"}>
                <MenuItem direction="up">Back</MenuItem>
                <Divider />
                <MenuItem onClick={() => addSample("bpmn")} description="Lorem ipsum dolor sit amet">
                  <Flex>
                    <FlexItem>Sample</FlexItem>
                    <FlexItem>
                      <FileLabel extension={"bpmn"} />
                    </FlexItem>
                  </Flex>
                </MenuItem>
                <MenuItem onClick={() => addSample("dmn")} description="Ipsum lorem dolor amet sit">
                  <Flex>
                    <FlexItem>Sample</FlexItem>
                    <FlexItem>
                      <FileLabel extension={"dmn"} />
                    </FlexItem>
                  </Flex>
                </MenuItem>
                <MenuItem onClick={() => addSample("pmml")} description="Sit lorem amet dolor ipsum">
                  <Flex>
                    <FlexItem>Sample</FlexItem>
                    <FlexItem>
                      <FileLabel extension={"pmml"} />
                    </FlexItem>
                  </Flex>
                </MenuItem>
              </DrilldownMenu>
            }
          >
            <b>Samples</b>
          </MenuItem>
          <Divider />
          <MenuItem
            itemId={"importFromUrlItemId"}
            direction={"down"}
            drilldownMenu={
              <DrilldownMenu id={"importFromUrlMenu"}>
                <MenuItem direction="up">Back</MenuItem>
                <Divider />
                <MenuInput>
                  <TextInput ref={urlInputRef} placeholder={"URL"} />
                </MenuInput>
                <MenuInput>
                  <Button
                    variant={ButtonVariant.primary}
                    isLoading={isImporting}
                    onClick={() => {
                      const url = urlInputRef.current?.value;
                      if (url) {
                        return importFromUrl(url);
                      }
                    }}
                  >
                    Import
                  </Button>
                </MenuInput>
              </DrilldownMenu>
            }
          >
            From URL
          </MenuItem>
          <MenuItem itemId={"importUploadingItemId"}>
            Upload... &nbsp;&nbsp;
            <span style={{ color: "red" }}>
              <i>{"//TODO"}</i>
            </span>
          </MenuItem>
        </MenuList>
      </MenuContent>
    </Menu>
  );
}
