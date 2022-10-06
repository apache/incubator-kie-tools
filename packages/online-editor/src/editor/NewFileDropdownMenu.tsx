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

import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { FileLabel } from "../filesList/FileLabel";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
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
import {
  SupportedFileExtensions,
  useEditorEnvelopeLocator,
} from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { AlertsController, useAlert } from "../alerts/Alerts";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { basename, extname } from "path";
import { ImportFromUrlForm } from "../workspace/components/ImportFromUrlForm";
import { UrlType } from "../workspace/hooks/ImportableUrlHooks";
import { useRoutes } from "../navigation/Hooks";
import { decoder } from "../workspace/encoderdecoder/EncoderDecoder";

export function NewFileDropdownMenu(props: {
  alerts: AlertsController | undefined;
  destinationDirPath: string;
  workspaceId: string;
  onAddFile: (file?: WorkspaceFile) => Promise<void>;
}) {
  const uploadFileInputRef = useRef<HTMLInputElement>(null);

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
    // do not try to simplify this ternary's condition as some heights are 0, resulting in an infinite loop.
    setMenuHeights((prev) => (prev[menuId] !== undefined ? prev : { ...prev, [menuId]: height }));
  }, []);

  const workspaces = useWorkspaces();
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();

  const addEmptyFile = useCallback(
    async (extension: SupportedFileExtensions) => {
      const file = await workspaces.addEmptyFile({
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
  const [importingError, setImportingError] = useState<string>();

  const importFromUrl = useCallback(
    async (urlString?: string) => {
      if (!urlString) {
        return;
      }

      setImporting(true);
      setImportingError(undefined);

      try {
        const url = new URL(urlString);
        const extension = extname(url.pathname).replace(".", "");
        const name = decodeURIComponent(basename(url.pathname, extname(url.pathname)));

        const response = await fetch(urlString);
        if (!response.ok) {
          setImportingError(`${response.status}${response.statusText ? `- ${response.statusText}` : ""}`);
          return;
        }

        const content = await response.text();

        const file = await workspaces.addFile({
          workspaceId: props.workspaceId,
          name,
          extension,
          content,
          destinationDirRelativePath: props.destinationDirPath,
        });
        await props.onAddFile(file);
      } catch (e) {
        setImportingError(e.toString());
      } finally {
        // setImporting(false);
      }
    },
    [props, workspaces]
  );

  const addSample = useCallback(
    (extension: SupportedFileExtensions) =>
      importFromUrl(
        `${window.location.origin}${window.location.pathname}${routes.static.sample.path({ type: extension })}`
      ),
    [importFromUrl, routes]
  );

  const successfullyUploadedAlert = useAlert(
    props.alerts,
    useCallback(({ close }, staticArgs: { qtt: number }) => {
      return (
        <Alert
          variant="success"
          title={`Successfully uploaded ${staticArgs.qtt} file(s).`}
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, []),
    { durationInSeconds: 4 }
  );

  const handleFileUpload = useCallback(
    async (e: React.ChangeEvent<HTMLInputElement>) => {
      const filesToUpload = await Promise.all(
        Array.from(e.target.files ?? []).map(async (file: File) => {
          return {
            path: file.name,
            content: await new Promise<string>((res) => {
              const reader = new FileReader();
              reader.onload = (event: ProgressEvent<FileReader>) =>
                res(decoder.decode(event.target?.result as ArrayBuffer));
              reader.readAsArrayBuffer(file);
            }),
          };
        })
      );

      const uploadedFiles = await Promise.all(
        filesToUpload.map(async (file) => {
          return workspaces.addFile({
            workspaceId: props.workspaceId,
            name: basename(file.path, extname(file.path)),
            extension: extname(file.path).replace(".", ""),
            content: file.content,
            destinationDirRelativePath: props.destinationDirPath,
          });
        })
      );

      const fileToGoTo = uploadedFiles.filter((file) => editorEnvelopeLocator.hasMappingFor(file.relativePath)).pop();

      await props.onAddFile(fileToGoTo);
      successfullyUploadedAlert.show({ qtt: uploadedFiles.length });
    },
    [editorEnvelopeLocator, workspaces, props, successfullyUploadedAlert]
  );

  const [url, setUrl] = useState("");

  return (
    <Menu
      tabIndex={1}
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
            description="BPMN files are used to generate business workflows."
          >
            <b>
              <FileLabel style={{ marginBottom: "4px" }} extension={"bpmn"} />
            </b>
          </MenuItem>
          <MenuGroup label={" "}>
            <MenuItem
              itemId={"newDmnItemId"}
              onClick={() => addEmptyFile("dmn")}
              description="DMN files are used to generate decision models"
            >
              <b>
                <FileLabel style={{ marginBottom: "4px" }} extension={"dmn"} />
              </b>
            </MenuItem>
          </MenuGroup>
          <MenuGroup label={" "}>
            <MenuItem
              itemId={"newPmmlItemId"}
              onClick={() => addEmptyFile("pmml")}
              description="PMML files are used to generate scorecards"
            >
              <b>
                <FileLabel style={{ marginBottom: "4px" }} extension={"pmml"} />
              </b>
            </MenuItem>
          </MenuGroup>
          <Divider />
          <MenuItem
            description={"Try sample models"}
            itemId="samplesItemId"
            direction={"down"}
            drilldownMenu={
              <DrilldownMenu id={"samplesMenu"}>
                <MenuItem direction="up">Back</MenuItem>
                <Divider />
                <MenuItem
                  onClick={() => addSample("bpmn")}
                  description="BPMN files are used to generate business workflows."
                >
                  <Flex>
                    <FlexItem>Sample</FlexItem>
                    <FlexItem>
                      <FileLabel extension={"bpmn"} />
                    </FlexItem>
                  </Flex>
                </MenuItem>
                <MenuItem onClick={() => addSample("dmn")} description="DMN files are used to generate decision models">
                  <Flex>
                    <FlexItem>Sample</FlexItem>
                    <FlexItem>
                      <FileLabel extension={"dmn"} />
                    </FlexItem>
                  </Flex>
                </MenuItem>
                <MenuItem onClick={() => addSample("pmml")} description="PMML files are used to generate scorecards">
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
            Samples
          </MenuItem>
          <Divider />
          <MenuItem
            itemId={"importFromUrlItemId"}
            direction={"down"}
            drilldownMenu={
              <DrilldownMenu id={"importFromUrlMenu"}>
                <MenuItem direction="up">Back</MenuItem>
                <Divider />
                <MenuInput onKeyDown={(e) => e.stopPropagation()}>
                  <ImportFromUrlForm
                    importingError={importingError}
                    allowedTypes={[UrlType.FILE, UrlType.GIST_FILE, UrlType.GITHUB_FILE]}
                    urlInputRef={urlInputRef}
                    url={url}
                    onChange={(url) => {
                      setUrl(url);
                      setImportingError(undefined);
                    }}
                    onSubmit={() => importFromUrl(url)}
                  />
                </MenuInput>
                <MenuInput>
                  <Button
                    variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
                    isLoading={isImporting}
                    onClick={() => importFromUrl(url)}
                  >
                    Import
                  </Button>
                </MenuInput>
              </DrilldownMenu>
            }
          >
            From URL
          </MenuItem>
          <MenuItem itemId={"importUploadingItemId"} onClick={() => uploadFileInputRef.current?.click()}>
            Upload...
            <input
              ref={uploadFileInputRef}
              type="file"
              multiple={true}
              style={{ display: "none" }}
              onChange={handleFileUpload}
            />
          </MenuItem>
        </MenuList>
      </MenuContent>
    </Menu>
  );
}
