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
  EditorEnvelopeLocator,
  KogitoEditorChannelApi,
  KogitoEditorChannelEnvelopeServer,
  useConnectedEditorEnvelopeServer
} from "@kogito-tooling/editor-envelope-protocol";
import { ChannelType } from "@kogito-tooling/channel-common-api";
import { useSyncedKeyboardEvents } from "@kogito-tooling/keyboard-shortcuts/dist/channel";
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour";
import * as CSS from "csstype";
import * as React from "react";
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import { File } from "../common";
import { StateControl } from "../stateControl";
import { EditorApi } from "@kogito-tooling/editor-api";
import { KogitoEditorChannelApiImpl } from "./KogitoEditorChannelApiImpl";

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

type ChannelApiMethodsAlreadyImplementedByEmbeddedEditor =
  | "receive_guidedTourUserInteraction"
  | "receive_guidedTourRegisterTutorial"
  | "receive_contentRequest";

type EmbeddedEditorChannelApiOverrides = Partial<
  Omit<KogitoEditorChannelApi, ChannelApiMethodsAlreadyImplementedByEmbeddedEditor>
>;

export type Props = EmbeddedEditorChannelApiOverrides & {
  file: File;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  channelType: ChannelType;
};

/**
 * Forward reference for the `EmbeddedEditor` to support consumers to call upon embedded operations.
 */
export type EmbeddedEditorRef = (EditorApi & { getStateControl(): StateControl }) | null;

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
  const iframeRef = useRef<HTMLIFrameElement>(null);
  const stateControl = useMemo(() => new StateControl(), []);

  const envelopeMapping = useMemo(() => props.editorEnvelopeLocator.mapping.get(props.file.fileExtension), [
    props.editorEnvelopeLocator,
    props.file
  ]);

  //Setup envelope bus communication
  const kogitoEditorChannelApiImpl = useMemo(() => {
    return new KogitoEditorChannelApiImpl(stateControl, props.file, props);
  }, [stateControl, props.file, props]);

  const envelopeServer = useMemo(() => {
    return new KogitoEditorChannelEnvelopeServer({
      postMessage: message => {
        iframeRef.current?.contentWindow?.postMessage(message, "*");
      }
    });
  }, []);

  useConnectedEditorEnvelopeServer(
    envelopeServer,
    kogitoEditorChannelApiImpl,
    props.editorEnvelopeLocator.targetOrigin,
    { fileExtension: props.file.fileExtension, resourcesPathPrefix: envelopeMapping?.resourcesPathPrefix ?? "" }
  );

  useEffect(() => {
    KogitoGuidedTour.getInstance().registerPositionProvider((selector: string) =>
      envelopeServer.request_guidedTourElementPositionResponse(selector).then(position => {
        const parentRect = iframeRef.current?.getBoundingClientRect();
        KogitoGuidedTour.getInstance().onPositionReceived(position, parentRect);
      })
    );
  }, [envelopeServer]);

  // Forward keyboard events to envelope
  useSyncedKeyboardEvents(envelopeServer.client);

  //Forward reference methods
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!iframeRef.current) {
        return null;
      }

      return {
        getStateControl: () => stateControl,
        getElementPosition: selector => envelopeServer.request_guidedTourElementPositionResponse(selector),
        redo: () => Promise.resolve(envelopeServer.notify_editorRedo()),
        undo: () => Promise.resolve(envelopeServer.notify_editorUndo()),
        getContent: () => envelopeServer.request_contentResponse().then(c => c.content),
        getPreview: () => envelopeServer.request_previewResponse(),
        setContent: async content => envelopeServer.notify_contentChanged({ content: content })
      };
    },
    [envelopeServer]
  );

  return (
    <>
      {!envelopeMapping && (
        <>
          <span>{`No Editor available for '${props.file.fileExtension}' extension`}</span>
        </>
      )}
      {envelopeMapping && (
        <iframe
          ref={iframeRef}
          id={"kogito-iframe"}
          data-testid={"kogito-iframe"}
          src={envelopeMapping.envelopePath}
          title="Kogito editor"
          style={containerStyles}
          data-envelope-channel={props.channelType}
        />
      )}
    </>
  );
};

export const EmbeddedEditor = React.forwardRef(RefForwardingEmbeddedEditor);
