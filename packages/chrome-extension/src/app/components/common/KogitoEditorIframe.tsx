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
import { useContext, useEffect, useMemo, useRef } from "react";
import { IsolatedEditorContext } from "./IsolatedEditorContext";
import { EnvelopeBusOuterMessageHandler } from "@kogito-tooling/microeditor-envelope-protocol";
import { runScriptOnPage } from "../../utils";
import { GlobalContext } from "./GlobalContext";

const GITHUB_CODEMIRROR_EDITOR_SELECTOR = `.file-editor-textarea + .CodeMirror`;
const GITHUB_EDITOR_SYNC_POLLING_INTERVAL = 1500;

export function KogitoEditorIframe(props: {
  openFileExtension: string;
  getFileContents: () => Promise<string | undefined>;
  readonly: boolean;
}) {
  const { router, editorIndexPath } = useContext(GlobalContext);
  const { textMode, fullscreen, onEditorReady } = useContext(IsolatedEditorContext);
  const ref = useRef<HTMLIFrameElement | null>(null);
  const envelopeBusOuterMessageHandler = useMemo(
    () =>
      new EnvelopeBusOuterMessageHandler(
        {
          postMessage: msg => {
            if (ref.current && ref.current.contentWindow) {
              ref.current.contentWindow.postMessage(msg, router.getTargetOrigin());
            }
          }
        },
        self => ({
          pollInit() {
            self.request_initResponse(window.location.origin);
          },
          receive_languageRequest() {
            self.respond_languageRequest(router.getLanguageData(props.openFileExtension));
          },
          receive_contentResponse(content: string) {
            if (props.readonly) {
              return;
            }
            runScriptOnPage(
              `document.querySelector("${GITHUB_CODEMIRROR_EDITOR_SELECTOR}").CodeMirror.setValue('${content}')`
            );
          },
          receive_contentRequest() {
            props.getFileContents().then(c => self.respond_contentRequest(c || ""));
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
            if (onEditorReady) {
              onEditorReady();
            }
          }
        })
      ),
    [ref, router, onEditorReady]
  );

  useEffect(
    () => {
      if (textMode) {
        envelopeBusOuterMessageHandler.request_contentResponse();
        return;
      }

      if (props.readonly) {
        return;
      }

      let task: number;
      Promise.resolve()
        .then(() => props.getFileContents())
        .then(c => envelopeBusOuterMessageHandler.respond_contentRequest(c || ""))
        .then(() => {
          task = window.setInterval(
              () => envelopeBusOuterMessageHandler.request_contentResponse(),
            GITHUB_EDITOR_SYNC_POLLING_INTERVAL
          );
        });

      return () => clearInterval(task);
    },
    [textMode, envelopeBusOuterMessageHandler]
  );

  useEffect(() => {
    const listener = (msg: MessageEvent) => envelopeBusOuterMessageHandler.receive(msg.data);
    window.addEventListener("message", listener, false);
    envelopeBusOuterMessageHandler.startInitPolling();

    return () => {
      envelopeBusOuterMessageHandler.stopInitPolling();
      window.removeEventListener("message", listener);
    };
  }, []);

  return (
    <iframe
      ref={ref}
      className={`kogito-iframe ${fullscreen ? "fullscreen" : "not-fullscreen"}`}
      src={router.getRelativePathTo(editorIndexPath)}
    />
  );
}
