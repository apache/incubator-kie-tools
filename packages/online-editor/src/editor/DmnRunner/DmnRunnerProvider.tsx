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
import { DmnRunnerCallbacksContext, DmnRunnerContext } from "./DmnRunnerContext";
import { DmnRunnerModelPayload, DmnRunnerService } from "./DmnRunnerService";
import { KieToolingExtendedServicesStatus } from "../../kieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { QueryParams } from "../../navigation/Routes";
import { jsonParseWithDate } from "../../json/JsonParse";
import { usePrevious } from "../../reactExt/Hooks";
import { useOnlineI18n } from "../../i18n";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { useGlobals } from "../../globalCtx/GlobalContext";
import { useKieToolingExtendedServices } from "../../kieToolingExtendedServices/KieToolingExtendedServicesContext";
import { Notification } from "@kie-tooling-core/notifications/dist/api";
import { DmnSchema } from "@kogito-tooling/form/dist/dmn";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  const queryParams = useQueryParams();
  const history = useHistory();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const workspaces = useWorkspaces();

  const [error, setError] = useState(false);
  const [schema, setSchema] = useState<DmnSchema | undefined>(undefined);
  const [isExpanded, setExpanded] = useState(false);
  const [mode, setMode] = useState(DmnRunnerMode.DRAWER);
  const [data, setData] = useState([{}]);
  const [dataIndex, setDataIndex] = useState<number>(0);

  const status = useMemo(() => {
    return isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE;
  }, [isExpanded]);

  const service = useMemo(
    () => new DmnRunnerService(kieToolingExtendedServices.baseUrl),
    [kieToolingExtendedServices.baseUrl]
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
      setSchema(await service.formSchema(payload));
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

    if (kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING) {
      props.editorPageDock?.setNotifications(i18n.terms.validation, "", []);
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
    props.editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
  }, [
    props.workspaceFile,
    props.editorPageDock,
    kieToolingExtendedServices.status,
    preparePayload,
    service,
    i18n.terms.validation,
  ]);

  useEffect(() => {
    validate();
  }, [validate]);

  useEffect(() => {
    if (!schema || !queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS)) {
      return;
    }

    try {
      setData([jsonParseWithDate(queryParams.get(QueryParams.DMN_RUNNER_FORM_INPUTS)!)]);
      setExpanded(true);
    } catch (e) {
      console.error(`Cannot parse "${QueryParams.DMN_RUNNER_FORM_INPUTS}"`, e);
    } finally {
      history.replace({
        pathname: globals.routes.editor.path({ extension: "dmn" }),
        search: globals.routes.editor.queryArgs(queryParams).without(QueryParams.DMN_RUNNER_FORM_INPUTS).toString(),
      });
    }
  }, [schema, history, globals.routes, queryParams]);

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
      setExpanded(true);
    }

    if (
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.STOPPED ||
      kieToolingExtendedServices.status === KieToolingExtendedServicesStatus.NOT_RUNNING
    ) {
      setExpanded(false);
    }
  }, [prevKieToolingExtendedServicesStatus, kieToolingExtendedServices.status, props.workspaceFile.extension]);

  const dmnRunnerCallbacks = useMemo(
    () => ({
      preparePayload,
      setData,
      setDataIndex,
      setExpanded,
      setError,
      setMode,
    }),
    [preparePayload]
  );

  const dmnRunner = useMemo(
    () => ({
      data,
      dataIndex,
      error,
      isExpanded,
      mode,
      schema,
      service,
      status,
    }),
    [data, error, schema, isExpanded, mode, service, status, dataIndex]
  );

  return (
    <>
      <DmnRunnerContext.Provider value={dmnRunner}>
        <DmnRunnerCallbacksContext.Provider value={dmnRunnerCallbacks}>
          {props.children}
        </DmnRunnerCallbacksContext.Provider>
      </DmnRunnerContext.Provider>
    </>
  );
}
