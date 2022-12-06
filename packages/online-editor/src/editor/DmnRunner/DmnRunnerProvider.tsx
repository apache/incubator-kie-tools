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
import { DmnRunnerModelPayload, DmnRunnerService, DmnRunnerModelResource } from "./DmnRunnerService";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { QueryParams } from "../../navigation/Routes";
import { jsonParseWithDate } from "../../json/JsonParse";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useOnlineI18n } from "../../i18n";
import { useQueryParams } from "../../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { DmnSchema, InputRow } from "@kie-tools/form-dmn";
import { useSettings } from "../../settings/SettingsContext";
import { useDmnRunnerInputs } from "../../dmnRunnerInputs/DmnRunnerInputsHook";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { DmnLanguageService } from "@kie-tools/dmn-language-service/src";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspaceFile: WorkspaceFile;
}

export function DmnRunnerProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  const queryParams = useQueryParams();
  const history = useHistory();
  const routes = useRoutes();
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();
  const settings = useSettings();
  const {
    inputRows,
    setInputRows,
    didUpdateInputRows,
    setDidUpdateInputRows,
    didUpdateOutputRows,
    setDidUpdateOutputRows,
  } = useDmnRunnerInputs(props.workspaceFile);

  const [error, setError] = useState(false);
  const [jsonSchema, setJsonSchema] = useState<DmnSchema | undefined>(undefined);
  const [isExpanded, setExpanded] = useState(false);
  const [mode, setMode] = useState(DmnRunnerMode.FORM);
  const [currentInputRowIndex, setCurrentInputRowIndex] = useState<number>(0);
  const dmnLanguageService = useMemo(() => new DmnLanguageService(), []);

  const status = useMemo(() => {
    return isExpanded ? DmnRunnerStatus.AVAILABLE : DmnRunnerStatus.UNAVAILABLE;
  }, [isExpanded]);

  const service = useMemo(
    () => new DmnRunnerService(settings.kieSandboxExtendedServices.config.url.jitExecutor),
    [settings.kieSandboxExtendedServices.config]
  );

  const getImportedModels = useCallback(
    async (importedModels: (string | null)[], resources: DmnRunnerModelResource[]) => {
      if (importedModels.length > 0) {
        const filteredModels = importedModels.filter((impotedModel) => impotedModel !== null) as string[];
        const fileContents = await Promise.all(
          filteredModels.map((importedModel) => {
            return workspaces.getFileContent({
              workspaceId: props.workspaceFile.workspaceId,
              relativePath: importedModel,
            });
          }, [] as Array<Promise<Uint8Array>>)
        );
        const decodedContents = fileContents.map((fileContent) => decoder.decode(fileContent));

        // set resources
        filteredModels.forEach((filteredModel, index) => {
          resources.push({
            URI: filteredModel,
            content: decodedContents[index],
          });
        });

        const importedFiles = decodedContents.flatMap((content) => dmnLanguageService.getImportedModels(content));
        await getImportedModels(importedFiles, resources);
      }
      return resources;
    },
    [workspaces, props.workspaceFile.workspaceId, dmnLanguageService]
  );

  const preparePayload = useCallback(
    async (formData?: InputRow) => {
      const currentFile = await workspaces.getFileContent({
        workspaceId: props.workspaceFile.workspaceId,
        relativePath: props.workspaceFile.relativePath,
      });

      const currentContentFile = decoder.decode(currentFile);
      const importedModels = dmnLanguageService.getImportedModels(currentContentFile);
      const resources = [
        {
          URI: props.workspaceFile.relativePath,
          content: currentContentFile,
        },
      ];
      const importedModelsResources = await getImportedModels(importedModels, resources);

      return {
        mainURI: props.workspaceFile.relativePath,
        resources: importedModelsResources,
        context: formData,
      } as DmnRunnerModelPayload;
    },
    [props.workspaceFile, workspaces, dmnLanguageService, getImportedModels]
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
        service.formSchema(payload).then((jsonSchema) => {
          setJsonSchema(jsonSchema);
        });
      })
      .catch((err) => {
        console.error(err);
        setError(true);
      });
  }, [extendedServices.status, props.workspaceFile.extension, preparePayload, service]);

  useEffect(() => {
    if (props.workspaceFile.extension !== "dmn") {
      return;
    }

    if (extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      props.editorPageDock?.setNotifications(i18n.terms.validation, "", []);
      return;
    }

    props.workspaceFile
      .getFileContents()
      .then((fileContents) => {
        const payload: DmnRunnerModelPayload = {
          mainURI: props.workspaceFile.relativePath,
          resources: [
            {
              URI: props.workspaceFile.relativePath,
              content: decoder.decode(fileContents),
            },
          ],
        };

        service.validate(payload).then((validationResults) => {
          const notifications: Notification[] = validationResults.map((validationResult: any) => ({
            type: "PROBLEM",
            path: "",
            severity: validationResult.severity,
            message: `${validationResult.messageType}: ${validationResult.message}`,
          }));
          props.editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
        });
      })
      .catch((err) => console.error(err));
  }, [props.workspaceFile, props.editorPageDock, extendedServices.status, service, i18n.terms.validation]);

  useEffect(() => {
    if (!jsonSchema || !queryParams.has(QueryParams.DMN_RUNNER_FORM_INPUTS)) {
      return;
    }

    try {
      setInputRows([jsonParseWithDate(queryParams.get(QueryParams.DMN_RUNNER_FORM_INPUTS)!) as InputRow]);
      setExpanded(true);
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
      prevKieSandboxExtendedServicesStatus &&
      prevKieSandboxExtendedServicesStatus !== KieSandboxExtendedServicesStatus.AVAILABLE &&
      prevKieSandboxExtendedServicesStatus !== KieSandboxExtendedServicesStatus.RUNNING &&
      extendedServices.status === KieSandboxExtendedServicesStatus.RUNNING
    ) {
      setExpanded(true);
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
      setDidUpdateInputRows,
      setDidUpdateOutputRows,
      setMode,
    }),
    [preparePayload, setDidUpdateInputRows, setDidUpdateOutputRows, setInputRows]
  );

  const dmnRunnerState = useMemo(
    () => ({
      currentInputRowIndex,
      error,
      inputRows,
      didUpdateInputRows,
      isExpanded,
      jsonSchema,
      mode,
      didUpdateOutputRows,
      service,
      status,
    }),
    [
      currentInputRowIndex,
      didUpdateInputRows,
      didUpdateOutputRows,
      error,
      inputRows,
      isExpanded,
      jsonSchema,
      mode,
      service,
      status,
    ]
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
