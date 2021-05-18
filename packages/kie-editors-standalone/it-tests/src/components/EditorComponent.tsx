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
import { useRef, useEffect, useState } from "react";

import { ContentType, ResourceContent } from "@kogito-tooling/workspace/dist/api";
import { StandaloneEditorApi } from "@kogito-tooling/kie-editors-standalone/dist/common/Editor";

import { FileLoader } from "./FileLoader";
import { ResourcesHolder, ResourcesHolderItem } from "../util/ResourcesHolder";

interface EditorOpenProps {
  container: Element;
  initialContent?: Promise<string>;
  readOnly?: boolean;
  origin?: string;
  resources?: InternalEditorOpenResources;
}

export type InternalEditorOpenResources = Map<
  string,
  {
    contentType: ContentType;
    content: Promise<string>;
  }
>;

export interface Props {
  id: string;
  initialContent?: Promise<string>;
  readOnly?: boolean;
  origin?: string;
  resources?: Map<string, ResourceContent>;
}

interface InternalProps {
  openEditor: (props: EditorOpenProps) => StandaloneEditorApi;
  id: string;
  initialContent?: Promise<string>;
  readOnly?: boolean;
  origin?: string;
  resources?: Map<string, ResourceContent>;
  defaultModelName?: string;
}
const divstyle = {
  flex: "1 1 auto",
};

const useForceUpdate = () => {
  const [value, setValue] = useState(0); // integer state
  return () => setValue((val) => ++val); // update the state to force render
};

export const EditorComponent: React.FC<InternalProps> = ({
  id,
  initialContent,
  readOnly,
  origin,
  defaultModelName,
  openEditor,
  resources,
}) => {
  const container = useRef<HTMLDivElement>(null);
  const [logs] = useState<string[]>([]);
  const [dirty, setDirty] = useState<boolean>(false);
  const [editor, setEditor] = useState<StandaloneEditorApi>();
  const forceUpdate = useForceUpdate();
  const [filesHolder, setFilesHolder] = useState<ResourcesHolder>(new ResourcesHolder(resources));
  const [modelName, setModelName] = useState<string>(defaultModelName || "new-file");

  useEffect(() => {
    const ed = openEditor({
      container: container.current!,
      initialContent,
      readOnly,
      origin,
      resources: createResourceContentCompatibleResources(filesHolder.resources),
    });
    ed.subscribeToContentChanges((isDirty) => {
      setDirty(isDirty);
    });
    setEditor(ed);
    return () => {
      ed!.close();
    };
  }, [id]);

  const setEditorContents = (resource: ResourcesHolderItem) => {
    editor!.setContent(resource.value.path, resource.value.content);
    setModelName(resource.name);
  };

  const createResourceContentCompatibleResources = (
    resources: Map<string, ResourceContent>
  ): InternalEditorOpenResources => {
    let compatibleResources: InternalEditorOpenResources = new Map();
    resources.forEach((value, key) => {
      compatibleResources.set(key, { content: Promise.resolve(value.content), contentType: value.type });
    });
    return compatibleResources;
  };

  const appendLog = (message: string) => {
    logs.push(message);
  };

  const editorUndo = () => {
    editor!.undo();
  };
  const editorRedo = () => {
    editor!.redo();
  };
  const editorSave = () => {
    editor!.getContent().then((result) => {
      filesHolder.addFile(
        { name: modelName, value: { path: modelName, type: ContentType.TEXT, content: result } },
        forceUpdate
      );
      editor?.markAsSaved();
    });
  };
  const editorSvg = () => {
    editor!.getPreview().then((content) => {
      const elem = window.document.createElement("a");
      elem.href = "data:text/svg+xml;charset=utf-8," + encodeURIComponent(content!);
      elem.download = modelName + ".svg";
      document.body.appendChild(elem);
      elem.click();
      document.body.removeChild(elem);
    });
  };
  const editorXml = () => {
    editor!.getContent().then((content) => {
      const elem = window.document.createElement("a");
      elem.href = "data:text/plain;charset=utf-8," + encodeURIComponent(content);
      elem.download = modelName;
      document.body.appendChild(elem);
      elem.click();
      document.body.removeChild(elem);
    });
  };

  const renderButtons = () => {
    return (
      <div id="buttons" style={{ flex: "0 1 auto" }}>
        <button id="undo" onClick={editorUndo} disabled={!dirty}>
          undo
        </button>
        <button id="redo" onClick={editorRedo} disabled={!dirty}>
          redo
        </button>
        <button id="save" onClick={editorSave} disabled={!dirty}>
          save
        </button>
        <button id="xml" onClick={editorXml}>
          Download XML
        </button>
        <button id="svg" onClick={editorSvg}>
          Download SVG
        </button>
      </div>
    );
  };

  return (
    <>
      <FileLoader
        allowDownload={true}
        allowUpload={true}
        onView={setEditorContents}
        resourcesHolder={filesHolder}
        onResourceChange={forceUpdate}
        ouiaId={id}
      />
      {dirty && (
        <div id="dirty" data-ouia-component-type="content-dirty">
          Unsaved changes.
        </div>
      )}
      {renderButtons()}
      <div id={id} data-ouia-component-type="editor" data-ouia-component-id={id} ref={container} style={divstyle} />
    </>
  );
};
