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
import { useKieToolingExtendedServices } from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { DmnFormSchema } from "@kogito-tooling/form/dist/dmn";
import { DmnRunnerContext } from "./DmnRunnerContext";
import { DmnRunnerModelPayload, DmnRunnerService } from "./DmnRunnerService";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { useOnlineI18n } from "../../common/i18n";
import { Notification } from "@kie-tooling-core/notifications/dist/api";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { useGlobals } from "../../common/GlobalContext";
import { QueryParams } from "../../common/Routes";
import { usePrevious } from "../../common/Hooks";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { jsonParseWithDate } from "../../common/utils";
import { NotificationsPanelController } from "../NotificationsPanel/NotificationsPanel";
import { decoder, useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";

interface Props {
  children: React.ReactNode;
  notificationsPanel: NotificationsPanelController | undefined;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerContextProvider(props: Props) {
  const { i18n } = useOnlineI18n();
  const queryParams = useQueryParams();
  const history = useHistory();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const workspaces = useWorkspaces();
  const [isDrawerExpanded, setDrawerExpanded] = useState(false);
  const [formData, setFormData] = useState<object>({});
  const [formSchema, setFormSchema] = useState<DmnFormSchema | undefined>(undefined);
  const [formError, setFormError] = useState(false);
  const [mode, setMode] = useState(DmnRunnerMode.DRAWER);
  const [tableData, setTableData] = useState([{}]);

  const status = useMemo(() => {
    return isDrawerExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE;
  }, [isDrawerExpanded]);

  const service = useMemo(
    () => new DmnRunnerService(kieToolingExtendedServices.baseUrl),
    [kieToolingExtendedServices.baseUrl]
  );

  const preparePayload = useCallback(
    async (formData?: any) => {
      // TODO: Get only the included files, not all dmn files
      const files = (
        await workspaces.getFiles({
          fs: await workspaces.fsService.getWorkspaceFs(props.workspaceFile.workspaceId),
          workspaceId: props.workspaceFile.workspaceId,
        })
      ).filter((f) => f.extension === "dmn");

      const resourcePromises = files.map(async (f) => ({
        URI: f.relativePath,
        content: decoder.decode(await f.getFileContents()),
      }));

      return {
        mainURI: props.workspaceFile.relativePath,
        resources: await Promise.all(resourcePromises),
        context: formData,
      } as DmnRunnerModelPayload;
    },
    [props.workspaceFile, workspaces]
  );

  const updateFormSchema = useCallback(async () => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    try {
      const payload = await preparePayload();
      setFormSchema(await service.formSchema(payload));
    } catch (err) {
      console.error(err);
      setFormError(true);
    }
  }, [preparePayload, props.workspaceFile, service]);

  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      setDrawerExpanded(false);
      return;
    }

    updateFormSchema();
  }, [props.workspaceFile, updateFormSchema]);

  const validate = useCallback(async () => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING) {
      props.notificationsPanel?.getTab(i18n.terms.validation)?.kogitoNotifications_setNotifications("", []);
      return;
    }

    const payload = await preparePayload(props.workspaceFile);
    const validationResults = await service.validate(payload);
    const notifications: Notification[] = validationResults.map((validationResult: any) => ({
      type: "PROBLEM",
      path: "",
      severity: validationResult.severity,
      message: `${validationResult.messageType}: ${validationResult.message}`,
    }));
    props.notificationsPanel?.getTab(i18n.terms.validation)?.kogitoNotifications_setNotifications("", notifications);
  }, [props.workspaceFile, props.notificationsPanel, kieToolingExtendedServices.status, i18n, preparePayload, service]);

  useEffect(() => {
    validate();
  }, [validate]);

  useEffect(() => {
    updateFormSchema();
  }, [updateFormSchema]);

  useEffect(() => {
    if (!formSchema || !queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS)) {
      return;
    }

    try {
      setFormData(jsonParseWithDate(queryParams.get(QueryParams.DMN_RUNNER_FORM_INPUTS)!));
      setDrawerExpanded(true);
    } catch (e) {
      console.error(`Cannot parse "${QueryParams.DMN_RUNNER_FORM_INPUTS}"`, e);
    } finally {
      history.replace({
        pathname: globals.routes.editor.path({ extension: "dmn" }),
        search: globals.routes.editor.queryArgs(queryParams).without(QueryParams.DMN_RUNNER_FORM_INPUTS).toString(),
      });
    }
  }, [formSchema, history, globals.routes, queryParams]);

  const prevKieToolingExtendedServicesStatus = usePrevious(kieToolingExtendedServices.status);
  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (
      prevKieToolingExtendedServicesStatus &&
      prevKieToolingExtendedServicesStatus !== KieToolingExtendedServicesStatus.AVAILABLE &&
      prevKieToolingExtendedServicesStatus !== KieToolingExtendedServicesStatus.RUNNING &&
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.RUNNING
    ) {
      setDrawerExpanded(true);
    }

    if (
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.STOPPED ||
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.NOT_RUNNING
    ) {
      setDrawerExpanded(false);
    }
  }, [prevKieToolingExtendedServicesStatus, kieToolingExtendedServices.status, props.workspaceFile.extension]);

  return (
    <DmnRunnerContext.Provider
      value={{
        status,
        formSchema,
        isDrawerExpanded,
        setDrawerExpanded,
        formData,
        setFormData,
        service,
        formError,
        setFormError,
        preparePayload,
        tableData,
        setTableData,
        mode,
        setMode,
      }}
    >
      {props.children}
    </DmnRunnerContext.Provider>
  );
}
