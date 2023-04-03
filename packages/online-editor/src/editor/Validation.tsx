import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useEffect } from "react";
import { EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { useOnlineI18n } from "../i18n";
import { ExtendedServicesModelPayload } from "@kie-tools/extended-services-api";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { DmnLanguageService, DmnLanguageServiceImportedModel } from "@kie-tools/dmn-language-service";
import { WorkspacesContextType } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

export function useFileValidation(
  workspaces: WorkspacesContextType,
  workspaceFile: WorkspaceFile | undefined,
  editorPageDock: EditorPageDockDrawerRef | undefined,
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

    if (extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      editorPageDock?.setNotifications(i18n.terms.validation, "", []);
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
          editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
        });
      })
      .catch((err) => console.error(err));
  }, [workspaceFile, editorPageDock, extendedServices.status, extendedServices.client, i18n.terms.validation]);

  // DMN validation
  useEffect(() => {
    if (!workspaceFile) {
      return;
    }
    if (workspaceFile.extension.toLocaleLowerCase() !== "dmn") {
      return;
    }

    if (extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      editorPageDock?.setNotifications(i18n.terms.validation, "", []);
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
              editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
            });
          });
      })
      .catch((err) => console.error(err));
  }, [
    workspaces,
    workspaceFile,
    editorPageDock,
    dmnLanguageService,
    extendedServices.status,
    extendedServices.client,
    i18n.terms.validation,
  ]);
}
