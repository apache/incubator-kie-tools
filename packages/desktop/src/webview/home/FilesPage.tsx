/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import {
  Alert,
  AlertVariant,
  Bullseye,
  Button,
  Card,
  CardBody,
  CardFooter,
  Grid,
  GridItem,
  PageSection,
  TextInput,
  Title
} from "@patternfly/react-core";
import { useCallback, useEffect, useState } from "react";
import { useMemo } from "react";
import * as electron from "electron";
import { extractFileExtension, removeDirectories } from "../../common/utils";
import IpcRendererEvent = Electron.IpcRendererEvent;
import * as ReactDOM from "react-dom";
import { File, UNSAVED_FILE_NAME } from "../../common/File";
import { useContext } from "react";
import { GlobalContext } from "../common/GlobalContext";

interface Props {
  openFile: (file: File) => void;
}

export function FilesPage(props: Props) {
  const context = useContext(GlobalContext);
  const [lastOpenedFiles, setLastOpenedFiles] = useState<string[]>([]);

  const ipc = useMemo(() => electron.ipcRenderer, [electron.ipcRenderer]);

  const openFileByPath = useCallback((filePath: string) => {
    ipc.send("openFileByPath", { filePath: filePath });
  }, []);

  useEffect(() => {
    ipc.on("returnLastOpenedFiles", (event: IpcRendererEvent, data: { lastOpenedFiles: string[] }) => {
      setLastOpenedFiles(data.lastOpenedFiles);
    });

    ipc.send("requestLastOpenedFiles");

    return () => {
      ipc.removeAllListeners("returnLastOpenedFiles");
    };
  }, [ipc, lastOpenedFiles]);
  const [url, setURL] = useState("");

  const showResponseError = useCallback((statusCode: number, description: string) => {
    ReactDOM.render(
      <div className={"kogito--alert-container"}>
        <Alert variant={AlertVariant.danger} title="An error happened while fetching your file">
          <br />
          <b>Error details: </b>
          {statusCode}
          {statusCode && description && " - "}
          {description}
        </Alert>
      </div>,
      document.getElementById("app")!
    );
  }, []);

  const showFetchError = useCallback((description: string) => {
    ReactDOM.render(
      <div className={"kogito--alert-container"}>
        <Alert variant={AlertVariant.danger} title="An unexpected error happened while trying to fetch your file">
          <br />
          <b>Error details: </b>
          {description}
          <br />
          <br />
          <b>Possible cause: </b>
          The URL to your file must allow CORS in its response, which should contain the following header:
          <br />
          <pre>Access-Control-Allow-Origin: *</pre>
        </Alert>
      </div>,
      document.getElementById("app")!
    );
  }, []);

  const importFileByUrl = useCallback(() => {
    fetch(url)
      .then(response => {
        if (response.ok) {
          response.text().then(content => {
            const file = {
              filePath: UNSAVED_FILE_NAME,
              fileType: extractFileExtension(url)!,
              fileContent: content
            };

            ipc.send("enableFileMenus");
            props.openFile(file);
          });
        } else {
          showResponseError(response.status, response.statusText);
        }
      })
      .catch(error => {
        showFetchError(error.toString());
      });
  }, [url, props.openFile, showResponseError, showFetchError]);

  return (
    <PageSection>
      <Button onClick={() => context.fileActions.createNewFile("bpmn")}>Create BPMN</Button>
      <Button onClick={() => context.fileActions.createNewFile("dmn")}>Create DMN</Button>
      <Button onClick={() => context.fileActions.openSample("bpmn")}>Try BPMN</Button>
      <Button onClick={() => context.fileActions.openSample("dmn")}>Try DMN</Button>

      <hr />

      <TextInput id="file-url" name="file-url" aria-describedby="file-url-helper" value={url} onChange={setURL} />
      <Button onClick={importFileByUrl}>Import</Button>

      <hr />

      {lastOpenedFiles.length === 0 && <Bullseye>No files were opened yet.</Bullseye>}

      {lastOpenedFiles.length > 0 && (
        <Grid gutter="lg" className="pf-m-all-12-col pf-m-all-6-col-on-md">
          {lastOpenedFiles.map(filePath => (
            <GridItem className="pf-m-3-col" key={filePath}>
              <Card className={"kogito--card"} onClick={() => openFileByPath(filePath)}>
                <CardBody>
                  <img title={filePath} src={"images/" + extractFileExtension(filePath) + "_thumbnail.png"} />
                </CardBody>
                <CardFooter>
                  <Title headingLevel="h3" size="md">
                    {removeDirectories(filePath)}
                  </Title>
                </CardFooter>
              </Card>
            </GridItem>
          ))}
        </Grid>
      )}
    </PageSection>
  );
}
