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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { GlobalContext } from "../../common/GlobalContext";
import { DmnRunnerContext } from "./DmnRunnerContext";
import JSONSchemaBridge from "uniforms-bridge-json-schema";
import { DmnRunnerService } from "./DmnRunnerService";
import { DmnRunnerModal } from "./DmnRunnerModal";
import { EmbeddedEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { diff } from "deep-object-diff";
import { getCookie, setCookie } from "../../common/utils";
import { useNotificationsPanel } from "../NotificationsPanel/NotificationsPanelContext";

const DMN_RUNNER_POLLING_TIME = 1000;
export const THROTTLING_TIME = 200;
const DMN_RUNNER_PORT_COOKIE_NAME = "dmn-runner-port";
export const DMN_RUNNER_DEFAULT_PORT = "8080";

interface Props {
  children: React.ReactNode;
  editor?: EmbeddedEditorRef;
  isEditorReady: boolean;
}

export function DmnRunnerContextProvider(props: Props) {
  const notificationsPanel = useNotificationsPanel();
  const [isDrawerExpanded, setDrawerExpanded] = useState(false);
  const [isModalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({});
  const globalContext = useContext(GlobalContext);
  const [status, setStatus] = useState(() =>
    globalContext.file.fileExtension === "dmn" ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE
  );
  const [jsonSchemaBridge, setJsonSchemaBridge] = useState<JSONSchemaBridge>();
  const [port, setPort] = useState(() => getCookie(DMN_RUNNER_PORT_COOKIE_NAME) ?? DMN_RUNNER_DEFAULT_PORT);
  const service = useMemo(() => new DmnRunnerService(port), [port]);
  const version = useMemo(() => "$_{WEBPACK_REPLACE__dmnRunnerVersion}", []);

  const updateJsonSchemaBridge = useCallback(() => {
    return props.editor
      ?.getContent()
      .then(content => service.getJsonSchemaBridge(content ?? ""))
      .then(newJsonSchemaBridge => {
        const propertiesDifference = diff(
          jsonSchemaBridge?.schema.definitions.InputSet.properties ?? {},
          newJsonSchemaBridge?.schema.definitions.InputSet.properties ?? {}
        );

        // Remove an formData property that has been deleted;
        setFormData(previousFormData => {
          const newFormData = { ...previousFormData };
          Object.keys(propertiesDifference).forEach(property => {
            delete (newFormData as any)?.[property];
          });
          return newFormData;
        });

        setJsonSchemaBridge(newJsonSchemaBridge);
      });
  }, [props.editor, service, jsonSchemaBridge]);

  // Pooling to detect either if DMN Runner is running or has stopped
  useEffect(() => {
    if (status === DmnRunnerStatus.UNAVAILABLE) {
      return;
    }

    let detectDmnRunner: number | undefined;
    if (status !== DmnRunnerStatus.RUNNING) {
      detectDmnRunner = window.setInterval(() => {
        service.checkServer().then(() => {
          // Check the running version of the DMN Runner, if outdated cancel polling and change status.
          service.version().then(data => {
            window.clearInterval(detectCrashesOrStops);
            if (data?.version !== version) {
              setStatus(DmnRunnerStatus.OUTDATED);
            } else {
              if (isModalOpen) {
                setDrawerExpanded(true);
              }
              setStatus(DmnRunnerStatus.RUNNING);
            }
          });
        });
      }, DMN_RUNNER_POLLING_TIME);

      return () => window.clearInterval(detectDmnRunner);
    }

    let detectCrashesOrStops: number | undefined;
    if (status === DmnRunnerStatus.RUNNING) {
      detectCrashesOrStops = window.setInterval(() => {
        service.checkServer().catch(() => {
          setStatus(DmnRunnerStatus.STOPPED);
          setModalOpen(true);
          setDrawerExpanded(false);
          window.clearInterval(detectCrashesOrStops);
        });
      }, DMN_RUNNER_POLLING_TIME);

      // After the detection of the DMN Runner, set the schema for the first time
      if (props.isEditorReady) {
        updateJsonSchemaBridge();
      }

      return () => window.clearInterval(detectCrashesOrStops);
    }
  }, [props.editor, props.isEditorReady, isModalOpen, status, service]);

  // Subscribe to any change on the DMN Editor and update the JsonSchemaBridge
  useEffect(() => {
    if (!props.editor || status === DmnRunnerStatus.UNAVAILABLE) {
      return;
    }

    let timeout: number | undefined;
    const subscription = props.editor.getStateControl().subscribe(() => {
      if (timeout) {
        clearTimeout(timeout);
      }
      timeout = window.setTimeout(() => {
        updateJsonSchemaBridge();
      }, THROTTLING_TIME);
    });
    return () => props.editor?.getStateControl().unsubscribe(subscription);
  }, [props.editor, status, updateJsonSchemaBridge]);

  const saveNewPort = useCallback((newPort: string) => {
    setPort(newPort);
    setCookie(DMN_RUNNER_PORT_COOKIE_NAME, newPort);
  }, []);

  // Subscribe to any change on the DMN Editor and validate the model
  useEffect(() => {
    if (!props.editor || status === DmnRunnerStatus.UNAVAILABLE) {
      return;
    }

    const validate = () => {
      props.editor
        ?.getContent()
        .then(content => service.validate(content ?? ""))
        .then(validationResults => {
          return validationResults.map((validationResult: any) => ({
            type: "PROBLEM",
            path: "",
            severity: validationResult.severity,
            message: `${validationResult.messageType}: ${validationResult.message}`
          }));
        })
        .then(notifications => {
          notificationsPanel.getTabRef("Validation")?.setNotifications("", notifications);
        });
    };

    let timeout: number | undefined;
    const subscription = props.editor.getStateControl().subscribe(() => {
      if (timeout) {
        clearTimeout(timeout);
      }
      timeout = window.setTimeout(validate, THROTTLING_TIME);
    });
    validate();

    return () => props.editor?.getStateControl().unsubscribe(subscription);
  }, [props.editor, status, props.isEditorReady]);

  return (
    <DmnRunnerContext.Provider
      value={{
        status,
        setStatus,
        jsonSchemaBridge,
        isDrawerExpanded,
        setDrawerExpanded,
        isModalOpen,
        setModalOpen,
        formData,
        setFormData,
        port,
        saveNewPort,
        service,
        version
      }}
    >
      {props.children}
      <DmnRunnerModal />
    </DmnRunnerContext.Provider>
  );
}
