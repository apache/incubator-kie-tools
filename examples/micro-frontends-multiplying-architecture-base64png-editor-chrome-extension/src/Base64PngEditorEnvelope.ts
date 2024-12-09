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

import * as EditorEnvelope from "@kie-tools-core/editor/dist/envelope";
import { Base64PngEditorFactory } from "@kie-tools-examples//micro-frontends-multiplying-architecture-base64png-editor";

/**
 * Initialize the Envelope with some args.
 *
 * @param args.container Where the envelope should be rendered. This id must be on the envelope html.
 * @param args.bus The communication interface, which determines what types of messages can be send or can be received from the Channel
 * @param args.editorFactory A new instance of the Editor that is going to be used by the envelope.
 */
EditorEnvelope.init({
  container: document.getElementById("envelope-app")!,
  bus: { postMessage: (message, targetOrigin, _) => window.parent.postMessage(message, targetOrigin!, _) },
  editorFactory: new Base64PngEditorFactory(),
});
