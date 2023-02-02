import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useEffect } from "react";
import { EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { useOnlineI18n } from "../i18n";
import { KieSandboxExtendedServicesModelPayload } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";

export function useFileValidation(
  workspaceFile: WorkspaceFile | undefined,
  editorPageDock: EditorPageDockDrawerRef | undefined
) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();

  useEffect(() => {
    if (!workspaceFile) {
      return;
    }
    if (
      workspaceFile.extension.toLocaleLowerCase() !== "bpmn" &&
      workspaceFile.extension.toLocaleLowerCase() !== "bpmn2" &&
      workspaceFile.extension.toLocaleLowerCase() !== "dmn"
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
        const payload: KieSandboxExtendedServicesModelPayload = {
          mainURI: workspaceFile.relativePath,
          resources: [
            {
              URI: workspaceFile.relativePath,
              content: decoder.decode(fileContents),
            },
          ],
        };

        if (workspaceFile.extension.toLowerCase() === "bpmn" || workspaceFile.extension.toLowerCase() === "bpmn2") {
          extendedServices.client.validateBpmn(payload).then((validationResults) => {
            const notifications: Notification[] = validationResults.map((validationResult: any) => ({
              type: "PROBLEM",
              path: "",
              severity: "ERROR",
              message: validationResult,
            }));
            editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
          });
        }
        if (workspaceFile.extension.toLowerCase() === "dmn") {
          extendedServices.client.validateDmn(payload).then((validationResults) => {
            const notifications: Notification[] = validationResults.map((validationResult: any) => ({
              type: "PROBLEM",
              path: "",
              severity: validationResult.severity,
              message: `${validationResult.messageType}: ${validationResult.message}`,
            }));
            editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
          });
        }
      })
      .catch((err) => console.error(err));
  }, [workspaceFile, editorPageDock, extendedServices.status, extendedServices.client, i18n.terms.validation]);
}
