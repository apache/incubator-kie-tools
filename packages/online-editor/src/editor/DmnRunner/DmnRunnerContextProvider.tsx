/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useCallback, useContext, useEffect, useState } from "react";
import { GlobalContext } from "../../common/GlobalContext";
import { DmnRunnerContext } from "./DmnRunnerContext";
import JSONSchemaBridge from "../../common/Bridge";
import { DmnRunner } from "../../common/DmnRunner";
import { DmnRunnerModal } from "./DmnRunnerModal";
import { EditorApi, KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "@kogito-tooling/editor/dist/api";
import { StateControl } from "@kogito-tooling/editor/dist/channel";
import { EnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";
import { useModals } from "../../common/ModalContext";

const DMN_RUNNER_POLLING_TIME = 500;

export enum DmnRunnerStatus {
  DISABLED,
  AVAILABLE,
  RUNNING,
  NOT_RUNNING,
  STOPPED
}

type Editor =
  | (EditorApi & {
      getStateControl(): StateControl;
      getEnvelopeServer(): EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>;
    })
  | null;

interface Props {
  children: React.ReactNode;
  editor?: Editor;
  isEditorReady: boolean;
}

export function DmnRunnerContextProvider(props: Props) {
  const [isDmnRunnerDrawerOpen, setDmnRunnerDrawerOpen] = useState(false);
  const [isDmnRunnerModalOpen, setDmnRunnerModalOpen] = useState(false);

  const context = useContext(GlobalContext);
  const modals = useModals();
  // This state saves the current status of the Dmn Runner server on the user machine.
  const [dmnRunnerStatus, setDmnRunnerStatus] = useState(DmnRunnerStatus.DISABLED);
  const [dmnRunnerJsonSchemaBridge, setDmnRunnerJsonSchemaBridge] = useState<JSONSchemaBridge>();

  const setJsonSchemaBridge = useCallback(
    () =>
      props.editor
        ?.getContent()
        .then(content => DmnRunner.getJsonSchemaBridge(content ?? ""))
        .then(jsonSchemaBridge => setDmnRunnerJsonSchemaBridge(jsonSchemaBridge)),
    [props.editor]
  );

  // Pooling to detect either if DMN Runner is running or has stopped
  useEffect(() => {
    if (context.file.fileExtension === "dmn") {
      let detectDmnRunner: number | undefined;
      if (dmnRunnerStatus !== DmnRunnerStatus.RUNNING) {
        detectDmnRunner = window.setInterval(() => {
          DmnRunner.checkServer().then(() => {
            setDmnRunnerStatus(DmnRunnerStatus.RUNNING);
            setDmnRunnerDrawerOpen(true);
            window.clearInterval(detectDmnRunner);
          });
        }, DMN_RUNNER_POLLING_TIME);

        return () => window.clearInterval(detectDmnRunner);
      }

      let detectCrashesOrStops: number | undefined;
      if (dmnRunnerStatus === DmnRunnerStatus.RUNNING) {
        detectCrashesOrStops = window.setInterval(() => {
          DmnRunner.checkServer().catch(() => {
            setDmnRunnerStatus(DmnRunnerStatus.STOPPED);
            setDmnRunnerModalOpen(true);
            setDmnRunnerDrawerOpen(false);
            window.clearInterval(detectCrashesOrStops);
          });
        }, DMN_RUNNER_POLLING_TIME);

        // After the detection that is running, set the schema for the first time
        setJsonSchemaBridge();

        return () => window.clearInterval(detectCrashesOrStops);
      }
    }
  }, [props.editor, dmnRunnerStatus, props.isEditorReady, modals.openModal]);

  useEffect(() => {
    if (isDmnRunnerModalOpen) {
      modals.openModal(
        <DmnRunnerModal
          dmnRunnerStatus={dmnRunnerStatus}
          setDmnRunnerStatus={setDmnRunnerStatus}
          setDmnRunnerModalOpen={setDmnRunnerModalOpen}
        />
      );
    }
  }, [dmnRunnerStatus, setDmnRunnerStatus, isDmnRunnerModalOpen]);

  // Subscribe to any change on the DMN Editor
  useEffect(() => {
    if (!props.editor || context.file.fileExtension !== "dmn") {
      return;
    }

    const subscription = props.editor.getStateControl().subscribe(() => setJsonSchemaBridge());

    return () => {
      props.editor?.getStateControl().unsubscribe(subscription);
    };
  }, [props.editor, context.file.fileExtension, setJsonSchemaBridge]);

  return (
    <DmnRunnerContext.Provider
      value={{
        status: dmnRunnerStatus,
        setStatus: setDmnRunnerStatus,
        jsonSchemaBridge: dmnRunnerJsonSchemaBridge,
        isDrawerOpen: isDmnRunnerDrawerOpen,
        setDrawerOpen: setDmnRunnerDrawerOpen,
        isModalOpen: isDmnRunnerModalOpen,
        setModalOpen: setDmnRunnerModalOpen
      }}
    >
      {props.children}
    </DmnRunnerContext.Provider>
  );
}
