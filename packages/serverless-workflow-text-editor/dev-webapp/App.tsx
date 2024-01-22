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

import {
  ChannelType,
  EditorEnvelopeLocator,
  EditorTheme,
  EnvelopeContentType,
  EnvelopeMapping,
} from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile, StateControl } from "@kie-tools-core/editor/dist/channel";
import {
  EmbeddedEditor,
  EmbeddedEditorChannelApiImpl,
  useDirtyState,
  useEditorRef,
} from "@kie-tools-core/editor/dist/embedded";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { basename, extname } from "path";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { DiagnosticSeverity } from "vscode-languageserver-types";
import "./App.scss";
import { ServerlessWorkflowEmptyState } from "./EmptyState";
import { HistoryButtons } from "./HistoryButtons";
import { DevWebAppSwfLanguageService } from "./channel/DevWebAppSwfLanguageService";
import { SwfTextEditorChannelApiImpl } from "./channel/SwfTextEditorChannelApiImpl";
import { SwfLanguageServiceChannelApiImpl } from "./channel/SwfLanguageServiceChannelApiImpl";

export type ServerlessWorkflowType = "json" | "yml" | "yaml";

const LOCALE = "en";

export const App = () => {
  const { editor, editorRef } = useEditorRef();
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const [isReady, setReady] = useState(false);
  const isDirty = useDirtyState(editor);

  const stateControl = useMemo(() => new StateControl(), [embeddedEditorFile?.getFileContents]);

  const editorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "swf",
          filePathGlob: "**/*.sw.+(json|yml|yaml)",
          resourcesPathPrefix: "",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "serverless-workflow-text-editor-envelope.html" },
        }),
      ]),
    []
  );

  useEffect(() => {
    if (embeddedEditorFile && !isReady) {
      setReady(true);
    }
  }, [embeddedEditorFile, isReady]);

  const swfLanguageService = useMemo(() => {
    if (!embeddedEditorFile) {
      return;
    }

    const devWebAppSwfLanguageService = new DevWebAppSwfLanguageService();
    return devWebAppSwfLanguageService.getLs(embeddedEditorFile.normalizedPosixPathRelativeToTheWorkspaceRoot!);
  }, [embeddedEditorFile]);

  const apiImpl = useMemo(() => {
    if (!embeddedEditorFile || !swfLanguageService) {
      return;
    }

    const defaultApiImpl = new EmbeddedEditorChannelApiImpl(stateControl, embeddedEditorFile, LOCALE, {
      kogitoEditor_ready: () => {
        setReady(true);
      },
    });

    const swfLanguageServiceChannelApiImpl = new SwfLanguageServiceChannelApiImpl(swfLanguageService);

    return new SwfTextEditorChannelApiImpl({ defaultApiImpl, swfLanguageServiceChannelApiImpl });
  }, [embeddedEditorFile, stateControl, swfLanguageService]);

  const onUndo = useCallback(async () => {
    editor?.undo();
  }, [editor]);

  const onRedo = useCallback(async () => {
    editor?.redo();
  }, [editor]);

  const onDownload = useCallback(async () => {
    if (!editor || !embeddedEditorFile || !downloadRef.current) {
      return;
    }

    const content = await editor.getContent();
    const fileBlob = new Blob([content], { type: "text/plain" });

    downloadRef.current.href = URL.createObjectURL(fileBlob);
    downloadRef.current.setAttribute("download", embeddedEditorFile.fileName);
    downloadRef.current.click();
  }, [editor, embeddedEditorFile]);

  const onValidate = useCallback(async () => {
    if (!editor || !swfLanguageService || !embeddedEditorFile) {
      return;
    }

    const content = await editor.getContent();
    const lsDiagnostics = await swfLanguageService.getDiagnostics({
      content: content,
      uriPath: embeddedEditorFile.normalizedPosixPathRelativeToTheWorkspaceRoot!,
    });

    const notifications = lsDiagnostics.map(
      (lsDiagnostic) =>
        ({
          normalizedPosixPathRelativeToTheWorkspaceRoot: "", // empty to not group them by path, as we're only validating one file.
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

    window.alert(JSON.stringify(notifications, undefined, 2));
  }, [editor, embeddedEditorFile, swfLanguageService]);

  const onSetContent = useCallback((normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) => {
    const match = /\.sw\.(json|yml|yaml)$/.exec(normalizedPosixPathRelativeToTheWorkspaceRoot.toLowerCase());
    const dotExtension = match ? match[0] : extname(normalizedPosixPathRelativeToTheWorkspaceRoot);
    const extension = dotExtension.slice(1);
    const fileName = basename(normalizedPosixPathRelativeToTheWorkspaceRoot);

    setEmbeddedEditorFile({
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      getFileContents: async () => content,
      isReadOnly: false,
      fileExtension: extension,
      fileName: fileName,
    });
  }, []);

  const onNewContent = useCallback(
    (type: ServerlessWorkflowType) => {
      onSetContent(`new-document.sw.${type}`, "");
    },
    [onSetContent]
  );

  const onSetTheme = useCallback(
    async (theme: EditorTheme) => {
      editor?.setTheme(theme);
    },
    [editor]
  );

  return (
    <Page>
      {!embeddedEditorFile && (
        <PageSection isFilled={true}>
          <ServerlessWorkflowEmptyState newContent={onNewContent} setContent={onSetContent} />
        </PageSection>
      )}

      {embeddedEditorFile && (
        <>
          <PageSection padding={{ default: "noPadding" }}>
            <HistoryButtons
              undo={onUndo}
              redo={onRedo}
              download={onDownload}
              validate={onValidate}
              isDirty={isDirty}
              setTheme={onSetTheme}
            />
          </PageSection>
          <PageSection padding={{ default: "noPadding" }} isFilled={true} hasOverflowScroll={false}>
            <div className="editor-container">
              {embeddedEditorFile && (
                <EmbeddedEditor
                  ref={editorRef}
                  file={embeddedEditorFile}
                  channelType={ChannelType.ONLINE}
                  editorEnvelopeLocator={editorEnvelopeLocator}
                  locale={LOCALE}
                  customChannelApiImpl={apiImpl}
                  stateControl={stateControl}
                  isReady={isReady}
                />
              )}
            </div>
            <a ref={downloadRef} />
          </PageSection>
        </>
      )}
    </Page>
  );
};
