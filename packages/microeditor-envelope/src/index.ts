/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { EnvelopeBusApi } from "@kogito-tooling/microeditor-envelope-protocol";
import { ReactElement } from "react";
import * as ReactDOM from "react-dom";
import { EditorContext } from "./api/context";
import { DefaultKeyboardShortcutsService, KeyboardShortcutsApi } from "./api/keyboardShortcuts";
import { ResourceContentApi, ResourceContentEditorCoordinator } from "./api/resourceContent";
import { StateControl, StateControlApi } from "./api/stateControl";
import { EditorEnvelopeController } from "./EditorEnvelopeController";
import { EditorFactory } from "./EditorFactory";
import { Renderer } from "./Renderer";
import { SpecialDomElements } from "./SpecialDomElements";

export * from "./api/context/EditorContext";
export * from "./api/resourceContent";
export { EditorEnvelopeController } from "./EditorEnvelopeController";
export * from "./EditorFactory";
export * from "./EnvelopeBusInnerMessageHandler";
export { SpecialDomElements } from "./SpecialDomElements";

declare global {
  interface Window {
    envelope: {
      editorContext: EditorContext;
      resourceContentEditorService?: ResourceContentApi;
      stateControl: StateControlApi;
      keyboardShortcuts: KeyboardShortcutsApi;
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
 * Starts the envelope at a container. Uses busApi to send messages out of the envelope and creates editors based on the editorFactory provided.
 * @param args.container The DOM element where the envelope should be rendered.
 * @param args.busApi The implementation of EnvelopeBusApi to send messages out of the envelope.
 * @param args.editorFactory The factory of Editors using a LanguageData implementation.
 * @param args.editorContext The context for Editors with information about the running channel.
 */
export function init(args: {
  container: HTMLElement;
  busApi: EnvelopeBusApi;
  editorFactory: EditorFactory<any>;
  editorContext: EditorContext;
}) {
  const specialDomElements = new SpecialDomElements();
  const renderer = new ReactDomRenderer();
  const resourceContentEditorCoordinator = new ResourceContentEditorCoordinator();
  const stateControl = new StateControl();
  const keyboardShortcutsService = new DefaultKeyboardShortcutsService({ editorContext: args.editorContext });
  const editorEnvelopeController = new EditorEnvelopeController(
    args.busApi,
    args.editorFactory,
    specialDomElements,
    stateControl,
    renderer,
    resourceContentEditorCoordinator,
    keyboardShortcutsService
  );

  return editorEnvelopeController.start({ container: args.container, context: args.editorContext }).then(messageBus => {
    window.envelope = {
      resourceContentEditorService: resourceContentEditorCoordinator.exposeApi(messageBus),
      editorContext: args.editorContext,
      stateControl: stateControl.exposeApi(messageBus),
      keyboardShortcuts: keyboardShortcutsService.exposeApi()
    };
  });
}
