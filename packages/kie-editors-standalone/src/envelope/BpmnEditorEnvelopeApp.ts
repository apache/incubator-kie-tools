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
import { BpmnEditorChannelApi, BpmnEditorEnvelopeApi } from "@kie-tools/kie-bc-editors/dist/bpmn/api";
import { BpmnEditor, BpmnEditorEnvelopeApiImpl } from "@kie-tools/kie-bc-editors/dist/bpmn/envelope";

const initEnvelope = () => {
  const container = document.getElementById("envelope-app")!;

  const removeHrefIfNecessary = (link: HTMLAnchorElement) => {
    if (link.getAttribute("href") === "#") {
      link.setAttribute("href", "javascript:void(0);");
    }
  };

  // The MutationObserver below replaces every <a href="#" /> with <a href="javascript:void(0);" />,
  // because the former will cause the iframe to reload.
  const mutationObserver = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
      mutation.addedNodes.forEach((node) => {
        if (node instanceof HTMLAnchorElement) {
          removeHrefIfNecessary(node);
        } else if (node instanceof Element) {
          Array.from(node.getElementsByTagName("a")).forEach(removeHrefIfNecessary);
        }
      });
    });
  });
  mutationObserver.observe(document.body, { childList: true, subtree: true });

  EditorEnvelope.initCustom<BpmnEditor, BpmnEditorEnvelopeApi, BpmnEditorChannelApi>({
    container: container,
    bus: { postMessage: (message, targetOrigin, _) => window.parent.postMessage(message, targetOrigin!, _) },
    apiImplFactory: {
      create: (args) => new BpmnEditorEnvelopeApiImpl(args, { shouldLoadResourcesDynamically: false }),
    },
  });
};

// Envelope should be initialized only after page was loaded.
if (document.readyState !== "loading") {
  initEnvelope();
} else {
  document.addEventListener("DOMContentLoaded", () => {
    initEnvelope();
  });
}
