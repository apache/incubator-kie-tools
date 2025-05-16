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

import * as React from "react";
import { useMemo, useCallback, useEffect, useRef, useState } from "react";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { FileLabel } from "../workspace/components/FileLabel";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import {
  DrilldownMenu,
  Menu,
  MenuContent,
  MenuSearch,
  MenuItem,
  MenuList,
  MenuSearchInput,
} from "@patternfly/react-core/dist/js/components/Menu";
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
import { ValidatedOptions } from "@patternfly/react-core/dist/js/helpers";
import { useNavigate } from "react-router-dom";
import { CreateWorkspaceFromUploadedFolder } from "./CreateWorkspaceFromUploadedFolder";
import { ImportFromUrlButton } from "../homepage/overView/ImportFromUrlButton";

const ROOT_MENU_ID = "addFileRootMenu";

export function NewFileDropdownMenu(props: {
  destinationDirPath: string;
  workspaceId?: string;
  onAddFile?: (file?: WorkspaceFile) => Promise<void>;
}) {
  const uploadFileInputRef = useRef<HTMLInputElement>(null);

  const [menuDrilledIn, setMenuDrilledIn] = useState<string[]>([]);
  const [drilldownPath, setDrilldownPath] = useState<string[]>([]);
  const [menuHeights, setMenuHeights] = useState<{ [key: string]: number }>({});
  const [activeMenu, setActiveMenu] = useState(ROOT_MENU_ID);
  const [url, setUrl] = useState("");
  const [isUrlValid, setIsUrlValid] = useState(ValidatedOptions.default);
  const navigate = useNavigate();

  const allowedUrlTypes = useMemo(
    () => (!props.workspaceId ? undefined : [UrlType.FILE, UrlType.GIST_FILE, UrlType.GITHUB_FILE]),
    [props]
  );

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
      if (!props.workspaceId) {
        return navigate(routes.newModel.path({ extension }));
      }

      const file = await workspaces.addEmptyFile({
        workspaceId: props.workspaceId,
        destinationDirRelativePath: props.destinationDirPath,
        extension,
      });
      await props.onAddFile?.(file);
    },
    [props, workspaces, routes, navigate]
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

      if (!props.workspaceId) {
        return navigate({
          pathname: routes.importModel.path({}),
          search: routes.importModel.queryString({ url: urlString }),
        });
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
        await props.onAddFile?.(file);
      } catch (e) {
        setImportingError(e.toString());
      } finally {
        // setImporting(false);
      }
    },
    [props, workspaces, navigate, routes]
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

  const uploadFilesIntoWorkspace = useCallback(
    async (workspaceId: string, files: FileList | null) => {
      const filesToUpload = await Promise.all(
        Array.from(files ?? []).map(async (file: File) => {
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
            workspaceId,
            name: name,
            extension: extension,
            content: file.content,
            destinationDirRelativePath: props.destinationDirPath,
          });
        })
      );

      const fileToGoTo = uploadedFiles.filter((file) => isEditable(file.relativePath)).pop();

      await props.onAddFile?.(fileToGoTo);
      successfullyUploadedAlert.show({ qtt: uploadedFiles.length });
    },
    [workspaces, props, successfullyUploadedAlert]
  );

  const uploadFilesIntoNewWorkspace = useCallback(
    async (files: FileList | null) => {
      const filesToUpload = Array.from(files ?? []);
      const workspaceData = await CreateWorkspaceFromUploadedFolder({ files: filesToUpload, workspaces });

      if (!workspaceData) {
        return;
      }

      navigate({
        pathname: routes.workspaceWithFilePath.path({
          workspaceId: workspaceData.workspaceId,
          fileRelativePath: workspaceData.fileRelativePath,
        }),
      });
    },
    [navigate, routes, workspaces]
  );

  const handleFileUpload = useCallback(
    async (e: React.ChangeEvent<HTMLInputElement>) => {
      if (!props.workspaceId) {
        return uploadFilesIntoNewWorkspace(e.target.files);
      }
      await uploadFilesIntoWorkspace(props.workspaceId, e.target.files);
    },
    [uploadFilesIntoWorkspace, props, uploadFilesIntoNewWorkspace]
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
          <MenuItem
            itemId={"newSdItemId"}
            onClick={() => addEmptyFile(FileTypes.YARD_YAML)}
            description="Define decision logic for services."
          >
            <b>
              <FileLabel style={{ marginBottom: "4px" }} extension={FileTypes.YARD_YAML} />
            </b>
          </MenuItem>
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
                <MenuSearch>
                  <MenuSearchInput>
                    <ImportFromUrlForm
                      importingError={importingError}
                      allowedTypes={allowedUrlTypes}
                      urlInputRef={urlInputRef}
                      url={url}
                      onChange={(url) => {
                        setUrl(url);
                        setImportingError(undefined);
                      }}
                      onValidate={setIsUrlValid}
                      onSubmit={() => importFromUrl(url)}
                    />
                  </MenuSearchInput>
                </MenuSearch>
                <MenuSearch>
                  <MenuSearchInput>
                    <ImportFromUrlButton
                      allowedTypes={allowedUrlTypes}
                      url={url}
                      isUrlValid={isUrlValid}
                      isLoading={isImporting}
                      onClick={() => importFromUrl(url)}
                    />
                  </MenuSearchInput>
                </MenuSearch>
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
