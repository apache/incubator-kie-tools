/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { PropsWithChildren, useEffect, useMemo } from "react";
import { EditorPageDockDrawerRef } from "../EditorPageDockDrawer";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useOnlineI18n } from "../../i18n";
import { useExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { useSettings } from "../../settings/SettingsContext";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { BpmnRunnerModelPayload, BpmnRunnerService } from "./BpmnRunnerService";

interface Props {
  editorPageDock: EditorPageDockDrawerRef | undefined;
  workspaceFile: WorkspaceFile;
}

export function BpmnRunnerProvider(props: PropsWithChildren<Props>) {
  const { i18n } = useOnlineI18n();
  const extendedServices = useExtendedServices();
  const settings = useSettings();

  const service = useMemo(
    () => new BpmnRunnerService(settings.kieSandboxExtendedServices.config.url.jitExecutor),
    [settings.kieSandboxExtendedServices.config]
  );

  useEffect(() => {
    if (props.workspaceFile.extension.toLocaleLowerCase() !== "bpmn") {
      return;
    }

    if (extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      props.editorPageDock?.setNotifications(i18n.terms.validation, "", []);
      return;
    }

    props.workspaceFile
      .getFileContents()
      .then((fileContents) => {
        const payload: BpmnRunnerModelPayload = {
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
            path: payload.mainURI,
            severity: "ERROR",
            message: validationResult,
          }));
          props.editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
        });
      })
      .catch((err) => console.error(err));
  }, [props.workspaceFile, props.editorPageDock, extendedServices.status, service, i18n.terms.validation]);

  return <>{props.children}</>;
}
