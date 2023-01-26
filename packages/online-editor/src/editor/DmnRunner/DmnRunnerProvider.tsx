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
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { DmnRunnerMode, DmnRunnerStatus } from "./DmnRunnerStatus";
import { DmnRunnerDispatchContext, DmnRunnerStateContext } from "./DmnRunnerContext";
import { KieSandboxExtendedServicesModelPayload } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { QueryParams } from "../../navigation/Routes";
import { jsonParseWithDate } from "../../json/JsonParse";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { DmnSchema, InputRow } from "@kie-tools/form-dmn";
import { useDmnRunnerInputs } from "../../dmnRunnerInputs/DmnRunnerInputsHook";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";

interface Props {
  isEditorReady?: boolean;
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const queryParams = useQueryParams();
  const history = useHistory();
  const routes = useRoutes();
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();

  const [isVisible, setVisible] = useState<boolean>(false);
  useEffect(() => {
    if (props.isEditorReady) {
      setVisible(true);
    } else {
      setVisible(false);
    }
  }, [props.isEditorReady]);

  const { inputRows, setInputRows } = useDmnRunnerInputs(props.workspaceFile);

  const [error, setError] = useState(false);
  const [jsonSchema, setJsonSchema] = useState<DmnSchema | undefined>(undefined);
  const [isExpanded, setExpanded] = useState(false);
  const [mode, setMode] = useState(DmnRunnerMode.FORM);
  const [currentInputRowIndex, setCurrentInputRowIndex] = useState<number>(0);
  const dmnLanguageService = useMemo(() => new DmnLanguageService(), []);

  const status = useMemo(() => {
    return isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE;
  }, [isExpanded]);

  const preparePayload = useCallback(
    async (formData?: InputRow) => {
      const currentResourceContent = await workspaces.resourceContentGet({
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: props.workspaceFile.relativePath,
      });

      if (!currentResourceContent) {
        throw new Error("Missing resource content from current file");
      }

      const importedModelsResources = await dmnLanguageService.getAllImportedModelsResources(
        workspaces,
        props.workspaceFile.workspaceId,
        [currentResourceContent?.content ?? ""]
      );
      const dmnResources = [currentResourceContent, ...importedModelsResources].map((resources) => ({
        URI: resources.path,
        content: resources.content ?? "",
      }));

      return {
        mainURI: props.workspaceFile.relativePath,
        resources: dmnResources,
        context: formData,
      } as KieSandboxExtendedServicesModelPayload;
    },
    [props.workspaceFile, workspaces, dmnLanguageService]
  );

  useEffect(() => {
    if (
      props.workspaceFile.extension !== "dmn" ||
      extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING
    ) {
      setExpanded(false);
      return;
    }

    preparePayload()
      .then((payload) => {
        extendedServices.client.formSchema(payload).then((jsonSchema) => {
          setJsonSchema(jsonSchema);
        });
      })
      .catch((err) => {
        console.error(err);
        setError(true);
      });
  }, [extendedServices.status, extendedServices.client, props.workspaceFile.extension, preparePayload]);

  useEffect(() => {
    if (queryParams.has(QueryParams.DMN_RUNNER_IS_EXPANDED)) {
      const isExpanded = queryParams.getBoolean(QueryParams.DMN_RUNNER_IS_EXPANDED);
      if (isExpanded !== undefined) {
        setExpanded(isExpanded);
      }
    }

    if (queryParams.has(QueryParams.DMN_RUNNER_MODE)) {
      const mode = queryParams.getString(QueryParams.DMN_RUNNER_MODE);
      if (mode === "form") {
        setMode(DmnRunnerMode.FORM);
      }
      if (mode === "table") {
        setMode(DmnRunnerMode.TABLE);
      }
    }

    if (queryParams.has(QueryParams.DMN_RUNNER_ROW)) {
      const row = queryParams.getNumber(QueryParams.DMN_RUNNER_ROW);
      if (row !== undefined) {
        setCurrentInputRowIndex(row);
      }
    }

    if (!jsonSchema || !queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS)) {
      return;
    }

    try {
      setInputRows([jsonParseWithDate(queryParams.getString(QueryParams.DMN_RUNNER_FORM_INPUTS)!) as InputRow]);
    } catch (e) {
      console.error(`Cannot parse "${QueryParams.DMN_RUNNER_FORM_INPUTS}"`, e);
    } finally {
      history.replace({
        pathname: routes.editor.path({ extension: "dmn" }),
        search: routes.editor.queryArgs(queryParams).without(QueryParams.DMN_RUNNER_FORM_INPUTS).toString(),
      });
    }
  }, [jsonSchema, history, routes, queryParams, setInputRows, props.workspaceFile]);

  const prevKieSandboxExtendedServicesStatus = usePrevious(extendedServices.status);
  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (
      extendedServices.status === KieSandboxExtendedServicesStatus.STOPPED ||
      extendedServices.status === KieSandboxExtendedServicesStatus.NOT_RUNNING
    ) {
      setExpanded(false);
    }
  }, [prevKieSandboxExtendedServicesStatus, extendedServices.status, props.workspaceFile.extension]);

  const dmnRunnerDispatch = useMemo(
    () => ({
      preparePayload,
      setCurrentInputRowIndex,
      setExpanded,
      setError,
      setInputRows,
      setMode,
    }),
    [preparePayload, setInputRows]
  );

  const dmnRunnerState = useMemo(
    () => ({
      currentInputRowIndex,
      error,
      inputRows,
      isExpanded,
      isVisible,
      jsonSchema,
      mode,
      status,
    }),
    [currentInputRowIndex, error, inputRows, isExpanded, isVisible, jsonSchema, mode, status]
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
