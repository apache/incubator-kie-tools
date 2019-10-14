/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import { useContext, useEffect, useRef, useState } from "react";
import { IsolatedEditorContext, IsolatedEditorContextType } from "./IsolatedEditorContext";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { runScriptOnPage } from "../../utils";
import { GlobalContext } from "./GlobalContext";

const githubCodeMirrorEditorSelector = `.file-editor-textarea + .CodeMirror`;
const GITHUB_EDITOR_SYNC_POLLING_INTERVAL = 1500;

export function KogitoEditorIframe(props: {
  openFileExtension: string;
  getFileContents: () => Promise<string | undefined>;
  readonly: boolean;
}) {
  const globalContext = useContext(GlobalContext);
  const isolatedEditorContext = useContext(IsolatedEditorContext);
  const ref = useRef<HTMLIFrameElement | null>(null);
  const [polling, setPolling] = useState<number | undefined>();
  const [prevIsolatedEditorContext, setPrevIsolatedEditorContext] = useState<IsolatedEditorContextType | undefined>();

  const envelopeBusOuterMessageHandler = new EnvelopeBusOuterMessageHandler(
    {
      postMessage: msg => {
        if (ref.current && ref.current.contentWindow) {
          ref.current.contentWindow.postMessage(msg, globalContext.router.getTargetOrigin());
        }
      }
    },
    self => ({
      pollInit() {
        self.request_initResponse(window.location.origin);
      },
      receive_languageRequest() {
        self.respond_languageRequest(globalContext.router.getLanguageData(props.openFileExtension));
      },
      receive_contentResponse(content: string) {
        if (props.readonly) {
          return;
        }
        runScriptOnPage(
          `document.querySelector("${githubCodeMirrorEditorSelector}").CodeMirror.setValue('${content}')`
        );
      },
      receive_contentRequest() {
        props
          .getFileContents()
          .then(c => self.respond_contentRequest(c || ""))
          .then(() => startPollingForChangesOnDiagram());
      },
      receive_setContentError() {
        //TODO: Display a nice message with explanation why "setContent" failed
        console.info("Set content error");
      },
      receive_dirtyIndicatorChange(isDirty: boolean) {
        //TODO: Perhaps show window.alert to warn that the changes were not saved?
        console.info(`Dirty indicator changed to ${isDirty}`);
      },
      receive_ready() {
        console.info(`Editor is ready`);
        if (isolatedEditorContext.onEditorReady) {
          isolatedEditorContext.onEditorReady();
        }
      }
    })
  );

  function startPollingForChangesOnDiagram() {
    if (props.readonly) {
      return;
    }

    if (polling) {
      return;
    }

    setPolling(
      window.setInterval(
        () => envelopeBusOuterMessageHandler.request_contentResponse(),
        GITHUB_EDITOR_SYNC_POLLING_INTERVAL
      )
    );
  }

  function stopPollingForChangesOnDiagram() {
    if (props.readonly) {
      return;
    }

    clearInterval(polling);
    setPolling(undefined);
  }

  useEffect(
    () => {
      console.info("OI");
      if (isolatedEditorContext.textMode) {
        stopPollingForChangesOnDiagram();
        envelopeBusOuterMessageHandler.request_contentResponse();
      } else if (prevIsolatedEditorContext && prevIsolatedEditorContext.textMode !== isolatedEditorContext.textMode) {
        props
          .getFileContents()
          .then(c => envelopeBusOuterMessageHandler.respond_contentRequest(c || ""))
          .then(startPollingForChangesOnDiagram);
      }

      setPrevIsolatedEditorContext(isolatedEditorContext);
    },
    [isolatedEditorContext]
  );

  useEffect(() => {
    const listener = (msg: MessageEvent) => envelopeBusOuterMessageHandler.receive(msg.data);
    window.addEventListener("message", listener, false);
    envelopeBusOuterMessageHandler.startInitPolling();

    return () => {
      envelopeBusOuterMessageHandler.stopInitPolling();
      window.removeEventListener("message", listener);
      stopPollingForChangesOnDiagram();
    };
  }, []);

  return (
    <iframe
      ref={ref}
      className={`kogito-iframe ${isolatedEditorContext.fullscreen ? "fullscreen" : "not-fullscreen"}`}
      src={globalContext.router.getRelativePathTo(globalContext.editorIndexPath)}
    />
  );
}
