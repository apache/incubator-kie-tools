/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { EditorContext } from "@kogito-tooling/core-api";
import { DefaultKeyboardShortcutsService, KeyboardShortcutsApi } from "@kogito-tooling/keyboard-shortcuts";
import { EnvelopeBus } from "@kogito-tooling/microeditor-envelope-protocol";
import { ReactElement } from "react";
import * as ReactDOM from "react-dom";
import { ResourceContentApi, ResourceContentServiceCoordinator } from "./api/resourceContent";
import { EditorEnvelopeController } from "./EditorEnvelopeController";
import { EditorFactory } from "@kogito-tooling/editor-api";
import { Renderer } from "./Renderer";
import { SpecialDomElements } from "./SpecialDomElements";
import { WorkspaceService, WorkspaceServiceApi } from "./api/workspaceService";
import { GuidedTourApi, GuidedTourServiceCoordinator } from "./api/tour";

declare global {
  interface Window {
    envelope: {
      guidedTourService: GuidedTourApi;
      editorContext: EditorContext;
      resourceContentEditorService?: ResourceContentApi;
      keyboardShortcuts: KeyboardShortcutsApi;
      workspaceService: WorkspaceServiceApi;
    };
  }
}

class ReactDomRenderer implements Renderer {
  public render(element: ReactElement, container: HTMLElement, callback: () => void) {
    setTimeout(() => {
      ReactDOM.render(element, container, callback);
    }, 0);
  }
}

/**
 * Starts the envelope at a container. Uses bus to send messages out of the envelope and creates editors based on the editorFactory provided.
 * @param args.container The DOM element where the envelope should be rendered.
 * @param args.bus The implementation of EnvelopeBus to send messages out of the envelope.
 * @param args.editorFactory The factory of Editors using a LanguageData implementation.
 * @param args.editorContext The context for Editors with information about the running channel.
 */
export function init(args: {
  container: HTMLElement;
  bus: EnvelopeBus;
  editorFactory: EditorFactory<any>;
  editorContext: EditorContext;
}) {
  const specialDomElements = new SpecialDomElements();
  const renderer = new ReactDomRenderer();
  const resourceContentEditorCoordinator = new ResourceContentServiceCoordinator();
  const guidedTourService = new GuidedTourServiceCoordinator();
  const keyboardShortcutsService = new DefaultKeyboardShortcutsService({ editorContext: args.editorContext });
  const workspaceService = new WorkspaceService();
  const editorEnvelopeController = new EditorEnvelopeController(
    args.bus,
    args.editorFactory,
    specialDomElements,
    renderer,
    resourceContentEditorCoordinator,
    keyboardShortcutsService
  );

  return editorEnvelopeController.start({ container: args.container, context: args.editorContext }).then(messageBus => {
    window.envelope = {
      guidedTourService: guidedTourService.exposeApi(messageBus),
      resourceContentEditorService: resourceContentEditorCoordinator.exposeApi(messageBus),
      editorContext: args.editorContext,
      keyboardShortcuts: keyboardShortcutsService.exposeApi(),
      workspaceService: workspaceService.exposeApi(messageBus)
    };
  });
}
