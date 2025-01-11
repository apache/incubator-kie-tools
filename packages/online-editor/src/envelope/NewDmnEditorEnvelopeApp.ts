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
import { NewDmnEditorInterface } from "@kie-tools/dmn-editor-envelope/dist/NewDmnEditorFactory";
import { NewDmnEditorChannelApi } from "@kie-tools/dmn-editor-envelope/dist/NewDmnEditorChannelApi";
import { NewDmnEditorEnvelopeApi } from "@kie-tools/dmn-editor-envelope/dist/NewDmnEditorEnvelopeApi";
import { NewDmnEditorEnvelopeApiImpl } from "@kie-tools/dmn-editor-envelope/dist/NewDmnEditorEnvelopeApiFactory";

EditorEnvelope.initCustom<NewDmnEditorInterface, NewDmnEditorEnvelopeApi, NewDmnEditorChannelApi>({
  container: document.getElementById("envelope-app")!,
  bus: { postMessage: (message, targetOrigin, _) => window.parent.postMessage(message, targetOrigin!, _) },
  apiImplFactory: {
    create: (args) => new NewDmnEditorEnvelopeApiImpl(args),
  },
});
