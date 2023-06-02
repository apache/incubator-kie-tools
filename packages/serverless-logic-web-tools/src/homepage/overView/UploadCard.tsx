/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useRef, useState } from "react";
import { isAbsolute } from "path";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useDropzone } from "react-dropzone";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useRoutes } from "../../navigation/Hooks";
import { useHistory } from "react-router";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { UploadIcon } from "@patternfly/react-icons/dist/js/icons/upload-icon";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { LocalFile } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/LocalFile";

enum UploadType {
  NONE,
  FILES,
  FOLDER,
  DND,
}

export function UploadCard(props: { expandWorkspace: (workspaceId: string) => void }) {
  const routes = useRoutes();
  const history = useHistory();
  const workspaces = useWorkspaces();

  const [uploading, setUploading] = useState(UploadType.NONE);

  const createWorkspaceFromUploadedFolder = useCallback(
    async (type: UploadType, acceptedFiles: File[]) => {
      function resolveRelativePath(args: { keepRootDirs: boolean; file: File & { path?: string } }) {
        if (!args.file.path) {
          return args.file.name;
        }

        if (args.keepRootDirs) {
          // Keeps the root directories name, just remove the preceding slash.
          return !isAbsolute(args.file.path)
            ? args.file.path
            : args.file.path.substring(args.file.path.indexOf("/") + 1);
        }

        // Remove first portion of the path, which is the uploaded directory name.
        return isAbsolute(args.file.path)
          ? args.file.path.substring(args.file.path.indexOf("/", 1) + 1)
          : args.file.path.substring(args.file.path.indexOf("/") + 1);
      }

      if (acceptedFiles.length === 0) {
        return;
      }

      const uploadedRootDirs = acceptedFiles.reduce((acc: Set<string>, file: File & { path?: string }) => {
        if (!file.path) {
          return acc.add(file.name);
        }

        return acc.add(
          isAbsolute(file.path)
            ? file.path.substring(1, file.path.indexOf("/", 1))
            : file.path.substring(0, file.path.indexOf("/"))
        );
      }, new Set<string>());

      const localFiles: LocalFile[] = await Promise.all(
        Array.from(acceptedFiles ?? []).map(async (file: File & { path?: string }) => {
          const path = resolveRelativePath({
            file,
            keepRootDirs: uploadedRootDirs.size > 1,
          });

          return {
            path,
            fileContents: await (async () =>
              new Promise<Uint8Array>((res) => {
                const reader = new FileReader();
                reader.onload = (event: ProgressEvent<FileReader>) =>
                  res(new Uint8Array(event.target?.result as ArrayBuffer));
                reader.readAsArrayBuffer(file);
              }))(),
          };
        })
      );

      const preferredName =
        uploadedRootDirs.size !== 1
          ? undefined
          : [...uploadedRootDirs][0] === localFiles[0].path
          ? undefined
          : [...uploadedRootDirs][0];

      setUploading(type);

      try {
        const { workspace, suggestedFirstFile } = await workspaces.createWorkspaceFromLocal({
          localFiles,
          preferredName,
        });

        if (!suggestedFirstFile) {
          return props.expandWorkspace(workspace.workspaceId);
        }

        history.push({
          pathname: routes.workspaceWithFilePath.path({
            workspaceId: workspace.workspaceId,
            fileRelativePath: suggestedFirstFile.relativePathWithoutExtension,
            extension: suggestedFirstFile.extension,
          }),
        });
      } finally {
        // setUploading(UploadType.NONE);
      }
    },
    [props, workspaces, history, routes]
  );

  const { acceptedFiles, getRootProps, getInputProps, isDragActive, draggedFiles } = useDropzone({
    onDrop: (acceptedFiles) => createWorkspaceFromUploadedFolder(UploadType.DND, acceptedFiles),
    noClick: true,
    noKeyboard: true,
    noDragEventsBubbling: false,
  });

  const uploadFilesInputRef = useRef<HTMLInputElement>(null);
  const uploadFolderInputRef = useRef<HTMLInputElement>(null);

  return (
    <div {...getRootProps()} className={"dropzone"} style={{ position: "relative" }}>
      {(isDragActive || uploading === UploadType.DND) && (
        <div
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            margin: "8px",
            width: "calc(100% - 16px)",
            height: "calc(100% - 16px)",
            backdropFilter: "blur(2px)",
            backgroundColor: "rgba(255, 255, 255, 0.9)",
            border: "5px  dashed lightgray",
            borderRadius: "16px",
            pointerEvents: "none",
            zIndex: 999,
          }}
        >
          {uploading === UploadType.DND && (
            <Bullseye>
              <TextContent>
                <Text component={TextVariants.h3}>
                  <Spinner size={"md"} />
                  &nbsp;&nbsp; Uploading {acceptedFiles.length} file(s).
                </Text>
              </TextContent>
            </Bullseye>
          )}
          {isDragActive && (
            <Bullseye>
              <TextContent>
                <Text component={TextVariants.h3}>Upload {draggedFiles.length} items(s).</Text>
              </TextContent>
            </Bullseye>
          )}
        </div>
      )}
      <input id={"upload-field"} {...getInputProps()} />
      <Card isFullHeight={true} isPlain={true} isCompact={true}>
        <CardTitle>
          <TextContent>
            <Text component={TextVariants.h2}>
              <UploadIcon />
              &nbsp;&nbsp;Upload
            </Text>
          </TextContent>
        </CardTitle>
        <CardBody>
          <TextContent>
            <Text component={TextVariants.p}>Drag & drop files and folders here...</Text>
          </TextContent>
        </CardBody>
        <CardFooter>
          <Split isWrappable={false} style={{ alignItems: "center" }}>
            <SplitItem isFilled={true}>
              <Divider />
            </SplitItem>
            <SplitItem style={{ padding: "0 16px 0 16px", color: "gray" }}>or</SplitItem>
            <SplitItem isFilled={true}>
              <Divider />
            </SplitItem>
          </Split>
          <br />
          <Button
            style={{ paddingLeft: 0 }}
            iconPosition="right"
            icon={uploading === UploadType.FILES ? <Spinner size="md" style={{ marginLeft: "8px" }} /> : <></>}
            variant={ButtonVariant.link}
            onClick={() => uploadFilesInputRef.current?.click()}
          >
            Select files...
          </Button>
          <input
            type={"file"}
            ref={uploadFilesInputRef}
            style={{ display: "none" }}
            multiple={true}
            onChange={(e) => createWorkspaceFromUploadedFolder(UploadType.FILES, Array.from(e.target.files ?? []))}
          />

          <br />
          <Button
            style={{ paddingLeft: 0 }}
            iconPosition="right"
            icon={uploading === UploadType.FOLDER ? <Spinner size="md" style={{ marginLeft: "8px" }} /> : <></>}
            variant={ButtonVariant.link}
            onClick={() => uploadFolderInputRef.current?.click()}
          >
            Select folder...
          </Button>
          <input
            type={"file"}
            ref={uploadFolderInputRef}
            style={{ display: "none" }}
            /* @ts-expect-error directory and webkitdirectory are not available but works*/
            webkitdirectory=""
            onChange={(e) => {
              const files = Array.from(e.target.files ?? []).map((f: any) => {
                f.path = f.webkitRelativePath;
                return f;
              });
              return createWorkspaceFromUploadedFolder(UploadType.FOLDER, files);
            }}
          />
        </CardFooter>
      </Card>
    </div>
  );
}
