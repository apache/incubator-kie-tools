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
import { useContext, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { GlobalContext } from "../common/GlobalContext";
import {
  ResourceContent,
  ResourcesList,
  ChannelType,
  ResourceContentRequest,
  KogitoEdit
} from "@kogito-tooling/core-api";

interface Props {
  editorType: string;
  onContentResponse: (content: string) => void;
  onPreviewResponse: (previewSvg: string) => void;
  onReady: () => void;
}

export type EditorRef = {
  requestContent(): void;
  requestPreview(): void;
} | null;

const RefForwardingEditor: React.RefForwardingComponent<EditorRef, Props> = (props, forwardedRef) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);

  const context = useContext(GlobalContext);

  const envelopeBusOuterMessageHandler = useMemo(() => {
    return context.envelopeBusOuterMessageHandlerFactory.createNew(iframeRef, self => ({
      pollInit() {
        self.request_initResponse(window.location.origin);
      },
      receive_languageRequest() {
        self.respond_languageRequest(context.router.getLanguageData(props.editorType)!);
      },
      receive_contentResponse(content: any) {
        props.onContentResponse(content.content);
      },
      receive_contentRequest() {
        self.respond_contentRequest({ content: context.file?.fileContent || "" });
      },
      receive_setContentError() {
        console.info("Set content error");
      },
      receive_dirtyIndicatorChange(isDirty: boolean) {
        console.info(`Dirty indicator changed to ${isDirty}`);
      },
      receive_ready() {
        console.info(`Editor is ready`);
        props.onReady();
      },
      receive_resourceContentRequest(resourceContentRequest: ResourceContentRequest) {
        console.debug(`Resource Content Request`);
        self.respond_resourceContent(new ResourceContent(resourceContentRequest.path, undefined));
      },
      receive_resourceListRequest(globPattern: string) {
        console.debug(`Resource List Request`);
        self.respond_resourceList(new ResourcesList(globPattern, []));
      },
      receive_previewRequest(previewSvg: string) {
        console.debug("received preview");
        props.onPreviewResponse(previewSvg);
      },
      notify_editorUndo: (edits: KogitoEdit[]) => {
        console.debug("Notify Undo");
      },
      notify_editorRedo: (edits: KogitoEdit[]) => {
        console.debug("Notify Redo");
      },
      receive_newEdit(edit: KogitoEdit) {
        console.debug(`New Edit: ` + edit.id);
      }
    }));
  }, []);

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
      requestContent: () => envelopeBusOuterMessageHandler.request_contentResponse(),
      requestPreview: () => envelopeBusOuterMessageHandler.request_previewResponse()
    }),
    [envelopeBusOuterMessageHandler]
  );

  return (
    <iframe ref={iframeRef} id={"kogito-iframe"} className="kogito--editor" src={context.iframeTemplateRelativePath} />
  );
};

export const Editor = React.forwardRef(RefForwardingEditor);
