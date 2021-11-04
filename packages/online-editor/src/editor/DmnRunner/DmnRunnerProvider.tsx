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
import { EditorPageDockDrawerController } from "../EditorPageDockDrawer";
import { decoder, useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";
import { DmnFormSchema } from "@kogito-tooling/form/dist/dmn";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { DmnRunnerCallbacksContext, DmnRunnerContext } from "./DmnRunnerContext";
import { DmnRunnerModelPayload, DmnRunnerService } from "./DmnRunnerService";
import { KieToolingExtendedServicesStatus } from "../KieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { QueryParams } from "../../common/Routes";
import { jsonParseWithDate } from "../../common/utils";
import { usePrevious } from "../../common/Hooks";
import { useOnlineI18n } from "../../common/i18n";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { useGlobals } from "../../common/GlobalContext";
import { useKieToolingExtendedServices } from "../KieToolingExtendedServices/KieToolingExtendedServicesContext";
import { Notification } from "@kie-tooling-core/notifications/dist/api";

interface Props {
  editorPageDock: EditorPageDockDrawerController | undefined;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  const queryParams = useQueryParams();
  const history = useHistory();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const workspaces = useWorkspaces();

  const [formData, setFormData] = useState<object>({});
  const [formError, setFormError] = useState(false);
  const [formSchema, setFormSchema] = useState<DmnFormSchema | undefined>(undefined);
  const [isDrawerExpanded, setDrawerExpanded] = useState(false);
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
    async (data?: any) => {
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
      setFormSchema(await service.formSchema(payload));
    } catch (err) {
      console.error(err);
      setFormError(true);
    }
  }, [props.workspaceFile.extension, preparePayload, service]);

  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      setDrawerExpanded(false);
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

  // useEffect(() => {
  //   updateFormSchema();
  // }, [updateFormSchema]);

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
    <>
      <DmnRunnerContext.Provider
        value={{
          formData,
          formError,
          formSchema,
          isDrawerExpanded,
          mode,
          status,
          service,
          tableData,
        }}
      >
        <DmnRunnerCallbacksContext.Provider
          value={{
            preparePayload,
            setDrawerExpanded,
            setFormData,
            setFormError,
            setMode,
            setTableData,
          }}
        >
          {props.children}
        </DmnRunnerCallbacksContext.Provider>
      </DmnRunnerContext.Provider>
    </>
  );
}
