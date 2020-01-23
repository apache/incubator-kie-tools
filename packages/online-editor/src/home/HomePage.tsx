/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useContext, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { GlobalContext } from "../common/GlobalContext";
import { EMPTY_FILE, File as UploadFile } from "../common/File";
import {
  Bullseye,
  Button,
  Grid,
  GridItem,
  Page,
  PageSection,
  Select,
  SelectOption,
  Stack,
  StackItem,
  Title,
  Toolbar,
  ToolbarItem
} from "@patternfly/react-core";

interface Props {
  onFileOpened: (file: UploadFile) => void;
}

export function HomePage(props: Props) {
  const context = useContext(GlobalContext);
  const history = useHistory();

  const uploadInputRef = useRef<HTMLInputElement>(null);
  const uploadBoxRef = useRef<HTMLDivElement>(null);

  const uploadBoxOnDragOver = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    uploadBoxRef.current!.className = "hover";
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const uploadBoxOnDragLeave = useCallback((e: React.DragEvent<HTMLDivElement>) => {
    uploadBoxRef.current!.className = "";
    e.stopPropagation();
    e.preventDefault();
    return false;
  }, []);

  const onFileUpload = useCallback(
    (file: File) => {
      props.onFileOpened({
        fileName: removeFileExtension(file.name),
        getFileContents: () =>
          new Promise<string | undefined>(resolve => {
            const reader = new FileReader();
            reader.onload = (event: any) => resolve(event.target.result as string);
            reader.readAsText(file);
          })
      });
      history.replace(context.routes.editor.url({ type: extractFileExtension(file.name)! }));
    },
    [context, history]
  );

  const uploadBoxOnDrop = useCallback(
    (e: React.DragEvent<HTMLDivElement>) => {
      uploadBoxRef.current!.className = "";
      e.stopPropagation();
      e.preventDefault();

      const file = e.dataTransfer.files[0];
      onFileUpload(file);

      return false;
    },
    [onFileUpload]
  );

  const editFile = useCallback(() => {
    if (uploadInputRef.current!.files) {
      const file = uploadInputRef.current!.files![0];
      onFileUpload(file);
    }
  }, [onFileUpload]);

  const editorTypeOptions = useMemo(() => [{ value: "BPMN" }, { value: "DMN" }], []);

  const [fileTypeSelect, setFileTypeSelect] = useState({ isExpanded: false, value: "BPMN" });

  const onSelectFileType = useCallback((event, selection) => {
    setFileTypeSelect({
      isExpanded: false,
      value: selection
    });
  }, []);

  const onToggleFileType = useCallback(
    isExpanded => {
      setFileTypeSelect({
        isExpanded: isExpanded,
        value: fileTypeSelect.value
      });
    },
    [fileTypeSelect]
  );

  const createFile = useCallback(() => {
    if (fileTypeSelect && fileTypeSelect.value) {
      props.onFileOpened(EMPTY_FILE);
      history.replace(context.routes.editor.url({ type: fileTypeSelect.value!.toLowerCase() }));
    }
  }, [context, history, fileTypeSelect]);

  const trySample = useCallback(() => {
    if (fileTypeSelect?.value) {
      const fileName = "sample";
      const fileExtension = fileTypeSelect.value!.toLowerCase();
      const filePath = `samples/${fileName}.${fileExtension}`;
      props.onFileOpened({
        fileName: fileName,
        getFileContents: () => fetch(filePath).then(response => response.text())
      });
      history.replace(context.routes.editor.url({ type: fileExtension }));
    }
  }, [context, history, fileTypeSelect]);

  return (
    <Page>
      <PageSection variant="light" style={{ flexBasis: "100%" }}>
        <Bullseye>
          <Grid gutter="lg" className="pf-m-all-12-col pf-m-all-6-col-on-md">
            <GridItem className="pf-u-text-align-center pf-m-12-col">
              <img src={"images/kogito_logo.png"} alt="Kogito Logo" />
            </GridItem>
            <GridItem>
              {/* Create side */}
              <Stack gutter="lg">
                <StackItem>
                  <Title headingLevel="h2" size="3xl">
                    Create
                  </Title>
                </StackItem>
                <StackItem>
                  <Toolbar>
                    <ToolbarItem>
                      <Select
                        onSelect={onSelectFileType}
                        onToggle={onToggleFileType}
                        isExpanded={fileTypeSelect.isExpanded}
                        selections={fileTypeSelect.value}
                        width={"7em"}
                      >
                        {editorTypeOptions.map((option, index) => (
                          <SelectOption key={index} value={option.value} />
                        ))}
                      </Select>
                      <Button className="pf-u-ml-md" variant="secondary" onClick={createFile}>
                        Create
                      </Button>
                      <Button className="pf-u-ml-md" variant="secondary" onClick={trySample}>
                        Try Sample
                      </Button>
                    </ToolbarItem>
                  </Toolbar>
                </StackItem>
              </Stack>
            </GridItem>
            <GridItem>
              {/* Edit side */}
              <Stack gutter="lg">
                <StackItem>
                  <Title headingLevel="h2" size="3xl">
                    Edit
                  </Title>
                </StackItem>
                <StackItem className="kogito--upload-box">
                  {/* Upload Drag Target */}
                  <div
                    ref={uploadBoxRef}
                    onDragOver={uploadBoxOnDragOver}
                    onDragLeave={uploadBoxOnDragLeave}
                    onDrop={uploadBoxOnDrop}
                  >
                    <Bullseye>Drop a BPMN or DMN file here</Bullseye>
                  </div>
                </StackItem>
                <StackItem className="kogito--upload-btn-container">
                  or
                  <div className="kogito--upload-btn">
                    <Button className="pf-u-ml-md" variant="secondary" onClick={editFile}>
                      Choose a local file
                    </Button>
                    <input className="pf-c-button" type="file" ref={uploadInputRef} onChange={editFile} />
                  </div>
                </StackItem>
              </Stack>
            </GridItem>
          </Grid>
        </Bullseye>
      </PageSection>
    </Page>
  );
}

function extractFileExtension(fileName: string) {
  return fileName
    .split(".")
    .pop()
    ?.match(/[\w\d]+/)
    ?.pop();
}

function removeFileExtension(fileName: string) {
  const fileExtension = extractFileExtension(fileName);

  if (!fileExtension) {
    return fileName;
  }

  return fileName.substr(0, fileName.length - fileExtension.length - 1);
}
