import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { useEffect, useMemo } from "react";
import { EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { useOnlineI18n } from "../i18n";
import { useSettings } from "../settings/SettingsContext";
import {
  KieSandboxExtendedServicesClient,
  KieSandboxExtendedServicesModelPayload,
} from "../kieSandboxExtendedServices/KieSandboxExtendedServicesClient";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";

export function useFileValidation(
  workspaceFile: WorkspaceFile | undefined,
  editorPageDock: EditorPageDockDrawerRef | undefined
) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();
  const settings = useSettings();

  const service = useMemo(
    () => new KieSandboxExtendedServicesClient(settings.kieSandboxExtendedServices.config.url.jitExecutor),
    [settings.kieSandboxExtendedServices.config]
  );

  useEffect(() => {
    if (!workspaceFile) {
      return;
    }
    if (
      workspaceFile.extension.toLocaleLowerCase() !== "bpmn" &&
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

        if (workspaceFile.extension.toLowerCase() === "bpmn") {
          service.validateBpmn(payload).then((validationResults) => {
            const notifications: Notification[] = validationResults.map((validationResult: any) => ({
              type: "PROBLEM",
              path: payload.mainURI,
              severity: "ERROR",
              message: validationResult,
            }));
            editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
          });
        }
        if (workspaceFile.extension.toLowerCase() === "dmn") {
          service.validateDmn(payload).then((validationResults) => {
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
  }, [workspaceFile, editorPageDock, extendedServices.status, service, i18n.terms.validation]);
}
