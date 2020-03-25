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

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { HomePage } from "./home/HomePage";
import { EditorPage } from "./editor/EditorPage";
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";
import { GlobalContext } from "./common/GlobalContext";
import { EnvelopeBusOuterMessageHandlerFactory } from "./editor/EnvelopeBusOuterMessageHandlerFactory";
import "@patternfly/patternfly/patternfly-variables.css";
import "@patternfly/patternfly/patternfly-addons.css";
import "@patternfly/patternfly/patternfly.css";
import "../../static/resources/style.css";
import { File } from "../common/File";
import { DesktopRouter } from "./common/DesktopRouter";
import * as electron from "electron";
import { FileActions } from "./common/FileActions";
import { Alert, AlertActionCloseButton, AlertVariant } from "@patternfly/react-core";

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

  const envelopeBusOuterMessageHandlerFactory = useMemo(() => new EnvelopeBusOuterMessageHandlerFactory(), []);

  const desktopRouter = useMemo(
    () =>
      new DesktopRouter(
        new GwtEditorRoutes({
          bpmnPath: "editors/bpmn",
          dmnPath: "editors/dmn"
        })
      ),
    []
  );

  const ipc = useMemo(() => electron.ipcRenderer, [electron.ipcRenderer]);

  const fileActions = useMemo(() => new FileActions(ipc), [ipc]);

  const Router = () => {
    switch (page) {
      case Pages.HOME:
        return <HomePage openFile={openFile} openFileByPath={openFileByPath} />;
      case Pages.EDITOR:
        return <EditorPage editorType={file!.fileType} onHome={goToHomePage} />;
      default:
        return <></>;
    }
  };

  const closeInvalidFileTypeErrorAlert = useCallback(() => setInvalidFileTypeErrorVisible(false), []);

  const openFile = useCallback(
    (fileToOpen: File) => {
      setFile(fileToOpen);
      setPage(Pages.EDITOR);
    },
    [page, file]
  );

  const openFileByPath = useCallback((filePath: string) => {
    ipc.send("openFileByPath", { filePath: filePath });
  }, []);

  const goToHomePage = useCallback(() => {
    ipc.send("setFileMenusEnabled", { enabled: false });
    setPage(Pages.HOME);
    setFile(undefined);
  }, [page, file]);

  const preventDefaultEvent = useCallback(ev => {
    ev.preventDefault();
  }, []);

  const dragAndDropFileEvent = useCallback(
    ev => {
      ev.preventDefault();
      if (ev.dataTransfer) {
        openFileByPath(ev.dataTransfer.files[0].path);
      }
    },
    [openFileByPath]
  );

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
    ipc.on("openFile", (event: any, data: any) => {
      if (desktopRouter.getLanguageData(data.file.fileType)) {
        openFile(data.file);
      } else {
        setInvalidFileTypeErrorVisible(true);
      }
    });

    return () => {
      ipc.removeAllListeners("openFile");
    };
  }, [ipc, page, file]);

  useEffect(() => {
    ipc.on("saveFileSuccess", (event: any, data: any) => {
      file!.filePath = data.filePath;
    });

    return () => {
      ipc.removeAllListeners("saveFileSuccess");
    };
  }, [ipc, file]);

  useEffect(() => {
    document.addEventListener("dragover", preventDefaultEvent);
    document.addEventListener("drop", preventDefaultEvent);
    document.body.addEventListener("drop", dragAndDropFileEvent);

    return () => {
      document.removeEventListener("dragover", preventDefaultEvent);
      document.removeEventListener("drop", preventDefaultEvent);
      document.body.removeEventListener("drop", dragAndDropFileEvent);
    };
  }, [preventDefaultEvent, dragAndDropFileEvent]);

  return (
    <GlobalContext.Provider
      value={{
        router: desktopRouter,
        envelopeBusOuterMessageHandlerFactory: envelopeBusOuterMessageHandlerFactory,
        iframeTemplateRelativePath: "envelope/index.html",
        fileActions: fileActions,
        file: file
      }}
    >
      {invalidFileTypeErrorVisible && (
        <div className={"kogito--alert-container"}>
          <Alert
            variant={AlertVariant.danger}
            title="This file extension is not supported."
            action={<AlertActionCloseButton onClose={closeInvalidFileTypeErrorAlert} />}
          />
        </div>
      )}
      <Router />
    </GlobalContext.Provider>
  );
}
