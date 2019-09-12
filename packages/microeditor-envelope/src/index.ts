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

import * as ReactDOM from "react-dom";
import { EditorEnvelopeController } from "./EditorEnvelopeController";
import { EnvelopeBusApi } from "@kogito-tooling/microeditor-envelope-protocol";
import { SpecialDomElements } from "./SpecialDomElements";
import { Renderer } from "./Renderer";
import { ReactElement } from "react";
import { EditorFactory } from "./EditorFactory";

export * from "./EditorFactory";
export * from "./EnvelopeBusInnerMessageHandler";

class ReactDomRenderer implements Renderer {
  public render(element: ReactElement, container: HTMLElement, callback: () => void) {
    ReactDOM.render(element, container, callback);
  }
}

/**
 * Starts the envelope at a container. Uses busApi to send messages out of the envelope and creates editors based on the editorFactory provided.
 * @param args.container The DOM element where the envelope should be rendered.
 * @param args.busApi The implementation of EnvelopeBusApi to send messages out of the envelope.
 * @param args.editorFactory The factory of Editors using a LanguageData implementation.
 */
export function init(args: { container: HTMLElement; busApi: EnvelopeBusApi; editorFactory: EditorFactory<any> }) {
  const specialDomElements = new SpecialDomElements();
  const renderer = new ReactDomRenderer();
  const editorEnvelopeController = new EditorEnvelopeController(
    args.busApi,
    args.editorFactory,
    specialDomElements,
    renderer
  );

  return editorEnvelopeController.start(args.container);
}
