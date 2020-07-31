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
  ChannelType,
  EditorContent,
  KogitoChannelBus,
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  StateControlCommand,
  Tutorial,
  useConnectedKogitoChannelBus,
  UserInteraction
} from "@kogito-tooling/microeditor-envelope-protocol";
import { useSyncedKeyboardEvents } from "@kogito-tooling/keyboard-shortcuts-channel";
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour";
import * as CSS from "csstype";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { File } from "../common";
import { EmbeddedEditorRouter } from "./EmbeddedEditorRouter";
import { StateControl } from "../stateControl";

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
   * Optional callback for when setting the editors content resulted in an error.
   */
  onSetContentError?: (errorMessage: string) => void;
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
   * Optional callback for when the editor signals an open file operation.
   */
  onOpenFile?: (path: string) => void;
  /**
   * Optional callback for when the editor signals a new edit.
   */
  onNewEdit?: (edit: KogitoEdit) => void;
  /**
   * Optional relative URL for the `envelope.html` used as the inner bus `IFRAME`. Defaults to `envelope/envelope.html`
   */
  envelopeUri?: string;
}

/**
 * Forward reference for the `EmbeddedEditor` to support consumers to call upon embedded operations.
 */
export type EmbeddedEditorRef = {
  /**
   * Get an instance of the StateControl
   */
  getStateControl(): StateControl;
  /**
   * Notify the editor to redo the last command and update the state control.
   */
  notifyRedo(): void;
  /**
   * Notify the editor to undo the last command and update the state control.
   */
  notifyUndo(): void;
  /**
   * Request the editor returns its current content.
   */
  requestContent(): Promise<EditorContent>;
  /**
   * Request the editor returns a preview of its current content.
   */
  requestPreview(): Promise<string>;
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
  const stateControl = useMemo(() => new StateControl(), []);

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

  const handleStateControlCommand = useCallback((stateControlCommand: StateControlCommand) => {
    switch (stateControlCommand) {
      case StateControlCommand.REDO:
        stateControl.redo();
        props.onEditorRedo?.();
        break;
      case StateControlCommand.UNDO:
        stateControl.undo();
        props.onEditorUndo?.();
        break;
      default:
        console.info(`Unknown message type received: ${stateControlCommand}`);
        break;
    }
  }, []);

  const envelopeUri = useMemo(() => props.envelopeUri ?? "envelope/envelope.html", [props.envelopeUri]);

  //Setup envelope bus communication
  const kogitoChannelBus = useMemo(() => {
    return new KogitoChannelBus(
      {
        postMessage: message => {
          if (iframeRef.current && iframeRef.current.contentWindow) {
            iframeRef.current.contentWindow.postMessage(message, "*");
          }
        }
      },
      {
        receive_setContentError(errorMessage: string) {
          props.onSetContentError?.(errorMessage);
        },
        receive_ready() {
          props.onReady?.();
        },
        receive_openFile: (path: string) => {
          props.onOpenFile?.(path);
        },
        receive_newEdit(edit: KogitoEdit) {
          stateControl.updateCommandStack(edit.id);
          props.onNewEdit?.(edit);
        },
        receive_stateControlCommandUpdate(stateControlCommand: StateControlCommand) {
          handleStateControlCommand(stateControlCommand);
        },
        receive_guidedTourUserInteraction(userInteraction: UserInteraction) {
          KogitoGuidedTour.getInstance().onUserInteraction(userInteraction);
        },
        receive_guidedTourRegisterTutorial(tutorial: Tutorial) {
          KogitoGuidedTour.getInstance().registerTutorial(tutorial);
        },
        //requests
        receive_languageRequest() {
          return Promise.resolve(props.router.getLanguageData(props.file.editorType));
        },
        receive_contentRequest() {
          return props.file.getFileContents().then(c => ({ content: c ?? "", path: props.file.fileName }));
        },
        receive_resourceContentRequest(request: ResourceContentRequest) {
          return onResourceContentRequest(request);
        },
        receive_resourceListRequest(request: ResourceListRequest) {
          return onResourceListRequest(request);
        }
      }
    );
  }, [
    props.router,
    props.file.editorType,
    props.file.fileName,
    props.onResourceContentRequest,
    props.onResourceListRequest,
    handleStateControlCommand
  ]);

  // Forward keyboard events to envelope
  useSyncedKeyboardEvents(kogitoChannelBus.client);

  //Attach/detach bus when component attaches/detaches from DOM
  useConnectedKogitoChannelBus(window.location.origin, kogitoChannelBus);

  useEffect(() => {
    KogitoGuidedTour.getInstance().registerPositionProvider((selector: string) =>
      kogitoChannelBus.request_guidedTourElementPositionResponse(selector).then(position => {
        const parentRect = iframeRef.current?.getBoundingClientRect();
        KogitoGuidedTour.getInstance().onPositionReceived(position, parentRect);
      })
    );
  }, [kogitoChannelBus]);

  //Forward reference methods
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!iframeRef.current) {
        return null;
      }

      return {
        getStateControl: () => stateControl,
        notifyRedo: () => kogitoChannelBus.notify_editorRedo(),
        notifyUndo: () => kogitoChannelBus.notify_editorUndo(),
        requestContent: () => kogitoChannelBus.request_contentResponse(),
        requestPreview: () => kogitoChannelBus.request_previewResponse(),
        setContent: async (content: string) => kogitoChannelBus.notify_contentChanged({ content: content })
      };
    },
    [kogitoChannelBus]
  );

  return (
    <iframe
      ref={iframeRef}
      id={"kogito-iframe"}
      data-testid={"kogito-iframe"}
      src={envelopeUri}
      title="Kogito editor"
      style={containerStyles}
      data-envelope-channel={props.channelType}
    />
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
