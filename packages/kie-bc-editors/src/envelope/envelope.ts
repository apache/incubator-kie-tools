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

import { ChannelType, getOperatingSystem } from "@kogito-tooling/channel-common-api";
import { GwtEditorWrapperFactory } from "..";
import * as MicroEditorEnvelope from "@kogito-tooling/editor/dist/envelope";
import { CompositeEditorFactory } from "@kogito-tooling/editor/dist/envelope";
import { EnvelopeBusMessage } from "@kogito-tooling/envelope-bus/dist/api";

MicroEditorEnvelope.init({
  container: document.getElementById("envelope-app")!,
  bus: {
    postMessage<D, Type>(message: EnvelopeBusMessage<D, Type>, targetOrigin?: string, _?: any) {
      window.parent.postMessage(message, "*", _);
    }
  },
  editorFactory: new CompositeEditorFactory([new GwtEditorWrapperFactory()]),
  editorContext: { channel: getChannelType(), operatingSystem: getOperatingSystem() }
});

export function getChannelType(): ChannelType {
  return frameElement.attributes.getNamedItem("data-envelope-channel")?.value as ChannelType;
}
