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

import { ChromeAppContainers, getGitHubEditor } from "./utils";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { useContext, useEffect, useLayoutEffect, useRef, useState } from "react";
import { EnvelopeBusOuterMessageHandler } from "appformer-js-microeditor-envelope-protocol";
import { Router } from "appformer-js-core/src";

interface GlobalContextType {
  fullscreen: boolean;
  textMode: boolean;
  textModeEnabled: boolean;
}

const GlobalContext = React.createContext<
  [GlobalContextType, <K extends keyof GlobalContextType>(g: Pick<GlobalContextType, K>) => void]
>([
  {
    fullscreen: false,
    textMode: false,
    textModeEnabled: false
  },
  <K extends keyof GlobalContextType>(g: Pick<GlobalContextType, K>) => {
    /**/
  }
]);

export function ChromeExtensionApp(props: {
  containers: ChromeAppContainers;
  openFileExtension: string;
  githubEditor: HTMLElement;
  router: Router;
}) {
  const [globalState, setGlobalState] = useState({ fullscreen: false, textMode: false, textModeEnabled: false });

  useLayoutEffect(
    () => {
      if (!globalState.fullscreen) {
        props.containers.iframeFullscreen.style.display = "none";
      } else {
        props.containers.iframeFullscreen.style.display = "block";
      }
    },
    [globalState]
  );

  useEffect(
    () => {
      if (globalState.textMode) {
        props.githubEditor.style.display = "block";
        props.containers.iframe.style.display = "none";
      } else {
        props.githubEditor.style.display = "none";
        props.containers.iframe.style.display = "block";
      }
    },
    [globalState]
  );

  return (
    <GlobalContext.Provider value={[globalState, setGlobalState]}>
      {ReactDOM.createPortal(<ToolbarButtons />, props.containers.fullScreenButton)}
      {globalState.fullscreen && ReactDOM.createPortal(<FullScreenButtonsBar />, props.containers.iframeFullscreen)}
      {ReactDOM.createPortal(
        <KogitoEditorIframe
          openFileExtension={props.openFileExtension}
          router={props.router}
          githubEditor={props.githubEditor}
        />,
        globalState.fullscreen ? props.containers.iframeFullscreen : props.containers.iframe
      )}
    </GlobalContext.Provider>
  );
}

function FullScreenButtonsBar() {
  const [globalState, setGlobalState] = useContext(GlobalContext);

  const exitFullScreen = () => {
    setGlobalState({ ...globalState, fullscreen: false, textModeEnabled: false });
  };

  return (
    <div
      style={{
        borderRadius: "0 0 10px 10px",
        backgroundColor: "black",
        width: "150px",
        height: "25px",
        position: "absolute",
        left: 0,
        right: 0,
        margin: "0 auto",
        zIndex: 1000,
        boxShadow: "0px 0px 10px 1px black",
        textAlign: "center"
      }}
    >
      <a href={"#"} style={{ color: "#e2e2e2" }} onClick={exitFullScreen}>
        Exit full screen
      </a>
    </div>
  );
}

function KogitoEditorIframe(props: { openFileExtension: string; githubEditor: HTMLElement; router: Router }) {
  const iframeRef = useRef<HTMLIFrameElement | null>(null);
  const targetOrigin = chrome.extension.getURL("");

  const [globalState, setGlobalState] = useContext(GlobalContext);

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
        // const githubEditorContent = getGitHubEditor().CodeMirror.getValue() || "";
        self.respond_contentRequest("");
      },
      receive_dirtyIndicatorChange(isDirty: boolean) {
        console.info(`Dirty indicator changed to ${isDirty}`);
      },
      receive_ready(): void {
        console.info(`Editor is ready`);
        setGlobalState({ ...globalState, textModeEnabled: true });
      }
    })
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
    <>
      <iframe
        ref={iframeRef}
        id={"kogito-iframe"}
        src={chrome.extension.getURL("envelope/index.html")}
        style={{
          width: globalState.fullscreen ? "100vw" : "100%",
          height: globalState.fullscreen ? "100vh" : "600px",
          border: globalState.fullscreen ? "0" : "1px solid lightgray",
          borderRadius: "4px"
        }}
      />
    </>
  );
}

function ToolbarButtons() {
  const [globalState, setGlobalState] = useContext(GlobalContext);

  const goFullScreen = (e: any) => {
    e.preventDefault();
    setGlobalState({ ...globalState, fullscreen: true });
  };

  const seeAsText = (e: any) => {
    e.preventDefault();
    setGlobalState({ ...globalState, textMode: true });
  };

  const seeAsKogito = (e: any) => {
    e.preventDefault();
    setGlobalState({ ...globalState, textMode: false });
  };

  return (
    <>
      {!globalState.textMode && (
        <button className={"btn btn-sm"} style={{ marginLeft: "4px", float: "right" }} onClick={goFullScreen}>
          Fullscreen
        </button>
      )}
      {!globalState.textMode && (
        <button
          disabled={!globalState.textModeEnabled}
          className={"btn btn-sm"}
          style={{ marginLeft: "4px", float: "right" }}
          onClick={seeAsText}
        >
          See as text
        </button>
      )}
      {globalState.textMode && (
        <button className={"btn btn-sm"} style={{ marginLeft: "4px", float: "right" }} onClick={seeAsKogito}>
          See as custom editor
        </button>
      )}
    </>
  );
}
