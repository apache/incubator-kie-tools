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

import React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { Card, CardBody, CardFooter, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Split, SplitItem } from "@patternfly/react-core/dist/js/layouts/Split";
import { UploadIcon } from "@patternfly/react-icons/dist/js/icons/upload-icon";
import { useCallback, useRef, useState } from "react";
import { useDropzone } from "react-dropzone";
import { CreateWorkspaceFromUploadedFolder } from "../../editor/CreateWorkspaceFromUploadedFolder";
import { useRoutes } from "../../navigation/Hooks";
import { useNavigate } from "react-router-dom";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

enum UploadType {
  NONE,
  FILES,
  FOLDER,
  DND,
}

export function UploadCard() {
  const routes = useRoutes();
  const navigate = useNavigate();
  const workspaces = useWorkspaces();

  const [uploading, setUploading] = useState(UploadType.NONE);

  const uploadFiles = useCallback(
    async (type: UploadType, acceptedFiles: File[]) => {
      setUploading(type);
      const val = await CreateWorkspaceFromUploadedFolder({ files: acceptedFiles, workspaces });

      if (!val) {
        return;
      }

      navigate({
        pathname: routes.workspaceWithFilePath.path({
          workspaceId: val.workspaceId,
          fileRelativePath: val.fileRelativePath,
        }),
      });
    },
    [navigate, routes, workspaces]
  );

  const { acceptedFiles, getRootProps, getInputProps, isDragActive, draggedFiles } = useDropzone({
    onDrop: (acceptedFiles) => uploadFiles(UploadType.DND, acceptedFiles),
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
            onChange={(e) => uploadFiles(UploadType.FILES, Array.from(e.target.files ?? []))}
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
              return uploadFiles(UploadType.FOLDER, files);
            }}
          />
        </CardFooter>
      </Card>
    </div>
  );
}
