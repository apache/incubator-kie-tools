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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import type { Meta, StoryObj } from "@storybook/react";

import "@kie-tools-core/editor/dist/envelope/styles.scss";
import {
  EditorEnvelopeI18nContext,
  editorEnvelopeI18nDefaults,
  editorEnvelopeI18nDictionaries,
} from "@kie-tools-core/editor/dist/envelope/i18n";
import {
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  KogitoEditorEnvelopeContext,
  KogitoEditorEnvelopeContextType,
} from "@kie-tools-core/editor/dist/api";
import { DefaultKeyboardShortcutsService } from "@kie-tools-core/keyboard-shortcuts/dist/envelope/DefaultKeyboardShortcutsService";
import { KeyBindingsHelpOverlay } from "@kie-tools-core/editor/dist/envelope/KeyBindingsHelpOverlay";
import { ResourceContent, ResourcesList, WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { I18nService } from "@kie-tools-core/i18n/dist/envelope";
import { getOperatingSystem } from "@kie-tools-core/operating-system";

import { ns as dmn15ns } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/meta";
import { DMN15_SPEC } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/Dmn15Spec";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { DmnEditorRoot, DmnEditorRootProps } from "../../src/DmnEditorRoot";

const emptyDmn = `<?xml version="1.0" encoding="UTF-8"?>
<definitions
  xmlns="${dmn15ns.get("")}"
  expressionLanguage="${DMN15_SPEC.expressionLanguage.default}"
  namespace="https://kie.org/dmn/${generateUuid()}"
  id="${generateUuid()}"
  name="DMN${generateUuid()}">
</definitions>`;

export type StorybookDmnEditorRootProps = DmnEditorRootProps & {
  initialContent: string;
  showKeyBindingsOverlay: boolean;
};

function DevWebApp(args: StorybookDmnEditorRootProps) {
  const editorRef = useRef<DmnEditorRoot>();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (editorRef.current) {
      editorRef.current.setContent("example.dmn", args.initialContent ?? emptyDmn);
      setLoading(false);
    }
  }, [args.initialContent]);

  const envelopeContext = useMemo<KogitoEditorEnvelopeContextType<any, any>>(() => {
    return {
      shared: {} as any,
      channelApi: { shared: {} as any } as any,
      services: {
        keyboardShortcuts: new DefaultKeyboardShortcutsService({ os: getOperatingSystem() }),
        i18n: new I18nService(),
      },
      supportedThemes: [],
    };
  }, []);

  const onNewEdit = useCallback(
    (workspaceEdit: WorkspaceEdit) => {
      envelopeContext?.channelApi.notifications.kogitoWorkspace_newEdit.send(workspaceEdit);
    },
    [envelopeContext]
  );

  const onRequestWorkspaceFileContent = useCallback(
    async (resource: ResourceContent) => {
      return envelopeContext?.channelApi.requests.kogitoWorkspace_resourceContentRequest(resource);
    },
    [envelopeContext]
  );

  const onRequestWorkspaceFilesList = useCallback(
    async (resource: ResourcesList) => {
      return (
        envelopeContext?.channelApi.requests.kogitoWorkspace_resourceListRequest(resource) ?? new ResourcesList("", [])
      );
    },
    [envelopeContext]
  );

  const onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot = useCallback(
    (normalizedPosixPathRelativeToTheWorkspaceRoot: string) => {
      envelopeContext?.channelApi.notifications.kogitoWorkspace_openFile.send(
        normalizedPosixPathRelativeToTheWorkspaceRoot
      );
    },
    [envelopeContext]
  );

  return (
    <div style={{ position: "absolute", width: "100vw", height: "100vh", top: "0", left: "0" }}>
      <KogitoEditorEnvelopeContext.Provider value={envelopeContext}>
        <I18nDictionariesProvider
          defaults={editorEnvelopeI18nDefaults}
          dictionaries={editorEnvelopeI18nDictionaries}
          ctx={EditorEnvelopeI18nContext}
          initialLocale={navigator.language}
        >
          {!loading && <KeyBindingsHelpOverlay />}
          <DmnEditorRoot
            {...args}
            exposing={(dmnEditorRoot) => (editorRef.current = dmnEditorRoot)}
            onNewEdit={onNewEdit}
            workspaceRootAbsolutePosixPath={
              args.workspaceRootAbsolutePosixPath ?? DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH
            }
            onRequestWorkspaceFileContent={onRequestWorkspaceFileContent}
            onRequestWorkspaceFilesList={onRequestWorkspaceFilesList}
            onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot={
              onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot
            }
            keyboardShortcutsService={envelopeContext?.services.keyboardShortcuts}
            isEvaluationHighlightsSupported={args.isEvaluationHighlightsSupported ?? false}
            isReadOnly={args.isReadOnly}
          />
        </I18nDictionariesProvider>
      </KogitoEditorEnvelopeContext.Provider>
    </div>
  );
}

const meta: Meta<typeof DevWebApp> = {
  title: "Dev/Web App",
  component: DevWebApp,
};

export default meta;
type Story = StoryObj<typeof DevWebApp>;

export const WebApp: Story = {
  render: (args) => <DevWebApp {...args} />,
  args: {
    initialContent: emptyDmn,
    workspaceRootAbsolutePosixPath: "/",
    isEvaluationHighlightsSupported: true,
    isReadOnly: false,
  },
};
