/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useCallback } from "react";
import { useOnlineI18n } from "../i18n";
import { ExtendedServicesModelPayload } from "@kie-tools/extended-services-api";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { WorkspacesContextType } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";

export function useFileValidation(
  workspaces: WorkspacesContextType,
  workspaceFile: WorkspaceFile | undefined,
  setNotifications: (tabName: string, path: string, notifications: Notification[]) => void,
  dmnLanguageService?: DmnLanguageService
) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();

  // BPMN validation
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
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
            if (canceled.get()) {
              return;
            }
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
              if (canceled.get()) {
                return;
              }
              const notifications: Notification[] = validationResults.map((validationResult: any) => ({
                type: "PROBLEM",
                normalizedPosixPathRelativeToTheWorkspaceRoot: "",
                severity: "ERROR",
                message: validationResult,
              }));
              setNotifications(i18n.terms.validation, "", notifications);
            });
          })
          .catch((err) => console.error(err));
      },
      [workspaceFile, setNotifications, extendedServices.status, extendedServices.client, i18n.terms.validation]
    )
  );

  // DMN validation
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
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
            if (canceled.get()) {
              return;
            }

            const decodedFileContent = decoder.decode(fileContent);
            const dmnSpecVersion = dmnLanguageService?.getSpecVersion(decodedFileContent);
            if (
              !dmnSpecVersion ||
              (dmnSpecVersion !== "1.0" &&
                dmnSpecVersion !== "1.1" &&
                dmnSpecVersion !== "1.2" &&
                dmnSpecVersion !== "1.3" &&
                dmnSpecVersion !== "1.4" &&
                dmnSpecVersion !== "1.5")
            ) {
              setNotifications(i18n.terms.validation, "", [
                {
                  type: "ALERT",
                  normalizedPosixPathRelativeToTheWorkspaceRoot: "",
                  severity: "WARNING",
                  message:
                    "Validation doesn't support this DMN version" + dmnSpecVersion ? "(" + dmnSpecVersion + ")" : "",
                },
              ]);
              return;
            }

            dmnLanguageService
              ?.buildImportIndex([
                {
                  content: decodedFileContent,
                  normalizedPosixPathRelativeToTheWorkspaceRoot: workspaceFile.relativePath,
                },
              ])
              .then((importIndex) => {
                if (canceled.get()) {
                  return;
                }

                const allImportedModelResources = [...importIndex.models.entries()].map(
                  ([normalizedPosixPathRelativeToTheWorkspaceRoot, model]) => ({
                    content: model.xml,
                    normalizedPosixPathRelativeToTheWorkspaceRoot,
                  })
                );

                const payload: ExtendedServicesModelPayload = {
                  mainURI: workspaceFile.relativePath,
                  resources: allImportedModelResources.map((resource) => ({
                    URI: resource.normalizedPosixPathRelativeToTheWorkspaceRoot,
                    content: resource.content,
                  })),
                };

                extendedServices.client.validateDmn(payload).then((validationResults) => {
                  if (canceled.get()) {
                    return;
                  }
                  const notifications: Notification[] = validationResults.map((validationResult) => {
                    let path = payload.resources.length > 1 ? validationResult.path : "";
                    if (
                      validationResult.severity === "ERROR" &&
                      validationResult.sourceId === null &&
                      validationResult.messageType === "REQ_NOT_FOUND"
                    ) {
                      const nodeId = validationResult.message.split("'")[1] ?? "";
                      path = dmnLanguageService.getPathFromNodeId(allImportedModelResources, nodeId);
                    }
                    return {
                      type: "PROBLEM",
                      normalizedPosixPathRelativeToTheWorkspaceRoot: path,
                      severity: validationResult.severity,
                      message: `${validationResult.messageType}: ${validationResult.message}`,
                    };
                  });
                  setNotifications(i18n.terms.validation, "", notifications);
                });
              });
          })
          .catch((err) => console.error(err));
      },
      [
        workspaces,
        workspaceFile,
        setNotifications,
        dmnLanguageService,
        extendedServices.status,
        extendedServices.client,
        i18n.terms.validation,
      ]
    )
  );
}
