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

import { Alert, AlertActionCloseButton, AlertVariant } from "@patternfly/react-core/dist/js/components/Alert";
import * as electron from "electron";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import "../../static/resources/style.css";
import { ElectronFile, UNSAVED_FILE_NAME } from "../common/ElectronFile";
import { GlobalContext } from "./common/GlobalContext";
import { EditorPage } from "./editor/EditorPage";
import { HomePage } from "./home/HomePage";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import IpcRendererEvent = Electron.IpcRendererEvent;
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { DesktopI18nContext, desktopI18nDefaults, desktopI18nDictionaries } from "./common/i18n";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { removeDirectories, removeFileExtension } from "../common/utils";

enum Pages {
  HOME,
  EDITOR,
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

const bpmnEnvelope = { resourcesPathPrefix: "../gwt-editors/bpmn", envelopePath: "envelope/bpmn-envelope.html" };

const dmnEnvelope = { resourcesPathPrefix: "../gwt-editors/dmn", envelopePath: "envelope/dmn-envelope.html" };

export function App() {
  const [page, setPage] = useState(Pages.HOME);
  const [file, setFile] = useState<EmbeddedEditorFile>({
    fileName: "",
    fileExtension: "",
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
  });
  const [invalidFileTypeErrorVisible, setInvalidFileTypeErrorVisible] = useState(false);

  const onFilenameChange = useCallback(
    (filePath: string) => {
      setFile({
        fileName: removeFileExtension(removeDirectories(filePath)!),
        fileExtension: file.fileExtension,
        getFileContents: file.getFileContents,
        isReadOnly: false,
        path: filePath,
      });
    },
    [file]
  );

  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "bpmn",
          filePathGlob: "**/*.bpmn?(2)",
          resourcesPathPrefix: "../gwt-editors/bpmn",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "envelope/bpmn-envelope.html" },
        }),
        new EnvelopeMapping({
          type: "dmn",
          filePathGlob: "**/*.dmn",
          resourcesPathPrefix: "../gwt-editors/dmn",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "envelope/dmn-envelope.html" },
        }),
      ]),
    []
  );

  const closeInvalidFileTypeErrorAlert = useCallback(() => {
    if (page === Pages.HOME) {
      setInvalidFileTypeErrorVisible(false);
    }
  }, [page]);

  const openFile = useCallback(
    (fileToOpen: ElectronFile) => {
      closeInvalidFileTypeErrorAlert();
      setPage(Pages.EDITOR);
      setFile({
        fileName: removeFileExtension(removeDirectories(fileToOpen.filePath)!),
        fileExtension: fileToOpen.fileType,
        getFileContents: () => Promise.resolve(fileToOpen.fileContent),
        isReadOnly: false,
        path: fileToOpen.filePath,
      });
    },
    [closeInvalidFileTypeErrorAlert]
  );

  const openFileByPath = useCallback((filePath: string) => {
    electron.ipcRenderer.send("openFileByPath", { filePath: filePath });
  }, []);

  const goToHomePage = useCallback(() => {
    electron.ipcRenderer.send("setFileMenusEnabled", { enabled: false });
    setPage(Pages.HOME);
  }, []);

  const dragAndDropFileEvent = useCallback(
    (ev) => {
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
    electron.ipcRenderer.on("openFile", (event: IpcRendererEvent, data: { file: ElectronFile }) => {
      if (data.file.filePath === UNSAVED_FILE_NAME || editorEnvelopeLocator.hasMappingFor(data.file.filePath)) {
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
  }, [page, editorEnvelopeLocator, openFile]);

  useEffect(() => {
    document.addEventListener("dragover", (e) => e.preventDefault());
    document.addEventListener("drop", (e) => e.preventDefault());
    document.body.addEventListener("drop", dragAndDropFileEvent);

    return () => {
      document.removeEventListener("dragover", (e) => e.preventDefault());
      document.removeEventListener("drop", (e) => e.preventDefault());
      document.body.removeEventListener("drop", dragAndDropFileEvent);
    };
  }, [dragAndDropFileEvent]);

  const Router = useMemo(() => {
    switch (page) {
      case Pages.HOME:
        return <HomePage openFile={openFile} openFileByPath={openFileByPath} />;
      case Pages.EDITOR:
        return <EditorPage onFilenameChange={onFilenameChange} onClose={goToHomePage} />;
      default:
        return <></>;
    }
  }, [page, openFile, openFileByPath, onFilenameChange, goToHomePage]);

  return (
    <I18nDictionariesProvider
      defaults={desktopI18nDefaults}
      dictionaries={desktopI18nDictionaries}
      initialLocale={navigator.language}
      ctx={DesktopI18nContext}
    >
      <DesktopI18nContext.Consumer>
        {({ i18n }) => (
          <GlobalContext.Provider
            value={{
              file,
              editorEnvelopeLocator,
            }}
          >
            {invalidFileTypeErrorVisible && (
              <div className={"kogito--alert-container"}>
                <Alert
                  variant={AlertVariant.danger}
                  title={i18n.app.title}
                  actionClose={
                    <AlertActionCloseButton
                      onClose={closeInvalidFileTypeErrorAlert}
                      ouiaId="close-danger-alert-button"
                    />
                  }
                  ouiaId="danger-alert"
                />
              </div>
            )}
            {Router}
          </GlobalContext.Provider>
        )}
      </DesktopI18nContext.Consumer>
    </I18nDictionariesProvider>
  );
}
