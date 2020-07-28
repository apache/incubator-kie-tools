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

import { Alert, AlertActionCloseButton, AlertVariant } from "@patternfly/react-core";
import * as electron from "electron";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import "@patternfly/patternfly/base/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.scss";
import "@patternfly/patternfly/patternfly.scss";
import "../../static/resources/style.css";
import { File } from "../common/File";
import { GlobalContext } from "./common/GlobalContext";
import { EditorPage } from "./editor/EditorPage";
import { HomePage } from "./home/HomePage";
import { EditorEnvelopeLocator } from "@kogito-tooling/editor-envelope-protocol";
import IpcRendererEvent = Electron.IpcRendererEvent;

interface Props {
  file?: File;
}

enum Pages {
  HOME,
  EDITOR
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

export function App(props: Props) {
  const [page, setPage] = useState(Pages.HOME);
  const [file, setFile] = useState(props.file);

  const [invalidFileTypeErrorVisible, setInvalidFileTypeErrorVisible] = useState(false);

  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () => ({
      targetOrigin: window.location.origin,
      mapping: new Map([
        ["bpmn", { resourcesPathPrefix: "../gwt-editors/bpmn", envelopePath: "envelope/envelope.html" }],
        ["bpmn2", { resourcesPathPrefix: "../gwt-editors/bpmn", envelopePath: "envelope/envelope.html" }],
        ["dmn", { resourcesPathPrefix: "../gwt-editors/dmn", envelopePath: "envelope/envelope.html" }],
      ])
    }),
    []
  );

  const closeInvalidFileTypeErrorAlert = useCallback(() => {
    if (page === Pages.HOME) {
      setInvalidFileTypeErrorVisible(false);
    }
  }, [page]);

  const openFile = useCallback(
    (fileToOpen: File) => {
      closeInvalidFileTypeErrorAlert();
      setFile(fileToOpen);
      setPage(Pages.EDITOR);
    },
    [page, file, closeInvalidFileTypeErrorAlert]
  );

  const openFileByPath = useCallback((filePath: string) => {
    electron.ipcRenderer.send("openFileByPath", { filePath: filePath });
  }, []);

  const goToHomePage = useCallback(() => {
    electron.ipcRenderer.send("setFileMenusEnabled", { enabled: false });
    setPage(Pages.HOME);
    setFile(undefined);
  }, [page, file]);

  const dragAndDropFileEvent = useCallback(
    ev => {
      ev.preventDefault();
      if (ev.dataTransfer) {
        openFileByPath(ev.dataTransfer.files[0].path);
      }
    },
    [openFileByPath]
  );

  const Router = () => {
    switch (page) {
      case Pages.HOME:
        return <HomePage openFile={openFile} openFileByPath={openFileByPath} />;
      case Pages.EDITOR:
        return <EditorPage fileExtension={file!.fileType} onClose={goToHomePage} />;
      default:
        return <></>;
    }
  };

  useEffect(() => {
    if (invalidFileTypeErrorVisible) {
      const autoCloseInvalidFileTypeErrorAlert = setTimeout(closeInvalidFileTypeErrorAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseInvalidFileTypeErrorAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [invalidFileTypeErrorVisible, closeInvalidFileTypeErrorAlert]);

  useEffect(() => {
    electron.ipcRenderer.on("openFile", (event: IpcRendererEvent, data: { file: File }) => {
      if (editorEnvelopeLocator.mapping.has(data.file.fileType)) {
        if (page === Pages.EDITOR) {
          setPage(Pages.HOME);
        }
        openFile(data.file);
      } else {
        setInvalidFileTypeErrorVisible(true);
      }
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("openFile");
    };
  }, [page, file]);

  useEffect(() => {
    electron.ipcRenderer.on("saveFileSuccess", (event: IpcRendererEvent, data: { filePath: string }) => {
      file!.filePath = data.filePath;
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("saveFileSuccess");
    };
  }, [file]);

  useEffect(() => {
    document.addEventListener("dragover", e => e.preventDefault());
    document.addEventListener("drop", e => e.preventDefault());
    document.body.addEventListener("drop", dragAndDropFileEvent);

    return () => {
      document.removeEventListener("dragover", e => e.preventDefault());
      document.removeEventListener("drop", e => e.preventDefault());
      document.body.removeEventListener("drop", dragAndDropFileEvent);
    };
  }, [dragAndDropFileEvent]);

  return (
    <GlobalContext.Provider
      value={{
        file: file,
        editorEnvelopeLocator: editorEnvelopeLocator
      }}
    >
      {invalidFileTypeErrorVisible && (
        <div className={"kogito--alert-container"}>
          <Alert
            variant={AlertVariant.danger}
            title="This file extension is not supported."
            actionClose={<AlertActionCloseButton onClose={closeInvalidFileTypeErrorAlert} />}
          />
        </div>
      )}
      <Router />
    </GlobalContext.Provider>
  );
}
