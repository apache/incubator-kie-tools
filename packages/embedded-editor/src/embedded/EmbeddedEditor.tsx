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
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { File } from "../common/File";
import "./AugmentedIFrame";
import { EmbeddedEditorRouter } from "./EmbeddedEditorRouter";

interface Props {
  file: File;
  router: EmbeddedEditorRouter;
  channelType: ChannelType;
  onContentResponse?: (content: EditorContent) => void;
  onSetContentError?: () => void;
  onDirtyIndicatorChange?: (isDirty: boolean) => void;
  onReady?: () => void;
  onResourceContentRequest?: (request: ResourceContentRequest) => Promise<ResourceContent | undefined>;
  onResourceListRequest?: (request: ResourceListRequest) => Promise<ResourcesList>;
  onEditorUndo?: (edits: ReadonlyArray<KogitoEdit>) => void;
  onEditorRedo?: (edits: ReadonlyArray<KogitoEdit>) => void;
  onNewEdit?: (edit: KogitoEdit) => void;
  onPreviewResponse?: (previewSvg: string) => void;
  envelopeUri?: string;
}

export type EmbeddedEditorRef = {
  requestContent(): void;
  requestPreview(): void;
  setContent(content: string): void;
} | null;

const RefForwardingEmbeddedEditor: React.RefForwardingComponent<EmbeddedEditorRef, Props> = (props: Props, forwardedRef) => {
  const iframeRef: React.RefObject<HTMLIFrameElement> = useRef<HTMLIFrameElement>(null);

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
    return Promise.resolve(new ResourceContent(request.path, undefined));
  }, [props.onResourceContentRequest]);

  const onResourceListRequest = useCallback((request: ResourceListRequest) => {
    if (props.onResourceListRequest) {
      return props.onResourceListRequest(request);
    }
    return Promise.resolve(new ResourcesList(request.pattern, []));
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
    if (props.onPreviewResponse) {
      props.onPreviewResponse(previewSvg);
    }
  }, [props.onPreviewResponse]);

  const envelopeUri = props.envelopeUri ?? "envelope/envelope.html";

  //Setup envelope bus communication
  const envelopeBusOuterMessageHandler = useMemo(() => {
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
        receive_setContentError() {
          onSetContentError();
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
        notify_editorUndo: (edits: ReadonlyArray<KogitoEdit>) => {
          onEditorUndo(edits);
        },
        notify_editorRedo: (edits: ReadonlyArray<KogitoEdit>) => {
          onEditorRedo(edits);
        },
        receive_newEdit(edit: KogitoEdit) {
          onNewEdit(edit);
        },
        receive_previewRequest(previewSvg: string) {
          onPreviewRequest(previewSvg);
        }
      }));
  }, [props]);

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
    <div className="kogito--editor--container">
      <iframe
        ref={iframeRef}
        id={"kogito-iframe"}
        src={envelopeUri}
        title="Kogito editor"
        dataenvelopechannel={props.channelType} />
    </div>
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
