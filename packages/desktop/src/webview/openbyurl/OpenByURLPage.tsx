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
import * as ReactDOM from "react-dom";
import {
  Bullseye,
  Form,
  FormGroup,
  Grid,
  GridItem,
  Page,
  PageSection,
  TextInput,
  Button,
  ActionGroup,
  Alert,
  AlertVariant
} from "@patternfly/react-core";
import { useCallback, useState } from "react";
import { File, UNSAVED_FILE_NAME } from "../../common/File";
import { extractFileExtension } from "../../common/utils";
import { useMemo } from "react";
import * as electron from "electron";

interface Props {
  openFile: (file: File) => void;
}

export function OpenByURLPage(props: Props) {
  const [url, setURL] = useState("");

  const ipc = useMemo(() => electron.ipcRenderer, [electron.ipcRenderer]);

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

  const openFile = useCallback(() => {
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
    <Page>
      <PageSection variant="light">
        <Bullseye>
          <Grid gutter="lg" className="pf-m-all-12-col pf-m-all-6-col-on-md">
            <GridItem className="pf-u-text-align-center pf-m-12-col">
              <Form>
                <FormGroup
                  label="File URL"
                  isRequired={true}
                  fieldId="file-url"
                  helperText="Please provide the file URL"
                >
                  <TextInput
                    isRequired={true}
                    id="file-url"
                    name="file-url"
                    aria-describedby="file-url-helper"
                    value={url}
                    onChange={setURL}
                  />
                </FormGroup>
                <ActionGroup>
                  <Button variant="primary" onClick={openFile}>
                    Open
                  </Button>
                </ActionGroup>
              </Form>
            </GridItem>
          </Grid>
        </Bullseye>
      </PageSection>
    </Page>
  );
}
