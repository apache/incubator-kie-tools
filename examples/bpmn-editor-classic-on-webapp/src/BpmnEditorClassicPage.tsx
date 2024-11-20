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

import * as React from "react";
import { ChannelType, EnvelopeContentType } from "@kie-tools-core/editor/dist/api";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { useMemo, useState } from "react";
import { EmbeddedEditor, useEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Page, PageSection, PageHeader } from "@patternfly/react-core/dist/js/components/Page";
import { Sidebar } from "./Sidebar";

export function BpmnEditorClassicPage() {
  /**
   * The reference of the Editor. It allows us to access/modify the Editor properties imperatively.
   */
  const { editor, editorRef } = useEditorRef();

  /**
   * State that handles the file. It's important to type with the File type of the @kie-tools/dist/embedded.
   * It's initialized with an empty file with the bpmn extension. The file is used by the EmbeddedEditor to set the content on the Editor. Updating the file will trigger a re-render on the Editor because the EmbeddedEditor will set updated content on the Editor.
   */
  const [file, setFile] = useState<EmbeddedEditorFile>({
    fileName: "new-file",
    fileExtension: "bpmn",
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
    normalizedPosixPathRelativeToTheWorkspaceRoot: "new-file.bpmn",
  });

  /**
   * The Editor envelope locator informs the EmbeddedEditor what file extension the Editor can open, and it maps to the respective envelope path and the Editor resources (like CSS, icons, etc).
   * On this example, we're using the envelope located on the bpmn.new page.
   */
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(() => {
    return new EditorEnvelopeLocator(window.location.origin, [
      new EnvelopeMapping({
        type: "bpmn",
        filePathGlob: "**/*.bpmn?(2)",
        resourcesPathPrefix: "../bpmn-editor-classic/",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "bpmn-editor-classic-envelope.html" },
      }),
    ]);
  }, []);

  return (
    <Page
      header={<PageHeader logo={<Brand src={"logo.png"} alt="Logo" />} />}
      sidebar={
        <Sidebar
          editor={editor}
          editorEnvelopeLocator={editorEnvelopeLocator}
          file={file}
          setFile={setFile}
          fileExtension={"bpmn"}
          accept={".bpmn"}
        />
      }
    >
      <PageSection padding={{ default: "noPadding" }}>
        <EmbeddedEditor
          ref={editorRef}
          file={file}
          editorEnvelopeLocator={editorEnvelopeLocator}
          channelType={ChannelType.EMBEDDED}
          locale={"en"}
        />
      </PageSection>
    </Page>
  );
}
