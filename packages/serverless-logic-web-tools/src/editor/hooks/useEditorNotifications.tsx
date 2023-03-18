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

import { useEffect, useState } from "react";
import { useAppI18n } from "../../i18n";
import { WebToolsEmbeddedEditorRef } from "../WebToolsEmbeddedEditor";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { DiagnosticSeverity } from "vscode-languageserver-types";

interface HookArgs {
  webToolsEditor: WebToolsEmbeddedEditorRef | undefined;
  content: string | undefined;
  fileRelativePath: string;
}

export function useEditorNotifications(args: HookArgs) {
  const { i18n } = useAppI18n();
  const { webToolsEditor, content, fileRelativePath } = { ...args };
  const [notifications, setNotifications] = useState<Notification[]>([]);

  useEffect(() => {
    if (!webToolsEditor?.isReady || !webToolsEditor?.languageService || content === undefined) {
      return;
    }

    webToolsEditor.languageService
      .getDiagnostics({
        content: content,
        uriPath: fileRelativePath,
      })
      .then((lsDiagnostics) => {
        const mappedDiagnostics = lsDiagnostics.map(
          (lsDiagnostic) =>
            ({
              path: "", // empty to not group them by path, as we're only validating one file.
              severity: lsDiagnostic.severity === DiagnosticSeverity.Error ? "ERROR" : "WARNING",
              message: `${lsDiagnostic.message} [Line ${lsDiagnostic.range.start.line + 1}]`,
              type: "PROBLEM",
              position: {
                startLineNumber: lsDiagnostic.range.start.line + 1,
                startColumn: lsDiagnostic.range.start.character + 1,
                endLineNumber: lsDiagnostic.range.end.line + 1,
                endColumn: lsDiagnostic.range.end.character + 1,
              },
            } as Notification)
        );
        setNotifications(mappedDiagnostics);
      })
      .catch((e) => console.error(e));
  }, [content, fileRelativePath, webToolsEditor, i18n]);

  return notifications;
}
