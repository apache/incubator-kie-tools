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

import {
  ChannelType,
  EditorApi,
  EditorEnvelopeLocator,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
} from "../../api";
import { useSyncedKeyboardEvents } from "@kie-tooling-core/keyboard-shortcuts/dist/channel";
import { useGuidedTourPositionProvider } from "@kie-tooling-core/guided-tour/dist/channel";
import type * as CSS from "csstype";
import * as React from "react";
import { useImperativeHandle, useMemo, useRef, useState } from "react";
import { File, StateControl } from "../../channel";
import { useEffectAfterFirstRender } from "../common";
import { KogitoEditorChannelApiImpl } from "./KogitoEditorChannelApiImpl";
import { EnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/channel";
import { useConnectedEnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/hooks";

type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;

type ChannelApiMethodsAlreadyImplementedByEmbeddedEditor =
  | "kogitoGuidedTour_guidedTourUserInteraction"
  | "kogitoGuidedTour_guidedTourRegisterTutorial"
  | "kogitoEditor_contentRequest";

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
export type EmbeddedEditorRef = EditorApi & {
  isReady: boolean;
  getStateControl(): StateControl;
  getEnvelopeServer(): EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>;
};

const containerStyles: CSS.Properties = {
  display: "flex",
  flex: 1,
  flexDirection: "column",
  width: "100%",
  height: "100%",
  border: "none",
  margin: 0,
  padding: 0,
  overflow: "hidden",
};

const RefForwardingEmbeddedEditor: React.ForwardRefRenderFunction<EmbeddedEditorRef | undefined, Props> = (
  props: Props,
  forwardedRef
) => {
  const iframeRef = useRef<HTMLIFrameElement>(null);
  const stateControl = useMemo(() => new StateControl(), [props.file.getFileContents]);
  const [isReady, setReady] = useState(false);

  const envelopeMapping = useMemo(
    () => props.editorEnvelopeLocator.mapping.get(props.file.fileExtension),
    [props.editorEnvelopeLocator, props.file]
  );

  //Setup envelope bus communication
  const kogitoEditorChannelApiImpl = useMemo(() => {
    return new KogitoEditorChannelApiImpl(stateControl, props.file, props.locale, {
      ...props,
      kogitoEditor_ready: () => {
        setReady(true);
        props.kogitoEditor_ready?.();
      },
    });
  }, [stateControl, props]);

  const envelopeServer = useMemo(() => {
    return new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
      { postMessage: (message) => iframeRef.current?.contentWindow?.postMessage(message, "*") },
      props.editorEnvelopeLocator.targetOrigin,
      (self) =>
        self.envelopeApi.requests.kogitoEditor_initRequest(
          { origin: self.origin, envelopeServerId: self.id },
          {
            fileExtension: props.file.fileExtension,
            resourcesPathPrefix: envelopeMapping?.resourcesPathPrefix ?? "",
            initialLocale: props.locale,
            isReadOnly: props.file.isReadOnly,
            channel: props.channelType,
          }
        )
    );
  }, [
    props.editorEnvelopeLocator.targetOrigin,
    props.file.fileExtension,
    props.file.isReadOnly,
    props.locale,
    props.channelType,
    envelopeMapping?.resourcesPathPrefix,
  ]);

  useConnectedEnvelopeServer(envelopeServer, kogitoEditorChannelApiImpl);

  useEffectAfterFirstRender(() => {
    envelopeServer.envelopeApi.notifications.kogitoI18n_localeChange(props.locale);
  }, [props.locale]);

  useEffectAfterFirstRender(() => {
    props.file.getFileContents().then((content) => {
      envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged({ content: content! });
    });
  }, [props.file.getFileContents]);

  // Register position provider for Guided Tour
  useGuidedTourPositionProvider(envelopeServer.envelopeApi, iframeRef);

  // Forward keyboard events to the EditorEnvelope
  useSyncedKeyboardEvents(envelopeServer.envelopeApi);

  //Forward reference methods
  useImperativeHandle(
    forwardedRef,
    () => {
      if (!iframeRef.current) {
        return undefined;
      }

      return {
        isReady: isReady,
        getStateControl: () => stateControl,
        getEnvelopeServer: () => envelopeServer,
        getElementPosition: (s) =>
          envelopeServer.envelopeApi.requests.kogitoGuidedTour_guidedTourElementPositionRequest(s),
        undo: () => Promise.resolve(envelopeServer.envelopeApi.notifications.kogitoEditor_editorUndo()),
        redo: () => Promise.resolve(envelopeServer.envelopeApi.notifications.kogitoEditor_editorRedo()),
        getContent: () => envelopeServer.envelopeApi.requests.kogitoEditor_contentRequest().then((c) => c.content),
        getPreview: () => envelopeServer.envelopeApi.requests.kogitoEditor_previewRequest(),
        setContent: (path, content) =>
          envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged({ path, content }),
        validate: () => envelopeServer.envelopeApi.requests.kogitoEditor_validate(),
      };
    },
    [envelopeServer, stateControl, isReady]
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
          key={envelopeMapping.envelopePath}
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
