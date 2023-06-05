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
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { FileLabel } from "../workspace/components/FileLabel";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrilldownMenu,
  Menu,
  MenuContent,
  MenuInput,
  MenuItem,
  MenuList,
} from "@patternfly/react-core/dist/js/components/Menu";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { basename } from "path";
import { ImportFromUrlForm } from "../workspace/components/ImportFromUrlForm";
import { useRoutes } from "../navigation/Hooks";
import { isEditable, SupportedFileExtensions } from "../extension";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { extractExtension } from "@kie-tools-core/workspaces-git-fs/dist/relativePath/WorkspaceFileRelativePathParser";
import { UrlType } from "../workspace/hooks/ImportableUrlHooks";
import { useGlobalAlert } from "../alerts/GlobalAlertsContext";
import { ValidatedOptions } from "@patternfly/react-core/dist/js";

const ROOT_MENU_ID = "addFileRootMenu";

export function NewFileDropdownMenu(props: {
  destinationDirPath: string;
  workspaceId: string;
  onAddFile: (file?: WorkspaceFile) => Promise<void>;
}) {
  const uploadFileInputRef = useRef<HTMLInputElement>(null);

  const [menuDrilledIn, setMenuDrilledIn] = useState<string[]>([]);
  const [drilldownPath, setDrilldownPath] = useState<string[]>([]);
  const [menuHeights, setMenuHeights] = useState<{ [key: string]: number }>({});
  const [activeMenu, setActiveMenu] = useState(ROOT_MENU_ID);
  const [url, setUrl] = useState("");
  const [isUrlValid, setIsUrlValid] = useState(ValidatedOptions.default);

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

  const workspaces = useWorkspaces();
  const routes = useRoutes();

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
        const extension = extractExtension(url.pathname);
        const name = decodeURIComponent(basename(url.pathname, `.${extension}`));

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
    (name: string, extension: SupportedFileExtensions) => {
      importFromUrl(
        `${window.location.origin}${window.location.pathname}${routes.static.sample.path({
          type: extension,
          name: name,
        })}`
      );
    },
    [importFromUrl, routes]
  );

  const successfullyUploadedAlert = useGlobalAlert(
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
          const extension = extractExtension(file.path);
          const name = decodeURIComponent(basename(file.path, `.${extension}`));
          return workspaces.addFile({
            workspaceId: props.workspaceId,
            name: name,
            extension: extension,
            content: file.content,
            destinationDirRelativePath: props.destinationDirPath,
          });
        })
      );

      const fileToGoTo = uploadedFiles.filter((file) => isEditable(file.relativePath)).pop();

      await props.onAddFile(fileToGoTo);
      successfullyUploadedAlert.show({ qtt: uploadedFiles.length });
    },
    [workspaces, props, successfullyUploadedAlert]
  );

  const NewJsonYamlDrilldownMenuItem = useCallback(
    (args: { prefixId: string; description: string; fileTypes: { json: FileTypes; yaml: FileTypes } }) => (
      <MenuItem
        itemId={`${args.prefixId}ItemId`}
        description={args.description}
        direction={"down"}
        drilldownMenu={
          <DrilldownMenu id={`${args.prefixId}File`}>
            <MenuItem direction="up">Back</MenuItem>
            <Divider />
            <MenuItem onClick={() => addEmptyFile(args.fileTypes.json)} itemId={`${args.prefixId}Json`}>
              JSON
            </MenuItem>
            <MenuItem onClick={() => addEmptyFile(args.fileTypes.yaml)} itemId={`${args.prefixId}Yaml`}>
              YAML
            </MenuItem>
          </DrilldownMenu>
        }
      >
        <b>
          <FileLabel style={{ marginBottom: "4px" }} extension={args.fileTypes.json} />
        </b>
      </MenuItem>
    ),
    [addEmptyFile]
  );

  return (
    <Menu
      style={{ boxShadow: "none", minWidth: "400px" }}
      id={ROOT_MENU_ID}
      containsDrilldown={true}
      onDrillIn={drillIn}
      onDrillOut={drillOut}
      activeMenu={activeMenu}
      onGetMenuHeight={setHeight}
      drilldownItemPath={drilldownPath}
      drilledInMenus={menuDrilledIn}
    >
      <MenuContent menuHeight={`${menuHeights[activeMenu]}px`}>
        <MenuList style={{ padding: 0 }}>
          <NewJsonYamlDrilldownMenuItem
            prefixId="newSwf"
            description="Define orchestration logic for services."
            fileTypes={{ json: FileTypes.SW_JSON, yaml: FileTypes.SW_YAML }}
          />
          <NewJsonYamlDrilldownMenuItem
            prefixId="newSd"
            description="Define decision logic for services."
            fileTypes={{ json: FileTypes.YARD_JSON, yaml: FileTypes.YARD_YAML }}
          />
          <MenuItem
            itemId={"newDashboardItemId"}
            onClick={() => addEmptyFile(FileTypes.DASH_YAML)}
            description="Define data visualization from data extracted from applications."
          >
            <b>
              <FileLabel style={{ marginBottom: "4px" }} extension={FileTypes.DASH_YAML} />
            </b>
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
                  <ImportFromUrlForm
                    importingError={importingError}
                    allowedTypes={[UrlType.FILE, UrlType.GIST_FILE, UrlType.GITHUB_FILE]}
                    urlInputRef={urlInputRef}
                    url={url}
                    onChange={(url) => {
                      setUrl(url);
                      setImportingError(undefined);
                    }}
                    onValidate={setIsUrlValid}
                    onSubmit={() => importFromUrl(url)}
                  />
                </MenuInput>
                <MenuInput>
                  <Button
                    variant={url.length > 0 ? ButtonVariant.primary : ButtonVariant.secondary}
                    isLoading={isImporting}
                    onClick={() => importFromUrl(url)}
                    isDisabled={isUrlValid !== ValidatedOptions.success}
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
