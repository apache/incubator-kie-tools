/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { SetStateAction, useRef } from "react";
import { ContentType, ResourceContent } from "@kie-tooling-core/workspace/dist/api";

export interface UploadedFile {
  name: string;
  value: ResourceContent;
}

interface Props {
  allowDownload: boolean;
  allowUpload: boolean;
  onView: (resource: UploadedFile) => void;
  files: UploadedFile[];
  setFiles: React.Dispatch<SetStateAction<UploadedFile[]>>;
  ouiaId?: string;
  ouiaSafe?: boolean;
}

export const FileLoader: React.FC<Props> = (props: Props) => {
  const fileInput = useRef<HTMLInputElement>(null);

  const handleSubmit = (event: any) => {
    event.preventDefault();
    if (fileInput!.current!.files!.length > 0) {
      Array.from(fileInput!.current!.files!).forEach((file) => {
        readUploadedFileAsText(file).then((fileContent) =>
          props.setFiles((files) => [
            ...files,
            { name: file.name, value: { path: file.name, type: ContentType.TEXT, content: fileContent } },
          ])
        );
      });
      fileInput.current!.value = "";
    }
  };
  const remove = (resource: UploadedFile) => {
    props.setFiles((files) => {
      const newFiles = Array.from(files);
      const resourceIndex = newFiles.findIndex((file) => file.name === resource.name);
      newFiles.splice(resourceIndex, 1);
      return newFiles;
    });
  };

  const download = (resource: UploadedFile) => {
    alert(resource.value.content);
  };

  const view = (resource: UploadedFile) => {
    props.onView(resource);
  };

  const renderedForm = (
    <>
      {props.allowUpload && (
        <form onSubmit={handleSubmit} data-ouia-component-type="file-upload-form">
          <label>
            File to upload:&nbsp;
            <input type="file" ref={fileInput} />
          </label>
          <button type="submit">Submit</button>
        </form>
      )}
    </>
  );

  const renderedFiles = (
    <ul data-ouia-component-type="file-list">
      {props.files.map((file) => {
        const item: UploadedFile = { name: file.name, value: file.value };
        return (
          <li data-ouia-component-type="file-list-item" data-ouia-component-id={item.name} key={item.name}>
            <span>{item.name}</span>
            <button
              data-ouia-component-type="file-list-item-button"
              data-ouia-component-id="view"
              onClick={() => view(item)}
            >
              view
            </button>
            {props.allowDownload && (
              <button
                data-ouia-component-type="file-list-item-button"
                data-ouia-component-id="download"
                onClick={() => download(item)}
              >
                download
              </button>
            )}
            <button
              data-ouia-component-type="file-list-item-button"
              data-ouia-component-id="remove"
              onClick={() => remove(item)}
            >
              remove
            </button>
          </li>
        );
      })}
    </ul>
  );

  return (
    <div
      data-ouia-component-type="file-loader"
      data-ouia-component-id={props.ouiaId}
      data-ouia-safe={props.ouiaSafe ? props.ouiaSafe : true}
    >
      {props.allowUpload && renderedForm}
      {renderedFiles}
    </div>
  );
};

function readUploadedFileAsText(inputFile: File): Promise<string> {
  const temporaryFileReader = new FileReader();

  return new Promise((resolve, reject) => {
    temporaryFileReader.onerror = () => {
      temporaryFileReader.abort();
      reject(new DOMException("Problem parsing input file."));
    };
    temporaryFileReader.onload = () => {
      resolve(temporaryFileReader!.result as string);
    };
    temporaryFileReader.readAsText(inputFile);
  });
}
