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
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  DependentFeature,
  useKieToolingExtendedServices,
} from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { DmnFormSchema } from "@kogito-tooling/form/dist/dmn";
import { DmnRunnerContext } from "./DmnRunnerContext";
import { DmnRunnerService } from "./DmnRunnerService";
import { EmbeddedEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { DmnRunnerStatus } from "./DmnRunnerStatus";
import { useNotificationsPanel } from "../NotificationsPanel/NotificationsPanelContext";
import { useOnlineI18n } from "../../common/i18n";
import { NotificationType } from "@kie-tooling-core/notifications/dist/api";
import { QueryParams, useQueryParams } from "../../queryParams/QueryParamsContext";
import { jsonParseWithDate } from "../../common/utils";

interface Props {
  children: React.ReactNode;
  editor?: EmbeddedEditorRef;
  isEditorReady: boolean;
}

const THROTTLING_TIME = 200;

export function DmnRunnerContextProvider(props: Props) {
  const { i18n } = useOnlineI18n();
  const queryParams = useQueryParams();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const notificationsPanel = useNotificationsPanel();
  const [isDrawerExpanded, setDrawerExpanded] = useState(false);
  const [formData, setFormData] = useState({});
  const [formSchema, setFormSchema] = useState<DmnFormSchema>();
  const [formError, setFormError] = useState(false);
  const [status, setStatus] = useState(
    kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.UNAVAILABLE
      ? DmnRunnerStatus.UNAVAILABLE
      : DmnRunnerStatus.AVAILABLE
  );

  const service = useMemo(
    () => new DmnRunnerService(kieToolingExtendedServices.baseUrl),
    [kieToolingExtendedServices.baseUrl]
  );

  useEffect(() => {
    if (!queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS)) {
      return;
    }

    try {
      setFormData(jsonParseWithDate(decodeURIComponent(queryParams.get(QueryParams.DMN_RUNNER_FORM_INPUTS)!)));
    } catch (e) {
      console.error("Cannot parse formInputs", e);
      return;
    }
  }, [queryParams]);

  const updateFormSchema = useCallback(
    (args: { openDrawer: boolean }) => {
      return props.editor
        ?.getContent()
        .then((content) => service.formSchema(content ?? ""))
        .then((newSchema) => {
          setFormSchema(newSchema);
          const shouldOpenDrawer =
            args.openDrawer &&
            (queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS) ||
              (kieToolingExtendedServices.isModalOpen &&
                kieToolingExtendedServices.installTriggeredBy === DependentFeature.DMN_RUNNER));

          if (shouldOpenDrawer) {
            setDrawerExpanded(shouldOpenDrawer);
          }
        })
        .catch((err) => {
          console.error(err);
          setFormError(true);
        });
    },
    [
      queryParams,
      kieToolingExtendedServices.installTriggeredBy,
      kieToolingExtendedServices.isModalOpen,
      props.editor,
      service,
    ]
  );

  useEffect(() => {
    if (kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING) {
      setStatus(DmnRunnerStatus.UNAVAILABLE);
      setDrawerExpanded(false);
      return;
    }

    setStatus(DmnRunnerStatus.AVAILABLE);
    // After the detection of the DMN Runner, set the schema for the first time
    if (props.isEditorReady) {
      updateFormSchema({ openDrawer: true });
    }
  }, [
    kieToolingExtendedServices.installTriggeredBy,
    kieToolingExtendedServices.isModalOpen,
    kieToolingExtendedServices.status,
    props.isEditorReady,
    updateFormSchema,
  ]);

  // Subscribe to any change on the DMN Editor to validate the model and update the JSON Schema
  useEffect(() => {
    if (!props.editor?.isReady || status !== DmnRunnerStatus.AVAILABLE) {
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
        updateFormSchema({ openDrawer: false });
      }, THROTTLING_TIME);
    });
    validate();

    return () => props.editor?.getStateControl().unsubscribe(subscription);
  }, [props.editor, status, props.isEditorReady, updateFormSchema, i18n, service, notificationsPanel]);

  return (
    <DmnRunnerContext.Provider
      value={{
        status,
        setStatus,
        formSchema,
        isDrawerExpanded,
        setDrawerExpanded,
        formData,
        setFormData,
        service,
        formError,
        setFormError,
      }}
    >
      {props.children}
    </DmnRunnerContext.Provider>
  );
}
