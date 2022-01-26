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
import { PropsWithChildren, useCallback, useEffect, useMemo, useState } from "react";
import { EditorPageDockDrawerRef } from "../EditorPageDockDrawer";
import { decoder, useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { DmnRunnerDispatchContext, DmnRunnerStateContext } from "./DmnRunnerContext";
import { DmnRunnerModelPayload, DmnRunnerService } from "./DmnRunnerService";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { QueryParams } from "../../navigation/Routes";
import { jsonParseWithDate } from "../../json/JsonParse";
import { usePrevious } from "../../reactExt/Hooks";
import { useOnlineI18n } from "../../i18n";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { useKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { DmnSchema } from "@kie-tools/form/dist/dmn";
import { useSettings } from "../../settings/SettingsContext";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  const queryParams = useQueryParams();
  const history = useHistory();
  const routes = useRoutes();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const workspaces = useWorkspaces();
  const settings = useSettings();

  const [error, setError] = useState(false);
  const [jsonSchema, setJsonSchema] = useState<DmnSchema | undefined>(undefined);
  const [isExpanded, setExpanded] = useState(false);
  const [mode, setMode] = useState(DmnRunnerMode.FORM);
  const [inputRows, setInputRows] = useState([{}]);
  const [currentInputRowIndex, setCurrentInputRowIndex] = useState<number>(0);

  const status = useMemo(() => {
    return isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE;
  }, [isExpanded]);

  const service = useMemo(
    () => new DmnRunnerService(settings.kieSandboxExtendedServices.config.buildUrl()),
    [settings.kieSandboxExtendedServices.config]
  );

  const preparePayload = useCallback(
    async (data?: any) => {
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
        context: data,
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
      setJsonSchema(await service.formSchema(payload));
    } catch (err) {
      console.error(err);
      setError(true);
    }
  }, [props.workspaceFile.extension, preparePayload, service]);

  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      setExpanded(false);
      return;
    }

    updateFormSchema();
  }, [updateFormSchema, props.workspaceFile.extension]);

  const validate = useCallback(async () => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (kieSandboxExtendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      props.editorPageDock?.setNotifications(i18n.terms.validation, "", []);
      return;
    }

    const payload: DmnRunnerModelPayload = {
      mainURI: props.workspaceFile.relativePath,
      resources: [
        {
          URI: props.workspaceFile.relativePath,
          content: decoder.decode(await props.workspaceFile.getFileContents()),
        },
      ],
    };
    const validationResults = await service.validate(payload);
    const notifications: Notification[] = validationResults.map((validationResult: any) => ({
      type: "PROBLEM",
      path: "",
      severity: validationResult.severity,
      message: `${validationResult.messageType}: ${validationResult.message}`,
    }));
    props.editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
  }, [props.workspaceFile, props.editorPageDock, kieSandboxExtendedServices.status, service, i18n.terms.validation]);

  useEffect(() => {
    validate();
  }, [validate]);

  useEffect(() => {
    if (!jsonSchema || !queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS)) {
      return;
    }

    try {
      setInputRows([jsonParseWithDate(queryParams.get(QueryParams.DMN_RUNNER_FORM_INPUTS)!)]);
      setExpanded(true);
    } catch (e) {
      console.error(`Cannot parse "${QueryParams.DMN_RUNNER_FORM_INPUTS}"`, e);
    } finally {
      history.replace({
        pathname: routes.editor.path({ extension: "dmn" }),
        search: routes.editor.queryArgs(queryParams).without(QueryParams.DMN_RUNNER_FORM_INPUTS).toString(),
      });
    }
  }, [jsonSchema, history, routes, queryParams]);

  const prevKieSandboxExtendedServicesStatus = usePrevious(kieSandboxExtendedServices.status);
  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (
      prevKieSandboxExtendedServicesStatus &&
      prevKieSandboxExtendedServicesStatus !== KieSandboxExtendedServicesStatus.AVAILABLE &&
      prevKieSandboxExtendedServicesStatus !== KieSandboxExtendedServicesStatus.RUNNING &&
      kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.RUNNING
    ) {
      setExpanded(true);
    }

    if (
      kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.STOPPED ||
      kieSandboxExtendedServices.status === KieSandboxExtendedServicesStatus.NOT_RUNNING
    ) {
      setExpanded(false);
    }
  }, [prevKieSandboxExtendedServicesStatus, kieSandboxExtendedServices.status, props.workspaceFile.extension]);

  const dmnRunnerDispatch = useMemo(
    () => ({
      preparePayload,
      setInputRows,
      setCurrentInputRowIndex,
      setExpanded,
      setError,
      setMode,
    }),
    [preparePayload]
  );

  const dmnRunnerState = useMemo(
    () => ({
      inputRows,
      currentInputRowIndex,
      error,
      isExpanded,
      mode,
      jsonSchema,
      service,
      status,
    }),
    [inputRows, error, jsonSchema, isExpanded, mode, service, status, currentInputRowIndex]
  );

  return (
    <>
      <DmnRunnerStateContext.Provider value={dmnRunnerState}>
        <DmnRunnerDispatchContext.Provider value={dmnRunnerDispatch}>
          {props.children}
        </DmnRunnerDispatchContext.Provider>
      </DmnRunnerStateContext.Provider>
    </>
  );
}
