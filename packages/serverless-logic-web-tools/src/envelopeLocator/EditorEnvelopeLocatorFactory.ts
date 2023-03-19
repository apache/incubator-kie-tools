/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import {
  EditorEnvelopeLocator,
  EnvelopeContentType,
  EnvelopeMapping,
} from "@kie-tools-core/editor/dist/api/EditorEnvelopeLocator";
import { GLOB_PATTERN } from "../extension";

export class EditorEnvelopeLocatorFactory {
  public create(args: { targetOrigin: string }) {
    return new EditorEnvelopeLocator(args.targetOrigin, [
      new EnvelopeMapping({
        type: "swf",
        filePathGlob: GLOB_PATTERN.sw,
        resourcesPathPrefix: ".",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "serverless-workflow-combined-editor-envelope.html" },
      }),
      new EnvelopeMapping({
        type: "dash",
        filePathGlob: GLOB_PATTERN.dash,
        resourcesPathPrefix: "",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dashbuilder-editor-envelope.html" },
      }),
      new EnvelopeMapping({
        type: "text",
        filePathGlob: GLOB_PATTERN.all,
        resourcesPathPrefix: "",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "text-editor-envelope.html" },
      }),
    ]);
  }
}
