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

import { EditorContent, KogitoEdit, ResourceContent, ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kogito-tooling/core-api";
import * as React from "react";
import { useContext, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { EmbeddedEditorContext } from "../common/EmbeddedEditorContext";
import { EditorType, File } from "../common/File";

interface Props {
  file: File;
  onContentResponse: (content: EditorContent) => void;
  onSetContentError: () => void;
  onDirtyIndicatorChange: (isDirty: boolean) => void;
  onReady: () => void;
  onResourceContentRequest: (request: ResourceContentRequest) => ResourceContent;
  onResourceListRequest: (request: ResourceListRequest) => ResourcesList;
  onEditorUndo: (edits: ReadonlyArray<KogitoEdit>) => void;
  onEditorRedo: (edits: ReadonlyArray<KogitoEdit>) => void;
  onNewEdit: (edit: KogitoEdit) => void;
  onPreviewRequest: (previewSvg: string) => void;
}

export type EditorRef = {
  requestContent(): void;
} | null;

const RefForwardingEditor: React.RefForwardingComponent<EditorRef, Props> = (props, forwardedRef) => {
  const context: EmbeddedEditorContext = useContext(EmbeddedEditorContext);
  const iframeRef: React.RefObject<HTMLIFrameElement> = useRef<HTMLIFrameElement>(null);
  const editorType: EditorType = useMemo(() => props.file.editorType, []);

  const envelopeBusOuterMessageHandler = useMemo(() => {
    return context.envelopeBusOuterMessageHandlerFactory.createNew(iframeRef, self => ({
      pollInit() {
        self.request_initResponse(window.location.origin);
      },
      receive_languageRequest() {
        self.respond_languageRequest(context.router.getLanguageData(props.file.editorType));
      },
      receive_contentResponse(content: EditorContent) {
        props.onContentResponse(content);
      },
      receive_contentRequest() {
        props.file
          .getFileContents()
          .then(c => self.respond_contentRequest({ content: c || "", path: props.file.fileName }));
      },
      receive_setContentError() {
        props.onSetContentError();
      },
      receive_dirtyIndicatorChange(isDirty: boolean) {
        props.onDirtyIndicatorChange(isDirty);
      },
      receive_ready() {
        props.onReady();
      },
      receive_resourceContentRequest(request: ResourceContentRequest) {
        self.respond_resourceContent(props.onResourceContentRequest(request));
      },
      receive_resourceListRequest(request: ResourceListRequest) {
        self.respond_resourceList(props.onResourceListRequest(request));
      },
      notify_editorUndo: (edits: ReadonlyArray<KogitoEdit>) => {
        props.onEditorUndo(edits);
      },
      notify_editorRedo: (edits: ReadonlyArray<KogitoEdit>) => {
        props.onEditorRedo(edits);
      },
      receive_newEdit(edit: KogitoEdit) {
        props.onNewEdit(edit);
      },
      receive_previewRequest(previewSvg: string) {
        props.onPreviewRequest(previewSvg);
      }
    }));
  }, [editorType, props]);

  useEffect(() => {
    const listener = (msg: MessageEvent) => envelopeBusOuterMessageHandler.receive(msg.data);
    window.addEventListener("message", listener, false);
    envelopeBusOuterMessageHandler.startInitPolling();

    return () => {
      envelopeBusOuterMessageHandler.stopInitPolling();
      window.removeEventListener("message", listener);
    };
  }, [envelopeBusOuterMessageHandler]);

  useImperativeHandle(
    forwardedRef,
    () => ({
      requestContent: () => envelopeBusOuterMessageHandler.request_contentResponse()
    }),
    [envelopeBusOuterMessageHandler]
  );

  return (
    <iframe
      style={{ width: "100%", height: "100%" }}
      ref={iframeRef}
      id={"kogito-iframe"}
      className="kogito--editor"
      src={context.iframeTemplateRelativePath}
      title="Kogito editor"
    />
  );
};

export const BaseEditor = React.forwardRef(RefForwardingEditor);
