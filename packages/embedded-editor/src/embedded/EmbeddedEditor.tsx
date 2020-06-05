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

import {
  ChannelStateControlEvent,
  ChannelType,
  EditorContent,
  getOperatingSystem,
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList
} from "@kogito-tooling/core-api";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import * as CSS from "csstype";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { File } from "../common/File";
import { EmbeddedEditorRouter } from "./EmbeddedEditorRouter";
import { EditorStateControl } from "../stateControl";
import { DefaultKeyboardShortcutsService, redoShortcut, undoShortcut } from "@kogito-tooling/microeditor-envelope";

/**
 * Properties supported by the `EmbeddedEditor`.
 */
export interface Props {
  /**
   * File to show in the editor.
   */
  file: File;
  /**
   * Router to map editor URLs to installations.
   */
  router: EmbeddedEditorRouter;
  /**
   * Channel in which the editor has been embedded.
   */
  channelType: ChannelType;
  /**
   * Optional callback for when the editors' content is returned followin a response for it.
   */
  onContentResponse?: (content: EditorContent) => void;
  /**
   * Optional callback for when setting the editors content resulted in an error.
   */
  onSetContentError?: (errorMessage: string) => void;
  /**
   * Optional callback for when the editor signals its content is changed.
   */
  onDirtyIndicatorChange?: (isDirty: boolean) => void;
  /**
   * Optional callback for when the editor has initialised and is considered ready.
   */
  onReady?: () => void;
  /**
   * Optional callback for when the editor is requesting external content.
   */
  onResourceContentRequest?: (request: ResourceContentRequest) => Promise<ResourceContent | undefined>;
  /**
   * Optional callback for when the editor is requesting a list of external content.
   */
  onResourceListRequest?: (request: ResourceListRequest) => Promise<ResourcesList>;
  /**
   * Optional callback for when the editor signals an _undo_ operation.
   */
  onEditorUndo?: () => void;
  /**
   * Optional callback for when the editor signals an _redo_ operation.
   */
  onEditorRedo?: () => void;
  /**
   * Optional callback for when the editor signals a new edit.
   */
  onNewEdit?: (edit: KogitoEdit) => void;
  /**
   * Optional callback for when a preview of editors' content is returned followin a response for it.
   */
  onPreviewResponse?: (previewSvg: string) => void;
  /**
   * Optional relative URL for the `envelope.html` used as the inner bus `IFRAME`. Defaults to `envelope/envelope.html`
   */
  envelopeUri?: string;
  editorStateControl?: EditorStateControl;
}

/**
 * Forward reference for the `EmbeddedEditor` to support consumers to call upon embedded operations.
 */
export type EmbeddedEditorRef = {
  notifyRedo(): void;
  notifyUndo(): void;
  /**
   * Request the editor returns its current content.
   */
  requestContent(): void;
  /**
   * Request the editor returns a preview of its current content.
   */
  requestPreview(): void;
  /**
   * Request to set the content of the editor; this will overwrite the content supplied by the `File.getFileContents()` passed in construction.
   * @param content
   */
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

const RefForwardingEmbeddedEditor: React.RefForwardingComponent<EmbeddedEditorRef, Props> = (
  props: Props,
  forwardedRef
) => {
  const iframeRef: React.RefObject<HTMLIFrameElement> = useRef<HTMLIFrameElement>(null);
  const keyboardShortcuts = useMemo(() => {
    return new DefaultKeyboardShortcutsService({ channel: props.channelType, operatingSystem: getOperatingSystem() });
  }, []);

  //Property functions default handling
  const onResourceContentRequest = useCallback(
    (request: ResourceContentRequest) => {
      if (props.onResourceContentRequest) {
        return props.onResourceContentRequest(request);
      }
      return Promise.resolve(new ResourceContent(request.path, undefined));
    },
    [props.onResourceContentRequest]
  );

  const onResourceListRequest = useCallback(
    (request: ResourceListRequest) => {
      if (props.onResourceListRequest) {
        return props.onResourceListRequest(request);
      }
      return Promise.resolve(new ResourcesList(request.pattern, []));
    },
    [props.onResourceListRequest]
  );

  const envelopeUri = useMemo(() => props.envelopeUri ?? "envelope/envelope.html", [props.envelopeUri]);

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
          props.onContentResponse?.(content);
        },
        receive_contentRequest() {
          props.file
            .getFileContents()
            .then(c => self.respond_contentRequest({ content: c ?? "", path: props.file.fileName }));
        },
        receive_setContentError(errorMessage: string) {
          props.onSetContentError?.(errorMessage);
        },
        receive_dirtyIndicatorChange(isDirty: boolean) {
          props.onDirtyIndicatorChange?.(isDirty);
        },
        receive_ready() {
          // registerShortcuts();
          props.onReady?.();
        },
        receive_resourceContentRequest(request: ResourceContentRequest) {
          onResourceContentRequest(request).then(r => self.respond_resourceContent(r!));
        },
        receive_resourceListRequest(request: ResourceListRequest) {
          onResourceListRequest(request).then(r => self.respond_resourceList(r!));
        },
        notify_editorUndo: () => {
          props.editorStateControl?.undoEvent();
          props.onEditorUndo?.();
        },
        notify_editorRedo: () => {
          props.editorStateControl?.redoEvent();
          props.onEditorRedo?.();
        },
        receive_newEdit(edit: KogitoEdit) {
          props.editorStateControl?.updateEventStack(edit.id);
          props.onNewEdit?.(edit);
        },
        receive_previewRequest(previewSvg: string) {
          props.onPreviewResponse?.(previewSvg);
        },
        notify_channelStateControl(stateControlEvent: ChannelStateControlEvent) {
          handleStateControlNotification(stateControlEvent);
        }
      })
    );
  }, [
    props.router,
    props.file.editorType,
    props.file.fileName,
    props.onResourceContentRequest,
    props.onResourceListRequest
  ]);

  const handleStateControlNotification = useCallback((stateControlEvent: ChannelStateControlEvent) => {
    switch (stateControlEvent) {
      case ChannelStateControlEvent.REDO:
        props.editorStateControl?.redoEvent();
        break;
      case ChannelStateControlEvent.UNDO:
        props.editorStateControl?.undoEvent();
        break;
      case ChannelStateControlEvent.SAVE:
        props.editorStateControl?.setSavedEvent();
        break;
    }
  }, []);

    useEffect(() => {
      const { combination, label, onKeyPress, opts } = undoShortcut(async () =>
        envelopeBusOuterMessageHandler.notify_editorUndo()
      );
      const undoId = keyboardShortcuts.registerKeyPress(combination, label, onKeyPress, opts);
      return () => keyboardShortcuts.deregister(undoId);
    }, []);

    useEffect(() => {
      const { combination, label, onKeyPress, opts } = redoShortcut(async () =>
        envelopeBusOuterMessageHandler.notify_editorUndo()
      );
      const redoId = keyboardShortcuts.registerKeyPress(combination, label, onKeyPress, opts);
      return () => keyboardShortcuts.deregister(redoId);
    }, []);

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
        notifyRedo: () => envelopeBusOuterMessageHandler.notify_editorRedo(),
        notifyUndo: () => envelopeBusOuterMessageHandler.notify_editorUndo(),
        requestContent: () => envelopeBusOuterMessageHandler.request_contentResponse(),
        requestPreview: () => envelopeBusOuterMessageHandler.request_previewResponse(),
        setContent: (content: string) => {
          envelopeBusOuterMessageHandler.respond_contentRequest({ content: content });
          return Promise.resolve();
        }
      };
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
      data-envelope-channel={props.channelType}
    />
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
