/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { initCustom } from "@kie-tools-core/editor/dist/envelope";
import {
  ServerlessWorkflowTextEditorChannelApi,
  ServerlessWorkflowTextEditorEnvelopeApi,
} from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { ServerlessWorkflowTextEditorFactory } from "@kie-tools/serverless-workflow-text-editor/dist/editor";
import { ServerlessWorkflowTextEditorEnvelopeApiImpl } from "@kie-tools/serverless-workflow-text-editor/dist/envelope/ServerlessWorkflowTextEditorEnvelopeApiImpl";
import { ServerlessWorkflowTextEditorApi } from "@kie-tools/serverless-workflow-text-editor/src";

const MONACO_LANG_WORKERS = ["json", "yaml"];
const EDITOR_WORKER = "editor";

/**
 * Helper to instantiate the Monaco Workers. It's implemented in the TextEditor html template (see `swfTextEditorEnvelopeIndex.template`).
 * There should be 3 workers available, one for each language ('json'/'yaml') and another ('editor') to connect the language worker with the editor.
 */
interface MonacoEditorWorkersHelper {
  /**
   * Creates an instance of the worker identified by the 'workerId'.
   * @param workerId id of the worker. Values should be 'json', 'yaml' or 'editor'
   */
  getMonacoEditorWorker(workerId: "json" | "yaml" | "editor"): Worker;
}

/**
 * Overrides the way Monaco initializes their workers.
 * If `MonacoEnvironment` implements the optional `getWorker` method it will use it to is default mechanism
 */
interface MonacoEnvironment {
  getWorker(moduleId: string, label: string): Worker;
}

declare global {
  interface Window {
    MonacoEditorWorkersHelper: MonacoEditorWorkersHelper;
    MonacoEnvironment: MonacoEnvironment;
  }
}
const initEnvelope = () => {
  // Overriding the MonacoEnvironment. It should be done here to make sure Monaco scripts are already loaded.
  self.MonacoEnvironment.getWorker = (moduleId: string, label: string) => {
    /*
     Identifying the worker to start. The 'label' argument refers to the worker to be started, the value could be the editor
     lang ('json'/'yaml') or 'editorWorker'. Assuming that if the label doesn't match any available lang should start the worker.
     */
    const workerId: any = MONACO_LANG_WORKERS.includes(label) ? label : EDITOR_WORKER;
    return self.MonacoEditorWorkersHelper.getMonacoEditorWorker(workerId);
  };

  initCustom<
    ServerlessWorkflowTextEditorApi,
    ServerlessWorkflowTextEditorEnvelopeApi,
    ServerlessWorkflowTextEditorChannelApi
  >({
    container: document.getElementById("text-envelope-app")!,
    bus: { postMessage: (message, targetOrigin, _) => window.parent.postMessage(message, "*", _) },
    apiImplFactory: {
      create: (args) =>
        new ServerlessWorkflowTextEditorEnvelopeApiImpl(args, new ServerlessWorkflowTextEditorFactory()),
    },
  });
};

// Envelope should be initialized only after page was loaded.
if (document.readyState !== "loading") {
  initEnvelope();
} else {
  document.addEventListener("DOMContentLoaded", () => {
    initEnvelope();
  });
}
