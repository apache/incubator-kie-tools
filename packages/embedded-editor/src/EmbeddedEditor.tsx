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

import { EditorContent, KogitoEdit, ResourceContent, ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kogito-tooling/core-api";
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly.css";
import * as React from "react";
import { useCallback, useImperativeHandle, useMemo, useRef } from "react";
import { File } from "./common/File";
import { GlobalContext } from "./common/GlobalContext";
import { OnlineEditorRouter } from "./common/OnlineEditorRouter";
import { Editor, EditorRef } from "./editor/Editor";
import { EnvelopeBusOuterMessageHandlerFactory } from "./editor/EnvelopeBusOuterMessageHandlerFactory";

interface Props {
  file: File;
  onContentResponse?: (content: EditorContent) => void;
  onSetContentError?: () => void;
  onDirtyIndicatorChange?: (isDirty: boolean) => void;
  onReady?: () => void;
  onResourceContentRequest?: (request: ResourceContentRequest) => ResourceContent;
  onResourceListRequest?: (request: ResourceListRequest) => ResourcesList;
  onEditorUndo?: (edits: ReadonlyArray<KogitoEdit>) => void;
  onEditorRedo?: (edits: ReadonlyArray<KogitoEdit>) => void;
  onNewEdit?: (edit: KogitoEdit) => void;
  onPreviewRequest?: (previewSvg: string) => void;
}

const iframeTemplateRelativePath: string = "envelope/index.html";

export type EmbeddedEditorRef = {
  requestContent(): void;
} | null;

const RefForwardingEmbeddedEditor: React.RefForwardingComponent<EmbeddedEditorRef, Props> = (props: Props, forwardedRef) => {
  const editorRef = useRef<EditorRef>(null);
  const envelopeBusOuterMessageHandlerFactory = useMemo(() => new EnvelopeBusOuterMessageHandlerFactory(), []);
  const onlineEditorRouter = useMemo(
    () =>
      new OnlineEditorRouter(
        new GwtEditorRoutes({
          bpmnPath: "gwt-editors/bpmn",
          dmnPath: "gwt-editors/dmn"
        })
      ),
    []
  );

  //Property functions default handling
  const onContentResponse = useCallback((content: EditorContent) => {
    if (props.onContentResponse) {
      props.onContentResponse(content);
    }
  }, [props.onContentResponse]);

  const onSetContentError = useCallback(() => {
    if (props.onSetContentError) {
      props.onSetContentError();
    }
  }, [props.onContentResponse]);

  const onDirtyIndicatorChange = useCallback((isDirty: boolean) => {
    if (props.onDirtyIndicatorChange) {
      props.onDirtyIndicatorChange(isDirty);
    }
  }, [props.onDirtyIndicatorChange]);

  const onReady = useCallback(() => {
    if (props.onReady) {
      props.onReady();
    }
  }, [props.onReady]);

  const onResourceContentRequest = useCallback((request: ResourceContentRequest) => {
    if (props.onResourceContentRequest) {
      return props.onResourceContentRequest(request);
    }
    return new ResourceContent(request.path, undefined);
  }, [props.onResourceContentRequest]);

  const onResourceListRequest = useCallback((request: ResourceListRequest) => {
    if (props.onResourceListRequest) {
      return props.onResourceListRequest(request);
    }
    return new ResourcesList(request.pattern, []);
  }, [props.onResourceListRequest]);

  const onEditorUndo = useCallback((edits: ReadonlyArray<KogitoEdit>) => {
    if (props.onEditorUndo) {
      props.onEditorUndo(edits);
    }
  }, [props.onEditorUndo]);

  const onEditorRedo = useCallback((edits: ReadonlyArray<KogitoEdit>) => {
    if (props.onEditorRedo) {
      props.onEditorRedo(edits);
    }
  }, [props.onEditorRedo]);

  const onNewEdit = useCallback((edit: KogitoEdit) => {
    if (props.onNewEdit) {
      props.onNewEdit(edit);
    }
  }, [props.onNewEdit]);

  const onPreviewRequest = useCallback((previewSvg: string) => {
    if (props.onPreviewRequest) {
      props.onPreviewRequest(previewSvg);
    }
  }, [props.onPreviewRequest]);

  useImperativeHandle(
    forwardedRef,
    () => ({
      requestContent: () => editorRef.current?.requestContent()
    }), [editorRef]);

  return (
    <GlobalContext.Provider
      value={{
        router: onlineEditorRouter,
        envelopeBusOuterMessageHandlerFactory: envelopeBusOuterMessageHandlerFactory,
        iframeTemplateRelativePath: iframeTemplateRelativePath
      }}
    >
      <Editor
        ref={editorRef}
        file={props.file}
        onLanguageRequest={() => onlineEditorRouter.getLanguageData(props.file.editorType)!}
        onContentResponse={onContentResponse}
        onSetContentError={onSetContentError}
        onDirtyIndicatorChange={onDirtyIndicatorChange}
        onReady={onReady}
        onResourceContentRequest={onResourceContentRequest}
        onResourceListRequest={onResourceListRequest}
        onEditorUndo={onEditorUndo}
        onEditorRedo={onEditorRedo}
        onNewEdit={onNewEdit}
        onPreviewRequest={onPreviewRequest}
      />
    </GlobalContext.Provider>
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
