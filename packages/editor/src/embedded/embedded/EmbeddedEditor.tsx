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

import { EditorApi, EditorEnvelopeLocator, KogitoEditorChannelApi } from "../../api";
import { ChannelType } from "@kogito-tooling/channel-common-api";
import { useSyncedKeyboardEvents } from "@kogito-tooling/keyboard-shortcuts/dist/channel";
import { useGuidedTourPositionProvider } from "@kogito-tooling/guided-tour/dist/channel";
import * as CSS from "csstype";
import * as React from "react";
import { useImperativeHandle, useMemo, useRef } from "react";
import { File, useEffectAfterFirstRender } from "../common";
import { StateControl } from "../stateControl";
import { KogitoEditorChannelApiImpl } from "./KogitoEditorChannelApiImpl";
import { useConnectedEnvelopeServer } from "@kogito-tooling/envelope-bus/dist/hooks";
import { KogitoEditorEnvelopeServer } from "../../channel";

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
  locale: string;
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
    return new KogitoEditorChannelApiImpl(stateControl, props.file, props.locale, props);
  }, [stateControl, props.file, props]);

  const envelopeServer = useMemo(() => {
    return new KogitoEditorEnvelopeServer(
      { postMessage: message => iframeRef.current?.contentWindow?.postMessage(message, "*") },
      props.editorEnvelopeLocator.targetOrigin,
      { fileExtension: props.file.fileExtension, resourcesPathPrefix: envelopeMapping?.resourcesPathPrefix ?? "" }
    );
  }, []);

  useConnectedEnvelopeServer(envelopeServer, kogitoEditorChannelApiImpl);

  useEffectAfterFirstRender(() => {
    envelopeServer.envelopeApi.notifications.receive_localeChange(props.locale);
  }, [props.locale]);

  // Register position provider for Guided Tour
  useGuidedTourPositionProvider(envelopeServer.envelopeApi, iframeRef);

  // Forward keyboard events to the EditorEnvelope
  useSyncedKeyboardEvents(envelopeServer.envelopeApi);

  //Forward reference methods
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!iframeRef.current) {
        return null;
      }

      return {
        getStateControl: () => stateControl,
        getElementPosition: selector =>
          envelopeServer.envelopeApi.requests.receive_guidedTourElementPositionRequest(selector),
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
