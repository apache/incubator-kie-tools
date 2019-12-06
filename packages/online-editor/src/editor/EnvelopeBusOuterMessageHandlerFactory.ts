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

import {
  EnvelopeBusOuterMessageHandler,
  EnvelopeBusOuterMessageHandlerImpl
} from "@kogito-tooling/microeditor-envelope-protocol";
import { RefObject } from "react";

export class EnvelopeBusOuterMessageHandlerFactory {
  public createNew(
    iframeRef: RefObject<HTMLIFrameElement>,
    impl: (self: EnvelopeBusOuterMessageHandler) => EnvelopeBusOuterMessageHandlerImpl
  ) {
    return new EnvelopeBusOuterMessageHandler(
      {
        postMessage: msg => {
          if (iframeRef.current && iframeRef.current.contentWindow) {
            iframeRef.current.contentWindow.postMessage(msg, "*");
          }
        }
      },
      impl
    );
  }
}
