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
import { useCallback } from "react";
import {
  Editor,
  EditorFactory,
  EditorInitArgs,
  KogitoEditorEnvelopeContextType,
  KogitoEditorChannelApi,
  EditorTheme,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  KogitoEditorEnvelopeApi,
} from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { ResourceContent, ResourcesList, WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { TestScenarioEditorRoot } from "./TestScenarioEditorRoot";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import {
  scesimEditorEnvelopeDictionaries,
  ScesimEditorEnvelopeI18nContext,
  scesimEditorEnvelopeI18nDefaults,
  useScesimEditorEnvelopeI18n,
} from "./i18n";

export class TestScenarioEditorFactory
  implements EditorFactory<Editor, KogitoEditorEnvelopeApi, KogitoEditorChannelApi>
{
  public createEditor(
    envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>,
    initArgs: EditorInitArgs
  ): Promise<Editor> {
    return Promise.resolve(new TestScenarioEditorInterface(envelopeContext, initArgs));
  }
}

export class TestScenarioEditorInterface implements Editor {
  private self: TestScenarioEditorRoot;
  public af_isReact = true;
  public af_componentId: "scesim-editor";
  public af_componentTitle: "Test Scenario Editor";

  constructor(
    private readonly envelopeContext: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>,
    private readonly initArgs: EditorInitArgs
  ) {}

  // Not in-editor

  public getPreview(): Promise<string | undefined> {
    return this.self.getDiagramSvg();
  }

  public async validate(): Promise<Notification[]> {
    return Promise.resolve([]);
  }

  // Forwarding to the editor

  public async setTheme(theme: EditorTheme): Promise<void> {
    return Promise.resolve(); // No-op for now. The Test Scenario Editor only has the LIGHT theme.
  }

  public async undo(): Promise<void> {
    return this.self.undo();
  }

  public async redo(): Promise<void> {
    return this.self.redo();
  }

  public getContent(): Promise<string> {
    return this.self.getContent();
  }

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void> {
    return this.self.setContent(normalizedPosixPathRelativeToTheWorkspaceRoot, content);
  }

  // This is the argument to ReactDOM.render. These props can be understood like "static globals".
  public af_componentRoot() {
    return (
      <I18nDictionariesProvider
        defaults={scesimEditorEnvelopeI18nDefaults}
        dictionaries={scesimEditorEnvelopeDictionaries}
        initialLocale={this.initArgs.initialLocale}
        ctx={ScesimEditorEnvelopeI18nContext}
      >
        <TestScenarioEditorRootWrapper
          exposing={(testScenarioEditorRoot) => (this.self = testScenarioEditorRoot)}
          envelopeContext={this.envelopeContext}
          workspaceRootAbsolutePosixPath={
            this.initArgs.workspaceRootAbsolutePosixPath ?? DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH
          }
          isReadOnly={this.initArgs.isReadOnly}
          locale={this.initArgs.initialLocale}
        />
      </I18nDictionariesProvider>
    );
  }
}

// This component is a wrapper. It memoizes the TestScenarioEditorRoot props beforing rendering it.
function TestScenarioEditorRootWrapper({
  envelopeContext,
  exposing,
  workspaceRootAbsolutePosixPath,
  isReadOnly,
  locale,
}: {
  envelopeContext?: KogitoEditorEnvelopeContextType<KogitoEditorEnvelopeApi, KogitoEditorChannelApi>;
  exposing: (s: TestScenarioEditorRoot) => void;
  workspaceRootAbsolutePosixPath: string;
  isReadOnly: boolean;
  locale: string;
}) {
  const { i18n } = useScesimEditorEnvelopeI18n();
  const onNewEdit = useCallback(
    (workspaceEdit: WorkspaceEdit) => {
      envelopeContext?.channelApi.notifications.kogitoWorkspace_newEdit.send(workspaceEdit);
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

  const onRequestWorkspaceFileContent = useCallback(
    async (resource: ResourceContent) => {
      return envelopeContext?.channelApi.requests.kogitoWorkspace_resourceContentRequest(resource);
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
    <TestScenarioEditorRoot
      exposing={exposing}
      onNewEdit={onNewEdit}
      onRequestWorkspaceFilesList={onRequestWorkspaceFilesList}
      onRequestWorkspaceFileContent={onRequestWorkspaceFileContent}
      onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot={
        onOpenFileFromNormalizedPosixPathRelativeToTheWorkspaceRoot
      }
      workspaceRootAbsolutePosixPath={workspaceRootAbsolutePosixPath}
      keyboardShortcutsService={envelopeContext?.services.keyboardShortcuts}
      isReadOnly={isReadOnly}
      i18n={i18n}
      locale={locale}
    />
  );
}
