/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useEffect } from "react";
import { useOnlineI18n } from "../i18n";
import { ExtendedServicesModelPayload } from "@kie-tools/extended-services-api";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
import { DmnLanguageService, DmnLanguageServiceImportedModel } from "@kie-tools/dmn-language-service";
import { WorkspacesContextType } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

export function useFileValidation(
  workspaces: WorkspacesContextType,
  workspaceFile: WorkspaceFile | undefined,
  setNotifications: (tabName: string, path: string, notifications: Notification[]) => void,
  dmnLanguageService?: DmnLanguageService
) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();

  // BPMN validation
  useEffect(() => {
    if (!workspaceFile) {
      return;
    }
    if (
      workspaceFile.extension.toLocaleLowerCase() !== "bpmn" &&
      workspaceFile.extension.toLocaleLowerCase() !== "bpmn2"
    ) {
      return;
    }

    if (extendedServices.status !== ExtendedServicesStatus.RUNNING) {
      setNotifications(i18n.terms.validation, "", []);
      return;
    }

    workspaceFile
      .getFileContents()
      .then((fileContents) => {
        const payload: ExtendedServicesModelPayload = {
          mainURI: workspaceFile.relativePath,
          resources: [
            {
              URI: workspaceFile.relativePath,
              content: decoder.decode(fileContents),
            },
          ],
        };

        extendedServices.client.validateBpmn(payload).then((validationResults) => {
          const notifications: Notification[] = validationResults.map((validationResult: any) => ({
            type: "PROBLEM",
            path: "",
            severity: "ERROR",
            message: validationResult,
          }));
          setNotifications(i18n.terms.validation, "", notifications);
        });
      })
      .catch((err) => console.error(err));
  }, [workspaceFile, setNotifications, extendedServices.status, extendedServices.client, i18n.terms.validation]);

  // DMN validation
  useEffect(() => {
    if (!workspaceFile) {
      return;
    }
    if (workspaceFile.extension.toLocaleLowerCase() !== "dmn") {
      return;
    }

    if (extendedServices.status !== ExtendedServicesStatus.RUNNING) {
      setNotifications(i18n.terms.validation, "", []);
      return;
    }

    workspaces
      .getFileContent({
        workspaceId: workspaceFile.workspaceId,
        relativePath: workspaceFile.relativePath,
      })
      .then((fileContent) => {
        const decodedFileContent = decoder.decode(fileContent);
        dmnLanguageService
          ?.getAllImportedModelsResources([decodedFileContent])
          .then((importedModelsResources: DmnLanguageServiceImportedModel[]) => {
            const resources = [
              { content: decodedFileContent, relativePath: workspaceFile.relativePath },
              ...importedModelsResources,
            ];
            const payload: ExtendedServicesModelPayload = {
              mainURI: workspaceFile.relativePath,
              resources: resources.map((resource) => ({
                URI: resource.relativePath,
                content: resource.content ?? "",
              })),
            };

            extendedServices.client.validateDmn(payload).then((validationResults) => {
              const notifications: Notification[] = validationResults.map((validationResult) => {
                let path = payload.resources.length > 1 ? validationResult.path : "";
                if (
                  validationResult.severity === "ERROR" &&
                  validationResult.sourceId === null &&
                  validationResult.messageType === "REQ_NOT_FOUND"
                ) {
                  const nodeId = validationResult.message.split("'")[1] ?? "";
                  path = dmnLanguageService.getPathFromNodeId(resources, nodeId);
                }
                return {
                  type: "PROBLEM",
                  path,
                  severity: validationResult.severity,
                  message: `${validationResult.messageType}: ${validationResult.message}`,
                };
              });
              setNotifications(i18n.terms.validation, "", notifications);
            });
          });
      })
      .catch((err) => console.error(err));
  }, [
    workspaces,
    workspaceFile,
    setNotifications,
    dmnLanguageService,
    extendedServices.status,
    extendedServices.client,
    i18n.terms.validation,
  ]);
}
