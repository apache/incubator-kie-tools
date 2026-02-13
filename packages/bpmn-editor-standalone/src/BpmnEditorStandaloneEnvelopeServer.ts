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

import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import {
  ChannelType,
  DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
} from "@kie-tools-core/editor/dist/api";

export const createEnvelopeServer = (
  iframe: HTMLIFrameElement,
  readOnly?: boolean,
  origin?: string,
  locale?: string
) => {
  const defaultOrigin = window.location.protocol === "file:" ? "*" : window.location.origin;

  return new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
    { postMessage: (message) => iframe.contentWindow?.postMessage(message, "*") },
    origin ?? defaultOrigin,
    (self) => {
      return self.envelopeApi.requests.kogitoEditor_initRequest(
        {
          origin: self.origin,
          envelopeServerId: self.id,
        },
        {
          resourcesPathPrefix: "",
          fileExtension: "bpmn",
          initialLocale: locale ?? "en-US",
          isReadOnly: readOnly ?? false,
          channel: ChannelType.STANDALONE,
          workspaceRootAbsolutePosixPath: DEFAULT_WORKSPACE_ROOT_ABSOLUTE_POSIX_PATH,
        }
      );
    }
  );
};
