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
import { useRef } from "react";
import { useGlobals } from "../common/GlobalContext";
import { useState } from "react";
import { useCallback } from "react";
import { useEffect } from "react";

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

export function SingleEditorToolbar(props: {
  readonly: boolean;
  textMode: boolean;
  textModeEnabled: boolean;
  onFullScreen: () => void;
  onSeeAsSource: () => void;
  onSeeAsDiagram: () => void;
  onOpenInExternalEditor: () => void;
  linkToExternalEditor: string | undefined;
}) {
  const globals = useGlobals();
  const [copyLinkSuccessAlertVisible, setCopyLinkSuccessAlertVisible] = useState(false);
  const linkToExternalEditorTextAreaRef = useRef<HTMLTextAreaElement>(null);
  const copyLinkSuccessAlertRef = useRef<HTMLDivElement>(null);

  const goFullScreen = (e: any) => {
    e.preventDefault();
    props.onFullScreen();
  };

  const seeAsSource = (e: any) => {
    e.preventDefault();
    props.onSeeAsSource();
  };

  const seeAsDiagram = (e: any) => {
    e.preventDefault();
    props.onSeeAsDiagram();
  };

  const openInExternalEditor = (e: any) => {
    e.preventDefault();
    props.onOpenInExternalEditor();
  };

  const copyLinkToExternalEditor = (e: any) => {
    e.preventDefault();
    linkToExternalEditorTextAreaRef.current?.select();
    if (document.execCommand("copy")) {
      setCopyLinkSuccessAlertVisible(true);
    }
    e.target.focus();
  };

  const closeCopyLinkSuccessAlert = useCallback(() => setCopyLinkSuccessAlertVisible(false), []);

  useEffect(() => {
    if (closeCopyLinkSuccessAlert) {
      const autoCloseCopyLinkSuccessAlert = setTimeout(closeCopyLinkSuccessAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseCopyLinkSuccessAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [copyLinkSuccessAlertVisible]);

  return (
    <>
      <div style={{ display: "flex" }}>
        {!props.textMode && (
          <button disabled={!props.textModeEnabled} className={"btn btn-sm kogito-button"} onClick={seeAsSource}>
            See as source
          </button>
        )}
        {props.textMode && (
          <button className={"btn btn-sm kogito-button"} onClick={seeAsDiagram}>
            See as diagram
          </button>
        )}
        {globals.externalEditorManager && (
          <button className={"btn btn-sm kogito-button"} onClick={openInExternalEditor}>
            Open in {globals.externalEditorManager.name}
          </button>
        )}
        {globals.externalEditorManager && props.linkToExternalEditor && (
          <div className={"position-relative"}>
            <button className={"btn btn-sm kogito-button"} onClick={copyLinkToExternalEditor}>
              Copy link to {globals.externalEditorManager.name}
            </button>
            {copyLinkSuccessAlertVisible && (
              <div
                ref={copyLinkSuccessAlertRef}
                className={"position-absolute"}
                style={{ marginTop: "34px", right: "0" }}
              >
                <div className={"dropdown-menu dropdown-menu-sw kogito-github-action-alert"}>
                  <span>Link copied to clipboard</span>
                </div>
              </div>
            )}
          </div>
        )}
        {!props.textMode && (
          <button className={"btn btn-sm kogito-button"} onClick={goFullScreen}>
            Full screen
          </button>
        )}
        <textarea
          ref={linkToExternalEditorTextAreaRef}
          defaultValue={props.linkToExternalEditor}
          style={{ opacity: 0, width: 0, height: 0 }}
        />
      </div>
      {props.readonly && !props.textMode && (
        <>
          {/* TODO: Add "info" icon with hint explaining how to edit the file */}
          <h4>🔸️ This is a readonly visualization</h4>
        </>
      )}
    </>
  );
}
