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
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { useMemo, useState } from "react";
import { Page, PageHeader, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { EmbeddedEditor } from "@kie-tools-core/editor/dist/embedded";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { Base64PngGallery } from "./Base64PngGallery";

/**
 * The Base64 PNG Editor Page which contains the Base64 PNG Editor and a Gallery to open samples on the editor.
 * @constructor
 */
export function Base64PngEditorPage() {
  /**
   * State that handles the file. It's important to type with the File type of the @kie-tools/dist/embedded.
   * It's initialized with an empty file with the base64png extension. The file is used by the EmbeddedEditor to set the content on the Editor. Updating the file will trigger a re-render on the Editor because the EmbeddedEditor will set updated content on the Editor.
   */
  const [file, setFile] = useState<EmbeddedEditorFile>({
    fileName: "new-file",
    fileExtension: "base64png",
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
    normalizedPosixPathRelativeToTheWorkspaceRoot: "new-file.base64png",
  });

  /**
   * The editor envelope locator informs the EmbeddedEditor what file extension the Editor can open, and it maps to the respective envelope path and the Editor resources (like CSS, icons, etc).
   */
  const editorEnvelopeLocator: EditorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "base64png",
          filePathGlob: "**/*.base64png",
          resourcesPathPrefix: "",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "base64png-editor-envelope.html" },
        }),
      ]),
    []
  );

  return (
    <Page
      header={<PageHeader logo={<Brand src={"logo.png"} alt="Logo" />}></PageHeader>}
      sidebar={<Base64PngGallery setFile={setFile} />}
    >
      <PageSection padding={{ default: "noPadding" }}>
        <EmbeddedEditor
          file={file}
          editorEnvelopeLocator={editorEnvelopeLocator}
          channelType={ChannelType.EMBEDDED}
          locale={"en"}
        />
      </PageSection>
    </Page>
  );
}
