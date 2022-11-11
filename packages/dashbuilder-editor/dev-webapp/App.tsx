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
  EditorTheme,
  EnvelopeContentType,
  EnvelopeMapping,
} from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, useEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import * as React from "react";
import { useMemo, useState, useEffect } from "react";
import "./App.scss";

export type ServerlessWorkflowType = "yml" | "yaml";

const FILE = {
  fileName: "test.dash.yaml",
  fileExtension: "dash.yaml",
  getFileContents: function (): Promise<string | undefined> {
    return Promise.resolve("");
  },
  isReadOnly: false,
};

export const App = () => {
  const { editor, editorRef } = useEditorRef();
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>(FILE);

  useEffect(() => {
    setEmbeddedEditorFile(FILE);
  }, []);

  const editorEnvelopeLocator = useMemo(
    () =>
      new EditorEnvelopeLocator(window.location.origin, [
        new EnvelopeMapping({
          type: "dash",
          filePathGlob: "**/*.dash.+(yml|yaml)",
          resourcesPathPrefix: "",
          envelopeContent: { type: EnvelopeContentType.PATH, path: "dashbuilder-editor-envelope.html" },
        }),
      ]),
    []
  );

  return (
    <Page>
      {
        <>
          <PageSection padding={{ default: "noPadding" }} isFilled={true} hasOverflowScroll={false}>
            <div className="editor-container">
              <EmbeddedEditor
                channelType={ChannelType.ONLINE}
                editorEnvelopeLocator={editorEnvelopeLocator}
                locale={"en"}
                ref={editorRef}
                file={embeddedEditorFile}
              />
            </div>
          </PageSection>
        </>
      }
    </Page>
  );
};
