/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { KogitoEditorChannelApi } from "@kogito-tooling/editor/dist/api";
import { PMMLEditor } from "../editor";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { EnvelopeBusMessageManager } from "@kogito-tooling/envelope-bus/dist/common";

const manager: EnvelopeBusMessageManager<
  KogitoEditorChannelApi,
  KogitoEditorChannelApi
> = new EnvelopeBusMessageManager((msg: any) => console.log(msg));

let editor: PMMLEditor;

ReactDOM.render(
  <div>
    <PMMLEditor exposing={(self: PMMLEditor) => (editor = self)} channelApi={manager.clientApi} />
    <div>
      <button onClick={setContent}>Set content</button>
    </div>
  </div>,
  document.getElementById("app")!
);

function setContent(): void {
  if (editor) {
    editor.setContent("", "content").finally(null);
  }
}
