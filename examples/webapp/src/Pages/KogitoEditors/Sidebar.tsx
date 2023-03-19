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
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import { useCallback, useRef, useState } from "react";
import { Nav, NavItem, NavList } from "@patternfly/react-core/dist/js/components/Nav";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { EmbeddedEditorRef, useDirtyState } from "@kie-tools-core/editor/dist/embedded";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";

function extractFileExtension(fileName: string) {
  return fileName.match(/[.]/)
    ? fileName
        .split(".")
        ?.pop()
        ?.match(/[\w\d]+/)
        ?.pop()
    : undefined;
}

function removeFileExtension(fileName: string) {
  const fileExtension = extractFileExtension(fileName);
  if (!fileExtension) {
    return fileName;
  }
  return fileName.substr(0, fileName.length - fileExtension.length - 1);
}

interface Props {
  editor?: EmbeddedEditorRef;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  file: EmbeddedEditorFile;
  setFile: React.Dispatch<EmbeddedEditorFile>;
  fileExtension: string;
  accept: string;
}

/**
 * A Sidebar component to enable edit the file name, create new files, open sample and open a file.
 *
 * @param props
 * @constructor
 */
export function Sidebar(props: Props) {
  /**
   * A state which indicates the Editor dirty state
   */
  const isDirty = useDirtyState(props.editor!);

  const downloadRef = useRef<HTMLAnchorElement>(null);
  const onDownload = useCallback(() => {
    props.editor?.getStateControl().setSavedCommand();
    props.editor?.getContent().then((content) => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob)!;
        downloadRef.current.download = `${props.file.fileName}.${props.fileExtension}`;
        downloadRef.current.click();
      }
    });
  }, [props.editor]);

  const [fileName, setFileName] = useState(props.file.fileName);
  const onChangeName = useCallback(() => {
    props.setFile({
      ...props.file,
      fileName,
    });
  }, [props.file, fileName]);

  const onNewFile = useCallback(() => {
    setFileName("new-file");
    props.setFile({
      isReadOnly: false,
      fileExtension: props.fileExtension,
      fileName: "new-file",
      getFileContents: () => Promise.resolve(""),
      path: `new-file.${props.fileExtension}`,
    });
  }, []);

  const onOpenSample = useCallback(() => {
    setFileName("sample");
    props.setFile({
      isReadOnly: false,
      fileExtension: props.fileExtension,
      fileName: "sample",
      getFileContents: () => fetch(`examples/sample.${props.fileExtension}`).then((response) => response.text()),
      path: `sample.${props.fileExtension}`,
    });
  }, []);

  const inputRef = useRef<HTMLInputElement>(null);
  const onOpenFile = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (!inputRef.current!.files) {
      return;
    }

    const currentFile = inputRef.current!.files![0];
    if (!props.editorEnvelopeLocator.hasMappingFor(currentFile.name)) {
      return;
    }

    setFileName(removeFileExtension(currentFile.name));
    props.setFile({
      isReadOnly: false,
      fileExtension: extractFileExtension(currentFile.name)!,
      fileName: removeFileExtension(currentFile.name),
      path: currentFile.name,
      getFileContents: () =>
        new Promise<string | undefined>((resolve) => {
          const reader = new FileReader();
          reader.onload = (event: any) => resolve(event.target.result as string);
          reader.readAsText(currentFile);
        }),
    });
  }, []);

  return (
    <div>
      <Nav className={"webapp--page-navigation"}>
        <NavList>
          <NavItem className={"webapp--page-kogito-editors-sidebar--navigation-nav-item"}>
            <div className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-div"}>
              <TextInput
                className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-text-input"}
                value={fileName}
                type={"text"}
                aria-label={"Edit file name"}
                onChange={setFileName}
                onBlur={onChangeName}
              />
            </div>
          </NavItem>
          <NavItem className={"webapp--page-kogito-editors-sidebar--navigation-nav-item"}>
            <div className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-div"}>
              <a className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-a"} onClick={onNewFile}>
                New Empty File
              </a>
            </div>
          </NavItem>
          <NavItem className={"webapp--page-kogito-editors-sidebar--navigation-nav-item"}>
            <div className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-div"}>
              <a className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-a"}>
                Open File
                <input
                  accept={props.accept}
                  className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-open-file pf-c-button"}
                  type="file"
                  aria-label="File selection"
                  onChange={onOpenFile}
                  ref={inputRef}
                />
              </a>
            </div>
          </NavItem>
          <NavItem className={"webapp--page-kogito-editors-sidebar--navigation-nav-item"}>
            <div className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-div"}>
              <a className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-a"} onClick={onOpenSample}>
                Open Sample
              </a>
            </div>
          </NavItem>
          <NavItem className={"webapp--page-kogito-editors-sidebar--navigation-nav-item"}>
            <div className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-div"}>
              <a className={"webapp--page-kogito-editors-sidebar--navigation-nav-item-a"} onClick={onDownload}>
                Download
              </a>
            </div>
          </NavItem>
          {isDirty && (
            <div style={{ display: "flex", alignItems: "center", padding: "20px" }}>
              <p style={{ color: "red" }}>File Edited!</p>
            </div>
          )}
        </NavList>
      </Nav>
      <a ref={downloadRef} />
    </div>
  );
}
