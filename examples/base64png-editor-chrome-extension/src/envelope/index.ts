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

import { init } from "@kie-tooling-core/editor/dist/envelope";
import { EnvelopeBusMessage } from "@kie-tooling-core/envelope-bus/dist/api";
import { Base64PngEditorFactory } from "@kogito-tooling-examples/base64png-editor";

/**
 * Initialize the Envelope with some args.
 *
 * @param args.container Where the envelope should be rendered. This id must be on the envelope html.
 * @param args.bus The communication interface, which determines what types of messages can be send or can be received from the Channel
 * @param args.editorFactory A new instance of the Editor that is going to be used by the envelope.
 * @param args.editorContext The context of where this envelope is going to run.
 */
init({
  container: document.getElementById("envelope-app")!,
  bus: {
    postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any) {
      window.parent.postMessage(message, targetOrigin!, _);
    },
  },
  editorFactory: new Base64PngEditorFactory(),
});
