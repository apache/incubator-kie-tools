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
import { EmbeddedEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { DmnRunnerStatus } from "./DmnRunnerStatus";

const DMN_RUNNER_POLLING_TIME = 500;

interface Props {
  children: React.ReactNode;
  editor?: EmbeddedEditorRef;
  isEditorReady: boolean;
}

export function DmnRunnerContextProvider(props: Props) {
  const [isDrawerOpen, setDrawerOpen] = useState(false);
  const [isModalOpen, setModalOpen] = useState(false);

  const globalContext = useContext(GlobalContext);
  const [status, setStatus] = useState(DmnRunnerStatus.UNAVAILABLE);
  const [jsonSchemaBridge, setJsonSchemaBridge] = useState<JSONSchemaBridge>();

  const updateJsonSchemaBridge = useCallback(() => {
    return props.editor
      ?.getContent()
      .then(content => DmnRunner.getJsonSchemaBridge(content ?? ""))
      .then(setJsonSchemaBridge);
  }, [props.editor]);

  useEffect(() => {
    if (globalContext.file.fileExtension === "dmn") {
      setStatus(DmnRunnerStatus.AVAILABLE);
    }
  }, [globalContext.file.fileExtension]);

  // Pooling to detect either if DMN Runner is running or has stopped
  useEffect(() => {
    if (status === DmnRunnerStatus.UNAVAILABLE) {
      return;
    }

    let detectDmnRunner: number | undefined;
    if (status !== DmnRunnerStatus.RUNNING) {
      detectDmnRunner = window.setInterval(() => {
        DmnRunner.checkServer().then(() => {
          setStatus(DmnRunnerStatus.RUNNING);
          setDrawerOpen(true);
          window.clearInterval(detectDmnRunner);
        });
      }, DMN_RUNNER_POLLING_TIME);

      return () => window.clearInterval(detectDmnRunner);
    }

    let detectCrashesOrStops: number | undefined;
    if (status === DmnRunnerStatus.RUNNING) {
      detectCrashesOrStops = window.setInterval(() => {
        DmnRunner.checkServer().catch(() => {
          setStatus(DmnRunnerStatus.STOPPED);
          setModalOpen(true);
          setDrawerOpen(false);
          window.clearInterval(detectCrashesOrStops);
        });
      }, DMN_RUNNER_POLLING_TIME);

      // After the detection that is running, set the schema for the first time
      if (props.isEditorReady) {
        updateJsonSchemaBridge();
      }

      return () => window.clearInterval(detectCrashesOrStops);
    }
  }, [props.editor, status, props.isEditorReady]);

  // Subscribe to any change on the DMN Editor
  useEffect(() => {
    if (!props.editor || status === DmnRunnerStatus.UNAVAILABLE) {
      return;
    }

    const subscription = props.editor.getStateControl().subscribe(updateJsonSchemaBridge);
    return () => props.editor?.getStateControl().unsubscribe(subscription);
  }, [props.editor, status, updateJsonSchemaBridge]);

  return (
    <DmnRunnerContext.Provider
      value={{
        status,
        setStatus,
        jsonSchemaBridge,
        isDrawerOpen,
        setDrawerOpen,
        isModalOpen,
        setModalOpen
      }}
    >
      {props.children}
      <DmnRunnerModal />
    </DmnRunnerContext.Provider>
  );
}
