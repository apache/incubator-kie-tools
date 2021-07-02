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
import { DmnFormSchema, DmnRunnerService } from "./DmnRunnerService";
import { DmnRunnerModal } from "./DmnRunnerModal";
import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { getCookie, setCookie } from "../../common/utils";
import { useNotificationsPanel } from "../NotificationsPanel/NotificationsPanelContext";
import { useOnlineI18n } from "../../common/i18n";
import { NotificationType } from "@kie-tooling-core/notifications/dist/api";

const KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME = 1000;
export const THROTTLING_TIME = 200;
const KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME = "kie-tooling-extended-services-port";
export const KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT = "21345";

interface Props {
  children: React.ReactNode;
  editor?: EmbeddedEditorRef;
  isEditorReady: boolean;
  closeDmnTour: () => void;
}

export function DmnRunnerContextProvider(props: Props) {
  const { i18n } = useOnlineI18n();
  const notificationsPanel = useNotificationsPanel();
  const [isDrawerExpanded, setDrawerExpanded] = useState(false);
  const [isModalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({});
  const [outdated, setOutdated] = useState(false);
  const globalContext = useContext(GlobalContext);
  const [status, setStatus] = useState(() =>
    globalContext.file.fileExtension === "dmn" ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE
  );
  const [formSchema, setFormSchema] = useState<DmnFormSchema>();
  const [port, setPort] = useState(
    () => getCookie(KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME) ?? KIE_TOOLING_EXTENDED_SERVICES_DEFAULT_PORT
  );
  const service = useMemo(() => new DmnRunnerService(port), [port]);
  const version = useMemo(() => process.env.WEBPACK_REPLACE__dmnRunnerCompatibleVersion ?? "0.0.0", []);
  const [formError, setFormError] = useState(false);

  const updateFormSchema = useCallback(() => {
    return props.editor
      ?.getContent()
      .then((content) => service.formSchema(content ?? ""))
      .then((newSchema) => setFormSchema(newSchema))
      .catch((err) => {
        console.error(err);
        setFormError(true);
      });
  }, [props.editor, service]);

  // Pooling to detect either if DMN Runner is running or has stopped
  useEffect(() => {
    if (status === DmnRunnerStatus.UNAVAILABLE) {
      return;
    }

    let detectCrashesOrStops: number | undefined;
    if (status === DmnRunnerStatus.RUNNING) {
      detectCrashesOrStops = window.setInterval(() => {
        service.check().catch(() => {
          setStatus(DmnRunnerStatus.STOPPED);
          setModalOpen(true);
          setDrawerExpanded(false);
          window.clearInterval(detectCrashesOrStops);
        });
      }, KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME);

      // After the detection of the DMN Runner, set the schema for the first time
      if (props.isEditorReady) {
        updateFormSchema();
      }

      return () => window.clearInterval(detectCrashesOrStops);
    }

    const detectDmnRunner: number | undefined = window.setInterval(() => {
      service
        .check()
        .then(() => {
          // Check the running version of the DMN Runner, if outdated cancel polling and change status.
          service.version().then((runnerVersion) => {
            window.clearInterval(detectDmnRunner);
            if (runnerVersion !== version) {
              setOutdated(true);
            } else {
              setOutdated(false);
              if (isModalOpen) {
                setDrawerExpanded(true);
              }
              setStatus(DmnRunnerStatus.RUNNING);
            }
          });
        })
        .catch((err) => {
          console.debug(err);
        });
    }, KIE_TOOLING_EXTENDED_SERVICES_POLLING_TIME);

    return () => window.clearInterval(detectDmnRunner);
  }, [props.editor, props.isEditorReady, isModalOpen, status, service]);

  const saveNewPort = useCallback((newPort: string) => {
    setPort(newPort);
    setCookie(KIE_TOOLING_EXTENDED_SERVICES_PORT_COOKIE_NAME, newPort);
  }, []);

  // Subscribe to any change on the DMN Editor to validate the model and update the JSON Schema
  useEffect(() => {
    if (!props.editor?.isReady || status !== DmnRunnerStatus.RUNNING) {
      return;
    }

    const validate = () => {
      props.editor
        ?.getContent()
        .then((content) => service.validate(content ?? ""))
        .then((validationResults) => {
          const notifications = validationResults.map((validationResult: any) => ({
            type: "PROBLEM" as NotificationType,
            path: "",
            severity: validationResult.severity,
            message: `${validationResult.messageType}: ${validationResult.message}`,
          }));
          notificationsPanel.getTabRef(i18n.terms.validation)?.kogitoNotifications_setNotifications("", notifications);
        });
    };

    let timeout: number | undefined;
    const subscription = props.editor.getStateControl().subscribe(() => {
      if (timeout) {
        clearTimeout(timeout);
      }
      timeout = window.setTimeout(() => {
        validate();
        updateFormSchema();
      }, THROTTLING_TIME);
    });
    validate();

    return () => props.editor?.getStateControl().unsubscribe(subscription);
  }, [props.editor, status, props.isEditorReady, updateFormSchema, i18n]);

  return (
    <DmnRunnerContext.Provider
      value={{
        status,
        setStatus,
        formSchema,
        isDrawerExpanded,
        setDrawerExpanded,
        isModalOpen,
        setModalOpen,
        formData,
        setFormData,
        port,
        saveNewPort,
        service,
        version,
        formError,
        setFormError,
        outdated,
        closeDmnTour: props.closeDmnTour,
      }}
    >
      {props.children}
      <DmnRunnerModal />
    </DmnRunnerContext.Provider>
  );
}
