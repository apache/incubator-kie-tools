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

import { findContainers, getGitHubEditor } from "./utils";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { useEffect, useRef, useState } from "react";
import { EnvelopeBusOuterMessageHandler } from "appformer-js-microeditor-envelope-protocol";
import { Router } from "appformer-js-core/src";

export function ChromeExtensionApp(props: { openFileExtension: string; githubEditor: HTMLElement; router: Router }) {
  const containers = findContainers();
  return (
    <>
      {ReactDOM.createPortal(<ToolbarButtons />, containers.fullScreenButton)}
      {ReactDOM.createPortal(
        <KogitoEditorIframe
          openFileExtension={props.openFileExtension}
          router={props.router}
          githubEditor={props.githubEditor}
        />,
        containers.iframe
      )}
    </>
  );
}

function KogitoEditorIframe(props: { openFileExtension: string; githubEditor: HTMLElement; router: Router }) {
  const iframeRef = useRef<HTMLIFrameElement | null>(null);
  const targetOrigin = chrome.extension.getURL("");

  const envelopeBusOuterMessageHandler = new EnvelopeBusOuterMessageHandler(
    {
      postMessage: msg => {
        if (iframeRef.current && iframeRef.current.contentWindow) {
          iframeRef.current.contentWindow.postMessage(msg, targetOrigin);
        }
      }
    },
    self => ({
      pollInit: () => {
        self.request_initResponse(window.location.origin);
      },
      receive_languageRequest: () => {
        self.respond_languageRequest(props.router.getLanguageData(props.openFileExtension));
      },
      receive_contentResponse: (content: string) => {
        // enableCommitButton();
        getGitHubEditor().CodeMirror.setValue(content);
      },
      receive_contentRequest: () => {
        const githubEditorContent = getGitHubEditor().CodeMirror.getValue() || "";
        self.respond_contentRequest(githubEditorContent);
      },
      receive_dirtyIndicatorChange(isDirty: boolean) {
        console.info(`Dirty indicator changed to ${isDirty}`);
      }
    })
  );

  useEffect(() => {
    props.githubEditor.style.display = "none";
    return () => {
      delete props.githubEditor.style.display;
    };
  }, []);

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
    <>
      <iframe
        ref={iframeRef}
        id={"kogito-iframe"}
        src={chrome.extension.getURL("envelope/index.html")}
        style={{
          marginTop: " 10px",
          marginBottom: " 10px",
          width: "100%",
          height: "600px",
          borderRadius: "4px",
          border: "1px solid lightgray"
        }}
      />
    </>
  );
}

function ToolbarButtons() {
  const [textMode, setTextMode] = useState(false);

  const goFullScreen = (e: any) => {
    e.preventDefault();
    console.info("Went full screen");
  };

  const seeAsText = (e: any) => {
    e.preventDefault();
    setTextMode(true);
  };

  const seeAsDiagram = (e: any) => {
    e.preventDefault();
    setTextMode(false);
  };

  return (
    <>
      {!textMode && (
        <button className={"btn btn-sm"} style={{ marginLeft: "4px", float: "right" }} onClick={goFullScreen}>
          Fullscreen
        </button>
      )}
      {!textMode && (
        <button className={"btn btn-sm"} style={{ marginLeft: "4px", float: "right" }} onClick={seeAsText}>
          See as text
        </button>
      )}
      {textMode && (
        <button className={"btn btn-sm"} style={{ marginLeft: "4px", float: "right" }} onClick={seeAsDiagram}>
          See as diagram
        </button>
      )}
    </>
  );
}
