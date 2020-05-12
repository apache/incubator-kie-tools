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
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import * as React from "react";
import { Props } from "./EmbeddedEditor";

export function newEnvelopeBusOuterMessageHandler(
  props: Props,
  iframeRef: React.RefObject<HTMLIFrameElement>,
  onContentResponse: (content: EditorContent) => void,
  onSetContentError: (errorMessage: string) => void,
  onDirtyIndicatorChange: (isDirty: boolean) => void,
  onReady: () => void,
  onResourceContentRequest: (request: ResourceContentRequest) => Promise<ResourceContent | undefined>,
  onResourceListRequest: (request: ResourceListRequest) => Promise<ResourcesList>,
  onEditorUndo: () => void,
  onEditorRedo: () => void,
  onNewEdit: (edit: KogitoEdit) => void,
  onPreviewResponse: (previewSvg: string) => void
): EnvelopeBusOuterMessageHandler {
  return new EnvelopeBusOuterMessageHandler(
    {
      postMessage: msg => {
        if (iframeRef.current && iframeRef.current.contentWindow) {
          iframeRef.current.contentWindow.postMessage(msg, "*");
        }
      }
    },
    self => ({
      pollInit() {
        self.request_initResponse(window.location.origin);
      },
      receive_languageRequest() {
        self.respond_languageRequest(props.router.getLanguageData(props.file.editorType));
      },
      receive_contentResponse(content: EditorContent) {
        onContentResponse(content);
      },
      receive_contentRequest() {
        props.file
          .getFileContents()
          .then(c => self.respond_contentRequest({ content: c ?? "", path: props.file.fileName }));
      },
      receive_setContentError(errorMessage: string) {
        onSetContentError(errorMessage);
      },
      receive_dirtyIndicatorChange(isDirty: boolean) {
        onDirtyIndicatorChange(isDirty);
      },
      receive_ready() {
        onReady();
      },
      receive_resourceContentRequest(request: ResourceContentRequest) {
        onResourceContentRequest(request).then(r => self.respond_resourceContent(r!));
      },
      receive_resourceListRequest(request: ResourceListRequest) {
        onResourceListRequest(request).then(r => self.respond_resourceList(r!));
      },
      notify_editorUndo: () => {
        onEditorUndo();
      },
      notify_editorRedo: () => {
        onEditorRedo();
      },
      receive_newEdit(edit: KogitoEdit) {
        onNewEdit(edit);
      },
      receive_previewRequest(previewSvg: string) {
        onPreviewResponse(previewSvg);
      }
    }));
}

