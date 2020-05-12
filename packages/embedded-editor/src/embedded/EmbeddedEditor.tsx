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

import { ChannelType, EditorContent, KogitoEdit, ResourceContent, ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kogito-tooling/core-api";
import * as CSS from 'csstype';
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { File } from "../common/File";
import { EmbeddedEditorRouter } from "./EmbeddedEditorRouter";
import { newEnvelopeBusOuterMessageHandler } from "./EnvelopeBusOuterMessageHandlerFactory";

export interface Props {
  file: File;
  router: EmbeddedEditorRouter;
  channelType: ChannelType;
  onContentResponse?: (content: EditorContent) => void;
  onSetContentError?: (errorMessage: string) => void;
  onDirtyIndicatorChange?: (isDirty: boolean) => void;
  onReady?: () => void;
  onResourceContentRequest?: (request: ResourceContentRequest) => Promise<ResourceContent | undefined>;
  onResourceListRequest?: (request: ResourceListRequest) => Promise<ResourcesList>;
  onEditorUndo?: () => void;
  onEditorRedo?: () => void;
  onNewEdit?: (edit: KogitoEdit) => void;
  onPreviewResponse?: (previewSvg: string) => void;
  envelopeUri?: string;
}

export type EmbeddedEditorRef = {
  requestContent(): void;
  requestPreview(): void;
  setContent(content: string): void;
} | null;

const containerStyles: CSS.Properties = {
  display: "flex",
  flex: 1,
  flexDirection: "column",
  width: "100%",
  height: "100%",
  border: "none",
  margin: 0,
  padding: 0,
  overflow: "hidden"
};

const RefForwardingEmbeddedEditor: React.RefForwardingComponent<EmbeddedEditorRef, Props> = (props: Props, forwardedRef) => {
  const iframeRef: React.RefObject<HTMLIFrameElement> = useRef<HTMLIFrameElement>(null);

  //Property functions default handling
  const onContentResponse = useCallback((content: EditorContent) => {
    if (props.onContentResponse) {
      props.onContentResponse(content);
    }
  }, [props.onContentResponse]);

  const onSetContentError = useCallback((errorMessage: string) => {
    if (props.onSetContentError) {
      props.onSetContentError(errorMessage);
    }
  }, [props.onSetContentError]);

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
    return Promise.resolve(new ResourceContent(request.path, undefined));
  }, [props.onResourceContentRequest]);

  const onResourceListRequest = useCallback((request: ResourceListRequest) => {
    if (props.onResourceListRequest) {
      return props.onResourceListRequest(request);
    }
    return Promise.resolve(new ResourcesList(request.pattern, []));
  }, [props.onResourceListRequest]);

  const onEditorUndo = useCallback(() => {
    if (props.onEditorUndo) {
      props.onEditorUndo();
    }
  }, [props.onEditorUndo]);

  const onEditorRedo = useCallback(() => {
    if (props.onEditorRedo) {
      props.onEditorRedo();
    }
  }, [props.onEditorRedo]);

  const onNewEdit = useCallback((edit: KogitoEdit) => {
    if (props.onNewEdit) {
      props.onNewEdit(edit);
    }
  }, [props.onNewEdit]);

  const onPreviewRequest = useCallback((previewSvg: string) => {
    if (props.onPreviewResponse) {
      props.onPreviewResponse(previewSvg);
    }
  }, [props.onPreviewResponse]);

  const envelopeUri = useMemo(() => props.envelopeUri ?? "envelope/envelope.html", [props.envelopeUri]);

  //Setup envelope bus communication
  const envelopeBusOuterMessageHandler = useMemo(
    () => {
      return newEnvelopeBusOuterMessageHandler(props,
        iframeRef,
        onContentResponse,
        onSetContentError,
        onDirtyIndicatorChange,
        onReady,
        onResourceContentRequest,
        onResourceListRequest,
        onEditorUndo,
        onEditorRedo,
        onNewEdit,
        onPreviewRequest)
    },
    [props.router, props.file.editorType, props.file.fileName,
    props.onContentResponse, props.onSetContentError, props.onDirtyIndicatorChange,
    props.onReady, props.onResourceContentRequest, props.onResourceListRequest, props.onEditorUndo,
    props.onEditorRedo, props.onNewEdit, props.onPreviewResponse]
  );

  //Attach/detach bus when component attaches/detaches from DOM
  useEffect(() => {
    const listener = (msg: MessageEvent) => envelopeBusOuterMessageHandler.receive(msg.data);
    window.addEventListener("message", listener, false);
    envelopeBusOuterMessageHandler.startInitPolling();

    return () => {
      envelopeBusOuterMessageHandler.stopInitPolling();
      window.removeEventListener("message", listener);
    };
  }, [envelopeBusOuterMessageHandler]);

  //Forward reference methods
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!iframeRef.current) {
        return null;
      }

      return {
        requestContent: () => envelopeBusOuterMessageHandler.request_contentResponse(),
        requestPreview: () => envelopeBusOuterMessageHandler.request_previewResponse(),
        setContent: (content: string) => {
          envelopeBusOuterMessageHandler.respond_contentRequest({ content: content });
          return Promise.resolve();
        }
      }
    },
    [envelopeBusOuterMessageHandler]
  );

  return (
    <iframe
      ref={iframeRef}
      id={"kogito-iframe"}
      src={envelopeUri}
      title="Kogito editor"
      style={containerStyles}
      data-envelope-channel={props.channelType} />
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
