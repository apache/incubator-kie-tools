/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import {
  ChannelType,
  EditorEnvelopeLocator,
  EnvelopeContentType,
  EnvelopeMapping,
} from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, useEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { basename, extname } from "path";
import * as React from "react";
import { useCallback, useMemo, useState } from "react";
import "./App.scss";
import { YardEmptyState } from "./EmptyState";
import { MenuButtons } from "./MenuButtons";

export type YardFileType = "json" | "yml" | "yaml";

export const App = () => {
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();
  const { editor, editorRef } = useEditorRef();

  const editorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "yard",
          filePathGlob: "**/*.yard.+(yml|yaml|json)",
          resourcesPathPrefix: "",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "yard-editor-envelope.html" },
        }),
      ]),
    []
  );

  const onUndo = useCallback(async () => {
    editor?.undo();
  }, [editor]);

  const onRedo = useCallback(async () => {
    editor?.redo();
  }, [editor]);

  const onGetContent = useCallback(async () => editor?.getContent() ?? "", [editor]);

  const onSetTheme = useCallback(
    async (theme) => {
      editor?.setTheme(theme);
    },
    [editor]
  );

  const onValidate = useCallback(async () => {
    if (!editor) {
      return;
    }

    const notifications = await editor.validate();
    window.alert(JSON.stringify(notifications, undefined, 2));
  }, [editor]);

  const onSetContent = useCallback((path: string, content: string) => {
    const match = /\.sw\.(json|yml|yaml)$/.exec(path.toLowerCase());
    const dotExtension = match ? match[0] : extname(path);
    const extension = dotExtension.slice(1);
    const fileName = basename(path);

    setEmbeddedEditorFile({
      path: path,
      getFileContents: async () => content,
      isReadOnly: false,
      fileExtension: extension,
      fileName: fileName,
    });
  }, []);

  const onNewContent = useCallback(
    (type: YardFileType) => {
      onSetContent(`new-document.yard.${type}`, "");
    },
    [onSetContent]
  );

  return (
    <Page>
      {!embeddedEditorFile && (
        <PageSection isFilled={true}>
          <YardEmptyState newContent={onNewContent} setContent={onSetContent} />
        </PageSection>
      )}

      {embeddedEditorFile && (
        <>
          <PageSection padding={{ default: "noPadding" }}>
            <MenuButtons undo={onUndo} redo={onRedo} get={onGetContent} setTheme={onSetTheme} validate={onValidate} />
          </PageSection>
          <PageSection padding={{ default: "noPadding" }} isFilled={true} hasOverflowScroll={false}>
            <div className="editor-container">
              {embeddedEditorFile && (
                <EmbeddedEditor
                  ref={editorRef}
                  file={embeddedEditorFile}
                  channelType={ChannelType.ONLINE}
                  editorEnvelopeLocator={editorEnvelopeLocator}
                  locale={"en"}
                />
              )}
            </div>
          </PageSection>
        </>
      )}
    </Page>
  );
};
